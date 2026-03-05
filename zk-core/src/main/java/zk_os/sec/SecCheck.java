package zk_os.sec;

import utl_rest.StatusException;
import zk_os.AppZos;

public class SecCheck {
	public static void checkIsOwnerOr404() {
		if (SecMan.isOwner()) {
			return;
		}
		if (AppZos.isDebugEnable()) {
			throw StatusException.C404("Access denied. Except owner.");
		} else {
			throw StatusException.C404();
		}
	}

	public static void checkIsAdminOrOwnerOr404() {
		if (SecMan.isOwnerOrAdmin()) {
			return;
		}
		if (AppZos.isDebugEnable()) {
			throw StatusException.C404("Access denied. Except admin or owner.");
		} else {
			throw StatusException.C404();
		}
	}

	public static void checkIsEditor_Admin_Owner_Or404() {
		if (!SecMan.isAllowedEditPlane()) {
			return;
		}
//		if (!SecManRMM.isEditorRole() && !SecManRMM.isOwnerOrAdmin()) {
//		if (!SecManRMM.isEditorRole() && !SecManRMM.isOwnerOrAdmin()) {
		if (AppZos.isDebugEnable()) {
			throw StatusException.C404("Access denied. Except editor.");
		} else {
			throw StatusException.C404();
		}
//		}
	}

	public static void checkIsNotAnonimOr404() {
		if (SecMan.isAnonimUnsafe()) {
			if (AppZos.isDebugEnable()) {
				throw StatusException.C404("access denied for anonim");
			} else {
				throw StatusException.C404();
			}
		}
	}
}
