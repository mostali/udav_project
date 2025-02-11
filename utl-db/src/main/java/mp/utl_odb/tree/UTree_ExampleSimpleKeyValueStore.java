package mp.utl_odb.tree;

import mp.utl_odb.DBU;
import mp.utl_odb.query_core.QP;
import mpf.test.ZNViewAno;
import mpu.IT;

@ZNViewAno
public class UTree_ExampleSimpleKeyValueStore {

	public static void main(String[] args) {

		DBU.ENABLE_LOG_WARN(); //clean sqlite-driver log out

		UTree mydb = UTree.tree("myDb"); //create db foo

		mydb.clear(); // clear db if exist

		{ // PUT operation

			mydb.put("key", "value");

			mydb.put("key", "value2", "ext");

			IT.state("value2".equals(mydb.get("key")));

			IT.state(1 == mydb.getCount());

		}

		{ // ADD operation

			mydb.add("key", "value3", null);

			IT.state("value2".equals(mydb.get("key")));

			IT.state("value3".equals(((CtxtDb.CtxTimeModel) mydb.getModelAscDesc(false)).getValue()));

			IT.state(2 == mydb.getCount());

		}

		{// Use
			IT.state(1 == mydb.getModels(QP.limit(1)).size());

			IT.state(2 == mydb.getModels(QP.like("value", "value%")).size());

			IT.state(0 == mydb.getModels(QP.offset(2L), QP.limit(2)).size());

		}

	}
}
