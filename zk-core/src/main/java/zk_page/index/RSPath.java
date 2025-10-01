package zk_page.index;

import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpc.fs.QueryArg;
import mpc.net.query.QueryUrl;
import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import udav_net.apis.zznote.ItemPath;
import udav_net.apis.zznote.NoteApi;
import zk_notes.node.NodeDir;
import zk_notes.node_state.libs.PageState;
import zk_os.AppZosConfig;
import zk_os.core.Sdn;
import zk_page.ZKR;
import zk_page.core.SpVM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum RSPath {
	ROOT, PLANE, PAGE;

	public static final Boolean ADD_QP_SKA = true;
	public static final String[] CHECK_QUERY_ARGS = Arrays.stream(PageState.TabsMode.values()).filter(tm -> tm != PageState.TabsMode.def).map(Enum::name).toArray(String[]::new);

	public static String toLink(Sdn sdn) {
		RSPath pathType = sdn.getPathType();
		switch (pathType) {
			case ROOT:
				return pathType.toRootLink();
			case PLANE:
				return pathType.toPlaneLink(sdn.key());
			case PAGE:
				return pathType.toPageLink(sdn);
			default:
				throw new WhatIsTypeException(pathType);
		}
	}

	public String icon() {
		switch (this) {
			case ROOT:
				return SYMJ.STARMANY;
			case PLANE:
				return SYMJ.TREE;
			case PAGE:
				return SYMJ.FLOWER;

			default:
				throw new WhatIsTypeException(this);
		}
	}

	public String toRootLink() {
		switch (this) {
			case ROOT:
				String sfx_ska = SpVM.get().hasSka() ? "?" + NoteApi.SKA + "=" + AppZosConfig.SUPER_KEY : "";
				return APP.HOST.getAppHost() + sfx_ska;
			default:
				throw new WhatIsTypeException(name() + " path");
		}
	}

	public String toPlaneLink(String planeName, QueryArg... args) {
		switch (this) {
			case PLANE:
				IT.state(!isSd3Index(planeName), "why here plane '%s'", planeName);
				QueryUrl query = SpVM.get().getQuery();
				List<Pare> qArgs = new ArrayList<>();
				if (ADD_QP_SKA && query.hasParam(NoteApi.SKA)) {
					qArgs.add(QueryArg.of(NoteApi.SKA, AppZosConfig.SUPER_KEY));
				}
				List<Pare<String, String>> currentQArgs = query.getQueryArgs(CHECK_QUERY_ARGS);
				qArgs.addAll(currentQArgs);
				qArgs.addAll(ARR.as(args));
				String sfx_ska = QueryArg.join((qArgs.toArray(new Pare[0])));
				return APP.HOST.getAppHostWithPlane(planeName) + "?" + sfx_ska;
			default:
				throw new WhatIsTypeException(name() + " path");
		}
	}

	//	public static void toRoot_Redirect(String planeName, String pagename, QueryArg... args) {
//		ZKR.redirectToLocation("");
//	}
	public static void toPage_Redirect(String planeName, String pagename, QueryArg... args) {
		ZKR.redirectToLocation(RSPath.PAGE.toPageLink(planeName, pagename, args), false);
	}

	public static void toPlane_Redirect(String planeName, QueryArg... args) {
		ZKR.redirectToLocation(RSPath.PLANE.toPlaneLink(planeName, args), false);
	}


	public String toPageLink(Pare<String, String> sdn, QueryArg... args) {
		return toPageLink(sdn.key(), sdn.val(), args);
	}

	public String toPageLink(String planeName, String pagename, QueryArg... args) {
		return toPageLink(planeName, pagename, null, args);
	}

	public String toPageLink(String planeName, String pagename, String other_path, QueryArg... args) {
		switch (this) {
			case PAGE:
//				IT.state(!RSPath.isSd3Index(planeName), "why here plane page '%s'", planeName);
				boolean hasSka = SpVM.get().hasSka();
				String qArgSka = hasSka ? "?" + NoteApi.SKA + "=" + AppZosConfig.SUPER_KEY : "";
				String appHost = APP.HOST.getAppHost0();
				String plane = RSPath.isSd3Index(planeName) ? "" : planeName + ".";
				String uri = other_path == null ? "" : "/" + other_path;
				String qArgIn = QueryArg.join(args);
				String qArgAll = X.empty(qArgSka) ? "?" + qArgIn : QueryArg.join(qArgSka, qArgIn);
				return "http://" + plane + appHost + "/" + pagename + uri + qArgAll;
			default:
				throw new WhatIsTypeException(name() + " path");
		}
	}

	public static boolean isSd3Index(String key) {
		return X.empty(key) || ItemPath.isAliasIndexPlane(key);
	}

	@Deprecated //?
	public String nameCom() {
		switch (this) {
			case PLANE:
				return "Spaces";
			case PAGE:
				return "Pages";
			case ROOT:
				return "Nodes";
			default:
				throw new WhatIsTypeException(this);
		}
	}


}
