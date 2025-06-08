package mpc.fs.fd;

import mpc.exception.FIllegalStateException;
import mpc.exception.FUnsupportedOperationException;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import mpu.core.RW;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.path.UPath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UFD {

	public static FIllegalStateException newExceptionFileWithChild(Path path, String... child) {
		return new FIllegalStateException("Entity [%s] is file. But set predicat-child [%s]", path, ARG.toDef(child));
	}

	public static FIllegalStateException newExceptionFileWithoutChild(Path path) {
		return new FIllegalStateException("Entity [%s] need child", path);
	}

	public static String[] childAsArray(String child) {
		return X.notEmpty(child) ? new String[]{child} : new String[0];
	}

	public static boolean checkIs(Path path, EFT fileOrDir, boolean... RETURN) {
		EFT checked = EFT.of(path, null);
		if (path != null && checked == IT.NN(fileOrDir)) {
			return true;
		} else if (ARG.isDefEqTrue(RETURN)) {
			return false;
		}
		throw new UnsupportedOperationException("Unsupported EFT '" + (path == null ? null : EFT.of(path)) + "'");
	}

	public static Path pathWith(Path path, String... child) {
		if (!ARG.isDef(child)) {
			return path;
		} else if (EFT.of(path) == EFT.FILE) {
			throw newExceptionFileWithChild(path, child);
		}
		return createDirWithChild(path, child);
	}

	public static Path pathWith(Path path, EFT fieType, String... child) {
		if (!ARG.isDef(child)) {
			return path;
		} else if (fieType == EFT.FILE) {
			throw newExceptionFileWithChild(path, child);
		}
		return createDirWithChild(path, child);
	}

	public static Path createDirWithChild(Path dir, String... child) {
		if (ARG.isDef(child)) {
			String[] childStr = IT.NE(child, "predicat is empty");
			String childFirst = IT.NE(childStr[0], "predicat[0] is empty");
			dir = dir.resolve(childFirst);
		}
		return dir;
	}

	public static InputStream toInputStream(Path path, InputStream... defRq) {
		EFT type = EFT.of(path, null);
		if (type != null) {
			switch (type) {
				case FILE:
					return RW.readAs(path, InputStream.class, defRq);
				case DIR:
					throw new FUnsupportedOperationException("Impossible get InputStream from dir '%s'", type);
				default:
					throw new WhatIsTypeException(type);
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Error get InputStream from path '%s'", path);
	}

	public static InputStream toInputStream(Path path, String child, InputStream... defRq) {
		EFT type = EFT.of(path, null);
		if (type != null) {
			switch (type) {
				case FILE:
					throw new FUnsupportedOperationException("Impossible resolve child '%s' from FILE '%s'", child, path);
				case DIR:
					Path pathWithChild = path.resolve(IT.NE(child));
					if (UFS.existDir(pathWithChild)) {
						throw new FUnsupportedOperationException("Impossible get InputStream from DIR '%s' with child '%s'", path, child);
					}
					return RW.readAs(pathWithChild, InputStream.class, defRq);
				default:
					throw new WhatIsTypeException(type);
			}
		}
		return ARG.toDefRq(defRq);
	}

	public static class CHILDS {
		public static List<Path> getAllChildPathsRelative(Path path, EFT fileType, List... defRq) {
			return getAllChildPaths(path, fileType, defRq).stream().map(p -> path.relativize(p)).collect(Collectors.toList());
		}

		public static List<String> getAllChildPathsNames(Path path, EFT fileType, List... defRq) {
			return UPath.path2names(getAllChildPaths(path, fileType, defRq));
		}

		public static List<Path> getAllChildPaths(Path path, EFT fileType, List... defRq) {
			try {
				return getAllChildPaths_(path, fileType);
			} catch (Exception e) {
				if (ARG.isDef(defRq)) {
					return ARG.toDef(defRq);
				}
				throw new RequiredRuntimeException(e);
			}
		}

		public static List<Path> getAllChildPaths_(Path path, EFT fileType) throws IOException {
			return Files.walk(path).filter(p -> {
				if (p.equals(path)) {
					return false;
				} else if (fileType == null) {
					return true;
				}
				switch (fileType) {
					case FILE:
						return Files.isRegularFile(p);
					case DIR:
						return Files.isDirectory(p);
					default:
						throw new WhatIsTypeException(fileType);
				}
			}).collect(Collectors.toList());
		}

		public static List<Path> listDirRecursive(Path directory) throws IOException {
			List<Path> files = new ArrayList<>();
			try (Stream<Path> paths = Files.walk(directory)) {
				paths
						.filter(Files::isRegularFile)
						.forEach(e -> files.add(e));
			}
			return files;
		}

		public static List<Path> findDiffInDstRelative(Path srcDir, Path dst) {
			List<Path> srcAll = getAllChildPathsRelative(srcDir, null);
			List<Path> dstAll = getAllChildPathsRelative(dst, null);
			if (srcAll.isEmpty() && dstAll.isEmpty()) {
				return Collections.EMPTY_LIST;
			} else {
				srcAll.removeAll(dstAll);
				return srcAll;
			}
		}

		public static List<Path> findDiffInDst(List<Path> src, List<Path> dst) {
			List<Path> diff = new LinkedList();
			for (Path srcPath : src) {
				if (!dst.contains(srcPath)) {
					diff.add(srcPath);
				}
			}
			return diff;
		}

		public static boolean isChildOfParent(Path parent, Path child) {
			return child.startsWith(parent.toAbsolutePath());
		}

		public static Path getRelativeChildPath(Path pathParent, Path pathChild) {
			return pathParent.relativize(pathChild);
		}

		public static Path childOfParent(String rootFolder, String childPath, Path defRootFolder) throws IOException {
			childPath = UF.normFileStart(IT.NE(childPath, "set child-path"));
			if (X.blank(rootFolder) && defRootFolder == null) {
				throw new NullPointerException("Set someone parent");
			}
			Path childPathOfParent = rootFolder == null ? defRootFolder.resolve(childPath) : Paths.get(rootFolder, childPath);
			return childPathOfParent;
		}
	}

}
