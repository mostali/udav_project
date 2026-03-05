package zk_notes.node_state.proxy;

import com.github.benmanes.caffeine.cache.Cache;
import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.fs.UFS;
import mpc.map.MAP;
import mpe.UCaffeine;
import mpu.X;
import mpu.core.ARG;
import mpv.byteunit.ByteUnit;
import zk_com.base.Tbx;
import zk_notes.node.NodeDir;
import zk_notes.node_state.EntityState;
import zk_os.db.net.WebUsr;
import zk_page.ZKR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class StateProxyRW extends ProxyRW {

	private final EntityState objState;

	public StateProxyRW(String originalSrcPath, EntityState objState) {
		super(originalSrcPath);
		this.objState = objState;
	}

	public static zk_notes.node_state.proxy.StateProxyRW of(Tbx tbx) {
		return of(tbx.getPathStr(), tbx.getFormState());
	}

	@Override
	public boolean hasProxyPath_FILE(boolean... withData1_existed0_propsEmp) {
		String targetPathFromFile = FK_FROM_FILE(objState, null);
		Boolean withDataOrexisted = ARG.toDefBooleanOrNull(withData1_existed0_propsEmp);
		return withDataOrexisted == null ? targetPathFromFile != null : UFS.existFile(targetPathFromFile, withDataOrexisted);
	}

	@Override
	public boolean hasProxyPath_DIR(boolean... withData1_existed0_propsEmp) {
		String targetPathFromFile = FK_FROM_DIR(objState, null);
		Boolean withDataOrexisted = ARG.toDefBooleanOrNull(withData1_existed0_propsEmp);
		return withDataOrexisted == null ? targetPathFromFile != null : UFS.existDir(targetPathFromFile, withDataOrexisted);
	}

	public static StateProxyRW of(String orgPath, EntityState objState) {
		return new StateProxyRW(orgPath, objState);
	}


	public Path getTargetPath_FILE(Path... defRq) {
		Path path = Paths.get(FK_FROM_FILE(objState, getOriginalSrcPathStr()));
		return ARG.isNotDef(defRq) ? path : UFS.existFile(path) ? path : ARG.throwMsg(() -> X.f("Except dir [%s] from FILE", path), defRq);
	}

	public Path getTargetPath_DIR(Path... defRq) {
		Path path = Paths.get(FK_FROM_DIR(objState, getOriginalSrcPathStr()));
		return ARG.isNotDef(defRq) ? path : UFS.existDir(path) ? path : ARG.throwMsg(() -> X.f("Except dir [%s] from DIR", path), defRq);
	}


	@Override
	public void writeContent(String content) {
		String objId = objState.toObjId();
		super.writeContent(content);
		afterWriteListener(objId, content);
		checkAndAddVersion(content, objId);
	}

	private void checkAndAddVersion(String content, String objId) {
		Boolean isVesioned = objState.getAs(EntityState.FK_VERSIONED, Boolean.class, false);
		boolean bigKb = ByteUnit.KB.toMB(content.length()) > 10_000;
		Boolean isVesionedDefaultTooBig = !isVesioned && bigKb;
		if (isVesioned || !isVesionedDefaultTooBig) {
			ICtxDb.CtxModel newVersion = AppCore.__VERSIONED().put(objId, content, ZKR.getClientIpAddress() + ":" + WebUsr.login());
			if (L.isInfoEnabled()) {
				L.info("Node versioned [{}] [{}] with content*kb*{}:\n{}", newVersion.getKey() + "#" + newVersion.getId(), newVersion.toString(), bigKb, content);
			}
		}
	}

	public interface UpdListener {

		void up(NodeDir nodeDir);

		default void rmm() {
			Map.Entry<String, UpdListener> byValueFirst = MAP.getByValueFirst(CACHE_STORE_LISNNER.asMap(), new UpdListener[]{this}, null);
			if (byValueFirst != null) {
				CACHE_STORE_LISNNER.invalidate(byValueFirst.getKey());
				L.info("UpdListener removed");
			}
		}

		;

	}

//	private LinkedMultiValueMap<String, UpdListener> lisn = new LinkedMultiValueMap<>();
//	static CacheLoader<String, UpdListener> cacheLoader = name -> {
//		return null;
//	};

	//	final LoadingCache<String, UpdListener> CACHE_STORE_LISNNER = UCaffeine.buildCache(cacheLoader, true, 300);
	public static final Cache<String, UpdListener> CACHE_STORE_LISNNER = UCaffeine.buildCacheSimple(true, 300);

	private void afterWriteListener(String objId, String newContent) {
		UpdListener ifPresent = CACHE_STORE_LISNNER.getIfPresent(objId);
		if (ifPresent == null) {
			return;
		}
		NodeDir nodeDir1 = NodeDir.of(objId, null);
		if (nodeDir1 == null) {
			L.warn("UpdListener found, but node '{}' not exist", objId);
			return;
		}
		ifPresent.up(nodeDir1);
	}
}

