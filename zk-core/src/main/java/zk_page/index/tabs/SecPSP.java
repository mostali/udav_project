package zk_page.index.tabs;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.APP;
import mpc.str.sym.SYMJ;
import mpe.call_msg.core.NodeID;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.TablePrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZState;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_notes.coms.SeNoteTbxm;
import zk_notes.events.AppEventsPage;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.PageState;
import zk_notes.node_state.impl.PlaneState;
import zk_os.coms.AFCSec;
import zk_os.core.Sdn;
import zk_os.core.Sdnu;
import zk_os.db.net.WebUsr;
import zk_os.sec.ROLE;
import zk_os.walkers.PagesWalker;
import zk_os.walkers.PlaneWalker;
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

		boolean isAllowedView = APP.IS_DEBUG_ENABLE || WebUsr.get().isMainRole_ADMIN_OWNER();
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

		window.appendChild(new SecPlanes());

	}


	static class SecPlanes extends Div0 {
		@Override
		protected void init() {
			super.init();

			Div0 __ = this;

			WebUsr usr = WebUsr.get();

			String STYLE_CENTER = "style='text-align:center'";

			__.appendH(1, "Security / " + usr.getLogin() + "", STYLE_CENTER);

			boolean withSysPlanes = false;
			boolean withSysPages = false;
			boolean withUserDomains = true;
			boolean withIndex = true;

			List<Pare3<String, Path, PlaneState>> planeStates = PlaneWalker.doWalkToList(withIndex, withSysPlanes, withUserDomains);

			for (Pare3<String, Path, PlaneState> planeState : planeStates) {

				String plane = planeState.key();
//				String titlePlane = plane.equals(NodeID.PAGE_INDEX_ALIAS) ? SYMJ.HOME2 + " " + plane : plane;
				String titlePlane = SYMJ.HOME2 + " " + plane;

				__.appendH(2, titlePlane, STYLE_CENTER);
				__.appendHr();

				List<Pare<String, PageState>> pageStates = PagesWalker.doWalkToList(plane, withSysPages);

				for (Pare<String, PageState> pageState : pageStates) {

					String pagename = pageState.key();
					String titlePagename = pagename.equals(NodeID.PAGE_INDEX_ALIAS) ? SYMJ.HOME + " " + pagename : pagename;

					//
					//

					Sdn sdn = Sdn.of(plane, pagename);
					Sdnu sdnu = sdn.withUser(usr);

					AFCSec.SecProps pageSecProps = AFCSec.CACHE_PAGES_PROPS.get(sdn).get(pagename);
					AFCSec.SecBool pageSecBool = AFCSec.CACHE_PAGES_BOOL.get(sdnu).get(pagename);
					String viewBoolWithProps = pageSecBool + " ||| " + pageSecProps;

//					__.appendH(3, " " + titlePagename, STYLE_CENTER);
					__.appendH(3, " " + titlePagename + " " + viewBoolWithProps + " ", STYLE_CENTER);
//					__.appendLbBlock(viewBoolWithProps).center();

					Map<String, AFCSec.SecProps> formsSecProps = AFCSec.CACHE_FORMS_PROPS.get(sdn);
					if (true) {

						if (!formsSecProps.isEmpty()) {

							{
								TablePrint t2 = new TablePrint("name", "props");
								Map<String, AFCSec.SecBool> stringSecBoolMap = AFCSec.CACHE_FORMS_BOOL.get(sdnu);
								stringSecBoolMap.forEach((k, v) -> t2.addRowAs(k, v));
								__.appendCode(t2.toStringBuilder().toString()).inlineBlock();
							}
							//
							//
							{
								TablePrint t = new TablePrint("name", "props");
								formsSecProps.forEach((k, v) -> t.addRowAs(k, v));
								__.appendCode(t.toStringBuilder().toString()).inlineBlock();
							}


						}

					} else {

						for (Map.Entry<String, AFCSec.SecProps> entryFormsSecProps : formsSecProps.entrySet()) {
							String formName = entryFormsSecProps.getKey();
							AFCSec.SecProps formSecProps = entryFormsSecProps.getValue();
							__.appendH(3, " " + formName, "style='color:darkred'");
							__.appendLbBlock(formSecProps + "");

							Map<String, AFCSec.SecBool> stringSecBoolMap = AFCSec.CACHE_FORMS_BOOL.get(Sdnu.of(usr.getSid(), sdn));
							__.appendLbBlock(stringSecBoolMap.get(formName) + "");

						}
					}

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
				}
			}
//			Collection<String> planesWithUsers = AFC.PLANES.DIR_PLANES_NAMES_CLEAN(true);
//			Map<Sdn, Map<String, AFCSec.SecProps>> all = AFCSec.CACHE_FORMS_PROPS.getAll(i -> i);

		}
	}

	@RequiredArgsConstructor
	static class SecPlane extends Div0 {
		final Sdn sdn;

		@Override
		protected void init() {
			super.init();


		}
	}
}
