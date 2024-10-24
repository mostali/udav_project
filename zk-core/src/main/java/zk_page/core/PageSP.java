package zk_page.core;


import mpu.X;
import mpu.core.ARG;
import mpc.exception.FIllegalArgumentException;
import mpc.rfl.RFL;
import mpu.func.Function2T;
import mpc.str.sym.SYMJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Window;
import utl_web.URsp;
import zk_com.base_ctr.Menupopup0;
import zk_com.sun_editor.IPerPage;
import zk_old_core.old.WithAgna;
import zk_form.WithLogo;
import zk_old_core.old.WithUsrLogo;
import zk_old_core.control_old.AgnaCom;
import zk_page.events.ZKEvents;
import zk_os.db.net.WebUsr;
import zk_os.sec.MatrixAccess;
import zk_os.sec.Sec;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.ZkPage;
import zk_notes.apiv1.NodeApiCallType;

import java.io.IOException;
import java.io.Serializable;

public abstract class PageSP implements Serializable {

	public static final Logger L = LoggerFactory.getLogger(PageSP.class);
	public static final Function2T<Class<? extends PageSP>, SpVM, PageSP, Exception> PSP_CLASS_PAGE_BUILDER = (Class<? extends PageSP> pg, SpVM spVM) -> pg.getDeclaredConstructor(Window.class, SpVM.class).newInstance(ZKC.getFirstWindow(), spVM);
	public static final String PAGENAME_LOGS = "@@logs";

	protected final Window window;
	private final SpVM spVM;
	private SpVM __spVM;

	public static void checkClassPageSP(Class page) {
		if (!PageSP.class.isAssignableFrom(page)) {
			throw new FIllegalArgumentException("Except class of type '%s', but it '%s'", PageSP.class, page);
		}
	}

	public Window window() {
		return window;
	}

	public SpVM spVM() {
		return spVM != null ? spVM : (__spVM != null ? __spVM : (__spVM = SpVM.get()));
	}

	private PagePathInfoWithQuery pagePathInfoWithQuery;

	public PagePathInfoWithQuery ppiq() {
		return pagePathInfoWithQuery != null ? pagePathInfoWithQuery : (pagePathInfoWithQuery = PagePathInfoWithQuery.current());
	}

	public PageSP(Window window, SpVM spVM) {
		this.window = window;
		this.spVM = spVM;
	}

	public PageSP buildPage() {

		Throwable exRslt = null;

		spVM().checkAndApplyAuthBySuperKey();

		checkRoles();

		try {

			if (!cleanResponse) {

				checkIsAllowedOr403_forPerPage();

				initAndAdd_LogoCom();

				initAndAdd_AdgnaCom();
			}

			NodeApiCallType.checkRestCall(spVM().ppi());

			buildPageImpl();

			if (!cleanResponse) {
				ZkPage.renderHeadPageAndForms(this);
			}

		} catch (Throwable ex) {
			exRslt = ex;
		}


		if (exRslt == null) {
			L.info(X.f("build PageSP SUCCESS '%s' ", spVM().ppi()));
			return this;
		}

		try {
			URsp.sendResponseAsException(ZKR.getResponse(), exRslt);
		} catch (IOException e) {
			L.error("sendResponseAsException", e);
//			throw new RuntimeException(e);
		}

		return this;
	}


	public PageRoute ano(PageRoute... defRq) {
		return RFL.getAno(getClass(), PageRoute.class, defRq);
	}

	protected void checkRoles() {
		MatrixAccess ma = getMA();

		if (ma == null) {
			return;
		}
		ma.hasAccess();
	}

	protected void initAndAdd_LogoCom() {
		if (!(this instanceof WithLogo)) {
			return;
		}
		WithLogo.LogoCom logoCom = ((WithLogo) this).getLogoOrCreate();
		window.appendChild(logoCom);

		Menupopup0 menu = logoCom.getContextMenu();

		if (this instanceof WithUsrLogo) {

			menu.addSeparator();

			WebUsr user = Sec.getUser();
			boolean isAnonim = Sec.isAnonim();
			if (isAnonim) {
				menu.addMenuitem(SYMJ.USER + " Login", ZKEvents.getEventLogin());
			} else {
				String name = user.getUserName(5) + " ( " + user.getFID().toString() + " )";
				menu.addMenuitem(SYMJ.USER + " " + name, ZKEvents.getEventOpenUserSettings());
				menu.addMenuitem(SYMJ.LOGOUT + " Logout", ZKEvents.getEventLogout());
			}

		}
	}

	protected void initAndAdd_AdgnaCom() {
		if (!(this instanceof WithAgna)) {
			return;
		}
		AgnaCom agnaCom = ((WithAgna) this).getAgnaOrCreate();
		window.appendChild(agnaCom);
	}

	@Deprecated
	protected void checkIsAllowedOr403_forPerPage() {
		if (this instanceof IPerPage) {
			Sec.checkIsAllowedOr403(((IPerPage) this).getPageRoute(), getMA());
		}
	}

	protected MatrixAccess getMA() {
		return null;
	}

	public abstract void buildPageImpl();

	boolean cleanResponse = false;

	public PageSP cleanResponse(boolean... cleanResponse) {
		this.cleanResponse = ARG.isDefEqFalse(cleanResponse);
		return this;
	}

}
