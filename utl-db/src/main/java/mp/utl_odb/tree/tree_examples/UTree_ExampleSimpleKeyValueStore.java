package mp.utl_odb.tree.tree_examples;

import mp.utl_odb.DBU;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.UTree;
import mp.utl_odb.tree.ctxdb.Ctx3Db;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpf.test.ZNViewAno;
import mpu.IT;
import mpu.X;

@ZNViewAno
public class UTree_ExampleSimpleKeyValueStore {

	public static void main(String[] args) {

		DBU.ENABLE_LOG_WARN(); //clean sqlite-driver log out

		UTree mydb = UTree.tree("myDb"); //create db foo

		X.p("Use:" + mydb);

		mydb.truncateTable(); // clear db if exist

		{ // PUT operation

			mydb.put("key", "value");

			mydb.put("key", "value2", "ext");

			IT.state("value2".equals(mydb.getValue("key")));

			IT.state(1 == mydb.getCount());

		}

		{ // ADD operation

			mydb.withUpdateMode(ICtxDb.UpdateMode.ADD).put("key", "value3", null);

			IT.state("value2".equals(mydb.getValue("key")));

			IT.state("value3".equals(((Ctx3Db.CtxModelCtr) mydb.getModelFirstOrLast(false)).getValue()));

			IT.state(2 == mydb.getCount());

		}

		{// Use
			IT.state(1 == mydb.getModels(QP.limit(1)).size());

			IT.state(2 == mydb.getModels(QP._like_("value", "value%")).size());

			IT.state(0 == mydb.getModels(QP.offset(2L), QP.limit(2)).size());

		}

	}
}
