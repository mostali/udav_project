package zk_radio.core;

import mpu.core.QDate;
import zk_com.base.Mp3;
import zk_page.ZKS;
import zk_radio.model.AUM;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class Zmp3 extends Mp3 {

	final QDate start;

	final AtomicBoolean detached = new AtomicBoolean();

	@Override
	protected void init() {
		super.init();
		detached.set(false);
	}

	public Zmp3(File file, boolean open) {
		super(file);
		start = QDate.now();
		setControls(true);
		if (open) {
			ZKS.MARGIN_LEFT(this, "-520px");
			width("600px");
		} else {
			width("0px");
		}
	}

	@Override
	public void detach() {
		super.detach();
		detached.set(true);
		L.info("Detach:::" + getMp3File());
	}

	@Override
	public void play() {
		super.play();
		L.info("Play:::" + getMp3File());
	}

	@Override
	public void stop() {
		super.stop();
//			stoped.set(true);
		L.info("Stop:::" + getMp3File());
	}

	@Override
	public void pause() {
		super.pause();
		L.info("Pause:::" + getMp3File());

	}

	@Override
	public void onAudioStart() {
		super.onAudioStart();
	}

	@Override
	public void onAudioEnd() {
		L.info("onAudioEnd:::" + getMp3File());
		ZRadioView.onSearchNextPlayItem(this, true);
	}

	public ZRadioView getZRadio() {
		return (ZRadioView) getParent();
	}


}
