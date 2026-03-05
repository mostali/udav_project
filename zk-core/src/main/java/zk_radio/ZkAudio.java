package zk_radio;

import mp.utl_odb.tree.UTree;
import mpc.arr.S_;
import mpu.X;
import mpu.core.ARRi;
import zk_form.notify.ZKI;
import zk_os.AppCoreZos;
import zk_radio.core.ZRadioView;
import zk_radio.model.AUM;
import zk_radio.walker.NodePlaylist;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class ZkAudio {

	public static final String DEF_FILE_NAME = "/opt/appVol/bea/intro.mp3";

	public static List<String> playlistLs() {
		return S_.mapToList(AUM.currentPlaylistFiles_Compiled(), Path::toString);

	}

	public static String next(String file) {
		return prevLast(file, true);
	}

	public static String prev(String file) {
		return prevLast(file, false);
	}

	public static String prevLast(String file, boolean nextPrev) {

		List<String> strings = playlistLs();

		if (X.empty(strings)) {
			return file;
		}
		int i = strings.indexOf(new File(file).getAbsolutePath());
		if (i == -1 || X.sizeOf(strings) == 1) {
			return strings.get(0);
		}
		if (i == strings.size() - 1) {
			//if last
			return nextPrev ? ARRi.first(strings) : strings.get(--i);
		}
		return strings.get(nextPrev ? i + 1 : (i == 0 ? strings.size() - 1 : i - 1));
	}


	public static void regEvent_setPLAY(ZRadioView.PlayItem file) {
		AUM.get().set_PLAY(file.getFileAudio());
	}

	public static UTree getDb() {
		return AppCoreZos.TREE_RADIO();
	}

//	public static void showInfoActivePlaylist(String playlist) {
//		ZKI.showMsgBottomRightSlow(ZKI.Level.INFO, "Active Playlist " + AUM.wrapLbPl(playlist));
//	}

	public static void showInfoActivePlaylist(NodePlaylist playlist) {
		ZKI.showMsgBottomRightSlow(ZKI.Level.INFO, "Active Node Playlist " + AUM.wrapLbPl(playlist.getNode().nodeId()));
	}

	public static void showInfoActivePlayTrack(String trackName) {
		ZKI.showMsgBottomRightFast_INFO("Active Track " + AUM.wrapLbPl(trackName));
	}
}
