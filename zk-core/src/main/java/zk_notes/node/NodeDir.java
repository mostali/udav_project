package zk_notes.node;

import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import udav_net.apis.zznote.NodeID;
import zk_notes.node_srv.TrackMap;
import zk_notes.node_srv.core.NodeEvalType;
import zk_os.AFC;
import udav_net.apis.zznote.ItemPath;
import zk_notes.node_srv.fsman.NodeFileTransferMan;
import zk_notes.node_state.FormState;
import zk_os.core.NodeData;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NodeDir extends SiteDir<FormState> {

	public static NodeDir ofNodeIdStr(String nodeID) {
		return ofNodeId(NodeID.of(nodeID));
	}

	public static NodeDir ofNodeId(NodeID nodeID) {
		return ofNodeName(nodeID.sdn(), nodeID.item());
	}

	public static Pare3<String, String, String> getJobKey_GNT(NodeDir node) {
		//		String nodeSdnName = node.nodeNameWithSdn();
		String nodeSdnName = node.nodeID().string();
		String jGroup = "G[" + nodeSdnName + "]";
		String jName = "N[" + nodeSdnName + "]";
		String jTriggerId = jName;
		return Pare3.of(jGroup, jName, jTriggerId);
	}

	public FormState newState() {
		return FormState.ofFormName_OrCreate(sdn(), nodeName());
	}

	public FormState newStateCom() {
		IT.state(state().isForm(), "except form component (but is com type) - %s", this);
		return FormState.ofPathComFile_orCreate(sdn(), nodeName(), EXT.JSON, false);
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
	public static NodeDir ofNodeName(Pare sdn, String noteName) {
		return new NodeDir(noteName, sdn);
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

	public NodeEvalType evalType(boolean strictValid, NodeEvalType... defRq) {
		return NodeEvalType.valueOf(state(), strictValid, defRq);
	}

	private NodeData nodeDataInjected;

	public NodeData nodeDataInjected(boolean... fresh) {
		return nodeDataInjected == null || ARG.isDefEqTrue(fresh) ? nodeDataInjected = nodeData0(null).fillIfEmpty(null) : nodeDataInjected;
	}

	public NodeData nodeDataInjected(TrackMap.TrackId trackId, boolean... fresh) {
		return nodeDataInjected == null || ARG.isDefEqTrue(fresh) ? nodeDataInjected = nodeData0(null).fillIfEmpty(trackId) : nodeDataInjected;
	}

	public NodeData nodeData0(String nodeData) {
		return NodeData.of(this, nodeData);
	}

	//NoteViewMedia
	public enum NVM {
		IMG, AUDIO, VIDEO;

		public static NVM ofNodeFirst(NodeDir nodeDir, NVM... defRq) {
			Map<GEXT, List<Path>> map = nodeDir.dMapGExt();
			if (map.containsKey(GEXT.IMG)) {
				return NVM.IMG;
			} else if (map.containsKey(GEXT.AUDIO)) {
				return NVM.AUDIO;
			} else if (map.containsKey(GEXT.VIDEO)) {
				return NVM.VIDEO;
			}
			return ARG.toDefThrowMsg(() -> X.f("Media type not found"), defRq);
		}
	}

	public NVT nvt(NVT... defRq) {
		return state().viewType(defRq);
	}

	//NoteViewType/Text
	public enum NVT {
		TEXT, //
		WYSIWYG, //
		HTML_WIN, //
		HTML, //
		MD_WIN, //
		MD, //
		//		PDF_WIN//
//		PRETTYCODE_WIN,
		PRETTYCODE;

		public String nameHu() {
			switch (this) {
				default:
					return name();
			}
		}

		public boolean isExt() {
			switch (this) {
				case TEXT:
				case HTML:
				case WYSIWYG:
					return false;
				default:
					return true;
			}
		}

		public boolean isWindowMode() {
			switch (this) {
				case WYSIWYG:
				case TEXT:
				case HTML_WIN:
				case MD_WIN:
					return true;
				case HTML:
				case MD:
				case PRETTYCODE:
					return false;
				default:
					throw new WhatIsTypeException(this);
			}
		}
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
