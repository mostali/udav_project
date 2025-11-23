package zk_notes.coms;

import lombok.Getter;
import mpc.fs.ext.GEXT;
import org.zkoss.zk.ui.Page;
import zk_com.base.Img;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;

public class SingleNodeImg extends Img {

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

	public SingleNodeImg(NodeDir nodeDir) {
		super(nodeDir.firstFile(GEXT.IMG));
		this.nodeDir = nodeDir;
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
		ObjState comStateJson = getFormState();
		comStateJson.apply(this, ObjState.WIDTH_HEIGHT);
	}

}
