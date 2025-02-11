package zk_os.sec;

import mpc.exception.FIllegalStateException;
import mpu.X;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import mpu.str.SPLIT;
import zk_form.notify.ZKI_Sec;
import zk_os.AppZos;
import zk_os.db.net.WebUsr;
import zk_page.core.SpVM;
import zk_notes.node_state.FormState;
import zk_notes.AppNotesCore;
import zk_notes.node.NodeDir;

import java.util.List;

public class SecMan {
	public static final String SECFORALL = "@";
	public static final String SECFORUSER = "#";
	public static final String ANONIM = "anonim";

//	public static Pare<Boolean, Boolean, Boolean> getSecState() {
//		if (!Sec.isEditorAdminOwner()) {
//			return false;
//		} else if (Sec.isAdminOrOwner()) {
//			return true;
//		}
//		return Sec.isPlaneOwner();
//	}

	public static boolean isOwnSd3() {
		if (!Sec.isEditorAdminOwner()) {
			return false;
		} else if (Sec.isAdminOrOwner()) {
			return true;
		}
		return Sec.isPlaneOwner();
	}

	public static boolean isAllowedEdit() {
		return Sec.isEditorAdminOwner();
	}


	public static boolean isAllowedEdit(NodeDir nodeDir) {
		boolean allowedAccessViewEdit = nodeDir.state().isAllowedAccess_View_Edit(true);
		if (allowedAccessViewEdit) {
			return true;
		}
		throw throwAccessDeniedByAppMode(nodeDir);
	}

	public static boolean isAllowedView(NodeDir nodeDir, boolean... THROW) {
		boolean allowedAccessViewEdit = nodeDir.state().isAllowedAccess_View_Edit();
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
			throw new NodeAccessDenied("access denied to node '%s'", nodeDir.nodeId());
		}
		throw new FIllegalStateException("Node '%s' not found", nodeDir.nodeId());
	}

	public static boolean isNotAnonim() {
		return Sec.isNotAnonim();
	}

	public static boolean isAnonim() {
		return Sec.isAnonim();
	}

	public static boolean isAdminOrOwner() {
		return Sec.isAdminOrOwner();
	}

	public static boolean isOwner() {
		return Sec.isOwner();
	}

	public static String login(String... defRq) {
		WebUsr webUsr = WebUsr.get(null);
		return webUsr != null ? webUsr.getLogin() : ARG.toDefThrow(() -> new RequiredRuntimeException("Except known user"), defRq);
	}

}
