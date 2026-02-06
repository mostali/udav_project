package mpc.fs.path;

import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.EFT;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UPath {

	public static Path normPath(Path path) {
		if (!isLastPathCurrent(path)) {
			return path;
		}
		Path parent = path.getParent();
		if (parent == null) {
			return Paths.get("");
		}
		return normPath(parent);
	}

	public static boolean isLastPathCurrent(Path path) {
		return eqName(path, ".");
	}

	public static boolean isLastPathParent(Path path) {
		return eqName(path, "..");
	}

	@Deprecated //TODO
	public static Path getParentOfPathWithParent(Path path0, Path... defRq) {
		Path path = normPath(path0);
		if (isLastPathParent(path)) {
			return path.resolve("..");
		}
		Path parent = path.getParent();
		if (parent != null) {
			return parent;
		} else {
			return Paths.get("..");
		}
		//		if (ARG.isDef(defRq)) {
		//			return ARG.toDef(defRq);
		//		}
		//		throw new RequiredRuntimeException("Path without parent:" + path);
	}

	public static boolean eqName(Path path, String name) {
		return path.getFileName().toString().equals(name);
	}

	public static boolean startsWith(Path path, String pfx) {
		return path.getFileName().toString().startsWith(pfx);
	}

	public static boolean endsWith(Path path, String pfx) {
		return path.getFileName().toString().endsWith(pfx);
	}

	public static Path existed(String file, EFT ft_or_any, Path... defRq) {
		if (file != null) {
			Path path = Paths.get(file);
			if (ft_or_any == null) {
				if (Files.exists(path)) {
					return path;
				}
			} else {
				switch (ft_or_any) {
					case DIR:
						if (Files.isDirectory(path)) {
							return path;
						}
						break;
					case FILE:
						if (Files.isRegularFile(path)) {
							return path;
						}
						break;
					default:
						throw new WhatIsTypeException(ft_or_any);
				}
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Path ( as %s ) '%s' not exist", ft_or_any, file);
	}

	public static boolean exist(String file, EFT ft_or_any) {
		return existed(file, ft_or_any, null) != null;
	}

	public static List<String> path2names(List<Path> paths) {
		return paths.stream().map(p -> p.getFileName().toString()).collect(Collectors.toList());
	}

	public static List<String> path2strs(List<Path> paths) {
		return paths.stream().map(p -> p.toString()).collect(Collectors.toList());
	}

	public static List<String> path2abs(List<Path> paths) {
		return paths.stream().map(p -> p.toAbsolutePath().toString()).collect(Collectors.toList());
	}

	public static Collection<String> path2namesRel(Path parent, List<Path> paths) {
		return paths.stream().map(p -> path2nameRel(parent, p)).collect(Collectors.toList());
	}

	public static String path2nameRel(Path parent, Path path) {
		return parent.relativize(path).toString();
	}

	public static String[] explodeToDir_I_File(Path path, String parentIfSingleFile) {
		return new String[]{path.getNameCount() == 1 ? parentIfSingleFile : path.getParent().toString(), path.getFileName().toString()};
	}

	public static Path item(Path path, int index, Path... defRq) {
		if (index >= 0 && path.getNameCount() > index) {
			return path.getName(index);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Path by index '%s' not found", index), defRq);
	}

	public static Path first(Path path, Path... defRq) {
		return item(path, 0, defRq);
	}

	public static Path last(Path path, Path... defRq) {
		if (path.getNameCount() > 0) {
			return path.getName(path.getNameCount() - 1);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except path last item from '%s'", path), defRq);
	}

	public static Path getTwo_ParentWithChild(Path writedClpImg) {
		return writedClpImg.getParent().getFileName().resolve(writedClpImg.getFileName());
	}
}
