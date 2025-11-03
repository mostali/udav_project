package zk_page.index;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Window;
import mpe.call_msg.core.NodeID;
import utl_rest.StatusException;
import zk_notes.control.NotesPSP;
import zk_notes.control.NotesSpace;
import zk_notes.factory.NodeCom;
import zk_notes.node.NodeDir;

public class IndexItemNPSP extends IndexRootNPSP {

	private final NodeID nodeID;

	public IndexItemNPSP(Window window, NodeID nodeID) {
		super(window);
		this.nodeID = nodeID;
	}

	@Override
	public void buildPage() {

		NotesPSP.initStyleWindowDefault(window);

		NotesSpace notesSpace = NotesSpace.initOnPage(window, true);

		HtmlBasedComponent parent = notesSpace == null ? window : notesSpace;

		NodeDir nodeDir = NodeDir.ofNodeId(nodeID);
		NodeCom nodeCom = NodeCom.of(nodeDir);

		if (!nodeDir.existNode(false)) {
			throw StatusException.C400("Not found");
		}
		nodeCom.buildAndAppendChildIn(parent);

//		Pare<NodeDir, Component> formNOL = NFCreate.createFormNOL(nodeDir, true);
//		if (formNOL != null) {
//			parent.appendChild(formNOL.val());
//		} else {

//			IZCom formCustom = NFCreate.createForm_CUSTOM(nodeDir);
//			if (formCustom != null) {
//				parent.appendChild((Component) formCustom);
//			}
//		}
//		window.appendChild(Xml.H(1, nodeID));

	}

}
