package mp.utl_odb.tree.trees;


import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.UTree;
import mpc.exception.FIllegalStateException;
import mpe.core.P;
import mpu.core.ARG;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class UTreeUniq {

	public static final Logger L = LoggerFactory.getLogger(UTreeUniq.class);

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


//	private static TreeCleaner treeCleaner = null;

	public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND = new Integer[]{0, 1000, 1000, 5000, 500, 0};

	public static void regAction(String treename, String action, long ms) {
//		if (treeCleaner == null) {
//			treeCleaner = new TreeCleaner();
//		}
		UTree treeDb = (UTree) UTree.treeApp(treename).setAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRSTEND);
//		if (L.isInfoEnabled()) {
//			L.info("Req uniq action '{}' in tree '{}' ( {} )", action, treename, treeDb.getDbFilePath());
//		}
		treeDb.put(action, ms);
//		if (treeCleaner.isNeedCheckAutoCleanDb()) {
//			String rslt = treeCleaner.runCleanDb(treeDb);
//			if (L.isInfoEnabled()) {
//				L.info("Run cleaner:" + rslt);
//			}
//		}
	}

//	public static class TreeCleaner implements UTreeAutoCleaner.FuncDbCleaner {
//		final AtomicInteger checkEveryCounter = new AtomicInteger(0);

//		@Override
//		public AtomicInteger getCheckEveryCounter() {
//			return checkEveryCounter;
//		}

//		@Override
//		public boolean getCleanFromFirstOrLast() {
//			return true;
//		}

//		@Override
//		public int getCheckEvery() {
//			return 1000;
//		}

//		@Override
//		public int getMaxRowCount() {
//			return 5_000;
//		}
//
//		@Override
//		public int getMinRowCount() {
//			return 1000;
//		}

//		@Override
//		public int getPacketRowRemove() {
//			return 500;
//		}
//
//	}

}
