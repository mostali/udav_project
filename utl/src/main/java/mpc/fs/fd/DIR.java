package mpc.fs.fd;

import mpc.ERR;
import mpc.fs.*;
import mpc.fs.ext.EXT;
import mpc.fs.ext.MapExt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DIR extends Fd {

	public DIR(String dir) {
		super(dir, EFT.DIR);
	}

	public DIR(Path path) {
		super(path, EFT.DIR);
	}

	public boolean existDir(String... childOrSelf) {
		return Files.isDirectory(childOrSelf.length == 0 ? path() : path().resolve(childOrSelf[0]));
	}

	public boolean existFile(String child) {
		return Files.isRegularFile(path().resolve(child));
	}

	public static DIR of(String dir) {
		return new DIR(dir);
	}

	public static DIR of(Path path) {
		return new DIR(path);
	}


	public Fd resolve(String fileOrDir) {
		return Fd.of(path().resolve(fileOrDir));
	}

	public DIR resolveDir(String dir) {
		return of(path().resolve(dir));
	}

	public FILE resolveFile(String file) {
		return FILE.of(path().resolve(file));
	}

	public DIR createIfNotExistRq() {
		try {
			return createIfNotExist();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public DIR createIfNotExist() throws IOException {
		UFS_BASE.MKDIR.createDirs_(path().toFile(), true, false);
		return this;
	}

	public Map<Path, EXT> getMapExt(EFT fileType, LS_SORT lsSort, String... child) {
		Path path = UFD.pathWith(path(), child);
		return MapExt.getMapExt(path, fileType, lsSort);
	}

	public List<Path> getChilds(EFT fileType, LS_SORT lsSort) {
		return getChilds(fileType, lsSort, null, null);
	}

	public List<Path> getChilds(EFT fileType, LS_SORT lsSort, String child_or_null, Predicate<Path> filter) {
		Path path = child_or_null == null ? path() : UFD.pathWith(path(), new String[]{child_or_null});
		return UDIR.ls(path, fileType, lsSort, filter);
	}

	public List<Path> getChilds(EFT fileType, String childDir, LS_SORT lsSort, List<Path>... defRq) {
		return getChilds(fileType, childDir, lsSort, null, defRq);
	}

	public List<Path> getChilds(EFT fileType, LS_SORT lsSort, List<Path>... defRq) {
		return UDIR.ls(path(), fileType, lsSort, defRq);
	}

	public List<Path> getChilds(EFT fileType, String childDir, LS_SORT lsSort, String child, List<Path>... defRq) {
		Path path = UFD.pathWith(path().resolve(childDir), UFD.childAsArray(child));
		return UDIR.ls(path, fileType, lsSort, defRq);
	}


	public List<Path> getChildsDefRq(EFT fileType, String root_part, LS_SORT lsSort, List<Path> defRq, String... child) {
		Path path = UFD.pathWith(path().resolve(root_part), child);
		return UDIR.ls(path, fileType, lsSort, defRq);
	}

	public List<Path> getChildsInner(EFT fileType, String root_part, String com, LS_SORT lsSort, String... child) {
		Path path = UFD.pathWith(path().resolve(root_part).resolve(ERR.notEmpty(com)), child);
		return UDIR.ls(path, fileType, lsSort);
	}

	public List<Path> getChildsInnerDefRq(EFT fileType, String root_part, String com, LS_SORT lsSort, List<Path> defRq, String... child) {
		Path path = UFD.pathWith(path().resolve(root_part).resolve(ERR.notEmpty(com)), child);
		return UDIR.ls(path, fileType, lsSort, defRq);
	}
}
