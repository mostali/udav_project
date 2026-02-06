package mpc.json;

import mpu.Sys;
import mpu.X;
import mpu.IT;
import mpc.fs.tmpfile.TmpFileOperation;
import org.junit.*;

import java.nio.file.Path;

public class PerGsonMapTest {
	public PerGsonMapTest() {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore//fail on autobuild via git installer
	@Test
	public void common_test() throws Exception {
		new TmpFileOperation() {
			@Override
			public void doOperationImpl(Path tmpFile) {
				PerGsonMap mp = PerGsonMap.ofOrCreate(tmpFile);
				Sys.p("Create blank >> " + mp.toStringJson());

				PerGsonMap child = mp.childOrCreate("child");
				Sys.p("Test childOrCreate OK >> " + mp.toStringJson());

				IT.state(mp.containsKey("child"), "child NOT containsKey");
				IT.state(child.parent() != null, "parent is null");
				IT.state(mp.child("child") != null, "child() is null");
				Sys.p("Test child OK >> " + mp.toStringJson());

				PerGsonMap child1 = mp.child("child");
				child1.put("ck1", "cv1");
				child1.put("ck2", "cv2");

				IT.state("cv1".equals(mp.child("child").get("ck1")), "Value not found");
				IT.state(child1.size() == 2, "length!=2");
				Sys.p("Test child with value OK >> " + mp.toStringJson());

				Sys.p("Test PerGsonMap Common Ok");
			}

		}.skipRemove(false).doOperation();
	}

	@Ignore//fail on autobuild via git installer
	@Test
	public void write_test() throws Exception {
		new TmpFileOperation() {

			@Override
			public void doOperationImpl(Path tmpFile) {
				PerGsonMap mp0 = PerGsonMap.of(PerGsonMapTest.class, "/mpc/json/PerGsomMap.json");
				PerGsonMap mp = PerGsonMap.of(mp0.map, tmpFile);
				mp.writeAndOffWrite();

				Sys.p(mp.toStringJson());
				Assert.assertTrue(!mp.isEmpty());

				PerGsonMap child1 = mp.child("child1");
				PerGsonMap child2 = child1.child("child2");

				child1.put("child1_id", 20);
				child2.put("child2_id", 200);

				child2.writeAndOffWrite();
				//
				//
				PerGsonMap fresh = mp.clone();

				String msg = X.f("map [\n%s\n]\n not eq [\n%s\n]\n", fresh.toStringJson(), mp.toStringJson());
				IT.state(fresh.equalsAsJson(mp), msg);

				Sys.p("Test PerGsonMap Write Tree Ok");
			}

		}.skipRemove(false).doOperation();
	}

}
