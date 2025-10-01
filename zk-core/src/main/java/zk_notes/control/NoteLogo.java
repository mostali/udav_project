package zk_notes.control;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.env.AppProfile;
import mpc.env.Env;
import mpc.env.boot.BootRunUtils;
import mpc.fs.UUrl;
import mpc.fs.ext.EXT;
import mpc.fs.fd.RES;
import mpc.str.sym.SYMJ;
import mpc.types.AtomicSupplier;
import mpc.types.OptSupplier;
import mpu.Sys;
import mpu.SysExec;
import mpu.func.Function2;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Menuitem;
import zk_com.base_ctr.Menupopup0;
import zk_form.WithLogo;
import zk_form.dirview.DirView0;
import zk_notes.events.ANM;
import zk_notes.events.AppEventsFD;
import zk_notes.events.AppEventsPage;
import zk_os.coms.AFC;
import zk_form.notify.ZKI;
import zk_os.core.Sdn;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_page.ZKR;
import zk_page.core.PageSP;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.FormState;
import zk_notes.AppNotesProps;
import zk_notes.ANI;
import zk_page.panels.TopNotesPropsPanel;
import zk_page.panels.TopQuickNotesPanel;
import zk_page.panels.BottomHistoryPanel;

import java.nio.file.Paths;
import java.util.function.Supplier;

public class NoteLogo extends WithLogo.LogoCom {

	public NoteLogo() {
		this(ROLE.toIcon());
	}

	public NoteLogo(String lb_OR_title) {
		super(lb_OR_title, null);
	}

	public NoteLogo(String lb_OR_title, String src) {
		super(lb_OR_title, src);
	}

