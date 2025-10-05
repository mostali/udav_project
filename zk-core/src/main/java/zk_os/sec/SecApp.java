package zk_os.sec;

import mpc.exception.WhatIsTypeException;
import mpe.str.CN;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ENUM;
import mpu.str.SPLIT;
import zk_notes.node_state.EntityState;
import zk_notes.node_state.FormState;
import zk_notes.node_state.sec.SecPropChecker;
import zk_os.db.net.WebUsr;

import java.util.List;

public class SecApp {

	public enum SecProp {
		SECV, SECE, SECR;

		public static SecProp valueOf(String secPropName, SecProp... defRq) {
			return ENUM.valueOf(secPropName, SecProp.class, true, defRq);
		}

		public List<String> getPropList(EntityState entityState) {
			switch (this) {
				case SECV:
				case SECE:
				case SECR:
					return SPLIT.allByComma(entityState.get(nameLC(), ""));

				default:
					throw new WhatIsTypeException(this);
			}

		}

		public String nameLC() {
			return name().toLowerCase();
		}

		public boolean isAllowedByProp(EntityState entityState, String plane) {
			return SecPropChecker.ofSecProp(this).isAllowedByProp(entityState, plane);
		}

		public SecPropChecker toSecPropChecker() {
			return new SecPropChecker(nameLC());
		}

		public boolean isAllowedForAnonimView(FormState pageState) {
			List<String> propList = SecApp.SecProp.SECV.getPropList(pageState);
			if (propList.contains(SecApp.SECFORALL)) {
				return true;
			}

			return pageState.get_USER(null) == null;
		}
	}

	public static final String SECFORALL = "@";
	public static final String SECFORUSER = "#";
	public static final String ANONIM = "anonim";

	public static final String SECR = "secr";//run
	public static final String SECE = "sece";//edit
	public static final String SECV = "secv";//view
	public static final String USER = CN.USER;

	public static boolean checkIsAllowInList(List<String> secAsList, WebUsr webUsr, boolean... checkForAllPattern) {
		if (secAsList == null || secAsList.isEmpty()) {
			return false;
		}
		if (ARG.isDefEqTrue(checkForAllPattern) && secAsList.contains(SECFORALL)) {
			return true;
		}
		return webUsr == null ? false : ARR.containsAny(secAsList, webUsr.getAlias(), webUsr.getLogin());
	}

	public static List<String> getSecRList(EntityState entityState) {
		return SPLIT.allByComma(entityState.get(SECR, ""));
	}

	public static List<String> getSecEList(EntityState entityState) {
		return SPLIT.allByComma(entityState.get(SECE, ""));
	}

	public static List<String> getSecVList(EntityState entityState) {
		return SPLIT.allByComma(entityState.get(SECV, ""));
	}
}
