package mp.utl_odb.tree.tree_examples;

import mp.utl_odb.DBU;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.ctxdb.CKey;
import mp.utl_odb.tree.ctxdb.Ctx10Db;
import mp.utl_odb.tree.ctxdb.Ctx5Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpf.test.ZNViewAno;
import mpu.IT;
import mpu.X;

@ZNViewAno
public class UTree5_ExampleSimpleKeyValueStore {

	public static void main(String[] args) {

		createCtx10();

//		createCtx5();
//
//		renameAll(UFS.ls(Paths.get("/home/dav/.data/gsv/db-ctxt/published/")), srcTablename, dstTablename);
//		X.exit("ok");

//		test1();
//		test2();
	}

	private static void createCtx10() {
		Ctx10Db mydb = Ctx10Db.of("myDb"); //create db foo
		mydb.removeDb();
		Ctx10Db mydbH = Ctx10Db.of("myDb"); //create db foo
		mydbH.withHistoryTable();
		Ctx10Db.CtxModel10 kk1 = mydb.put("kk1", "vv1", "oo1");
		kk1.setId(null);
		Ctx10Db.CtxModel10h kk1H = Ctx10Db.CtxModel10h.of(kk1);
		mydbH.checkLazyCreateDb();
		mydbH.saveModelAsCreate(kk1H);
	}

	private static void createCtx5() {
		Ctx5Db mydb = Ctx5Db.of("myDb"); //create db foo
		mydb.removeDb();
		Ctx5Db mydbH = Ctx5Db.of("myDb"); //create db foo
		mydbH.setHistory(true);
		Ctx5Db.CtxModel5 kk1 = mydb.put("kk1", "vv1", "oo1");
		kk1.setId(null);
		Ctx5Db.CtxModel5h kk1H = Ctx5Db.CtxModel5h.of(kk1);
		mydbH.checkLazyCreateDb();
		mydbH.saveModelAsCreate(kk1H);
	}

	private static void test2() {
		Ctx5Db mydb = Ctx5Db.of("myDb"); //create db foo
		mydb.truncateTable(); // clear db if exist
		CKey k1 = CKey.of("k1");

		{
			mydb.put(k1, null, CKey.Val.of("v1"), CKey.O1.of("o1v1"));
			Ctx5Db.CtxModel5 modelBy = mydb.getModelBy(k1);
			IT.state("v1".equals(modelBy.getValue()));
			IT.state("o1v1".equals(modelBy.getO1()));
		}
		{
			mydb.put(k1, null, CKey.Val.of("v2"), CKey.O1.of("o1v2"), CKey.O2.of("o2v2"));
			Ctx5Db.CtxModel5 modelBy = mydb.getModelBy(k1);
			IT.state("v2".equals(modelBy.getValue()));
			IT.state("o1v2".equals(modelBy.getO1()));
			IT.state("o2v2".equals(modelBy.getO2()));
		}
		{
			mydb.put(k1, null, CKey.Val.of("v3"), CKey.O1.of("o1v3"), CKey.O2.of("o2v3"), CKey.O3.of("o3v3"));
			Ctx5Db.CtxModel5 modelBy = mydb.getModelBy(k1);
			IT.state("v3".equals(modelBy.getValue()));
			IT.state("o1v3".equals(modelBy.getO1()));
			IT.state("o2v3".equals(modelBy.getO2()));
			IT.state("o3v3".equals(modelBy.getO3()));
		}
		{
			mydb.put(k1, null, CKey.Val.of("v4"), CKey.O1.of("o1v4"), CKey.O2.of("o2v4"), CKey.O3.of("o3v4"), CKey.O4.of("o4v4"));
			Ctx5Db.CtxModel5 modelBy = mydb.getModelBy(k1);
			IT.state("v4".equals(modelBy.getValue()));
			IT.state("o1v4".equals(modelBy.getO1()));
			IT.state("o2v4".equals(modelBy.getO2()));
			IT.state("o3v4".equals(modelBy.getO3()));
			IT.state("o4v4".equals(modelBy.getO4()));
		}
	}

	public static void test1() {

		DBU.ENABLE_LOG_WARN(); //clean sqlite-driver log out

		Ctx5Db mydb = Ctx5Db.of("myDb"); //create db foo
		X.p("Use:" + mydb);

		mydb.truncateTable(); // clear db if exist

		{ // PUT operation

			mydb.put("key", "value");

			mydb.put("key", "value2", "ext");

			IT.state("value2".equals(mydb.getValue("key")));

			IT.state(1 == mydb.getCount());

		}

		{ // ADD operation

			mydb.withUpdateMode(ICtxDb.UpdateMode.ADD);
			mydb.put("key", "value3", null);
			mydb.withUpdateMode(ICtxDb.UpdateMode.PUT);

			IT.state("value2".equals(mydb.getValue("key")));

			IT.state("value3".equals(((Ctx5Db.CtxModel5) mydb.getModelFirstOrLast(false)).getValue()));

			IT.state(2 == mydb.getCount());

		}

		{// Use
			IT.state(1 == mydb.getModels(QP.limit(1)).size());

			IT.state(2 == mydb.getModels(QP._like_("value", "value%")).size());

			IT.state(0 == mydb.getModels(QP.offset(2L), QP.limit(2)).size());

		}

	}
}
