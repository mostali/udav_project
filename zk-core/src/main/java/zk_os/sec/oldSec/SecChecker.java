//package zk_notes.node_state.sec;
//
//import lombok.RequiredArgsConstructor;
//import mpt.IAnonim;
//import zk_notes.node_state.EntityState;
//import zk_os.db.net.WebUsr;
//import zk_os.sec.Sec;
//
//@RequiredArgsConstructor
//public abstract class SecChecker {
//
//	//	public static final String NO_PLANE_NAME = "//NOPLANE";
//	final Boolean isRoleOwner_That;
//
//	final Boolean isRoleAdmin_That;
//
//	final Boolean isAnonim_That;
//	final Boolean ifEmptyProp_That;
//	final Boolean isPropEqUserByLoginAlias_That;
//
//	final boolean checkUserLocks;
//
//	final boolean checkPlaneOwner;
//
//	boolean isUserEmptyThatAllowedForAll = true;
//
//	private WebUsr usr;
//
//	public SecChecker() {
//		this(true, true, false, true, true, true, true);
//	}
//
////	public boolean isAllowedByProp(EntityState entityState) {
////		return isAllowedByProp(entityState, NO_PLANE_NAME);
////	}
//
//	public abstract boolean isAllowedByProp(EntityState entityState, String plane);
//
//	public boolean isRoleOwner() {
//		return Sec.isOwnerRole(usr());
//	}
//
//	public boolean isRoleAdmin() {
//		return Sec.isAdminRole(usr());
//	}
//
//	public boolean isRoleAdminOrOwner() {
//		return isRoleOwner() || isRoleAdmin();
//	}
//
//	public boolean isAllowedDefault() {
//		WebUsr user = usr();
//		if (isRoleOwner_That != null && Sec.isOwnerRole(user)) {
//			return isRoleOwner_That;
//		}
//		if (isRoleAdmin_That != null && Sec.isAdminRole(user)) {
//			return isRoleAdmin_That;
//		}
//		if (isAnonim_That != null && IAnonim.isAnonimUnsafeTrue(user)) {
//			return isAnonim_That;
//		}
//		if (checkUserLocks && !user.isAllowedForWorkOnSite()) {
//			return false;
//		}
//		return true;
//	}
//
//	public WebUsr usr() {
//		return usr != null ? usr : (usr = Sec.getUser(null));
//	}
//}
