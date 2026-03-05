package zk_notes.factory;

import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.func.FunctionV;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import zk_com.base.Tbx;
import zk_com.base.Xml;
import zk_com.base.XmlHtml;
import zk_com.base.XmlMd;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_com.core.IZWin;
import zk_form.dirview.DirView0;
import zk_form.dirview.DirViewNode;
import zk_form.dirview.TreeViewNode;
import zk_notes.coms.*;
import zk_form.ext.GalleryVF;
import zk_notes.events.ANMF;
import zk_notes.node.core.NVM;
import zk_notes.node.core.NVT;
import zk_notes.node_state.proxy.NodeProxyRW;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.ZKPage;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;
import zk_page.ZKS;
import zk_notes.coms.PrettyCodeXml;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class NFItem {

	public static final Logger L = LoggerFactory.getLogger(NFItem.class);

	public static class HtmlWinCom extends Div0 {
		public HtmlWinCom(Component... coms) {
			super(coms);
		}

		public static HtmlWinCom of(Component... coms) {
			return new HtmlWinCom(coms);
		}
	}

	public static Pare<NodeDir, Component> createFormNOL(NodeDir node, boolean... withBeahviours) {
		IZCom izCom = NFItem.createForm_CUSTOM(node);
		if (izCom == null) {
			izCom = NFItem.createForm_MEDIA(node);
		}
		if (izCom != null && ARG.isDefEqTrue(withBeahviours)) {
			return initBehavioursNVT(node, izCom);
		}
		return null;
	}

	public static IZCom createForm_CUSTOM(NodeDir node) {
		NVT nvt = node.nvt(null);
		if (nvt == null) {
			return null;
		}
		return createForm_CUSTOM(node, nvt);
	}

	public static IZCom createForm_CUSTOM(NodeDir node, NVT nvt) {
		IZCom nvtForm;
		switch (nvt) {
			case DIR:
				nvtForm = NFItem.createForm_DIR(node);
				break;
			case TREE_NODE:
				return NFItem.createForm_TREE(node);
			default:
				nvtForm = null;
				break;
		}
		return nvtForm;
	}

	public static IZWin createForm_DIR(NodeDir node) {
		//return (IZWin) DirView0.createWithSimpleMenuAsForm(node.toPath()).borderSilver().inlineBlock();
		NodeProxyRW proxyRW = node.getProxyRW();
		Path targetAnyPathRead = proxyRW.getTargetPathFileRead();
		boolean isDir = UFS.isDir(targetAnyPathRead);
		DirViewNode dirViewNode = isDir ? new DirViewNode(node, targetAnyPathRead, false) : new DirViewNode(node, false);
		DirView0.applyDefaultMenu(dirViewNode);
		return dirViewNode;
	}

	public static IZWin createForm_TREE(NodeDir node) {
		//CtxTreeView view = CtxTreeView.createView(node.toPathEventTree());
		//return (IZWin) view.borderSilver().inlineBlock();
		NodeProxyRW proxyRW = node.getProxyRW();
		Path targetAnyPathRead = proxyRW.getTargetPathFileRead();
		boolean isSql = EXT.SQLITE.has(targetAnyPathRead);
		TreeViewNode treeViewNode = isSql ? new TreeViewNode(node, targetAnyPathRead.toString(), false) : new TreeViewNode(node, false);
		NFStyle.applyState_Width(treeViewNode, node.state());
		return treeViewNode;
	}

	public static IZWin createForm(NodeDir node, NVT nvt, NodeDir.Behaviour... openBeahviour) {
		IZWin com = createForm0(node, nvt);
		if (ARG.isDef(openBeahviour)) {
			initBehavioursNVT(node, com, openBeahviour);
		}
		return com;
	}

	private static IZWin createForm0(NodeDir node, NVT nvt) {

		switch (nvt) {

			case DIR:
				return createForm_DIR(node);

			case TREE_NODE:
				return createForm_TREE(node);

			case TEXT:
				return (IZWin) createCom_NOTE(node);

			case HTML_WIN:
//				Xml xml = Xml.ofFile(pathForm.toString());
				Xml xml = Xml.ofXml(node.injectStr());
				HtmlWinCom wrapper = HtmlWinCom.of(xml);
				ZKS.WIDTH_HEIGHT100(wrapper);
				ZKS.OVERFLOW(wrapper, 0);
				return wrapper;

			case WYSIWYG:
				return createForm_WYSIWYG(node);

			case CODE:
				return createForm_PRETTYCODE(node);

			case MD_WIN:
				return Xml.ofMd(node.injectStr());


			case HTML:
				return XmlHtml.ofWithFormName(node.injectStr(), node.nodeName());

			case MD:
				return XmlMd.ofWithFormName(node.injectStr(), node.nodeName());


			default:
				throw new WhatIsTypeException(nvt);
		}
	}

	public static PrettyCodeXml createForm_PRETTYCODE(NodeDir node) {
		String data = node.getProxyRW(true).readContentOrEmpty();
		return PrettyCodeXml.ofWithFormName(data, true, node.nodeName());
	}

	public static SeNoteTbxm createForm_WYSIWYG(NodeDir nodeDir) {
		ObjState stateForm = nodeDir.state();
		Path pathForm = nodeDir.getPath_FormFc_Data();
		boolean isEditorAdminOwner = SecMan.isAllowedEditPlane();

		SeNoteTbxm seTbx0 = (SeNoteTbxm) new SeNoteTbxm(pathForm, Tbx.DIMS.WH100).json(stateForm.isJson());//.eventBusable(Pare.of(QzNoteService.SaveTbxListener.BUSEVENT_NOTE_SAVE, nodeDir));
		if (isEditorAdminOwner) {
			seTbx0.saveble().saveOnShortCut();
		}
		seTbx0.placeholder("html data");
		return seTbx0;
	}

	public static IZCom createCom_NOTE(NodeDir nodeDir) {

		ObjState stateForm = nodeDir.state();
		Path pathForm = nodeDir.getPath_FormFc_Data();
		boolean isEditorAdminOwner = SecMan.isAllowedEditPlane();

		Integer size = (Integer) stateForm.getAs(BeType.note_size.name0, Integer.class, null);
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

	public static IZCom createForm_MEDIA(NodeDir node) {
		NVM nmt = node.nvm_first_auto_cached();
		if (nmt == null) {
			return null;
		}
		return createForm_MEDIA(node, nmt);
	}

	public static IZCom createForm_MEDIA(NodeDir node, NVM nmt) {
		switch (nmt) {
			case IMG:
				return createForm_IMG(node);
			case AUDIO:
				return createForm_MULTIAUDIO(node);
			case VIDEO:
				return createForm_VIDEO(node);
			default:
				throw new WhatIsTypeException(nmt);
		}
	}


	public static IZCom createForm_IMG(NodeDir node) {
		List<Path> files = node.dLsGEXT(GEXT.IMG);
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

	public static @NotNull IZCom createForm_MULTIAUDIO(NodeDir node) {
		SingleNodeAudioMulti singleNodeAudioMulti = new SingleNodeAudioMulti(node);
		return singleNodeAudioMulti;
	}

	public static @NotNull IZCom createForm_VIDEO(NodeDir node) {
		SingleNodeVideo com = new SingleNodeVideo(node);
		return com;
	}


	//
	//
	//

	static @NotNull Pare initBehavioursNVT(NodeDir node, IZCom izCom, NodeDir.Behaviour... openBeahviour) {

		boolean fAllowedForEdit = node.state().isAllowedAccess_EDIT();

		Pare<NodeDir, IZCom> nodeCom = Pare.of(node, izCom);

		Supplier<Menupopup0> menu = () -> izCom.getOrCreateMenupopup(ZKC.getFirstWindow());
		//MEDIA MENU BEHAVIOUR
		FunctionV menuBehaviour = () -> {

			if (!fAllowedForEdit) {
				return;
			}

			NVT nvt = node.nvt(null);

			if (nvt != null) {

				switch (nvt) {
					case DIR:
					case TREE_NODE:
						//appendMenu
						ANMF.applyNolCom(menu.get(), node, true);

						//
						izCom.inlineBlock();

						return;

					default:
						break;
//						throw new WhatIsTypeException(nvt);

				}

			}


			NVM nvm = node.nvm_first_auto_cached();
			if (nvm == null) {
				return;
			}

			switch (nvm) {
				case IMG:
				case AUDIO:
				case VIDEO:
					ANMF.applyNolCom(menu.get(), node, true);
					break;
				default:
					throw new WhatIsTypeException(nvm);
			}

		};

		menuBehaviour.apply();

		boolean adminOrOwner = SecMan.isOwnerOrAdmin();

		if (!ARR.contains(openBeahviour, NodeDir.Behaviour.Relative)) {
			izCom.absolute();
		}

		NFBe.applyDnd_PosWh_Behaviours(izCom, node.nodeName(), adminOrOwner, true);

		if (ARR.contains(openBeahviour, NodeDir.Behaviour.Inline)) {
			izCom.inlineBlock();
		}

		return nodeCom;
	}

}
