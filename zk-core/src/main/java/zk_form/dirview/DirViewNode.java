package zk_form.dirview;

import lombok.Getter;
import lombok.SneakyThrows;
import mpc.rfl.RFL;
import org.zkoss.zk.ui.Component;
import zk_notes.node.NodeDir;

import java.nio.file.Path;

public class DirViewNode extends DirView0 {

	final @Getter NodeDir nodeDir;

	@Override
	public String getFormName() {
		return nodeDir.nodeName();
	}

	@SneakyThrows
	@Override
	public Component newCom() {
		DirView newInst = RFL.inst0(getClass(), false, new Class[]{NodeDir.class, boolean.class}, new Object[]{getNodeDir(), open});
		newInst.openedAll(openedAll).filterFiles(filterFiles).applierDirMenu(applierDirMenu);
		return newInst;
	}

	public DirViewNode(NodeDir nodeDir, boolean open) {
		this(nodeDir, nodeDir.getSelfDir(), open);
	}

	public DirViewNode(NodeDir nodeDir, Path dir, boolean open) {
		super(dir, open);
		this.nodeDir = nodeDir;
	}
}
