package zk_notes.node;

import lombok.Setter;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import mpe.call_msg.core.INode;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpe.call_msg.core.NodeID;
import zk_com.core.IZWin;
import zk_notes.factory.NFCreate;
import zk_notes.node.core.NVM;
import zk_notes.node.core.NVT;
import mpe.call_msg.injector.TrackMap;
import zk_notes.node_srv.NodeEvalType;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.AppStatePath;
import zk_notes.node_state.ProxyRW;
import zk_notes.node_state.impl.ComState;
import zk_notes.node_srv.EventsStateTree;
import zk_notes.node_state.impl.FormState;
import zk_os.coms.AFC;
import udav_net.apis.zznote.ItemPath;
import zk_notes.fsman.NodeFileTransferMan;
import zk_notes.node_state.ObjState;
import mpe.call_msg.injector.NodeData;
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

	@Override
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
		return new NodeID(sdnPare(), nodeName());
	}

	public String nodeId() {
		return nodeID().toString0();
	}

	public ItemPath toItemPath() {
		return ItemPath.of(sdnPare(), nodeName);
	}

//	public NodeDir setStateProp(String prop, Object value) {
//		state().set(prop, value);
//		return this;
//	}
//
//	public String getStateProp(String prop, String... defRq) {
//		return state().get(prop, defRq);
//	}

	public ComState stateCom(boolean... create) {
		FormState state = super.state();
		IT.state(state.isForm(), "except form state - %s", this);
		return state.stateCom(create);
	}

	public EventsStateTree stateEventsTree() {
		return new EventsStateTree(this);
	}

	@Override
	protected FormState newAfcState() {
		return AppStateFactory.forForm(sdnPare(), nodeName());
	}

	//
	//

	public Path getSelfDir() {
		return getPath_FormFcParent();
	}

	public Path getPath_FormFcParent() {
		return getPath_FormFc_Data().getParent();
	}

	public Path getPath_FormFc_Data() {
		Pare sdn = sdnPare();
		return AppStatePath.getFormDataPath(sdn.keyStr(), sdn.valStr(), nodeName);
	}

	public Path getPath_FormFc_Props() {
		Pare sdn = sdnPare();
		return AppStatePath.getFormPropsPath(sdn.keyStr(), sdn.valStr(), nodeName);
	}

	public Path getPath_ComFc() {
		Pare sdn = sdnPare();
		return AppStatePath.getComPropsPath(sdn.keyStr(), sdn.valStr(), nodeName);
	}


	//
	//

	public Path getStatePropsPath(AFC.AfcEntity afcEntity) {
		Pare<String, String> sdn = sdnPare();
		switch (afcEntity) {
			case PLANE:
				return AppStatePath.getPlanePropsPath(sdn.keyStr());
			case PAGE:
				return AppStatePath.getPagePropsPath(sdn.keyStr(), sdn.valStr());
			case FORM:
				return AppStatePath.getFormPropsPath(sdn.keyStr(), sdn.valStr(), nodeName);
			case COM:
				return AppStatePath.getComPropsPath(sdn.keyStr(), sdn.valStr(), nodeName);
			case PAGECOM:
				return AppStatePath.getPagecomPropsPath(sdn.keyStr(), sdn.valStr(), nodeName);
			default:
				throw new WhatIsTypeException(afcEntity);
		}
	}

	public ObjState getStateProps(AFC.AfcEntity afcEntity, boolean... create) {
		Pare<String, String> sdn = sdnPare();
		switch (afcEntity) {
			case PLANE:
				return AppStateFactory.forPlane(sdn.key(), create);
			case PAGE:
				return AppStateFactory.forPage(sdn, create);
			case FORM:
				return AppStateFactory.forForm(sdn, nodeName, create);
			case COM:
				return AppStateFactory.forCom(sdn, nodeName, create);
			case PAGECOM:
				return AppStateFactory.forPagecom(sdn, nodeName, create);
			default:
				throw new WhatIsTypeException(afcEntity);
		}
	}


	//
	//

	public static NodeDir ofCurrentPage(String nodeName, boolean... create) {
		return ofNodeName(Sdn.get(), nodeName, create);
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
		return NodeDir.ofNodeName(Pare.of(sd3, sdnPare().val()), nodeName());
	}

	public NodeDir cloneWithPage(String pagename) {
		return NodeDir.ofNodeName(Pare.of(sdnPare().key(), pagename), nodeName());
	}

	public NodeDir cloneWithItem(String nodeName, String pagename) {
		return NodeDir.ofNodeName(Pare.of(sdnPare().key(), pagename), nodeName);
	}

	public NodeDir cloneWithItem(String nodeName) {
		return NodeDir.ofNodeName(sdnPare(), nodeName);
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

	public String nodeDataStr(String... defRq) {
		return state().nodeData(defRq);
	}

	public String nodeDataStrCached(boolean... fresh) {
		return state().nodeDataCached(fresh);
	}

	//
	//


	private @Setter NodeData nodeDataInjected;

	public String injectStr(boolean... fresh) {
		return inject(fresh).nodeDataStr();
	}

	public NodeData inject(boolean... fresh) {
		return nodeDataInjected == null || ARG.isDefEqTrue(fresh) ? nodeDataInjected = INode.super.inject() : nodeDataInjected;
	}

	public NodeData inject(TrackMap.TrackId trackId, boolean... fresh) {
		return nodeDataInjected == null || ARG.isDefEqTrue(fresh) ? nodeDataInjected = INode.super.inject(trackId) : nodeDataInjected;
	}

	//
	//
	public NodeDir createIfNotExist() {
		UFS.MKDIR.createDirs(getSelfDir());
		return this;
	}

	public ProxyRW.NodeProxyRW getProxyRW(boolean... injected) {
		return ProxyRW.NodeProxyRW.of(this, injected);
	}


	public enum Behaviour {
		Inline, Absolute, Relative
	}

	public IZWin createForm(NVT nvt, Behaviour... withBehaviours) {
		return NFCreate.createForm(this, nvt, withBehaviours);
	}

	//
	//
	//

	public ObjState.Fields fields() {
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
	public NodeDir toNodeImpl() {
		return this;
	}

	@Override
	public String readNodeDataStr() {
		return nodeDataStr();
	}

	@Override
	public String toObjId() {
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
