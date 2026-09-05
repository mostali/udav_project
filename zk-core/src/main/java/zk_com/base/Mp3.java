package zk_com.base;


import lombok.Getter;
import lombok.SneakyThrows;
import mpc.fs.UF;
import mpu.X;
import org.zkoss.sound.AAudio;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Audio;
import org.zkoss.zul.Div;
import zk_com.core.IZCom;
import zk_page.ZKC;
import zk_radio.model.AUM;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Mp3 extends Audio implements IZCom {

	public Mp3(String src) {
		super(src);
		mp3File = null;
	}

	private final @Getter String mp3File;

	public String getMp3FileName() {
		return UF.fn(getMp3File());
	}

	public Mp3(Path file) {
		this(file.toFile());
	}

	@SneakyThrows
	public Mp3(File file) {
		setContent(new AAudio(file));
		setControls(true);
		this.mp3File = file.toString();
	}

	public Mp3() {
		mp3File = null;
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
		applyBindEndEvent();
		if (mp3File != null) {
			String fn = UF.fn(mp3File);
			title(fn);
		}
	}

	protected void applyBindEndEvent() {
		String scriptPlay = "el('#'+this.uuid).onplay = (event) => zAu.send(new zk.Event(zk.Widget.$('#'+this.uuid), 'onAudioStart', {}, {toServer:true}));  ";
		String scriptEnd = "el('#'+this.uuid).onended = (event) => zAu.send(new zk.Event(zk.Widget.$('#'+this.uuid), 'onAudioEnd', {}, {toServer:true}));  ";
		setWidgetListener("onBind", scriptPlay + scriptEnd);
		addEventListener("onAudioEnd", e -> onAudioEnd());
		addEventListener("onAudioStart", e -> onAudioStart());
	}

	public void onAudioStart() {
		stopAll();
	}

	public void stopAll() {
		Mp3 tt = this;
		getPlayListComs().forEach(i -> {
			i.stop();
		});
		tt.play();
	}

	public void onAudioEnd() {
		List<Mp3> playlistCom = getPlayListComs();
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

	private List<Mp3> getPlayListComs() {
		return getParent().getChildren().stream().filter(c -> c instanceof Mp3).map(c -> (Mp3) c).collect(Collectors.toList());
	}

	public static Div wrapDiv(Mp3 mp3) {
		return ZKC.newDiv(mp3);
	}

	public static Div wrapDiv(String src) {
		return ZKC.newDiv(new Mp3(src));
	}
}
