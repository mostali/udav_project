package zk_radio.model;

import lombok.SneakyThrows;
import mpc.env.Env;
import mpe.NT;

public class AUP {

	@SneakyThrows
	public static void main(String[] args) {
		Env.setAppName(NT.BEA);

		String usr = "bearoot";
//		List<Ctx3Db.CtxModelCtr> models = getAllPlaylistModels(usr);

//		X.exit(models);
	}

//	public static List<AUC> getAllPlaylistModels() {
//		return getAllPlaylistModels(WebUsr.login()).stream().map(AUC::of).collect(Collectors.toList());
//	}

//	public static List<Ctx3Db.CtxModelCtr> getAllPlaylistModels(String usr) {
//		QP like = QP.like(CN.KEY, usr + SingleProfile.UserCol.DEL_MAIN + "%");
//		QP likeN = QP.notlike(CN.KEY, usr + SingleProfile.UserCol.DEL_MAIN);
//		List<Ctx3Db.CtxModelCtr> models = AppZosCore.TREE_RADIO().getModels(like, likeN);
//		return models;
//	}

//	public static AUM addPlaylist(String playlistname) {
//		String key = SingleProfile.UserCol.toKey(playlistname);
//		Ctx3Db.CtxModelCtr put = AppZosCore.TREE_RADIO().put(key);
//		return AUM.of(put);
//	}
//
//	public static boolean rmPlaylist(String playlistname) {
//		String key = SingleProfile.UserCol.toKey(playlistname);
//		return AppZosCore.TREE_RADIO().removeByKeyIfExist(key) > 0;
//	}
}
