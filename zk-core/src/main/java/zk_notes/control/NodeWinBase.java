package zk_notes.control;

import lombok.RequiredArgsConstructor;
import org.zkoss.zul.Window;
import zk_notes.node.NodeDir;

@RequiredArgsConstructor
public class NodeWinBase {

	public final NodeDir nodeDir;

	protected boolean absMode;

	public NodeCom absMode(boolean absMode) {
		this.absMode = absMode;
		return (NodeCom) this;
	}

	protected Window.Mode _mode;

	public NodeCom mode(Window.Mode mode) {
		this._mode = mode;
		return (NodeCom) this;
	}

	protected String _title;

	public NodeCom title(String title) {
		this._title = title;
		return (NodeCom) this;
	}

	protected NodeFactory.OptsOpenNode _opts;

	public NodeCom opts(NodeFactory.OptsOpenNode opts) {
		this._opts = opts;
		return (NodeCom) this;
	}

	protected NodeDir.NVT _nvt;

	public NodeCom nvt(NodeDir.NVT nvt) {
		this._nvt = nvt;
		return (NodeCom) this;
	}
}
