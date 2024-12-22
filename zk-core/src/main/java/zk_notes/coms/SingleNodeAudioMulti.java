package zk_notes.coms;

import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import org.zkoss.zk.ui.Component;
import zk_com.base.Lb;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Div0N;
import zk_com.core.IZStyle;
import zk_notes.*;
import zk_page.ZKC;
import zk_page.ZKColor;
import zk_page.node.NodeDir;

import java.nio.file.Path;
import java.util.List;

public class SingleNodeAudioMulti extends Div0N {

	public SingleNodeAudioMulti(NodeDir nodeDir) {
		super(nodeDir);

	}

	@Override
	protected void init() {
		super.init();

		NodeDir node = getNodeDir();

		List<Path> audioFiles = node.fLsGEXT(GEXT.AUDIO);

		boolean isAllowedSec = node.state().isAllowedAccess_Edit(true);
//		boolean isAllowedSec = Sec.isEditorAdminOwner();

		IZStyle capLbCom;
		Component other = null;

		if (!isAllowedSec) {
			capLbCom = new Lb(node.nodeName()).font_bold_nice(AppNotesTheme.FONT_SIZE_WIDGET_HEADER);
		} else {
			capLbCom = new ANMC.FormEditableName(node);
			capLbCom.font_bold_nice(AppNotesTheme.FONT_SIZE_WIDGET_HEADER);
			other = Ln.uploadTo(SYMJ.UPLOAD, node.fPath(), 10);
		}

		Div0 div0 = other != null ? Div0.of((Component) capLbCom, other) : Div0.of((Component) capLbCom);
		Div0 header = (Div0) div0.center().padding("10px").bgcolor(ZKColor.BLUES.nextColor()).border_radius("36px 36px 0px 0px");

		if (isAllowedSec) {
			ANM0.applyMenu_FormFileItem(header.getOrCreateMenupopup(ZKC.getFirstWindow()), node);
		}

		if (isAllowedSec && capLbCom instanceof ANMC.FormEditableName) {
			((ANMC.FormEditableName) capLbCom).enableSlaveComs(() -> header.getComs(), 3_000);
		}

		appendChild(header);

		for (Path audioFile : audioFiles) {
			appendChild((Component) new SingleNodeAudio(getNodeDir(), audioFile).block());
		}
	}


}
