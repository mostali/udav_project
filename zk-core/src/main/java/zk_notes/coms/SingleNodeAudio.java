package zk_notes.coms;

import lombok.Getter;
import mpc.fs.UF;
import mpc.fs.ext.GEXT;
import mpu.X;
import org.zkoss.sound.AAudio;
import org.zkoss.zk.ui.Page;
import zk_com.base.Mp3;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;
import zk_radio.model.AUM;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class SingleNodeAudio extends Mp3 {

	private final @Getter NodeDir nodeDir;
	private final @Getter Path audioFile;

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
		this.audioFile = audioFile;

		setControls(true);

		title(audioFile.getFileName().toString());

		AAudio cont = new AAudio(audioFile.toFile());

		setContent(cont);

	}

	@Override
	public void onAudioStart() {
		super.onAudioStart();

//		NodeDir nodeDir = NodeDir.ofDir(sdn(), UF.parent(getMp3File()));
		AUM.get().set_PLAYLIST(nodeDir.nodeId());
		AUM.get().set_PLAY(getAudioFile().toString());

	}

//	@Override
//	public String getMp3File() {
//		return super.getAudioFile();
//	}

	protected void init() {
		super.init();

		ObjState comStateJson = getComState();

		comStateJson.apply(this, ObjState.WIDTH_HEIGHT);

	}

}
