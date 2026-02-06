package zk_form.dirview;

import lombok.Getter;
import zk_form.tree.CtxTreeView;
import zk_notes.node.NodeDir;

public class TreeViewNode extends CtxTreeView {

//	private static String toDbPathWithCreateDb(NodeDir nodeDir) {
//		return nodeDir.getNodeEventTree().getDbFilePath().toString();
//	}


	;
	final @Getter NodeDir nodeDir;

	public TreeViewNode(NodeDir nodeDir, boolean isModal) {
		super(nodeDir.stateEventsTree().getDb().getDbFilePath().toString(), isModal);
		this.nodeDir = nodeDir;
	}

	@Override
	public String getFormName() {
		return nodeDir.nodeName();
	}

//	@SneakyThrows
//	@Override
//	public Component newCom() {
//		DirView newInst = RFL.inst_(getClass(), false, new Class[]{NodeDir.class, boolean.class}, new Object[]{getNodeDir(), open});
//		newInst.openedAll(openedAll).filterFiles(filterFiles).applierDirMenu(applierDirMenu);
//		return newInst;
//	}

//	public TreeViewNode(NodeDir nodeDir, boolean open) {
//		super(nodeDir.getPathFormFcParent(), open);
//		this.nodeDir = nodeDir;
//	}
}
