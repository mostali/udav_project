package zk_notes.coms;

import lombok.Getter;
import mpc.fs.ext.GEXT;
import mpu.X;
import org.zkoss.sound.AAudio;
import org.zkoss.zk.ui.Page;
import zk_com.base.Mp3;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class SingleNodeAudio extends Mp3 {

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

	public SingleNodeAudio(NodeDir nodeDir) {
		this(nodeDir, nodeDir.firstFile(GEXT.AUDIO));
		padding("10px");
	}

	public SingleNodeAudio(NodeDir nodeDir, Path audioFile) {
		super();

		this.nodeDir = nodeDir;

		setControls(true);


		title(audioFile.getFileName().toString());

		AAudio cont = new AAudio(audioFile.toFile());
		setContent(cont);

		setWidgetListener("onBind", "el('#'+this.uuid).onended = (event) => zAu.send(new zk.Event(zk.Widget.$('#'+this.uuid), 'onAudioEnd', {}, {toServer:true}));  ");
		addEventListener("onAudioEnd", e -> onAudioEnd());

	}

	public void onAudioEnd() {
		List<SingleNodeAudio> playlistCom = getPlayListComs();
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

	private List<SingleNodeAudio> getPlayListComs() {
		return getParent().getChildren().stream().filter(c -> c instanceof SingleNodeAudio).map(c -> (SingleNodeAudio) c).collect(Collectors.toList());
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
		ObjState comStateJson = getComState();
		comStateJson.apply(this, ObjState.WIDTH_HEIGHT);

	}


//	public void setPlayListCom(List<SingleNodeAudio> audios) {
//		this.playlistCom = audios;
//	}
}
