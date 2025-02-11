package zk_notes.control;

import lombok.Data;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.json.UGson;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.RW;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Window;
import zk_com.base.Xml;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_com.core.IZWin;
import zk_form.control.ErrLb;
import zk_form.events.DefAction;
import zk_notes.events.ANM;
import zk_notes.coms.*;
import zk_os.AFC;
import zk_os.AFCC;
import zk_os.sec.Sec;
import zk_page.*;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.FormState;
import zk_notes.types.HostProfileContract;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeFactory extends NodeFactoryCom {

	public static final Logger L = LoggerFactory.getLogger(NodeFactory.class);

	public static DefAction getDefCloseHideAction(NodeDir formIdentity) {
		DefAction closeAction = (e) -> formIdentity.state().set_STATE(null);
		return closeAction;
	}

	public static Map<NodeDir, Component> buildPageComMap(Pare sd3pn, HtmlBasedComponent parent, boolean renderHead) {
		Set<Path> set = AFC.FORMS.DIR_FORMS_LS_CLEAN(sd3pn);
		return set.stream().map(nodePath -> {

			String nodeName = nodePath.getFileName().toString();

			AFCC.FileType fileType = AFCC.FileType.of(nodeName, null);

			NodeDir nodeDir = NodeDir.ofDir(sd3pn, nodePath);
			if (fileType != null) {
				String fileData = FormState.ofFormDirOrCreate(sd3pn, nodePath).readFcData(null);
				switch (fileType) {
					case XML:
						Xml xml = Xml.ofXml(fileData);
						return Pare.of(nodeDir, xml);
					case HEAD_AFTER:
						ZKC.getFirstPageCtrl().addAfterHeadTags(fileData);
						return null;
					case HEAD_BEFORE:
						ZKC.getFirstPageCtrl().addBeforeHeadTags(fileData);
						return null;
				}
			}

			Pare<NodeDir, Component> buildedCom = NodeDir.buildSingleCom(parent, nodeDir);

			return Pare.of(nodeDir, buildedCom.val());

		}).filter(i -> i != null).collect(//
				Collectors.toMap(nd -> nd.key(), //
						nd -> nd.val(), //
						(v1, v2) -> X.throwException("who is master? %s vs %s", v1, v2), //
						LinkedHashMap::new) //
		);//
	}

	public static Pare<NodeDir, Component> buildSingleComImpl(HtmlBasedComponent parent, NodeDir node) {

		FormState stateForm = node.state();
		boolean allowedForEdit = stateForm.isAllowedAccess_Edit();
		boolean allowedForView = stateForm.isAllowedAccess_View_Edit();
		if (!allowedForView && !allowedForEdit) {
			return Pare.of(node, new ErrLb("view=" + allowedForView + ", edit=" + allowedForEdit));
		}

		NodeDir.NVM nmt = node.nvm_first(null);
		if (nmt != null) {
			switch (nmt) {
				case IMG: {
					IZCom com = createForm_Img(node);

					com.draggablePersistenseForm(node.nodeName());

					boolean allowedAccessEdit = com.getComState_JSON().isAllowedAccess_Edit();
					if (allowedAccessEdit) {
						Menupopup0 menupopup = com.getOrCreateMenupopup(ZKC.getFirstWindow());
						ANM.applyMenu_NodeImg(menupopup, node);
					}

					return (Pare) Pare.of(node, com);
				}
				case AUDIO: {
					IZCom com = createForm_MultiAudio(node);
					com.draggablePersistenseForm(node.nodeName());
					return Pare.of(node, (Component) com);
				}
				case VIDEO: {
					IZCom com = createForm_Video(node);
					com.draggablePersistenseForm(node.nodeName());
					return Pare.of(node, (Component) com);
				}
				default:
					throw new WhatIsTypeException(nmt);
			}
		}


		boolean isActual = stateForm.hasPropEnable(CN.ACTUAL, true);

		NodeLn nodeLn = isActual ? new NodeLn(parent, node) : new NodeLn(parent, node, true);

		stateForm.stateCom().apply_TITLE(nodeLn);
		stateForm.stateCom().apply_TITLEX(nodeLn);

		return Pare.of(node, nodeLn);

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


	public static Window openNoteWin_Opened(String nodeName, Pare sdn) {
		return openNoteWin_Opened(NodeDir.ofNodeName(sdn, nodeName));
	}

	public static Window openNoteWin_Opened(NodeDir nodeDir) {
		return openNoteWin(nodeDir, true, false, true);
	}

	public static Window openNoteWin(NodeDir nodeDir) {
		return openNoteWin(nodeDir, true, false, false);
	}

	//
	private static Window openNoteWin(NodeDir nodeDir, boolean persistState, boolean showFilename, boolean skipCloseCheck) {
		OptsOpenNode optsON = new OptsOpenNode();
		optsON.persistState = persistState;
		optsON.showFilename = showFilename;
		optsON.skipCloseCheck = skipCloseCheck;
		return openNoteWin(nodeDir, optsON);
	}

	@Data
	public static class OptsOpenNode {
		public static NodeFileTransferMan.AddNewForm.OptsAdd DEF = new NodeFileTransferMan.AddNewForm.OptsAdd();
		public boolean persistState = false;
		public boolean showFilename = false;
		public boolean skipCloseCheck = false;
		public boolean isOnQView = false;
	}

	public static Window openNoteWin(NodeDir nodeDir, OptsOpenNode opts) {

		boolean persistState = opts.persistState;

		if (persistState && !opts.skipCloseCheck && check_AndIsClose(nodeDir)) {
			return null;
		}

		if (persistState) {
			persistState = nodeDir.state().isAllowedAccess_Edit();
		}

		NodeDir.NVT nvt = nodeDir.nvt(NodeDir.NVT.TEXT);

		if (persistState) {
			String existState = nodeDir.state().get_STATE(null);
			if (X.notEquals(existState, nvt.name())) {
				nodeDir.state().set_STATE(nvt);
			}
		}

		FormState stateForm = nodeDir.state();

		boolean isEditorAdminOwner = Sec.isEditorAdminOwner();

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

				comText.absolute();

				stateForm.apply_TOP_LEFT(comXml);
				stateForm.apply_WIDTH_HEIGHT(comXml);
				stateForm.apply_TITLE(comXml);
				stateForm.apply_TITLEX(comXml);

				ZKC.getFirstWindow().appendChild(comXml);

				return null;

			default:
				throw new WhatIsTypeException(nvt);
		}

		NodeCom nodeWin = NodeCom.of(nodeDir);

		Pare<HtmlBasedComponent, Window> comTextWin = nodeWin.mode(Window.Mode.OVERLAPPED).opts(opts).absMode(true).buildAndAppendChildIn();

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


	public static boolean openFormIdentitySinglyAsWin0_ifNotClosed(NodeDir nodeDir, boolean onQView) {
		//form state may be opened - check & open
		if (nodeDir.state().is_STATE_CLOSED()) {
			return false;
		}
		NodeFactory.openNoteWin_Opened(nodeDir);
		return true;

	}
}
