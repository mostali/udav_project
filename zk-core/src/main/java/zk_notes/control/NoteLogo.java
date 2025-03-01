package zk_notes.control;

import mp.utl_odb.tree.UTree;
import mpc.env.APP;
import mpc.env.AppProfile;
import mpc.env.boot.BootRunUtils;
import mpc.fs.ext.EXT;
import mpc.fs.fd.RES;
import mpc.str.sym.SYMJ;
import mpu.Sys;
import mpu.func.Function2;
import mpu.str.RANDOM;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import zk_com.base_ctr.Menupopup0;
import zk_form.WithLogo;
import zk_form.dirview.SimpleDirView;
import zk_notes.events.ANM;
import zk_notes.events.AppEventsFD;
import zk_notes.events.AppPageEvents;
import zk_os.AFC;
import zk_os.AppZosProps;
import zk_form.notify.ZKI;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_page.ZKCFinder;
import zk_page.ZKR;
import zk_page.behaviours.EventHighlightForm;
import zk_page.core.PageSP;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.FormState;
import zk_notes.AppNotesProps;
import zk_notes.ANI;

import java.nio.file.Paths;
import java.util.List;

public class NoteLogo extends WithLogo.LogoCom {

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
				menu.addMenuItem("Login via Vk", (Event e) -> ZKR.redirectToPage("https://vk.com/im?sel=" + vkBotId, true));
			}
			if (tgBotId != null) {
				menu.addMenuItem("Login via Tg", (Event e) -> ZKR.redirectToPage("https://t.me/" + tgBotId, true));
			}
			menu.addMenuItem("Login..", (Event e) -> ZKR.redirectToPage("/login", true));
			return;
		}

		FormState pagetate = getPageState();

		Boolean headOpenState = pagetate.hasPropEnable(AppZosProps.LOGO_HEADER_OPEN, false);

		boolean secAdminOrOwner = SecMan.isAdminOrOwner();
		boolean isOwner = SecMan.isOwner();
		boolean secAllowedEdit = SecMan.isAllowedEdit();


		if (SecMan.isAllowedEdit()) {

			menu.add_______();

			Function2<String, String, Object> successCallback = (String newNotesName, String val) -> NodeFileTransferMan.addNewFormAndOpenUX(newNotesName, val);
			String[] initValues = {"Note-" + RANDOM.UUID(3), ""};
			String[] placeholders = {"name", "content"};
			menu.addMI_Tbx2_Cfrm(ANI.NEW + "New form..", initValues, placeholders, successCallback);

			Menupopup0 uploadMenu = menu.addInnerMenu("Upload form..");

			ANM.applyMenu_Upload(uploadMenu, ppi().sdnHybryd());

		}

		if (secAllowedEdit) {
			menu.add_______();
			AppPageEvents.applyEvent_EditPageProps(menu, pagetate.pathFc());
		}


		if (secAllowedEdit) {
			menu.add_______();
			menu.addMenuItem(ANI.SETTINGS + " Quick Panel" + (headOpenState ? SYMJ.OK_GREEN : ""), (Event e) -> {
				if (headOpenState) {
					pagetate.set(AppZosProps.LOGO_HEADER_OPEN, false);
					NotesPageHeader.removeMeFirst();
				} else {
					NotesPageHeader.openSimple();
					pagetate.set(AppZosProps.LOGO_HEADER_OPEN, true);
				}
				ZKR.restartPage();
			});
		}

		if (secAdminOrOwner) {

			menu.add_______();

			Boolean openStateCgfPanel = pagetate.hasPropEnable(AppNotesProps.PAGE_CONFIG_OPEN, false);

			menu.addMenuItem(ANI.SETTINGS + " Config Panel" + (openStateCgfPanel ? SYMJ.OK_GREEN : ""), (Event e) -> {
				if (openStateCgfPanel) {
					pagetate.set(AppNotesProps.PAGE_CONFIG_OPEN, false);
					NotesHeaderProps.findFirst().removeMe();
				} else {
					NotesHeaderProps.openSimple();
					pagetate.set(AppNotesProps.PAGE_CONFIG_OPEN, true);
				}
				ZKR.restartPage();
			});


		}

		menu.add_______();
		menu.addMI_Href("Logout " + Sec.getUser().getUsername(), "/logout", "");

		if (secAdminOrOwner) {

			if (isOwner) {

				menu.add_______();

				menu.addMI_Href_v1("App Logs", PageSP.PAGENAME_LOGS);

				menu.addMI_EDITOR("App Logger's", APP.getPathLOG(), true, EXT.XML);

				menu.add_______();

				menu.addMenuItem(ANI.REBOOT + " Reboot App", e -> ZKI.infoEditorBw(Sys.exec_rq("./r.sh", "--R").getOutMerged()));
				menu.addMenuItem(ANI.REBOOT + " Open FS", e -> SimpleDirView.openWithSimpleMenu(APP.LOCATION.getAppDataDir(true)));
				AppEventsFD.applyEvent_OPENTREE(menu, APP.GNC_TREE());
				AppEventsFD.applyEvent_OPENTREE(menu, APP.GND_TREE());
				AppEventsFD.applyEvent_OPENTREE(menu, APP.GNDD_TREE());
				menu.addMenuItem(SYMJ.CLEAR + " Clear All Ctx Db", e -> {
					UTree.clear( //
//							APP.GNC_TREE(), //
							APP.GND_TREE(), //
							APP.GNDD_TREE() //
					);
					ZKR.restartPage();
				});
//				menu.addMenuItem(SYMJ.GLOB + " Open GNC Db", e -> AppEventsFD.applyEvent_OPENDIR_OS()));
//				menu.addMenuItem(SYMJ.GLOB + " Open GNC Db", e -> ZKM.showModal("Global Node Context", Listbox0.fromTreeDb(APP.GNC_TREE())));

				menu.addMenuitem_ExecAnyScriptOS(ANI.EXEC + " Exec Sh", "bash");

				menu.addMI_EDITOR(ANI.PROPS + " App Props*RL", APP.getPathAP(), true, EXT.PROPERTIES);

				menu.addMI_EDITOR(ANI.PROPS + " >App Props*" + AppProfile.getFirstUseful(), APP.getPathAP(1), true, EXT.PROPERTIES);

				menu.addMI_EDITOR(ANI.SCRIPT + "App Runner R.SH", Paths.get("r.sh"), true, EXT.SH);

				menu.add_______();

				menu.addMI_OpenDirView(ANI.DIRVIEW + " Open Head Rsrc's", AFC.HEADS.DIR_HEADS());
				menu.addMenuItem(ANI.DIRVIEW + " Open Page Dir", e -> Sys.open_Nautilus(pagetate.pathFc().getParent()));

//				menu.add_______();

//				menu.addMenuItem(SYMJ.CLEAR + " Clear Page From Empty Notes", e -> NodeFileTransferMan.clearPageFromEmptyNotes(sdn()));

			}

			Menupopup0 innerMenuAbout = menu.addInnerMenu("About");
			innerMenuAbout.addMI_Href("v." + BootRunUtils.getVersionFromAny(WithLogo.class, "0"));
			innerMenuAbout.addMenuItem("Debug border on component", e -> EventHighlightForm.applyOnOff_MouseOverOut((List) ZKCFinder.findAllInWin(HtmlBasedComponent.class, true)));
			//				Tbx_CfrmSerializableEventListener.toMenuItemComponent("exec","")
			//				simpleMenupopup.addMenuitem("Debug border on component", e -> EventHighlightForm.applyOnOff_MouseOverOut((List) ZKComFinder.findAllInWin(HtmlBasedComponent.class, true)));

		}

		menu.addMenuItem(ANI.HELP + " Help", (Event e) -> ZKI.infoEditorBw(RES.readString("/help.txt")));

	}

}
