package zk_notes.factory;

import lombok.RequiredArgsConstructor;
import org.zkoss.zul.Window;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVT;

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

	protected NFOpen.OptsOpenNode _opts;

	public NodeCom opts(NFOpen.OptsOpenNode opts) {
		this._opts = opts;
		return (NodeCom) this;
	}

	protected NVT _nvt;

	public NodeCom nvt(NVT nvt) {
		this._nvt = nvt;
		return (NodeCom) this;
	}
}
