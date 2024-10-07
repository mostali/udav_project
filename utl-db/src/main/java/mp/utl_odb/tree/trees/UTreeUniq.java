package mp.utl_odb.tree.trees;


import lombok.SneakyThrows;
import mp.utl_odb.DBU;
import mp.utl_odb.query_core.OrderParam;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.CtxtDb;
import mp.utl_odb.tree.UTree;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARG;
import mpe.core.P;
import mpc.exception.FIllegalStateException;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class UTreeUniq {

	public static final Logger L = LoggerFactory.getLogger(UTreeUniq.class);

	public static final int MAX_ROWS = 10_000_000;
	public static final int MIN_ROWS = 1_000_000;

	public static Long getCountOf(String tree, QP[] qps, Long... defRq) {
		return UTree.tree(tree).getCountOf(qps, defRq);
	}

	public static boolean isNotUniqAction(String tree, String action, Function<UTree, String> treeCleaner, boolean regAction, boolean... RETURN) {
		return !isUniqAction(tree, action, treeCleaner, regAction, RETURN);
	}

	public static boolean isUniqAction(String tree, String action, Function<UTree, String> treeCleaner, boolean regAction, boolean... RETURN) {
		UTree treeDb = UTree.tree(tree);
		Long date = treeDb.getAs(action, Long.class, null);
		if (date == null) {
			if (treeCleaner != null) {
				String result = treeCleaner.apply(treeDb);
				if (L.isInfoEnabled()) {
					L.info("TreeCleaner '{}' uniq action '{}' is applied:{}", tree, action, result);
				}
			} else {
				P.warnBig("Need impl tree cleaner '" + tree + "', action '" + action + "'");
				P.warnBig("Need impl tree cleaner '" + tree + "', action '" + action + "'", L);
			}
			if (regAction) {
				regAction(tree, action, System.currentTimeMillis());
			}
			return true;
		} else if (ARG.isDefEqTrue(RETURN)) {
			return false;
		}
		throw new FIllegalStateException("Tree '%s' , action '%s' already happens at '%s'", tree, action, QDate.of(date));
	}

	public static int checkEvery = 0;

	public static void regAction(String treename, String action, long ms) {
		UTree treeDb = UTree.treeApp(treename);
		if (L.isInfoEnabled()) {
			L.info("Req uniq action '{}' in tree '{}' ( {} )", action, treename, treeDb.getDbFilePath());
		}
		treeDb.put(action, ms);
		if (++checkEvery % FuncDbCleaner.EVERY == 0) {
			String rslt = treeCleanerByCount.apply(treeDb);
			if (L.isInfoEnabled()) {
				L.info("Run cleaner:" + rslt);
			}
		}
	}

	static int i0 = 0;

	public static void main(String[] args) {
		DBU.ENABLE_WARN();
//		for (int i = 0; i < 11_000; ++i) {
		while (true) {
			regAction("test-cleaner", ++i0 + "", System.currentTimeMillis());
		}
//		}
	}

	public static final Function<UTree, String> treeCleanerBySizeMb = new Function<UTree, String>() {

		int MAX_MB = 500;
		int MIN_MB = 100;
		long PACKET_REMOVE = 1_000L;
		boolean cleanFirstOrEnd = true;

		@SneakyThrows
		@Override
		public String apply(UTree treeDb) {
			double dbFileSizeMb = treeDb.getDbFileSizeMb();
			if (dbFileSizeMb < MAX_MB) {
				return X.f("Db has size %sMb. Is not exceeded limit '%s'", dbFileSizeMb, MAX_MB);
			}
			while (dbFileSizeMb > MIN_MB) {
				List<CtxtDb.CtxTimeModel> models = DBU.getModels(treeDb, CtxtDb.CtxTimeModel.class, cleanFirstOrEnd ? OrderParam.paramAsc(CN.ID) : OrderParam.paramDesc(CN.ID), 0L, PACKET_REMOVE);
				DBU.removeAllModels(treeDb, models);
				L.info("Remove db packet with '%s' model's", PACKET_REMOVE);
			}
			return X.f("Clean Db ok from '%s' to '%s'", dbFileSizeMb, treeDb.getDbFileSizeMb());
		}
	};

	public static final Function<UTree, String> treeCleanerByCount = new FuncDbCleaner() {
	};

	interface FuncDbCleaner extends Function<UTree, String> {

		int D = 1;

		int EVERY = D * 100;
		int MAX_COUNT = D * 100;
		int MIN_COUNT = D * 10;
		long PACKET_REMOVE = D * MIN_COUNT;
		boolean cleanFirstOrEnd = true;

		@SneakyThrows
		@Override
		default String apply(UTree treeDb) {
			long countDb = treeDb.getCount();
			if (countDb < MAX_COUNT) {
				return X.f("Db has %s rows. Is not exceeded limit '%s'", countDb, MAX_COUNT);
			}
			while (countDb > MIN_COUNT) {
				List<CtxtDb.CtxTimeModel> models = DBU.getModels(treeDb, CtxtDb.CtxTimeModel.class, cleanFirstOrEnd ? OrderParam.paramAsc(CN.ID) : OrderParam.paramDesc(CN.ID), 0L, PACKET_REMOVE);
				DBU.removeAllModels(treeDb, models);
				L.info("Remove db {} packet with '{}' model's", countDb, PACKET_REMOVE);
				countDb = treeDb.getCount();
			}
			return X.f("Clean Db (%s) ok from '%s' to '%s'", countDb, countDb, treeDb.getDbFileSizeMb());
		}
	}

	;
}
