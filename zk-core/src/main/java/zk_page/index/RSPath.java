package zk_page.index;

import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpu.IT;
import mpu.X;
import mpu.pare.Pare;
import udav_net.apis.zznote.NoteApi;
import zk_notes.node.NodeDir;
import zk_os.AppZosConfig;
import udav_net.apis.zznote.ItemPath;
import zk_page.ZKR;
import zk_page.core.SpVM;

public enum RSPath {
	ROOT, PLANE, PAGE;

	public static void redirectToPageWitNode(NodeDir dstNode) {
		ZKR.redirectToLocation(toPlanPage(dstNode), false);
	}

	public static String toPlanPage(NodeDir dstNode) {
		Pare<String, String> sdn = dstNode.sdn();
		boolean rootSd3 = isSd3Index(sdn.key());
		boolean rootPage = isSd3Index(sdn.val());
		if (rootSd3 && rootPage) {
			return ROOT.toRootLink();
		} else if (rootPage) {
			return PLANE.toPlanPage(sdn);
		} else {
			return PAGE.toPlanPage(sdn);
		}
	}

	public String toRootLink() {
		switch (this) {
			case ROOT:
				String sfx_ska = SpVM.get().hasSka() ? "?" + NoteApi.SKA + "=" + AppZosConfig.SUPER_KEY : "";
				return "http://" + APP.PROP.getAppHost() + sfx_ska;
			default:
				throw new WhatIsTypeException(name() + " path");
		}
	}

	public String toPlaneLink(String planeName) {
		switch (this) {
			case PLANE:
				IT.state(!isSd3Index(planeName), "why here plane '%s'", planeName);
				String sfx_ska = SpVM.get().hasSka() ? "?" + NoteApi.SKA + "=" + AppZosConfig.SUPER_KEY : "";
				return "http://" + planeName + "." + APP.PROP.getAppHost() + sfx_ska;
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

	public static void toPage_Redirect(String sd3, String pageName) {
		ZKR.redirectToLocation(RSPath.PAGE.toPlanPage(sd3, pageName), false);
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
				String sfx_ska = SpVM.get().hasSka() ? "?" + NoteApi.SKA + "=" + AppZosConfig.SUPER_KEY : "";
				return "http://" + (RSPath.isSd3Index(planeName) ? "" : planeName + ".") + APP.PROP.getAppHost() + "/" + pagename + (other_path == null ? "" : "/" + other_path) + sfx_ska;
			default:
				throw new WhatIsTypeException(name() + " path");
		}
	}

	public static boolean isSd3Index(String key) {
		return X.empty(key) || ItemPath.isIndex(key);
	}

}
