package utl_web;

import lombok.SneakyThrows;
import mpc.X;
import mpc.arr.Arr;
import mpc.args.ARG;
import mpc.env.AP;
import mpc.exception.EmptyException;
import mpc.exception.IResponseStatusException;
import mpc.fs.UF;
import mpc.fs.UUrl;
import mpc.log.L;
import mpc.str.*;
import mpc.fs.query.QueryUrl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import utl_rest.URest;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static mpc.Sys.e;

public class UWeb {

	//https://stackoverflow.com/questions/2707374/java-servlets-how-do-i-detect-if-a-user-is-from-a-mobile-device
	public static boolean isMobile() {
		return isMobile(URest.getRequest());
	}

	public static boolean isMobile(final HttpServletRequest request) {
		// http://www.hand-interactive.com/m/resources/detect-mobile-java.htm
		final String userAgent = request.getHeader("User-Agent");
		final String httpAccept = request.getHeader("Accept");
		final UAgentInfo detector = new UAgentInfo(userAgent, httpAccept);
		return detector.detectMobileQuick();
	}

	public static String getHost(HttpServletRequest... request) {
		HttpServletRequest rq = ARG.isDef(request) ? ARG.toDef(request) : URest.getRequest();
		return rq.getHeader("Host");
	}

	public static String getHost(Boolean addHttps_OrHttp_OrNot, HttpServletRequest... request) {
		return addHttps_OrHttp_OrNot != null ? getHost(request) : (addHttps_OrHttp_OrNot ? "https://" : "http://") + getHost(request);
	}

	public static String getHost(Boolean addHttps_OrHttp_OrNot, String address, HttpServletRequest... request) {
		return getHost(addHttps_OrHttp_OrNot, request) + address;
	}

