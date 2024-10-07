package zk_notes.control;

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
import zk_notes.ANM;
import zk_os.AppZosProps;
import zk_form.notify.ZKI;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_page.ZKCF;
import zk_page.ZKR;
import zk_page.behaviours.EventHighlightForm;
import zk_page.core.PageSP;
import zk_page.node.fsman.NodeFileTransferMan;
import zk_page.node_state.FormState;
import zk_notes.AppNotesProps;
import zk_notes.ANI;
import zk_notes.AppNotes;

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
			Integer vkBotId = AppNotes.getVkBotId(null);
			String tgBotId = AppNotes.getTgBotId(null);
			if (vkBotId != null) {
				menu.addMenuitem("Login via Vk", (Event e) -> ZKR.redirectToPage("https://vk.com/im?sel=" + vkBotId, true));
			}
			if (tgBotId != null) {
				menu.addMenuitem("Login via Tg", (Event e) -> ZKR.redirectToPage("https://t.me/" + tgBotId, true));
			}
			menu.addMenuitem("Login..", (Event e) -> ZKR.redirectToPage("/login", true));
			return;
		}

		FormState comState = getPageState();

		Boolean headOpenState = comState.hasPropEnable(AppZosProps.LOGO_HEADER_OPEN, false);

		boolean secAdminOrOwner = SecMan.isAdminOrOwner();
		boolean isOwner = SecMan.isOwner();
		boolean secAllowedEdit = SecMan.isAllowedEdit();


		if (SecMan.isAllowedEdit()) {

			menu.addSeparator();

			Function2<String, String, Object> successCallback = (String newNotesName, String val) -> NodeFileTransferMan.addNewFormAndOpenUX(newNotesName, val);
			String[] initValues = {"Note-" + RANDOM.UUID(3), ""};
			String[] placeholders = {"name", "content"};
			menu.addMenuitem_Tbx2_Cfrm(ANI.NEW + "New form..", initValues, placeholders, successCallback);

			Menupopup0 uploadMenu = menu.addInnerMenu("Upload form..");

			ANM.applyMenu_Upload(uploadMenu, ppi().sdn());

		}

		if (secAllowedEdit) {
			menu.addSeparator();
			menu.addMenuitem("Page Props", (Event e) -> ZKI.infoEditorBw(comState.pathFc(), EXT.JSON));
		}


		if (secAllowedEdit) {
			menu.addSeparator();
			menu.addMenuitem(ANI.SETTINGS + " Quick Panel" + (headOpenState ? SYMJ.OK_GREEN : ""), (Event e) -> {
				if (headOpenState) {
					comState.updatePropSingle(AppZosProps.LOGO_HEADER_OPEN, false);
					NotesPageHeader.removeMeFirst();
				} else {
					NotesPageHeader.openSimple();
					comState.updatePropSingle(AppZosProps.LOGO_HEADER_OPEN, true);
				}
				ZKR.restartPage();
			});
		}

		if (secAdminOrOwner) {

			menu.addSeparator();

			Boolean openStateCgfPanel = comState.hasPropEnable(AppNotesProps.PAGE_CONFIG_OPEN, false);

			menu.addMenuitem(ANI.SETTINGS + " Config Panel" + (openStateCgfPanel ? SYMJ.OK_GREEN : ""), (Event e) -> {
				if (openStateCgfPanel) {
					comState.updatePropSingle(AppNotesProps.PAGE_CONFIG_OPEN, false);
					NotesHeaderProps.findFirst().removeMe();
				} else {
					NotesHeaderProps.openSimple();
					comState.updatePropSingle(AppNotesProps.PAGE_CONFIG_OPEN, true);
				}
				ZKR.restartPage();
			});


		}

		menu.addSeparator();
		menu.addMenuitem("Logout " + Sec.getUser().getUsername(), "/logout", "");

		if (secAdminOrOwner) {

			if (isOwner) {

				menu.addSeparator();

				menu.addMenuitem_Href("App Logs", PageSP.PAGENAME_LOGS);

				menu.addMenuitem_EDITOR("App Logger's", APP.getPathLOG(), true, EXT.XML);

				menu.addSeparator();

				menu.addMenuitem(ANI.REBOOT + " Reboot App", e -> ZKI.infoEditorBw(Sys.exec_rq("./r.sh", "--R").getOutMerged()));

				menu.addMenuitem_ExecAnyScriptOS(ANI.EXEC + " Exec Sh", "bash");

				menu.addMenuitem_EDITOR(ANI.PROPS + " App Props*RL", APP.getPathAP(), true, EXT.PROPERTIES);

				menu.addMenuitem_EDITOR(ANI.PROPS + " >App Props*" + AppProfile.getFirstUseful(), APP.getPathAP(1), true, EXT.PROPERTIES);

				menu.addMenuitem_EDITOR(ANI.SCRIPT + "App Runner R.SH", Paths.get("r.sh"), true, EXT.SH);

				menu.addSeparator();

			}

			Menupopup0 innerMenuAbout = menu.addInnerMenu("About");
			innerMenuAbout.addMenuitem("v." + BootRunUtils.getVersionFromAny(WithLogo.class, "0"));
			innerMenuAbout.addMenuitem("Debug border on component", e -> EventHighlightForm.applyOnOff_MouseOverOut((List) ZKCF.findAllInWin(HtmlBasedComponent.class, true)));
			//				Tbx_CfrmSerializableEventListener.toMenuItemComponent("exec","")
			//				simpleMenupopup.addMenuitem("Debug border on component", e -> EventHighlightForm.applyOnOff_MouseOverOut((List) ZKComFinder.findAllInWin(HtmlBasedComponent.class, true)));

		}

		menu.addMenuitem(ANI.HELP + " Help", (Event e) -> ZKI.infoEditorBw(RES.readString("/help.txt")));

	}
}
