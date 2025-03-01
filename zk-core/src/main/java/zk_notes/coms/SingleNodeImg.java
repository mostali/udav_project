package zk_notes.coms;

import lombok.Getter;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import org.zkoss.zk.ui.Page;
import zk_com.base.Img;
import zk_com.base_ctr.Menupopup0;
import zk_notes.ANI;
import zk_notes.events.ANM;
import zk_notes.events.AppEventsFD;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FormState;

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
		FormState comStateJson = getFormState_PROPS();
		comStateJson.apply_WIDTH_HEIGHT(this);
	}

	public static void addContextMenu(Menupopup0 menupopup, NodeDir nodeDir) {
		menupopup.addMI_DeleteNode_WithSec(ANI.DELETE_ENTITY + " Remove media '" + nodeDir.nodeName() + "'", nodeDir);

		menupopup.addMI_EDITOR(ANI.EDIT_MODE + " Edit props", nodeDir.state().pathProps(), true, EXT.JSON);

		menupopup.add_______();

		AppEventsFD.applyEvent_OPENDIR(menupopup, nodeDir.toPath());

		ANM.ANM_Mark.applySecurityItems(menupopup, nodeDir.state());

	}
}
