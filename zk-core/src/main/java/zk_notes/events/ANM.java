package zk_notes.events;

import lombok.RequiredArgsConstructor;
import mpc.env.APP;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.EXT;
import mpc.fs.path.IPath;
import mpc.str.sym.SYMJ;
import mpc.types.enums.EOnOff;
import mpc.types.enums.EnDis;
import mpe.core.P;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.func.Function2;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import zk_com.base.*;
import zk_notes.ANI;
import zk_notes.AppNotes;
import zk_notes.apiv1.client.LocalNoteApi;
import zk_notes.control.MovePostionCom;
import zk_notes.control.TopFixedPanel;
import zk_notes.node_srv.core.NodeActionIO;
import zk_notes.node_srv.core.NodeEvalType;
import zk_notes.node_state.FormState;
import zk_notes.node_state.libs.PlaneState;
import zk_os.AFC;
import zk_os.AFCC;
import zk_os.core.NodeData;
import zk_os.core.Sdn;
import zk_os.sec.Sec;
import zk_page.*;
import zk_page.index.RSPath;
import zk_notes.node.NodeDir;
import zk_notes.control.NotesSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SerializableEventListener;
import utl_ssh.RTSession;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.ColorPicker;
import zk_form.events.Tbx2_CfrmSerializableEventListener;
import zk_form.events.Tbx_CfrmSerializableEventListener;
import zk_form.notify.ZKI;
import zk_os.sec.SecMan;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.types.HostProfileContract;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//AppNotesMenu
public class ANM extends ANM0 {

	public static final String MARK_DEL = " " + SYMJ.ARROW_RIGHT_SPEC + " ";

