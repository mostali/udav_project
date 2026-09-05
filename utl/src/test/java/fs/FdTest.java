package fs;

import mpc.fs.UFS;
import mpc.fs.fd.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
	public void fdExist() throws Exception {
		Assert.assertTrue(Fd.ofAsFd("pom.xml").fdExist());
		Assert.assertFalse(Fd.ofAsFd("pom.xml0").fdExist());
		Assert.assertTrue(Fd.ofAsFd(".").fdExist());
		Assert.assertTrue(Fd.ofAsFd("src").fdExist());
		Assert.assertFalse(Fd.ofAsFd("src0").fdExist());
	}

	@Test
	public void cat() throws Exception {
		Assert.assertTrue(Fd.ofAsFd("pom.xml").cat_().length() > 0);
		String content = Fd.ofAsFd("src/test/resources/FdTest.txt").cat_();
		Assert.assertTrue(content.length() > 0);

		String ct = Fd.ofAsFd("FdTest.txt").cat_(null);
		Assert.assertTrue("cat:file not exist", ct == null);

		content = Fd.ofAsFd("src").catWith_("test/resources/FdTest.txt");
		Assert.assertTrue(content.length() > 0);
		content = Fd.ofAsFd("src/test").catWith_("resources/FdTest.txt");
		Assert.assertTrue(content.length() > 0);
		content = Fd.ofAsFd("src/test/resources").catWith_("FdTest.txt");
		Assert.assertTrue(content.length() > 0);
	}

	@Test
	public void createIfNotExist() throws Exception {
		String rand = UUID.randomUUID().toString();

		try {
			Fd.ofAsFd(rand).createIfNotExist();
			Assert.assertFalse("createIfNotExist:file need self impl", true);
		} catch (Exception var3) {
		}

	}

	@Test
	public void delete_dir() throws Exception {
		String rand = UUID.randomUUID().toString()+"--";
		DIR dir = DIR.of(rand).createIfNotExist();
		Assert.assertTrue(dir.fdExist(new String[0]));
		Assert.assertTrue(dir.toPath().getFileName().toString().equals(rand));
		Assert.assertTrue((new File(dir.getFileOrDir())).getName().equals(rand));
		dir.fDelete(new String[0]);
		Assert.assertFalse(dir.fdExist(new String[0]));
	}

	@Test
	public void delete_file() throws Exception {
		String rand = UUID.randomUUID().toString();
		FILE file = FILE.of(rand).createIfNotExist();
		Assert.assertTrue(file.fdExist(new String[0]));
		Assert.assertTrue(file.toPath().getFileName().toString().equals(rand));
		Assert.assertTrue((new File(file.getFileOrDir())).getName().equals(rand));
		file.fDelete(new String[0]);
		Assert.assertFalse(file.fdExist(new String[0]));
	}

	@Test
	public void copyToDir() throws Exception {
		String rand = UUID.randomUUID().toString();
		DIR dir = DIR.of(rand).createIfNotExist();
		Assert.assertTrue(dir.fdExist(new String[0]));
		String rand2 = UUID.randomUUID().toString();
		DIR dir2 = DIR.of(rand2).createIfNotExist();
		Assert.assertTrue(dir2.fdExist(new String[0]));
		Path path = dir.copyToDir(dir2.toPath(), false, UFS.COPY.CopyOpt.DIR_MERGE);
//		Assert.assertTrue(dir2.ls(new String[0]).size() == 1);
//		Assert.assertTrue(((Path) dir2.ls(new String[0]).get(0)).equals(dir2.fPath().resolve(dir.fName())));
		Fd target = Fd.of(path);
		Assert.assertTrue(target.toPath().getFileName().toString().equals(rand));
		Assert.assertTrue((new File(target.getFileOrDir())).getName().equals(rand));
		dir.fDelete(new String[0]);
		dir2.fDelete(new String[0]);
		Assert.assertFalse(dir2.fdExist(new String[0]));
	}
}
