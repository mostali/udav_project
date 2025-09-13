package zk_notes.events;

import lombok.RequiredArgsConstructor;
import mpc.env.APP;
import mpc.fs.ext.EXT;
import mpc.fs.path.IPath;
import mpc.str.sym.SYMJ;
import mpc.types.enums.EnDis;
import mpe.core.P;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.zkoss.zul.Menu;
import udav_net.apis.zznote.ApiCase;
import udav_net.apis.zznote.NoteApi;
import zk_com.base.*;
import zk_notes.ANI;
import zk_notes.AppNotes;
import zk_notes.apiv1.client.NoteApi0;
import zk_notes.node.core.NVT;
import zk_notes.node_state.FormState;
import zk_os.AFCC;
import zk_os.core.NodeData;
import zk_os.sec.Sec;
import zk_page.*;
import zk_notes.node.NodeDir;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base_ctr.Menupopup0;
import zk_os.sec.SecMan;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

//AppNotesMenu
public class ANM extends ANMFd {

	public static final String MARK_DEL = " " + SYMJ.ARROW_RIGHT_SPEC + " ";

	public static void applyMenu_Upload(Menupopup0 uploadMenu, Pare sdn) {
		Path file = AppNotes.getFormBlankDir(sdn, "file-", 3);
		uploadMenu.addMI_UploadTo("File..", file, false);

		Path video = AppNotes.getFormBlankDir(sdn, "video-", 3);
		uploadMenu.addMI_UploadTo("Video..", video, false);

		Path image = AppNotes.getFormBlankDir(sdn, "img-", 3);
		Function<String, Boolean> successClb = s -> {
			P.p("Image>>>" + s);
			return false;
		};
		uploadMenu.addMI_UploadTo("Image..", image, true, successClb);
	}

	public static void applyMenu_EditAs(Menupopup0 menu, NodeDir nodeDir, boolean innerMenu) {
		if (innerMenu) {
			menu = menu.addInnerMenu(ANI.EDIT_MODE + " Edit data..");
		}
//		Path pathForm = nodeDir.state().pathFc();
//		if (UFS.existFile(pathForm)) {
//			menu.addMI_EDITOR(ANI.EDIT_MODE + " Edit Form Data", pathForm, true, null);
//		}
//		Path pathCom = nodeDir.state().pathProps();
//		if (UFS.existFile(pathCom)) {
//			menu.addMI_EDITOR(ANI.EDIT_MODE + " Edit Form Props", pathCom, true, EXT.JSON);
//		}
		menu.addMI_EDITOR(ANI.EDIT_MODE + " Edit Form Data", nodeDir.state().pathFc(), true, null);
		menu.addMI_EDITOR(ANI.EDIT_MODE + " Edit Form Props*", nodeDir.state().pathPropsCom(), false, EXT.JSON);
	}

	public static void applyMenu_OpenAs(Menupopup0 menu, NodeDir nodeDir) {
		menu.add_______();
		Menupopup0 menuViewAs = menu.addInnerMenu("View As..");
		menuViewAs.addMI(ANI.OPEN_SE_EDITOR + " View As - HTML", e -> ZKM.showModal(nodeDir.nodeName(), Xml.ofFile(nodeDir.getPathFc())));
		menuViewAs.addMI(ANI.OPEN_SE_EDITOR + " View As - Markdown", e -> ZKM.showModal(nodeDir.nodeName(), Xml.ofMd(nodeDir.getPathFc())));
	}

	public static void applyMenu_Mark(Menupopup0 markMenu, NodeDir nodeDir) {
		ANM_Mark.applyMenu_Mark(markMenu, nodeDir);
	}

	public static class ANM_Exec {
		@RequiredArgsConstructor
		public enum ExeType {
			SH(CN.BASH), PY(CN.PYTHON3);
			final String cmdKey;
		}

		public static void applyExecMenu(Menupopup0 menu, NodeDir node) {
			FormState formState = node.state();

			addOpenToClipboardMenu(menu.addInnerMenu(SYMJ.CLIPBOARD + " Copy to clipboard..."), node);

			ExeType exeType = (ExeType) formState.getAs(CN.EXE, ExeType.class, null);
			if (exeType == null) {
				return;
			}
			Menupopup0 inMenuExecuteAs = menu.addInnerMenu("Execute As..");
			inMenuExecuteAs.addMenuitem_ExecFileTmp(ANI.EXEC + " " + exeType.cmdKey, exeType.cmdKey, formState.readFcData());
		}
	}

