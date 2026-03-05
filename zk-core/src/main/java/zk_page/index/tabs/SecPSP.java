package zk_page.index.tabs;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.exception.NI;
import mpc.str.sym.SYMJ;
import mpe.cmsg.ns.NodeID;
import mpu.X;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.TablePrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Window;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.events.AppEventsPage;
import zk_notes.node_state.impl.PageState;
import zk_notes.node_state.impl.PlaneState;
import zk_os.coms.SpaceType;
import zk_os.coms.cache.SecBool;
import zk_os.coms.cache.SecCache;
import zk_os.coms.cache.SecPropsTuple;
import zk_os.core.Sdn;
import zk_os.core.Sdnu;
import zk_os.db.net.WebUsr;
import zk_os.sec.ROLE;
import zk_os.walkers.PagesWalker;
import zk_os.walkers.PlaneWalker;
import mpe.img.EColor;
import zk_page.ZKS;
import zk_page.behaviours.BgImg;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.with_com.WithMainTbx;
import zk_page.with_com.WithSearch;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

//@PageRoute(sd3 = "index", role = ROLE.ADMIN)
@PageRoute(pagename = "@@sec", role = ROLE.ANONIM)
public class SecPSP extends PageSP implements IPerPage, IZState, WithLogo, WithMainTbx, WithSearch {

	public static final Logger L = LoggerFactory.getLogger(SecPSP.class);

	public SecPSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	@SneakyThrows
	public void buildPageImpl() {

		WebUsr usr = WebUsr.get();

		boolean isAllowedView = APP.IS_DEBUG_ENABLE || usr.isMainRole_ADMIN_OWNER();
		if (!isAllowedView) {
			return;
		}

		SeNoteTbxm.registerHeadCom();

		ZKS.PADDING0(window);
		ZKS.MARGIN(window, "30px 0 0 0");
		ZKS.HEIGHT_MIN(window, "1200px");

		ZKS.BGIMAGE(window, BgImg.getBgImageRelPathAsCssBgProp("bg_l_sec.png"), "contain", "top", "repeat");

		//
		//

		Menupopup0 menu = getLogoOrAdd().getContextMenu();

		AppEventsPage.applyEvent_EditPageProps(menu, getPageState().pathFc());

		//
		//

		WalkerOpts walkerOpts = new WalkerOpts();

		String sdnStr = ppiq().queryUrl().getFirstAsStr("sdn", null);
		if (X.empty(sdnStr)) {

			SecAllPlanes secAllPlanes = new SecAllPlanes(usr, walkerOpts);
			window.appendChild(secAllPlanes);

			return;
		} else if ("*".equals(sdnStr)) {

			String string0 = SecCache.toString0(0);
			window.appendChild(Xml.PRE(string0));

			return;
		}

		Div0 oneView;
		NodeID nodeID = NodeID.of(sdnStr);
		switch (nodeID.state) {
			case FULL:
			case PAGED:
				oneView = SecAllPlanes.buildView_OnePage(usr, nodeID.plane(), nodeID.page());
				break;

			case BIG:
			case EMPTY:
			case SINGLE:
			default:
				NI.stop("Illegal node '%s'", sdnStr);
				return;

		}

		window.appendChild(oneView);


	}


	static class WalkerOpts {
		boolean withSysPlanes = false;
		boolean withSysPages = false;
		boolean withUserDomains = true;
		boolean withIndex = true;
	}

	@RequiredArgsConstructor
	static class SecAllPlanes extends Div0 {

		final WebUsr usr;
		final WalkerOpts walkerOpts;

		private static String STYLE_CENTER = "style='text-align:center'";

		@Override
		protected void init() {
			super.init();

			Div0 __ = this;

			__.appendH(1, "Security / " + usr.getLogin() + "", STYLE_CENTER);

			List<Pare3<String, Path, PlaneState>> planeStates = PlaneWalker.doWalkToList(walkerOpts.withIndex, walkerOpts.withSysPlanes, walkerOpts.withUserDomains);

			for (Pare3<String, Path, PlaneState> planeState : planeStates) {

				Div0 div0 = buildOnePlaneView(planeState, usr, walkerOpts);

				__.appendChild(div0);
			}
//			Collection<String> planesWithUsers = AFC.PLANES.DIR_PLANES_NAMES_CLEAN(true);
//			Map<Sdn, Map<String, AFCSec.SecProps>> all = AFCSec.CACHE_FORMS_PROPS.getAll(i -> i);

		}

