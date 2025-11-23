package zk_notes.coms;

import mpc.fs.ext.GEXT;
import mpc.str.sym.SYMJ;
import org.zkoss.zk.ui.Component;
import zk_com.base.Lb;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Div0Node;
import zk_com.core.IZStyle;
import zk_notes.AxnTheme;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.NodeCapsCom;
import zk_page.ZKColor;

import java.nio.file.Path;
import java.util.List;

public class SingleNodeAudioMulti extends Div0Node {

	public SingleNodeAudioMulti(NodeDir nodeDir) {
		super(nodeDir);

	}

	@Override
	protected void init() {
		super.init();

		NodeDir node = getNodeDir();

		List<Path> audioFiles = node.fLsGEXT(GEXT.AUDIO);

		boolean isAllowedEdit = node.state().isAllowedAccess_EDIT();
//		boolean isAllowedSec = Sec.isEditorAdminOwner();

		IZStyle capLbCom;
		Component other = null;

		if (!isAllowedEdit) {
			capLbCom = new Lb(node.nodeName()).font_bold_nice(AxnTheme.FONT_SIZE_WIDGET_HEADER);
		} else {
			capLbCom = new NodeCapsCom.FormEditableName(node);
			capLbCom.font_bold_nice(AxnTheme.FONT_SIZE_WIDGET_HEADER);
			other = Ln.uploadTo(SYMJ.UPLOAD, node.toPath(), AxnTheme.MAX_FILE_SIZE);
		}

		Div0 div0 = other != null ? Div0.of((Component) capLbCom, other) : Div0.of((Component) capLbCom);
		Div0 header = (Div0) div0.center().padding("10px").bgcolor(ZKColor.BLUE.nextColor()).border_radius("36px 36px 0px 0px");

//		if (isAllowedEdit) {
//			Menupopup0 menu = header.getOrCreateMenupopup(ZKC.getFirstWindow());
//			ANMF.applyMenu_FormFileItem(menu, node);
//			ANM.applyMenu_OpenAs(menu, node);
//
//		}

		if (isAllowedEdit && capLbCom instanceof NodeCapsCom.FormEditableName) {
			((NodeCapsCom.FormEditableName) capLbCom).enableDisappearComs(() -> header.getComs(), 3_000);
		}

		appendChild(header);

		for (Path audioFile : audioFiles) {
			appendChild((Component) new SingleNodeAudio(getNodeDir(), audioFile).block());
		}
	}


}
