package zk_notes.node_state;

import mpu.core.ARG;
import utl_rest.StatusException;
import zk_os.core.Sdn;
import zk_os.sec.SecApp;
import zk_os.sec.UO;

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

	default EntityState entityState() {
		return (EntityState) this;
	}

	//
	// CHECK's

//	default boolean isAllowedAccess_RUN() {
//		return SecPropChecker.ofSecR().isAllowedByProp(entityState(), Sdn.PLANERQ());
//	}

	@Deprecated
	default boolean isAllowedAccess_EDIT() {
		return UO.EDIT.isAllowed(entityState());
//		return SecApp.SecProp.SECE.isAllowedByProp(entityState(), Sdn.PLANERQ());
	}

	@Deprecated
	default boolean isAllowedAccess_VIEW() {
		return UO.VIEW.isAllowed(entityState());
//		return SecApp.SecProp.SECV.isAllowedByProp(entityState(), Sdn.PLANERQ());
	}

	@Deprecated
	default boolean isAllowedAccess_VIEW_EDIT(boolean... checkSecEdit) {
		return isAllowedAccess_VIEW() || (ARG.isDefEqTrue(checkSecEdit) && isAllowedAccess_EDIT());
	}


}
