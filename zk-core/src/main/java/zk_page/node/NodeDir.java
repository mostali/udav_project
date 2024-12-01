package zk_page.node;

import mpc.env.APP;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import mpe.wthttp.CleanDataResponseException;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_form.control.ErrLb;
import zk_notes.apiv1.NodeApiCallType;
import zk_notes.control.NodeFactory;
import zk_os.AFC;
import zk_os.core.ItemPath;
import zk_page.node.fsman.NodeFileTransferMan;
import zk_page.node_state.FormState;

import java.nio.file.Path;
import java.util.Objects;

public class NodeDir extends SiteDir<FormState> {

	//
	//

	public Object call() {
		FormState state = state();
		String exe = state.upd().get_EXE(null);
		if (X.notEmpty(exe)) {
			try {
				NodeApiCallType.checkExeParam(sdn(), state.pathFc(), nodeName, exe, state.get("jp", null));
			} catch (CleanDataResponseException ex) {
				if (ex.status == 200) {
					return ex.getCleanData();
				} else {
					throw ex;
				}
			}
		}
		return FormState.ofFormName(nodeName(), sdn());
	}


	public FormState newState() {
		return FormState.ofFormName(nodeName(), sdn());
	}

	public FormState newStateCom() {
		IT.state(state().isForm(), "except form component (but is com type) - %s", this);
		return FormState.ofPathComFile_OrCreate(sdn(), nodeName(), EXT.JSON, false);
	}

	//
	//
	public static NodeDir ofNodeName(String noteName, Pare sdn) {
		return new NodeDir(noteName, sdn);
	}

	public static NodeDir ofDir(Path nodeDir, Pare sdn) {
		return new NodeDir(nodeDir, sdn);
	}

	public static NodeDir ofFile(Path file_formOrProps, Pare sdn) {
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


//	public boolean hasSingleFile_Media() {
//		return fLsGEXT(GEXT.G_GEXT_MEDIA).size() == 1;
//	}

	public Path firstFile(GEXT gext, Path... defRq) {
		Path rslt = ARRi.first(fLsGEXT(gext), null);
		return rslt != null ? rslt : ARG.toDefThrow(() -> new RequiredRuntimeException("except file %s", gext), defRq);
	}

//	public GEXT typeMedia(GEXT... defRq) {
//		if (ARG.isNotDef(defRq)) {
//			return typeBinaryPareSinglyUniq().getKey();
//		}
//		Pare<GEXT, List<Path>> gextListPare = typeBinaryPareSinglyUniq(null);
//		if (gextListPare != null) {
//			return gextListPare.key();
//		}
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("TypeBinary not defined from path", fPath()), defRq);
//	}
//
//	public Pare<GEXT, List<Path>> typeBinaryPareSinglyUniq(Pare<GEXT, List<Path>>... defRq) {
//		Path checkedFile = firstFile(GEXT.IMG, null);
//		GEXT gext = null;
//		if (checkedFile != null) {
//			gext = GEXT.IMG;
//		} else if ((checkedFile = firstFile(GEXT.VIDEO, null)) != null) {
//			gext = GEXT.VIDEO;
//		}
//		if (gext != null) {
//			return Pare.of(gext, ARR.as(checkedFile));
//		}
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("TypeBinary not defined from path '%s'", fPath()), defRq);
//	}
//
//	public Map<GEXT, List<Path>> typeBinaryPareFirstAny(Pare<GEXT, List<Path>>... defRq) {
//		Map<Path, EXT> map = fMapExt().map();
//		for (Map.Entry<Path, EXT> entry : map.entrySet()) {
//			switch (GEXT.of(entry.getKey())){
//				case IMG:
//				case AUDIO:
//				case AUDIO:
//			}
//		}
//		Map.Entry<Path, EXT> path = UMap.getByValueFirst(map, EXT.G_BINARY, null);
//		if (path != null) {
//		}
//		Path checkedFile = firstFile(GEXT.IMG, null);
//		GEXT gext = null;
//		if (checkedFile != null) {
//			gext = GEXT.IMG;
//		} else if ((checkedFile = firstFile(GEXT.VIDEO, null)) != null) {
//			gext = GEXT.VIDEO;
//		}
//		if (gext != null) {
//			return Pare.of(gext, ARR.as(checkedFile));
//		}
//		return ARG.toDefThrow(() -> new RequiredRuntimeException("TypeBinary not defined from path '%s'", fPath()), defRq);
//	}

	public NVT nvt(NVT... defRq) {
		return state().viewType(defRq);
	}

	public Pare<NodeDir, Component> buildSingleCom(HtmlBasedComponent parent) {
		Path path = fPath();
		try {
			Pare<NodeDir, Component> nodeDirComponentPare = NodeFactory.buildComMapImpl(parent, path, sdn());
			if (L.isDebugEnabled()) {
				L.debug("buildCom:" + nodeDirComponentPare);
			}
			return nodeDirComponentPare;
		} catch (Exception ex) {
			L.error("Unexcepted buildAndAppendNote:" + path, ex);
			if (APP.isDebugEnable()) {
				return Pare.of(NodeDir.ofDir(path, sdn()), new ErrLb("Unexcepted err:" + nodeName(), ex));
			}
			return null;
		}
	}

	public Path toComsPath(EXT ext) {
		Pare sdn = sdn();
		return AFC.getRpaComStatePath(sdn.keyStr(), sdn.valStr(), nodeName, ext);
	}

	public NodeDir cloneWithSd3(String sd3) {
		return NodeDir.ofNodeName(nodeName(), Pare.of(sd3, sdn().val()));
	}

	public NodeDir cloneWithPage(String pagename) {
		return NodeDir.ofNodeName(nodeName(), Pare.of(sdn().key(), pagename));
	}

	public NodeDir cloneWithItem(String nodeName) {
		return NodeDir.ofNodeName(nodeName, sdn());
	}

	public FormState.Upd upd() {
		return state().upd();
	}

	public ItemPath toItemPath() {
		return ItemPath.of(sdn(), nodeName);
	}

	public enum NVT {
		TEXT, //
		WYSIWYG, //
		HTML_WIN, //
		HTML, //
		MD_WIN, //
		MD, //
//		PDF_WIN//
		;

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
	}

	//
	//
	//

	public NodeFileTransferMan fsMan() {
		return new NodeFileTransferMan(this);
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
