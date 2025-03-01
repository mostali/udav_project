package udav_net.apis.zznote;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.fs.QueryArg;
import mpc.fs.UUrl;
import mpc.net.IllegalHttpStatusException;
import mpc.net.JHttp;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NoteApi {

	public static final String _API = "_api";
	public static final String _ATI = "_ati";

	public static final String _API_PARTURL = "/" + _API;
	public static final String _API_PARTURL_ = _API_PARTURL + "/";

	public static final String _ATI_PARTURL = "/" + _ATI;
	public static final String _ATI_PARTURL_ = _ATI_PARTURL + "/";


	public static final String MSG_400_SET_BODY = "Set body or key 'v' for request";
	public static final String MSG_404_ITEM_NOTE_FOUND = "Item '%s' not found";
	public static final String MSG_200_ITEM_ADDED = "Item '%s' is added";

	public static final String SKA = "ska";

	public static final String EXE_REST = "rest";

	public static Function<String, String> funcFindHostByAlias = null;

	public final ZApiUrl zApiUrl;

	public NoteApi(String domainWithPort, String ska) {
		zApiUrl = new NoteApi.ZApiUrl(domainWithPort, ska);
	}

	@SneakyThrows
	public String DELETE_page(Pare<String, String> sdn) throws IllegalHttpStatusException {
		String url = zApiUrl.urlToPage(sdn);
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
		return GET_items(nodeId.sd3Rq(), nodeId.pageRq());
	}

	@SneakyThrows
	public String GET_items(Pare<String, String> sdn) throws IllegalHttpStatusException {
		return GET_items(ItemPath.wrapSd3(sdn.key()), ItemPath.wrapSd3(sdn.val()));
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

	@SneakyThrows
	public String GET_item(NodeID nodeId, Pare... urlArgs) throws IllegalHttpStatusException {
		return GET_item(nodeId.toItemPath(), urlArgs);

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

		public String urlToPage(Pare<String, String> sdn) {
			Pare<String, String> sdnu = ItemPath.unwrapSdn(sdn);
			String sd = sdnu.keyOr("");
			String page = IT.NE(sdnu.val(), "set page");
			if (X.notEmpty(sd)) {
				sd += ".";
			}
			String _pagePart = UUrl.normPartStart(page);
			String query = queryPart();
			return http() + sd + domainNameWithPort + ctxPart() + _API_PARTURL + _pagePart + query;
		}

		public String urlToPageWithBody(ItemPath itemPath, boolean isGetOrPut) {
			return urlToPageWithBody(itemPath, isGetOrPut, null);
		}

		public String urlToDirectPageWithBody(ItemPath itemPath) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _API_PARTURL + pagePart + itemNamePart + query;
		}

		public String urlToEventPageWithBody(NodeID nodeID, String oper, String k, String v) {
			String sd3 = nodeID.sd3Rq();
			String pagePart = UUrl.normPartEnd(nodeID.pageRq());
			String itemNamePart = UUrl.normPartEnd(nodeID.itemRq());
			String operPart = UUrl.normPartEnd(oper);
			String query = queryPartWithValue(v);
			query += ("&k=" + k);
			String url = http() + sd3 + "." + domainNameWithPort + ctxPart() + _ATI_PARTURL_ + pagePart + itemNamePart + operPart + query;
			return url;
		}

		public String urlToPageWithBody(ItemPath itemPath, boolean isGetOrPut, String v) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPartWithValue(v);
			return http() + sdPart + domainNameWithPort + ctxPart() + _API_PARTURL + pagePart + opPart + itemNamePart + query;
		}

		public String urlToPageNoBody(ItemPath itemPath, boolean isGetOrPut) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _API_PARTURL + pagePart + opPart + itemNamePart + query;
		}

		public String urlToPageNoBody_State(ItemPath itemPath, boolean isGetOrPut, String state, String key, String value) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			query += "&state=" + state + "&k=" + key + "&v=" + value;
			return http() + sdPart + domainNameWithPort + ctxPart() + _API_PARTURL + pagePart + opPart + itemNamePart + query;
		}

		public String urlToPage(ItemPath itemPath, boolean isGetOrPut) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _API_PARTURL + pagePart + opPart + query;
		}

		public String urlToPage(String sd3, String pagename, boolean isGetOrPut) {
			String sdPart = ItemPath.getSubdomainAsUrlPart(IT.NB(sd3));
			String pagePart = ItemPath.getPageAsUrlPart(IT.NB(pagename));
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _API_PARTURL + pagePart + opPart + query;
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
			return QueryArg.joinQueryFromQueryArgs(ql);
		}


		public String toPageWithItem(ItemPath itemPath, String v) {
			//			switch (itemPath.mode) {
			//				case SINGLY:
			//				case PARE:
			//				case ALL:
			//				default:
			//					throw new WhatIsTypeException(itemPath.mode);
			//			}
			return urlToPageWithBody(itemPath, true, v);
		}

		public String GET_toDirectItem(ItemPath itemPath) {
			return urlToDirectPageWithBody(itemPath);
		}

		public String GET_toItem(ItemPath itemPath, Pare... args) {
			String url = urlToPageWithBody(itemPath, true);
			if (ARG.isDef(args)) {
				url += "&" + QueryArg.join(args);
			}
			return url;
		}

		public String GET_toEvent(NodeID nodeID, String oper, String k, String v, Pare... queryArgs) {
			String url = urlToEventPageWithBody(nodeID, oper, k, v);
			if (ARG.isDef(queryArgs)) {
				url += "&" + QueryArg.join(queryArgs);
			}
			return url;
		}

		public String PUT_toItem(ItemPath itemPath, String v) {
			return urlToPageWithBody(itemPath, false, v);
		}

		public String PUT_toItem(ItemPath itemPath) {
			return urlToPageNoBody(itemPath, false);
		}

		public String PUT_toItem_State(ItemPath itemPath, String state, String key, String value) {
			return urlToPageNoBody_State(itemPath, false, state, key, value);
		}

		public String GET_toPage(ItemPath itemPath) {
			return urlToPage(itemPath, true);
		}

		public String GET_toPage(Pare<String, String> sdn) {
			return urlToPage(ItemPath.wrapSd3(sdn.key()), ItemPath.wrapSd3(sdn.val()), true);
		}

		public String GET_toPage(String sd3, String pagename) {
			return urlToPage(sd3, pagename, true);
		}

	}
}
