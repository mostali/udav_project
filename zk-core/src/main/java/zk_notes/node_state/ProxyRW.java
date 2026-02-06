package zk_notes.node_state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.fs.UFS;
import mpu.core.ARG;
import mpu.core.RW;
import zk_com.base.Tbx;
import zk_notes.node.NodeDir;
import zk_notes.node_state.impl.FormState;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public abstract class ProxyRW {

	private final @Getter String originalSrcPathStr;

	public static ProxyRW of(Tbx tbx) {
		return StateProxyRW.of(tbx.getPathStr(), tbx.getFormState());
	}

	public static String getTargetPathFromFile(EntityState state, String... defRq) {
		return state.get(FormState.FK_FROM_FILE, defRq);
	}

//	public abstract String readContent(String... defRq);

//	public abstract void writeContent(String content);

	public abstract boolean hasProxyPath(boolean... existedFile);

	public String readContentOrEmpty() {
		return readContent("");
	}

	public String readContent(String... defRq) {
		return RW.readString(getTargetAnyPath(), defRq);
	}

	@SneakyThrows
	public void writeContent(String content) {
		RW.write_(getTargetAnyPath(), content, true);
	}

	public abstract Path getTargetAnyPath();

	public Path getOriginalPath() {
		return Paths.get(getOriginalSrcPathStr());
	}

	//
	//
	//

	public static class StateProxyRW extends ProxyRW {

		private final EntityState objState;

		public StateProxyRW(String originalSrcPath, EntityState objState) {
			super(originalSrcPath);
			this.objState = objState;
		}

		public static StateProxyRW of(Tbx tbx) {
			return of(tbx.getPathStr(), tbx.getFormState());
		}

		@Override
		public boolean hasProxyPath(boolean... withData1_existed0_propsEmp) {
			String targetPathFromFile = getTargetPathFromFile(objState, null);
			Boolean withDataOrexisted = ARG.toDefBooleanOrNull(withData1_existed0_propsEmp);
			return withDataOrexisted == null ? targetPathFromFile != null : UFS.existFile(targetPathFromFile, withDataOrexisted);
		}

		public static StateProxyRW of(String orgPath, EntityState objState) {
			return new StateProxyRW(orgPath, objState);
		}

		public Path getTargetAnyPath() {
			String targetPathFromFile = getTargetPathFromFile(objState, getOriginalSrcPathStr());
			return Paths.get(targetPathFromFile);
		}

	}

	public static class NodeProxyRW extends StateProxyRW {

		private final NodeDir nodeDir;

		private boolean withInjected;

		public NodeProxyRW(NodeDir nodeDir) {
			super(nodeDir.getPath_FormFc_Data().toString(), nodeDir.state());
			this.nodeDir = nodeDir;
		}

		public static NodeProxyRW of(NodeDir nodeDir, boolean... injected) {
			return new NodeProxyRW(nodeDir).withInject(injected);
		}

		private NodeProxyRW withInject(boolean... injected) {
			this.withInjected = ARG.isDefNotEqFalse(injected);
			return this;
		}

		public String readContent(String... defRq) {
			String targetPathFromFile = getTargetPathFromFile(nodeDir.state(), null);
			if (targetPathFromFile != null) {
				return RW.readString(Paths.get(targetPathFromFile), "");
			} else if (withInjected) {
				return nodeDir.injectStr();
			} else {
				return RW.readString(getOriginalPath(), "");
			}
		}

		@Override
		public Path getTargetAnyPath() {
			if (withInjected) {
				throw new FIllegalStateException("Node '%s' was injected. Write is illegal operation.", nodeDir.nodeId());
			}
			return super.getTargetAnyPath();
		}

		public Path getTargetAnyPath_READ() {
			return super.getTargetAnyPath();
		}

	}
}
