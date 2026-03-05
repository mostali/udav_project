package zk_radio.core;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mpc.fs.UF;
import mpc.fs.ext.GEXT;
import mpu.core.ARRi;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_form.notify.ZKI;
import mpe.img.EColor;
import zk_page.ZKSession;
import zk_radio.model.AUM;
import zk_radio.ZkAudio;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ZRadioView extends Div0 {

	public static final String SK_OPEN_PLAYER = "OpenPlayer";

	public ZRadioView() {
		super();
	}

	final PlayLb playBt = (PlayLb) new PlayLb(true).block();

	Boolean firstInit = null;

	@Override
	protected void init() {
		super.init();

//		width("20px");
		width("20px");
		height("20px");
		padding_left(5);

		applyNiceBg(EColor.LBLUE.nextColor(), "white");

		appendChild(playBt);

//		newPlayLb(true);

		Menupopup0 playMenu = playBt.getOrCreateMenupopup(this);

		new PlayMenu().buildMenu(playMenu);

		String[] play = AUM.get().getPlayState(null);

		String playFile = play == null ? ZkAudio.DEF_FILE_NAME : play[1];

		onNextAudio(playFile, false);

		if (play == null) {
			if (firstInit) {
				ZKI.infoAfterPointer("Not found active play item");
			} else {
				firstInit = false;
			}
		}

//		onCLICK(e -> doCycle0(getZmp3First(), false));

		onDBLCLICK(e -> {

			getZmp3First().stop();
		});

	}

	public void stopAll() {
		getPlaylistItems().forEach(c -> c.stop());
	}

	public Zmp3 getZmp3First() {
		return getPlaylistItems().get(0);
	}

	public void onNextAudioAuto(boolean play) {
		String[] playState = AUM.get().getPlayState();
		Set<String> paths = AUM.currentPlaylistSetFiles_Compiled();

		String foundPath = null;
		boolean found = false;

		for (String path : paths) {
			if (found) {
				foundPath = path;
				break;
			}
			if (found = path.equals(playState[1])) {
				found = true;
			}
		}
		if (foundPath == null) {
			foundPath = ARRi.first(paths);
		}

		onNextAudio(foundPath, play);

	}


	public void onNextAudio(String file, boolean play) {
		Boolean openView = ZKSession.getSessionAttrs().getAsBoolean(SK_OPEN_PLAYER, false);
		PlayItem playItem = PlayItem.builder().fileAudio(file).play(play).openView(openView).build();
		onShowNextPlayItem(this, playItem);
		if (firstInit != null) {
			ZkAudio.showInfoActivePlayTrack(playItem.fileName());
		} else {
			firstInit = true;
		}

	}

	@Builder
	@Data
	public static class PlayItem {
		private final String fileAudio;
		private final Boolean play;
		private final Boolean openView;

		public String fileName() {
			return UF.fn(fileAudio);
		}
	}

	public static void onShowNextPlayItem(Div0 c, PlayItem playItem) {

		String file = playItem.fileAudio;
		boolean play = playItem.play;
		boolean openView = playItem.openView;

		boolean isAudio = GEXT.AUDIO.has(file);
		if (!isAudio) {
			ZKI.showMsgBottomRightSlow(ZKI.Level.WARN, "Not found audio track type '%s'", file);
//			NI.stop("What is type? [" + EXT.of(file, null) + "]");
			return;
		}

		File audioItem = new File(file);

		if (!audioItem.exists()) {
			ZKI.showMsgBottomRightSlow(ZKI.Level.WARN, "Not found audio track '%s'", file);
			return;
//			throw new FIllegalArgumentException("Not found audio item '%s'", audioItem.getName());
		}

		c.title(audioItem.getName());

		Zmp3 child = new Zmp3(audioItem, openView) {
//			@Override
//			public void onAudioStart() {
//				super.onAudioStart();
//
//				NodeDir nodeDir = NodeDir.ofDir(Sdn.get(), UF.parent(getMp3File()));
//				AUM.get().set_PLAYLIST(nodeDir.nodeId());
//				AUM.get().set_PLAY(getMp3File());
//
//			}
		};

		c.appendChild(child);

		if (play) {
			child.play();
		}

		ZkAudio.regEvent_setPLAY(playItem);
	}

	public static abstract class SearchColl0 {
		public abstract String[] searchNext();
	}

	@RequiredArgsConstructor
	static class SearchColl extends SearchColl0 {

		private final Zmp3 zmp3;

		private final Boolean nextOrPrev;

		@Override
		public String[] searchNext() {
			String curMp3File = zmp3.getMp3File();
			String next = nextOrPrev ? ZkAudio.next(curMp3File) : ZkAudio.prev(curMp3File);
			return new String[]{next, curMp3File};
		}
	}

//	@RequiredArgsConstructor
//	static class SearchFileColl extends SearchColl0 {
//
//		private final String curMp3File;
//
//		private final String file;
//
//		@Override
//		public String[] searchNext() {
//			return new String[]{file, curMp3File};
//		}
//	}

//	private static void doCycle0(Zmp3 zmp3, String file) {
//		doCycle0(zmp3, new SearchFileColl(zmp3.getMp3File(), file));
//	}

	public static void onSearchNextPlayItem(Zmp3 zmp3, boolean nextOrPrev) {
		onSearchNextPlayItem(zmp3, new SearchColl(zmp3, nextOrPrev));
	}

	private static void onSearchNextPlayItem(Zmp3 zmp3, SearchColl0 searchColl) {
		if (zmp3.detached.get()) {
			return;
		}
		String[] next = searchColl.searchNext();

		L.info("Current [{}], next [{}]", next[1], next);

		zmp3.getZRadio().onNextAudio(next[0], true);

		zmp3.stop();

		zmp3.detach();

		boolean needShowLast = false;
		if (needShowLast) {
			zmp3.getZRadio().getPlaylistItems().stream().filter(i -> i.start.isBefore(zmp3.start)).forEach(i -> i.detach());
		}


	}

	public List<Zmp3> getPlaylistItems() {
		return (List) getChildren().stream().filter(c -> c instanceof Zmp3).collect(Collectors.toList());
	}
}
