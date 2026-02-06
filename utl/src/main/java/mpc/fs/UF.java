package mpc.fs;

import mpc.fs.ext.EXT;
import mpc.fs.path.IPath;
import mpc.url.UUrl;
import mpu.core.ARG;
import mpu.IT;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.ext.FileExtInfo;
import mpc.env.Env;
import mpe.str.Cyr2Lat;
import mpc.str.sym.SYM;
import mpu.str.Regexs;
import mpu.str.UST;
import mpu.str.TKN;
import mpu.X;
import mpv.byteunit.ByteUnit;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UF {


	public final static String DEL_NAME_BLINE = "___";

	public static final String SEP = File.separator;
	public static final String NL = SYM.NEWLINE;
	public static final String CURRENT_DIR_UNIX = "./";
	public static final String CURRENT_DIR_WIN = ".\\";
	public static final String ROOT_DIR_UNIX = "/";
	public static final String ROOT_DIR_WIN = "\\";

	public static final char[] ILLEGAL_CHARACTERS_FILENAME = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
	public static final char[] ILLEGAL_CHARACTERS_DIRNAME = {'\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', ':'};
	public static final String PFX_FILE = "file://";

	public static String clearFilename(String filename) {
		return filename.chars().mapToObj(c -> {
			return StringUtils.containsNone("" + (char) c, ILLEGAL_CHARACTERS_FILENAME) ? (Character) (char) c : "_";
		}).map(String::valueOf).collect(Collectors.joining());
	}

	public static String clearDirname(String filename) {
		return filename.chars().mapToObj(c -> {
			return StringUtils.containsNone("" + (char) c, ILLEGAL_CHARACTERS_DIRNAME) ? (Character) (char) c : "_";
		}).map(String::valueOf).collect(Collectors.joining());
	}

	public static String normFileStartEnd(String path) {
		return normFileEnd(normFileStart(path));
	}

	public static String normDir(String dir, String child) {
		return normFile(dir, child) + SEP;
	}

	public static String normFile(String dir, String child) {
		return normDir(IT.NE(dir)) + normFile(IT.NE(child));
	}

	public static String normUnixRootDir(String dir) {
		return ROOT_DIR_UNIX + normFileStartRk(dir);
	}

	public static String normUnixRootDir(String dir, String child) {
		return normUnixRootFile(dir, child) + ROOT_DIR_UNIX;
	}

	public static String normUnixRootFile(String dir, String child) {
		return normUnixRootFile(normFile(dir, child));
	}

	public static String normUnixRootFile(String file) {
		return file.startsWith(ROOT_DIR_UNIX) ? file : ROOT_DIR_UNIX + file;
	}


	public static String normDir(String... paths) {
		return normFd(true, paths);
	}

	public static String normFile(String... paths) {
		return normFd(false, paths);
	}

	public static String normFd(boolean isDir, String... pathss) {
		switch (pathss.length) {
			case 0:
				throw new IllegalArgumentException("empty dir parts");
			case 1:
				return normDir(pathss[1]);
			default:
				StringBuilder urlSb = new StringBuilder(normFileEnd(pathss[0]));
				for (int i = 1; i < pathss.length; i++) {
					urlSb.append(SEP);
					urlSb.append(normFile(pathss[i]));
				}
				if (isDir) {
					urlSb.append(SEP);
				}
				return urlSb.toString();
		}
	}

	public static String normDir(String dir) {
		return dir == null ? null : normFileEnd(dir) + SEP;
	}

	public static String normDirEnd(String dir) {
		return isSlashLast(dir) ? dir : dir + SEP;
	}

	public static String normFileEndRk(String file) {
		while (isSlashLast(file)) {
			file = file.substring(0, file.length() - 1);
		}
		return file;
	}

	public static String normFileEnd(String file) {
		return isSlashLast(file) ? file.substring(0, file.length() - 1) : file;
	}

	public static boolean isSlashLast(String file) {
		return file.endsWith(ROOT_DIR_UNIX) || file.endsWith(ROOT_DIR_WIN);
	}

	public static boolean isSlashFirst(String file) {
		return file.startsWith(ROOT_DIR_UNIX) || file.startsWith(ROOT_DIR_WIN);
	}

	public static String normFileStartRk(String file) {
		while (isSlashFirst(file)) {
			file = file.substring(1);
		}
		return file;
	}

	public static String normFileStart(String file) {
		return isSlashFirst(file) ? file.substring(1) : file;
	}

	public static Path normFileStartRootAndRel(Path path) {
		return path == null ? null : Paths.get(UF.normFileStartRootAndRel(path.toString()));
	}

	public static String normFileStartRootAndRel(String f) {
		while (true) {
			if (isSlashFirst(f)) {
				f = f.substring(1);
				continue;
			}
			if (f.startsWith(CURRENT_DIR_UNIX) || f.startsWith(CURRENT_DIR_WIN)) {
				f = f.substring(2);
				continue;
			}
			return f;
		}
	}

	public static String normDirCurrentStartRel(String f) {
		while (f.startsWith(CURRENT_DIR_UNIX) || f.startsWith(CURRENT_DIR_WIN)) {
			f = f.substring(2);
		}
		return f;
	}

	public static String normFile(String f) {
		f = normFileEnd(f);
		f = normFileStart(f);
		return f;
	}

	public static boolean equalsFileName(String f1, String f2) {
		return normFileEnd(f1).equals(normFileEnd(f2));
	}

	/**
	 * *************************************************************
	 * --------------------------- Create Filename ------------------------
	 * *************************************************************
	 */

	public static String createCompoundFileName_clearStringCyrKeepSlash(String... args) {
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(arg).append(DEL_NAME_BLINE);
		}
		String res = sb.substring(0, sb.length() - DEL_NAME_BLINE.length());
		return UF.clearStringCyrKeepSlash(res);
	}


	//	public static String clearFileNameRemoveSlash(Path fileName) {
