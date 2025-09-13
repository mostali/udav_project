package zk_page.index;

import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpc.fs.QueryArg;
import mpc.fs.UUrl;
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
import java.util.stream.Stream;

public enum RSPath {
	ROOT, PLANE, PAGE;

	public static final Boolean ADD_QP_SKA = true;
	public static final String[] CHECK_QUERY_ARGS = Arrays.stream(PageState.TabsMode.values()).filter(tm -> tm != PageState.TabsMode.def).map(Enum::name).toArray(String[]::new);

	public static void redirectToPageWitNode(NodeDir dstNode) {
		ZKR.redirectToLocation(toPlanPage(dstNode), false);
	}

	public static String toPlanPage(NodeDir dstNode) {
		Sdn sdn0 = dstNode.sdn0();
		RSPath pathType = sdn0.getPathType();
		if (true) {
			switch (pathType) {
				case ROOT:
					return ROOT.toRootLink();
				case PLANE:
					return PLANE.toPlanPage(sdn0);
				case PAGE:
					return PAGE.toPlanPage(sdn0);
				default:
					throw new WhatIsTypeException(pathType);
			}
		}
		{
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
				return "http://" + APP.MAIN.getAppHost() + sfx_ska;
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
				return "http://" + planeName + "." + APP.MAIN.getAppHost() + "?" + sfx_ska;
			default:
				throw new WhatIsTypeException(name() + " path");
		}
	}

	public static void toPlanPage_Redirect(String planeName, String pagename, QueryArg... args) {
		ZKR.redirectToLocation(RSPath.PAGE.toPlanPage(planeName, pagename, args), false);
	}

	public static void toPlane_Redirect(String planeName, QueryArg... args) {
		ZKR.redirectToLocation(RSPath.PLANE.toPlaneLink(planeName, args), false);
	}

	public static void toPage_Redirect(String sd3, String pageName, QueryArg... args) {
		ZKR.redirectToLocation(RSPath.PAGE.toPlanPage(sd3, pageName, args), false);
	}

	public String toPlanPage(Pare<String, String> sdn, QueryArg... args) {
		return toPlanPage(sdn.key(), sdn.val(), args);
	}

	public String toPlanPage(String planeName, String pagename, QueryArg... args) {
		return toPlanPage(planeName, pagename, null, args);
	}

	public String toPlanPage(String planeName, String pagename, String other_path, QueryArg... args) {
		switch (this) {
			case PAGE:
//				IT.state(!RSPath.isSd3Index(planeName), "why here plane page '%s'", planeName);
				boolean hasSka = SpVM.get().hasSka();
				String qArgSka = hasSka ? "?" + NoteApi.SKA + "=" + AppZosConfig.SUPER_KEY : "";
				String appHost = APP.MAIN.getAppHost();
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
