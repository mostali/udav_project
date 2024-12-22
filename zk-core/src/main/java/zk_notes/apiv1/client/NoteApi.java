package zk_notes.apiv1.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.exception.RestStatusException;
import mpc.net.IllegalHttpStatusException;
import mpc.net.JHttp;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.str.JOIN;
import mpu.str.STR;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import mpe.wthttp.CleanDataResponseException;
import zk_os.AppZosConfig;
import zk_os.core.ItemPath;
import zk_os.sec.Sec;
import zk_page.ZKR;

import javax.servlet.ServletInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NoteApi {

	public static final String _UP_API_PARTURL_ = "/" + NoteApi.UP_API_PARTURL + "/";

	public static final String UP_API_PARTURL = "_api";
	public static final String _UP_API_PARTURL = "/" + UP_API_PARTURL;

	public static final String MSG_400_SET_BODY = "Set body or key 'v' for request";
	public static final String MSG_404_ITEM_NOTE_FOUND = "Item '%s' not found";
	public static final String MSG_200_ITEM_ADDED = "Item '%s' is added";

	public static Function<String, String> funcFindHostByAlias = null;

	public final ZApiUrl zApiUrl;

	@Deprecated
	public NoteApi() {
		this(APP.getAppDomain(), AppZosConfig.SUPER_KEY);
	}

	public NoteApi(String domainWithPort, String ska) {
		zApiUrl = new NoteApi.ZApiUrl(domainWithPort, ska);
	}

	@SneakyThrows
	public static void postCallWithBody(Path formStatePath) {
		ServletInputStream inputStream = ZKR.getRequest().getInputStream();
		String inData;
		if (inputStream.available() == 0) {
			inData = ZKR.getRequestQueryParamAsStr("v", null);
			if (inData == null) {
				throw RestStatusException.C400(MSG_400_SET_BODY);
			}
			inData = inData.replace(STR.NL_HTML, inData);
		} else {
			inData = IOUtils.toString(inputStream);
		}
		RW.write(formStatePath, inData, true);
		throw new CleanDataResponseException(CN.OK + ":" + inData.length());
	}

	@SneakyThrows
	public String DELETE_item(ItemPath itemPath) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_toDirectItem(itemPath);
		return JHttp.DELETE_BODY(url, null, String.class, 200);

	}

	@SneakyThrows
	public String PUT_item(ItemPath itemPath, String bodyLines, boolean viaPost) throws IllegalHttpStatusException {
		String url = viaPost ? zApiUrl.PUT_toItem(itemPath) : zApiUrl.PUT_toItem(itemPath, bodyLines);
		return viaPost ?//
				JHttp.POST_BODY(url, bodyLines, String.class, 200) ://
				JHttp.GET_BODY(url, null, String.class, 200);
	}


	@SneakyThrows
	public String GET_items(ItemPath itemPath) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_toPage(itemPath);
		return JHttp.GET_BODY(url, null, String.class, 200);

	}

	@SneakyThrows
	public String GET_item(ItemPath itemPath, String... exe) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_toItem_WithExe(itemPath, exe);
		return JHttp.GET_BODY(url, null, String.class, 200);

	}

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

		public String urlToPageWithBody(ItemPath itemPath, boolean isGetOrPut) {
			return urlToPageWithBody(itemPath, isGetOrPut, null);
		}

		public String urlToDirectPageWithBody(ItemPath itemPath) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _UP_API_PARTURL + pagePart + itemNamePart + query;
		}

		public String urlToPageWithBody(ItemPath itemPath, boolean isGetOrPut, String v) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart(v);
			return http() + sdPart + domainNameWithPort + ctxPart() + _UP_API_PARTURL + pagePart + opPart + itemNamePart + query;
		}

		public String urlToPageNoBody(ItemPath itemPath, boolean isGetOrPut) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _UP_API_PARTURL + pagePart + opPart + itemNamePart + query;
		}

		public String urlToPage(ItemPath itemPath, boolean isGetOrPut) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + _UP_API_PARTURL + pagePart + opPart + query;
		}

		private String ctxPart() {
			return X.empty(ctx) ? "" : "/" + ctx;
		}

		private static @NotNull String opSymbolPart(boolean isGetOrPut) {
			return "/" + (isGetOrPut ? "*" : "!");
		}

		private String queryPart() {
			return queryPart(null);
		}

		private String queryPart(String v) {
			String skaArg = X.empty(ska) ? "" : Sec.SKA + "=" + ska;
			String vArg = X.empty(v) ? "" : CN.V + "=" + v;
			List<String> ql = ARR.as(skaArg, vArg).stream().filter(X::notEmpty).collect(Collectors.toList());
			if (ql.isEmpty()) {
				return "";
			}
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
			return urlToPageWithBody(itemPath, true, v);
		}

		public String GET_toDirectItem(ItemPath itemPath) {
			return urlToDirectPageWithBody(itemPath);
		}

		public String GET_toItem_WithExe(ItemPath itemPath, String... exe) {
			String url = urlToPageWithBody(itemPath, true);
			if (ARG.isDef(exe)) {
				url += "&exe=" + ARG.toDef(exe);
			}
			return url;
		}

		public String PUT_toItem(ItemPath itemPath, String v) {
			return urlToPageWithBody(itemPath, false, v);
		}

		public String PUT_toItem(ItemPath itemPath) {
			return urlToPageNoBody(itemPath, false);

		}

		public String GET_toPage(ItemPath itemPath) {
			return urlToPage(itemPath, true);
		}
	}
}
