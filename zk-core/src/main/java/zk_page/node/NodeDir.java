package zk_page.node;

import mpc.env.APP;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import zk_form.control.ErrLb;
import zk_notes.control.NodeFactory;
import zk_os.AFC;
import zk_page.node.fsman.NodeFileTransferMan;
import zk_page.node_state.FormState;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class NodeDir extends SiteDir<FormState> {

	//
	//

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


	public boolean hasSingleFile_Media() {
		return fLs(GEXT.MEDIA_GEXT).size() == 1;
	}

	public Path singleFile(GEXT gext, Path... defRq) {
		Path rslt = ARRi.first(fLs(gext), null);
		return rslt != null ? rslt : ARG.toDefThrow(() -> new RequiredRuntimeException("except file %s", gext), defRq);
	}

	public GEXT typeMedia(GEXT... defRq) {
		if (ARG.isNotDef(defRq)) {
			return typeBinaryPare().getKey();
		}
		Pare<GEXT, List<Path>> gextListPare = typeBinaryPare(null);
		if (gextListPare != null) {
			return gextListPare.key();
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("TypeBinary not defined from path", fPath()), defRq);
	}

	public Pare<GEXT, List<Path>> typeBinaryPare(Pare<GEXT, List<Path>>... defRq) {
		Path checkedFile = singleFile(GEXT.IMG, null);
		GEXT gext = null;
		if (checkedFile != null) {
			gext = GEXT.IMG;
		} else if ((checkedFile = singleFile(GEXT.VIDEO, null)) != null) {
			gext = GEXT.VIDEO;
		}
		if (gext != null) {
			return Pare.of(gext, ARR.as(checkedFile));
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("TypeBinary not defined from path '%s'", fPath()), defRq);
	}

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

	public NodeDir cloneRenameSd3(String sd3) {
		return NodeDir.ofNodeName(nodeName(), Pare.of(sd3, sdn().val()));
	}

	public NodeDir cloneRenameName(String nodeNmae) {
		return NodeDir.ofNodeName(nodeNmae, sdn());
	}

	public enum NVT {
		TEXT_WIN, SE_WIN, XML_WIN, HTML_BLOCK;

		public String nameHu() {
			switch (this) {
				case SE_WIN:
					return "WYSIWYG";
				case TEXT_WIN:
					return "TEXT";
				case XML_WIN:
					return "HTML_WIN";
				case HTML_BLOCK:
					return "HTML";
				default:
					throw new WhatIsTypeException(this);
			}
		}

//		public Class getViewType() {
//			switch (this) {
//				case SE_WIN:
//					return SeTbxWin.class;
//				case XML_WIN:
//					return Xml.class;
//				case TEXT_WIN:
//					return NotesTbxWin.class;
//				default:
//					throw new WhatIsTypeException(this);
//			}
//		}
//
//		public boolean wrapperWin() {
//			switch (this) {
//				default:
//					return true;
//			}
//		}

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
