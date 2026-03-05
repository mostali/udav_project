package zk_notes.events;

import mp.utl_odb.netapp.AppCore;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.str.sym.SYMJ;
import mpc.types.OptLazySupplier;
import mpc.types.enums.EOnOff;
import mpe.cmsg.core.INodeType;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARR;
import mpu.func.Function2;
import mpu.func.FunctionV3;
import mpu.func.FunctionV4;
import mpu.pare.Pare;
import mpu.str.JOIN;
import mpu.str.UST;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Menuitem;
import utl_ssh.RTSession;
import zk_com.base.Mi;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.ColorPicker;
import zk_form.events.Tbxm2_CfrmSEL;
import zk_form.events.Tbx_CfrmSEL;
import zk_form.notify.ZKI;
import zk_notes.ANI;
import zk_notes.control.MovePostionCom;
import zk_notes.control.NotesSpace;
import zk_notes.std_actions.WebNodeAction;
import zk_notes.node.NodeDir;
import zk_notes.factory.NFTrans;
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
import java.util.concurrent.atomic.AtomicBoolean;
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

			INodeType nodeEvalType = nodeDir.evalType( null);
			if (nodeEvalType != null) {
				applyFormMenu(menu, nodeDir);
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

			Mi miCopyNotes = Tbx_CfrmSEL.toMenuItemComponent("Do copy '" + nodeName + "'", SYMJ.COPY + " Copy item", nodeName, "Set new Notes name", (String newNotesName) -> {
				if (NFTrans.COPY.doCopyItem(newNotesName, nodeDir)) {
					NotesSpace.rerenderFirst();
				}
				return null;
			});
			menu.addMI_Href_in_Self(miCopyNotes);

			//
			Function<String, Object> moveItemFunc = (String newNodeName) -> {
				NFTrans.moveItemNote(NodeDir.ofNodeName(sdn, nodeName), NodeDir.ofNodeName(sdn, newNodeName));
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
				Mi menuItemComponent = Tbxm2_CfrmSEL.toMI(ANI.VIEW_MODE + "Host Exec", "open session", ARR.of("", ""), ARR.of("", ""), func);
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

		OptLazySupplier<Menupopup0> getterVerisonPanel = new OptLazySupplier(() -> innerSpecify.addInnerMenu(SYMJ.MENU_V + " Versioning.."));

		AtomicBoolean boolIsVersion = new AtomicBoolean(false);

		FunctionV4<String, Boolean, Boolean, String> adderBool0 = (prop, defIfNotPresent, isCom, msg) -> {
			ObjState objStateUsed = isCom ? formStateCom : formState;
			boolean hasEnable = objStateUsed.hasPropEnable(prop, defIfNotPresent);
			String lbl0 = EOnOff.valueOf(hasEnable).nameCap();
			Pare<Menuitem, Menupopup0> menuitemMenupopup0Pare = (boolIsVersion.get() ? getterVerisonPanel.get() : innerSpecify).addMI(ANM.LBL_PROP(prop, lbl0), ANM.ANM_Mark.buildEventPropChanger_BOOL_SWAP(objStateUsed, prop, defIfNotPresent));
			menuitemMenupopup0Pare.key().setTooltiptext(msg);
		};
		FunctionV3<String, Boolean, String> adderBoolCom = (prop, defIfNotPresent, msg) -> {
			adderBool0.apply(prop, defIfNotPresent, true, msg);
		};
		FunctionV3<String, Boolean, String> adderBoolForm = (prop, defIfNotPresent, msg) ->
		{
			adderBool0.apply(prop, defIfNotPresent, false, msg);
		};

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

		adderBoolCom.apply(EntityState.LINK_VISIBLE, true, "Видимость компонента ссылки");

		innerSpecify.add_______();

		adderBoolCom.apply(EntityState.BODY_TOGGLE, false, "Открывать/закрывать заметку по клику на заголовке");
		adderBoolCom.apply(EntityState.BODY_VISIBLE, true, "Скрывать содержимое заметки - показывать только шапку");
		adderBoolCom.apply(EntityState.BODY_OPENIFHIDE, false, "Если заметка скрыта - открывать при наведении на шапку");


		innerSpecify.add_______();

		// DEPRECATED
		adderBoolCom.apply(EntityState.DEPRECATED, false, "Зачеркнвает компонент ссылки");

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
			String key = FormState.FK_FROM_FILE;
			String link2file = formState.get(key, null);
			String setFontSize = ANM.LBL_PROP(key, link2file);
			innerSpecify.addMI_Cfm1(setFontSize, "Set file source", link2file, (in) -> {
				formState.set(key, X.empty(in) ? null : in);
				NotesSpace.rerenderFirst();
				return null;
			});
		}
		{// LINK2DIR
			String key = FormState.FK_FROM_DIR;
			String link2file = formState.get(key, null);
			String setFontSize = ANM.LBL_PROP(key, link2file);
			innerSpecify.addMI_Cfm1(setFontSize, "Set file source", link2file, (in) -> {
				formState.set(key, X.empty(in) ? null : in);
				NotesSpace.rerenderFirst();
				return null;
			});
		}


		innerSpecify.add_______();


		{//

			{
				boolIsVersion.set(true);
				adderBoolForm.apply(EntityState.FK_VERSIONED, true, "Версионирование");
				boolIsVersion.set(false);//todo
			}

			{
				getterVerisonPanel.get().addMI("Show last", (in) -> {
					List list = AppCore.__VERSIONED_LS(nodeDir.nodeId());
					ZKI.infoAfterPointer(JOIN.allByComma(list));
				});
				getterVerisonPanel.get().addMI_Cfm1(SYMJ.ARROW_RIGHT_SPEC + " Set Version", "Set Version", "", (in) -> {
					ICtxDb.CtxModel ctxModel = AppCore.__VERSIONED_GET(UST.INT(in));
					if (ctxModel == null) {
						ZKI.alert("Not found version '%s'. Allowed [" + JOIN.allByComma(AppCore.__VERSIONED_LS(nodeDir.nodeId())) + "]", in);
						return null;
					}
					nodeDir.getProxyRW(false).writeContent(ctxModel.getValue());
					NotesSpace.rerenderFirst();
					return null;
				});
			}
		}

		innerSpecify.add_______();

		{//POSITION

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

	}


	public static void applyFormMenu(Menupopup0 menu, NodeDir node) {
		INodeType nodeEvalType = node.evalType();
		menu.addMI(nodeEvalType.stdProps().titleWithIcon(), e -> WebNodeAction.of(node).doAction( null));
	}
}
