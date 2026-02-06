package mp.utl_odb.tree.tree_examples;

import lombok.SneakyThrows;
import mp.utl_odb.DBU;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.trees.lifecache.UTreeShortLife;
import mpe.rt.SLEEP;
import mpf.test.ZNViewAno;
import mpu.IT;
import mpu.X;
import mpu.str.Hu;

@ZNViewAno
public class UTree_ExampleShortLifeCache {
	@SneakyThrows
	public static void main(String[] args) {
		DBU.ENABLE_LOG_WARN();
		test1();
//		test2();
	}

	private static void test2() throws UTreeShortLife.ModelLifeMsException {
		X.p("test2");
		UTreeShortLife myDb = UTreeShortLife.tree("myDb");
		myDb.checkLazyCreateDb();
		myDb.truncateTable();
		int maxLifeMs = 2000;
		X.p("checkIsAllowedAction1:" + myDb.checkIsAllowedOrReg("sl", maxLifeMs));
		X.p("checkIsAllowedAction2:" + myDb.checkIsAllowedOrReg("sl", maxLifeMs));
		SLEEP.ms(maxLifeMs, "wait:" + maxLifeMs);
		try {
			X.p("checkIsAllowedAction3:" + myDb.checkIsAllowedOrReg("sl", maxLifeMs));
			X.throwException("no here");
		} catch (UTreeShortLife.ModelLifeMsException ex) {
			IT.state(ex.getMessage().contains("" + maxLifeMs), ex.getMessage());
			X.p("ok happens exception:" + ex.getMessage() + ":" + ex.getTimeModel().getTimeAsQDate().diffabs() + " >>> " + ex.getTimeModel());

			Ctx3Db.CtxModelCtr model = myDb.put("sl");
			X.p("checkIsAllowedAction4 - after put:" + myDb.checkIsAllowedOrReg("sl", maxLifeMs) + " >>> " + model);

		}
	}

	public static void test1() throws UTreeShortLife.ModelLifeMsException {

		UTreeShortLife myDb = UTreeShortLife.tree("myDb");
		myDb.checkLazyCreateDb();

		myDb.truncateTable();

		X.p("Value not exist:" + myDb.getModel_WithShortLife("key", Hu.MS("3s")));

		myDb.put("key", "value");

		X.p("Value is life 3s:" + myDb.getModel_WithShortLife("key", Hu.MS("3s")).getValue());
		X.p("Value is life 3s:" + myDb.getModel_WithShortLife("key", Hu.MS("3s")).getValue());
		SLEEP.sec(3, "sleep 3s, value is dead");
		X.p("Check value exist for 5sec life:" + myDb.getModel_WithShortLife("key", Hu.MS("5s")).getValue());
		try {
			myDb.getModel_WithShortLife("key", Hu.MS("3s")).getValue();
			X.throwException("no here");
		} catch (UTreeShortLife.ModelLifeMsException ex) {
			IT.state(ex.getMessage().contains("3000"), ex.getMessage());
			X.p("ok happens exception:" + ex.getMessage() + ":" + ex.getTimeModel().getTimeAsQDate().diffabs());
		}

	}


}
