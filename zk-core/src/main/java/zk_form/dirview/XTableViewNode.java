package zk_form.dirview;

import lombok.Getter;
import zk_form.tree.XTableView;
import zk_notes.node.NodeDir;

public class XTableViewNode extends XTableView {

	final @Getter NodeDir nodeDir;

	public XTableViewNode(NodeDir nodeDir, boolean isModal) {
		super(nodeDir.stateEventsTree().getDb().getDbFilePath().toString(), isModal);
		this.nodeDir = nodeDir;
	}

	public XTableViewNode(NodeDir nodeDir, String pathFileDb, boolean isModal) {
		super(pathFileDb, isModal);
		this.nodeDir = nodeDir;
	}

	@Override
	public String getFormName() {
		return nodeDir.nodeName();
	}

}
