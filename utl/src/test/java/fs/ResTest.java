package fs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import mpc.fs.fd.DIR;
import mpc.fs.fd.FILE;
import mpc.fs.fd.RES;

public class ResTest {
	public ResTest() {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void exist() throws Exception {
		Assert.assertTrue(RES.of(ResTest.class, "/FdTest.txt", FILE.class).exist());
		Assert.assertFalse(RES.of(ResTest.class, "FdTest.txt", FILE.class).exist());

		Assert.assertTrue(RES.of(ResTest.class, "/fd-dir-test/FdTest.txt", FILE.class).exist());
		Assert.assertFalse(RES.of(ResTest.class, "fd-dir-test/FdTest.txt", FILE.class).exist());

		//
		//
		Assert.assertTrue(RES.of(ResTest.class, "/fd-dir-test", DIR.class).exist());
		Assert.assertTrue(RES.of(ResTest.class, "/fd-dir-test/", DIR.class).exist());
		Assert.assertFalse(RES.of(ResTest.class, "fd-dir-test", DIR.class).exist());
		Assert.assertFalse(RES.of(ResTest.class, "fd-dir-test/", DIR.class).exist());
	}

	@Test
	public void exist_child() throws Exception {
		Assert.assertTrue(RES.of(ResTest.class, "/fd-dir-test", DIR.class).exist("FdTest.txt"));
		Assert.assertFalse(RES.of(ResTest.class, "/fd-dir-test", DIR.class).exist("FdTest.txt0"));
	}

	@Test
	public void cat() throws Exception {
		Assert.assertTrue(RES.of(ResTest.class, "/FdTest.txt", FILE.class).cat_().length() > 0);
		Assert.assertTrue(RES.of(ResTest.class, "/fd-dir-test/FdTest.txt", FILE.class).cat_().length() > 0);
	}

}
