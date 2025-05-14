package zk_notes.control;

import lombok.Data;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.json.UGson;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Window;
import zk_com.core.IZWin;
import zk_form.events.DefAction;
import zk_form.head.StdHeadLib;
import zk_notes.coms.*;
import zk_notes.events.ANM;
import zk_os.sec.SecMan;
import zk_page.*;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.FormState;
import zk_notes.types.HostProfileContract;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.List;

public class NodeFactory extends NodeFactoryCom {

	public static final Logger L = LoggerFactory.getLogger(NodeFactory.class);

	public static DefAction getDefCloseHideAction(NodeDir formIdentity) {
		DefAction closeAction = (e) -> formIdentity.state().set_STATE(null);
		return closeAction;
	}

	public static void on_checkBusinessLogic_HPE(Path nodePropsPath) {
		if (!UFS.existFile(nodePropsPath)) {
			return;
		}
		String data = RW.readContent(nodePropsPath);
		boolean gson = UGson.isGson(data);
		if (gson) {
			HostProfileContract hostProfileContract = HostProfileContract.of(data, null);
			if (L.isInfoEnabled()) {
				L.info("HostProfileContract was loaded:" + hostProfileContract);
			}
		}

	}


	public static Window openNoteWin_unstateless(String nodeName, Pare sdn, Component... parent) {
		return openNoteWin_unstateless(NodeDir.ofNodeName(sdn, nodeName), parent);
	}

	public static Window openNoteWin_unstateless(NodeDir nodeDir, Component... parent) {
		return openNoteWin(nodeDir, true, false, true, parent);
	}

	public static Window openNoteWin(NodeDir nodeDir, Component... parent) {
		return openNoteWin(nodeDir, true, false, false, parent);
	}

	//
	private static Window openNoteWin(NodeDir nodeDir, boolean persistState, boolean showFilename, boolean skipCloseCheck, Component... parent) {
		OptsOpenNode optsON = new OptsOpenNode();
		optsON.persistState = persistState;
		optsON.showFilename = showFilename;
		optsON.skipCloseCheck = skipCloseCheck;
		return openNoteWin(nodeDir, optsON, parent);
	}

	@Data
	public static class OptsOpenNode {
		public static NodeFileTransferMan.AddNewForm.OptsAdd DEF = new NodeFileTransferMan.AddNewForm.OptsAdd();
		public boolean persistState = false;
		public boolean showFilename = false;
		public boolean skipCloseCheck = false;
		public boolean isOnQView = false;
	}

	public static Window openNoteWin(NodeDir nodeDir, OptsOpenNode opts, Component... parent) {

		boolean persistState = opts.persistState;

		if (persistState && !opts.skipCloseCheck && check_AndIsClose(nodeDir)) {
			return null;
		}

		if (persistState) {
			persistState = nodeDir.state().isAllowedAccess_EDIT();
		}

		NodeDir.NVT nvt = nodeDir.nvt(NodeDir.NVT.TEXT);

		if (persistState) {
			String existState = nodeDir.state().get_STATE(null);
			if (X.notEquals(existState, nvt.name())) {
				nodeDir.state().set_STATE(nvt);
			}
		}

		FormState formState = nodeDir.state();

		switch (nvt) {
			case WYSIWYG:
			case TEXT:
			case HTML_WIN:
			case MD_WIN:
				//need apply prop's
				break;

			case HTML:
			case MD:
			case PRETTYCODE:

				//show as self

				IZWin comText = NodeFactoryCom.createForm_Text(nodeDir, nvt);

				HtmlBasedComponent comXml = (HtmlBasedComponent) comText;

				if (nodeDir.state().fields().get_FIXED_orTrue()) {

					comText.absolute();
					formState.apply_TOP_LEFT(comXml);
					formState.apply_WIDTH_HEIGHT(comXml);

				}


				formState.apply_TITLE(comXml);
				formState.apply_TITLEX(comXml);

				if (nvt == NodeDir.NVT.PRETTYCODE) {
					StdHeadLib.PRETTYFY_JS.addToPage();
				}

				HtmlBasedComponent firstWindow = (HtmlBasedComponent) ARG.toDef(() -> ZKC.getFirstWindow(), parent);

				if (SecMan.isAllowedEdit(nodeDir)) {
					ANM.applyMenu_SimpleViewCom_EDIT(comText.getOrCreateMenupopup(firstWindow), nodeDir);
				}

				firstWindow.appendChild(comXml);

				if (comText instanceof Window) {
					return (Window) comText;
				}

				return comText._showInWindow();

			default:
				throw new WhatIsTypeException(nvt);
		}

		NodeCom nodeWin = NodeCom.of(nodeDir);

		NodeCom nodeCom = nodeWin.mode(Window.Mode.OVERLAPPED).opts(opts).absMode(true);

		Pare<HtmlBasedComponent, Window> comTextWin = nodeCom.buildAndAppendChildIn(parent);

		return IT.NN(comTextWin.val(), "except window");
	}

	private static boolean check_AndIsClose(NodeDir nodeDir) {
		FormState formState = nodeDir.state();
		String state = formState.get_STATE(null);
		if (state != null) {
			List<NoteTbxm> notesTbxWins = ZKNFinder.findAllWindowComInRoots_andRemove(NoteTbxm.class, w -> w.fParent().equals(nodeDir.toPath()), true);
			List<SeNoteTbxm> notesSeWins = ZKNFinder.findAllWindowComInRoots_andRemove(SeNoteTbxm.class, w -> w.fParent().equals(nodeDir.toPath()), true);
			if (L.isDebugEnabled()) {
				L.debug("Com '%s' removed*{}*{} = {} / {}", X.sizeOf(notesTbxWins), X.sizeOf(notesSeWins), notesTbxWins, notesSeWins);
			}
			formState.set_STATE(null);
			return true;
		}
		return false;
	}


	public static boolean openFormIdentitySinglyAsWin0_ifNotClosed(NodeDir nodeDir, Component... parent) {
		//form state may be opened - check & open
		if (nodeDir.state().is_STATE_CLOSED()) {
			return false;
		}
		NodeFactory.openNoteWin_unstateless(nodeDir, parent);
		return true;

	}
}
