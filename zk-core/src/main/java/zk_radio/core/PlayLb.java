package zk_radio.core;

import mpc.str.sym.SYMJ;
import org.jetbrains.annotations.NotNull;
import zk_com.base.Lb;
import zk_form.notify.ZKI;
import zk_page.ZKS;
import zk_radio.ZkAudio;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayLb extends Lb {
	final AtomicBoolean playRef;

	public PlayLb(boolean play) {
		super(getPlayLabel(play));
		playRef = new AtomicBoolean(play);
	}

	private static @NotNull String getPlayLabel(boolean play) {
		return play ? SYMJ.TRACK_PLAY0 : SYMJ.TRACK_PAUSE;
	}

	@Override
	protected void init() {
		super.init();
		applyValue();
		onCLICK(e -> {
			PlayLb target = (PlayLb) e.getTarget();
			boolean isPrevPlay = target.playRef.get();
			target.playRef.set(!isPrevPlay);
			applyValue();
			if (isPrevPlay) {
				onStart();
			} else {
				onPause();
			}
		});
	}

	private void onStart() {
		getZRadio().getPlaylistItems().forEach(c -> {
			c.play();
			ZkAudio.showInfoActivePlayTrack(c.getMp3FileName());
		});
	}

	private ZRadioView getZRadio() {
		return (ZRadioView) getParent();
	}

	private void onPause() {
		getZRadio().getPlaylistItems().forEach(c -> c.pause());
	}

	private void applyValue() {
		boolean isPLay = playRef.get();
		if (isPLay) {
			ZKS.MARGIN_LEFT(this, null);
		} else {
			ZKS.MARGIN_LEFT(this, -5);
		}
		setValue(getPlayLabel(isPLay));
	}
}
