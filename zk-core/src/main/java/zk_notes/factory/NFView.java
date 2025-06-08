package zk_notes.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import zk_notes.coms.NoteTbxm;
import zk_notes.node.NodeDir;
import zk_page.ZKNFinder;

import java.util.List;

public class NFView extends NFCom {

	public static final Logger L = LoggerFactory.getLogger(NFView.class);

	public static NoteTbxm openIfNotFoundNodeForm(NodeDir nodeDir, Component... parent) {
		NoteTbxm node = nodeTbxm(nodeDir);
		if (node != null) {
			return node;
		}
		NFOpen.openNoteWin(nodeDir, parent);
		return null;
	}

	private static NoteTbxm nodeTbxm(NodeDir nodeDir) {
		List<Component> allNodeCom = ZKNFinder.findAllNodeCom(true, false);
		return (NoteTbxm) allNodeCom.stream().filter(c -> isComNodeTbxm(c, nodeDir)).findFirst().orElseGet(() -> null);
	}

	private static boolean isComNodeTbxm(Component c, NodeDir nodeDir) {
		return c instanceof NoteTbxm && ((NoteTbxm) c).getNodeDir().equals(nodeDir);
	}
}
