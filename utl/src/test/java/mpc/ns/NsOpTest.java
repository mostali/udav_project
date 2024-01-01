package mpc.ns;

import mpf.ns.space.oper.def.AddNsOp;
import mpf.ns.space.oper.def.CopyNsOp;
import mpf.ns.space.oper.def.MoveNsOp;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class NsOpTest {
	public NsOpTest() {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	public void move_test() throws Exception {
		MoveNsOp.testSrc();
		MoveNsOp.testTopic();
	}

	@Test
	@Ignore
	public void copy_test() throws Exception {
		CopyNsOp.testSrc();
		CopyNsOp.testTopic();
	}

	@Test
	@Ignore
	public void add_test() throws Exception {
		AddNsOp.testSpace();
		AddNsOp.testTopic();
	}


}
