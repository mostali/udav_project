package zk_notes.factory;

import lombok.Data;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.json.UGson;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.pare.Pare;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_com.core.IZComFadeIO;
import zk_com.core.IZWin;
import zk_form.events.DefAction;
import zk_form.head.StdHeadLib;
import zk_notes.coms.NoteTbxm;
import zk_notes.events.ANMF;
import zk_notes.node.core.NVT;
import zk_os.AppZosProps;
import zk_os.db.net.WebUsr;
import zk_os.sec.UO;
import zk_os.sec.SecMan;
import zk_page.*;
import zk_notes.node.NodeDir;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.ObjState;
import zk_notes.types.HostProfileContract;

import java.nio.file.Path;
import java.util.List;

public class NFOpen extends NFCreate {

	public static final Logger L = LoggerFactory.getLogger(NFOpen.class);

	public static DefAction getDefCloseHideAction(NodeDir formIdentity) {
		DefAction closeAction = (e) -> formIdentity.state().set_STATE(null);
		return closeAction;
	}

	public static void on_checkBusinessLogic_HPE(Path nodePropsPath) {
		if (!UFS.existFile(nodePropsPath)) {
			return;
		}
		String data = RW.readString(nodePropsPath);
		boolean gson = UGson.isGson(data);
		if (gson) {
			HostProfileContract hostProfileContract = HostProfileContract.of(data, null);
			if (L.isInfoEnabled()) {
				L.info("HostProfileContract was loaded:" + hostProfileContract);
			}
		}
	}


	public static Window openFormRequired(String nodeName, Pare sdn, Component... parent) {
		return openFormRequired(NodeDir.ofNodeName(sdn, nodeName), parent);
	}

	public static Window openFormRequired(NodeDir nodeDir, Component... parent) {
		return openNoteWinImpl(nodeDir, true, false, true, parent);
	}

	public static Window openFormRequiredNoPersist(NodeDir nodeDir, Component... parent) {
		return openNoteWinImpl(nodeDir, false, false, true, parent);
	}


	public static Window openForm(NodeDir nodeDir, Component... parent) {
		return openNoteWinImpl(nodeDir, true, false, false, parent);
	}

//	public static Window openNoteWin(NodeDir nodeDir, Component... parent) {
//		return openNoteWinImpl(nodeDir, true, false, false, parent);
//	}

	//
	private static Window openNoteWinImpl(NodeDir nodeDir, boolean persistState, boolean showFilename, boolean requiredOpen, Component... parent) {
		OptsOpenNode optsON = new OptsOpenNode();
		optsON.persistState = persistState;
		optsON.showFilename = showFilename;
		optsON.requiredOpen = requiredOpen;
		return openNoteWin(nodeDir, optsON, parent);
	}

	@Data
	public static class OptsOpenNode {

		public static NodeFileTransferMan.AddNewForm.OptsAdd DEF = NodeFileTransferMan.AddNewForm.OptsAdd.newOpts();
		public boolean persistState = false;
		public boolean showFilename = false;
		public boolean requiredOpen = false;
		//		public boolean withBeahviors = false;
		public boolean isOnQView = false;

	}

