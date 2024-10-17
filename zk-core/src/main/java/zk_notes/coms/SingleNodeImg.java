package zk_notes.coms;

import lombok.Getter;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import org.zkoss.zk.ui.Page;
import zk_com.base.Img;
import zk_com.base_ctr.Menupopup0;
import zk_page.node.NodeDir;
import zk_page.node_state.FormState;

import java.nio.file.Path;

public class SingleNodeImg extends Img {

	@Getter
	private final NodeDir nodeDir;

	@Override
	public String getComStateName() {
		return nodeDir.nodeName();
	}

	public SingleNodeImg(NodeDir nodeDir) {
		super(nodeDir.singleFile(GEXT.IMG));
		this.nodeDir = nodeDir;
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
		FormState comStateJson = getComState_JSON();
		comStateJson.apply_WIDTH_HEIGHT(this);
	}

	public static void addContextMenu(Menupopup0 menupopup,SingleNodeImg singleNodeImg) {

		NodeDir nodeDir1 = singleNodeImg.getNodeDir();
		menupopup.addMenuitem_DeleteNode_WithSec("Remove this image..", nodeDir1);
		EXT props = EXT.JSON;

		Path comPath = nodeDir1.toComsPath(props);
		menupopup.addMenuitem_EDITOR("Edit props", comPath, true, props);
	}
}
