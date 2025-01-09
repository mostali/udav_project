package zk_os.sec;

import mpu.X;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import mpu.str.SPLIT;
import zk_os.db.net.WebUsr;
import zk_page.core.SpVM;
import zk_page.node_state.FormState;
import zk_notes.AppNotesCore;
import zk_page.node.NodeDir;

import java.util.List;

public class SecMan {
	public static final String SECFORALL = "@";
	public static final String SECFORUSER = "#";
	public static final String ANONIM = "anonim";

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
		return nodeDir.state().isAllowedAccess_View_Edit(true);
	}

	public static boolean isAllowedView(NodeDir nodeDir) {
		return nodeDir.state().isAllowedAccess_View_Edit();
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

	public static boolean hasMarkAllowedForAllAndCheckProfileList(FormState formState, String sec_prop, boolean... RETURN) {
		String sec_prop_val = formState.get(sec_prop, "");
		if ("@".equals(sec_prop_val)) {
			return true;
		}
		String sd3 = SpVM.get().subdomain3();
		if (sec_prop_val.startsWith("@@")) {
			List<String> values = AppNotesCore.loadTopCtxValues(sec_prop_val.substring(1));
			boolean containsSd3 = values.contains(sd3);
			if (containsSd3) {
				return true;
			}
			return ARG.isDefEqTrue(RETURN) ? false : X.throwException(new RequiredRuntimeException("Check secprop '%s' not found*", sec_prop));
		}
		List<String> users = SPLIT.allByComma(sec_prop_val);
		if (users.contains(sd3)) {
			return true;
		}
		return ARG.isDefEqTrue(RETURN) ? false : X.throwException(new RequiredRuntimeException("Check secprop '%s' not found", sec_prop));
	}

	public static String login(String... defRq) {
		WebUsr webUsr = WebUsr.get(null);
		return webUsr != null ? webUsr.getLogin() : ARG.toDefThrow(() -> new RequiredRuntimeException("Except known user"), defRq);
	}

}
