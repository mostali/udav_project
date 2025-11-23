package zk_page.core;


import mpu.X;
import mpu.core.ARG;
import mpc.exception.FIllegalArgumentException;
import mpc.rfl.RFL;
import mpu.func.Function2T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Window;
import utl_web.URsp;
import zk_form.head.StdHeadLib;
import zk_form.WithLogo;
import zk_os.core.Sdn;
import zk_page.with_com.WithAgna;
import zk_page.with_com.WithBread;
import zk_page.with_com.WithMainTbx;
import zk_rmm.AgnaCom;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.ZKPage;
import zk_notes.apiv1.NodeApiCallType;

import java.io.Serializable;

public abstract class PageSP implements Serializable {

	public static final Logger L = LoggerFactory.getLogger(PageSP.class);

	public static final Function2T<Class<? extends PageSP>, SpVM, PageSP, Exception> PSP_CLASS_PAGE_BUILDER = (Class<? extends PageSP> pg, SpVM spVM) -> {
		return pg.getDeclaredConstructor(Window.class, SpVM.class).newInstance(ZKC.getFirstWindow(), spVM);
	};
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

		SpVM spVM = spVM();

		spVM.applyAuth_BySKA();

		try {

			NodeApiCallType.checkRestCall(spVM.ppiq());

			if (!cleanResponse) {
				initWithComs(spVM.sdn());
			}

			buildPageImpl();

			if (!cleanResponse) {
				ZKPage.renderHeadPageAndForms(this);
				ZKPage.renderHeadRsrcs(window, StdHeadLib.PAGE_JS);
			}

		} catch (Throwable ex) {
			exRslt = ex;
		}


		if (exRslt == null) {
			L.info(X.f("build PageSP SUCCESS '%s' ", spVM.ppi()));
			return this;
		}

//		try {
		URsp.sendResponseAsException(ZKR.getResponse(), exRslt);
//		} catch (IOException e) {
//			L.error("sendResponseAsException", e);
//			throw new RuntimeException(e);
//		}

		return this;
	}

	private void initWithComs(Sdn sdn) {
		if (this instanceof WithBread) {
			((WithBread) this).getBreadOrAdd(sdn, window);
		}


		if (this instanceof WithMainTbx) {
			((WithMainTbx) this).getMainTbxOrAdd(window);
		}

		if (this instanceof WithMainTbx) {
			((WithMainTbx) this).getMainTbxOrAdd(window);
		}

//		if (this instanceof WithBHP && getPa) {
//			((WithBHP) this).getWithBottomHistoryPanelOrAdd(window);
//		}


//				if (this instanceof WithSearch) {
//					if (Sec.isAdminOrOwner()) {
//						((WithSearch) this).getSearchBandboxOrAdd(window);
//					}
//				}

		if (this instanceof WithLogo) {
			((WithLogo) this).getLogoOrAdd(window);
		}

		initAndAdd_AdgnaCom();
	}


	public PageRoute ano(PageRoute... defRq) {
		return RFL.getClassAnnotation(getClass(), PageRoute.class, defRq);
	}

	protected void initAndAdd_AdgnaCom() {
		if (!(this instanceof WithAgna)) {
			return;
		}
		AgnaCom agnaCom = ((WithAgna) this).getAgnaOrCreate();
		window.appendChild(agnaCom);
	}

	public abstract void buildPageImpl();

	boolean cleanResponse = false;

	public PageSP cleanResponse(boolean... cleanResponse) {
		this.cleanResponse = ARG.isDefEqFalse(cleanResponse);
		return this;
	}

}
