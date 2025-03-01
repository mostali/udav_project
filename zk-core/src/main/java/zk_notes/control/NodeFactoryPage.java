package zk_notes.control;

import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.log.L;
import mpe.str.CN;
import mpu.X;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.base.Xml;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_form.control.ErrLb;
import zk_notes.events.ANM;
import zk_notes.node.NodeDir;
import zk_notes.node.SitePersEntity;
import zk_notes.node_state.FormState;
import zk_os.AFC;
import zk_os.AFCC;
import zk_os.AppZos;
import zk_page.ZKC;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeFactoryPage {

	public static Map<NodeDir, Component> buildPageComMap(Pare sd3pn, HtmlBasedComponent parent, boolean renderHead) {
		Set<Path> set = AFC.FORMS.DIR_FORMS_LS_CLEAN(sd3pn);
		return set.stream().map(nodePath -> {

			String nodeName = UF.fn(nodePath);

			AFCC.FileType fileType = AFCC.FileType.of(nodeName, null);

			NodeDir nodeDir = NodeDir.ofDir(sd3pn, nodePath);
			if (fileType != null) {
				String fileData = FormState.ofFormDirOrCreate(sd3pn, nodePath).readFcData(null);
				if (X.empty(fileData)) {
					L.error("Node '{}' with '{}' data is empty", nodePath, fileType);
				} else {
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
						default:
							throw new WhatIsTypeException(fileType);
					}
				}
			}

			Pare<NodeDir, Component> buildedCom = buildSingleCom(parent, nodeDir);

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
		boolean fAllowedForEdit = stateForm.isAllowedAccess_Edit();
		boolean fAllowedForView = stateForm.isAllowedAccess_View_Edit();
		if (!fAllowedForView && !fAllowedForEdit) {
			return Pare.of(node, new ErrLb("view=" + fAllowedForView + ", edit=" + fAllowedForEdit));
		}

		NodeDir.NVM nmt = node.nvm_first(null);
		if (nmt != null) {

			Pare<NodeDir, IZCom> nodeCom;
			switch (nmt) {
				case IMG: {
					IZCom com = NodeFactoryCom.createForm_Img(node);
					nodeCom = Pare.of(node, com);
					break;
				}
				case AUDIO: {
					nodeCom = Pare.of(node, NodeFactoryCom.createForm_MultiAudio(node));
					break;
				}
				case VIDEO: {
					nodeCom = Pare.of(node, NodeFactoryCom.createForm_Video(node));
					break;
				}
				default:
					throw new WhatIsTypeException(nmt);
			}

			FunctionV1<IZCom> draggableBehaviour = (com) -> {
//				boolean cAllowedAccessEdit = com.getComState_JSON().isAllowedAccess_Edit();
				com.draggablePersistenseForm(node.nodeName(), fAllowedForEdit);
			};

			draggableBehaviour.apply(nodeCom.val());

			FunctionV1<IZCom> menuBehaviour = (com) -> {
				switch (nmt) {
					case IMG:
						if (fAllowedForEdit) {
							Menupopup0 menupopup = nodeCom.val().getOrCreateMenupopup(ZKC.getFirstWindow());
							ANM.applyMenu_NodeImg(menupopup, node);
						}
						break;
					case AUDIO:
					case VIDEO:
						break;
					default:
						throw new WhatIsTypeException(nmt);
				}
				if (fAllowedForEdit) {
					com.draggablePersistenseForm(node.nodeName(), true);
				}
			};
			menuBehaviour.apply(nodeCom.val());

			return (Pare) nodeCom;
		}


		boolean isActual = stateForm.hasPropEnable(CN.ACTUAL, true);

		NodeLn nodeLn = isActual ? new NodeLn(parent, node) : new NodeLn(parent, node, true);

		stateForm.stateCom().apply_TITLE(nodeLn);
		stateForm.stateCom().apply_TITLEX(nodeLn);

		return Pare.of(node, nodeLn);

	}

	public static Pare<NodeDir, Component> buildSingleCom(HtmlBasedComponent parent, NodeDir nodeDir) {
		Path path = nodeDir.toPath();
		try {
			Pare<NodeDir, Component> nodeDirComponentPare = buildSingleComImpl(parent, nodeDir);
			if (SitePersEntity.L.isDebugEnabled()) {
				SitePersEntity.L.debug("buildCom:" + nodeDirComponentPare);
			}
			return nodeDirComponentPare;
		} catch (Exception ex) {
			SitePersEntity.L.error("Unexcepted buildAndAppendNote:" + path, ex);
			if (AppZos.isDebugEnable()) {
//				return Pare.of(NodeDir.ofDir(path, sdn()), new ErrLb("Unexcepted err:" + nodeName(), ex));
				return Pare.of(nodeDir, new ErrLb("Unexcepted err:" + nodeDir.nodeName(), ex));
			}
			return null;
		}
	}
}