	public static void applyFormLink(Menupopup0 menu, NodeDir nodeDir) {

		Path nodeDirPath = nodeDir.toPath();

		String nodeName = nodeDir.nodeName();

		Pare sdn = nodeDir.sdn();

		FormState formState = FormState.ofFormDirOrCreate(sdn, nodeDirPath);

		boolean anonim = SecMan.isAnonim();
		boolean allowedView = formState.isAllowedAccess_VIEW_EDIT();
		boolean allowedEdit = formState.isAllowedAccess_EDIT();
		boolean isAdminOrOwner = SecMan.isAdminOrOwner();

		if (allowedEdit) {

			NodeEvalType nodeEvalType = nodeDir.evalType(false, null);
			if (nodeEvalType != null) {
				NodeActionIO.applyFormMenu(menu, nodeDir);
				menu.add_______();
			}

			menu.addMI(SYMJ.ARROW_UPDOWN + " Move Position", (e) -> MovePostionCom.show(nodeDir));
			menu.addMI(SYMJ.FILE_IMG2 + " Choice Color", (Event e) -> ZKC.getFirstWindow().appendChild(new ColorPicker() {
				@Override
				public void onCLICK_COLOR(Event event, String parentName, String colorCode) {
					FormState.ofFormName_OrCreate(sdn, nodeName).set(FormState.BG_COLOR, colorCode);
					removeMe();
					NotesSpace.rerenderFirst();
				}
			}));


			{
				Menupopup0 innerSpecify = menu.addInnerMenu(SYMJ.MENU_V + " Special view..");
				{// ACTUAL
					String prop = CN.ACTUAL;
					boolean defIfNotPresent = true;
					boolean hasEnable = formState.hasPropEnable(prop, defIfNotPresent);
					String lbl0 = EOnOff.valueOf(!hasEnable).nameCap();
					innerSpecify.addMI(LBL(prop, lbl0), ANM_Mark.buildEventPropChanger_BOOL_SWAP(nodeDir, prop, defIfNotPresent));
				}
				{// ACTUAL
					String prop = CN.BIG;
					boolean defIfNotPresent = true;
					boolean hasEnable = formState.hasPropEnable(prop, defIfNotPresent);
					String lbl0 = EOnOff.valueOf(!hasEnable).nameCap();
					innerSpecify.addMI(LBL(prop, lbl0), ANM_Mark.buildEventPropChanger_BOOL_SWAP(nodeDir, prop, defIfNotPresent));
				}
				{//FIX
					FormState.Fields nodeFields = formState.fields();
					Supplier<String> get = () -> SYMJ.FIX + (nodeFields.get_FIXED_orTrue() ? SYMJ.OK_GREEN : "") + " Fix on page";
					innerSpecify.addMI(get.get(), (e) -> {
						nodeFields.set_FIXED(!nodeFields.get_FIXED());
//					TopFixedPanel.findFirst().replaceWith(new TopFixedPanel());
						NotesSpace.rerenderFirst();
						Menuitem key = (Menuitem) e.getTarget();
						key.setLabel(get.get());
					});
				}
			}
		}

		if (allowedEdit) {

			ANM.applyMenu_EditAs(menu, nodeDir);

			ANM.applyMenu_OpenAs(menu, nodeDir);

			AppEventsFD.applyEvent_OPENDIR(menu, nodeDir.toPath());

			menu.add_______();

			Mi miCopyNotes = Tbx_CfrmSerializableEventListener.toMenuItemComponent("Do copy '" + nodeName + "'", ANI.COPY + "Copy", nodeName, "Set new Notes name", (String newNotesName) -> {
				Path newNotesPath = AppNotes.getPathOfFormNote_PPI(newNotesName);
				if (UFS.exist(newNotesPath)) {
					ZKI.alert("Notes '%s' already exist", newNotesName);
				} else {
					UFS_BASE.COPY.copyDirectory(nodeDirPath, newNotesPath.getParent());
					NotesSpace.rerenderFirst();
				}
				return null;
			});
			menu.addMI_Href(miCopyNotes);

			//
			Function<String, Object> moveItemFunc = (String newNodeName) -> {
				NodeFileTransferMan.moveItemNote(NodeDir.ofNodeName(sdn, nodeName), NodeDir.ofNodeName(sdn, newNodeName));
				ZKI.infoSingleLine("File '%s' renamed successfully to '%s'", nodeName, newNodeName);
				ZKR.restartPage();
				return null;
			};

			menu.addMI_Cfm1(ANI.MOVE_FILE + " Rename item", "set new name", nodeName, moveItemFunc);

			//MOVE ITEM
			AppPageEvents.applyEvent_MoveFormToOtherPage(menu, sdn, formState.formName());
		}

		if (allowedEdit) {
			menu.addMI_DeleteNode_WithSec(SYMJ.FILE_BASKET_MAN + " Delete Item", nodeDir);
			menu.addMI_MovePageNode_WithSec(nodeDir);
		}

//		menupopup.addContextMenuSeparator();
//		menupopup.addMenuitem(ANI.LOGVIEW + "Log View", e -> LogFileView.openSingly(pathFile.toString()));

		if (allowedEdit) {
			applyMenu_FormFileItem(menu, nodeDir);
		}

		if (isAdminOrOwner) {
			ANM_Exec.applyExecMenu(menu, nodeDir);
		}


		if (!anonim) { // EXEC HOST PROFILE

			HostProfileContract hpc = HostProfileContract.of(formState.readFcData(null), null);
			if (hpc != null) {

				menu.add_______();

				Function2<String, String, Object> func = (cmdExec, jsonProfile) -> {

					HostProfileContract hostProfileContract = HostProfileContract.of(jsonProfile, null);

					if (hostProfileContract == null) {
						ZKI.alert("HostProfileInvalid");
					}

					RTSession rts = new RTSession(hpc.getHost(), hpc.getPort(), hpc.getLogin(), hpc.getPass());
					Pare<Integer, Queue> integerQueuePare = rts.execSudo(cmdExec, false);

					List collect = (List) integerQueuePare.val().stream().collect(Collectors.toList());
					ZKI.infoEditorBw(collect);
					return collect;
				};
				Mi menuItemComponent = Tbx2_CfrmSerializableEventListener.toMI(ANI.VIEW_MODE + "Host Exec", "open session", ARR.of("", ""), ARR.of("", ""), func);
				menu.addMI_Href(menuItemComponent);

			}
		}

	}


	public static void applyPlaneLink(Menupopup0 menu, String sd3) {

//		SdTree sdTree = SdTree.of(sdn);
//		TreeState state = sdTree.state();
//		String curVal = state.get(ISecState.SECE, null);
//		boolean userEqualsByLoginOrAlias = SecMan.isUserEqualsByLoginOrAlias(sdn.key());

		if (Sec.isAnonim()) {
			return;
		}

		PlaneState formState = FormState.ofPlaneState_orCreate(sd3);
		boolean allowedEdit = formState.isAllowedAccess_EDIT();
		if (!allowedEdit) {
			return;
		}

		menu.addMI_EDITOR("Edit plane state", formState.pathFc(), true, EXT.JSON);


		if (!X.emptyFile_NotExist(formState.pathFc())) {
			menu.add_______();
		}

		ANM_Mark.applySecurityItems(menu, formState);

		menu.add_______();

		Path rpaSd3 = AFC.PLANES.getRpaPlaneDir(sd3);

		menu.addMI_DeleteFile_WithSec("Delete plane", rpaSd3);

		FunctionV1<String> func = (newPlaneName) -> {
			NodeFileTransferMan.movePlane(sd3, newPlaneName);
			if (Sdn.isEmptySd3()) {
				ZKR.restartPage();
			} else {
				RSPath.toPlane_Redirect(newPlaneName);
			}
		};

		menu.addMI_RenameFile_Cfrm(rpaSd3.toString(), func);


	}


