package zk_notes.node_state.sec;

import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import zk_notes.node_state.EntityState;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecApp;
import zk_os.sec.SecEnt;

import java.util.List;
import java.util.function.Function;

public class SecPropChecker extends SecChecker {

	public static SecPropChecker ofSecProp(SecApp.SecProp secProp) {
		return new SecPropChecker(secProp.nameLC());
	}

//	public static SecPropChecker ofSecV() {
//		return new SecPropChecker(SecApp.SECV);
//	}
//
//	public static SecPropChecker ofSecE() {
//		return new SecPropChecker(SecApp.SECE);
//	}

	public static SecPropChecker ofSecR() {
		return new SecPropChecker(SecApp.SECR);
	}

	public static SecPropChecker ofSecUSER() {
		return new SecPropChecker(SecApp.USER);
	}

	final String secPropName;

	public SecPropChecker(String prop) {
		super(true, true, false, true, true, true, true);
		this.secPropName = prop;

	}
//	public SecPropChecker(String prop) {
//		this(prop, true, true, false, true, true, true, true);
//	}
//
//	public SecPropChecker(String prop, Boolean isOwner_That, Boolean isAdmin_That, Boolean isAnonim_That, Boolean ifEmptyProp_That, Boolean isPropEqUserByLoginAlias_That, boolean checkUserLocks, boolean checkPlaneOwner) {
//		super(isOwner_That, isAdmin_That, isAnonim_That, ifEmptyProp_That, isPropEqUserByLoginAlias_That, checkUserLocks, checkPlaneOwner);
//		this.secPropName = prop;
//	}

	@Override
	public boolean isAllowedByProp(EntityState entityState, String plane) {

		if (isAllowedDefault()) {
			return true;
		}

		WebUsr user = usr();

		if (super.checkPlaneOwner && SecEnt.isPlaneOwner(user, IT.NN(plane, "set plane for valid security check's"))) {
			return true;
		}

		Function<String, Boolean> checkerUserProp = (userPropValue) -> user.equalsByLoginOrAlias(userPropValue);

		String secProp_value = entityState.get(secPropName, null);
		if (X.empty(secProp_value)) {
			if (super.ifEmptyProp_That) {
				String userProp = entityState.get(SecApp.USER, null);
				if (X.empty(userProp)) {
					return isUserEmptyThatAllowedForAll;
				}
				if (checkerUserProp.apply(userProp)) {
					return true;
				}
			}
			return false;
		}

		SecApp.SecProp secProp = SecApp.SecProp.valueOf(secPropName, null);
		if (secProp != null) {
			List<String> propList = secProp.getPropList(entityState);
			boolean isAllowedByPropList = SecApp.checkIsAllowInList(propList, user, true);
			if (isAllowedByPropList) {
				return true;
			}
		}

//		if (SecApp.SECFORALL.equals(secProp_value)) {
//			return true;
//		}

		return super.isPropEqUserByLoginAlias_That && checkerUserProp.apply(secProp_value);

	}
}
