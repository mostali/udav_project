package zk_os.api.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.net.IllegalHttpStatusException;
import mpc.net.JHttp;
import mpc.str.sym.SYMJ;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARR;
import mpu.str.JOIN;
import org.jetbrains.annotations.NotNull;
import zk_os.core.ItemPath;
import zk_os.sec.Sec;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NoteApi {

	public static final String UP_API_PARTURL = "_api";
	public static final String UP_API_PARTURL_ = "/" + UP_API_PARTURL;

	final ZApiUrl zApiUrl;

	public NoteApi(String domainWithPort, String ska) {
		zApiUrl = new NoteApi.ZApiUrl(domainWithPort, ska);
	}

	@SneakyThrows
	public String DELETE_item(ItemPath itemPath) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_toDirectItem(itemPath);
		return JHttp.DELETE_BODY(url, null, String.class, 200);

	}

	@SneakyThrows
	public String PUT_item(ItemPath itemPath, String bodyLines) throws IllegalHttpStatusException {
		bodyLines = bodyLines.replace("\n", "&nbsp;");
		String url = zApiUrl.PUT_toItem(itemPath, bodyLines);
		return JHttp.GET_BODY(url, null, String.class, 200);

	}

	@SneakyThrows
	public String GET_items(ItemPath itemPath) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_toPage(itemPath);
		return JHttp.GET_BODY(url, null, String.class, 200);

	}

	@SneakyThrows
	public String GET_item(ItemPath itemPath) throws IllegalHttpStatusException {
		String url = zApiUrl.GET_toItem(itemPath);
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

		public String urlToPageWithItem(ItemPath itemPath, boolean isGetOrPut) {
			return urlToPageWithItem(itemPath, isGetOrPut, null);
		}

		public String urlToDirectPageWithItem(ItemPath itemPath) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + UP_API_PARTURL_ + pagePart + itemNamePart + query;
		}

		public String urlToPageWithItem(ItemPath itemPath, boolean isGetOrPut, String v) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String itemNamePart = itemPath.nameAsPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart(v);
			return http() + sdPart + domainNameWithPort + ctxPart() + UP_API_PARTURL_ + pagePart + opPart + itemNamePart + query;
		}

		public String urlToPage(ItemPath itemPath, boolean isGetOrPut) {
			String sdPart = itemPath.subdomainAsUrlPart();
			String pagePart = itemPath.pageAsUrlPart();
			String opPart = opSymbolPart(isGetOrPut);
			String query = queryPart();
			return http() + sdPart + domainNameWithPort + ctxPart() + UP_API_PARTURL_ + pagePart + opPart + query;
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
			return urlToPageWithItem(itemPath, true, v);
		}

		public String GET_toDirectItem(ItemPath itemPath) {
			return urlToDirectPageWithItem(itemPath);
		}

		public String GET_toItem(ItemPath itemPath) {
			return urlToPageWithItem(itemPath, true);
		}

		public String PUT_toItem(ItemPath itemPath, String v) {
			return urlToPageWithItem(itemPath, false, v);
		}

		public String GET_toPage(ItemPath itemPath) {
			return urlToPage(itemPath, true);
		}
	}
}