	@Override
	protected void init() {
		super.init();

		boolean anonim = SecMan.isAnonim();
		if (anonim) {
			Integer vkBotId = APP.getVkBotId(null);
			String tgBotId = APP.getTgBotId(null);
			if (vkBotId != null) {
				menu.addMI("Login via Vk", (Event e) -> ZKR.redirectToPage("https://vk.com/im?sel=" + vkBotId, true));
			}
			if (tgBotId != null) {
				menu.addMI("Login via Tg", (Event e) -> ZKR.redirectToPage("https://t.me/" + tgBotId, true));
			}
			menu.addMI("Login..", (Event e) -> ZKR.redirectToPage("/login", true));
			return;
		}

		FormState pagetate = getPageState();

		boolean isAdmin = SecMan.isAdminOrOwner();
		boolean isOwner = SecMan.isOwner();
		boolean isAlllowEdit = SecMan.isAllowedEdit();


		if (isAlllowEdit) {

			{ // ADD
				Function2<String, String, Object> successCallback = (String newNotesName, String val) -> NodeFileTransferMan.addNewFormAndOpenUX(newNotesName, val);
				String[] initValues = {"Note-" + RANDOM.UUID(3), ""};
				String[] placeholders = {"name", "content"};
				menu.addMI_Tbx2_Cfrm(ANI.NEW + "New form..", initValues, placeholders, successCallback);

			}

			{ // UPLOAD
				Menupopup0 uploadMenu = menu.addInnerMenu("Upload form..");

				ANM.applyMenu_Upload(uploadMenu, ppi().sdnAny());
			}

			{ //Page Props
				if (isAlllowEdit) {
					menu.add_______();
					AppEventsPage.applyEvent_EditPageProps(menu, pagetate.pathFc());
				}
			}

			menu.add_______();
			menu.add_______();
			menu.add_______();

			menu.addMI_OpenDirView(ANI.DIRVIEW + " Open Head Rsrc's", AFC.HEADS.DIR_HEADS(),true);
			menu.addMI(ANI.DIRVIEW + " Open Page Dir", e -> Sys.open_Nautilus(pagetate.pathFc().getParent()));

			menu.add_______();

			Menupopup0 viewsMenu = menu.addInnerMenu(SYMJ.FILE2 + " View's..");

			applyMainViews(viewsMenu);


		}

		OptSupplier<Menupopup0> getPanelsMenuRef = new OptSupplier(() -> menu.addInnerMenu(ANI.SETTINGS + " Panel's"));

		if (isAlllowEdit) {
//			menu.add_______();
			Supplier<Boolean> headOpenState = () -> pagetate.hasPropEnable(AppNotesProps.LOGO_HEADER_OPEN, false);
			getPanelsMenuRef.get().addMI(ANI.SETTINGS + " Quick Notes Panel" + (headOpenState.get() ? SYMJ.OK_GREEN : ""), (Event e) -> {
				if (headOpenState.get()) {
					pagetate.set(AppNotesProps.LOGO_HEADER_OPEN, false);
					TopQuickNotesPanel.removeMeFirst(null);
				} else {
					TopQuickNotesPanel.openSimple();
					pagetate.set(AppNotesProps.LOGO_HEADER_OPEN, true);
				}
//				ZKR.restartPage();
			});
		}

		if (isAdmin) {


			{ //CONFIG PANEL
				Supplier<Boolean> openStateCgfPanel = () -> pagetate.hasPropEnable(AppNotesProps.PAGE_CONFIG_OPEN, false);
				getPanelsMenuRef.get().addMI(ANI.SETTINGS + " App Config Panel" + (openStateCgfPanel.get() ? SYMJ.OK_GREEN : ""), (Event e) -> {
					if (openStateCgfPanel.get()) {
						pagetate.set(AppNotesProps.PAGE_CONFIG_OPEN, false);
//						TopNotesPropsPanel.findFirst().removeMe();
						TopNotesPropsPanel.removeMeFirst(null);
					} else {
						TopNotesPropsPanel.openSimple();
						pagetate.set(AppNotesProps.PAGE_CONFIG_OPEN, true);
					}
//					ZKR.restartPage();
				});

			}
			{ //BOTTOM HISTORY PANEL
				Supplier<Boolean> openStateCgfPanel = () -> pagetate.hasPropEnable(AppNotesProps.PAGE_BOTTOM_HISTOTY_PANEL_OPEN, false);
				getPanelsMenuRef.get().addMI(ANI.SETTINGS + " Task History Panel" + (openStateCgfPanel.get() ? SYMJ.OK_GREEN : ""), (Event e) -> {
					if (openStateCgfPanel.get()) {
						pagetate.set(AppNotesProps.PAGE_BOTTOM_HISTOTY_PANEL_OPEN, false);
//						BottomHistoryPanel.findFirst().removeMe();
						BottomHistoryPanel.removeMeFirst(null);

					} else {
						BottomHistoryPanel.openSimple();
						pagetate.set(AppNotesProps.PAGE_BOTTOM_HISTOTY_PANEL_OPEN, true);
					}
//					ZKR.restartPage();
				});

			}
//			{
//				Boolean openStateCgfPanel = pagetate.hasPropEnable(AppNotesProps.TASKS_OPEN, false);
//
//				menu.addMI(ANI.SETTINGS + " Tasks Panel" + (openStateCgfPanel ? SYMJ.OK_GREEN : ""), (Event e) -> {
//					pagetate.set(AppNotesProps.TASKS_OPEN, !openStateCgfPanel);
//					ZKR.restartPage();
//				});
//			}
		}

		if (isAdmin) {

			if (isOwner) {

				menu.add_______();

				menu.addMI(ANI.REBOOT + " Open FS", e -> DirView0.openWithSimpleMenu(APP.LOCATION.getAppDataDir(true)));
				menu.addMI(ANI.REBOOT + " Open FS*", e -> DirView0.openWithSimpleMenu(Env.RUN_LOCATION));
				menu.addMenuitem_ExecAnyScriptOS(ANI.EXEC + " Exec Sh", "bash");

				menu.add_______();

				Menupopup0 menuTrees = menu.addInnerMenu("Tree's..");

				AppEventsFD.applyEvent_OPENTREE(menuTrees, APP.TREE_GNC());
				AppEventsFD.applyEvent_OPENTREE(menuTrees, APP.TREE_GUL());
				AppEventsFD.applyEvent_OPENTREE(menuTrees, APP.TREE_GND_TASKS_V1());
				AppEventsFD.applyEvent_OPENTREE(menuTrees, APP.TREE_GNDD_V1());

				Pare<Menuitem, Menupopup0> mi = menuTrees.addMI(SYMJ.CLEAR + " Clear All Ctx Db", e -> {
					UTree.clearAll( //
							APP.TREE_GND_TASKS_V1(), //
							APP.TREE_GNDD_V1() //
					);
					ZKR.restartPage();
				});

				Menupopup0 appPropsMenu = menu.addInnerMenu("App Props");

				applyAppMenu(appPropsMenu);

			}

			menu.add_______();

			menu.addMI_Href("Logout " + Sec.getUser().getUsername(), "/logout", "");

			{
				Menupopup0 innerMenuAbout = menu.addInnerMenu("About");
				if (SecMan.isAdminOrOwner()) {
					innerMenuAbout.addMI_SimpleLabel(APP.getAppName());
					innerMenuAbout.addMI_SimpleLabel(APP.HOST.getAppHost0());
				}
				innerMenuAbout.addMI_SimpleLabel("v." + APP.getVersion(WithLogo.class, "SNAPSHOT"));

				innerMenuAbout.addMI(ANI.HELP + " Help", (Event e) -> ZKI.infoEditorDark(RES.readString("/about.md")));

			}

		}


	}

