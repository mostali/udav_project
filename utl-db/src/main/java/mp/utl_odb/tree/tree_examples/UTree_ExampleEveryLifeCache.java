package mp.utl_odb.tree.tree_examples;

import lombok.SneakyThrows;
import mp.utl_odb.DBU;
import mp.utl_odb.tree.trees.lifecache.UTreeEveryLife;
import mp.utl_odb.tree.trees.lifecache.UTreeShortLife;
import mpe.rt.SLEEP;
import mpf.test.ZNViewAno;
import mpu.IT;
import mpu.X;
import mpu.str.Hu;

@ZNViewAno
public class UTree_ExampleEveryLifeCache {
	@SneakyThrows
	public static void main(String[] args) {
		DBU.ENABLE_LOG_WARN();
		test1();
	}

	public static void test1() throws UTreeShortLife.ModelLifeMsException {

		UTreeEveryLife myDb = UTreeEveryLife.tree("myDb");
		myDb.checkLazyCreateDb();
		myDb.truncateTable();

		X.p("Value not exist:" + myDb.getModel_WithEveryLife("key", Hu.MS("3s")));

		myDb.put("key", "value");

		try {
			X.p("Value is NEXT life AFTER 3s:" + myDb.getModel_WithEveryLife("key", Hu.MS("3s")).getValue());
			X.throwException("no here");
		} catch (UTreeShortLife.ModelLifeMsException ex) {
			IT.state(ex.getMessage().contains("3000"), ex.getMessage());
			X.p("ok happens exception:" + ex.getMessage() + ":" + ex.getTimeModel().getTimeAsQDate().diffabs());
		}

		SLEEP.sec(3, "sleep 3s, value is dead");

		X.p("Value is NEXT life AFTER 3s:" + myDb.getModel_WithEveryLife("key", Hu.MS("3s")).getValue());


	}


}
