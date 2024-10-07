package zk_page;

import lombok.SneakyThrows;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpc.fs.fd.RES;
import mpc.log.L;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;
import mpc.fs.query.QueryUrl;
import utl_web.UWeb;
import zk_form.events.RedirectHrefEvent;
import zk_pages.GenericPageSP;
import zk_old_core.old.fswin.FsWin;
import zk_os.AppZosConfig;
import zk_old_core.old.mwin.MWin;
import zk_form.control.QuickCmdRunner;
import zk_old_core.control_old.TopAdminMenu;
import zk_page.core.PagePathInfoWithQuery;
import zk_old_core.GenericViewPageComponent;
import zk_old_core.mdl.PageDirModel;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

//REQUEST
public class ZKR {

	@SneakyThrows
	public static void download(Path file) {
		Filedownload.save(IT.isFileExist(file.toFile()), null);
	}

	public static void activePush(boolean... enable) {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (!desktop.isServerPushEnabled()) {
			desktop.enableServerPush(ARG.isDefNotEqFalse(enable));
		}
	}

	@SneakyThrows
	public static void activePushCom(Desktop desktop) {
		Executions.activate(desktop);
	}

	@SneakyThrows
	public static void deactivePushCom(Desktop desktop) {
		Executions.deactivate(desktop);
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
		return UWeb.sendError404(getResponse(), msg);
	}

	public static Void sendError500(Throwable err) throws IOException {
		if (AppZosConfig.IS_DEBUG) {
			return UWeb.sendError500(getResponse(), err.getMessage());
		}
		return UWeb.sendError500(getResponse(), null);
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

	public static String getCookieValue(String key, String... defRq) {
		return UWeb.getCookieValue(getRequest(), key, defRq);
	}

	public static <T> T getCookieValueAs(String key, Class<T> asType, T... defRq) {
		return UWeb.getCookieValueAs(getRequest(), key, asType, defRq);
	}

	public static Cookie getCookie(String key, Cookie... defRq) {
		return UWeb.getCookie(getRequest(), key, defRq);
	}

	public static void setCookie(String key, Object value) {
		UWeb.setCookie(getResponse(), key, value);
	}

	public static void invalidatePage() {
		ZKC.getFirstPage().invalidate();
		ZKCF.rootsByClass(GenericViewPageComponent.class, true).get(0).invalidate();
	}

	@SneakyThrows
	public static void rebuildPage() {
		rebuildPage(PageDirModel.get());
	}

	@SneakyThrows
	public static void rebuildPage(PageDirModel pageDirModel) {
		try {

			Page page = ZKC.getFirstPage();
//		ZKComFinder.findAll(page, Window.class, false).get(0);
			Window window = ZKC.getFirstWindow();

			if (false) {

				do {
					List<Component> children = window.getChildren();
					if (X.empty(children)) {
						break;
					}
					ZKC.removeMeReturnParent(children.get(0));

				} while (true);
				//Iterator<Component> iterator = window.getChildren().iterator();
				//while (iterator.hasNext()) {
				//iterator.remove();
				//ZKC.removeMeReturnParent(iterator.next());
				//}

			} else {
				ZKCF.findAll(page, GenericViewPageComponent.class, true, Collections.EMPTY_LIST).forEach(c -> ZKC.removeMeReturnParent((Component) c));

				//		ZKComFinder.findCom(page, MWin.class, false).forEach(c -> ZKC.newParent(c, window));
				MWin.findAll(MWin.class, Collections.EMPTY_LIST).forEach(c -> ZKC.removeMeReturnParent((Component) c));
				FsWin.findAll(FsWin.class, Collections.EMPTY_LIST).forEach(c -> ZKC.removeMeReturnParent((Component) c));
//				SeWin.findAll(Collections.EMPTY_LIST).forEach(c -> ZKC.removeMeReturnParent((Component) c));

				ZKCF.findAll(page, TopAdminMenu.class, true, Collections.EMPTY_LIST).forEach(c -> ZKC.removeMeReturnParent((Component) c));
				ZKCF.findAll(page, QuickCmdRunner.class, true, Collections.EMPTY_LIST).forEach(c -> ZKC.removeMeReturnParent((Component) c));
			}

			GenericPageSP.buildPage(window, pageDirModel, true);
		} catch (Exception ex) {
			if (L.isErrorEnabled()) {
				L.error("Rebuild Page", ex);
			}
			throw ex;
		}
	}


	public static void sendError400(String setKey) throws IOException {
		UWeb.sendError400(getResponse(), setKey);
	}


}