	private static void addOpenToClipboardMenu(Menupopup0 menu, NodeDir node) {

		String url2item = new NoteApi0().zApiUrl.GET_toItem(node.toItemPath());
		String url2event = new NoteApi0().zApiUrl.GET_toEvent(node.nodeID(), "get", NoteApi.PK_K, null);

//		menu.addMI(SYMJ.LINK + " Copy link to rest", e -> ZKJS.eval(X.f("copyToClb('%s')", url2item)));

		{
			if (Sec.isAdminOrOwner()) {
				if (node.getPathFcParentLs().size() > 2 || IPath.ofd(node.getPathFc().getParent()).dMapExt(ARR.EMPTY_MAP).hasOnly(EXT.PROPS, EXT.PROPS$$)) {
					Path pathFc = node.getPathFc();
					String dldUrl = APP.MAIN.getAppHost() + "/" + ApiCase._adi + "/.planes" + AFCC.relativizeAppFile(pathFc);
					menu.addMI(SYMJ.LINK + " Copy link to download file via API", e -> ZKJS.eval(X.f("copyToClb('%s')", dldUrl)));
				}
			}
		}
		menu.addMI(SYMJ.LINK + " Copy link to rest", e -> ZKJS.eval(X.f("copyToClb('%s')", url2item)));
		menu.addMI(SYMJ.LINK + " Copy link to rest event", e -> ZKJS.eval(X.f("copyToClb('%s')", url2event)));
		menu.addMeI_CopyCurlBashScriptToClipboard_I_Bash(node.nodeName());
		menu.add_______();
		menu.addMI(SYMJ.LINK + " Copy nodeID", e -> ZKJS.eval(X.f("copyToClb('%s')", node.nodeId())));
		menu.addMI(SYMJ.LINK + " Copy node data injected", e -> ZKME.openEditorTextReadonly(node.nodeId(), NodeData.of(node).newInjected(null).nodeData));
		menu.add_______();
		menu.addMI(ANI.DOWNLOAD + " Download As File", e -> ZKR.download(node.state().pathFc()));
		menu.addMI(SYMJ.LINK + " Copy node file pth", e -> ZKJS.eval(X.f("copyToClb('%s')", node.getPathFormFc())));

	}

	public static class ANM_Mark {
		public static void applyMenu_Mark(Menupopup0 mainMenu, NodeDir nodeDir) {

			FormState formState = nodeDir.state();

			Pare<Menu, Menupopup0> menus = mainMenu.addInnerMenu0(SYMJ.EYE + " More View's", true);
			Arrays.stream(NVT.values()).forEach(nvt -> {
				String lbl = "Change to '" + nvt.nameHu() + "'" + LBL_PREVVL(formState.get_VIEW(null));
				Menupopup0 m = nvt.isPrimaryInMenu() ? menus.val() : mainMenu;
				m.addMI(LBL(FormState.PK_VIEW, lbl), (SerializableEventListener) event -> {
					formState.fields().set_VIEW(nvt);
					ZKR.restartPage();
				});
			});
			mainMenu.appendChild(menus.key());
		}

		public static void applySecurityItems(Menupopup0 mainMenu, FormState formState) {

			Menupopup0 accessMenu = mainMenu.addInnerMenu("Security Access..");

			addEnableDisableProp_SEC(accessMenu, formState, FormState.SECV);
			addEnableDisableProp_SEC(accessMenu, formState, FormState.SECE);
//			markMenu.addSeparator();
			addEnableDisableProp_USER(accessMenu, formState, FormState.PK_USER);
		}

		private static void addEnableDisableProp_USER(Menupopup0 markMenu, FormState formState, String prop) {
			String curUser = formState.get_USER(null);
			String lbl = X.empty(curUser) ? "Set me (" + Sec.login() + ")" : " Allowed for all ";
			lbl += LBL_PREVVL(curUser);
			markMenu.addMI(LBL(FormState.PK_USER, lbl), buildEventPropChanger_REMOVE_or_SET_STATIC_VALUE(formState, prop, Sec.login()));
		}

		private static void addEnableDisableProp_SEC(Menupopup0 markMenu, FormState formState, String prop) {
			boolean hasPropNotEmpty = formState.hasPropNotEmpty(prop);
			String lbl = EnDis.valueOf(!hasPropNotEmpty).nameCap() + " for all" + LBL_PREVVL(formState.get(prop, null));
			markMenu.addMI(LBL(prop, lbl), buildEventPropChanger_REMOVE_or_SET_STATIC_VALUE(formState, prop, SecMan.SECFORALL));
		}

		public static EventListener buildEventPropChanger_BOOL_SWAP(NodeDir nodeDir, String prop, boolean defIfNotPresent) {
			return (SerializableEventListener) event -> {
				boolean hasProp = nodeDir.state().hasPropEnable(prop, defIfNotPresent);
				nodeDir.setStateProp(prop, !hasProp);
				ZKR.restartPage();
			};
		}

		private static EventListener buildEventPropChanger_VALUE(NodeDir nodeDir, String prop, Object value) {
			return (SerializableEventListener) event -> {
				nodeDir.setStateProp(prop, value);
				ZKR.restartPage();
			};
		}

		private static EventListener buildEventPropChanger_REMOVE_or_SET_STATIC_VALUE(FormState state, String prop, Object defValue) {
			return (SerializableEventListener) event -> {
				boolean hasPropNotEmpty = state.hasPropNotEmpty(prop);
				state.set(prop, hasPropNotEmpty ? null : defValue);
				ZKR.restartPage();
			};
		}
	}

	//
	//
	//

	public static String LBL(String propName, String lbl) {
		return propName + MARK_DEL + lbl;
	}

	public static String LBL_PREVVL(String prevVal) {
//			return " " + STR.COL_DEL + " " + prevVal;
//			return " " + SYMJ.EQUALS + " " + prevVal;
//			return " " + SYMJ.LAMP + prevVal;
//		return " " + SYMJ.EXCLAM_RED + prevVal;
		return " " + SYMJ.GLOB + prevVal;
	}


}
