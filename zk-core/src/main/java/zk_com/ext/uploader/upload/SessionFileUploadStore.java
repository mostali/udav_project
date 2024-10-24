package zk_com.ext.uploader.upload;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.zkoss.lang.Generics.cast;

/**
 * @author pobedenniy.alexey
 * @since 30.11.2014
 */
public class SessionFileUploadStore implements FileUploadStore {
	protected static final Logger log = LoggerFactory.getLogger(SessionFileUploadStore.class);

	public void put() {

	}

	@Override
	public Integer getMaxFileSize(String uploadComponentId, String desktopId, WebApp webApp, HttpSession httpSession) {
		Session zkSession = SessionsCtrl.getSession(webApp, httpSession);
		if (zkSession != null) {
			if (zkSession instanceof SessionCtrl) {
				Desktop desktop = ((WebAppCtrl) webApp).getDesktopCache(zkSession).getDesktop(desktopId);
				if (desktop != null) {
					return cast(desktop.getAttribute(FileUploadSpecification.MAX_FILE_SIZE_PREFIX + uploadComponentId));
				}
			}
		}
		return null;
	}


	@Override
	public void put(String desktopId, String contentId, ServletContext servletContext, HttpSession httpSession, List<FileItem> items) {
		WebApp webApp = WebManager.getWebApp(servletContext);
		Session zkSession = SessionsCtrl.getSession(webApp, httpSession);
		if (zkSession != null) {
			if (zkSession instanceof SessionCtrl) {
				Desktop desktop = ((WebAppCtrl) webApp).getDesktopCache(zkSession).getDesktop(desktopId);
				if (desktop != null) {
					desktop.setAttribute(contentId, items);
				}
			}
		}
	}

	@Override
	public void setMaxFileSize(String uploadComponentId, String desktopId, WebApp webApp, Object nativeSession, Integer maxFileSize) {
		Session zkSession = SessionsCtrl.getCurrent();
		if (zkSession != null) {
			if (zkSession instanceof SessionCtrl) {
				Desktop desktop = ((WebAppCtrl) webApp).getDesktopCache(zkSession).getDesktop(desktopId);
				if (desktop != null) {
					desktop.setAttribute(FileUploadSpecification.MAX_FILE_SIZE_PREFIX + uploadComponentId, maxFileSize);
				}
			}
		}
	}

	@Override
	public void remove(String desktopId, String contenId, WebApp webApp, Object nativeSession) {
		Session zkSession = SessionsCtrl.getCurrent();
		if (zkSession != null) {
			if (zkSession instanceof SessionCtrl) {
				Desktop desktop = ((WebAppCtrl) webApp).getDesktopCache(zkSession).getDesktop(desktopId);
				if (desktop != null) {
					desktop.removeAttribute(contenId);
				}
			}
		}
	}

	@Override
	public List<FileItem> get(String desktopId, String contenId, WebApp webApp, Object nativeSession) {
		Session zkSession = SessionsCtrl.getCurrent();
		if (zkSession != null) {
			if (zkSession instanceof SessionCtrl) {
				Desktop desktop = ((WebAppCtrl) webApp).getDesktopCache(zkSession).getDesktop(desktopId);
				if (desktop != null) {
					return cast(desktop.getAttribute(contenId));
				}
			}
		}
		return null;
	}
}