	public static void applyPageLink(Menupopup0 menu, Pare<String, String> sdn, boolean... showLinkToPage) {

//		SdTree sdTree = SdTree.of(sdn);
//		TreeState state = sdTree.state();
//		String curVal = state.get(ISecState.SECE, null);
//		boolean userEqualsByLoginOrAlias = SecMan.isUserEqualsByLoginOrAlias(sdn.key());

		if (ARG.isDefEqTrue(showLinkToPage)) {
			menu.addMI_Href_v1(SYMJ.ARROW_WN + "Open Page", RSPath.PAGE.toPlanPage(sdn), true);
		}

		if (Sec.isAnonim()) {
			return;
		}

		FormState pageState = FormState.ofPageState_orCreate(sdn);
		boolean allowedEdit = pageState.isAllowedAccess_EDIT();
		if (!allowedEdit) {
			return;
		}

		FormState.Fields pageFields = pageState.fields();

		Path pageStatePath = pageState.pathFc();

		AppPageEvents.applyEvent_EditPageProps(menu, pageStatePath);

		menu.add_______();

		Path pageDir = AFC.PAGES.getDir(sdn);

		{//FIX
			Supplier<String> get = () -> SYMJ.FIX + (pageFields.get_FIXED() ? SYMJ.OK_GREEN : "") + " Fix on top";
			menu.addMI(get.get(), (e) -> {
				pageFields.set_FIXED(!pageFields.get_FIXED());
				TopFixedPanel.findFirst().replaceWith(new TopFixedPanel());
				Menuitem key = (Menuitem) e.getTarget();
				key.setLabel(get.get());
			});
		}


		{//RENAME
			FunctionV1 functionV1 = (newPagename) -> {
				if (Sdn.isEmptySd3()) {
					RSPath.toPlanPage_Redirect(sdn.keyStr(), newPagename.toString());
				} else {
					RSPath.toPlane_Redirect(sdn.keyStr());
				}
			};
			menu.addMI_RenameFileDirect(pageDir, functionV1);
		}

		{//DELETE
			menu.addMI_DeleteFile_WithSec(ANI.DELETE_ENTITY + " Delete page", pageDir, false);
		}

		{
			AppEventsFD.applyEvent_OPENDIR_OS(menu, pageDir);
		}
		{
			AppEventsFD.applyEvent_OPENDIR(menu, pageDir);
		}
		{
			AppPageEvents.applyEvent_MovePageToOtherPlane(menu, sdn);
		}
		menu.add_______(true);

		ANM_Mark.applySecurityItems(menu, pageState);

		menu.add_______();

		AppEventsZip.applyEvent_ZipPage(menu, sdn);
		AppEventsZip.applyEvent_DownloadZipPage(menu, sdn);
		AppEventsZip.applyEvent_RecoveryPageFromArchive(menu, sdn);

	}

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

	public static void applyMenu_EditAs(Menupopup0 menu, NodeDir nodeDir) {
		Menupopup0 innerMenu = menu.addInnerMenu(ANI.EDIT_MODE + " Edit data..");
		innerMenu.add_______();
		innerMenu.addMI(ANI.EDIT_MODE + " Edit Data", e -> ZKI.infoEditorBw(nodeDir.getPathFc()));
		innerMenu.addMI_EDITOR(ANI.EDIT_MODE + " Edit Props", nodeDir.state().pathProps(), true, EXT.JSON);
		innerMenu.addMI_EDITOR(ANI.EDIT_MODE + " Edit Props (Com)", nodeDir.state().pathPropsCom(), false, EXT.JSON);
	}

	public static void applyMenu_OpenAs(Menupopup0 menu, NodeDir nodeDir) {
		menu.add_______();
		menu.addMI(ANI.OPEN_SE_EDITOR + " Html View (Modal)", e -> ZKM.showModal(nodeDir.nodeName(), Xml.ofFile(nodeDir.getPathFc())));
	}

	public static void applyMenu_Mark(Menupopup0 markMenu, NodeDir nodeDir) {
		ANM_Mark.applyMenu_Mark(markMenu, nodeDir);
	}

