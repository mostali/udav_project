package zk_page;

import lombok.SneakyThrows;
import mpc.env.AP;
import mpc.env.APP;
import mpc.fs.fd.RES;
import mpc.net.query.QueryUrl;
import mpc.net.CON;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.func.FunctionV1;
import mpu.func.FunctionV2;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import utl_web.URsp;
import utl_web.UWeb;
import zk_form.events.RedirectHrefEvent;
import zk_os.AppZosConfig;
import zk_page.core.PagePathInfoWithQuery;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

//REQUEST
public class ZKR {

	public static FunctionV2<String, String> toRedirectWithPalenAndPath = (plane, path) -> ZKR.redirectToPage(APP.HOST.getAppUrlWithPlaneAndPath(plane, path));
	public static FunctionV1<String> toRedirectWithPath = (path) -> ZKR.redirectToPage(APP.HOST.getAppUrlWithPath(path));
	public static FunctionV1<String> toWindowByUrl = (urlTo) -> ZKR.openWindow800_1200(urlTo);

	public static boolean isInEventListener() {
		return Events.inEventListener();
	}

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

	public static void redirectToHome( boolean... newTab) {
		ZKR.redirectToPage("/", newTab);
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
		if (APP.IS_DEBUG_ENABLE) {
			X.throwException(err);
//			return URsp.sendError500(getResponse(), err.getMessage());
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

	public static QueryUrl getRequestQuery() {
		return UWeb.getQueryStringForwardedTyped(getRequest());
	}

	public static String getRequestQueryParamAsStr(String key, String... defRq) {
		return getRequestQuery().getFirstAsStr(key, defRq);
	}

	public static PagePathInfoWithQuery getPagePathInfoWithQuery() {
		return PagePathInfoWithQuery.current();
	}

	public static CON.Method getRequestMethod(CON.Method... defRq) {
		return CON.Method.valueOf(getRequest().getMethod(), null);
	}

	public static void openWindow800_1200(String url) {
		ZKJS.openWindow(url, "800", "1200", "100","80",true, true, true);
	}

	public static String getClientIpAddress() {
		return UWeb.getClientIpAddress(getRequest());
	}

	public static String getPlaneFromRequest() {
		Execution execution = Executions.getCurrent();
		HttpServletRequest servletRequest = (HttpServletRequest) execution.getNativeRequest();
		return getPlaneFromRequest(servletRequest);
	}

	public static String getPlaneFromRequest(HttpServletRequest servletRequest) {
		return UWeb.getSubDomian_part3(servletRequest, AppZosConfig.SD3_INDEX, "");
	}

	public static int getCookieAuthTimeout() {
		return AP.getAs("web.session.timeout.bycookie.sec", Integer.class, (int) TimeUnit.DAYS.toSeconds(30));
	}
}
