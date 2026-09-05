//package zk_radio.model;
//
//import mp.utl_odb.tree.ctxdb.Ctx3Db;
//import mpc.json.GsonMap;
//import mpe.str.CN;
//import mpu.X;
//import mpu.core.ARG;
//import zk_radio.CXAA;
//import zk_radio.ZkAudio;
//import zk_radio.walker.SingleProfile;
//
////AudioProfileMain
//public class AUM2 extends AuRow {
//
//	public AUM2(Ctx3Db.CtxModelCtr rowDb) {
//		super(rowDb);
//	}
//
//	public static AUM2 of(Ctx3Db.CtxModelCtr modelCtr) {
//		return new AUM2(modelCtr);
//	}
//
////	public static AUM2 loadMain() {
////		Ctx3Db.CtxModelCtr model = ZkAudio.getDb().getModelByKeyOrCreate(SingleProfile.UserCol.toKeyMain());
////		return AUM2.of(model);
////	}
//
//
//	public String get_PLAY(String... defRq) {
//		GsonMap gm = rowDb.getExtAs(GsonMap.class, null);
//		if (gm != null) {
//			return gm.getAsString(CXAA.PLAY, defRq);
//		}
//		return ARG.toDefThrowMsg(() -> X.f("Except key '%s'", CXAA.PLAY), defRq);
//	}
//
//	public String get_PLAYLIST(String... defRq) {
//		GsonMap gm = rowDb.getExtAs(GsonMap.class, null);
//		if (gm != null) {
//			return gm.getAsString(CXAA.PLAYLIST, defRq);
//		}
//		return ARG.toDefThrowMsg(() -> X.f("Except key '%s'", CXAA.PLAYLIST), defRq);
//	}
//
//	public void set_PLAY(String plName) {
//		__setAndWriteExt(CXAA.PLAY, plName, CN.EXT);
//	}
//
//	public void set_PLAYLIST(String plName) {
//		__setAndWriteExt(CXAA.PLAYLIST, plName, CN.EXT);
//	}
//
//
//
//}
