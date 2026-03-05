package zk_notes.node;

import lombok.Setter;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import mpe.cmsg.core.INode;
import mpe.cmsg.core.INodeType;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpe.cmsg.ns.NodeID;
import zk_com.core.IZWin;
import zk_notes.factory.NFItem;
import zk_notes.node.core.NVM;
import zk_notes.node.core.NVT;
import mpe.cmsg.TrackMap;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.AppStatePath;
import zk_notes.node_state.proxy.NodeProxyRW;
import zk_notes.node_state.impl.ComState;
import zk_notes.node_srv.EventsStateTree;
import zk_notes.node_state.impl.FormState;
import udav_net.apis.zznote.ItemPath;
import zk_notes.factory.NFTrans;
import zk_notes.node_state.ObjState;
import mpe.cmsg.NodeData;
import zk_os.core.Sdn;

import java.nio.file.Path;
import java.util.Objects;

public class NodeDir extends SiteDir<FormState> implements INode<NodeDir> {

	public static NodeDir ofNodeIdStr(String nodeID) {
		return ofNodeId(NodeID.of(nodeID));
	}

	public static NodeDir of(String nodeID, NodeDir... defRq) {
		NodeDir nodeDir = ofNodeId(nodeID);
		if (nodeDir.existNode(false)) {
			return nodeDir;
		}
		return ARG.throwMsg(() -> X.f("Node '%s' not found", nodeID), defRq);
	}

	public static NodeDir ofNodeId(String nodeID) {
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
		Path rslt = ARRi.first(dLsGEXT(gext), null);
		return rslt != null ? rslt : ARG.throwErr(() -> new RequiredRuntimeException("except file %s", gext), defRq);
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

	private NVM nvm;

	public NVM nvm_first_auto_cached() {
		return nvm != null ? nvm : (nvm = nvm_first_auto(null));
	}

	public NVM nvm_first_auto(NVM... defRq) {
		return NVM.getAutoType(this, defRq);
	}

	private INodeType nodeType;

	public INodeType evalType(INodeType... defRq) {
		if (nodeType != null) {
			return nodeType;
		}
		return this.nodeType = defineNodeType(false, defRq);
	}

//	public INodeType evalTypeStrict(boolean strictValid, INodeType... defRq) {
//
//		FormState state = state();
//		//Wth?
//		ICallMsg iCallMsg = new ICallMsg() {
//			@Override
//			public String toObjMsgId(String... defRq) {
//				return state.nodeDir().toObjId();
//			}
//
//			@Override
//			public String iNodeDataCached(boolean... fresh) {
//				return state.nodeDataCached(fresh);
//			}
//
//			@Override
//			public Object getFromSrc() {
//				return state.nodeDir();
//			}
//
//		};
//		INodeType iNodeType = ICallMsg.defineNodeType(iCallMsg, strictValid, defRq);

	/// /		return this.nodeType = iNodeType;
//		return iNodeType;
//	}
	//
	//
	public String nodeDataStr(String... defRq) {
		return state().nodeData(defRq);
	}

	public String nodeDataStrCached(boolean... fresh) {
		resetCache(fresh);
		return state().nodeDataCached(fresh);
	}

	public void resetCache(boolean... fresh) {
		if (ARG.isDefEqTrue(fresh)) {
			nodeDataInjected = null;
			nodeType = null;
			nvm = null;
		}
	}

	//
	//


	private @Setter NodeData nodeDataInjected;

	public String injectStr(boolean... fresh) {
		resetCache(fresh);
		return inject(fresh).nodeDataStr();
	}

	public NodeData inject(boolean... fresh) {
		resetCache(fresh);
		return nodeDataInjected == null || ARG.isDefEqTrue(fresh) ? nodeDataInjected = INode.super.inject() : nodeDataInjected;
	}

	public NodeData inject(TrackMap.TrackId trackId, boolean... fresh) {
		resetCache(fresh);
		return nodeDataInjected == null || ARG.isDefEqTrue(fresh) ? nodeDataInjected = INode.super.inject(trackId) : nodeDataInjected;
	}

	//
	//
	public NodeDir createIfNotExist() {
		UFS.MKDIR.createDirs(getSelfDir());
		return this;
	}

	public NodeProxyRW getProxyRW(boolean... injected) {
		return NodeProxyRW.of(this, injected);
	}

	public static IZWin toIZWin(NodeDir nodeDir, NVT def) {

		NVM nvm = nodeDir.nvm_first_auto(null);

		if (nvm != null) {
			return (IZWin) NFItem.createForm_MEDIA(nodeDir, nvm);
		}

		NVT nvt = def != null ? def : nodeDir.nvt(NVT.TEXT);

		return NFItem.createForm(nodeDir, nvt);

	}


	public enum Behaviour {
		Inline, Absolute, Relative
	}

	public IZWin createForm(NVT nvt, Behaviour... withBehaviours) {
		return NFItem.createForm(this, nvt, withBehaviours);
	}

	//
	//
	//

	public ObjState.Fields fields() {
		return state().fields();
	}

	public NFTrans fsMan() {
		return new NFTrans(this);
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
	public String readNodeDataStr(String... defRq) {
		return nodeDataStr(defRq);
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
