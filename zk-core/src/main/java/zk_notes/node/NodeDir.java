package zk_notes.node;

import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import mpe.wthttp.core.INode;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.pare.Pare;
import udav_net.apis.zznote.NodeID;
import zk_com.core.IZWin;
import zk_notes.factory.NFCreate;
import zk_notes.node.core.NVM;
import zk_notes.node.core.NVT;
import zk_notes.node_srv.TrackMap;
import zk_notes.node_srv.core.NodeEvalType;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.AppStatePath;
import zk_notes.node_state.libs.EventsStateTree;
import zk_os.coms.AFC;
import udav_net.apis.zznote.ItemPath;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.FormState;
import zk_os.core.NodeData;
import zk_os.core.Sdn;

import java.nio.file.Path;
import java.util.Objects;

public class NodeDir extends SiteDir<FormState> implements INode<NodeDir> {

	public static NodeDir ofNodeIdStr(String nodeID) {
		return ofNodeId(NodeID.of(nodeID));
	}

	public static NodeDir ofNodeId(NodeID nodeID) {
		return ofNodeName(nodeID.sdn(), nodeID.item());
	}

	private final String nodeName;

	public String nodeName() {
		return nodeName;
	}

	public NodeDir(String nodeName, Pare sdn) {
		super(sdn);
		this.nodeName = IT.NE(nodeName, "set node name");
	}

	public NodeDir(Path nodeDir, Pare sdn) {
		super(nodeDir, sdn);
		this.nodeName = nodeDir.getFileName().toString();
	}

	public NodeID nodeID() {
		return new NodeID(nodeName(), sdn());
	}

	public String nodeId() {
		return nodeID().toString0();
	}

	public ItemPath toItemPath() {
		return ItemPath.of(sdn(), nodeName);
	}

	public NodeDir setStateProp(String prop, Object value) {
		state().set(prop, value);
		return this;
	}

	public String getStateProp(String prop, String... defRq) {
		return state().get(prop, defRq);
	}

	public FormState stateCom(boolean... create) {
		FormState state = super.state();
		IT.state(state.isForm(), "except form state - %s", this);
		return state.stateCom(create);
	}

	public EventsStateTree stateEventsTree() {
		return new EventsStateTree(this);
	}

	@Override
	protected FormState newAfcState() {
		return AppStateFactory.ofFormName_orCreate(sdn(), nodeName());
	}

	//
	//

	public Path getPath_FormFcParent() {
		return getPath_FormFc_Data().getParent();
	}

	public Path getPath_FormFc_Data() {
		Pare sdn = sdn();
		return AppStatePath.getFormDataPath(sdn.keyStr(), sdn.valStr(), nodeName);
	}

	public Path getPath_FormFc_Props() {
		Pare sdn = sdn();
		return AppStatePath.getFormPropsPath(sdn.keyStr(), sdn.valStr(), nodeName);
	}

	public Path getPath_ComFc() {
		Pare sdn = sdn();
		return AppStatePath.getComStatePath(sdn.keyStr(), sdn.valStr(), nodeName);
	}


	//
	//

	public Path getStatePropsPath(AFC.AfcEntity afcEntity) {
		Pare<String, String> sdn = sdn();
		switch (afcEntity) {
			case PLANE:
				return AppStatePath.getPlaneStatePath(sdn.keyStr());
			case PAGE:
				return AppStatePath.getPageStatePath(sdn.keyStr(), sdn.valStr());
			case FORM:
				return AppStatePath.getFormPropsPath(sdn.keyStr(), sdn.valStr(), nodeName);
			case COM:
				return AppStatePath.getComStatePath(sdn.keyStr(), sdn.valStr(), nodeName);
			case PAGECOM:
				return AppStatePath.getPagecomStatePath(sdn.keyStr(), sdn.valStr(), nodeName);
			default:
				throw new WhatIsTypeException(afcEntity);
		}
	}

	public FormState getStateProps(AFC.AfcEntity afcEntity, boolean... create) {
		Pare<String, String> sdn = sdn();
		switch (afcEntity) {
			case PLANE:
				return AppStateFactory.ofPlaneName_orCreate(sdn.key(), create);
			case PAGE:
				return AppStateFactory.ofPageName_orCreate(sdn, create);
			case FORM:
				return AppStateFactory.ofFormName_orCreate(sdn, nodeName, create);
			case COM:
				return AppStateFactory.ofComName_orCreate(sdn, nodeName, create);
			case PAGECOM:
				return AppStateFactory.ofPagecomName_orCreate(sdn, nodeName, create);
			default:
				throw new WhatIsTypeException(afcEntity);
		}
	}


