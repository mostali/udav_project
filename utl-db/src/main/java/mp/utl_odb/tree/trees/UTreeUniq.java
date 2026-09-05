package mp.utl_odb.tree.trees;


import mp.utl_odb.DBU;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.ctxdb.CKey;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.UTree;
import mpc.exception.FIllegalStateException;
import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UTreeUniq {

	public static final Logger L = LoggerFactory.getLogger(UTreeUniq.class);

	public static class UTreeUniq_Test {

		public static void main(String[] args) {
			DBU.ENABLE_LOG_WARN();
			try {
				IT.state(isUniqAction("test", "tact2", false, false).key());
				IT.state(isUniqAction("test", "tact2", true, false).key());
				IT.state(!isUniqAction("test", "tact2", false, true).key());
			} finally {
				UTree.treeApp("test").deleteDb();
			}

			X.p(SYMJ.OK_GREEN + " UTreeUniq_Test OK");

		}
	}

	public static Long getCountOf(String tree, QP[] qps, Long... defRq) {
		return UTree.tree(tree).getCountOf(qps, defRq);
	}

	public static boolean isNotUniqAction(String tree, String action, boolean regAction, boolean... RETURN) {
		return !isUniqActionBool(tree, action, regAction, RETURN);
	}

	public static boolean isUniqActionBool(String tree, String action, boolean regAction, boolean... RETURN) {
		return isUniqAction(tree, action, regAction, RETURN).key();
	}

	public static Pare<Boolean, Ctx3Db.CtxModelCtr> isUniqAction(String tree, String action, boolean regAction, boolean... RETURN) {
		UTree treeDb = UTree.treeApp(tree);
		Ctx3Db.CtxModelCtr model = treeDb.getModelBy(CKey.of(action), true);
//		Long date = treeDb.getAs(action, Long.class, null);
		if (model == null) {
			if (regAction) {
				model = regAction(tree, action);
			}
			return Pare.of(true, model);
		}
		if (ARG.isDefEqTrue(RETURN)) {
			return Pare.of(false, model);
		}
		throw new FIllegalStateException("Tree '%s' , action '%s' already happens at '%s'", tree, action, model.getTimeAsQDate());
	}


	public static final Integer[] AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRST0END1 = new Integer[]{0, 1000, 1000, 5000, 500, 0};

	public static Ctx3Db.CtxModelCtr regAction(String treename, String action) {
		UTree treeDb = (UTree) UTree.treeApp(treename).withAutoCleanCfg(AUTOCLEAN_CTR_EVERY_MIN_MAX_PACKET_FIRST0END1);
		return treeDb.put(action);
	}

}
