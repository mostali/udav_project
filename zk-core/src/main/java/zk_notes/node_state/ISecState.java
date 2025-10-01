package zk_notes.node_state;

import mpu.X;
import mpu.core.ARG;
import mpu.str.SPLIT;
import utl_rest.StatusException;
import zk_os.db.net.WebUsr;
import zk_os.sec.Sec;
import zk_os.sec.SecEnt;
import zk_os.sec.SecMan;

import java.util.List;

public interface ISecState {

	String SECE = "sece";//edit
	String SECV = "secv";//view

	//
	//
	// CHECK SEC


	static void checkIsAllowedViewOr403(SecFileState formState) {
		if (!formState.isAllowedAccess_VIEW_EDIT(true)) {
			StatusException.C403_or_404();
		}
	}

	static void checkIsAllowedEditOr403(SecFileState formState) {
		if (!formState.isAllowedAccess_EDIT(true)) {
			StatusException.C403_or_404();
		}
	}

	//
	// PROP SEC LIST

	default List<String> getSecEditList() {
		return SPLIT.allByComma(entityState().get(SECE, ""));
	}

	default List<String> getSecViewList() {
		return SPLIT.allByComma(entityState().get(SECV, ""));
	}

	default EntityState entityState() {
		return (EntityState) this;
	}


	//
	// CHECK's

	default boolean isAllowedAccess_EDIT() {
		return Sec.isAdminOrOwner() || //
				checkIsAllowInList(getSecEditList(), true) || //
				isAllow_byProp_USER(true, true) || //
				SecEnt.isPlaneOwner();
	}

	default boolean isAllowedAccess_EDIT(boolean checkAdminAndOwner) {
		if (checkAdminAndOwner && Sec.isAdminOrOwner()) {
			return true;
		}
		return checkIsAllowInList(getSecEditList(), true) || //
				isAllow_byProp_USER(true, true) || //
				SecEnt.isPlaneOwner();
	}

	default boolean isAllowedAccess_View(boolean checkAdminAndOwner) {
		if (checkAdminAndOwner && Sec.isAdminOrOwner()) {
			return true;
		}
		return checkIsAllowInList(getSecViewList(), true) || //
				isAllow_byProp_USER(true, true) || //
				SecEnt.isPlaneOwner();
	}

	default boolean checkIsAllowInList(List<String> secAsList, boolean... checkForAllPattern) {
		if (ARG.isDefEqTrue(checkForAllPattern) && secAsList.contains(SecMan.SECFORALL)) {
			return true;
		}
		return secAsList.contains(Sec.alias());
	}

	default boolean isAllowedAccess_VIEW_EDIT(boolean... checkSecEdit) {
		return Sec.isAdminOrOwner() || //
				checkIsAllowInList(getSecViewList(), true) || //
				(ARG.isDefEqTrue(checkSecEdit) ? checkIsAllowInList(getSecEditList()) : false) || //
				isAllow_byProp_USER(true, true);
	}

	//
	//

	default boolean isAllow_byProp_SECE(boolean isAnonim_That, boolean ifEmptyProp_That, boolean isPropEqUserByLoginAlias_That) {
		String secE_value = entityState().get(ISecState.SECE, null);
		if (X.empty(secE_value)) {
			return ifEmptyProp_That;
		}
		WebUsr user = Sec.getUser(null);
		if (user == null || Sec.isAnonim()) {
			return isAnonim_That;
		}
		if (!user.isAllowedForWorkOnSite()) {
			return false;
		}
		return !isPropEqUserByLoginAlias_That ? false : user.equalsByLoginOrAlias(secE_value);
	}

	default boolean isAllow_byProp_USER(boolean ifEmptyProp_That, boolean isPropEqUserByLoginAlias_That) {
		String prop_USER = entityState().get(EntityState.PK_USER, null);
		if (X.empty(prop_USER)) {
			return ifEmptyProp_That;
		}
		WebUsr user = Sec.getUser(null);
		if (user == null || Sec.isAnonim()) {
			boolean isAnonim_That = false;
			return isAnonim_That;
		}
		if (!user.isAllowedForWorkOnSite()) {
			return false;
		}
		return !isPropEqUserByLoginAlias_That ? false : user.equalsByLoginOrAlias(prop_USER);
	}


//	static boolean isAllow_byAny(List<String> secAsList) {
//		return secAsList.contains(SecMan.SECFORALL);
//	}


//	//
//	//
//	//
//	//
//	// NEW WAY
//
//	private boolean isAllowedAccess_View(boolean... checkEditor) {
//		boolean additionalyCheckEditor = ARG.isDefEqTrue(checkEditor);
//		boolean allowedAccessByUser = isAllowedAccess(true, false, true, true, true, true);
//		if (allowedAccessByUser) {
//			return true;
//		}
//		boolean checkView = allowedAccessByUser ? true : checkPropSec(false);
//		return checkView;
//	}
//
//	private boolean isAllowedAccess_View() {
//		boolean allowedAccessByUser = isAllowedAccess(true, false, true, true, true, true);
//		return allowedAccessByUser ? true : checkPropSec(true);
//	}
//
//	private boolean isAllowedAccess_Edit() {
//		boolean allowedAccessByUser = isAllowedAccess(true, false, true, true, false, true);
//		return allowedAccessByUser ? true : checkPropSec(true);
//	}
//
//	private boolean checkPropSec(boolean isEditProp) {
//		return (isEditProp ? getSecEditList() : getSecViewList()).contains(Sec.alias());
//	}
//
//	//
//	// COMMON
//
//	private boolean isAllowedAccess(boolean checkAdminAndOwner, //
//									boolean isAnonim_That, boolean ifEmptyPropUser_That, boolean isPropUserEqUserAlias_That,//
//									boolean checkView_for_ANY, //
//									boolean checkEdit_for_ANY//
//	) {
//		if (checkAdminAndOwner && Sec.isAdminOrOwner()) {
//			return true;
//		} else if (isAllow_byProp_USER(isAnonim_That, ifEmptyPropUser_That, isPropUserEqUserAlias_That)) {
//			return true;
//		} else if (checkView_for_ANY && isAllow_byAny(getSecViewList())) {
//			return true;
//		} else if (checkEdit_for_ANY && isAllow_byAny(getSecEditList())) {
//			return true;
//		}
//		return false;
//	}

}
