package zk_notes;

import lombok.RequiredArgsConstructor;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.EXT;
import mpc.str.sym.SYMJ;
import mpc.types.enums.EOnOff;
import mpc.types.enums.EnDis;
import mpe.core.P;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARR;
import mpu.func.Function2;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Window;
import zk_form.notify.ZKI_Modal;
import zk_notes.apiv1.client.NoteApi;
import zk_notes.coms.SingleNodeImg;
import zk_os.AFC;
import zk_os.sec.Sec;
import zk_page.*;
import zk_page.core.SpVM;
import zk_page.index.RSPath;
import zk_page.index.Sd3DdChoicer;
import zk_page.node.NodeDir;
import zk_notes.control.NotesSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SerializableEventListener;
import utl_ssh.RTSession;
import zk_com.base.Mi;
import zk_com.base.Xml;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.ThemeColorPicker;
import zk_form.events.Tbx2_CfrmSerializableEventListener;
import zk_form.events.Tbx_CfrmSerializableEventListener;
import zk_form.notify.ZKI;
import zk_os.sec.SecMan;
import zk_page.node.fsman.NodeFileTransferMan;
import zk_page.node_state.*;
import zk_notes.types.HostProfileContract;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

//AppNotesMenu
public class ANM extends ANM0 {

	public static final String MARK_DEL = " " + SYMJ.ARROW_RIGHT_SPEC + " ";

	public static void applyNotesPageLink(Menupopup0 menu, NodeDir nodeDir) {

		Path nodeDirPath = nodeDir.fPath();

		String nodeName = nodeDir.nodeName();

		Pare sdn = nodeDir.sdn();

		FormState formState = FormState.ofFormDir(sdn, nodeDirPath);

		boolean anonim = SecMan.isAnonim();
		boolean allowedView = formState.isAllowedAccess_View_Edit();
		boolean allowedEdit = formState.isAllowedAccess_Edit();
		boolean isAdminOrOwner = SecMan.isAdminOrOwner();

		if (allowedEdit) {
			menu.addMenuitem(SYMJ.FILE_IMG2 + " Choice Color", (Event e) -> ZKC.getFirstWindow().appendChild(new ThemeColorPicker() {
				@Override
				public void onCLICK_COLOR(Event event, String parentName, String colorCode) {
					FormState.ofFormName(nodeName, sdn).update(FormState.BG_COLOR, colorCode);
					removeMe();
					NotesSpace.rerenderFirst();
				}
			}));
		}

		if (allowedEdit) {

			menu.add_______();

			menu.addMenuitem(ANI.EDIT_MODE + " Edit Props", e -> ZKME.openEditorJson(formState.pathProps(), true));

			Function<String, Object> stringFunctionV1 = (String newNodeName) -> {
				NodeFileTransferMan.moveItemNote(NodeDir.ofNodeName(nodeName, sdn), NodeDir.ofNodeName(newNodeName, sdn));
				ZKI.infoSingleLine("File '%s' renamed successfully to '%s'", nodeName, newNodeName);
				ZKR.restartPage();
				return null;
			};

			menu.addMenuitem_Cfm1(SYMJ.EDIT_PENCIL + " Rename item", "set new name", nodeName, stringFunctionV1);
		}

		if (allowedEdit) {
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
			menu.addMenuitem(miCopyNotes);
		}

		if (allowedEdit) {
			menu.addMenuitem_DeleteNode_WithSec(SYMJ.FILE_BASKET_MAN + " Delete Item", nodeDir);
			menu.addMenuitem_MovePageNode_WithSec(nodeDir);
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
				Mi menuItemComponent = Tbx2_CfrmSerializableEventListener.toMenuItemComponent(ANI.VIEW_MODE + "Host Exec", "open session", ARR.of("", ""), ARR.of("", ""), func);
				menu.addMenuitem(menuItemComponent);

			}
		}

		if (allowedView) {
//			menu.addSeparator();
			menu.addMenuitem(ANI.DOWNLOAD + " Download As File", e -> ZKR.download(formState.pathFc()));
			menu.addMenuitem_CopyCurlBashScriptToClipboard_I_Bash(formState.formName());
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

		FormState formState = FormState.ofPlaneState(sd3);
		boolean allowedEdit = formState.isAllowedAccess_Edit();
		if (!allowedEdit) {
			return;
		}

		menu.addMenuitem_EDITOR("Edit plane state", formState.pathFc(), true, EXT.JSON);


		if (!X.emptyFile_NotExist(formState.pathFc())) {
			menu.add_______();
		}

		ANM_Mark.addSecurityItems(menu, formState);

		menu.add_______();

		Path rpaSd3 = AFC.getRpaPlaneDir(sd3);

		menu.addMenuitem_DeleteFile_WithSec("Delete plane", rpaSd3);

		FunctionV1<String> func = (newPlaneName) -> {
			NodeFileTransferMan.movePlane(sd3, newPlaneName);
			if (X.empty(SpVM.get().subdomain3())) {
				ZKR.restartPage();
			} else {
				RSPath.toPlane_Redirect(newPlaneName);
			}
		};

		menu.addMenuitem_RenameFile_Cfrm(rpaSd3.toString(), func);


	}


