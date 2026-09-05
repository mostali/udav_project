package zk_radio.model;

import lombok.SneakyThrows;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.json.GsonMap;
import mpc.str.sym.SYMJ;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import org.jetbrains.annotations.NotNull;
import zk_notes.node.NodeDir;
import zk_os.db.net.WebUsr;
import zk_radio.CXAA;
import zk_radio.ZkAudio;
import zk_radio.walker.NodePlaylist;
import zk_radio.walker.NodePlProc;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

//AudioProfileMain
public class AUM extends AuRow {

	public static final String PL = ".pl";

	public AUM(Ctx3Db.CtxModelCtr rowDb) {
		super(rowDb);
	}

	public static AUM of(Ctx3Db.CtxModelCtr modelCtr) {
		return new AUM(modelCtr);
	}

	public static String toKeyMain() {
		return WebUsr.login();
	}

	public static AUM get() {
		Ctx3Db.CtxModelCtr model = ZkAudio.getDb().getModelByKeyOrCreate(toKeyMain());
		return AUM.of(model);
	}

	@SneakyThrows
	public static Set<String> currentPlaylistSetFiles_Compiled(String... nodeID) {
		String nodeId = ARG.toDefOr(null, nodeID);
		if (nodeId == null) {
			String[] play = AUM.get().getPlayState(null);
			if (play == null || play[0].isEmpty()) {
				return ARR.EMPTY_SET;
			}
			nodeId = play[0];
		}
		return new NodePlProc(NodeDir.ofNodeId(nodeId)).processPlaylistSet();
	}

	@SneakyThrows
	public static List<Path> currentPlaylistFiles_Compiled(String... nodeID) {
		String nodeId = ARG.toDefOr(null, nodeID);
		if (nodeId == null) {
			String[] play = AUM.get().getPlayState(null);
			if (play == null || play[0].isEmpty()) {
				return ARR.EMPTY_LIST;
			}
			nodeId = play[0];
		}
		return new NodePlProc(NodeDir.ofNodeId(nodeId)).processPlaylist();
	}

	public static List<String> currentPlaylist_NodeDir_LsNames(GEXT... gext) {
		return UFS.toFileNames(AUM.currentPlaylist_NodeDir().dLsGEXT(gext));
	}

	public static NodeDir currentPlaylist_NodeDir() {
		return NodeDir.ofNodeId(AUM.get().getPlayState()[0]);
	}

	public static NodePlaylist current_NodePlaylist() {
		return NodePlaylist.of(currentPlaylist_NodeDir());
	}

	//
	//

	public static @NotNull String wrapLbPl(String playlist) {
		return SYMJ.FILE_MUSIC + " " + playlist;
	}

	public static @NotNull String wrapLbPl(NodePlaylist playlist) {
		return SYMJ.FILE_MUSIC_RADIO + " " + playlist;
	}

	//
	//

	public static @NotNull String wrapLbTrack(String track) {
		return SYMJ.FILE_MUSIC_ONE + " " + track;
	}

	public static @NotNull String wrapLbTrackWith(String[] play) {
		return SYMJ.FILE_MUSIC_ONE + " " + play[0] + " - " + play[1];
	}

	//
	//

	public String[] getPlayState(String[]... defRq) {
		GsonMap gm = rowDb.getExtAs(GsonMap.class, null);
		if (gm != null) {
			String PLAYLIST = gm.getAsString(CXAA.PLAYLIST, null);
			if (PLAYLIST == null) {
				return ARG.throwMsg(() -> X.f("Except key '%s'", CXAA.PLAYLIST), defRq);
			}
			String PLAY = gm.getAsString(CXAA.PLAY, null);
			if (PLAY == null) {
				return ARG.throwMsg(() -> X.f("Except key '%s'", CXAA.PLAY), defRq);
			}
			return new String[]{PLAYLIST, PLAY};
		}
		return ARG.throwMsg(() -> X.f("Except ext json '%s'"), defRq);
	}

	public void set_PLAY(String plName) {
		__setAndWriteExt(CXAA.PLAY, plName, CN.EXT);
	}

	public void set_PLAYLIST(String plName) {
		__setAndWriteExt(CXAA.PLAYLIST, plName, CN.EXT);
	}


}
