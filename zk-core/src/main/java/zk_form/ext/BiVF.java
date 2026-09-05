package zk_form.ext;

import org.zkoss.zk.ui.Component;
import zk_com.base_ctr.Div0;
import zk_com.core.IZState;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.core.ZService;

public class BiVF extends Div0 implements IZState {//IHeadCom,

	NodeDir nodeDir;

	public BiVF(NodeDir nodeDir) {
		this.nodeDir = nodeDir;
	}

	@Override
	public String getFormName() {
		return nodeDir.nodeName();
	}

	@Override
	protected void init() {


		String s = nodeDir.nodeDataStrCached();
		appendLb("asd:" + s);

		ZService nodeSrv = nodeDir.stdSrv(null);

		Component com = nodeSrv.buildView(nodeDir, this);

		appendChild(com);

	}

}
