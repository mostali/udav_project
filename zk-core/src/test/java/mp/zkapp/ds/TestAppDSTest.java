package mp.zkapp.ds;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import zk_old_core.app_ds.TestAppDS;

public class TestAppDSTest {
	public TestAppDSTest() {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void exist() throws Exception {
		TestAppDS.main(null);
	}

}
