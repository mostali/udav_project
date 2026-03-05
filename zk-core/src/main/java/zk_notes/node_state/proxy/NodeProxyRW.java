package zk_notes.node_state.proxy;

import mpc.exception.FIllegalStateException;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpc.fs.path.IPath;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import zk_notes.node.NodeDir;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NodeProxyRW extends StateProxyRW {

	private final NodeDir nodeDir;

	private boolean withInjected;

	public NodeProxyRW(NodeDir nodeDir) {
		super(nodeDir.getPath_FormFc_Data().toString(), nodeDir.state());
		this.nodeDir = nodeDir;
	}

	public static zk_notes.node_state.proxy.NodeProxyRW of(NodeDir nodeDir, boolean... injected) {
		return new zk_notes.node_state.proxy.NodeProxyRW(nodeDir).withInject(injected);
	}

	private zk_notes.node_state.proxy.NodeProxyRW withInject(boolean... injected) {
		this.withInjected = ARG.isDefNotEqFalse(injected);
		return this;
	}

	public String readContent(String... defRq) {
		String targetPathFromFile = FK_FROM_FILE(nodeDir.state(), null);
		if (targetPathFromFile != null) {
			if (GEXT.BINARY.has(targetPathFromFile)) {
				return "-BINARY-CONTENT-";
			}
			return RW.readString(Paths.get(targetPathFromFile), "");
		} else if (withInjected) {
			return nodeDir.injectStr();
		} else {
			return RW.readString(getOriginalPath(), "");
		}
	}

	@Override
	public Path getTargetPath_FILE(Path... defRq) {
		if (withInjected) {
			throw new FIllegalStateException("Node '%s' was injected. Write is illegal operation.", nodeDir.nodeId());
		}
		return super.getTargetPath_FILE(defRq);
	}

	public Path getTargetPathFileRead() {
		return super.getTargetPath_FILE();
	}

	public IPath getTargetPathFile(IPath... defRq) {
		Path targetPathDir = ARG.isDef(defRq) ? super.getTargetPath_FILE(null) : super.getTargetPath_FILE();
		return targetPathDir != null ? IPath.of(targetPathDir, EFT.FILE) : ARG.throwMsg0(() -> X.f("Except dir [%s] from FILE", nodeDir.nodeId()), defRq);
	}

	public IPath getTargetPathDir(IPath... defRq) {
		Path targetPathDir = ARG.isDef(defRq) ? super.getTargetPath_DIR(null) : super.getTargetPath_DIR();
		return targetPathDir != null ? IPath.of(targetPathDir, EFT.DIR) : ARG.throwMsg0(() -> X.f("Except dir [%s] from DIR", nodeDir.nodeId()), defRq);
	}


}
