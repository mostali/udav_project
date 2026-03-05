package zk_form.dirview;

import lombok.Getter;
import zk_form.tree.CtxTreeView;
import zk_notes.node.NodeDir;

public class TreeViewNode extends CtxTreeView {

	final @Getter NodeDir nodeDir;

	public TreeViewNode(NodeDir nodeDir, boolean isModal) {
		super(nodeDir.stateEventsTree().getDb().getDbFilePath().toString(), isModal);
		this.nodeDir = nodeDir;
	}

	public TreeViewNode(NodeDir nodeDir, String pathFileDb, boolean isModal) {
		super(pathFileDb, isModal);
		this.nodeDir = nodeDir;
	}

	@Override
	public String getFormName() {
		return nodeDir.nodeName();
	}

}
