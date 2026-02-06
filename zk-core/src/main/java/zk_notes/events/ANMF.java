package zk_notes.events;

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
import zk_notes.control.MovePostionCom;
import zk_notes.control.NotesSpace;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.NodeActionIO;
import zk_notes.node_srv.NodeEvalType;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.EntityState;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.FormState;
import zk_notes.types.HostProfileContract;
import zk_os.sec.UO;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.ZKR;

import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

//AppNotesMenu
public class ANMF {

	public static void applyNolCom(Menupopup0 menu, NodeDir nodeDir, boolean... isBinaryComWoNodeLn) {
		applyForm(menu, nodeDir, isBinaryComWoNodeLn);
	}

	public static void applyNolCom0(Menupopup0 menu, NodeDir nodeDir, boolean... isBinaryComWoNodeLn) {

		boolean allowedEdit = UO.isAllowed_EDIT(nodeDir);
		if (!allowedEdit) {
			return;
		}

		menu.addMI(SYMJ.ARROW_UPDOWN + " Move Position/Width", (e) -> MovePostionCom.show(nodeDir, isBinaryComWoNodeLn));
		menu.add_______();

		menu.addMI_DeleteNode_WithSec(ANI.DELETE_ENTITY + " Remove node '" + nodeDir.nodeName() + "'", nodeDir);

		ANM.applyMenu_EditAs(menu, nodeDir, false);
		menu.add_______();

		ANM.ANM_Mark.applySecurityItems(menu, nodeDir.state());
		menu.add_______();

		ANM.applyMenu_Mark(menu, nodeDir);
		menu.add_______();

		ANMFd.applyMenu_FdOperations(menu, nodeDir);

//		ANM.ANM_Mark.applySecurityItems(menu, nodeDir.state());

	}

	public static void applyFormSec(Menupopup0 menu, NodeDir nodeDir, boolean... isBinaryComWoNodeLn) {
		if (SecMan.isNotAnonimUnsafe()) {
			applyForm(menu, nodeDir, isBinaryComWoNodeLn);
		}
	}

