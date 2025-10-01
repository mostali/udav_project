package zk_notes.node_state;

import mp.utl_odb.tree.UTree;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS_BASE;
import mpu.IT;
import mpu.core.ARG;
import mpu.pare.Pare;
import udav_net.apis.zznote.NodeID;
import zk_notes.node.NodeDir;
import zk_notes.node_state.libs.EventsStateTree;
import zk_notes.node_state.libs.ComState;
import zk_notes.node_state.libs.PageState;
import zk_notes.node_state.libs.PlaneState;
import zk_os.coms.AFC;

import java.nio.file.Path;

public class AppStateFactory {

	//
	//
	// byName

	public static PlaneState ofPlaneName_orCreate(String sd3, boolean... create) {
		Path dstStateFile = AppStatePath.getPlaneStatePath(sd3);
		return ofPath_EntityFile_orCreate(Pare.of(sd3), dstStateFile, AFC.AfcEntity.PLANE, ARG.isDefEqTrue(create));
	}

	public static PageState ofPageName_orCreate(Pare<String, String> sdn, boolean... create) {
		Path pageState = AppStatePath.getPageStatePath(sdn.key(), sdn.val());
		return ofPath_EntityFile_orCreate(sdn, pageState, AFC.AfcEntity.PAGE, create);
	}

	public static FormState ofFormName_orCreate(Pare sdn, String noteName, boolean... create) {
		Path pathOfFormNotePpi = AppStatePath.getFormDataPath(sdn, noteName);
		return ofPath_EntityFile_orCreate(sdn, pathOfFormNotePpi, AFC.AfcEntity.FORM, create);
	}

	public static FormState ofComName_orCreate(Pare<String, String> sdn, String comname, boolean... create) {
		Path pathOfFormNotePpi = AppStatePath.getComStatePath(sdn.key(), sdn.val(), comname);
		return ofPath_EntityFile_orCreate(sdn, pathOfFormNotePpi, AFC.AfcEntity.COM, create);
	}

	public static FormState ofPagecomName_orCreate(Pare<String, String> sdn, String comname, boolean... create) {
		Path comState = AppStatePath.getPagecomStatePath(sdn.key(), sdn.val(), comname);
		return ofPath_EntityFile_orCreate(sdn, comState, AFC.AfcEntity.PAGECOM, create);
	}

	//
	//

	public static FormState ofFormDir_orCreate(Pare sdn, Path nodeDir) {
		return ofFormName_orCreate(sdn, nodeDir.getFileName().toString());
	}

	public static FormState ofFormName_WithContent(Pare sdn, String nodeName, String content, boolean... create) {
		Path pathOfFormNote = sdn != null ? AppStatePath.getFormDataPath(sdn, nodeName) : AppStatePath.getFormDataPath_PPI(nodeName);
		if (ARG.isDefEqTrue(create)) {
			boolean created = UFS_BASE.MKFILE.createFileIfNotExistWithContentMkdirs(pathOfFormNote, content);
			if (!created) {
				UFS_BASE.RM.deleteDir(pathOfFormNote.getParent());
				IT.state(UFS_BASE.MKFILE.createFileIfNotExistWithContentMkdirs(AppStatePath.getFormDataPath_PPI(nodeName), content));
			}
		}
		return ofPath_EntityFile_orCreate(sdn, pathOfFormNote, AFC.AfcEntity.FORM);
	}


	//
	//

	public static <T extends FormState> T ofPath_EntityFile_orCreate(Pare sdn, Path pathCom, AFC.AfcEntity entity, boolean... create) {
		FormState formState;
		switch (entity) {

			case PLANE:
				formState = new PlaneState(sdn, pathCom.toString());
				break;

			case PAGE:
				formState = new PageState(sdn, pathCom.toString());
				break;

			case COM:
				formState = new ComState(sdn, pathCom.toString());
				break;

			case FORM:
				formState = new FormState(sdn, pathCom.toString(), true);
				break;

			default:
				throw new WhatIsTypeException(entity);

		}

		formState.pathFc = pathCom;//transient
		if (ARG.isDefEqTrue(create)) {
			formState.getPropsOrCreate();
		}

		return (T) formState;

	}

	//
	//

	public static FormState ofState_OrCreate(Pare<String, String> sdn, AFC.AfcEntity afcEntityName, String nodeName, boolean... create) {

		FormState formState;
		switch (afcEntityName) {
			case FORM:
				formState = ofFormName_orCreate(sdn, nodeName, create);
				break;
			case COM:
				formState = ofComName_orCreate(sdn, nodeName, create);
				break;
			case PLANE:
				formState = ofPlaneName_orCreate(sdn.key(), create);
				break;
			case PAGE:
				formState = ofPageName_orCreate(sdn, create);
				break;
			case PAGECOM:
				formState = ofPagecomName_orCreate(sdn, nodeName, create);
				break;
			default:
				throw new WhatIsTypeException(afcEntityName);
//				return ARG.toDefThrowMsg(() -> X.f("illegal state for update : %s", afcEntityName), defRq);

		}
		return formState;
	}

	public static UTree getEventTreeOrCreate(NodeID nodeID) {
		Path pathNodeEventTreePath = AFC.EVENTS.toPathNodeEventTreePath(nodeID.sdn(), nodeID.itemRq());
		UTree tree = (UTree) UTree.tree(pathNodeEventTreePath).withAutoCleanCfg(EventsStateTree.AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
//		tree.createDbIfNotExists();
		if (tree.isEmptyDb()) {
			tree.put("node.id", nodeID.toString());
		}
//		ICtxDb.of(pathNodeEventTreePath).checkLazyCreateDb();
		return tree;
	}
}