	public static Map<String, Object> getMapRequestAttributes(HttpServletRequest... request) {
		HttpServletRequest rq = ARG.isDef(request) ? ARG.toDef(request) : URest.getRequest();
		Map<String, Object> props = new HashMap();
		Enumeration<String> attributeNames = rq.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String vl = attributeNames.nextElement();
			props.put(vl, rq.getAttribute(vl));
		}
		return props;
	}

	public static Map<String, String> getMapRequestHeaderAttributes(HttpServletRequest... request) {
		HttpServletRequest rq = ARG.isDef(request) ? ARG.toDef(request) : URest.getRequest();
		Map<String, String> props = new HashMap();
		Enumeration<String> attributeNames = rq.getHeaderNames();
		while (attributeNames.hasMoreElements()) {
			String vl = attributeNames.nextElement();
			props.put(vl, rq.getHeader(vl));
		}
		return props;
	}

	public static Map<String, Set<String>> getMapRequestHeadersAttributes(HttpServletRequest... request) {
		HttpServletRequest rq = ARG.isDef(request) ? ARG.toDef(request) : URest.getRequest();
		Map<String, Set<String>> props = new HashMap();
		Enumeration<String> attributeNames = rq.getHeaderNames();
		while (attributeNames.hasMoreElements()) {
			String vl = attributeNames.nextElement();
			props.put(vl, Arr.toSet(rq.getHeaders(vl)));
		}
		return props;
	}

	public static String getPathFromReferer(HttpServletRequest request, String... defRq) {
		return UUrl.getPathFromUrl(request.getHeader("Referer"), defRq);
	}

	public static String getPathWithoutContext(HttpServletRequest request) {
		String u = request.getServletPath();
		if (u.startsWith("/")) {
			u = u.substring(1);
		}
		if (u.endsWith("/")) {
			u = u.substring(0, u.length() - 1);
		}
		return u;
	}

	public static String getSubDomian_part3(ServletRequest request, int sd3_index, String returnIfNull) {
		String[] ms = StringUtils.split(request.getServerName(), '.');
		return sd3_index < ms.length ? ms[(sd3_index + 1 - ms.length) * -1] : returnIfNull;
	}

	@Deprecated
	public static String getSubDomian_part3(ServletRequest request) {
		String[] ms = StringUtils.split(request.getServerName(), '.');
		String rslt = null;
		switch (ms.length) {
			case 0:
			case 1:
			case 2:
				return null;
			case 3:
				rslt = ms[0];
				break;
			case 4:
				rslt = ms[1];
				break;
			case 5:
				rslt = ms[2];
				break;
			case 6:
				rslt = ms[7];
				break;
			default:
				throw new UnsupportedOperationException("What is domain?" + request.getServerName());
		}
		if (ms.length >= 4 && STR.isLengthBetwenEq(ms[0], 1, 3) && UST.isLong(new Long[]{0L, 255L}, ms[0], ms[1], ms[2], ms[3])) {
			return ms.length > 4 ? ms[5] : null;
		}
		return rslt;
	}

	@SneakyThrows
	public static void responseImageFromRsrc(HttpServletResponse response, String fromRsrcDir, String fromPath) {
		String rsrc = UF.normFile(fromRsrcDir, fromPath);
		InputStream is = UWeb.class.getResourceAsStream(rsrc);
		responseContentType(response, ContentType.IMG_PNG, is);
	}

	@SneakyThrows
	public static void responseFromFileByExt(HttpServletResponse response, ContentType contentType, File file) {
		responseContentTypeFromFile(response, contentType, file);
	}

	@SneakyThrows
	public static void responseImageFromFile(HttpServletResponse response, File file) {
		responseContentTypeFromFile(response, ContentType.IMG_PNG, file);
	}

	@SneakyThrows
	public static void responseContentTypeFromFile(HttpServletResponse response, ContentType contentType, File file) {
		if (!file.isFile()) {
			UWeb.sendError404(response);
			return;
		}
		responseContentType(response, contentType, new FileInputStream(file));
	}

	@SneakyThrows
	public static void responseContentType(HttpServletResponse response, ContentType contentType, InputStream data) {
		try {
			responseContentType_(response, contentType, data);
		} catch (IOException e) {
			if (L.isErrorEnabled()) {
				L.error("responseContentType", e);
			}
			throw e;
		}
	}

	public static void responseContentType_(HttpServletResponse response, ContentType contentType, InputStream data) throws IOException {
		response.setHeader("Content-Type", contentType.mimeType);
		try {
			IOUtils.copy(data, response.getOutputStream());
		} finally {
			IOUtils.closeQuietly(data);
			response.flushBuffer();
		}
	}

	@SneakyThrows
	public static void responseContentTypeFromRsrc(HttpServletResponse response, ContentType contentType, String fromRsrcDir, String fromPath) {
		try {
			responseContentTypeFromRsrc_(response, contentType, fromRsrcDir, fromPath);
		} catch (EmptyException | IOException e) {
			if (L.isErrorEnabled()) {
				L.error(X.f("responseContentTypeFromRsrc '%s', '%s', '%s'", contentType, fromRsrcDir, fromPath), e);
				UWeb.sendError404(response);
			}
		}
	}

	public static void responseContentTypeFromRsrc_(HttpServletResponse response, ContentType contentType, String fromRsrcDir, String fromPath) throws IOException, EmptyException {
		response.setHeader("Content-Type", contentType.mimeType);
		//File path = new File(servletContext.getRealPath("/WEB-INF/includes/css/"));
		String rsrc = ".".equals(fromRsrcDir) ? UF.normUnixRootFile(fromPath) : UF.normUnixRootFile(fromRsrcDir, fromPath);
		InputStream is = UWeb.class.getResourceAsStream(rsrc);
		if (is == null) {
			throw new EmptyException(rsrc);
		}
		IOUtils.copy(is, response.getOutputStream());
		IOUtils.closeQuietly(is);
		response.flushBuffer();
	}

	public static Sb buildReportRequest_AsSingleLine(boolean headers, boolean attributes, int tabLevel, Logger... logger) {
		Sb sb = buildReportRequest(URest.getRequest(), headers, attributes, tabLevel, logger);
		return Sb.of(STR.nonl(sb.toString(), " "));
	}

	public static Sb buildReportRequest(boolean headers, boolean attributes, int tabLevel, Logger... logger) {
		return buildReportRequest(URest.getRequest(), headers, attributes, tabLevel, logger);
	}

	public static Sb buildReportRequest(HttpServletRequest request) {
		return buildReportRequest(request, true, true, 0, mpc.log.L.L);
	}

	public static Sb buildReportRequest(HttpServletRequest request, boolean headers, boolean attributes, int tabLevel, Logger... logger) {
		String TAB = STR.TAB(tabLevel);
		String TAB2 = STR.TAB(tabLevel + 1);
		String TAB3 = STR.TAB(tabLevel + 2);
		Sb sb = new Sb();
		sb.NL("ServletPath:" + request.getServletPath());
		sb.NL("ContextPath:" + request.getContextPath());
		if (headers) {
			Map<String, Set<String>> mapHeaders = getMapRequestHeadersAttributes(request);
			Sb hSb = Rt.buildReport(mapHeaders, "Headers", tabLevel);
			sb.append(hSb);
		}
		if (attributes) {
			Map<String, Object> mapAttributes = getMapRequestAttributes(request);
			Sb hSb = Rt.buildReport(mapAttributes, "Attributes", tabLevel);
			sb.append(hSb);
		}
		if (ARG.isDef(logger)) {
			ARG.toDef(logger).info(sb.toString());
		}
		return sb;
	}

	public static Cookie getCookie(HttpServletRequest request, String name, Cookie... defRq) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(name)) {
					return c;
				}
			}
		}
		return ARG.toDefRq(defRq);
	}

	public static <T> T getCookieValueAs(HttpServletRequest request, String key, Class<T> asType, T... defRq) {
		String cookie = getCookieValue(request, key, null);
		if (cookie != null) {
			return UST.strTo(cookie, asType, defRq);
		}
		return ARG.toDefRq(defRq);
	}

	public static String getCookieValue(HttpServletRequest request, String key, String... defRq) {
		Cookie cookie = getCookie(request, key, null);
		if (cookie != null) {
			return cookie.getValue();
		}
		return ARG.toDefRq(defRq);
	}

	public static Cookie setCookie(HttpServletResponse response, String name, Object value) {
		Cookie userCookie = new Cookie(name, value == null ? null : value.toString());
		response.addCookie(userCookie);
		return userCookie;
	}

	public static Void sendError404(HttpServletResponse response) throws IOException {
		return sendErrorWithCode(response, HttpServletResponse.SC_NOT_FOUND, null);
	}

	public static Void sendError404(HttpServletResponse response, String msg, Object... args) throws IOException {
		return sendErrorWithCode(response, HttpServletResponse.SC_NOT_FOUND, msg, args);
	}

	public static Void sendError400(HttpServletResponse response, String msg, Object... args) throws IOException {
		return sendErrorWithCode(response, HttpServletResponse.SC_BAD_REQUEST, msg, args);
	}

	public static Void sendError500(HttpServletResponse response, String msg, Object... args) throws IOException {
		return sendErrorWithCode(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg, args);
	}

	public static Void sendError403(HttpServletResponse response, String msg, Object... args) throws IOException {
		return sendErrorWithCode(response, HttpServletResponse.SC_FORBIDDEN, msg, args);
	}

	public static Void sendMsg(HttpServletResponse response, int code, String msg, Object... args) throws IOException {
		msg = msg == null ? null : X.f(msg, args);
		response.setStatus(code);
		response.getWriter().println(msg);
		return null;
	}

	public static Void sendErrorWithCode(HttpServletResponse response, int code, String msg, Object... args) throws IOException {
		if (msg != null) {
			response.sendError(code, X.f(msg, args));
		} else {
			response.sendError(code);
		}
		return null;
	}

	public static String getQueryStringForwarded(HttpServletRequest request) {
		Object args = request.getAttribute("javax.servlet.forward.query_string");
		return args == null ? null : args.toString();
	}

	public static QueryUrl getQueryStringForwardedTyped(HttpServletRequest request) {
		return new QueryUrl(getQueryStringForwarded(request));
	}

	public static QueryUrl getQueryStringTyped(HttpServletRequest request) {
		return new QueryUrl(getQueryString(request));
	}

	public static String getQueryString(HttpServletRequest request) {
		return request.getQueryString();
	}

	public static String getHostWoSd() {//unsafe - what is host without not sd3?
		return USToken.lastGreedy(getHost(), ".");
	}

	public static String getBodyFromRequest(HttpServletRequest request) throws IOException {
		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				//stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}
		body = stringBuilder.toString();
		return body;
	}


	//https://stackoverflow.com/questions/16558869/getting-ip-address-of-client
	public static String getClientIpAddress(HttpServletRequest request) {
		String xForwardedForHeader = request.getHeader("X-Forwarded-For");
		if (xForwardedForHeader == null) {
			return request.getRemoteAddr();
		} else {
			// As of https://en.wikipedia.org/wiki/X-Forwarded-For
			// The general format of the field is: X-Forwarded-For: client, proxy1, proxy2 ...
			// we only want the client
			//return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
			return USToken.first(xForwardedForHeader, ',', xForwardedForHeader).trim();
		}
	}

	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	public static HttpServletResponse getResponse() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	}

	public static Authentication getAuth(Authentication... defRq) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return authentication;
		}
		return ARG.toDefThrow("Auth is null", defRq);
	}

	public static Object getAuthPrincipal(Authentication... defRq) {
		Object principal = getAuth().getPrincipal();
		if (principal != null) {
			return principal;
		}
		return ARG.toDefThrow("AuthPrincipal is null", defRq);
	}

	public static void sendResponse(HttpServletResponse response, IResponseStatusException sex) throws IOException {
		if (sex.isOk()) {
			UWeb.sendMsg(response, sex.code(), sex.getMessage());
		} else {
			UWeb.sendErrorWithCode(response, sex.code(), sex.getMessage());
		}
	}

	public static String appendPortToHostWithPath(String hostWithPath, boolean... mayBeWoHttp) {
		int sbstr = -1;
		if (ARG.isDefEqTrue(mayBeWoHttp)) {
			if (!UUrl.hasPfxHttpOrHttps(hostWithPath)) {
				sbstr = UUrl.PFX_HTTP.length();
				hostWithPath = UUrl.PFX_HTTP + hostWithPath;
			}
		}
		String[] hostAndPath = UUrl.getHostAndPath(hostWithPath);
		hostAndPath[0] = appendPortToHostWoPath(hostAndPath[0]);
		String url = UUrl.normUrlParts(hostAndPath);
		return sbstr <= 0 ? url : url.substring(sbstr);
	}

	public static String appendPortToHostWoPath(String hostWithoutPath) {
		//on prod machine host has port, on local machine - not (feature nginx?)
		//fix it
		if (USToken.last(hostWithoutPath, ':', Integer.class, null) != null) {
			return hostWithoutPath; //host already has port
		}
		Integer portOrNullIf80 = getPortOrNullIf80();
		return portOrNullIf80 == null ? hostWithoutPath : (hostWithoutPath + ":" + portOrNullIf80);
	}

	private static Integer getPortOrNullIf80() {
		String portStr = AP.get("server.port", null);
		if (portStr == null) {
			return null;
		}
		portStr = AP.getValueWoDef(portStr);
		return UST.INT(portStr);
	}

	@SneakyThrows
	public static void sendResponseWithCleanDataAndClose(HttpServletResponse response, String data) {
		response.setStatus(200);
		response.getWriter().write(data);
		response.getWriter().close();
	}

	@SneakyThrows
	public static void sendResponse500AndClose(HttpServletResponse response, String data) {
		response.setStatus(500);
		response.getWriter().write(data);
		response.getWriter().close();
	}
}