//		return clearFileNameRemoveSlash(fileName.getFileName().toString());
//	}
	@Deprecated
	public static String clearFileName_RemoveSlash(String fileName) {
		return fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
	}

	@Deprecated
	public static String clearFileName_KeepSlash(String fileName) {
		return fileName.replaceAll("[^a-zA-Z0-9\\.\\-\\\\/]", "_");
	}

	@Deprecated
	public static String clearFileNameRu_KeepSlash(String fileName) {
		return fileName.replaceAll("[^а-яА-Яa-zA-Z0-9\\.\\-\\\\/]", "_");
	}

	@Deprecated
	public static String clearFileNameRU_RemoveSlash(String fileName) {
		return fileName.replaceAll("[^а-яА-Яa-zA-Z0-9\\.\\-]", "_");
	}

	/**
	 * *************************************************************
	 * --------------------------- Get data from url ------------------------
	 * *************************************************************
	 */
	@Deprecated//See UUrl
	public static String getExtFromDirtyUrl(String url) {
		String urlWoQuery = UUrl.getUrlWoQuery(url);
		return getExtFromCleanUrl(urlWoQuery);
	}

	@Deprecated
	public static String getExtFromCleanUrl(String url) {
		return url.substring(url.lastIndexOf('.') + 1);
	}

	@Deprecated
	public static String getFileNameFromUrl_WithoutExtension(String url) {
		String file = getFileNameFromUrl(url);
		return TKN.firstGreedy(file, '.', file);
	}

	/**
	 * 1. if not found -> index=-1 + 1 =0 (return full string)
	 * 2. if end with char '/' -> return ''
	 *
	 * @param url
	 * @return
	 */
	@Deprecated //error if is dirty url?
	public static String getFileNameFromUrl(String url) {
		String file = url.substring(url.lastIndexOf('/') + 1, url.length());
		return UUrl.getUrlWoQuery(file);
	}

	/**
	 * *************************************************************
	 * --------------------------- Get data from url ------------------------
	 * *************************************************************
	 */

	@Deprecated
	public static String clearStringCyrRemoveSlash(String searchPath) {
		return clearFileName_RemoveSlash(Cyr2Lat.cyr2lat(searchPath));
	}

	@Deprecated
	public static String clearStringCyrKeepSlash(String searchPath) {
		return clearFileName_KeepSlash(Cyr2Lat.cyr2lat(searchPath));
	}

	@Deprecated//see UUrl
	public static String getPathFileNameWithQuery(String url) {
		return Paths.get(url).getFileName().toString();
	}

	public static String getNameWoExt(String path, String... defRq) {
		return TKN.firstGreedy(path, '.', defRq);
	}

	public static String getNameWoExt(Path path, String... defRq) {
		return getNameWoExt(path.getFileName().toString(), defRq);
	}

	public static String fnWoExtQk(Path p) {
		String fname = p.getFileName().toString();
		return TKN.firstGreedy(fname, '.', fname);
	}

	public static File f(String file) {
		IT.notEmpty(file, "file is null");
		return new File(file);
	}

	public static Path path(String file, String... childs) {
		return Paths.get(file, childs);
	}

	public static String fnFromUrl(String url) throws MalformedURLException {
		return fnFromUrl(UST.URL(url));
	}

	public static String fnFromUrl(URL url) throws MalformedURLException {
		return path(url.getPath()).getFileName().toString();
	}

	public static String replacePackageSeparator(String packageName) {
		return packageName.replace(".", UF.SEP);
	}


	public static boolean isFile(String file) {
		return Files.isRegularFile(Paths.get(file));
	}

	public static boolean isFileOrDirNameByEnd(String file) {
		return file.endsWith(ROOT_DIR_UNIX) || file.endsWith(ROOT_DIR_WIN) ? false : true;
	}

	public static boolean isDir(String file) {
		return file.endsWith(ROOT_DIR_UNIX) || file.endsWith(ROOT_DIR_WIN);
	}


	//
	//..

	public static Path normParentCharacter(Path path, Path parent) {
		if (!isPathStartFromParent(path)) {
			return path;
		} else if (path.getNameCount() == 1) {
			return parent;
		} else {
			String woParent = path.toString().substring(2);
			return parent.resolve(UF.normFileStartRk(woParent));
		}
	}


	public static boolean isPathStartFromParent(Path path) {
		return path.getName(0).toString().equals("..");
	}

	//
	//~

	public static boolean isPathStartFromHome(Path path) {
		return path.getName(0).toString().equals("~");
	}

	public static String normHomeCharacter(String s) {
		return s.startsWith("~") ? normDir(Env.HOME_LOCATION.toString()) + normFileStart(s.substring(1)) : s;
	}

	public static Path normHomeCharacter(Path path) {
		return isPathStartFromHome(path) ? Paths.get(normHomeCharacter(path.toString())) : path;
	}

	//
	//

	public static String getFirstCleanName(String file, String... defRq) {
		String cleanRootDir = UF.normFile(UF.normFileStartRootAndRel(file));
		if (X.notEmpty(cleanRootDir)) {
			String first = Paths.get(cleanRootDir).getName(0).toString();
			return first;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Path '%s' without first clean name", file);
	}

	public static boolean isCurrentDir(String dir) {
		return "".equals(UF.normDirCurrentStartRel(dir));
	}

	public static String normPath(String part1, String part2) {
		if (part1.endsWith(ROOT_DIR_UNIX) || part1.endsWith(ROOT_DIR_WIN)) {
			return part2.startsWith(ROOT_DIR_UNIX) || part2.startsWith(ROOT_DIR_WIN) ? UF.normFileEnd(part1) + part2 : part1 + part2;
		} else {
			return part2.startsWith(ROOT_DIR_UNIX) ? part1 + part2 : UF.normDirEnd(part1) + part2;
		}
	}

	public static String toAbsPath(String toFile) {
		return new File(toFile).getAbsolutePath();
	}

	public static FileExtInfo getExtInfo(Path file) {
		FileExtInfo fext = FileExtInfo.of(file);
		return fext;
	}

	public static List<String> fn(List files) {
		Collection<Path> convert = UFS.convert(files, Path.class);
		return convert.stream().map(UF::fn).collect(Collectors.toList());
	}

	public static String fn(String fd) {
		return fn(Paths.get(fd));
	}

	public static String sfn(Path path, String... defRq) {
		return EXT.fn(path, defRq);
	}

	public static String fnp(Path path, String... defRq) {
		if (path != null) {
			Path parent = path.getParent();
			if (parent != null) {
				Path fn = parent.getFileName();
				if (fn != null) {
					return fn.toString();
				}
				return ARG.toDefThrow(() -> new RequiredRuntimeException("Path [%s] parent filename is null", path), defRq);
			}
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Path [%s] parent is null", path), defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Path [%s] is null", path), defRq);
	}

	public static String fn(Path path, String... defRq) {
		if (path != null) {
			Path fn = path.getFileName();
			if (fn != null) {
				return fn.toString();
			}
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Path [%s] filename is null", path), defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Path is null"), defRq);
	}

	public static String fnWithSize(Path path) {
		return fn(path) + "(" + X.sizeOf(path, ByteUnit.MB) + "Mb)";
	}

	public static String ln(File file) {
		return "file://" + file.getAbsolutePath();
	}

	public static String ln(Object file) {
		if (file instanceof File) {
			file = ((File) file).getAbsolutePath();
		} else if (file instanceof Path) {
			file = ((Path) file).toAbsolutePath();
		} else if (file instanceof IPath) {
			file = ((IPath) file).toPath().toAbsolutePath();
		}
		return "file://" + file;
	}

	public static String unln(String fn) {
		IT.state(fn.startsWith("file://"));
		return fn.substring(7);
	}

	public static boolean isValidFilename(String filename) {
		return !StringUtils.containsNone(filename, ILLEGAL_CHARACTERS_FILENAME);
	}

	public static boolean isValidFilenameClean(String filename) {
		return X.notEmpty(filename) && filename.matches(Regexs.WORD_RU);
	}

	public static String toStrConsole(Path tmpFile) {
		return "file://" + tmpFile;
	}

	public static Path newRenameName(Path renamedPath, String newName) {
		return renamedPath.getParent().resolve(newName);
	}

	public static String fn(Path resolve, int count, String... defRq) {
		if (count > resolve.getNameCount()) {
			return ARG.toDefThrowMsg(() -> X.f("Not found path item %s from %s", count, resolve), defRq);
		}
		return fn_(resolve, count).toString();
	}

	public static String name(Path path, int index, String... defRq) {
		if (index > path.getNameCount()) {
			return ARG.toDefThrowMsg(() -> X.f("Not found path item %s from %s", index, path), defRq);
		}
		return path.getName(index).toString();
	}

	public static Path fn_(Path resolve, int count) {
		Path path = null;
		for (int f = resolve.getNameCount() - count; f < resolve.getNameCount(); f++) {
			path = path == null ? resolve.getName(f) : path.resolve(resolve.getName(f));
		}
		return path;
	}

	public static List<Path> toList(Path path) {
		return ARR.toList(path.iterator());
	}

	public static Path item(Path path, int index, Path... defRq) {
		return ARRi.item(toList(path), index, defRq);
	}

	public static String itemString(Path path, int index, Path... defRq) {
		return item(path, index, defRq).toString();
	}

	public static Path cloneParent(Path fileLog, String withName) {
		return fileLog.getParent().resolve(withName);
	}
}
