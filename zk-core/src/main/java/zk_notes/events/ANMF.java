package zk_notes.events;

import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.str.sym.SYMJ;
import mpc.types.enums.EOnOff;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARR;
import mpu.func.Function2;
import mpu.pare.Pare;
import mpu.str.STR;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Menuitem;
import utl_ssh.RTSession;
import zk_com.base.Mi;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.ColorPicker;
import zk_form.events.Tbx2_CfrmSerializableEventListener;
import zk_form.events.Tbx_CfrmSerializableEventListener;
import zk_form.notify.ZKI;
import zk_notes.ANI;
import zk_notes.node_state.AppStatePath;
import zk_notes.control.MovePostionCom;
import zk_notes.control.NotesSpace;
import zk_notes.factory.NodeCom;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.core.NodeActionIO;
import zk_notes.node_srv.core.NodeEvalType;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.FormState;
import zk_notes.types.HostProfileContract;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.ZKR;

import java.nio.file.Path;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//AppNotesMenu
public class ANMF {

	public static void applyNolCom(Menupopup0 menu, NodeDir nodeDir, boolean... isBinaryComWoNodeLn) {
		applyForm(menu, nodeDir, isBinaryComWoNodeLn);
	}

	public static void applyNolCom0(Menupopup0 menu, NodeDir nodeDir, boolean... isBinaryComWoNodeLn) {

		boolean allowedEdit = SecMan.isAllowedEdit(nodeDir);
		if (!allowedEdit) {
			return;
		}

		menu.addMI(SYMJ.ARROW_UPDOWN + " Move Position", (e) -> MovePostionCom.show(nodeDir, isBinaryComWoNodeLn));
		menu.add_______();

		menu.addMI_DeleteNode_WithSec(ANI.DELETE_ENTITY + " Remove node '" + nodeDir.nodeName() + "'", nodeDir);

		ANM.applyMenu_EditAs(menu, nodeDir, false);
		menu.add_______(true);

		ANM.applyMenu_Mark(menu, nodeDir);
		menu.add_______();

		ANM.ANM_Mark.applySecurityItems(menu, nodeDir.state());
		menu.add_______();

		ANMFd.applyMenu_FdOperations(menu, nodeDir);

//		ANM.ANM_Mark.applySecurityItems(menu, nodeDir.state());

	}

	public static void applyFormSec(Menupopup0 menu, NodeDir nodeDir, boolean... isBinaryComWoNodeLn) {
		if (Sec.isNotAnonimUnsafe()) {
			applyForm(menu, nodeDir, isBinaryComWoNodeLn);
		}
	}

