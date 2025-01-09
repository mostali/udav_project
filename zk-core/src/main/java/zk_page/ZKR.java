package zk_page;

import lombok.SneakyThrows;
import mpc.fs.fd.RES;
import mpc.fs.query.QueryUrl;
import mpc.net.CON;
import mpu.IT;
import mpu.core.ARG;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import utl_web.URsp;
import utl_web.UWeb;
import zk_form.events.RedirectHrefEvent;
import zk_os.AppZosConfig;
import zk_page.core.PagePathInfoWithQuery;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

//REQUEST
public class ZKR {

	@SneakyThrows
	public static void download(Path file) {
		Filedownload.save(IT.isFileExist(file.toFile()), null);
	}

	public static void redirectToLocation(String location, boolean blank) {
		if (blank) {
			Clients.evalJavaScript(RedirectHrefEvent.getJavaScript_WO_BLANK(location));
		} else {
			Clients.evalJavaScript(RedirectHrefEvent.getJavaScript_Location(location));
		}
	}

	public static void redirectToPage(String address, boolean... newTab) {
		//				Executions.sendRedirect("/" + getModelSP().getFromPath());

		if (ARG.isDefEqTrue(newTab)) {
			Clients.evalJavaScript("window.open(\"" + address + "\",'_blank');");
//			Executions.getCurrent().sendRedirect(address, "_blank");
		} else {
			Executions.sendRedirect(address);
		}
	}

	public static void restartPage() {
		//				Executions.sendRedirect("/" + getModelSP().getFromPath());
		Executions.sendRedirect("");
//				getWindow().invalidate();
//				ZKC.removeMeReturnParent()
	}

	public static Void sendError404() throws IOException {
		return sendError404(null);
	}

	public static Void sendError404(String msg) throws IOException {
		return URsp.sendError404(getResponse(), msg);
	}

	public static Void sendError500(Throwable err) throws IOException {
		if (AppZosConfig.IS_DEBUG) {
			return URsp.sendError500(getResponse(), err.getMessage());
		}
		return URsp.sendError500(getResponse(), null);
	}

	public static HttpServletResponse getResponse() {
		return (HttpServletResponse) Executions.getCurrent().getNativeResponse();
	}

	public static void evalJavaScriptRsrc(String path) throws IOException {
		evalJavaScript(RES.readString(path));
	}

	public static void evalJavaScript(String js) {
		Clients.evalJavaScript(js);
	}

	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) Executions.getCurrent().getNativeRequest();
	}

	public static Session getSession() {
		return SessionsCtrl.getCurrent();
	}

	public static Map<String, Object> getSessionAttrs() {
		return getSession().getAttributes();
	}

	public static QueryUrl getRequestQuery() {
		return UWeb.getQueryStringForwardedTyped(getRequest());
	}

	public static String getRequestQueryParamAsStr(String key, String... defRq) {
		return getRequestQuery().getFirstAsStr(key, defRq);
	}

	public static PagePathInfoWithQuery getPagePathInfoWithQuery() {
		return PagePathInfoWithQuery.current();
	}

	public static String getCookieValue(String key, String... defRq) {
		return UWeb.getCookieValue(getRequest(), key, defRq);
	}

	public static <T> T getCookieValueAs(String key, Class<T> asType, T... defRq) {
		return UWeb.getCookieValueAs(getRequest(), key, asType, defRq);
	}

	public static Cookie getCookie(String key, Cookie... defRq) {
		return UWeb.getCookie(getRequest(), key, defRq);
	}

	public static void setCookie(String key, Object value, boolean onlyWithHttps) {
		UWeb.setCookie(getResponse(), key, value, onlyWithHttps);
	}

	public static void deleteCookie(String key) {
		UWeb.deleteCookie(getResponse(), key);
	}

	public static CON.Method getRequestMethod(CON.Method... defRq) {
		return CON.Method.valueOf(getRequest().getMethod(), null);
	}
}
