package zk_notes.coms;

import lombok.Getter;
import mpc.fs.ext.GEXT;
import mpu.X;
import org.zkoss.video.AVideo;
import org.zkoss.zk.ui.Page;
import zk_com.core.IZCom;
import zk_com.ext.video.AdvVideo;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class SingleNodeVideo extends AdvVideo implements IZCom {

	@Getter
	private final NodeDir nodeDir;

	@Override
	public String getComName() {
		return nodeDir == null ? getClass().getSimpleName() : nodeDir.nodeName();
	}

	@Override
	public String getFormName() {
		return nodeDir == null ? getClass().getSimpleName() : nodeDir.nodeName();
	}

	public SingleNodeVideo(File videoFile) {
		this(null, videoFile);
	}

	public SingleNodeVideo(NodeDir nodeDir) {
		this(nodeDir, nodeDir.firstFile(GEXT.VIDEO).toFile());
	}

	public SingleNodeVideo(NodeDir nodeDir, File videoFile) {
		super();

		this.nodeDir = nodeDir;

		File file = videoFile;

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
		//TODO why com
//		FormState comStateJson = getComState_JSON();
		ObjState comStateJson = getFormState();
		if (comStateJson.existPropsFile()) {
			comStateJson.apply(this, ObjState.WIDTH_HEIGHT);
//			Menupopup0 menu = getOrCreateMenupopup(ZKC.getFirstWindow());
//			ANMF0.applyNolCom(menu, getNodeDir(),true);
		}
	}


}
