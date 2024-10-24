package zk_old_core.mdl;

import mpu.core.ARG;
import mpc.fs.LS_SORT;
import mpc.fs.UDIR;
import mpc.fs.dir_struct.DirStructRW;
import mpc.fs.fd.DIR;
import mpc.fs.fd.EFT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import zk_page.ZKC;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public abstract class DirModel extends FdModel {

	public static final Logger L = LoggerFactory.getLogger(DirModel.class);

	public static List<Path> getAllPaths(Path path) {
		return UDIR.lsAll(path);
	}

	public void setAttributeTo(Component com, String key) {
		String value = attrKey_Root1();
		setAttributeTo(com, key, value);
	}

	public void setAttributeTo(Component com, String key, String value) {
		ZKC.setAttributeToCom(com, key, value);
	}

	public String attrKey_Root1() {
		return dir().getFileOrDir();
	}

	private transient DIR rootDirPath;

//	public String name() {
//		return path().getFileName().toString();
//	}
//
//	public Path path() {
//		return dir().getFileOrDirPath();
//	}

	public DIR dir() {
		return rootDirPath != null ? rootDirPath : (DIR.of(rootFd()));
	}

	public DirModel(Path rootDirPath) {
		super(rootDirPath);
	}


//	public RuProps props() {
//		return props(false, false);
//	}

//	public Path getPropsPath() {
//		return getDefaultPropertiesPath(path());
//	}
//	public static RuProps readProps(Path pageDir, boolean syncWrite) {
//		return RuProps.of(getDefaultPropertiesPath(pageDir)).syncWrite(syncWrite);
//	}
//	public static Path getDefaultPropertiesPath(Path comDir) {
//		return comDir.resolve(RepoDS.FN_ROOT_PROPS);
//	}

	private transient Map mapExt;

	public Map getMapExt(boolean... fresh) {
		if (mapExt == null || ARG.isDefEqTrue(fresh)) {
			return mapExt = dir().getMapExt(null, LS_SORT.NATURAL);
		}
		return mapExt;
	}

//	public static DirModel of(String pathUsrDir) {
//		return new DirModel(Paths.get(pathUsrDir));
//	}

//	public static DirModel of(File file) {
//		return of(file.toPath());
//	}

//	public static DirModel of(Path file) {
//		return new DirModel(file);
//	}

	public List<Path> getChilds(EFT fileType, LS_SORT sort) {
		return dir().getChilds(fileType, sort);
	}

	public List<Path> getChilds(EFT fileType, Predicate<Path> filter) {
		return getChilds(fileType, null, filter);
	}

	public List<Path> getChilds(EFT fileType, LS_SORT sort, Predicate<Path> filter) {
		return dir().getChilds(fileType, sort, null, filter);
	}

	public Path path(String filename) {
		return DirStructRW.child(path(), filename);
	}


//	public String cnt(String filename) {
//		return UFILE.readLine(path(), filename);
//	}

	public List<Path> getAllPaths() {
		return getAllPaths(path());
	}
}
