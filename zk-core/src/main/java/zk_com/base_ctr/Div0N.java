package zk_com.base_ctr;

import lombok.Getter;
import zk_page.node.NodeDir;

public class Div0N extends Div0 {
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

	public Div0N(NodeDir nodeDir) {
		super();

		this.nodeDir = nodeDir;
	}

}