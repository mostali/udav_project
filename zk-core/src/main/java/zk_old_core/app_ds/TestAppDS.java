package zk_old_core.app_ds;

import mpu.Sys;
import mpu.IT;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.fs.path.UPath;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TestAppDS extends AppDS {

	public static TestAppDS dir = new TestAppDS("dir");
	public static TestAppDS file = new TestAppDS("file");
	public static TestAppDS self = new TestAppDS(".");
	public static final String TEST_DIR = "./tmp/test-struct";
	public static final String TEST_DIR_OUT = "./tmp/test-struct-out";

	static Path testStruct = null;
	static Path testStructOut = null;

	public static void main(String[] args) throws IOException {
		try {
			Path testDir = Paths.get(TEST_DIR);
			UFS_BASE.RM.deleteDir_(testDir);
			UFS_BASE.MKDIR.createDirs_(testDir);
			Path testDirOut = Paths.get(TEST_DIR_OUT);
			UFS_BASE.RM.deleteDir_(testDirOut);
			UFS_BASE.MKDIR.createDirs_(testDirOut);


			testStruct = UFS.DIR(TEST_DIR);
			testStructOut = UFS.DIR(TEST_DIR_OUT);

			check_mkdir_flag();

			Path testOk = TestAppDS.dir.writeToDir(testStruct, "child-dir", "child-file", "child-file-content", true);
			Sys.p("Ok - create in struct / child-dir / child-file :" + testOk);

			testOk = TestAppDS.dir.writeToFile(testStruct, "struct-file", "file-content", true);
			Sys.p("Ok - create in DIR:" + testOk);

			testOk = TestAppDS.self.writeToFile(testStruct, "self-file", "self-file-content", true);
			Sys.p("Ok - create in SELF:" + testOk);

			TestAppDS.self.setPropsProperty(testStruct, "key", "value-self");
			Sys.p("Ok - set property in SELF");

			TestAppDS.dir.setPropsProperty(testStruct, "key", "value-struct");
			Sys.p("Ok - set property in DIR");

			Path childDir = TestAppDS.dir.mkDir_(testStruct, "child-mkdir");
			Sys.p("Ok - mkDir_ child in DIR:" + childDir);

			TestAppDS.dir.writeToDir(testStruct, "child-mkdir", "ttt", "tt", false);

			Path newDir = TestAppDS.self.moveToMe(testStruct, childDir);
			Sys.p("Ok - moveToMe in SELF:" + IT.isDirExist(newDir));

			newDir = TestAppDS.self.rename(testStruct, "child-mkdir", "child-mkdir-renamed");
			IT.state(UPath.eqName(newDir, "child-mkdir-renamed"));
			Sys.p("Ok - rename SELF:" + IT.isDirExist(newDir));

			newDir = TestAppDS.dir.moveToMe(testStruct, newDir);
			Sys.p("Ok - moveToMe in DIR:" + IT.isDirExist(newDir));

			newDir = TestAppDS.dir.moveToMe(testStruct, newDir);
			Sys.p("Ok - moveToMe in DIR:" + IT.isDirExist(newDir));


			newDir = TestAppDS.dir.renameMe(testStruct, "new-name");
			Sys.p("Ok - renameMe in DIR:" + IT.isDirExist(newDir));


			newDir = TestAppDS.self.moveMe(testStruct, testDirOut, null, StandardCopyOption.REPLACE_EXISTING);
			Sys.p("Ok - moveMe in OUT:" + IT.isDirExist(newDir));

//				newDir = TestDS.self.moveMeTo(testStruct, testDirOut);
//				P.p("Ok - moveMeTo in OUT:" + UC.isDirExist(newDir));

		} finally {
			UFS_BASE.RM.deleteDir(testStruct);
			UFS_BASE.RM.deleteDir(testStructOut);
//				UC.isDirNotExist(testStruct);
			Sys.p("test final");
		}
	}

	private static void check_mkdir_flag() throws IOException {

		try {
			TestAppDS.dir.writeToDir_(testStruct, "child-dir", "child-file", "child-file-content", null);
		} catch (IOException ex) {
			if (ex instanceof NoSuchFileException) {
				Sys.p("OK - mkdirs-flag");
			} else {
				throw ex;
			}
		}

	}

	public TestAppDS(String name) {
		super(name);
	}


}
