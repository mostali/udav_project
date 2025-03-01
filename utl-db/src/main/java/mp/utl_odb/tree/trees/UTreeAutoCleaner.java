package mp.utl_odb.tree.trees;

import mp.utl_odb.DBU;
import mp.utl_odb.query_core.OrderParam;
import mp.utl_odb.tree.CtxtDb;
import mp.utl_odb.tree.UTree;
import mpe.str.CN;
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
				UTree treeDb = (UTree) UTree.treeApp("test").setAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
				treeDb.put("action" + i0++, "void");
			}
		}
	}

	static int __K = 1;

	static int __MAX_COUNT = __K * 100;
	static int __MIN_COUNT = __K * 10;
	static int __PACKET_REMOVE = __K * __MIN_COUNT;
	static boolean __cleanFirstOrEnd = true;

	int __CHECK_EVERY = 100;

	public static String checkAutoClean(CtxtDb ctxtDb, Integer[] ctr_every_min_max_packet_firstEnd) {
		try {
			return checkAutoClean0(ctxtDb, ctr_every_min_max_packet_firstEnd);
		} catch (Exception e) {
			L.error("checkAutoClean0", e);
			return "ERR:" + e.getMessage();
		}
	}

	private static String checkAutoClean0(CtxtDb ctxtDb, Integer[] ctr_every_min_max_packet_firstEnd) {
		if (++ctr_every_min_max_packet_firstEnd[0] < ctr_every_min_max_packet_firstEnd[1]) {
			return X.f("no limit %s < %s", ctr_every_min_max_packet_firstEnd[0], ctr_every_min_max_packet_firstEnd[1]);
		}
		int minRows = ARRi.item(ctr_every_min_max_packet_firstEnd, 2, __MIN_COUNT);
		int maxRows = ARRi.item(ctr_every_min_max_packet_firstEnd, 3, __MAX_COUNT);
		int packet = ARRi.item(ctr_every_min_max_packet_firstEnd, 4, __PACKET_REMOVE);
		boolean cleanFirstOrEnd = ARRi.item(ctr_every_min_max_packet_firstEnd, 5, 0) == 0;

		long countDb0 = ctxtDb.getCount();
		long countDb = countDb0;
		if (countDb <= maxRows) {
			return X.f("Db has %s rows. Is not exceeded limit '%s'", countDb, maxRows);
		}
		Supplier<Long> getterTotalAfterClean = () -> {
			List<CtxtDb.CtxTimeModel> models = DBU.getModels(ctxtDb,
					CtxtDb.CtxTimeModel.class,
					cleanFirstOrEnd ? OrderParam.paramAsc(CN.ID) : OrderParam.paramDesc(CN.ID), 0L, (long) packet);
			DBU.removeAllModels(ctxtDb, models);
			if (L.isInfoEnabled()) {
				L.info("Remove db {} packet with '{}' model's", countDb, packet);
			}
			return countDb0 - models.size();
		};
		Long cleaned = getterTotalAfterClean.get();
		ctr_every_min_max_packet_firstEnd[0] = 0;
		return X.f("AutoClean Db successfully (%s->%s), current db size '%s', ctr_every_min_max_packet_firstEnd - %s", countDb0, cleaned, ctxtDb.getDbFileSizeMb(), ARR.as(ctr_every_min_max_packet_firstEnd));
	}

//
//	public static final Function<UTree, String> treeCleanerBySizeMb = new Function<UTree, String>() {
//
//		int MAX_MB = 500;
//		int MIN_MB = 100;
//		long PACKET_REMOVE = 1_000L;
//		boolean cleanFirstOrEnd = true;
//
//		@SneakyThrows
//		@Override
//		public String apply(UTree treeDb) {
//			double dbFileSizeMb = treeDb.getDbFileSizeMb();
//			if (dbFileSizeMb < MAX_MB) {
//				return X.f("Db has size %sMb. Is not exceeded limit '%s'", dbFileSizeMb, MAX_MB);
//			}
//			while (dbFileSizeMb > MIN_MB) {
//				List<CtxtDb.CtxTimeModel> models = DBU.getModels(treeDb, CtxtDb.CtxTimeModel.class, cleanFirstOrEnd ? OrderParam.paramAsc(CN.ID) : OrderParam.paramDesc(CN.ID), 0L, PACKET_REMOVE);
//				DBU.removeAllModels(treeDb, models);
//				UTreeUniq.L.info("Remove db packet with '%s' model's", PACKET_REMOVE);
//			}
//			return X.f("Clean Db ok from '%s' to '%s'", dbFileSizeMb, treeDb.getDbFileSizeMb());
//		}
//	};

}
