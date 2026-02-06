package udav_net.apis.zznote;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.str.ObjTo;
import mpc.url.QueryArg;
import mpc.url.UUrl;
import mpc.net.IllegalHttpStatusException;
import mpc.net.JHttp;
import mpe.str.CN;
import mpe.call_msg.core.NodeID;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import mpu.str.UST;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NoteApi {

	public static final String PK_EXE = CN.EXE;
	//	@Deprecated
//	public static final String PK_REST_EXE__OLD = "rest";
	public static final String PK_STATE = CN.STATE;
	public static final String PK_K = "k";
	public static final String PK_V = "v";


	public static void main(String[] args) {

	}

	public static final String MSG_400_SET_BODY = "Set body or key 'v' for request";
	public static final String MSG_404_ITEM_NOTE_FOUND = "Item '%s' not found";
	public static final String MSG_200_ITEM_ADDED = "Item '%s' is added";

	public static final String SKA = "ska";

	public static Function<String, String> funcFindHostByAlias = null;

	public final ZApiUrl zApiUrl;

	public NoteApi(String domainWithPort, String ska) {
		zApiUrl = new NoteApi.ZApiUrl(domainWithPort, ska);
	}

	@SneakyThrows
	public String DELETE_page(Pare<String, String> sdn) throws IllegalHttpStatusException {
		String url = zApiUrl.urlTo_Page(sdn);
		return JHttp.DELETE_BODY(url, null, String.class, 200);
	}

	@SneakyThrows
	public String DELETE_item(ItemPath itemPath) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_toDirectItem(itemPath);
		return JHttp.DELETE_BODY(url, null, String.class, 200);
	}

	@SneakyThrows
	public String PUT_item(ItemPath itemPath, String bodyLines, boolean viaPOST_GET) throws IllegalHttpStatusException {
		String url = viaPOST_GET ? zApiUrl.PUT_toItem(itemPath) : zApiUrl.PUT_toItem(itemPath, bodyLines);
		return viaPOST_GET ?//
				JHttp.POST_BODY(url, bodyLines, String.class, 200) ://
				JHttp.GET_BODY(url, null, String.class, 200);
	}

	@SneakyThrows
	public String PUT_item_state(ItemPath itemPath, String state, String key, String value, boolean viaPOST_GET) throws IllegalHttpStatusException {
		String url = zApiUrl.PUT_toItem_State(itemPath, state, key, value);
		return viaPOST_GET ?//
				X.throwException("not supported POST") :
//				JHttp.POST_BODY(url, bodyLines, String.class, 200) ://
				JHttp.GET_BODY(url, null, String.class, 200);
	}


	/**
	 * *************************************************************
	 * ----------------------------GET ITEM'S ----------------------
	 * *************************************************************
	 */

	@SneakyThrows
	public String GET_items(NodeID nodeId) throws IllegalHttpStatusException {
		return GET_items(nodeId.planeRq(), nodeId.pageRq());
	}

	@SneakyThrows
	public List<String> GET_itemsMap(String page) throws IllegalHttpStatusException {
		List<String> strings = GET_itemsList(page);
		return strings;
	}

	@SneakyThrows
	public List<String> GET_itemsList(String page) throws IllegalHttpStatusException {
		return SPLIT.allByNL(GET_items(page));
	}

	@SneakyThrows
	public String GET_items(String page) throws IllegalHttpStatusException {
		return GET_items(Pare.of(NodeID.PLANE_INDEX_ALIAS, page == null ? NodeID.PAGE_INDEX_ALIAS : IT.NE(page, "set page")));
	}


	public String GET_items(Pare<String, String> sdn) throws IllegalHttpStatusException {
		return GET_items(NodeID.wrapPlane(sdn.key()), NodeID.wrapPlane(sdn.val()));
	}

	@SneakyThrows
	public String GET_items(String sd3, String pagename) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_toPage(IT.NB(sd3), IT.NB(pagename));
		return JHttp.GET_BODY(url, null, String.class, 200);
	}

	/**
	 * *************************************************************
	 * ----------------------------GET ITEM ----------------------
	 * *************************************************************
	 */

	public <T> T GET_item_as(NodeID nodeId, Class<T> asType, Pare... urlArgs) throws IllegalHttpStatusException {
		String s = GET_item(ItemPath.of(nodeId), urlArgs);
		return UST.strTo(s, asType);
	}

	@SneakyThrows
	public String GET_item(NodeID nodeId, Pare... urlArgs) throws IllegalHttpStatusException {
//		return GET_item(nodeId.toItemPath(), urlArgs);
		return GET_item(ItemPath.of(nodeId), urlArgs);
	}

	@SneakyThrows
	public String GET_item(ItemPath itemPath, Pare... urlArgs) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_toItem(itemPath, urlArgs);
		return JHttp.GET_BODY(url, null, String.class, 200);

	}

	@SneakyThrows
	public String GET_event(NodeID nodeID, String oper, String k, String v, Pare... queryArgs) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_toEvent(nodeID, oper, k, v, queryArgs);
		return JHttp.GET_BODY(url, null, String.class, 200);
	}

	/**
	 * *************************************************************
	 * ---------------------------- ----------------------
	 * *************************************************************
	 */

	public static class ZApiUrl {

		final String domainNameWithPort;
		final String ctx = "";
		final boolean https = false;
		final String ska;

		public ZApiUrl(String domainNameWithPort, String ska) {
			this.domainNameWithPort = domainNameWithPort;
			this.ska = ska;
		}


		private Object http() {
			return https ? "https://" : "http://";
		}

		public String urlTo_Page(Pare<String, String> sdn) {
			Pare<String, String> sdnu = NodeID.unwrapSdn(sdn);
			String sd = sdnu.keyOr("");
			String page = IT.NE(sdnu.val(), "set page");
			if (X.notEmpty(sd)) {
				sd += ".";
			}
			String _pagePart = UUrl.normPartStart(page);
			String query = queryPart();
			return http() + sd + domainNameWithPort + ctxPart() + ApiCase._api._NAME + _pagePart + query;
		}

		public String urlTo_PageWithBody(ItemPath itemPath, boolean isGetOrPut) {
			return urlTo_PageWithBody(itemPath, isGetOrPut, null);
		}

		public String urlTo_DirectPageWithBody(ItemPath itemPath) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + ApiCase._api._NAME + pagePart + itemNamePart + query;
		}

		public String urlTo_EventPageWithBody(NodeID nodeID, String oper, String k, String v) {
			String sd3 = nodeID.planeRq();
			String sd3Part = NodeID.isPlaneAliasIndex(sd3) ? "" : sd3 + ".";
			String pagePart = UUrl.normPartEnd(nodeID.pageRq());
			String itemNamePart = UUrl.normPartEnd(nodeID.itemRq());
			String operPart = UUrl.normPartEnd(oper);
			String query = queryPartWithValue(v);
			query += ("&k=" + k);
			String url = http() + sd3Part + domainNameWithPort + ctxPart() + ApiCase._ati._NAME_ + pagePart + itemNamePart + operPart + query;
			return url;
		}

		public String urlTo_PageWithBody(ItemPath itemPath, boolean isGetOrPut, String v) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPartWithValue(v);
			return http() + sdPart + domainNameWithPort + ctxPart() + ApiCase._api._NAME + pagePart + opPart + itemNamePart + query;
		}

		public String urlTo_PageNoBody(ItemPath itemPath, boolean isGetOrPut) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + ApiCase._api._NAME + pagePart + opPart + itemNamePart + query;
		}

		public String urlTo_PageNoBody_State(ItemPath itemPath, boolean isGetOrPut, String state, String key, String value) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			query += "&state=" + state + "&k=" + key + "&v=" + value;
			return http() + sdPart + domainNameWithPort + ctxPart() + ApiCase._api._NAME + pagePart + opPart + itemNamePart + query;
		}

		public String urlTo_Page(ItemPath itemPath, boolean isGetOrPut) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + ApiCase._api._NAME + pagePart + opPart + query;
		}

		public String urlTo_Page(String sd3, String pagename, boolean isGetOrPut) {
			String sdPart = ItemPath.getSubdomainAsUrlPart(IT.NB(sd3));
			String pagePart = ItemPath.getPageAsUrlPart(IT.NB(pagename));
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + ApiCase._api._NAME + pagePart + opPart + query;
		}


		private String ctxPart() {
			return X.empty(ctx) ? "" : "/" + ctx;
		}

		private static @NotNull String opSymbolPart(boolean isGetOrPut) {
			return "/" + (isGetOrPut ? "*" : "!");
		}

		private String queryPart() {
			return queryPartWithValue(null);
		}

		private String queryPartWithValue(String v) {
			String skaArg = X.empty(ska) ? "" : SKA + "=" + ska;
			String vArg = X.empty(v) ? "" : CN.V + "=" + v;
			List<String> ql = ARR.as(skaArg, vArg).stream().filter(X::notEmpty).collect(Collectors.toList());
			if (ql.isEmpty()) {
				return "";
			}
			return joinQueryFromQueryArgsFinal(ql);
		}

		private static String joinQueryFromQueryArgsFinal(List<String> ql) {
			return JOIN.allBy(ql, "&", "?", "");
		}

		public String toPageWithItem(ItemPath itemPath, String v) {
			//			switch (itemPath.mode) {
			//				case SINGLY:
			//				case PARE:
			//				case ALL:
			//				default:
			//					throw new WhatIsTypeException(itemPath.mode);
			//			}
			return urlTo_PageWithBody(itemPath, true, v);
		}

		public String GET_toDirectItem(ItemPath itemPath) {
			return urlTo_DirectPageWithBody(itemPath);
		}

		public String GET_toItem(ItemPath itemPath, Pare... args) {
			String url = urlTo_PageWithBody(itemPath, true);
			if (ARG.isDef(args)) {
				url += "&" + QueryArg.joinAsString(args);
			}
			return url;
		}

		public String GET_toEvent(NodeID nodeID, String oper, String k, String v, Pare... queryArgs) {
			String url = urlTo_EventPageWithBody(nodeID, oper, k, v);
			if (ARG.isDef(queryArgs)) {
				url += "&" + QueryArg.joinAsString(queryArgs);
			}
			return url;
		}

		public String PUT_toItem(ItemPath itemPath, String v) {
			return urlTo_PageWithBody(itemPath, false, v);
		}

		public String PUT_toItem(ItemPath itemPath) {
			return urlTo_PageNoBody(itemPath, false);
		}

		public String PUT_toItem_State(ItemPath itemPath, String state, String key, String value) {
			return urlTo_PageNoBody_State(itemPath, false, state, key, value);
		}

		public String GET_toPage(ItemPath itemPath) {
			return urlTo_Page(itemPath, true);
		}

		public String GET_toPage(Pare<String, String> sdn) {
			return urlTo_Page(NodeID.wrapPlane(sdn.key()), NodeID.wrapPlane(sdn.val()), true);
		}

		public String GET_toPage(String sd3, String pagename) {
			return urlTo_Page(sd3, pagename, true);
		}

	}

}
