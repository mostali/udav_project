package zk_notes.control;

import lombok.Data;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.fs.ext.MapExt;
import mpc.json.UGson;
import mpc.str.sym.SYMJ;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zul.Window;
import zk_com.base.Tbx;
import zk_com.base.Xml;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_com.core.IZWin;
import zk_form.control.ErrLb;
import zk_form.events.DefAction;
import zk_form.head.IHeadCom;
import zk_notes.ANM;
import zk_notes.ANMC;
import zk_notes.AppNotesTheme;
import zk_notes.coms.*;
import zk_old_core.coms.GalleryVF;
import zk_os.AFCC;
import zk_os.sec.Sec;
import zk_page.*;
import zk_page.node.NodeDir;
import zk_page.node.fsman.NodeFileTransferMan;
import zk_page.node_state.FormState;
import zk_notes.AppNotes;
import zk_notes.types.HostProfileContract;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeFactory {

	public static final Logger L = LoggerFactory.getLogger(NodeFactory.class);

	public static DefAction getDefCloseHideAction(NodeDir formIdentity) {
		DefAction closeAction = (e) -> formIdentity.state().updateProp_STATE(null);
		return closeAction;
	}

//	@NotNull
//	public static EventListener show_XML_InModal(Pare<String, String> sd3pn, Path nodeDirPath) {
//		Path formpathFile = AFC.getRpaFormStatePath(sd3pn, nodeDirPath.getFileName().toString());
//		EventListener eventListener = e -> {
//			Window window = ZKM.showModal(null, Xml.ofFile(formpathFile.toString()));
//			window.doEmbedded();
//			ZKS.ABSOLUTE(window);
//			//							window.doHighlighted();
//			//							window.doPopup();
//			//							window.doOverlapped();
//			window.setZindex(ZkTheme.MAX_INDEX);
//		};
//		return eventListener;
//	}

	public static Map<NodeDir, Component> buildPageComMap(Pare sd3pn, HtmlBasedComponent parent, boolean renderHead) {
		return AppNotes.getAllNotesOfPage(sd3pn).stream().map(notePath -> {
			String noteName = notePath.getFileName().toString();
			AFCC.FileType fileType = AFCC.FileType.of(noteName, null);
			NodeDir nodeDir = NodeDir.ofDir(notePath, sd3pn);
			if (fileType != null) {
				String fileData = FormState.ofFormDir(sd3pn, notePath).readFcData(null);
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
			Pare<NodeDir, Component> buildedCom = nodeDir.buildSingleCom(parent);
			return Pare.of(nodeDir, buildedCom.val());
		}).filter(i -> i != null).collect(Collectors.toMap(nd -> nd.key(), nd -> nd.val(), (v1, v2) -> X.throwException("who is master? %s vs %s", v1, v2), LinkedHashMap::new));
	}

	public static Pare<NodeDir, Component> buildComMapImpl(HtmlBasedComponent parent, Path nodeDirPath, Pare<String, String> sdn) {

		NodeDir node = NodeDir.ofDir(nodeDirPath, sdn);

		boolean allowedForEdit = node.state().isAllowedAccess_Edit();
		boolean allowedForView = node.state().isAllowedAccess_View_Edit();
		if (!allowedForView && !allowedForEdit) {
			return Pare.of(node, new ErrLb("view=" + allowedForView + ", edit=" + allowedForEdit));
		}

		MapExt mapExt = node.fMapExt();
		Map<GEXT, List<Path>> map = mapExt.gmap();

		if (map.containsKey(GEXT.IMG)) {
			List<Path> files = node.fLsGEXT(GEXT.IMG);
			if (X.sizeOf0(files) > 1) {
				List<String> srcs = (List<String>) UFS.convert(files, String.class);
				GalleryVF galleryVF = new GalleryVF(srcs, false);
				galleryVF.width(200);
				galleryVF.draggablePersistenseForm(node.nodeName());
				ZkPage.renderHeadRsrc_Form((PageCtrl) ZKC.getFirstPage(), (IHeadCom) galleryVF);
				return Pare.of(node, galleryVF);
			} else {
				SingleNodeImg singleNodeImg = new SingleNodeImg(node);
				singleNodeImg.draggablePersistenseForm(node.nodeName());

				boolean allowedAccessEdit = singleNodeImg.getComState_JSON().isAllowedAccess_Edit();
				if (allowedAccessEdit) {
					Menupopup0 menupopup = singleNodeImg.getOrCreateMenupopup(ZKC.getFirstWindow());
					ANM.applyMenu_SingleNodeImg(menupopup, singleNodeImg);
				}
				return Pare.of(node, singleNodeImg);
			}
		}

		if (map.containsKey(GEXT.AUDIO)) {
//			List<Path> audioFiles = map.get(GEXT.AUDIO);
			IZCom com;
//			if (audioFiles.size() == 1) {
//				com = new SingleNodeAudio(nodeDir);
//			} else {
			com = new SingleNodeAudioMulti(node);
//			}
			boolean isAllowedSec = node.state().isAllowedAccess_Edit(true);
//			if (isAllowedSec) {
			com.draggablePersistenseForm(node.nodeName());
//			}
			return Pare.of(node, (Component) com);
		}

		if (map.containsKey(GEXT.VIDEO)) {
			SingleNodeVideo com = new SingleNodeVideo(node);
			com.draggablePersistenseForm(node.nodeName());
			return Pare.of(node, com);
		}

//
////		GEXT typeMedia = nodeDir.typeMedia(null);
//
//		Map.Entry<GEXT, List<Path>> first = ARRi.first(map, null);
//
//		GEXT typeMedia = first != null ? first.getKey() : null;
//
//		if (typeMedia != null) {
//
//			switch (typeMedia) {
//
//				case IMG: {
//
//				}
//
//				case AUDIO:
//
//				case VIDEO:
//
////				case EDITABLE: {
////					//ok show link
////					break;
////				}
//
//				default:
//					return Pare.of(nodeDir, new ErrLb("undefined media note:" + nodeDir.nodeName()));
//
//			}
//	}

		boolean isActual = node.state().hasPropEnable(CN.ACTUAL, true);

		NodeLn nodeLn = isActual ? new NodeLn(parent, node) : new NodeLn(parent, node, true);

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

	public static Window openFormIdentitySinglyAsWin0(NodeDir nodeDir, boolean persistState) {
		return openFormIdentitySinglyAsWin0(nodeDir, persistState, false, false);
	}

	public static Window openFormIdentitySinglyAsWin0_Opened(String nodeName, Pare sdn) {
		return openFormIdentitySinglyAsWin0_Opened(NodeDir.ofNodeName(nodeName, sdn));
	}

	public static Window openFormIdentitySinglyAsWin0_Opened(NodeDir nodeDir) {
		return openFormIdentitySinglyAsWin0(nodeDir, true, false, true);
	}

	//
	public static Window openFormIdentitySinglyAsWin0(NodeDir nodeDir, boolean persistState, boolean showFilename, boolean skipCloseCheck) {
		OptsOpenNode optsON = new OptsOpenNode();
		optsON.persistState = persistState;
		optsON.showFilename = showFilename;
		optsON.skipCloseCheck = skipCloseCheck;
		return openFormIdentitySinglyAsWin0(nodeDir, optsON);
	}

	@Data
	public static class OptsOpenNode {
		public static NodeFileTransferMan.AddNewForm.OptsAdd DEF = new NodeFileTransferMan.AddNewForm.OptsAdd();
		public boolean persistState = false;
		public boolean showFilename = false;
		public boolean skipCloseCheck = false;
		public boolean isOnQView = false;
	}

	public static Window openFormIdentitySinglyAsWin0(NodeDir nodeDir, OptsOpenNode opts) {

		boolean persistState = opts.persistState;
		boolean showFilename = opts.showFilename;
		boolean skipCloseCheck = opts.skipCloseCheck;

		if (persistState && ARG.isDefNotEqTrue(skipCloseCheck) && checkAndIsClose(nodeDir)) {
			return null;
		}

		Pare sdn = nodeDir.sdn();

		if (persistState) {
			persistState = nodeDir.state().isAllowedAccess_Edit();
		}

		DefAction closeAction = persistState ? getDefCloseHideAction(nodeDir) : null;

		NodeDir.NVT nvt = nodeDir.nvt(NodeDir.NVT.TEXT);

		if (persistState) {
			String existState = nodeDir.state().getProp_STATE(null);
			if (X.notEquals(existState, nvt.name())) {
				nodeDir.state().updateProp_STATE(nvt);
			}
		}

		String formname = nodeDir.nodeName();

		boolean isPPI = X.empty(sdn);

		Path pathForm = isPPI ? AppNotes.getPathOfFormNote_PPI(formname) : AppNotes.getPathOfFormNote_NOPPI(sdn, formname);

		FormState stateForm = FormState.ofPathFormFile_orCreate(pathForm, sdn);

		IZWin izWin;

		boolean isEditorAdminOwner = Sec.isEditorAdminOwner();

		switch (nvt) {

			case WYSIWYG:
				SeNoteTbxm seTbx0 = (SeNoteTbxm) new SeNoteTbxm(pathForm, Tbx.DIMS.WH100).json(stateForm.isJson());
				izWin = seTbx0;
				if (isEditorAdminOwner) {
					seTbx0.saveble().saveOnShortCut();
				}
				izWin.placeholder("html");
				break;

			case TEXT: {
				Integer size = (Integer) stateForm.getAs(CN.SIZE, Integer.class, null);

				if (size == null || size < 2) {
					NoteTbxm nodeTbx0 = (NoteTbxm) new NoteTbxm(nodeDir.nodeName(), pathForm, Tbx.DIMS.WH100).prettyjson(stateForm.isJson());
					izWin = nodeTbx0;
					if (isEditorAdminOwner) {
						nodeTbx0.saveOnShortCut();
					}
				} else {
					NoteTbxmy noteTbxmy = new NoteTbxmy(nodeDir.nodeName(), pathForm, size, Tbx.DIMS.WH100);
					izWin = noteTbxmy;
					if (isEditorAdminOwner) {
						noteTbxmy.saveOnShortCut();
					}
				}
				izWin.placeholder("data");
				break;
			}

			case HTML_WIN:
				izWin = Xml.ofFile(pathForm.toString());
				break;

			case HTML:
			case MD:

				Xml com;
				switch (nvt) {
					case HTML:
						com = Xml.ofFile(pathForm.toString());
						break;
					case MD:
						com = Xml.ofMd(pathForm.toString());
						break;
//					case PDF_WIN:
//						com = Xml.ofPdf(pathForm.toString());
//						break;
					default:
						throw new WhatIsTypeException("set impl:" + nvt);

				}
				com.absolute();
				stateForm.apply_TOP_LEFT(com);
				stateForm.apply_WIDTH_HEIGHT(com);
				ZKC.getFirstWindow().appendChild(com);

				return null;

			case MD_WIN:
				izWin = Xml.ofMd(pathForm);
				break;

			default:
				throw new WhatIsTypeException(nvt);
		}

		String title = formname + " ";
		boolean showFN = ARG.isDefEqTrue(showFilename);
		if (showFN) {
			title += SYMJ.ARROW_RIGHT_SPEC + pathForm;
		} else {
			String title0 = stateForm.getProp_TITLE(null);
			if (X.notEmpty(title0)) {
				title += SYMJ.ARROW_RIGHT_SPEC + title0;
			}
		}

		if (isEditorAdminOwner) {
			izWin._sizable()._closable();
		}

		Window seWin = izWin._ovl()._title(title)._showInWindow();

		ANMC.applyWinTbxCap(sdn, izWin, seWin, nodeDir);

		ZKS.PADDING_WIN(seWin, 10, 0);

		if (opts.isOnQView) {
			ZKS.ZINDEX(seWin, AppNotesTheme.ZI_QVIEW_WIN);
		}

		if (closeAction != null) {
			seWin.addEventListener("onClose", (SerializableEventListener<Event>) event -> closeAction.onDefAction(event));
		}

		HtmlBasedComponent applyCom = izWin instanceof NoteTbxm ? (HtmlBasedComponent) izWin : seWin;

		FormState.apply_TopLeft_WidthHeigth_Bg(seWin, applyCom, stateForm);

		if (isEditorAdminOwner) {
			FormState.addEventListenerMoveAndResize(seWin, applyCom, stateForm);
		}

		return seWin;
	}

	private static boolean checkAndIsClose(NodeDir nodeDir) {
		FormState formState = nodeDir.state();
		String state = formState.getProp_STATE(null);
		if (state != null) {
			List<NoteTbxm> notesTbxWins = ZKF.roots0c_remove_wc(NoteTbxm.class, w -> w.fParent().equals(nodeDir.fPath()), true);
			List<SeNoteTbxm> notesSeWins = ZKF.roots0c_remove_wc(SeNoteTbxm.class, w -> w.fParent().equals(nodeDir.fPath()), true);
			if (L.isDebugEnabled()) {
				L.debug("Com '%s' removed*{}*{} = {} / {}", X.sizeOf(notesTbxWins), X.sizeOf(notesSeWins), notesTbxWins, notesSeWins);
			}
			formState.updateProp_STATE(null);
			return true;
		}
		return false;
	}


	public static boolean openFormIdentitySinglyAsWin0_ifNotClosed(NodeDir nodeDir, boolean persistState, boolean onQView) {
		//form state may be opened - check & open
		if (nodeDir.state().is_STATE_CLOSED()) {
			return false;
		}
		OptsOpenNode optsOpenNode = new OptsOpenNode();
		optsOpenNode.persistState = persistState;
		optsOpenNode.showFilename = false;
		optsOpenNode.skipCloseCheck = true;
		optsOpenNode.isOnQView = onQView;
		NodeFactory.openFormIdentitySinglyAsWin0(nodeDir, persistState, false, true);
		return true;

	}
}
