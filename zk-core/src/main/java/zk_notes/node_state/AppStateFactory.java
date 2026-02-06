package zk_notes.node_state;

import mp.utl_odb.tree.UTree;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpu.IT;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpe.call_msg.core.NodeID;
import zk_notes.node_srv.EventsStateTree;
import zk_notes.node_state.impl.*;
import zk_os.coms.AFC;

import java.nio.file.Path;

public class AppStateFactory {

	//
	//
	// byName

	public static PlaneState forPlane(String plane, boolean... create) {
		Path dstStateFile = AppStatePath.getPlanePropsPath(plane);
		return ofPath_EntityFile_orCreate(Pare.of(plane), dstStateFile, AFC.AfcEntity.PLANE, ARG.isDefEqTrue(create));
	}

	public static PageState forPage(Pare<String, String> sdn, boolean... create) {
		Path pageState = AppStatePath.getPagePropsPath(sdn.key(), sdn.val());
		return ofPath_EntityFile_orCreate(sdn, pageState, AFC.AfcEntity.PAGE, create);
	}

	public static FormState forForm(Pare sdn, String formName, boolean... create) {
		Path pathOfFormNotePpi = AppStatePath.getFormDataPath(sdn, formName);
		return ofPath_EntityFile_orCreate(sdn, pathOfFormNotePpi, AFC.AfcEntity.FORM, create);
	}

	public static ComState forCom(Pare<String, String> sdn, String comname, boolean... create) {
		Path pathOfFormNotePpi = AppStatePath.getComPropsPath(sdn.key(), sdn.val(), comname);
		return ofPath_EntityFile_orCreate(sdn, pathOfFormNotePpi, AFC.AfcEntity.COM, create);
	}

	public static PagecomState forPagecom(Pare<String, String> sdn, String pagecomName, boolean... create) {
		Path comState = AppStatePath.getPagecomPropsPath(sdn.key(), sdn.val(), pagecomName);
		return ofPath_EntityFile_orCreate(sdn, comState, AFC.AfcEntity.PAGECOM, create);
	}

	//
	//

	public static ObjState ofFormDir_orCreate(Pare sdn, Path nodeDir) {
		return forForm(sdn, nodeDir.getFileName().toString());
	}

	public static ObjState ofFormName_WithContent(Pare sdn, String nodeName, String content, boolean... create) {
		Path pathOfFormNote = sdn != null ? AppStatePath.getFormDataPath(sdn, nodeName) : AppStatePath.getFormDataPath_PPI(nodeName);
		if (ARG.isDefEqTrue(create)) {
			boolean created = UFS.MKFILE.createFileIfNotExistWithContentMkdirs(pathOfFormNote, content);
			if (!created) {
				UFS.RM.deleteDir(pathOfFormNote.getParent());
				IT.state(UFS.MKFILE.createFileIfNotExistWithContentMkdirs(AppStatePath.getFormDataPath_PPI(nodeName), content));
			}
		}
		return ofPath_EntityFile_orCreate(sdn, pathOfFormNote, AFC.AfcEntity.FORM);
	}


	//
	//

	public static <T extends ObjState> T ofPath_EntityFile_orCreate(Pare sdn, Path pathCom, AFC.AfcEntity entity, boolean... create) {
		ObjState formState;
		switch (entity) {

			case PLANE:
				formState = new PlaneState(sdn, pathCom.toString());
				break;

			case PAGE:
				formState = new PageState(sdn, pathCom.toString());
				break;
			case PAGECOM:
				formState = new PagecomState(sdn, pathCom.toString());
				break;
			case COM:
				formState = new ComState(sdn, pathCom.toString());
				break;

			case FORM:
				formState = new FormState(sdn, pathCom.toString());
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

	public static ObjState ofState_OrCreate(Pare<String, String> sdn, AFC.AfcEntity afcEntityName, String entName, boolean... create) {

		ObjState formState;
		switch (afcEntityName) {
			case FORM:
				formState = forForm(sdn, entName, create);
				break;
			case COM:
				formState = forCom(sdn, entName, create);
				break;
			case PLANE:
				formState = forPlane(sdn.key(), create);
				break;
			case PAGE:
				formState = forPage(sdn, create);
				break;
			case PAGECOM:
				formState = forPagecom(sdn, entName, create);
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