		private static Div0 buildOnePlaneView(Pare3<String, Path, PlaneState> planeState, WebUsr usr, WalkerOpts walkerOpts) {

			Div0 viewOnePlane = new Div0();

			String plane = planeState.key();
//				String titlePlane = plane.equals(NodeID.PAGE_INDEX_ALIAS) ? SYMJ.HOME2 + " " + plane : plane;
			String titlePlane = SYMJ.HOME2 + " " + plane;

			viewOnePlane.appendH(2, titlePlane, STYLE_CENTER).bgcolor(EColor.LBLUE.nextColor()).block().height(100);//

			List<Pare<String, PageState>> pageStates = PagesWalker.doWalkToList(plane, walkerOpts.withSysPages);

			for (Pare<String, PageState> pageState : pageStates) {

				Div0 onePageView = buildView_OnePage(usr, plane, pageState.key());

				viewOnePlane.appendChild(onePageView);

			}

			return viewOnePlane;
		}

		private static Div0 buildView_OnePage(WebUsr usr, String plane, String pagename) {

			Div0 divOnePage = new Div0();

			String titlePagename = pagename.equals(NodeID.PAGE_INDEX_ALIAS) ? SYMJ.HOME + " " + pagename : pagename;

			//
			//

			Sdn sdn = Sdn.of(plane, pagename);
			Sdnu sdnu = sdn.withUser(usr);

			SecPropsTuple pageSecProps = SecCache.getPropsPages(sdn);//
			SecBool pageSecBool = SecCache.getCacheBool(SpaceType.PAGES).get(sdnu.toSdnuPlaneIndex()).get(pagename);

//			SecBool pageSecBool = SecCache.getCacheBool(SpaceType.PAGES).get(sdnu).get(pagename);
//			SecPropsTuple pageSecProps = SecCache.getCacheProps(SpaceType.PAGES).get(sdn).get(pagename);// AFCSec.CACHE_PAGES_PROPS

			String viewBoolWithProps = pageSecBool + " ||| " + pageSecProps;

//					__.appendH(3, " " + titlePagename, STYLE_CENTER);
			Xml h3Page = divOnePage.appendH(3, " " + titlePagename + " " + viewBoolWithProps + " ", STYLE_CENTER);
			h3Page.bgcolor(EColor.GRAY.nextColor()).block().height(100);
//					__.appendLbBlock(viewBoolWithProps).center();


			h3Page.onCLICK(e -> {
				Div0 div0 = buildView_Nodes(sdnu);
				divOnePage.appendChild(div0);
			});


//					Rt.buildTable()
//					Table0
//					AFCSec.getItemPaths(usr, AFC.SpaceType.PAGES, sdn);
//					for (AFC.SpaceType spaceType : AFC.SpaceType.values()) {

//					__.appendH(4, spaceType, "style='text-align:center;color:coral'");

//						Set<Path> itemPaths = AFCSec.getItemPaths(usr, spaceType, Sdn.of(plane, pagename), AFCSec.I_SECV);
//
//						for (Path itemPath : itemPaths) {
//							__.appendLbBlock(UF.fn(itemPath));
//						}

//					}
			return divOnePage;
		}

		private static Div0 buildView_Nodes(Sdnu sdnu) {
			Div0 divPage = new Div0();

//			Map<String, SecPropsTuple> formsSecProps = AFCSec.CACHE_FORMS_PROPS.get(sdnu.sdn);
//			Map<String, SecPropsTuple> formsSecProps = SecCache.getCacheProps(SpaceType.NODES).get(sdnu.sdn);
			Map<String, SecPropsTuple> formsSecProps = SecCache.getPropsNodes(sdnu.sdn);

			if (formsSecProps.isEmpty()) {
				divPage.appendLbBlock("NO DATA").center();
				return divPage;
			}

			{
				TablePrint t2 = new TablePrint("name", "props");
				Map<String, SecBool> stringSecBoolMap = SecCache.getCacheBool(SpaceType.NODES).get(sdnu);
				stringSecBoolMap.forEach((k, v) -> t2.addRowAs(k, v));
				divPage.appendCode(t2.toStringBuilder().toString()).inlineBlock();
			}
			//
			//
			{
				TablePrint t = new TablePrint("name", "props");
				formsSecProps.forEach((k, v) -> t.addRowAs(k, v));
				divPage.appendCode(t.toStringBuilder().toString()).inlineBlock();
			}

			if (true) {

			} else {

				for (Map.Entry<String, SecPropsTuple> entryFormsSecProps : formsSecProps.entrySet()) {
					String formName = entryFormsSecProps.getKey();
					SecPropsTuple formSecProps = entryFormsSecProps.getValue();
					divPage.appendH(3, " " + formName, "style='color:darkred'");
					divPage.appendLbBlock(formSecProps + "");

					Map<String, SecBool> stringSecBoolMap = SecCache.getCacheBool(SpaceType.NODES).get(sdnu);
					divPage.appendLbBlock(stringSecBoolMap.get(formName) + "");

				}
			}

			return divPage;
		}
	}

//	@RequiredArgsConstructor
//	static class SecPlane extends Div0 {
//		final Sdn sdn;
//
//		@Override
//		protected void init() {
//			super.init();
//
//
//		}
//	}
}
