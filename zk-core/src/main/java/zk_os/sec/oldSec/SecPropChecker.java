//package zk_notes.node_state.sec;
//
//import mpu.IT;
//import mpu.X;
//import zk_notes.node_state.EntityState;
//import zk_os.db.net.WebUsr;
//import zk_os.sec.SecApp;
//import zk_os.sec.SecEnt;
//
//import java.util.List;
//import java.util.function.Function;
//
//public class SecPropChecker extends SecChecker {
//
//	public static SecPropChecker ofSecProp(SecApp.SecProp secProp) {
//		return new SecPropChecker(secProp.nameLC());
//	}
//
//	public static SecPropChecker ofSecR() {
//		return new SecPropChecker(SecApp.SECR);
//	}
//
//	public static SecPropChecker ofSecUSER() {
//		return new SecPropChecker(SecApp.USER);
//	}
//
//	final String secPropName;
//
//	public SecPropChecker(String prop) {
//		super(true, true, false, true, true, true, true);
//		this.secPropName = prop;
//
//	}
//
//	@Override
//	public boolean isAllowedByProp(EntityState entityState, String plane) {
//
//		if (isAllowedDefault()) {
//			return true;
//		}
//
//		WebUsr user = usr();
//
//		if (super.checkPlaneOwner && !user.isAnonimStrict() && SecEnt.isPlaneOwner(user, IT.NN(plane, "set plane for valid security check's"))) {
//			return true;
//		}
//
//		Function<String, Boolean> checkerUserProp = (userPropValue) -> user.isEqualsUserByLoginOrAlias(userPropValue);
//
//		String secProp_value = entityState.get(secPropName, null);
//		if (X.empty(secProp_value)) {
//			if (super.ifEmptyProp_That) {
//				String userProp = entityState.get(SecApp.USER, null);
//				if (X.empty(userProp)) {
//					return isUserEmptyThatAllowedForAll;
//				}
//				if (checkerUserProp.apply(userProp)) {
//					return true;
//				}
//			}
//			return false;
//		}
//
//		SecApp.SecProp secProp = SecApp.SecProp.valueOf(secPropName, null);
//		if (secProp != null) {
//			List<String> propList = secProp.getPropList(entityState);
//			boolean isAllowedByPropList = SecApp.checkIsAllowInList(propList, user, true);
//			if (isAllowedByPropList) {
//				return true;
//			}
//		}
//
//		return super.isPropEqUserByLoginAlias_That && checkerUserProp.apply(secProp_value);
//
//	}
//}
