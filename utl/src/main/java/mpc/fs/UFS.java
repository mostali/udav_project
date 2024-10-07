package mpc.fs;

import lombok.SneakyThrows;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpc.arr.UStream;
import mpe.core.P;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.WhatIsTypeCheckedException;
import mpc.fs.ext.EXT;
import mpc.fs.fd.EFT;
import mpc.fs.path.IPath;
import mpc.map.UMap;
import mpu.core.RW;
import mpu.str.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UFS extends UFS_BASE {

	public static void main(String[] args) {
		Map<Path, List<Path>> twins = UFS.findTwinsAsMap(Paths.get("/home/dav/pjbf_tasks/69/12077852/content/"));
		P.exit(Rt.buildReport(twins));
		P.exit(twins);
		P.exit(COPY.copyDirContentWithReplace(Paths.get("/home/dav/pjbf_tasks/-1/src"), Paths.get("/home/dav/pjbf_tasks/-1/dst")));
		P.exit(generateUniqFilename(Paths.get("/home/dav/pjbf_tasks/64/txt (1)1.txt")));
	}

	public static Path FILE(String file) {
		return IT.isFileExist(Paths.get(file));
	}

	public static Path DIR(String file) {
		return IT.isDirExist(Paths.get(file));
	}

	public static InputStream[] cloneInputStream(InputStream fileInputStream, int countClones) throws IOException {
		if (countClones < 2) {
			countClones = 2;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = fileInputStream.read(buffer)) > -1) {
			baos.write(buffer, 0, len);
		}
		baos.flush();

		InputStream[] clones = new InputStream[countClones];
		for (int i = 0; i < countClones; i++) {
			clones[i] = new ByteArrayInputStream(baos.toByteArray());
		}
		return clones;
	}

	public static void convertCharset(Path fileIn, Path fileOut, Charset charsetIn, Charset charsetOut) throws IOException {
		String content = FileUtils.readFileToString(fileIn.toFile(), charsetIn);
		FileUtils.write(fileOut.toFile(), content, charsetOut);
	}

	/**
	 * *************************************************************
	 * ---------------------------- EXISTS ----------------------
	 * *************************************************************
	 */

	public static boolean exist(Path path) {
		return Files.exists(path);
	}

	public static boolean existFile(String path, boolean... withContent) {
		return ARG.isDefEqTrue(withContent) ? isFileWithContent(path) : Files.isRegularFile(Paths.get(path));
	}

	public static boolean existFile(Path path, boolean... withContent) {
		return ARG.isDefEqTrue(withContent) ? isFileWithContent(path) : Files.isRegularFile(path);
	}

	public static boolean existDir(Path path, boolean... withContent) {
		return ARG.isDefEqTrue(withContent) ? isDirWithContent(path) : Files.isDirectory(path);
	}

	public static boolean existFileObject(Object file) {
		IT.NN(file);
		if (file instanceof Path) {
			return Files.exists((Path) file);
		} else if (file instanceof File) {
			return ((File) file).exists();
		} else if (file instanceof CharSequence) {
			return new File(IT.NE(file.toString())).exists();
		}
		throw new FIllegalArgumentException("Except type file, but it [%s]", file.getClass());
	}

	public static boolean isFileWithContent(String file) {
		return isFileWithContent(Paths.get(file));
	}

	@SneakyThrows
	public static boolean isExistFileWithContent(Path destination) {
		return Files.size(IT.isFileExist(destination)) > 0;
	}

	public static boolean isFileWithContent(Path destination) {
		boolean isFile = Files.isRegularFile(destination);
		if (!isFile) {
			return false;
		}
		try {
			return Files.size(destination) != 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isDir(String path) {
		return isDir(Paths.get(path));
	}

	public static boolean isDir(Path path) {
		try {
			return Files.isDirectory(path);
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isDirEmpty(Path destination, boolean... noError) {
		return !isDirWithContent(destination, noError);
	}

	public static boolean isDirWithContent(Path destination, boolean... noError) {
		try (DirectoryStream<Path> directory = Files.newDirectoryStream(destination)) {
			if (directory.iterator().hasNext()) {
				return true;
			}
		} catch (IOException e) {
			if (ARG.isDefEqTrue(noError)) {
				return false;
			}
			X.throwException(e);
		}
		return false;
	}

	/**
	 * *************************************************************
	 * ---------------------------- CONVERT ----------------------
	 * *************************************************************
	 */
	public static <T, R> Collection<R> convert(Collection<T> files, Class<R> toType) {
		return files.stream().map(t -> convert(t, toType, false)).collect(Collectors.toList());
	}

	public static <T, R> R convert(T fromFile, Class<R> toType, boolean checkExist, R... defRq) {
		Exception err = null;
		try {
			R t = convertImpl(fromFile, toType);
			if (t != null) {
				if (!checkExist || UFS.existFileObject(t)) {
					return t;
				}
				err = new FIllegalArgumentException("File not exist[%s]", t);
			} else {
				err = new FIllegalArgumentException("Couldn't convert [%s] to [%s]", fromFile.getClass(), toType);
			}
		} catch (Exception ex) {
			err = ex;
		}
		return ARG.toDefThrow(err, defRq);
	}


	public static <T, R> R convertImpl(T fromFile, Class<R> toType) {
		if (toType.isAssignableFrom(fromFile.getClass())) {
			return (R) fromFile;
		} else if (CharSequence.class.isAssignableFrom(toType)) {
			return (R) fromFile.toString();
		} else if (CharSequence.class.isAssignableFrom(fromFile.getClass())) {
			if (toType == File.class) {
				return (R) new File(fromFile.toString());
			} else if (toType == Path.class) {
				return (R) Paths.get(fromFile.toString());
			}
		} else if (Path.class.isAssignableFrom(toType)) {
			if (toType == File.class) {
				return (R) ((File) fromFile).toPath();
			}
		} else if (File.class.isAssignableFrom(toType)) {
			if (Path.class.isAssignableFrom(fromFile.getClass())) {
				return (R) ((Path) fromFile).toFile();
			}
		}
		return null;
	}

	public static Path pwd() {
		return Paths.get(".").toAbsolutePath();
	}

	public static Boolean isFileOrDirOrNull(Path fd) throws WhatIsTypeCheckedException {
		if (fd != null) {
			if (Files.isRegularFile(fd)) {
				return true;
			} else if (Files.isDirectory(fd)) {
				return false;
			} else if (Files.exists(fd)) {
				throw new WhatIsTypeCheckedException("Path '%s' is not FILE&DIR", fd);
			}
		}
		return null;
	}

	public static Path copyFileNotExist_WithAutoSuffix(Path path) {
		String nm = path.getFileName().toString();
		Integer last = USToken.last(nm, "_", Integer.class, null);
		if (last == null) {
			last = 2;
			do {
				String newName = nm + "_" + ++last;
				path = path.getParent().resolve(newName);
			} while (Files.exists(path));
			return path;
		}
		do {
			String newName = USToken.firstGreedy(nm, "_", nm) + "_" + ++last;
			path = path.getParent().resolve(newName);
		} while (Files.exists(path));
		return path;
	}

	public static long size(IPath src) {
		return size(src.fPath());
	}

	public static long size(Path src) {
		return src.toFile().length();
	}

	@SneakyThrows
	public static void moveIfNotExistOrEmpty(Path targetFile, Path newChatVideoFile) {
		if (UFS.existFile(newChatVideoFile)) {
			if (targetFile.toFile().length() == newChatVideoFile.toFile().length()) {
				if (L.isInfoEnabled()) {
					L.info("Skip same downloaded file:" + targetFile);
				}
				return;
			} else {
				if (L.isInfoEnabled()) {
					L.info("Remove old downloaded file:" + newChatVideoFile);
				}
				Files.delete(newChatVideoFile);
			}
		}
		UFS.MV.move(targetFile, newChatVideoFile, true);
	}

	public static boolean isFileContainsLine(Path file, String line) throws IOException {
		IT.notNull(file, "Path is null");
		return RW.readContent_(file).contains(line);
	}

	public static boolean isEqFile(String file1, String file2, boolean... byContent) {
		return isEqFile(Paths.get(file1), Paths.get(file2), byContent);
	}

	public static boolean isEqFile(Path file1, Path file2, boolean... byContent) {
		boolean equals = Objects.equals(UFS.size(file1), UFS.size(file2));
		if (!equals) {
			return false;
		}
		return ARG.isDefEqTrue(byContent) ? Objects.equals(RW.readContent(file1), RW.readContent(file2)) : equals;
	}

	public static List<Path> findNotTwins(Path dir1, Path dir2, boolean... byContent) {
		List<Path> twinsDir1 = findTwins(dir1, dir2, byContent);
		List<Path> files1 = UDIR.ls(dir1, EFT.FILE, Collections.EMPTY_LIST);
		files1.removeAll(twinsDir1);
		return files1;
	}

	public static Sb findTwinsReport(Path dir1, Path dir2, boolean... byContent) {

		List<Path> notTwinsFR = UFS.findNotTwins(dir1, dir2, byContent);
		Sb rp1 = Rt.buildReport(notTwinsFR, "Uniq dir1:");
		List<Path> notTwinsWS = UFS.findNotTwins(dir2, dir1, byContent);
		Sb rt2 = Rt.buildReport(notTwinsWS, "Uniq dir2:");
		List<Path> twinsWS = UFS.findTwins(dir2, dir1, byContent);
		Sb rt3 = Rt.buildReport(twinsWS, "Twins dir1:");
		List<Path> twinsFR = UFS.findTwins(dir1, dir2, byContent);
		Sb rt4 = Rt.buildReport(twinsFR, "Twins dir2:");
		return Sb.of(rp1, rt2, rt3, rt4);
	}

	public static List<Path> findTwins(Path dir, boolean byContent) {
		List<Path> paths = UDIR.lsAll(dir, Files::isRegularFile);
		return findTwins(paths, paths, byContent);
	}

	public static List<Path> findTwins(Path dir1, Path dir2, boolean... byContent) {
		List<Path> files1 = UDIR.ls(dir1, EFT.FILE, Collections.EMPTY_LIST);
		List<Path> files2 = UDIR.ls(dir2, EFT.FILE, Collections.EMPTY_LIST);
		return findTwins(files1, files2, byContent);
	}

	public static List<Path> findTwins(List<Path> files1, List<Path> files2, boolean... byContent) {
		List<Path> twins = new LinkedList<>();
		for (Path file1 : files1) {
			for (Path file2 : files2) {
				if (file1 == file2) {
					continue;
				}
				boolean isSame = isEqFile(file1, file2, byContent);
				if (isSame) {
					twins.add(file1);
				}
			}
		}
		return twins;
	}

	public static Map<Path, List<Path>> findTwinsAsMap(Path dir, boolean... byContent) {
		List<Path> paths = UDIR.lsAll(dir, Files::isRegularFile);
		return findTwinsAsMap(paths, paths, byContent);
	}

	public static Map<Path, List<Path>> findTwinsAsMap(List<Path> files1, List<Path> files2, boolean... byContent) {
		Map<Path, List<Path>> twins = new HashMap<>();
		for (Path file1 : files1) {
			for (Path file2 : files2) {
				if (file1 == file2) {
					continue;
				}
				if (contains(twins, file2)) {
					continue;
				}
				boolean isSame = isEqFile(file1, file2, byContent);
				if (isSame) {
					UMap.putToValue(twins, file1, file2);
				}
			}
		}
		return twins;
	}

	private static boolean contains(Map<Path, List<Path>> twins, Path file) {
		Map.Entry<Path, List<Path>> pathListEntry = twins.entrySet().stream().filter(i -> i.getKey().equals(file) || i.getValue().contains(file)).findAny().orElse(null);
		return pathListEntry != null;
	}

	public static Path generateUniqFilename(Path file) {
		while (UFS.exist(file)) {
			String fn = UF.fn(file);
			String[] fn_ext = EXT.two(fn);
			String fnWoExt = fn_ext[0];
			Path finalFile = file;
			Function<String, Path> genNewFn = (String newFn) -> finalFile.getParent().resolve(EXT.twoJoin(newFn, fn_ext[1]));
			if (!fnWoExt.endsWith(")")) {
				file = genNewFn.apply(fnWoExt + "(0)");
			} else {
				String[] twoWithInc = USToken.twoGreedy(fnWoExt, "(", null);
				if (twoWithInc == null) {
					file = genNewFn.apply(fnWoExt + "(0)");
				} else {
					String inc = STR.substr(twoWithInc[1], -1);
					Long aLong = UST.LONG(inc, null);
					if (aLong == null) {
						file = genNewFn.apply(fnWoExt + "(0)");
					} else {
						file = genNewFn.apply(twoWithInc[0] + "(" + ++aLong + ")");
					}
				}
			}
		}
		return file;
	}

	public static void removeTwins(Map<Path, List<Path>> twins) {
		UStream.flatMapToList(twins.values()).forEach(UFS.RM::removeFileQk);
	}

	public static Map<String, Path> toMap(Collection<Path> alLSd3) {
		return alLSd3.stream().collect(Collectors.toMap(p -> p.getFileName().toString(), p -> p, (v1, v2) -> X.throwException("twins '%s' found", UF.fn(v1), UF.fn(v2)), LinkedHashMap::new));
	}

}
