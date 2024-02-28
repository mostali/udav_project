package mp.utl_odb.tree.trees;


import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.UTree;
import mpu.core.ARG;
import mpe.core.P;
import mpc.exception.FIllegalStateException;
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

	public static void regAction(String tree, String action, long ms) {
		UTree treeDb = UTree.tree(tree);
		treeDb.put(action, ms);
		if (L.isInfoEnabled()) {
			L.info("Req uniq action '{}' in tree '{}' ( {} )", action, tree, treeDb.getDbFilePath());
		}
	}
}