	private void applyAppMenu(Menupopup0 appPropsMenu) {

		appPropsMenu.addMI_Href_OpenWindow("App Logs", PageSP.PAGENAME_LOGS);

		appPropsMenu.addMI_EDITOR("App Logger's", APP.getPathLogbackXml(), true, EXT.XML);

		appPropsMenu.add_______();

		appPropsMenu.addMI_EDITOR(ANI.PROPS + " App Props*RL", APP.getPathAP(), true, EXT.PROPERTIES);

		appPropsMenu.addMI_EDITOR(ANI.PROPS + " >App Props*" + AppProfile.getFirstUseful(), APP.getPathAP(1), true, EXT.PROPERTIES);

		appPropsMenu.addMI_EDITOR(ANI.SCRIPT + "App Runner R.SH", Paths.get("r.sh"), true, EXT.SH);

		appPropsMenu.add_______();

		if (BootRunUtils.checkAllowedR_SH()) {
			appPropsMenu.addMI(ANI.REBOOT + " Reboot App", e -> ZKI.infoEditorDark(SysExec.exec(false, "./r.sh", "--R").getOutMerged()));
		}
	}

	@RequiredArgsConstructor
	public enum BasePageViews {
		HTML, XSD, JSON, TASKS, __ACTUATOR, __DOCKER;

		public String getUrl() {
			switch (this) {
				case TASKS:
					return UUrl.normUrl(APP.HOST.getAppHost(), "tasks");
				default:
					if (name().startsWith("__")) {
						return UUrl.normUrl(APP.HOST.getAppHost(), "@@" + name().substring(2).toLowerCase());
					} else {
						return APP.HOST.getAppUrlWithPlaneAndPath("view", name().toLowerCase());
					}
			}
		}

		public String toName() {
			return name().startsWith("__") ? name().substring(2) : name();
		}
	}

	private void applyMainViews(Menupopup0 viewsMenu) {

		String currentUrl = Sdn.getRq().toCurrentUrl("?tbf=*");

		viewsMenu.addMI_Href("Page Index", "/@@index", null);

		viewsMenu.add_______();

		viewsMenu.addMI(SYMJ.SQUARE_BLACK + " Single note tabs view", (Event e) -> ZKR.redirectToPage(currentUrl));

		viewsMenu.add_______();

		for (BasePageViews value : BasePageViews.values()) {
			viewsMenu.addMI(SYMJ.SQUARE + " " + value.toName() + " View", (Event e) -> ZKR.toWindowByUrl.apply(value.getUrl()));
		}

		viewsMenu.add_______();
		viewsMenu.addMI(SYMJ.SQUARE_BLACK + " Manual", (Event e) -> ZKR.toRedirectWithPath.apply("_manual"));
		viewsMenu.add_______();

		viewsMenu.addMI(SYMJ.SQUARE_BLACK + " Demo Notes", (Event e) -> ZKR.toRedirectWithPath.apply("demo-notes"));
		viewsMenu.addMI(SYMJ.SQUARE_BLACK + " Demo Com (Dev)", (Event e) -> ZKR.toRedirectWithPath.apply("demo-com"));

		viewsMenu.add_______();


		viewsMenu.addMI(SYMJ.SQUARE_BLACK + " Class generator", (Event e) -> ZKR.toWindowByUrl.apply(APP.HOST.getAppUrlWithPlaneAndPath("view", "dsrc")));
		viewsMenu.addMI(SYMJ.SQUARE_BLACK + " Dev Script generator", (Event e) -> ZKR.toWindowByUrl.apply(APP.HOST.getAppUrlWithPlaneAndPath("view", "class")));

		;

		viewsMenu.add_______();


		viewsMenu.addMI(SYMJ.SQUARE_BLACK + " Log Analyzer", (Event e) -> ZKR.toRedirectWithPath.apply("@@log"));
		AppEventsFD.applyEvent_LOG_SIMPLE_VIEW(viewsMenu, APP.getPathServerLog());


	}

}
