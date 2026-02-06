package zk_notes.factory;

import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpu.X;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_com.base.Xml;
import zk_form.control.ErrLb;
import zk_notes.control.NodeLn;
import zk_notes.node.NodeDir;
import zk_notes.node.SitePersEntity;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.EntityState;
import zk_notes.node_state.ObjState;
import zk_os.*;
import zk_os.coms.AFC;
import zk_os.coms.AFCC;
import zk_os.coms.AFCSec;
import zk_os.core.Sdn;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecApp;
import zk_os.sec.UO;
import zk_page.ZKC;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NFPageCom {

	public static final Logger L = LoggerFactory.getLogger(NFPageCom.class);

	public static Map<NodeDir, Component> buildPageComMap(Sdn sdn, HtmlBasedComponent parent) {

		Set<Path> set = AFCSec.getItemPaths(WebUsr.get(), AFC.SpaceType.NODES, sdn, UO.VIEW, UO.EDIT, UO.RUN);
//		Set<Path> set = AFC.FORMS.DIR_FORMS_LS_CLEAN(sd3pn);

		return set.stream().map(nodePath -> {

			String nodeName = UF.fn(nodePath);

			AFCC.FileType fileType = AFCC.FileType.of(nodeName, null);

			NodeDir nodeDir = NodeDir.ofDir(sdn, nodePath);
			if (fileType != null) {
				String fileData = AppStateFactory.ofFormDir_orCreate(sdn, nodePath).readFcData(null);
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

		ObjState stateForm = node.state();

		boolean fAllowedForEdit = stateForm.isAllowedAccess_EDIT();
		boolean fAllowedForView = stateForm.isAllowedAccess_VIEW();
//		boolean fAllowedForView = stateForm.isAllowedAccess_VIEW_EDIT();
		if (!fAllowedForView && !fAllowedForEdit) {
			if (APP.IS_PROM_ENABLE) {
//			if (AppZosProps.APD_IS_PROM_ENABLE.getValueOrDefault(false)) {
				if (L.isDebugEnabled()) {
					L.debug("is Prom enable, com skip warn view=false,edit=false");
				}
				return Pare.of(node, null);
			}
			return Pare.of(node, new ErrLb("view=" + fAllowedForView + ", edit=" + fAllowedForEdit));
		}


		Pare<NodeDir, Component> extCom = NFCreate.createFormNOL(node, true);
		if (extCom != null) {
			return extCom;
		}

		NodeLn nodeLn = buildNodeLn(parent, node);

		return Pare.of(node, nodeLn);

	}

	private static @NotNull NodeLn buildNodeLn(HtmlBasedComponent parent, NodeDir node) {

		ObjState stateCom = node.stateCom();

		boolean isDeprecated = stateCom.hasPropEnable(EntityState.DEPRECATED, false);

		NodeLn nodeLn = isDeprecated ? new NodeLn(parent, node, (Boolean) null) : new NodeLn(parent, node);

		NFStyle.applyComDefaultStyle(nodeLn, node);

		NFStyle.applyState_BgColor(nodeLn, node.stateCom());

		NFStyle.applyState_RandomOrTopLeft_ForNode(nodeLn, stateCom);

		return nodeLn;
	}

}
