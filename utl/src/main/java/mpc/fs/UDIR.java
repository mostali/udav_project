package mpc.fs;

import lombok.SneakyThrows;
import mpc.fs.fd.UFD;
import mpu.core.ARG;
import mpu.IT;
import mpc.arr.NaturalOrderComparator;
import mpc.arr.STREAM;
import mpe.core.UBool;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.fd.EFT;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UDIR {

	public static boolean empty(Path path) {
		return empty(path, null, true);
	}

	@SneakyThrows
	public static boolean empty(Path path, Boolean returnIsNotDirectory_throwIfNull, Boolean returnIfNotExist_throwIfNull) {
		Boolean check = checkIsDir(path, returnIsNotDirectory_throwIfNull, returnIfNotExist_throwIfNull);
		if (check != null) {
			return check;
		}
		try (Stream<Path> entries = Files.list(path)) {
			return !entries.findFirst().isPresent();
		}
	}

	public static boolean hasFd(Path path,boolean skipErrors) {
		return hasFd(path, null, false,skipErrors);
	}

	@SneakyThrows
	public static boolean hasFd(Path path, Boolean returnIsNotDirectory_throwIfNull, Boolean returnIfNotExist_throwIfNull, boolean skipErrors) {
		Boolean check = checkIsDir(path, returnIsNotDirectory_throwIfNull, returnIfNotExist_throwIfNull);
		if (check != null) {
			return check;
		}
		try {
			try (Stream<Path> entries = Files.list(path)) {
				return entries.findFirst().isPresent();
			}
		} catch (AccessDeniedException ex) {
			if (skipErrors) {
				return false;
			}
			throw ex;
		}
	}

	@Nullable
	private static Boolean checkIsDir(Path path, Boolean returnIsNotDirectory_throwIfNull, Boolean returnIfNotExist_throwIfNull) {
		if (!Files.isDirectory(path)) {
			if (Files.exists(path)) {
				if (returnIsNotDirectory_throwIfNull == null) {
					throw new FIllegalStateException("Fd '%s ' exist - but is not dir", path);
				} else {
					return returnIsNotDirectory_throwIfNull;
				}
			} else {
				if (returnIfNotExist_throwIfNull == null) {
					throw new FIllegalStateException("Dir '%s' is not exist", path);
				} else {
					return returnIsNotDirectory_throwIfNull;
				}
			}
		}
		return null;
	}

	public static List<String> dir2dirnames(String dir) {
		return dir2dirnames(dir, new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
	}

	public static List<String> dir2dirnames(String dir, FilenameFilter ffilter) {
		File file = new File(dir);
		String[] directories = file.list(ffilter);
		if (directories == null) {
			return Collections.EMPTY_LIST;
		}
		return Arrays.asList(directories);
	}

	public static List<String> dir2dirschilds(String dir) {
		List<String> l = dir2dirs(dir);
		List<String> ls = new ArrayList();
		l.forEach(e -> {
			ls.addAll(dir2dirs(dir, null));
		});
		return ls;
	}

	public static List<String> dir2dirs(String dir) {
		return dir2dirs(dir, null);
	}

	public static List<String> dir2dirs(String dir, FilenameFilter ffilter) {
		File file = new File(dir);
		String[] directories = ffilter == null ? file.list() : file.list(ffilter);
		final String ndir = UF.normDir(dir);
		if (directories == null) {
			return Collections.EMPTY_LIST;
		}
		return Arrays.asList(directories).stream().map(e -> {
			return ndir + e;
		}).collect(Collectors.toList());
	}

	public static List<String> dir2files(String dir) {
		return dir2files(dir, (dir1, name) -> new File(dir1, name).isFile());
	}

	@Deprecated//old way
	public static List<String> dir2files(String dir, FilenameFilter ffilter) {
		File file = new File(dir);
		IT.isDirExist(file.toPath(), "Is not directory", dir);
		String[] directories = ffilter == null ? file.list() : file.list(ffilter);
		return Stream.of(directories).map(e -> UF.normDir(dir) + e).collect(Collectors.toList());
	}

	public static List<Path> ls(Path path, EFT fileType, List... defRq) {
		return LS_SORT.NOT.ls(path, fileType, defRq);
	}

	@Deprecated
	public static List<Path> lsNativeSort(Path path, EFT fileType, Boolean ascOrDescOrNothing, List... defRq) {
		LS_SORT sort = null;
		if (ascOrDescOrNothing == null) {
			sort = LS_SORT.NOT;
		} else if (ascOrDescOrNothing) {
			sort = LS_SORT.NATIVE;
		} else {
			sort = LS_SORT.NATIVE_DESC;
		}
		return ls(path, fileType, sort, defRq);
	}


	public static List<Path> ls(Path path, EFT fileType, LS_SORT lsSort, List... defRq) {
		return ls(path, fileType, lsSort, null, defRq);
	}

	public static List<Path> ls(Path path, EFT fileType, LS_SORT lsSort, Predicate<Path> filter, List... defRq) {
		if (lsSort == null) {
			lsSort = LS_SORT.NOT;
		}
		return lsSort.ls(path, fileType, filter, defRq);
	}

	static List<Path> ls_(Path path, EFT fileType, Boolean nativeOrNaturalOrNot, Boolean reverse, Boolean caseSensitive_ONLY_NATURAL) throws IOException {
		return ls_(path, fileType, nativeOrNaturalOrNot, reverse, caseSensitive_ONLY_NATURAL, null);
	}

	static List<Path> ls_(Path path, EFT fileType, Boolean nativeOrNaturalOrNot, Boolean reverse, Boolean caseSensitive_ONLY_NATURAL, Predicate<Path> filter) throws IOException {
		IT.isDirExist(path);
		Stream<Path> stream = Files.list(path);
		if (fileType != null) {
			switch (fileType) {
				case DIR:
					stream = stream.filter(file -> Files.isDirectory(file));
					break;
				case FILE:
					stream = stream.filter(file -> Files.isRegularFile(file));
					break;
			}
		}
		if (filter != null) {
			stream = stream.filter(filter);
		}
		if (nativeOrNaturalOrNot == null) {
			return stream.collect(Collectors.toList());
		} else if (nativeOrNaturalOrNot) {
			if (UBool.isTrueSafe(reverse)) {
				stream = stream.sorted(Comparator.reverseOrder());
			} else {
				stream = stream.sorted(Comparator.naturalOrder());
			}
		} else {
			stream = stream.sorted(UBool.isFalseSafe(caseSensitive_ONLY_NATURAL) ? NaturalOrderComparator.CASEINSENSITIVE_PATH_NUMERICAL_ORDER : NaturalOrderComparator.CASESENSITIVE_PATH_NUMERICAL_ORDER);
			List list = stream.collect(Collectors.toList());
			if (UBool.isTrueSafe(reverse)) {
				Collections.reverse(list);
			}
			return list;
		}
		return stream.collect(Collectors.toList());
	}

	public static List<Path> ls(String dir) {
		return ls(Paths.get(dir), null);
	}

	public static List<Path> lsAll(Path dir, Predicate<Path>... fileFilter) {
		List<Path> items = lsAll(dir.toFile()).stream().map(File::toPath).collect(Collectors.toList());
		return ARG.isDef(fileFilter) ? STREAM.filterToAll(items, ARG.toDef(fileFilter)) : items;
	}

	public static Collection<File> lsAll(File dir, Predicate<File>... fileFilter) {
		IT.isDirExist(dir.toPath());
		Collection<File> items = FileUtils.listFiles(dir, null, true);
		return ARG.isDef(fileFilter) ? STREAM.filterToAll(items, ARG.toDef(fileFilter)) : items;
	}

	@Deprecated //mv from UFS
	public static List<Path> ls_paths(Path directory, List<Path>... defRq) {
		File[] files = directory.toFile().listFiles();
		if (files != null) {
			return Arrays.asList(files).stream().map(f -> f.toPath()).collect(Collectors.toList());
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Folder [%s], File#listFiles return NULL", directory);
	}

	public static List<Path> ls(Path dir, EFT fileType, Boolean isAscOrDescOrNull, String... child) {
		Path path = UFD.pathWith(dir, child);
		return lsNativeSort(path, fileType, isAscOrDescOrNull);
	}
}
