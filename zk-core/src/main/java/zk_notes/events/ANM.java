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
import zk_notes.apiv1.client.NoteApi0;
import zk_notes.node.core.NVT;
import zk_notes.node_state.ObjState;
import zk_os.coms.AFCC;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecApp;
import zk_os.sec.SecMan;
import zk_page.*;
import zk_notes.node.NodeDir;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base_ctr.Menupopup0;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

//AppNotesMenu
public class ANM extends ANMFd {

	public static final String MARK_DEL = " " + SYMJ.ARROW_RIGHT_SPEC + " ";

	public static void applyMenu_Upload(Menupopup0 uploadMenu, Pare sdn) {
		Path file = AFCC.getFormDirBlank(sdn, "file-", 3);
		uploadMenu.addMI_UploadTo(SYMJ.UPLOAD + " File..", file);
		uploadMenu.addMI_UploadDdTo(SYMJ.UPLOAD + " File Drag & Drop..", file);

		Path video = AFCC.getFormDirBlank(sdn, "video-", 3);
		uploadMenu.addMI_UploadTo(SYMJ.UPLOAD + " Video..", video);

		Path image = AFCC.getFormDirBlank(sdn, "img-", 3);
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
		menu.addMI_EDITOR(SYMJ.PUZZLE + " Edit Form Data", nodeDir.getProxyRW(false).getTargetAnyPath(), true, null);
//		menu.addMI_EDITOR(ANI.EDIT_MODE + " Edit Form Props*", nodeDir.state().pathPropsCom(), false, EXT.JSON);
		menu.addMI_EDITOR(ANI.PROPS_ZZZ + " Edit Form Props", nodeDir.getPath_FormFc_Props(), false, EXT.JSON);
		menu.add_______(true);
		menu.addMI_EDITOR(ANI.PROPS_ZZZ + " Edit Link Props", nodeDir.getPath_ComFc(), false, EXT.JSON);
	}

	public static void applyMenu_SimpleViewAs(Menupopup0 menu, NodeDir nodeDir) {
		Menupopup0 menuViewAs = menu.addInnerMenu(SYMJ.THINK_EYE + " Simple View As..");
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
			ObjState formState = node.state();

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
			if (SecMan.isOwnerOrAdmin()) {
				if (node.getPathFcParentLs().size() > 2 || IPath.ofDirExisted(node.getPathFc().getParent()).dMapExt(ARR.EMPTY_MAP).hasOnly(EXT.PROPS, EXT.PROPS$$)) {
					Path pathFc = node.getPathFc();
					String appHost0 = APP.APP_HOST;// APP.HOST.getAppHost0();
					String dldUrl = appHost0 + "/" + ApiCase._adi + "/.planes" + AFCC.relativizeAppFile(pathFc);
					menu.addMI(SYMJ.LINK + " Copy link to download file via API", e -> ZKJS.eval(X.f("copyToClb('%s')", dldUrl)));
				}
			}
		}
		menu.addMI(SYMJ.LINK + " Copy link to rest", e -> ZKJS.eval(X.f("copyToClb('%s')", url2item)));
		menu.addMI(SYMJ.LINK + " Copy link to rest event", e -> ZKJS.eval(X.f("copyToClb('%s')", url2event)));
		menu.addMeI_CopyCurlBashScriptToClipboard_I_Bash(node.nodeName());
		menu.add_______();
		menu.addMI(SYMJ.LINK + " Copy nodeID", e -> ZKJS.eval(X.f("copyToClb('%s')", node.nodeId())));
		menu.addMI(SYMJ.LINK + " Copy node data injected", e -> ZKME.textReadonly(node.nodeId(), node.injectStr()));
		menu.add_______();
		menu.addMI(ANI.DOWNLOAD + " Download As File", e -> ZKR.download(node.state().pathFc()));
		menu.addMI(SYMJ.LINK + " Copy node file pth", e -> ZKJS.eval(X.f("copyToClb('%s')", node.getPath_FormFc_Data())));

	}

	public static class ANM_Mark {
		public static void applyMenu_Mark(Menupopup0 mainMenu, NodeDir nodeDir) {

			ObjState formState = nodeDir.state();

			Pare<Menu, Menupopup0> menus = mainMenu.addInnerMenu0(SYMJ.EYE + " More View's", true);
			Arrays.stream(NVT.values()).forEach(nvt -> {
				String lbl = "Change to '" + nvt.nameHu() + "'" + LBL_PREVVL(formState.get_VIEW(null));
				Menupopup0 m = nvt.isPrimaryInMenu() ? menus.val() : mainMenu;
				m.addMI(LBL_PROP(ObjState.PK_VIEW, lbl), (SerializableEventListener) event -> {
					formState.fields().set_VIEW(nvt);
					ZKR.restartPage();
				});
			});
			mainMenu.appendChild(menus.key());
		}

		public static void applySecurityItems(Menupopup0 mainMenu, ObjState formState) {

			Menupopup0 accessMenu = mainMenu.addInnerMenu(SYMJ.SHIELD + " Security Access..");

			addEnableDisableProp_SEC(accessMenu, formState, SecApp.SECV);
			addEnableDisableProp_SEC(accessMenu, formState, SecApp.SECE);
			addEnableDisableProp_SEC(accessMenu, formState, SecApp.SECR);
//			markMenu.addSeparator();
			addEnableDisableProp_USER(accessMenu, formState, SecApp.USER);
		}

		private static void addEnableDisableProp_USER(Menupopup0 markMenu, ObjState formState, String prop) {
			String curUser = formState.get_USER(null);
			String login = WebUsr.login();
			String lbl = X.empty(curUser) ? "Set me (" + login + ")" : " Allowed for all ";
			lbl += LBL_PREVVL(curUser);
			markMenu.addMI(LBL_PROP(SecApp.USER, lbl), buildEventPropChanger_REMOVE_or_SET_STATIC_VALUE(formState, prop, login));
		}

		private static void addEnableDisableProp_SEC(Menupopup0 markMenu, ObjState formState, String prop) {
			boolean hasPropNotEmpty = formState.hasPropNotEmpty(prop);
			String lbl = EnDis.valueOf(!hasPropNotEmpty).nameCap() + " for all" + LBL_PREVVL(formState.get(prop, null));
			markMenu.addMI(LBL_PROP(prop, lbl), buildEventPropChanger_REMOVE_or_SET_STATIC_VALUE(formState, prop, SecApp.SECFORALL));
		}

		public static EventListener buildEventPropChanger_BOOL_SWAP(ObjState state, String prop, boolean defIfNotPresent) {
			return (SerializableEventListener) event -> {
				boolean hasProp = state.hasPropEnable(prop, defIfNotPresent);
				state.set(prop, !hasProp);
				ZKR.restartPage();
			};
		}

		private static EventListener buildEventPropChanger_VALUE(NodeDir nodeDir, String prop, Object value) {
			return (SerializableEventListener) event -> {
				nodeDir.state().set(prop, value);
				ZKR.restartPage();
			};
		}

		private static EventListener buildEventPropChanger_REMOVE_or_SET_STATIC_VALUE(ObjState state, String prop, Object defValue) {
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

	public static String LBL_PROP(String propName, String lbl) {
		return propName + MARK_DEL + lbl;
	}

	public static String LBL_PREVVL(String prevVal) {
//			return " " + STR.COL_DEL + " " + prevVal;
//			return " " + SYMJ.EQUALS + " " + prevVal;
//			return " " + SYMJ.LAMP + prevVal;
//		return " " + SYMJ.EXCLAM_RED + prevVal;
		return " " + SYMJ.GLOB_RED + prevVal;
	}


}
