package utl_web;

import mpc.net.CON;
import mpu.core.ARR;
import mpu.core.ARG;
import mpc.url.UUrl;
import mpc.net.query.QueryUrl;
import mpu.str.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import utl_rest.URest;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

import static mpu.Sys.e;

public class UWeb {

	public static final Logger L = LoggerFactory.getLogger(UWeb.class);

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
		return addHttps_OrHttp_OrNot != null ? getHost(request) : (CON.HTTPS(true)) + getHost(request);
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
			props.put(vl, ARR.toSet(rq.getHeaders(vl)));
		}
		return props;
	}

	public static String getPathFromReferer(HttpServletRequest request, String... defRq) {
		return UUrl.getPathFromUrl(request.getHeader("Referer"), defRq);
	}

	public static String getServletPathWithoutContext(HttpServletRequest request) {
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

	public static Sb buildReportRequest_AsSingleLine(boolean headers, boolean attributes, int tabLevel, Logger... logger) {
		Sb sb = buildReportRequest(URest.getRequest(), headers, attributes, tabLevel, logger);
		return Sb.of(STR.noNL(sb.toString(), " "));
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

	@Deprecated //run only from sd3
	public static String getHostWoSd(boolean... alreadyHere) {//unsafe - what is host without not sd3?
		return ARG.isDefEqTrue(alreadyHere) ? getHost() : TKN.lastGreedy(getHost(), ".");
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
			return TKN.first(xForwardedForHeader, ',', xForwardedForHeader).trim();
		}
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

	public static Sb buildSimpleReportRequest(boolean... oneLine) {
		return ARG.isDefEqTrue(oneLine) ? buildReportRequest_AsSingleLine(true, true, 0) : buildReportRequest(true, true, 0);
	}

}
