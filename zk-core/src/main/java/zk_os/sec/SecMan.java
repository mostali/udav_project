package zk_os.sec;

import mpc.exception.FIllegalStateException;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import zk_form.notify.ZKI_Sec;
import zk_os.AppZos;
import zk_os.db.net.WebUsr;
import zk_notes.node.NodeDir;

public class SecMan {

	public static boolean isPlaneOwnerOrAdmin(String plane) {
		return Sec.isAdminOrOwnerRole() || SecEnt.isPlaneOwner(plane);
	}

	@Deprecated
	public static boolean isAllowedEdit() {
		return Sec.isEditorAdminOwnerRole();
	}


	public static boolean isAllowedRun(NodeDir nodeDir, boolean... THROW) {
		boolean allowedAccessRun = nodeDir.state().isAllowedAccess_RUN();
		if (allowedAccessRun) {
			return true;
		}
		if (ARG.isDefEqTrue(THROW)) {
			throw throwAccessDeniedByAppMode(nodeDir);
		}
		return false;
	}

	public static boolean isAllowedEdit(NodeDir nodeDir, boolean... THROW) {
		boolean allowedAccessViewEdit = nodeDir.state().isAllowedAccess_EDIT();
		if (allowedAccessViewEdit) {
			return true;
		}
		if (ARG.isDefEqTrue(THROW)) {
			throw throwAccessDeniedByAppMode(nodeDir);
		}
		return false;
	}

	public static boolean isAllowedView(NodeDir nodeDir, boolean... THROW) {
		boolean allowedAccessViewEdit = nodeDir.state().isAllowedAccess_VIEW_EDIT();
		if (allowedAccessViewEdit) {
			return true;
		} else if (ARG.isDefNotEqTrue(THROW)) {
			ZKI_Sec.infoBottomRightFast("Not allowed view for user '%s'", WebUsr.get());
			return false;
		}
		throw throwAccessDeniedByAppMode(nodeDir);
	}

	public static class NodeAccessDenied extends FIllegalStateException {
		public NodeAccessDenied(String message, Object... args) {
			super(message, args);
		}

		public NodeAccessDenied(Throwable throwable, String message, Object... args) {
			super(throwable, message, args);
		}
	}

	public static FIllegalStateException throwAccessDeniedByAppMode(NodeDir nodeDir) {
		if (AppZos.isDebugEnable()) {
			throw new NodeAccessDenied("access denied to node '%s'", nodeDir.nodeID());
		}
		throw new FIllegalStateException("Node '%s' not found", nodeDir.nodeID());
	}

	public static boolean isNotAnonim() {
		return Sec.isNotAnonimUnsafe();
	}

	public static boolean isAnonim() {
		return Sec.isAnonimUnsafe();
	}

	public static boolean isAdminOrOwner() {
		return Sec.isAdminOrOwnerRole();
	}

	public static boolean isOwner() {
		return Sec.isOwnerRole();
	}

	public static String login(String... defRq) {
		WebUsr webUsr = WebUsr.get(null);
		return webUsr != null ? webUsr.getLogin() : ARG.toDefThrow(() -> new RequiredRuntimeException("Except known user"), defRq);
	}

}