	public static Window openNoteWin(NodeDir nodeDir, OptsOpenNode opts, Component... parent) {

		boolean persistState = opts.persistState;

		boolean skipOpen = persistState && !opts.requiredOpen && nodeDir.state().get_STATE(null) != null;

		if (skipOpen) {
			return null;
		}

		if (persistState) {
			persistState = nodeDir.state().isAllowedAccess_EDIT();
		}

		NVT nvt = nodeDir.nvt(NVT.TEXT);

		if (persistState) {
			String existState = nodeDir.state().get_STATE(null);
			if (X.notEquals(existState, nvt.name())) {
				nodeDir.state().set_STATE(nvt);
			}
		}

		ObjState formState = nodeDir.state();

		switch (nvt) {

			case WYSIWYG:
			case TEXT:
			case HTML_WIN:
			case MD_WIN:

				NodeCom nodeWin = NodeCom.of(nodeDir);

				NodeCom nodeCom = nodeWin.mode(Window.Mode.OVERLAPPED).opts(opts).absMode(true);

				Pare<HtmlBasedComponent, Window> comTextWin = nodeCom.buildAndAppendChildIn(parent);

				return IT.NN(comTextWin.val(), "except window");

			case DIR:
			case TREE_NODE:
			case HTML:
			case MD:
			case CODE:

				//show as self

				IZWin comText = NFCreate.createForm(nodeDir, nvt, NodeDir.Behaviour.Absolute);

				HtmlBasedComponent targetCom = (HtmlBasedComponent) comText;

				ObjState.Position pos = nodeDir.state().fields().get_POSITION(ObjState.Position.ABS);

				switch (pos) {
					case ABS:
						comText.absolute();
						formState.apply(targetCom, ObjState.TOP_LEFT);
						formState.apply(targetCom, ObjState.WIDTH_HEIGHT);

				}


				formState.apply(targetCom, ObjState.PK_TITLE);
				formState.apply(targetCom, ObjState.PK_TITLEX);

				if (nvt == NVT.CODE) {
					StdHeadLib.PRETTYFY_JS.addToPage();
				}

				HtmlBasedComponent firstWindow = (HtmlBasedComponent) ARG.toDef(() -> ZKC.getFirstWindow(), parent);

				if (UO.isAllowed_EDIT(nodeDir)) {
					Menupopup0 menu = comText.getOrCreateMenupopup(firstWindow);
					ANMF.applyNolCom(menu, nodeDir);
				}

				firstWindow.appendChild(targetCom);

				if (comText instanceof Window) {
					return (Window) comText;
				}

				Window window = comText._showInWindow();

				if (AppZosProps.APD_UI_EFFECTS_ENABLE.getValueOrDefault()) {
					IZComFadeIO.addEffectInImpl(window);
				}

				return window;

			default:
				throw new WhatIsTypeException(nvt);
		}

	}

	public static boolean openFormInit(NodeDir nodeDir, Component... parent) {
		//form state may be opened - check & open
		if (nodeDir.state().is_STATE_CLOSED()) {
			return false;
		}
		NFOpen.openFormRequired(nodeDir, parent);
		return true;

	}

	//
	//
	//

	public static void openFormOrCloseToggle(NodeDir nodeDir, Component... parent) {

		if (!UO.VIEW.isAllowed(nodeDir)) {
			if (L.isInfoEnabled()) {
				L.info("openFormOrCloseToggle not allowed for user [{}]", WebUsr.login());
			}
			return;
		}

//		if (UO.EDIT.isAllowed(nodeDir)) {
//		} else if (UO.VIEW.isAllowed(nodeDir)) {
//			NFOpen.openFormRequiredNoPersist(nodeDir, ZKC.getFirstWindow());
//		}

		boolean persistState = SecMan.isAllowedEditPlane(nodeDir.sdn());

		IZCom detachedCom = closeCom(nodeDir, persistState);

		if (detachedCom != null) {
			return; //closed
		}

		if (persistState) {
			NFOpen.openFormRequired(nodeDir, parent);
		} else {
			NFOpen.openFormRequiredNoPersist(nodeDir, parent);
		}


	}

	public static @Nullable IZCom closeCom(NodeDir nodeDir, boolean persist) {
		IZCom node = nodeFormAny(nodeDir);
		if (node != null) {
			if (node instanceof NoteTbxm) {
				((NoteTbxm) node).detachNodeCom(persist);
			} else {
				((Component) node).detach();
			}
		}
		return node;
	}

	private static IZCom nodeFormAny(NodeDir nodeDir) {
		List<Component> allNodeCom = ZKNFinder.findAllNodeCom(true, false);
//		return (NoteTbxm) allNodeCom.stream().filter(c -> isComNodeTbxm(c, nodeDir)).findFirst().orElseGet(() -> null);
		String nodeName = nodeDir.nodeName();
		return allNodeCom.stream().filter(c -> c instanceof IZCom).map(c -> (IZCom) c).filter(c -> nodeName.equals(c.getFormName())).findFirst().orElseGet(() -> null);
	}

	@Deprecated
	private static NoteTbxm nodeTbxm(NodeDir nodeDir) {
		List<Component> allNodeCom = ZKNFinder.findAllNodeCom(true, false);
		return (NoteTbxm) allNodeCom.stream().filter(c -> isComNodeTbxm(c, nodeDir)).findFirst().orElseGet(() -> null);
	}

	@Deprecated
	private static boolean isComNodeTbxm(Component c, NodeDir nodeDir) {
		return c instanceof NoteTbxm && ((NoteTbxm) c).getNodeDir().equals(nodeDir);
	}

	//
	//
	//

}
