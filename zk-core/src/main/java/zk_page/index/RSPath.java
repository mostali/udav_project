package zk_page.index;

import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpu.IT;
import mpu.X;
import mpu.pare.Pare;
import zk_os.AFCC;
import zk_os.AppZosConfig;
import zk_os.sec.Sec;
import zk_page.ZKR;
import zk_page.core.SpVM;

public enum RSPath {
	ROOT, PLANE, PAGE;

	public String toRootLink() {
		switch (this) {
			case ROOT:
				String sfx_ska = SpVM.get().hasSka() ? "?" + Sec.SKA + "=" + AppZosConfig.SUPER_KEY : "";
				return "http://" + APP.getAppDomain() + sfx_ska;
			default:
				throw new WhatIsTypeException(name() + " path");
		}
	}

	public String toPlaneLink(String planeName) {
		switch (this) {
			case PLANE:
				IT.state(!isSd3Index(planeName), "why here plane '%s'", planeName);
				String sfx_ska = SpVM.get().hasSka() ? "?" + Sec.SKA + "=" + AppZosConfig.SUPER_KEY : "";
				return "http://" + planeName + "." + APP.getAppDomain() + sfx_ska;
			default:
				throw new WhatIsTypeException(name() + " path");
		}
	}

	public static void toPlanPage_Redirect(String planeName, String pagename) {
		ZKR.redirectToLocation(RSPath.PAGE.toPlanPage(planeName, pagename), false);
	}

	public static void toPlane_Redirect(String planeName) {
		ZKR.redirectToLocation(RSPath.PLANE.toPlaneLink(planeName), false);
	}

	public String toPlanPage(Pare<String, String> sdn) {
		return toPlanPage(sdn.key(), sdn.val());
	}

	public String toPlanPage(String planeName, String pagename) {
		return toPlanPage(planeName, pagename, null);
	}

	public String toPlanPage(String planeName, String pagename, String other_path) {
		switch (this) {
			case PAGE:
//				IT.state(!RSPath.isSd3Index(planeName), "why here plane page '%s'", planeName);
				String sfx_ska = SpVM.get().hasSka() ? "?" + Sec.SKA + "=" + AppZosConfig.SUPER_KEY : "";
				return "http://" + (RSPath.isSd3Index(planeName) ? "" : planeName + ".") + APP.getAppDomain() + "/" + pagename + (other_path == null ? "" : "/" + other_path) + sfx_ska;
			default:
				throw new WhatIsTypeException(name() + " path");
		}
	}

	public static boolean isSd3Index(String key) {
		return X.empty(key) || isSd3IndexStrict(key);
	}

	public static boolean isSd3IndexStrict(String key) {
		return AFCC.SD3_INDEX_ALIAS.equals(key);
	}

}
