package zk_page.index;

import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpc.url.QueryArg;
import mpc.url.UUrl;
import mpc.net.query.QueryUrl;
import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import mpe.call_msg.core.NodeID;
import udav_net.apis.zznote.NoteApi;
import zk_notes.node_state.impl.PageState;
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

	//
	//
	//

	public static @NotNull List<Pare> getAllCurrentArgs(QueryUrl query, QueryArg... withArgs) {
		List<Pare> qArgsAll = new ArrayList<>();
		if (ADD_QP_SKA && query.hasParam(NoteApi.SKA, AppZosConfig.SUPER_KEY)) {
			qArgsAll.add(QueryArg.of(NoteApi.SKA, AppZosConfig.SUPER_KEY));
		}
//		List<Pare<String, String>> currentQArgs = query.getQueryArgsByNames(CHECK_QUERY_ARGS);
		List<Pare<String, String>> currentQArgs = (List) query.getQueryArgsAsList();
//		currentQArgs = QueryUrl.keepFirstMatchingAndRemoveOthers(currentQArgs, ARR.as(CN.TB));
		qArgsAll.addAll(currentQArgs);
		qArgsAll.addAll(ARR.as(withArgs));
		return qArgsAll;
	}

	public String toRootLink() {
		switch (this) {
			case ROOT:
//				a
				String sfx_ska = SpVM.get().hasSkaStrict() ? "?" + NoteApi.SKA + "=" + AppZosConfig.SUPER_KEY : "";
				return APP.HOST.getAppHostWithProtocol() + sfx_ska;
			default:
				throw new WhatIsTypeException("illegal type '%s' (except '%s') for build RootLink", this, ROOT);
		}
	}

	public String toPlaneLink(String planeName, QueryArg... withArgs) {
		switch (this) {
			case PLANE:
				IT.state(!NodeID.isPlaneAliasIndexOrEmpty(planeName), "why here plane '%s'", planeName);
				List<Pare> qArgsAll = getAllCurrentArgs(SpVM.get().getQuery(), withArgs);
				String finalUrl = APP.HOST.getAppHostWithPlane(planeName, qArgsAll.toArray(new Pare[0]));
				return finalUrl;
			default:
				throw new WhatIsTypeException("illegal type '%s' (except '%s') for build PlaneLink", this, PLANE);
		}
	}

	public String toPageLink(Pare<String, String> sdn, QueryArg... args) {
		return toPageLink(sdn.key(), sdn.val(), args);
	}

	public String toPageLink(String planeName, String pagename, QueryArg... args) {
		return toPageLink(planeName, pagename, null, args);
	}

	public String toPageLink(String plane, String pagename, String other_path, QueryArg... withArgs) {
		switch (this) {
			case PAGE:

				List<Pare> qArgsAll = getAllCurrentArgs(SpVM.get().getQuery(), withArgs);

				String appHostWithPlane = NodeID.isPlaneAliasIndexOrEmpty(plane) ? APP.HOST.getAppHostWithProtocol() : APP.HOST.getAppHostWithPlane(plane);

				String urlWithPath = UUrl.joinUrlPaths(ARR.filterNotEmpty(appHostWithPlane, pagename, other_path));

				String finalUrl = QueryArg.joinToUrl(urlWithPath, qArgsAll.toArray(new Pare[0]));

				return finalUrl;

//				if (true) {
//
//					QueryUrl query = SpVM.get().getQuery();
//					List<Pare> qArgsAll = new ArrayList<>();
//					if (ADD_QP_SKA && query.hasParam(NoteApi.SKA, AppZosConfig.SUPER_KEY)) {
//						qArgsAll.add(QueryArg.of(NoteApi.SKA, AppZosConfig.SUPER_KEY));
//					}
////					String appHost = APP.HOST.getAppHost0();
//					String plane = ItemPath.isPlaneAliasIndexOrEmpty(planeName) ? "" : planeName + ".";
//					String uri = other_path == null ? "" : UUrl.normFileStart(other_path);
//					String qArgIn = QueryArg.joinAsString(withArgs);
//					String qArgAll = X.empty(qArgSka) ? "?" + qArgIn : JOIN.argsBy("&", qArgSka, qArgIn);
////					return "http://" + plane + appHost + "/" + pagename + uri + qArgAll;
//
//					String appHostWithPlane = APP.HOST.getAppHostWithPlane(plane);
//					String urlWithPath = appHostWithPlane + UUrl.normPartStart(pagename) + UUrl.normPartStart(uri) + qArgAll;
//					return QueryArg.joinToUrl(urlWithPath, qArgsAll.toArray(new Pare[0]));
//				}


			default:
				throw new WhatIsTypeException("illegal type '%s' (except '%s') for build PageLink", this, PAGE);
		}
	}


	//	public static void toRoot_Redirect(String planeName, String pagename, QueryArg... args) {
//		ZKR.redirectToLocation("");
//	}
	public static void toPage_Redirect_CheckNoIndex(String planeName, String pagename, QueryArg... args) {
		if (NodeID.PAGE_INDEX_ALIAS.equals(pagename)) {
			ZKR.redirectToLocation(RSPath.PLANE.toPlaneLink(planeName, args), false);
		} else {
			toPage_Redirect(planeName, pagename, args);
		}
	}

	public static void toPlane_Redirect_CheckNoIndex(String planeName, QueryArg... args) {
		if (NodeID.PLANE_INDEX_ALIAS.equals(planeName)) {
			ZKR.redirectToLocation(RSPath.ROOT.toRootLink(), false);
		} else {
			toPlane_Redirect(planeName, args);
		}
	}

	//
	//

	public static void toPage_Redirect(String planeName, String pagename, QueryArg... args) {
		ZKR.redirectToLocation(toPage(planeName, pagename, args), false);
	}

	public static String toPage(String planeName, String pagename, QueryArg... args) {
		return RSPath.PAGE.toPageLink(planeName, pagename, args);
	}

	public static void toPlane_Redirect(String planeName, QueryArg... args) {
		ZKR.redirectToLocation(RSPath.PLANE.toPlaneLink(planeName, args), false);
	}

	//
	//

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