	public static void applyForm(Menupopup0 menu, NodeDir nodeDir, boolean... isBinaryComWoNodeLn) {

//		Path nodeDirPath = nodeDir.toPath();

		String nodeName = nodeDir.nodeName();

		Pare sdn = nodeDir.sdnPare();

//		FormState formState = AppStateFactory.ofFormDir_orCreate(sdn, nodeDirPath);
		ObjState formState = nodeDir.state();
		ObjState formStateCom = nodeDir.stateCom();

//		boolean anonim = SecMan.isAnonim();
//		boolean allowedView = formState.isAllowedAccess_VIEW_EDIT();
		boolean allowedEdit = formState.isAllowedAccess_EDIT();
//		boolean isAdminOrOwner = SecMan.isOwnerOrAdmin();

		if (allowedEdit) {

			NodeEvalType nodeEvalType = nodeDir.evalType(false, null);
			if (nodeEvalType != null) {
				NodeActionIO.applyFormMenu(menu, nodeDir);
				menu.add_______();
			}

			menu.addMI(SYMJ.ARROW_UPDOWN + " Move Position/Width", (e) -> MovePostionCom.show(nodeDir, isBinaryComWoNodeLn));
			menu.add_______();

			ANM.applyMenu_EditAs(menu, nodeDir, true);
			menu.add_______();

			applyMenu_Behaviours(menu, nodeDir);
			menu.add_______();

			ANM.ANM_Mark.applySecurityItems(menu, nodeDir.state());
			menu.add_______();

			Mi miCopyNotes = Tbx_CfrmSerializableEventListener.toMenuItemComponent("Do copy '" + nodeName + "'", SYMJ.COPY + " Copy item", nodeName, "Set new Notes name", (String newNotesName) -> {
				if (NodeFileTransferMan.COPY.doCopyItem(newNotesName, nodeDir)) {
					NotesSpace.rerenderFirst();
				}
				return null;
			});
			menu.addMI_Href_in_Self(miCopyNotes);

			//
			Function<String, Object> moveItemFunc = (String newNodeName) -> {
				NodeFileTransferMan.moveItemNote(NodeDir.ofNodeName(sdn, nodeName), NodeDir.ofNodeName(sdn, newNodeName));
				ZKI.infoSingleLine("File '%s' renamed successfully to '%s'", nodeName, newNodeName);
				ZKR.restartPage();
				return null;
			};

			menu.addMI_Cfm1(ANI.MOVE_FILE + " Rename item", "set new name", nodeName, moveItemFunc);

			menu.addMI_DeleteNode_WithSec(ANI.DELETE_ENTITY + " Delete Item", nodeDir);

			menu.add_______(true);

			//MOVE ITEM
			AppEventsPage.applyEvent_MoveNode2Page(menu, sdn, formState.objName());


			menu.addMI_MoveNode2Plane_WithSec(nodeDir);

			menu.add_______(true);

			ANM.applyMenu_Mark(menu, nodeDir);

			menu.add_______();

			ANM.applyMenu_SimpleViewAs(menu, nodeDir);

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
				menu.addMI_Href_in_Self(menuItemComponent);

			}
		}

	}

	private static void applyMenu_Behaviours(Menupopup0 menu, NodeDir nodeDir) {

		String nodeName = nodeDir.nodeName();

		Pare sdn = nodeDir.sdnPare();

		ObjState formState = nodeDir.state();
		ObjState formStateCom = nodeDir.stateCom();

//		menu.add_______();

		Menupopup0 innerSpecify = menu.addInnerMenu(SYMJ.MENU_V + " Behaviours..");

		innerSpecify.addMI(SYMJ.FILE_IMG2 + " Choice Color - Form", (Event e) -> ZKC.getFirstWindow().appendChild(new ColorPicker() {
			@Override
			public void onCLICK_COLOR(Event event, String parentName, String colorCode) {
				AppStateFactory.forForm(sdn, nodeName).set(ObjState.BG_COLOR, colorCode);
				removeMe();
				NotesSpace.rerenderFirst();
			}
		}));
		innerSpecify.addMI(SYMJ.FILE_IMG2 + " Choice Color - Link", (Event e) -> ZKC.getFirstWindow().appendChild(new ColorPicker() {
			@Override
			public void onCLICK_COLOR(Event event, String parentName, String colorCode) {
				AppStateFactory.forCom(sdn, nodeName).set(ObjState.BG_COLOR, colorCode);
				removeMe();
				NotesSpace.rerenderFirst();
			}
		}));

		innerSpecify.add_______();

		{// VISIBLE
			String prop = EntityState.LINK_VISIBLE;
			boolean defIfNotPresent = true;
			boolean hasEnable = formStateCom.hasPropEnable(prop, defIfNotPresent);
			String lbl0 = EOnOff.valueOf(hasEnable).nameCap();
			Pare<Menuitem, Menupopup0> menuitemMenupopup0Pare = innerSpecify.addMI(ANM.LBL_PROP(prop, lbl0), ANM.ANM_Mark.buildEventPropChanger_BOOL_SWAP(formStateCom, prop, defIfNotPresent));
			menuitemMenupopup0Pare.key().setTooltiptext("Видимость компонента ссылки");

		}

		innerSpecify.add_______();
		{// BODY TOGGLE
			String prop = EntityState.BODY_TOGGLE;
			boolean defIfNotPresent = false;
			boolean hasEnable = formStateCom.hasPropEnable(prop, defIfNotPresent);
			String lbl0 = EOnOff.valueOf(hasEnable).nameCap();
			Pare<Menuitem, Menupopup0> menuitemMenupopup0Pare = innerSpecify.addMI(ANM.LBL_PROP(prop, lbl0), ANM.ANM_Mark.buildEventPropChanger_BOOL_SWAP(formStateCom, prop, defIfNotPresent));
			menuitemMenupopup0Pare.key().setTooltiptext("Открывать/закрывать заметку по клику на заголовке");
		}
		{// BODY_VISIBLE
			String prop = EntityState.BODY_VISIBLE;
			boolean defIfNotPresent = true;
			boolean hasEnable = formStateCom.hasPropEnable(prop, defIfNotPresent);
			String lbl0 = EOnOff.valueOf(hasEnable).nameCap();
			Pare<Menuitem, Menupopup0> menuitemMenupopup0Pare = innerSpecify.addMI(ANM.LBL_PROP(prop, lbl0), ANM.ANM_Mark.buildEventPropChanger_BOOL_SWAP(formStateCom, prop, defIfNotPresent));
			menuitemMenupopup0Pare.key().setTooltiptext("Скрывать содержимое заметки - показывать только шапку");
		}
		{// BODY_VISIBLE
			String prop = EntityState.BODY_OPENIFHIDE;
			boolean defIfNotPresent = false;
			boolean hasEnable = formStateCom.hasPropEnable(prop, defIfNotPresent);
			String lbl0 = EOnOff.valueOf(hasEnable).nameCap();
			Pare<Menuitem, Menupopup0> menuitemMenupopup0Pare = innerSpecify.addMI(ANM.LBL_PROP(prop, lbl0), ANM.ANM_Mark.buildEventPropChanger_BOOL_SWAP(formStateCom, prop, defIfNotPresent));
			menuitemMenupopup0Pare.key().setTooltiptext("Если заметка скрыта - открывать при наведении на шапку");
		}
		innerSpecify.add_______();
		{// DEPRECATED
			String prop = EntityState.DEPRECATED;
			boolean defIfNotPresent = false;
			boolean hasEnable = formStateCom.hasPropEnable(prop, defIfNotPresent);
			String lbl0 = EOnOff.valueOf(hasEnable).nameCap();
			Pare<Menuitem, Menupopup0> menuitemMenupopup0Pare = innerSpecify.addMI(ANM.LBL_PROP(prop, lbl0), ANM.ANM_Mark.buildEventPropChanger_BOOL_SWAP(formStateCom, prop, defIfNotPresent));
			menuitemMenupopup0Pare.key().setTooltiptext("Зачеркнвает компонент ссылки");
		}
		{// FONT-SIZE
			String fontSizeVal = formStateCom.get("font-size", "");
			String fontSize = ANM.LBL_PROP("FontSize", fontSizeVal);
			innerSpecify.addMI_Cfm1(fontSize, "Set New FontSize", fontSizeVal, (in) -> {
				formStateCom.set("font-size", X.empty(in) ? null : in);
				NotesSpace.rerenderFirst();
				return null;
			});
		}
		{// HREF
			String href = formStateCom.get(CN.HREF, null);
			String setFontSize = ANM.LBL_PROP(CN.HREF, href);
			innerSpecify.addMI_Cfm1(setFontSize, "Set new URL", href, (in) -> {
				formStateCom.set(CN.HREF, X.empty(in) ? null : in);
				NotesSpace.rerenderFirst();
				return null;
			});
		}

		{// LINK2FILE
			String key= FormState.FK_FROM_FILE;
			String link2file = formState.get(key, null);
			String setFontSize = ANM.LBL_PROP(key, link2file);
			innerSpecify.addMI_Cfm1(setFontSize, "Set file source", link2file, (in) -> {
				formState.set(key, X.empty(in) ? null : in);
				NotesSpace.rerenderFirst();
				return null;
			});
		}

		innerSpecify.add_______();

		{//POSITION

			{
//				FormState.Position curVal = formState.fields().get_POSITION(FormState.Position.ABS);
//				String curValStr = ANM.LBL_PROP("Position - Form", curVal.name());
//				innerSpecify.addMI_Position(nodeDir);
//				innerSpecify.addMI_Position(nodeDir, true);
			}

			{
//				FormState.Position curVal = formState.fields().get_POSITION(FormState.Position.ABS);
//				String curValStr = ANM.LBL_PROP("Position - Form", curVal.name());
//				innerSpecify.addMI_Position(nodeDir);
			}


			{
				ObjState.Position curVal = formState.fields().get_POSITION(ObjState.Position.ABS);
				String curValStr = ANM.LBL_PROP("Position - Form", curVal.name());
				innerSpecify.addMI_Cfm1(curValStr, "Set Position - Form " + ARR.as(ObjState.Position.values()) + " | Absolute / Fixed / Relative ", curVal.name(), (in) -> {
					formState.fields().set_POSITION(ObjState.Position.valueOf(in));
					NotesSpace.rerenderFirst();
					return null;
				});
			}

			{
				ObjState.Position curVal = formStateCom.fields().get_POSITION(ObjState.Position.ABS);
				String curValStr = ANM.LBL_PROP("Position - Link", curVal.name());
				innerSpecify.addMI_Cfm1(curValStr, "Set Position - Link " + ARR.as(ObjState.Position.values()) + " | Absolute / Fixed / Relative ", curVal.name(), (in) -> {
					formStateCom.fields().set_POSITION(ObjState.Position.valueOf(in));
					NotesSpace.rerenderFirst();
					return null;
				});
			}

		}

//		{//FIX RELATIVE
//			FormState.Fields nodeFields = formState.fields();
//			Supplier<String> get = () -> SYMJ.FIX + (nodeFields.get_RELATIVE(false) ? SYMJ.OK_GREEN : "") + " Relative on page - Form";
//			innerSpecify.addMI(get.get(), (e) -> {
//				nodeFields.set_RELATIVE(!nodeFields.get_RELATIVE(false));
////					TopFixedPanel.findFirst().replaceWith(new TopFixedPanel());
//				NotesSpace.rerenderFirst();
//				Menuitem key = (Menuitem) e.getTarget();
//				key.setLabel(get.get());
//			});
//		}

		//
		//


//		{//FIX FORM
//			FormState.Fields nodeFormFields = formState.fields();
//			Supplier<String> get = () -> SYMJ.FIX + (nodeFormFields.get_FIXED(false) ? SYMJ.OK_GREEN : "") + " Fix on page - Form";
//			innerSpecify.addMI(get.get(), (e) -> {
//				nodeFormFields.set_FIXED(!nodeFormFields.get_FIXED(false));
////					TopFixedPanel.findFirst().replaceWith(new TopFixedPanel());
//				NotesSpace.rerenderFirst();
//				Menuitem key = (Menuitem) e.getTarget();
//				key.setLabel(get.get());
//			});
//		}
//		{//FIX COM
//			FormState.Fields nodeComFields = formState.stateCom().fields();
//			Supplier<String> get = () -> SYMJ.FIX + (nodeComFields.get_FIXED(false) ? SYMJ.OK_GREEN : "") + " Fix on page - Link";
//			innerSpecify.addMI(get.get(), (e) -> {
//				nodeComFields.set_FIXED(!nodeComFields.get_FIXED(false));
////					TopFixedPanel.findFirst().replaceWith(new TopFixedPanel());
//				NotesSpace.rerenderFirst();
//				Menuitem key = (Menuitem) e.getTarget();
//				key.setLabel(get.get());
//			});
//		}
	}


}
