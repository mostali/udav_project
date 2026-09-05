package mp.utl_odb.tree.trees.lifecache;

import mpe.db.JdbcUrl;
import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.tree.ctxdb.CKey;
import mpc.fs.Ns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.function.Function;

public class UTreeTtl extends UTreeLifeCacheAbstract {

	public static final Logger L = LoggerFactory.getLogger(UTreeTtl.class);

	@Override
	public CacheType cacheType() {
		return CacheType.SHORT_LIFE;
	}

	public static UTreeTtl tree(String appTreeName) {
		return AppCore.of().treeShortLife(appTreeName);
	}

	public static UTreeTtl tree(Ns ns, String dbName) {
		return tree(ns.path(JdbcUrl.buildDbFileName(dbName)));
	}

	public static UTreeTtl tree(Path path) {
		return (UTreeTtl) new UTreeTtl(path).withUpdateMode(UpdateMode.ALWAYSLAST);
	}

	public UTreeTtl(Path path) {
		super(path);
	}

	public CtxModelCtr getModel_WithMaxTtl(CKey key, long shortLifeMs) throws ModelLifeMsException {
		return getModel_WithMaxTTL(key, shortLifeMs);
	}

	public CtxModelCtr getModel_WithMaxTtl(String key, long shortLifeMs) throws ModelLifeMsException {
		return getModel_WithMaxTTL(CKey.of(key), shortLifeMs);
	}

//	public CtxModelCtr getModel_WithMaxTtl(String key, long shortLifeMs, Function<String, CtxModelCtr> loader) throws ModelLifeMsException {
//		return getModel_WithMaxTTL(CKey.of(key), shortLifeMs);
//	}


}