	public static void applyForm(Menupopup0 menu, NodeDir nodeDir, boolean... isBinaryComWoNodeLn) {

		Path nodeDirPath = nodeDir.toPath();

		String nodeName = nodeDir.nodeName();

		Pare sdn = nodeDir.sdn();

//		FormState formState = AppStateFactory.ofFormDir_orCreate(sdn, nodeDirPath);
		FormState formState = nodeDir.state();
		FormState formStateCom = nodeDir.stateCom();

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

			menu.addMI(SYMJ.ARROW_UPDOWN + " Move Position", (e) -> MovePostionCom.show(nodeDir, isBinaryComWoNodeLn));

			ANM.applyMenu_EditAs(menu, nodeDir, true);

			ANM.applyMenu_OpenAs(menu, nodeDir);

			menu.add_______();

			Mi miCopyNotes = Tbx_CfrmSerializableEventListener.toMenuItemComponent("Do copy '" + nodeName + "'", SYMJ.COPY + "Copy", nodeName, "Set new Notes name", (String newNotesName) -> {
				Path newNotesPath = AppStatePath.getFormDataPath_PPI(newNotesName);
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
			AppEventsPage.applyEvent_MoveFormToOtherPage(menu, sdn, formState.formName());

			menu.addMI_DeleteNode_WithSec(ANI.DELETE_ENTITY + " Delete Item", nodeDir);
			menu.addMI_MovePageNode_WithSec(nodeDir);

			menu.add_______(true);

			ANM.applyMenu_Mark(menu, nodeDir);

			applyMenuSpecialView(menu, nodeDir);

			menu.add_______();
			ANM.ANM_Mark.applySecurityItems(menu, nodeDir.state());

			menu.add_______();

			ANMFd.applyMenu_FdOperations(menu, nodeDir);
		}

		boolean isOwner = SecMan.isOwner();
		if (isOwner) {
			menu.add_______();
			ANM.ANM_Exec.applyExecMenu(menu, nodeDir);
//			menu.add_______();
//			menu.addMI(ANI.LOGVIEW + " Log View", e -> LogFileView.openSingly(.toString()));
		}

		if (isOwner) { // EXEC HOST PROFILE

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
					ZKI.infoEditorDark(collect);
					return collect;
				};
				Mi menuItemComponent = Tbx2_CfrmSerializableEventListener.toMI(ANI.VIEW_MODE + "Host Exec", "open session", ARR.of("", ""), ARR.of("", ""), func);
				menu.addMI_Href(menuItemComponent);

			}
		}

	}

	private static void applyMenuSpecialView(Menupopup0 menu, NodeDir nodeDir) {

		String nodeName = nodeDir.nodeName();

		Pare sdn = nodeDir.sdn();

		FormState formState = nodeDir.state();
		FormState formStateCom = nodeDir.stateCom();

		Menupopup0 innerSpecify = menu.addInnerMenu(SYMJ.MENU_V + " Special view..");

		innerSpecify.addMI(SYMJ.FILE_IMG2 + " Choice Color", (Event e) -> ZKC.getFirstWindow().appendChild(new ColorPicker() {
			@Override
			public void onCLICK_COLOR(Event event, String parentName, String colorCode) {
				AppStateFactory.ofFormName_orCreate(sdn, nodeName).set(FormState.BG_COLOR, colorCode);
				removeMe();
				NotesSpace.rerenderFirst();
			}
		}));

		innerSpecify.add_______();

		{// VISIBLE
			String prop = NodeCom.VISIBLE;
			boolean defIfNotPresent = true;
			boolean hasEnable = formStateCom.hasPropEnable(prop, defIfNotPresent);
			String lbl0 = EOnOff.valueOf(hasEnable).nameCap();
			innerSpecify.addMI(ANM.LBL_PROP(prop, lbl0), ANM.ANM_Mark.buildEventPropChanger_BOOL_SWAP(formStateCom, prop, defIfNotPresent));
		}

		{// DEPRECATED
			String prop = NodeCom.DEPRECATED;
			boolean defIfNotPresent = false;
			boolean hasEnable = formStateCom.hasPropEnable(prop, defIfNotPresent);
			String lbl0 = EOnOff.valueOf(hasEnable).nameCap();
			innerSpecify.addMI(ANM.LBL_PROP(prop, lbl0), ANM.ANM_Mark.buildEventPropChanger_BOOL_SWAP(formStateCom, prop, defIfNotPresent));
		}
		{// FONT-SIZE
			String fontSize = ANM.LBL_PROP("FontSize", formStateCom.get("font-size", ""));
			innerSpecify.addMI_Cfm1(fontSize, "Set New FontSize", fontSize, (in) -> {
				formStateCom.set("font-size", X.empty(in) ? null : in);
				NotesSpace.rerenderFirst();
				return null;
			});
		}
		{// HREF
			String href = formStateCom.get(CN.HREF, null);
			String setFontSize = ANM.LBL_PROP(STR.capitalizeLC(CN.HREF), href);
			innerSpecify.addMI_Cfm1(setFontSize, "Set new URL", href, (in) -> {
				formStateCom.set(CN.HREF, X.empty(in) ? null : in);
				NotesSpace.rerenderFirst();
				return null;
			});
		}

		{//FIX
			FormState.Fields nodeFields = formState.fields();
			Supplier<String> get = () -> SYMJ.FIX + (nodeFields.get_FIXED(true) ? SYMJ.OK_GREEN : "") + " Fix on page";
			innerSpecify.addMI(get.get(), (e) -> {
				nodeFields.set_FIXED(!nodeFields.get_FIXED(false));
//					TopFixedPanel.findFirst().replaceWith(new TopFixedPanel());
				NotesSpace.rerenderFirst();
				Menuitem key = (Menuitem) e.getTarget();
				key.setLabel(get.get());
			});
		}
	}


}
