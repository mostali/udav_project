package zk_notes.control;

import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.GEXT;
import mpe.str.CN;
import mpu.X;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import zk_com.base.Tbx;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_com.core.IZCom;
import zk_com.core.IZWin;
import zk_notes.coms.*;
import zk_form.ext.GalleryVF;
import zk_os.sec.Sec;
import zk_page.ZKPage;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FormState;
import zk_page.ZKS;
import zk_pages.PrettyCodeXml;

import java.nio.file.Path;
import java.util.List;

public class NodeFactoryCom {

	public static IZWin createForm_Text(NodeDir nodeDir) {
		return createForm_Text(nodeDir, nodeDir.nvt(NodeDir.NVT.TEXT));
	}

	public static class HtmlWinCom extends Div0 {
		public HtmlWinCom(Component... coms) {
			super(coms);
		}

		public static HtmlWinCom of(Component... coms) {
			return new HtmlWinCom(coms);
		}
	}

	public static IZWin createForm_Text(NodeDir nodeDir, NodeDir.NVT nvt) {

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

			case PRETTYCODE:
				return createForm_PRETTYCODE(nodeDir);

			case MD_WIN:
				return Xml.ofMd(pathForm);

			case HTML:
			case MD:

				switch (nvt) {
					case HTML:
//						return Xml.ofFile(pathForm.toString());
						return Xml.ofXml(nodeDir.nodeDataInjected().nodeData);
					case MD:
//						return Xml.ofMd(pathForm);
						return Xml.ofMd(nodeDir.nodeDataInjected().nodeData);
					//					case PDF_WIN:
					//						com = Xml.ofPdf(pathForm.toString());
					//						break;
					default:
						throw new WhatIsTypeException("set impl:" + nvt);

				}

			default:
				throw new WhatIsTypeException(nvt);
		}
	}

	public static PrettyCodeXml createForm_PRETTYCODE(NodeDir nodeDir) {
		return PrettyCodeXml.of(nodeDir.nodeData(), true);
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
			NoteTbxm nodeTbx0 = (NoteTbxm) new NoteTbxm(nodeDir.nodeName(), pathForm, Tbx.DIMS.WH100).prettyjson(stateForm.isJson());//.eventBusable(Pare.of(QzNoteService.SaveTbxListener.BUSEVENT_NOTE_SAVE, nodeDir));
			if (isEditorAdminOwner) {
				nodeTbx0.saveOnShortCut();
			}
//			Div0 div0 = Div0.of(nodeTbx0);
//			div0.width_height(100.0,100.0);
//			div0.width_height(100.0,100.0);
//			return div0;
//			nodeTbx0.setHflex("min");
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

	public static IZCom createForm_Media(NodeDir nodeDir, NodeDir.NVM nmt) {
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

	public static IZCom createForm_Img(NodeDir node) {
		List<Path> files = node.fLsGEXT(GEXT.IMG);
		if (X.sizeOf0(files) > 1) {
			GalleryVF galleryVF = new GalleryVF(node);
			galleryVF.width(200);
			ZKPage.renderHeadRsrc(galleryVF);
			//return galleryVF;
			//Div0 div0 = Div0D.of(galleryVF);
			return galleryVF;
		} else {
			SingleNodeImg singleNodeImg = new SingleNodeImg(node);
			return singleNodeImg;
		}
	}

	public static @NotNull IZCom createForm_MultiAudio(NodeDir node) {
		//			List<Path> audioFiles = map.get(GEXT.AUDIO);
		IZCom com;
		//			if (audioFiles.size() == 1) {
		//				com = new SingleNodeAudio(nodeDir);
		//			} else {
		com = new SingleNodeAudioMulti(node);
		//			}
		return com;
	}

	public static @NotNull IZCom createForm_Video(NodeDir node) {
		SingleNodeVideo com = new SingleNodeVideo(node);
		return com;
	}


}
