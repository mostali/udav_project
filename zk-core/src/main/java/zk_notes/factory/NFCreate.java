package zk_notes.factory;

import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.GEXT;
import mpe.str.CN;
import mpu.X;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import zk_com.base.Tbx;
import zk_com.base.Xml;
import zk_com.base.XmlHtml;
import zk_com.base.XmlMd;
import zk_com.base_ctr.Div0;
import zk_com.core.IZCom;
import zk_com.core.IZWin;
import zk_form.dirview.DirView0;
import zk_form.dirview.DirViewNode;
import zk_form.dirview.TreeViewNode;
import zk_form.tree.CtxTreeView;
import zk_notes.coms.*;
import zk_form.ext.GalleryVF;
import zk_notes.node.core.NVM;
import zk_notes.node.core.NVT;
import zk_os.AFC;
import zk_os.core.Sdn;
import zk_os.sec.Sec;
import zk_page.ZKPage;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FormState;
import zk_page.ZKS;
import zk_notes.coms.PrettyCodeXml;

import java.nio.file.Path;
import java.util.List;

public class NFCreate {

	public static class HtmlWinCom extends Div0 {
		public HtmlWinCom(Component... coms) {
			super(coms);
		}

		public static HtmlWinCom of(Component... coms) {
			return new HtmlWinCom(coms);
		}
	}

	public static IZWin createForm(NodeDir nodeDir, NVT nvt) {

		Path pathForm = nodeDir.getPathFormFc();

		switch (nvt) {

			case TEXT:
				return (IZWin) createCom_Note(nodeDir);

			case HTML_WIN:
				Xml xml = Xml.ofFile(pathForm.toString());
				HtmlWinCom wrapper = HtmlWinCom.of(xml);
				ZKS.WIDTH_HEIGHT100(wrapper);
				ZKS.OVERFLOW(wrapper, 0);
				return wrapper;

			case WYSIWYG:
				return createForm_SE(nodeDir);

			case CODE:
				return createForm_PRETTYCODE(nodeDir);

			case MD_WIN:
				return Xml.ofMd(pathForm);

			case DIR:
				return (IZWin) DirView0.createWithSimpleMenuAsForm(nodeDir.toPath()).borderSilver().inlineBlock();

			case TREE_NODE:
				CtxTreeView view = CtxTreeView.createView(nodeDir.toPathEventTree());
				return (IZWin) view.borderSilver().inlineBlock();

			case HTML:
			case MD:

				switch (nvt) {
					case HTML:
						return XmlHtml.ofWithFormName(nodeDir.nodeDataInjected().nodeData, nodeDir.nodeName());
					case MD:
						return XmlMd.ofWithFormName(nodeDir.nodeDataInjected().nodeData, nodeDir.nodeName());

					default:
						throw new WhatIsTypeException("set impl:" + nvt);
				}

			default:
				throw new WhatIsTypeException(nvt);
		}
	}

	public static PrettyCodeXml createForm_PRETTYCODE(NodeDir nodeDir) {
		return PrettyCodeXml.ofWithFormName(nodeDir.nodeData(), true, nodeDir.nodeName());
	}

	public static SeNoteTbxm createForm_SE(NodeDir nodeDir) {
		FormState stateForm = nodeDir.state();
		Path pathForm = nodeDir.getPathFormFc();
		boolean isEditorAdminOwner = Sec.isEditorAdminOwner();

		SeNoteTbxm seTbx0 = (SeNoteTbxm) new SeNoteTbxm(pathForm, Tbx.DIMS.WH100).json(stateForm.isJson());//.eventBusable(Pare.of(QzNoteService.SaveTbxListener.BUSEVENT_NOTE_SAVE, nodeDir));
		if (isEditorAdminOwner) {
			seTbx0.saveble().saveOnShortCut();
		}
		seTbx0.placeholder("html data");
		return seTbx0;
	}

	public static IZCom createCom_Note(NodeDir nodeDir) {

		FormState stateForm = nodeDir.state();
		Path pathForm = nodeDir.getPathFormFc();
		boolean isEditorAdminOwner = Sec.isEditorAdminOwner();

		Integer size = (Integer) stateForm.getAs(CN.SIZE, Integer.class, null);
		if (size == null || size < 2) {
//			NoteTbxm nodeTbx0 = (NoteTbxm) new NoteTbxm(nodeDir, pathForm, Tbx.DIMS.BYCONTENT).prettyjson(stateForm.isJson());//.eventBusable(Pare.of(QzNoteService.SaveTbxListener.BUSEVENT_NOTE_SAVE, nodeDir));
			NoteTbxm nodeTbx0 = (NoteTbxm) new NoteTbxm(nodeDir, pathForm, Tbx.DIMS.WH100).prettyjson(stateForm.isJson());//.eventBusable(Pare.of(QzNoteService.SaveTbxListener.BUSEVENT_NOTE_SAVE, nodeDir));
			if (isEditorAdminOwner) {
				nodeTbx0.saveOnShortCut();
			}
			return nodeTbx0;
		} else {
			NoteTbxmy noteTbxmy = new NoteTbxmy(nodeDir.nodeName(), pathForm, size, Tbx.DIMS.WH100);
			if (isEditorAdminOwner) {
				noteTbxmy.saveOnShortCut();
			}
			noteTbxmy.placeholder("data");
			return noteTbxmy;
		}
	}

	//
	// --------------------------- MEDIA ------------------------------------
	//

	public static IZCom createForm_Media(NodeDir nodeDir, NVM nmt) {
		switch (nmt) {
			case IMG:
				return createForm_Img(nodeDir);
			case AUDIO:
				return createForm_MultiAudio(nodeDir);
			case VIDEO:
				return createForm_Video(nodeDir);
			default:
				throw new WhatIsTypeException(nmt);
		}
	}

	public static IZCom createForm_Dir(NodeDir node) {
//		DirView0.createWithSimpleMenuAsForm
//		return new DirViewNode(node, true);
		return new DirViewNode(node, true);
	}

	public static IZCom createForm_Tree(NodeDir node) {
//		DirView0.createWithSimpleMenuAsForm
//		return new DirViewNode(node, true);
		return new TreeViewNode(node, false);
	}

	public static IZCom createForm_Img(NodeDir node) {
		List<Path> files = node.fLsGEXT(GEXT.IMG);
		if (X.sizeOf0(files) > 1) {
			GalleryVF galleryVF = new GalleryVF(node);
			galleryVF.width(200);
			ZKPage.renderHeadRsrc(galleryVF);
			return galleryVF;
		} else {
			SingleNodeImg singleNodeImg = new SingleNodeImg(node);
			return singleNodeImg;
		}
	}

	public static @NotNull IZCom createForm_MultiAudio(NodeDir node) {
		SingleNodeAudioMulti singleNodeAudioMulti = new SingleNodeAudioMulti(node);
		return singleNodeAudioMulti;
	}

	public static @NotNull IZCom createForm_Video(NodeDir node) {
		SingleNodeVideo com = new SingleNodeVideo(node);
		return com;
	}


}
