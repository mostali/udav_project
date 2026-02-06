package zk_notes.control;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.env.AppProfile;
import mpc.env.Env;
import mpc.env.boot.BootRunUtils;
import mpc.url.UUrl;
import mpc.fs.ext.EXT;
import mpc.fs.fd.RES;
import mpc.str.sym.SYMJ;
import mpc.types.OptLazySupplier;
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
import zk_notes.AppNotesCore;
import zk_notes.events.ANM;
import zk_notes.events.AppEventsFD;
import zk_notes.events.AppEventsPage;
import zk_os.coms.AFC;
import zk_form.notify.ZKI;
import zk_os.core.Sdn;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_os.tasks.TaskPanel;
import zk_os.tasks.v1.TaskPanel_V1;
import zk_page.ZKR;
import zk_page.core.PageSP;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.ObjState;
import zk_notes.AppNotesProps;
import zk_notes.ANI;
import zk_page.panels.AppConfigPropsTopPanel;
import zk_page.panels.QuickNotesTopPanel;
import zk_page.panels.BottomHistoryPanel;
import zk_pages.LogPSP;

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

		ObjState pagetate = getPageState();

		boolean isAdmin = SecMan.isOwnerOrAdmin();
		boolean isOwner = SecMan.isOwner();
		boolean isAlllowEdit = SecMan.isAllowedEditPlane();


		if (isAlllowEdit) {

			{ // ADD
				Function2<String, String, Object> successCallback = (String newNotesName, String val) -> NodeFileTransferMan.addNewFormAndOpenUX(newNotesName, val);
				String[] initValues = {"Note-" + RANDOM.uuid(3), ""};
				String[] placeholders = {"name", "content"};
				menu.addMI_Tbx2_Cfrm(SYMJ.FILE_HTML + " New form..", initValues, placeholders, successCallback);

			}

			{ // UPLOAD
				Menupopup0 uploadMenu = menu.addInnerMenu(SYMJ.UPLOAD + " Upload form..");

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

			Pare<Menuitem, Menupopup0> miOpenDirView = menu.addMI_OpenDirView(ANI.DIRVIEW + " Open Head Rsrc's", AFC.HEADS.RPA_HEADS(), true);
			miOpenDirView.key().setTooltiptext("Add files to dir for render as " + SYMJ.ARROW_RIGHT_SPEC + " HEAD-AFTER:*.js,*.css | HEAD-BEFORE:*.head | BODY: *.xml, *.html");

			menu.addMI(ANI.DIRVIEW + " Open Page Dir", e -> Sys.open_Nautilus(pagetate.pathFc().getParent()));

			menu.add_______();

			Menupopup0 viewsMenu = menu.addInnerMenu(ANI.PAGE_ENT + " View's..");

			applyMainViews(viewsMenu);


		}

		OptLazySupplier<Menupopup0> getPanelsMenuRef = new OptLazySupplier(() -> menu.addInnerMenu(ANI.SETTINGS + " Panel's"));


		{

			if (isAdmin) { //CONFIG PANEL
				String propName = AppNotesProps.PP_CONFIG_OPENED;
				Supplier<Boolean> openStateCgfPanel = () -> pagetate.hasPropEnable(propName, false);
				Menupopup0 menupopup0 = getPanelsMenuRef.get();
				menupopup0.addMI(ANI.SETTINGS + " App Config Panel" + (openStateCgfPanel.get() ? SYMJ.OK_GREEN : ""), (Event e) -> {
					if (openStateCgfPanel.get()) {
						pagetate.set(propName, false);
//						TopNotesPropsPanel.findFirst().removeMe();
						AppConfigPropsTopPanel.removeMeFirst(null);
					} else {
						AppConfigPropsTopPanel.openSimple();
						pagetate.set(propName, true);
					}
//					ZKR.restartPage();
				});

				menupopup0.add_______();
			}

			if (isAlllowEdit) {
//			menu.add_______();
				Supplier<Boolean> headOpenState = () -> pagetate.hasPropEnable(AppNotesProps.PP_QUICK_NOTES_OPENED, false);
				getPanelsMenuRef.get().addMI(ANI.SETTINGS + " Quick Notes Panel" + (headOpenState.get() ? SYMJ.OK_GREEN : ""), (Event e) -> {
					if (headOpenState.get()) {
						pagetate.set(AppNotesProps.PP_QUICK_NOTES_OPENED, false);
						QuickNotesTopPanel.removeMeFirst(null);
					} else {
						QuickNotesTopPanel.openSimple();
						pagetate.set(AppNotesProps.PP_QUICK_NOTES_OPENED, true);
					}
//				ZKR.restartPage();
				});
			}

			if (isAdmin) { //BOTTOM HISTORY PANEL
				String propName = AppNotesProps.PP_BOTTOM_HISTORY_OPEN;
				Supplier<Boolean> openStateCgfPanel = () -> pagetate.hasPropEnable(propName, false);
				getPanelsMenuRef.get().addMI(ANI.SETTINGS + " Action History Panel" + (openStateCgfPanel.get() ? SYMJ.OK_GREEN : ""), (Event e) -> {
					Boolean opened = openStateCgfPanel.get();
					pagetate.set(propName, !opened);
					if (opened) {
						BottomHistoryPanel.removeMeFirst(null);
					} else {
						BottomHistoryPanel.openSimple();
					}
//					ZKR.restartPage();
				});

			}

			if (isAdmin) {
				{ //TASKS PANEL
					String propName = AppNotesProps.PP_TASKS_OPENED;
					Supplier<Boolean> openStateCgfPanel = () -> pagetate.hasPropEnable(propName, false);
					getPanelsMenuRef.get().addMI(ANI.SETTINGS + " Task's Panel" + (openStateCgfPanel.get() ? SYMJ.OK_GREEN : ""), (Event e) -> {
						Boolean opened = openStateCgfPanel.get();
						pagetate.set(propName, !opened);
						if (opened) {
							TaskPanel.removeMeFirst(null);
						} else {
							TaskPanel.openSimple();
						}
					});
				}

				{ //TASKS PANEL V1
					String propName = AppNotesProps.PP_TASKS_V1_OPENED;
					Supplier<Boolean> openStateCgfPanel = () -> pagetate.hasPropEnable(propName, false);
					getPanelsMenuRef.get().addMI(ANI.SETTINGS + " Task's Panel V1" + (openStateCgfPanel.get() ? SYMJ.OK_GREEN : ""), (Event e) -> {
						Boolean opened = openStateCgfPanel.get();
						pagetate.set(propName, !opened);
						if (opened) {
							TaskPanel_V1.removeMeFirst(null);
						} else {
							TaskPanel_V1.openSimple();
						}
					});
				}

			}


		}

		if (isAdmin) {

			{
				menu.add_______();

				Menupopup0 menuTrees = menu.addInnerMenu(SYMJ.FILE_DB + " Tree Data's..");

				AppEventsFD.applyEvent_OPENTREE(menuTrees, APP.TREE_GNC());
				menuTrees.add_______();
				AppEventsFD.applyEvent_OPENTREE(menuTrees, APP.TREE_GUL());
				AppEventsFD.applyEvent_OPENTREE(menuTrees, APP.TREE_GND_TASKS_V1());
				AppEventsFD.applyEvent_OPENTREE(menuTrees, APP.TREE_GNDD_V1());
				AppEventsFD.applyEvent_OPENTREE(menuTrees, AppNotesCore.TREE_NOTIFY_GLOB().getDbFilePath());

				Pare<Menuitem, Menupopup0> mi = menuTrees.addMI(SYMJ.CLEAR + " Clear All Ctx Db", e -> {
					UTree.clearAll( //
							APP.TREE_GND_TASKS_V1(), //
							APP.TREE_GNDD_V1() //
					);
					ZKR.restartPage();
				});

			}

			if (isOwner) {

				menu.add_______();

				menu.addMI(ANI.REBOOT + " Open FS/App", e -> DirView0.openWithSimpleMenu(APP.LOCATION.getAppDataDir(true)));
				menu.addMI(ANI.REBOOT + " Open FS/RL", e -> DirView0.openWithSimpleMenu(Env.RUN_LOCATION));
				menu.addMenuitem_ExecAnyScriptOS(ANI.EXEC + " Exec Sh", "bash");

			}

			menu.add_______();

			menu.addMI_Href_in_Self(SYMJ.LOGOUT + " Logout / " + Sec.getUser().getUsername(), "/logout", "");

			Menupopup0 appPropsMenu = menu.addInnerMenu(SYMJ.TOOLS + " App");

			applyAppMenu(appPropsMenu);

			{
				Menupopup0 innerMenuAbout = menu.addInnerMenu(SYMJ.INFO_SIMPLE + " About");

				innerMenuAbout.addMI(ANI.HELP + " Help", (Event e) -> ZKI.infoEditorDark(RES.readString("/about.md")));

				innerMenuAbout.add_______();

				if (SecMan.isOwnerOrAdmin()) {
//					innerMenuAbout.addMI_SimpleLabel(APP.getAppName());
					innerMenuAbout.addMI_SimpleLabel(APP.APP_HOST);//.HOST.getAppHost0()
					innerMenuAbout.add_______();
				}
				innerMenuAbout.addMI_Href_in_Self(SYMJ.LINK + " Open Project on GitHub", "https://github.com/mostali/zznote", null);

				innerMenuAbout.add_______();

				innerMenuAbout.addMI_SimpleLabel("v." + APP.getVersion(WithLogo.class, "SNAPSHOT"));


			}

		}


	}

	private void applyAppMenu(Menupopup0 appPropsMenu) {

		appPropsMenu.addMI_Href_OpenWindow(ANI.FILE_LOG + " App Log", PageSP.PAGENAME_LOGS);
		appPropsMenu.addMI_Download(ANI.DOWNLOAD + " Download App Log", LogPSP.FILE_LOG);

		appPropsMenu.addMI_EDITOR("App Logger's", APP.getPathLogbackXml(), true, EXT.XML);

		appPropsMenu.add_______();

		appPropsMenu.addMI_EDITOR(ANI.PROPS_ZZZ + " App Props*RL", APP.getPathAP(), true, EXT.PROPERTIES);

		appPropsMenu.addMI_EDITOR(ANI.PROPS_ZZZ + " >App Props*" + AppProfile.getFirstUseful(), APP.getPathAP(1), true, EXT.PROPERTIES);

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
					return UUrl.normUrl(APP.HOST.getAppHostWithProtocol(), "tasks");
				default:
					if (name().startsWith("__")) {
						return UUrl.normUrl(APP.HOST.getAppHostWithProtocol(), "@@" + name().substring(2).toLowerCase());
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

		String currentUrl = Sdn.get().toCurrentUrl("?tbf=*");

		viewsMenu.addMI_Href_in_Self("Page Index", "/@@index", null);

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
