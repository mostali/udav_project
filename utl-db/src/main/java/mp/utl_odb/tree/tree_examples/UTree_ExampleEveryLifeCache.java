package mp.utl_odb.tree.tree_examples;

import lombok.SneakyThrows;
import mp.utl_odb.DBU;
import mp.utl_odb.tree.trees.lifecache.UTreeEveryLife;
import mp.utl_odb.tree.trees.lifecache.UTreeTtl;
import mpe.rt.SLEEP;
import mpf.test.ZNViewAno;
import mpu.IT;
import mpu.X;
import mpu.str.Hu;

//Значение оживает 1 раз кажый период
@ZNViewAno
public class UTree_ExampleEveryLifeCache {
	@SneakyThrows
	public static void main(String[] args) {
		DBU.ENABLE_LOG_WARN();
		test1();
	}

	public static void test1() throws UTreeTtl.ModelLifeMsException {

		UTreeEveryLife myDb = UTreeEveryLife.tree("myDb");
		myDb.checkLazyCreateDb();
		myDb.truncateTable();

		X.p("Value not exist:" + myDb.getModel_WithEveryLife("key", Hu.MS("3s")));

		myDb.put("key", "value");

		try {
			X.p("Value is NEXT life AFTER 3s:" + myDb.getModel_WithEveryLife("key", Hu.MS("3s")).getValue());
			X.throwIllegalStateException("no here");
		} catch (UTreeTtl.ModelLifeMsException ex) {
			IT.state(ex.getMessage().contains("3000"), ex.getMessage());
			X.p("ok happens exception:" + ex.getMessage() + ":" + ex.getTimeModel().getTimeAsQDate().diffabs());
		}

		SLEEP.sec(3, "sleep 3s, value is dead");

		X.p("Value is NEXT life AFTER 3s:" + myDb.getModel_WithEveryLife("key", Hu.MS("3s")).getValue());


	}


}
