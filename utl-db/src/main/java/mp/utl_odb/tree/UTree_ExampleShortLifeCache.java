package mp.utl_odb.tree;

import lombok.SneakyThrows;
import mp.utl_odb.DBU;
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
		UTree myDb = UTree.tree("myDb");

		myDb.clear();

		myDb.put("key", "value");

		String value = myDb.getModel_WithMaxLife("key", Hu.MS("3s")).getValue();

		value = myDb.getModel_WithMaxLife("key", Hu.MS("3s")).getValue(); // life value 3sec

		SLEEP.sec(3, "cache is life");

		value = myDb.getModel_WithMaxLife("key", Hu.MS("4s")).getValue(); // value is dead after 3sec

		SLEEP.sec(1, "cache is death");
		try {
			value = myDb.getModel_WithMaxLife("key", Hu.MS("3s")).getValue();
			X.throwException("no here");
		} catch (CtxtlDb.ShortLifeException ex) {
			IT.state(ex.getMessage().contains("ShortLifeException 3000"), ex.getMessage());
		}

	}


}
