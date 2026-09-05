package zk_com.base_ctr;

import lombok.Getter;
import zk_notes.node.NodeDir;

public class Div0Node extends Div0 {
	@Getter
	private final NodeDir nodeDir;

	@Override
	public String getComName() {
		return nodeDir.nodeName();
	}

	@Override
	public String getFormName() {
		return nodeDir.nodeName();
	}

	public Div0Node(NodeDir nodeDir) {
		super();

		this.nodeDir = nodeDir;
	}

}
