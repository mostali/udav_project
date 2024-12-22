package zk_notes.coms;

import lombok.Getter;
import mpc.fs.ext.GEXT;
import mpu.Sys;
import mpu.X;
import org.zkoss.video.AVideo;
import org.zkoss.zk.ui.Page;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_com.ext.uploader.AdvVideo;
import zk_page.ZKC;
import zk_page.node.NodeDir;
import zk_page.node_state.FormState;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class SingleNodeVideo extends AdvVideo implements IZCom {

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

	public SingleNodeVideo(NodeDir nodeDir) {
		super();

		this.nodeDir = nodeDir;

		File file = nodeDir.firstFile(GEXT.VIDEO).toFile();

		setControls(true);

		AVideo cont = new AVideo(file);

		setContent(cont);

		setWidgetListener("onBind", "el('#'+this.uuid).onended = (event) => zAu.send(new zk.Event(zk.Widget.$('#'+this.uuid), 'onVideoEnd', {}, {toServer:true}));  ");
		addEventListener("onVideoEnd", e -> {
			onVideoEnd();
		});
	}

	private List<SingleNodeVideo> getPlayListComs() {
		return getParent().getChildren().stream().filter(c -> c instanceof SingleNodeVideo).map(c -> (SingleNodeVideo) c).collect(Collectors.toList());
	}

	public void onVideoEnd() {
		List<SingleNodeVideo> playlistCom = getPlayListComs();
		if (X.empty(playlistCom)) {
			return;
		}
		int thisIndex = -1;
		for (int i = 0; i < playlistCom.size(); i++) {
			if (playlistCom.get(i) == this) {
				thisIndex = i;
			}
		}
		if (thisIndex < 0) {
			return;
		}
		playlistCom.get(thisIndex == playlistCom.size() - 1 ? 0 : thisIndex + 1).play();
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
		FormState comStateJson = getComState_JSON();
		comStateJson.apply_WIDTH_HEIGHT(this);
		Menupopup0 menu = getOrCreateMenupopup(ZKC.getFirstWindow());
		SingleNodeImg.addContextMenu(menu, getNodeDir());
	}


}