	public static void applyPageLink(Menupopup0 menu, Pare<String, String> sdn) {

//		SdTree sdTree = SdTree.of(sdn);
//		TreeState state = sdTree.state();
//		String curVal = state.get(ISecState.SECE, null);
//		boolean userEqualsByLoginOrAlias = SecMan.isUserEqualsByLoginOrAlias(sdn.key());

		if (Sec.isAnonim()) {
			return;
		}

		FormState formState = FormState.ofPageState(sdn);
		boolean allowedEdit = formState.isAllowedAccess_Edit();
		if (!allowedEdit) {
			return;
		}

		menu.add_______();
		menu.addMenuitem_EDITOR("Edit page state", formState.pathFc(), true, EXT.JSON);


		if (!X.emptyFile_NotExist(formState.pathFc())) {
			menu.add_______();
		}

		ANM_Mark.addSecurityItems(menu, formState);

		menu.add_______();

		Path rpaSd3Page = AFC.getRpaPageDir(sdn);

		FunctionV1 functionV1 = (newPagename) -> {
			if (X.empty(SpVM.get().subdomain3())) {
				RSPath.toPlanPage_Redirect(sdn.keyStr(), newPagename.toString());
			} else {
				RSPath.toPlane_Redirect(sdn.keyStr());
			}
			return;
		};
		menu.addMenuitem_RenameFileDirect(rpaSd3Page, functionV1);

		menu.addMenuitem_DeleteFile_WithSec("Delete page", rpaSd3Page);

		menu.addMenuitem("Move to other plane", (e) -> {

			AtomicReference<Window> winRef = new AtomicReference<>();

			String title = X.f("Move page '%s'", sdn.val());

			Sd3DdChoicer child = new Sd3DdChoicer() {
				@Override
				public void onChoicePath(String dstSd3) {
					String sd3 = sdn.key();
					String pagename = sdn.val();
					Function<Boolean, Void> func = (rslt) -> {
						if (rslt) {
							NodeFileTransferMan.movePageToSd3(sd3, pagename, dstSd3);
							if (X.empty(SpVM.get().subdomain3())) {
								RSPath.toPlane_Redirect(dstSd3);
							} else {
								RSPath.toPlanPage_Redirect(dstSd3, pagename);
							}
						}
						return null;
					};
					String message = X.f("Move page '%s' to plane '%s'", pagename, dstSd3);
					ZKI_Modal.showMessageBoxBlueYN(title, message, func);
					winRef.get().onClose();
				}
			};


			Window window = child._title(title)._closable()._modal()._popup()._showInWindow();
			winRef.set(window);

		});

	}


	public static void applyMenu_Upload(Menupopup0 uploadMenu, Pare sdn) {
		Path file = AppNotes.getRpaForms_BlankDir(sdn, "file-", 3);
		uploadMenu.addMenuitem_UploadTo("File..", file, false);

		Path video = AppNotes.getRpaForms_BlankDir(sdn, "video-", 3);
		uploadMenu.addMenuitem_UploadTo("Video..", video, false);

		Path image = AppNotes.getRpaForms_BlankDir(sdn, "img-", 3);
		Function<String, Boolean> successClb = s -> {
			P.p(s);
			return false;
		};
		uploadMenu.addMenuitem_UploadTo("Image..", image, true, successClb);
	}

	public static void applyMenu_OpenAs(Menupopup0 menuOpenAs, NodeDir nodeDir) {
		menuOpenAs.addMenuitem(ANI.OPEN_SE_EDITOR + " Html View (Modal)", e -> ZKM.showModal(nodeDir.nodeName(), Xml.ofFile(nodeDir.getPathFc())));
		menuOpenAs.addMenuitem(ANI.EDIT_MODE + " Text Editor", e -> ZKI.infoEditorBw(nodeDir.getPathFc()));
	}

	public static void applyMenu_Mark(Menupopup0 markMenu, NodeDir nodeDir) {
		ANM_Mark.applyMenu_Mark(markMenu, nodeDir);
	}

