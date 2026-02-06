package mp.utl_odb.tree.trees;

import mp.utl_ndb.SqlDbUrl;
import mp.utl_odb.DBU;
import mp.utl_odb.query_core.OrderParam;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mp.utl_odb.tree.UTree;
import mpc.fs.UF;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ARRi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

public class UTreeAutoCleaner {
	public static final Logger L = LoggerFactory.getLogger(UTreeAutoCleaner.class);

	static class Test {
		public static int i0 = 0;
		//		public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 10, 50, 100, 50, 0};
		public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 10, 10, 30, 15, 0};

		public static void main(String[] args) {
			DBU.ENABLE_LOG_WARN();
			while (true) {
				UTree treeDb = (UTree) UTree.treeApp("test").withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
				treeDb.put("action" + i0++, "void");
			}
		}
	}

	static int __K = 1;

	static int __MAX_COUNT = __K * 100;
	//	static int __MIN_COUNT = __K * 10;
	static int __PACKET_REMOVE = __MAX_COUNT / 5;
//	static boolean __cleanFirstOrEnd = true;

//	int __CHECK_EVERY = 100;

	public static String checkAutoClean(ICtxDb ctxtDb, Integer[] ctr_every_min_max_packet_firstEnd) {
		try {
			String s = checkAutoClean0(ctxtDb, ctr_every_min_max_packet_firstEnd);
			if (L.isDebugEnabled()) {
				L.debug(s);
			}
			return s;
		} catch (Exception e) {
			L.error("checkAutoClean0", e);
			return "ERR:" + e.getMessage();
		}
	}

	private static String checkAutoClean0(ICtxDb ctxDb, Integer[] ctr_every_min_max_packet_first0End1) {
		if (++ctr_every_min_max_packet_first0End1[0] < ctr_every_min_max_packet_first0End1[1]) {
			return X.f("no limit for check size %s < %s", ctr_every_min_max_packet_first0End1[0], ctr_every_min_max_packet_first0End1[1]);
		}

		String ln2dbfile = UF.ln(ctxDb.getDbFilePath());

		if (L.isDebugEnabled()) {
			L.debug("Init AutoClean operation {} >>> {}", ARR.as(ctr_every_min_max_packet_first0End1), ln2dbfile);
		}

		int minRows = ARRi.item(ctr_every_min_max_packet_first0End1, 2, -1);
		int maxRows = IT.isPosNotZero(ARRi.item(ctr_every_min_max_packet_first0End1, 3, __MAX_COUNT), "max count must be positive");
		int packet = IT.isPosNotZero(ARRi.item(ctr_every_min_max_packet_first0End1, 4, __PACKET_REMOVE), "AutoClean packet must be positive");
		boolean cleanFirstOrEnd = ARRi.item(ctr_every_min_max_packet_first0End1, 5, 0) == 0;

		long countDb0 = ctxDb.getCount();
		long countDb = countDb0;
		if (countDb <= maxRows) {
			return X.f("Db has %s rows. Is not exceeded limit '%s'", countDb, maxRows);
		}
		Supplier<Long> doClenOperaiton = () -> {
			OrderParam orderQp = cleanFirstOrEnd ? OrderParam.paramAsc(CN.ID) : OrderParam.paramDesc(CN.ID);
			List<Ctx3Db.CtxModelCtr> models = DBU.getModels((SqlDbUrl) ctxDb, Ctx3Db.CtxModelCtr.class, orderQp, 0L, (long) packet);
			DBU.removeAllModels((SqlDbUrl) ctxDb, models);
			if (L.isDebugEnabled()) {
				L.debug("AutoClean packet*{}({}) successfully from >>> {}", packet, models.size(), ln2dbfile);
			}
			return (long) models.size();
		};
		do {
			doClenOperaiton.get();
			if (minRows < 1) {
				break;
			}
		} while (ctxDb.getCount() - packet >= minRows);

		ctr_every_min_max_packet_first0End1[0] = 0;
		return X.f("AutoClean Db successfully, current db*%s, size %s, ctr_every_min_max_packet_firstEnd - %s >>> %s", ctxDb.getCount(-1L), DBU.getDbFileSizeMb(ctxDb), ARR.as(ctr_every_min_max_packet_first0End1), ln2dbfile);
	}

}
