package mpc.map;

import mpc.env.Env;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BootMapTest {
	public BootMapTest() {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void common_test() throws Exception {
		BootMap.doAlltest();
	}


}