	public static void applyMenu_SingleNodeImg(Menupopup0 menu, SingleNodeImg singleNodeImg) {
		SingleNodeImg.addContextMenu(menu, singleNodeImg.getNodeDir());
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

			String url2item = new NoteApi().zApiUrl.GET_toItem_WithExe(node.toItemPath());
			menu.addMenuitem(SYMJ.LINK + " Copy link to rest", e -> ZKJS.eval(X.f("copyToClb('%s')", url2item)));


			ExeType exeType = (ExeType) formState.getAs(CN.EXE, ExeType.class, null);
			if (exeType == null) {
				return;
			}
			Menupopup0 inMenuExecuteAs = menu.addInnerMenu("Execute As..");
			inMenuExecuteAs.addMenuitem_ExecFileTmp(ANI.EXEC + " " + exeType.cmdKey, exeType.cmdKey, formState.readFcData());
		}
	}

	public static class ANM_Mark {
		public static void applyMenu_Mark(Menupopup0 mainMenu, NodeDir nodeDir) {

			FormState formState = nodeDir.state();

			// ACTUAL
			String prop = CN.ACTUAL;
			boolean defIfNotPresent = true;
			boolean hasEnable = formState.hasPropEnable(prop, defIfNotPresent);
			String lbl0 = EOnOff.valueOf(!hasEnable).nameCap();
			mainMenu.addMenuitem(LBL(prop, lbl0), buildEventPropChanger_BOOL_SWAP(nodeDir, prop, defIfNotPresent));

			// VIEW
			mainMenu.add_______();

			Pare<Menu, Menupopup0> menus = mainMenu.addInnerMenu0(SYMJ.EYE + " More View's", true);
			Arrays.stream(NodeDir.NVT.values()).forEach(nvt -> {
				String lbl = "Change to '" + nvt.nameHu() + "'" + LBL_PREVVL(formState.getProp_VIEW(null));
				Menupopup0 m = nvt.isExt() ? menus.val() : mainMenu;
				m.addMenuitem(LBL(FormState.PK_VIEW, lbl), (SerializableEventListener) event -> {
					formState.updateProp_VIEW(nvt);
					ZKR.restartPage();
				});
			});
			mainMenu.appendChild(menus.key());
			mainMenu.add_______();

			Menupopup0 accessMenu = mainMenu.addInnerMenu("Security Access..");
			addSecurityItems(accessMenu, formState);
		}

		private static void addSecurityItems(Menupopup0 markMenu, FormState formState) {
			addEnableDisableProp_SEC(markMenu, formState, FormState.SECV);
			addEnableDisableProp_SEC(markMenu, formState, FormState.SECE);
//			markMenu.addSeparator();
			addEnableDisableProp_USER(markMenu, formState, FormState.PK_USER);
		}

		private static void addEnableDisableProp_USER(Menupopup0 markMenu, FormState formState, String prop) {
			String curUser = formState.getProp_USER(null);
			String lbl = X.empty(curUser) ? "Set me (" + Sec.login() + ")" : " Allowed for all ";
			lbl += LBL_PREVVL(curUser);
			markMenu.addMenuitem(LBL(FormState.PK_USER, lbl), buildEventPropChanger_REMOVE_or_SET_STATIC_VALUE(formState, prop, Sec.login()));
		}

		private static void addEnableDisableProp_SEC(Menupopup0 markMenu, FormState formState, String prop) {
			boolean hasPropNotEmpty = formState.hasPropNotEmpty(prop);
			String lbl = EnDis.valueOf(!hasPropNotEmpty).nameCap() + " for all" + LBL_PREVVL(formState.get(prop, null));
			markMenu.addMenuitem(LBL(prop, lbl), buildEventPropChanger_REMOVE_or_SET_STATIC_VALUE(formState, prop, SecMan.SECFORALL));
		}

		private static EventListener buildEventPropChanger_BOOL_SWAP(NodeDir nodeDir, String prop, boolean defIfNotPresent) {
			return (SerializableEventListener) event -> {
				boolean hasProp = nodeDir.state().hasPropEnable(prop, defIfNotPresent);
				nodeDir.state().update(prop, !hasProp);
				ZKR.restartPage();
			};
		}

		private static EventListener buildEventPropChanger_VALUE(NodeDir nodeDir, String prop, Object value) {
			return (SerializableEventListener) event -> {
				nodeDir.state().update(prop, value);
				ZKR.restartPage();
			};
		}

		private static EventListener buildEventPropChanger_REMOVE_or_SET_STATIC_VALUE(FormState state, String prop, Object defValue) {
			return (SerializableEventListener) event -> {
				boolean hasPropNotEmpty = state.hasPropNotEmpty(prop);
				state.update(prop, hasPropNotEmpty ? null : defValue);
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
