package zk_notes.coms;

import mpc.fs.UFS_BASE;
import mpc.fs.ext.GEXT;
import mpc.fs.path.IPath;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.func.FunctionV;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Events;
import zk_com.base.Img;
import zk_com.base.Lb;
import zk_com.base.Ln;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Div0Node;
import zk_com.core.IZStyle;
import zk_form.notify.ZKI_Quest;
import zk_notes.AxnTheme;
import zk_notes.control.NotesSpace;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.NodeCapsCom;
import mpe.img.EColor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class SingleNodeAudioMulti extends Div0Node {

	public SingleNodeAudioMulti(NodeDir nodeDir) {
		super(nodeDir);
	}

	@Override
	protected void init() {
		super.init();

		NodeDir node = getNodeDir();

		applyDropEvent();

		IPath proxy = node.getProxyRW().getTargetPathDir(null);

		IPath targetAnyIPathParent = proxy == null ? node.toIPath() : proxy;

		boolean isAllowedEdit = node.state().isAllowedAccess_EDIT();

		FunctionV addHead = () -> {

			IZStyle capLbCom;
			Component other = null;

			if (!isAllowedEdit) {
				capLbCom = new Lb(node.nodeName()).font_bold_nice(AxnTheme.FONT_SIZE_WIDGET_HEADER);
			} else {
				capLbCom = new NodeCapsCom.FormEditableName(node);
				capLbCom.font_bold_nice(AxnTheme.FONT_SIZE_WIDGET_HEADER);
				other = Ln.uploadTo(SYMJ.UPLOAD, targetAnyIPathParent.toPath(), AxnTheme.MAX_FILE_SIZE);
			}

			Div0 div0 = other != null ? Div0.of((Component) capLbCom, other) : Div0.of((Component) capLbCom);

			Div0 header = (Div0) div0.center().padding("10px").bgcolor(EColor.BLUE.nextColor()).border_radius("36px 36px 0px 0px");

			if (isAllowedEdit && capLbCom instanceof NodeCapsCom.FormEditableName) {
				((NodeCapsCom.FormEditableName) capLbCom).enableDisappearComs(() -> header.getComs(), 3_000);
			}

			appendChild(header);

		};

		Map<GEXT, List<Path>> gextListMap = targetAnyIPathParent.dMapGExt();

		boolean isImgf = gextListMap.containsKey(GEXT.IMG);

		FunctionV1<List<Path>> addImg = (imgs) -> {
			Img img;
			if (isImgf) {
				img = Img.of(imgs.get(0));
				img.applyInnerProps_Width(getFormState());
				appendChild(img);
			}
		};

		List<Path> imgs = gextListMap.get(GEXT.IMG);
		if (X.notEmpty(imgs)) {
			addImg.apply(imgs);
		} else {
			addHead.apply();
		}

		List<Path> audioFiles = gextListMap.get(GEXT.AUDIO);

		audioFiles.forEach(audioFile -> {

			IZStyle block = new SingleNodeAudio(getNodeDir(), audioFile).block();

			appendChild((Component) block);

		});

	}

	private void applyDropEvent() {

		addEventListener(Events.ON_DROP, e -> {
			Component dragged = ((DropEvent) e).getDragged();
			if (dragged instanceof SingleNodeImg) {
				SingleNodeImg img = (SingleNodeImg) dragged;
				NodeDir draggedNodeDir = img.getNodeDir();
				NodeDir node = getNodeDir();
				String msg = X.f("Move node img [%s] to [%s] ?", draggedNodeDir.nodeId(), node.nodeId());
				ZKI_Quest.showMessageBoxBlueYN("Moving node content", msg, yn -> {
					if (!yn) {
						return;
					}
					List<Path> paths = draggedNodeDir.dLsGEXT(GEXT.IMG);
					paths.forEach(p -> UFS_BASE.MV.moveIn(p, node.toPath(), null));
					ZKI_Quest.showMessageBoxBlueYN("Ok", X.f("Moving image successfully! Delete draggable node '%s'?", draggedNodeDir.nodeName()), yn2 -> {
						if (!yn2) {
							return;
						}
						draggedNodeDir.fdRmIfExist();
						NotesSpace.rerenderFirst();
					});
					NotesSpace.rerenderFirst();
				});
			}
		});
	}

}
