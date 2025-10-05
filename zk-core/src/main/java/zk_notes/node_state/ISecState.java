package zk_notes.node_state;

import mpu.core.ARG;
import utl_rest.StatusException;
import zk_notes.node_state.sec.SecPropChecker;
import zk_os.core.Sdn;
import zk_os.sec.SecApp;

public interface ISecState {

	//
	//
	// CHECK SEC


	static void checkIsAllowedViewOr403(SecFileState formState) {
		if (!formState.isAllowedAccess_VIEW_EDIT(true)) {
			StatusException.C403_or_404();
		}
	}

	static void checkIsAllowedEditOr403(SecFileState formState) {
		if (!formState.isAllowedAccess_EDIT()) {
			StatusException.C403_or_404();
		}
	}

	//
	// PROP SEC LIST

	default EntityState entityState() {
		return (EntityState) this;
	}

	//
	// CHECK's

	default boolean isAllowedAccess_RUN() {
		return SecPropChecker.ofSecR().isAllowedByProp(entityState(), Sdn.PLANE());
	}

	default boolean isAllowedAccess_EDIT() {
//		return isAllowedAccess_EDIT(true);
//	}
//	default boolean isAllowedAccess_EDIT(boolean checkAdminAndOwner) {
		return SecApp.SecProp.SECE.isAllowedByProp(entityState(), Sdn.PLANE());
//		return SecPropChecker.ofSecE().isAllowedByProp(entityState(), Sdn.PLANE());
//		if (checkAdminAndOwner && Sec.isAdminOrOwner()) {
//			return true;
//		}
//		return SecApp.checkIsAllowInList(SecApp.getSecEList(entityState()), true) || //
//				isAllow_byProp_USER(true, true) || //
//				SecEnt.isPlaneOwner();
	}

	default boolean isAllowedAccess_VIEW() {
//		return isAllowedAccess_View(true);
//	}
//	default boolean isAllowedAccess_View(boolean checkAdminAndOwner) {
		return SecApp.SecProp.SECV.isAllowedByProp(entityState(), Sdn.PLANE());
//		if (checkAdminAndOwner && Sec.isAdminOrOwner()) {
//			return true;
//		}
//		return SecApp.checkIsAllowInList(SecApp.getSecVList(entityState()), true) || //
//				isAllow_byProp_USER(true, true) || //
//				SecEnt.isPlaneOwner();
	}

	//
	//

	default boolean isAllowedAccess_VIEW_EDIT(boolean... checkSecEdit) {
		return isAllowedAccess_VIEW() || (ARG.isDefEqTrue(checkSecEdit) && isAllowedAccess_EDIT());

//		return Sec.isAdminOrOwner() || //
//				SecApp.checkIsAllowInList(SecApp.getSecVList(entityState()), true) || //
//				(ARG.isDefEqTrue(checkSecEdit) ? SecApp.checkIsAllowInList(SecApp.getSecEList(entityState()), true) : false) || //
//				isAllow_byProp_USER(true, true);
	}

	//
	//

//	default boolean isAllow_byProp_SECR() {
//		return SecPropChecker.ofSecR().isAllowedByProp(entityState());
//	}

//	default boolean isAllow_byProp_SECE(boolean isAnonim_That, boolean ifEmptyProp_That, boolean isPropEqUserByLoginAlias_That) {
//		return SecPropChecker.ofSecE().isAllowedByProp(entityState());
//	}
//	default boolean isAllow_byProp_SECE(boolean isAnonim_That, boolean ifEmptyProp_That, boolean isPropEqUserByLoginAlias_That) {
//		String secE_value = entityState().get(SecApp.SECE, null);
//		if (X.empty(secE_value)) {
//			return ifEmptyProp_That;
//		}
//		WebUsr user = Sec.getUser(null);
//		if (user == null || Sec.isAnonim(user)) {
//			return isAnonim_That;
//		}
//		if (!user.isAllowedForWorkOnSite()) {
//			return false;
//		}
//		return !isPropEqUserByLoginAlias_That ? false : user.equalsByLoginOrAlias(secE_value);
//	}

//	default boolean isAllow_byProp_USER(boolean ifEmptyProp_That, boolean isPropEqUserByLoginAlias_That) {
//		return SecPropChecker.ofSecUSER().isAllowedByProp(entityState());
//	}
//	default boolean isAllow_byProp_USER(boolean ifEmptyProp_That, boolean isPropEqUserByLoginAlias_That) {
//		String prop_USER = entityState().get(SecApp.USER, null);
//		if (X.empty(prop_USER)) {
//			return ifEmptyProp_That;
//		}
//		WebUsr user = Sec.getUser(null);
//		if (user == null || Sec.isAnonim(user)) {
//			boolean isAnonim_That = false;
//			return isAnonim_That;
//		}
//		if (!user.isAllowedForWorkOnSite()) {
//			return false;
//		}
//		return !isPropEqUserByLoginAlias_That ? false : user.equalsByLoginOrAlias(prop_USER);
//	}


}