	public static void applyMenu_SimpleViewCom_EDIT(Menupopup0 menupopup, NodeDir nodeDir) {
		boolean allowedEdit = SecMan.isAllowedEdit(nodeDir);
		if (!allowedEdit) {
			return;
		}
		menupopup.addMI_DeleteNode_WithSec(ANI.DELETE_ENTITY + " Remove node '" + nodeDir.nodeName() + "'", nodeDir);


		menupopup.addMI_EDITOR(ANI.EDIT_MODE + " Edit props", nodeDir.state().pathProps(), true, EXT.JSON);


		menupopup.add_______();

		AppEventsFD.applyEvent_OPENDIR(menupopup, nodeDir.toPath());

		ANM_Mark.applySecurityItems(menupopup, nodeDir.state());

	}

	public static class ANM_Exec {
		@RequiredArgsConstructor
		public enum ExeType {
			SH(CN.BASH), PY(CN.PYTHON3);
			final String cmdKey;
		}

		private static void applyExecMenu(Menupopup0 menu, NodeDir node) {
			FormState formState = node.state();

			menu.add_______();

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

		String url2item = new LocalNoteApi().zApiUrl.GET_toItem(node.toItemPath());
		String url2event = new LocalNoteApi().zApiUrl.GET_toEvent(node.nodeID(), "get", "k", null);

		menu.addMI(SYMJ.LINK + " Copy link to rest", e -> ZKJS.eval(X.f("copyToClb('%s')", url2item)));

		{
			if (Sec.isAdminOrOwner()) {
				if (node.getPathFcParentLs().size() > 2 || IPath.ofd(node.getPathFc().getParent()).dMapExt(ARR.EMPTY_MAP).hasOnly(EXT.PROPS, EXT.PROPS$$)) {
					Path pathFc = node.getPathFc();
					String dldUrl = APP.getAppHost() + "/_adi/.planes" + AFCC.relativizeAppFile(pathFc);
					menu.addMI(SYMJ.LINK + " Copy link to download API", e -> ZKJS.eval(X.f("copyToClb('%s')", dldUrl)));
				}
			}
		}
		menu.addMI(SYMJ.LINK + " Copy link to rest", e -> ZKJS.eval(X.f("copyToClb('%s')", url2item)));
		menu.addMI(SYMJ.LINK + " Copy link to rest event", e -> ZKJS.eval(X.f("copyToClb('%s')", url2event)));
		menu.addMI(SYMJ.LINK + " Copy nodeID", e -> ZKJS.eval(X.f("copyToClb('%s')", node.nodeId())));
		menu.addMI(SYMJ.LINK + " Copy node data injected", e -> ZKM_Editor.openEditorText(node.nodeId(), NodeData.of(node).fillIfEmpty(null).nodeData));

		menu.add_______();

		menu.addMI(ANI.DOWNLOAD + " Download As File", e -> ZKR.download(node.state().pathFc()));
		menu.addMeI_CopyCurlBashScriptToClipboard_I_Bash(node.nodeName());
	}

	public static class ANM_Mark {
		public static void applyMenu_Mark(Menupopup0 mainMenu, NodeDir nodeDir) {

			FormState formState = nodeDir.state();

			// VIEW
			mainMenu.add_______(true);

			Pare<Menu, Menupopup0> menus = mainMenu.addInnerMenu0(SYMJ.EYE + " More View's", true);
			Arrays.stream(NodeDir.NVT.values()).forEach(nvt -> {
				String lbl = "Change to '" + nvt.nameHu() + "'" + LBL_PREVVL(formState.get_VIEW(null));
				Menupopup0 m = nvt.isExt() ? menus.val() : mainMenu;
				m.addMI(LBL(FormState.PK_VIEW, lbl), (SerializableEventListener) event -> {
					formState.fields().set_VIEW(nvt);
					ZKR.restartPage();
				});
			});
			mainMenu.appendChild(menus.key());
			mainMenu.add_______();

			applySecurityItems(mainMenu, formState);
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

		private static EventListener buildEventPropChanger_BOOL_SWAP(NodeDir nodeDir, String prop, boolean defIfNotPresent) {
			return (SerializableEventListener) event -> {
				boolean hasProp = nodeDir.state().hasPropEnable(prop, defIfNotPresent);
				nodeDir.state().set(prop, !hasProp);
				ZKR.restartPage();
			};
		}

		private static EventListener buildEventPropChanger_VALUE(NodeDir nodeDir, String prop, Object value) {
			return (SerializableEventListener) event -> {
				nodeDir.state().set(prop, value);
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
