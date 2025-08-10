package mpc.fs;

import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpc.arr.STREAM;
import mpe.core.P;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.WhatIsTypeCheckedException;
import mpc.fs.ext.EXT;
import mpc.fs.path.IPath;
import mpu.core.QDate;
import mpu.core.RW;
import mpu.str.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UFS extends UFS_BASE {

	public static void main(String[] args) {
		X.exit();
		System.out.println(new File(".").getAbsolutePath());
		X.exit();
		X.p(toMapByAscCreatedText(Paths.get("/home/dav/pjbf/aiweb-exp/cache-nav")));
		X.exit();
		List test = new ArrayList(Arrays.asList(0, 1, 3));
		for (int i = 0; i < test.size(); i++) {
			Object o = test.get(i);
			X.p("+" + o);
			if (o.equals(1)) {
				test.add(2, 3);
			}
		}
		X.exit();
//		String c1 = RW.readContent(Paths.get("/home/dav/pjnsi_tasks/24/150d6b6b-5cb8-4aec-b028-0c65a77096eb.xml"));
//		String c2 = RW.readContent(Paths.get("/home/dav/pjnsi_tasks/24/96416410-81cd-49d1-a823-abc0a61f34d5.xml"));
//		X.exit(c1.equals(c2));

		P.exit(COPY.copyDirContentWithReplace(Paths.get("/home/dav/pjbf_tasks/-1/src"), Paths.get("/home/dav/pjbf_tasks/-1/dst")));
		P.exit(generateUniqFilename(Paths.get("/home/dav/pjbf_tasks/64/txt (1)1.txt")));
	}

	//return asc by lastModified
	@SneakyThrows
	public static Map<Path, Long> toMapByAscCreated(Path dir) {
		Comparator<Path> pathComparator = (p1, p2) -> p1.toFile().lastModified() > p2.toFile().lastModified() ? 1 : -1;
		return Files.list(dir).sorted(pathComparator).collect(Collectors.toMap(p -> p, p -> p.toFile().lastModified(), (t1, t2) -> t1, LinkedHashMap::new));
	}

	@SneakyThrows
	public static Map<Path, String> toMapByAscCreatedText(Path dir) {
		Comparator<Path> pathComparator = (p1, p2) -> p1.toFile().lastModified() > p2.toFile().lastModified() ? 1 : -1;
		return Files.list(dir).sorted(pathComparator).collect(Collectors.toMap(p -> p, p -> QDate.of(p.toFile().lastModified()).f(QDate.F.MONO20NF), (t1, t2) -> t1, LinkedHashMap::new));
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

	public static boolean exist(String path) {
		return exist(Paths.get(path));
	}

	public static boolean exist(Path path) {
		return Files.exists(path);
	}

	public static boolean existFile(String path, boolean... withContent) {
		return ARG.isDefEqTrue(withContent) ? isFileWithContent(path) : isFile(Paths.get(path));
	}

	public static boolean existFile(Path path, boolean... withContent) {
		return ARG.isDefEqTrue(withContent) ? isFileWithContent(path) : isFile(path);
	}

	public static boolean existDir(Path path, boolean... withContent) {
		return ARG.isDefEqTrue(withContent) ? isDirWithContent(path, false) : Files.isDirectory(path);
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
	public static boolean isFileWithContent(Path destination) {
		boolean isFile = isFile(destination);
		if (!isFile) {
			return false;
		}
		return Files.size(destination) != 0;
	}

	public static boolean isFile(Path destination) {
		return destination != null && Files.isRegularFile(destination);
	}

	public static boolean isDir(String path) {
		return isDir(Paths.get(path));
	}

	public static boolean isDir(Path path) {
		return Files.isDirectory(path);
	}

	public static boolean isDirWoContent(Path destination, boolean requiredDir) {
		return !isDirWithContent(destination, requiredDir);
	}

	@SneakyThrows
	public static boolean isDirWithContent(Path destination, boolean requiredDir) {
		if (!UFS.isDir(destination)) {
			if (!requiredDir) {
				return false;
			} else if (!UFS.exist(destination)) {
				throw new FileNotFoundException("Directory '" + destination + "' not exist's");
			} else {
				throw new FileNotFoundException("Is not directory '" + destination + "'");
			}
		}
		try (DirectoryStream<Path> directory = Files.newDirectoryStream(destination)) {
			Iterator<Path> iterator = directory.iterator();
			while (iterator.hasNext()) {
				Path next = iterator.next();
				if (UFS.isDir(next)) {
					if (isDirWithContent(next, requiredDir)) {
						return true;
					}
				} else if (UFS.isFile(next)) {
					if (isFileWithContent(next)) {
						return true;
					}
				}
			}
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
			if (isFile(fd)) {
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
		Integer last = TKN.last(nm, "_", Integer.class, null);
		if (last == null) {
			last = 2;
			do {
				String newName = nm + "_" + ++last;
				path = path.getParent().resolve(newName);
			} while (Files.exists(path));
			return path;
		}
		do {
			String newName = TKN.firstGreedy(nm, "_", nm) + "_" + ++last;
			path = path.getParent().resolve(newName);
		} while (Files.exists(path));
		return path;
	}

	public static long size(IPath src) {
		return size(src.toPath());
	}

	public static long size(Path src) {
		return src.toFile().length();
	}

	@SneakyThrows
	public static long sizeLines(Path src) {
		long lineCount;
		try (Stream<String> stream = Files.lines(src, StandardCharsets.UTF_8)) {
			lineCount = stream.count();
		}
		return lineCount;
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
				String[] twoWithInc = TKN.twoGreedy(fnWoExt, "(", null);
				if (twoWithInc == null) {
					file = genNewFn.apply(fnWoExt + "(0)");
				} else {
					String inc = STR.substrCount(twoWithInc[1], -1);
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
		STREAM.flatMapToList(twins.values()).forEach(UFS.RM::removeFileQk);
	}

	public static Map<String, Path> toMapByFn(Collection<Path> alLSd3) {
		return alLSd3.stream().collect(Collectors.toMap(p -> p.getFileName().toString(), p -> p, (v1, v2) -> X.throwException("twins '%s' found", UF.fn(v1), UF.fn(v2)), LinkedHashMap::new));
	}

	@SneakyThrows
	public static List<Path> ls(Path path, List... defRq) {
		if (Files.isDirectory(path)) {
			return Files.list(path).collect(Collectors.toList());
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except dir '%s'", path), defRq);
	}

	@SneakyThrows
	public static TreeSet<Path> lsSorted(Path path, Predicate<Path> filter, TreeSet... defRq) {
		if (Files.isDirectory(path)) {
			return Files.list(path).filter(filter).collect(Collectors.toCollection(TreeSet::new));
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except dir '%s'", path), defRq);
	}

	public static List<Path> getRelativePaths(List<Path> list, Path commonPath) {
		return STREAM.mapToList(list, p -> commonPath.relativize(p));
	}

	@SneakyThrows
	public static Path deleteFile(Path path) {
		Files.delete(path);
		return path;
	}

	@SneakyThrows
	public static void truncate(Path fileLog) {
		Files.write(fileLog, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
	}

	public static List<String> toFileNames(Set<Path> paths) {
		return paths.stream().map(p -> p.getFileName().toString()).collect(Collectors.toList());
	}

	public static void writeTmpFile(String data) {
		Path file = Paths.get("/tmp/" + UUID.randomUUID() + ".tmpfile");
		RW.write(file, data);
		L.info("Created tmp file " + UF.PFX_FILE + file);
	}
}
