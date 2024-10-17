package fs;

import mpc.fs.UFS_BASE;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import mpc.fs.fd.DIR;
import mpc.fs.fd.FILE;
import mpc.fs.fd.Fd;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

public class FdTest {
	public FdTest() {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void exist() throws Exception {
		Assert.assertTrue(Fd.of("pom.xml").exist(new String[0]));
		Assert.assertFalse(Fd.of("pom.xml0").exist(new String[0]));
		Assert.assertTrue(Fd.of(".").exist(new String[0]));
		Assert.assertTrue(Fd.of("src").exist(new String[0]));
		Assert.assertFalse(Fd.of("src0").exist(new String[0]));
	}

	@Test
	public void cat() throws Exception {
		Assert.assertTrue(Fd.of("pom.xml").cat_().length() > 0);
		String content = Fd.of("src/test/resources/FdTest.txt").cat_();
		Assert.assertTrue(content.length() > 0);

		String ct = Fd.of("FdTest.txt").cat_(null);
		Assert.assertTrue("cat:file not exist", ct == null);

		content = Fd.of("src").catWith_("test/resources/FdTest.txt");
		Assert.assertTrue(content.length() > 0);
		content = Fd.of("src/test").catWith_("resources/FdTest.txt");
		Assert.assertTrue(content.length() > 0);
		content = Fd.of("src/test/resources").catWith_("FdTest.txt");
		Assert.assertTrue(content.length() > 0);
	}

	@Test
	public void createIfNotExist() throws Exception {
		String rand = UUID.randomUUID().toString();

		try {
			Fd.of(rand).createIfNotExist();
			Assert.assertFalse("createIfNotExist:file need self impl", true);
		} catch (Exception var3) {
		}

	}

	@Test
	public void delete_dir() throws Exception {
		String rand = UUID.randomUUID().toString();
		DIR dir = DIR.of(rand).createIfNotExist();
		Assert.assertTrue(dir.exist(new String[0]));
		Assert.assertTrue(dir.path().getFileName().toString().equals(rand));
		Assert.assertTrue((new File(dir.getFileOrDir())).getName().equals(rand));
		dir.delete(new String[0]);
		Assert.assertFalse(dir.exist(new String[0]));
	}

	@Test
	public void delete_file() throws Exception {
		String rand = UUID.randomUUID().toString();
		FILE file = FILE.of(rand).createIfNotExist();
		Assert.assertTrue(file.exist(new String[0]));
		Assert.assertTrue(file.path().getFileName().toString().equals(rand));
		Assert.assertTrue((new File(file.getFileOrDir())).getName().equals(rand));
		file.delete(new String[0]);
		Assert.assertFalse(file.exist(new String[0]));
	}

	@Test
	public void copyToDir() throws Exception {
		String rand = UUID.randomUUID().toString();
		DIR dir = DIR.of(rand).createIfNotExist();
		Assert.assertTrue(dir.exist(new String[0]));
		String rand2 = UUID.randomUUID().toString();
		DIR dir2 = DIR.of(rand2).createIfNotExist();
		Assert.assertTrue(dir2.exist(new String[0]));
		Path path = dir.copyToDir(dir2.path(), false, UFS_BASE.COPY.CopyOpt.DIR_MERGE);
		Assert.assertTrue(dir2.ls(new String[0]).size() == 1);
		Assert.assertTrue(((Path) dir2.ls(new String[0]).get(0)).equals(dir2.path().resolve(dir.name())));
		Fd target = Fd.of(path);
		Assert.assertTrue(target.path().getFileName().toString().equals(rand));
		Assert.assertTrue((new File(target.getFileOrDir())).getName().equals(rand));
		dir.delete(new String[0]);
		dir2.delete(new String[0]);
		Assert.assertFalse(dir2.exist(new String[0]));
	}
}
