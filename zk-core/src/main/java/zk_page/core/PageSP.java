package zk_page.core;


import mpc.map.MapTableContract;
import mpf.contract.IContract;
import mpu.X;
import mpu.core.ARG;
import mpc.exception.FIllegalArgumentException;
import mpc.rfl.RFL;
import mpu.func.Function2T;
import mpu.str.SPLIT;
import mpu.str.UST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Html;
import org.zkoss.zul.Window;
import utl_web.URsp;
import zk_form.events.IBoolEvent;
import zk_form.head.StdHeadLib;
import zk_form.WithLogo;
import zk_os.sec.Sec;
import zk_rmm.AgnaCom;
import zk_os.AppZosProps;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.ZKPage;
import zk_notes.apiv1.NodeApiCallType;
import zk_notes.node_state.FormState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

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
		int[] gridParams = BoolEvent.getGrid(AppZosProps.AUTO_GRID_PX.getValueOrDefault());

		TransformerPageCollections.GridAligner.alignToGrid(comsGrid, gridParams[0]);

		TransformerPageCollections.OffsetAdder.addOffset(comsGrid, new int[]{gridParams[1], gridParams[2]});

		comsGrid.forEach(comCoor -> comsCoors.get(comCoor).stateCom().fields().set_TOP_LEFT(comCoor));
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

//		checkRoles();

		try {

			if (!cleanResponse) {

				if (this instanceof WithMainTbx) {
					((WithMainTbx) this).getMainTbxOrAdd(window);
				}

				if (this instanceof WithSearch) {
					if (Sec.isAdminOrOwner()) {
						((WithSearch) this).getSearchBandboxOrAdd(window);
					}
				}

				if (this instanceof WithLogo) {
					((WithLogo) this).getLogoOrAdd(window);
				}

				initAndAdd_AdgnaCom();

			}

			NodeApiCallType.checkRestCall(spVM().ppiq());

			buildPageImpl();

			if (!cleanResponse) {
				ZKPage.renderHeadPageAndForms(this);
				ZKPage.renderHeadRsrcs(window, StdHeadLib.PAGE_JS);
			}

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

//		try {
		URsp.sendResponseAsException(ZKR.getResponse(), exRslt);
//		} catch (IOException e) {
//			L.error("sendResponseAsException", e);
//			throw new RuntimeException(e);
//		}

		return this;
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

	//		ZKPage.addJsTag(window.getPage(), "function onBool(data){\n" + "    zAu.send(new zk.Event(zk.Widget.$('.boolCom'), 'onBool', {'data':data?data:null}, {toServer:true}));\n" + "}");
	public static class BoolEvent extends Html implements IBoolEvent<BoolEvent> {

		public BoolEvent() {
		}

		public BoolEvent(String content) {
			super(content);
		}

		@Override
		public void onEvent(Event event) throws Exception {
			Map<String, Object> data = (Map) event.getData();
			Coor coor = Coor.of(data);
			doEvent(event, data, coor);
		}

		protected void doEvent(Event event, Map<String, Object> data, Coor coor) {
			try {
				L.info("Bool coor : " + coor.mapc());
			} catch (Exception ex) {
				L.error("doEvent:" + ex);
			}
		}

		;

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

//	public static class PulseEvent extends Html implements IBoolEvent<BoolEvent> {
//
//		public PulseEvent() {
//		}
//
//		public PulseEvent(String content) {
//			super(content);
//		}
//
//		@Override
//		public void onEvent(Event event) throws Exception {
//			Map<String, Object> data = (Map) event.getData();
//			Coor coor = Coor.of(data);
//			doEvent(event, data, coor);
//		}
//
//		protected void doEvent(Event event, Map<String, Object> data, Coor coor) {
//			L.info("Bool coor : " + coor.mapc());
//		}
//
//		@Override
//		public void onPageAttached(Page newpage, Page oldpage) {
//			super.onPageAttached(newpage, oldpage);
//			addEventListener("onBool", this);
//		}
//
//		public interface Coor extends IContract {
//			int getX();
//
//			int getY();
//
//			int getX2();
//
//			int getY2();
//
//			long getDuration();
//
//			String getDirection();
//
//			public static Coor of(Map data) {
//				return MapTableContract.buildContract_MarkNotRq(data, Coor.class);
//			}
//
//			default String toStringSimple() {
////				return X.f("%s_%s_%s <<< %s_%s <<<%s", getNid(), getName(), getCreated());
//				return mapc().toString();
//			}
//		}
//	}

}
