package zk_notes.factory;

import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpe.str.CN;
import mpu.X;
import mpu.func.FunctionV;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.base.Xml;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_form.control.ErrLb;
import zk_notes.control.NodeLn;
import zk_notes.events.ANMF;
import zk_notes.node.NodeDir;
import zk_notes.node.SitePersEntity;
import zk_notes.node.core.NVM;
import zk_notes.node.core.NVT;
import zk_notes.node_state.FormState;
import zk_os.*;
import zk_os.sec.Sec;
import zk_page.ZKC;
import zk_page.ZKS;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NFPageCom {

	public static final Logger L = LoggerFactory.getLogger(NFPageCom.class);

	public static Map<NodeDir, Component> buildPageComMap(Pare sd3pn, HtmlBasedComponent parent) {

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

			Component val = buildedCom.val();
//			if (val == null) {
//				L.info("Component '{}' is NULL after build (is hide?)", buildedCom.key().nodeId());
//			}
			return Pare.of(nodeDir, val);

		}).filter(Pare::hasVal).collect(//

				Collectors.toMap(nd -> nd.key(), //

						nd -> nd.val(), //

						(v1, v2) -> X.throwException("who is master? %s vs %s", v1, v2), //

						LinkedHashMap::new) //

		);//
	}

	public static Pare<NodeDir, Component> buildSingleCom(HtmlBasedComponent parent, NodeDir nodeDir) {
		Path path = nodeDir.toPath();
		try {
			Pare<NodeDir, Component> nodeDirComponentPare = buildSingleComImpl(parent, nodeDir);
			if (L.isDebugEnabled()) {
				L.debug("buildCom:" + nodeDirComponentPare);
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

	private static Pare<NodeDir, Component> buildSingleComImpl(HtmlBasedComponent parent, NodeDir node) {

		FormState stateForm = node.state();
		boolean fAllowedForEdit = stateForm.isAllowedAccess_EDIT();
		boolean fAllowedForView = stateForm.isAllowedAccess_VIEW_EDIT();
		if (!fAllowedForView && !fAllowedForEdit) {
			if (AppZosProps.APD_IS_PROM_ENABLE.getValueOrDefault(false)) {
				if (L.isDebugEnabled()) {
					L.debug("is Prom enable, com skip warn view=false,edit=false");
				}
				return Pare.of(node, null);
			}
			return Pare.of(node, new ErrLb("view=" + fAllowedForView + ", edit=" + fAllowedForEdit));
		}

		Supplier<IZCom> nvmCom = () -> {
			NVM nmt = node.nvm_first(null);
			return nmt == null ? null : NFCreate.createForm_Media(node, nmt);
		};
		Supplier<IZCom> nvtCom = () -> {
			NVT nvt0 = node.nvt(null);
			if (nvt0 == null) {
				return null;
			}
			switch (nvt0) {
				case DIR:
					return NFCreate.createForm_Dir(node);
				case TREE_NODE:
					return NFCreate.createForm_Tree(node);
				default:
					return null;
			}
		};

		IZCom izCom = nvtCom.get();
		if (izCom == null) {
			izCom = nvmCom.get();
		}
		if (izCom != null) {
			return buildNVType(node, izCom);
		}

		boolean isDeprecated = stateForm.hasPropEnable(CN.DEPRECATED, false);
		boolean isBig = stateForm.hasPropEnable(CN.BIG, false);
		boolean isHide = stateForm.hasPropEnable("hideComLink", false);

		NodeLn nodeLn = isDeprecated ? new NodeLn(parent, node, null) : new NodeLn(parent, node);
//		NodeLn nodeLn = new NodeLn(parent, node);

		stateForm.stateCom().apply_TITLE(nodeLn);
		stateForm.stateCom().apply_TITLEX(nodeLn);

		if (isBig) {
			ZKS.FONT_SIZE(nodeLn, 36);
		}

		if (isHide) {
			if (L.isDebugEnabled()) {
				L.debug("ComponentLink '{}' is HIDE", node.nodeId());
			}
			nodeLn.setVisible(isHide);
		}

		return Pare.of(node, nodeLn);

	}

	private static @NotNull Pare buildNVType(NodeDir node, IZCom izCom) {

		boolean fAllowedForEdit = node.state().isAllowedAccess_EDIT();

		Pare<NodeDir, IZCom> nodeCom;
		nodeCom = Pare.of(node, izCom);

		//DRAGGABLE BEHAVIOUR
//		FunctionV1<IZCom> draggableBehaviour = (com) -> com.draggablePersistenseForm(node.nodeName(), fAllowedForEdit);
//		draggableBehaviour.apply(nodeCom.val());

		IZCom com = nodeCom.val();
		Supplier<Menupopup0> menu = () -> com.getOrCreateMenupopup(ZKC.getFirstWindow());
		//MEDIA MENU BEHAVIOUR
		FunctionV menuBehaviour = () -> {

			if (!fAllowedForEdit) {
				return;
			}

			NVT nvt = node.nvt(null);
			if (nvt == NVT.DIR) {
				ANMF.applyNolCom(menu.get(), node, true);
				return;
			}

			NVM nvm = node.nvm_first(null);
			if (nvm == null) {
				L.warn("Except NVM from node {}", node.nodeID());
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

//		if (Sec.isAdminOrOwner()) {
//			com.draggablePersistenceForm(node.nodeName(), true);
//		}

		menuBehaviour.apply();

		boolean adminOrOwner = Sec.isAdminOrOwner();

		NFBe.applyDndPosWhBehaviours(com, node.nodeName(), adminOrOwner, true, true);

		return nodeCom;
	}

}
