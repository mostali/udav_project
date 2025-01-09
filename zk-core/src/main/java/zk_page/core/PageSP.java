package zk_page.core;


import mpc.map.MapTableContract;
import mpf.contract.IContract;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpc.exception.FIllegalArgumentException;
import mpc.rfl.RFL;
import mpu.func.Function2T;
import mpc.str.sym.SYMJ;
import mpu.str.SPLIT;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Html;
import org.zkoss.zul.Window;
import utl_web.URsp;
import zk_com.base_ctr.Menupopup0;
import zk_com.sun_editor.IPerPage;
import zk_form.events.IBoolEvent;
import zk_form.head.StdHeadLib;
import zk_old_core.oldstate.WithAgna;
import zk_form.WithLogo;
import zk_old_core.oldstate.WithUsrLogo;
import zk_old_core.coms.AgnaCom;
import zk_os.AppZosProps;
import zk_page.events.ZKEvents;
import zk_os.db.net.WebUsr;
import zk_os.sec.MatrixAccess;
import zk_os.sec.Sec;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.ZkPage;
import zk_notes.apiv1.NodeApiCallType;
import zk_page.node_state.FormState;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

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

				initAndAdd_LogoCom();

				initAndAdd_AdgnaCom();
			}

			NodeApiCallType.checkRestCall(spVM().ppiq());

			buildPageImpl();

			if (!cleanResponse) {
				ZkPage.renderHeadPageAndForms(this);
			}

			ZkPage.renderHeadRsrcs(window, StdHeadLib.PAGE_JS);

//			{
//				Boolean enableMoveCom = (Boolean) SpVM.get().pageState().getAs("enable.move-com", Boolean.class, false);
//				if (enableMoveCom) {
//					IBoolEvent.initNewAndAppend(window);
//				}
//			}

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

			menu.add_______();

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

	protected MatrixAccess getMA() {
		return null;
	}

	public abstract void buildPageImpl();

	boolean cleanResponse = false;

	public PageSP cleanResponse(boolean... cleanResponse) {
		this.cleanResponse = ARG.isDefEqFalse(cleanResponse);
		return this;
	}

	public static class BoolCom extends Html implements IBoolEvent<BoolCom> {


		public BoolCom() {
		}

		public BoolCom(String content) {
			super(content);
		}

		@Override
		public void onEvent(Event event) throws Exception {
			Map<String, Object> data = (Map) event.getData();
			Coor coor = Coor.of(data);
			Sys.p(coor.mapc());

			doAlignPageGrid();
//			NotesSpace.rerenderFirst();
		}

		public static void doAlignPageGrid() {
			Map<int[], FormState> comsCoors = SpVM.get().getAllFormComStatesAsGrid();
			ArrayList<int[]> comsGrid = new ArrayList<>(comsCoors.keySet());
//			if (false) {
//				List<int[]> closestComponents = ComponentFinder.findClosestComponents(comsGrid, new int[]{coor.getX(), coor.getY()}, 50);
//				for (int[] closestComponent : closestComponents) {
//					int px = (int) (coor.getDuration() / 20);
//					FormState formState = comsCoors.get(closestComponent);
//					formState.updatePropSingle(CN.TOP, closestComponent[0] + px + "px");
//					formState.updatePropSingle(CN.LEFT, closestComponent[1] + px + "px");
//
//				}
//			}
			int[] gridParams = getGrid(AppZosProps.AUTO_GRID_PX.getValueOrDefault());

			TransformerPageCollections.GridAligner.alignToGrid(comsGrid, gridParams[0]);

			TransformerPageCollections.OffsetAdder.addOffset(comsGrid, new int[]{gridParams[1], gridParams[2]});

			comsGrid.forEach(com -> comsCoors.get(com).comState().updatePropSingle_TopLeft(com));
		}

		public static int[] getGrid(String pat) {
			String[] grid = SPLIT.argsByComma(pat);
			return new int[]{UST.INT(grid[0]), UST.INT(grid[1]), UST.INT(grid[2])};
		}

		@Override
		public void onPageAttached(Page newpage, Page oldpage) {
			super.onPageAttached(newpage, oldpage);
			addEventListener("onBool", this);
		}

		public interface Coor extends IContract {
			int getX();

			int getY();

			int getX2();

			int getY2();

			long getDuration();

			String getDirection();

			public static Coor of(Map data) {
				return MapTableContract.buildContract_MarkNotRq(data, Coor.class);
			}

			default String toStringSimple() {
//				return X.f("%s_%s_%s <<< %s_%s <<<%s", getNid(), getName(), getCreated());
				return mapc().toString();
			}
		}
	}


}
