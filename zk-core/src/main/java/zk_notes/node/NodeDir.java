package zk_notes.node;

import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS_BASE;
import mpc.fs.ext.EXT;
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
import zk_os.AFC;
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

	public static String loadData(Sdn rq, String clickedItemName) {
		return ofNodeName(rq, clickedItemName).nodeData();
	}

	public NodeDir setStateProp(String prop, Object defRq) {
		state().set(prop, defRq);
		return this;
	}

	public String getStateProp(String prop, String... defRq) {
		return state().get(prop, defRq);
	}

	public FormState newState() {
		return FormState.ofFormName_OrCreate(sdn(), nodeName());
	}

	public FormState newStateCom(boolean create) {
		IT.state(state().isForm(), "except form component (but is com type) - %s", this);
		return FormState.ofPathComFile_orCreate(sdn(), nodeName(), EXT.JSON, create);
	}

	public Path getPathFormFc() {
		return getPathFormFc(EXT.PROPS);
	}

	public Path getPathFormFc(EXT ext) {
		Pare sdn = sdn();
		return AFC.FORMS.getRpaFormStatePath(sdn.keyStr(), sdn.valStr(), nodeName, ext);
	}

	public Path getPathComFc() {
		return getPathComFc(EXT.JSON);
	}

	public Path getPathComFc(EXT ext) {
		Pare sdn = sdn();
		return AFC.COMS.getRpaComStatePath(sdn.keyStr(), sdn.valStr(), nodeName, ext);
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

	public Path firstFile(GEXT gext, Path... defRq) {
		Path rslt = ARRi.first(fLsGEXT(gext), null);
		return rslt != null ? rslt : ARG.toDefThrow(() -> new RequiredRuntimeException("except file %s", gext), defRq);
	}

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

	public FormState.Fields upd() {
		return state().fields();
	}

	public ItemPath toItemPath() {
		return ItemPath.of(sdn(), nodeName);
	}

	public NodeID nodeID() {
		return new NodeID(nodeName(), sdn());
	}

	public String nodeId() {
		return nodeID().string();
	}

	public NVM nvm_first(NVM... defRq) {
		return NVM.ofNodeFirst(this, defRq);
	}

	public String nodeData(String... defRq) {
		return state().nodeData(defRq);
	}

	public String nodeDataCached(boolean... fresh) {
		return state().nodeDataCached(fresh);
	}

	public NodeEvalType evalType(boolean strictValid, NodeEvalType... defRq) {
		return NodeEvalType.valueOf(state(), strictValid, defRq);
	}

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

	public Path getPathFormFcParent() {
		return getPathFormFc().getParent();
	}

	public NVT nvt(NVT... defRq) {
		return state().viewType(defRq);
	}

	//
	//
	//

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

	public NodeDir createIfNotExist() {
		UFS_BASE.MKDIR.createDirs(getPathFormFcParent());
		return this;
	}

	public IZWin createForm(NVT nvt) {
		return NFCreate.createForm(this, nvt);
	}

	public Path toPathEventTree() {
		return AFC.EVENTS.toPathNodeEventTreePath(Sdn.getRq(), nodeName());
	}

	public ICtxDb getNodeEventTree() {
		return getEventTree(this);
	}

	private static UTree getEventTree(NodeDir nodeDir) {
		Path pathNodeEventTreePath = nodeDir.toPathEventTree();
//		UFS_BASE.MKDIR.createDirs(pathNodeEventTreePath.getParent());
//		ICtxDb.
		UTree tree = UTree.tree(pathNodeEventTreePath);
//		tree.createDbIfNotExists();
		if (tree.isEmptyDb()) {
			tree.put("node.id", nodeDir.nodeId());
		}
//		ICtxDb.of(pathNodeEventTreePath).checkLazyCreateDb();
		return tree;
	}
}