	//
	//

	public static NodeDir ofCurrentPage(String nodeName, boolean... create) {
		return ofNodeName(Sdn.getRq(), nodeName, create);
	}

	public static NodeDir ofNodeName(String plane, String page, String nodeName, boolean... create) {
		return ofNodeName(Pare.of(plane, page), nodeName, create);
	}

	public static NodeDir ofNodeName(Pare sdn, String noteName, boolean... create) {
		NodeDir nodeDir = new NodeDir(noteName, sdn);
		if (ARG.isDefEqTrue(create)) {
			nodeDir.createIfNotExist();
		}
		return nodeDir;
	}

	public static NodeDir ofDir(Pare sdn, Path nodeDir) {
		return new NodeDir(nodeDir, sdn);
	}

	public static NodeDir ofFile(Pare sdn, Path file_formOrProps) {
		return new NodeDir(file_formOrProps.getParent(), sdn);
	}

	//
	//

	public Path firstFile(GEXT gext, Path... defRq) {
		Path rslt = ARRi.first(fLsGEXT(gext), null);
		return rslt != null ? rslt : ARG.toDefThrow(() -> new RequiredRuntimeException("except file %s", gext), defRq);
	}

	//
	//

	public NodeDir cloneWithSd3(String sd3) {
		return NodeDir.ofNodeName(Pare.of(sd3, sdn().val()), nodeName());
	}

	public NodeDir cloneWithPage(String pagename) {
		return NodeDir.ofNodeName(Pare.of(sdn().key(), pagename), nodeName());
	}

	public NodeDir cloneWithItem(String nodeName, String pagename) {
		return NodeDir.ofNodeName(Pare.of(sdn().key(), pagename), nodeName);
	}

	public NodeDir cloneWithItem(String nodeName) {
		return NodeDir.ofNodeName(sdn(), nodeName);
	}

	//
	//

	public NVT nvt(NVT... defRq) {
		return state().viewType(defRq);
	}

	public NVM nvm_first(NVM... defRq) {
		return NVM.ofNodeFirst(this, defRq);
	}

	public NodeEvalType evalType(boolean strictValid, NodeEvalType... defRq) {
		return NodeEvalType.valueOf(state(), strictValid, defRq);
	}
	//
	//

	public String nodeData(String... defRq) {
		return state().nodeData(defRq);
	}

	public String nodeDataCached(boolean... fresh) {
		return state().nodeDataCached(fresh);
	}

	//
	//


	private NodeData nodeDataInjected;

	public NodeData nodeDataInjected(boolean... fresh) {
		return nodeDataInjected == null || ARG.isDefEqTrue(fresh) ? nodeDataInjected = nodeData0(null).newInjected(null) : nodeDataInjected;
	}

	public NodeData nodeDataInjected(TrackMap.TrackId trackId, boolean... fresh) {
		return nodeDataInjected == null || ARG.isDefEqTrue(fresh) ? nodeDataInjected = nodeData0(null).newInjected(trackId) : nodeDataInjected;
	}

	public NodeData nodeData0(String nodeData) {
		return NodeData.of(this, nodeData);
	}

	//
	//

	public NodeDir createIfNotExist() {
		UFS_BASE.MKDIR.createDirs(getPath_FormFcParent());
		return this;
	}

	public enum Beahviour {
		Inline, Absolute, Relative
	}

	public IZWin createForm(NVT nvt, Beahviour... withBehaviours) {
		return NFCreate.createForm(this, nvt, withBehaviours);
	}

	//
	//
	//

	public FormState.Fields fields() {
		return state().fields();
	}

	public NodeFileTransferMan fsMan() {
		return new NodeFileTransferMan(this);
	}

	@Override
	public String fCat(String... defRq) {
		return state().readFcData(defRq);
	}

	public String fCatCom(String... defRq) {
		return state().stateCom().readFcData(defRq);
	}


	//
	//
	//


	@Override
	public NodeDir toNode() {
		return this;
	}

	@Override
	public String toNodeData() {
		return nodeData();
	}

	@Override
	public String toNodeId() {
		return nodeId();
	}


	//
	//
	//

	@Override
	public String toString() {
		String head = getClass().getSimpleName() + SYMJ.ARROW_RIGHT_SPEC + nodeName + super.toString();
		return head;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof SiteDir)) {
			return false;
		}
		NodeDir siteDir = (NodeDir) o;
		return Objects.equals(nodeName, siteDir.nodeName) && Objects.equals(siteDirPath, siteDir.siteDirPath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodeName, siteDirPath);
	}

}
