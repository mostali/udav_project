package mpc.fs.fd;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARG;
import mpc.fs.LS_SORT;
import mpc.fs.UDIR;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public enum EFT {
	FILE, DIR;

	public static EFT of(String fd, EFT... defRq) {
		return EFT.of(Paths.get(fd), defRq);
	}

	public static EFT of(Path fd, EFT... defRq) {
		if (fd != null) {
			if (Files.isRegularFile(fd)) {
				return FILE;
			} else if (Files.isDirectory(fd)) {
				return DIR;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new WhatIsTypeException("What is type of file? [%s]. Exist:[%s]", fd, fd == null ? false : Files.exists(fd));
	}

	public static boolean existSaveRq(Path file, boolean isFile) {
		return of(isFile).existSave(file);
	}

	@Deprecated
	public static EFT of(boolean isFile) {
		return isFile ? FILE : DIR;
	}

	public static boolean existSave(Path destFileOrDir, boolean isFileOrDir) {
		return of(isFileOrDir).existSave(destFileOrDir);
	}

	public static Boolean toBoolean(Path path) {
		if (Files.isRegularFile(path)) {
			return true;
		} else if (Files.isDirectory(path)) {
			return false;
		}
		return null;
	}

	public boolean notExistSave(Path newPath) {
		return !existSave(newPath);
	}

	public boolean existSave(File newPath) {
		return existSave(newPath.toPath());
	}

	public boolean existSave(String newPath) {
		return existSave(Paths.get(newPath));
	}

	public boolean existSave(Path newPath) {
		if (!Files.exists(newPath)) {
			return false;
		}
		switch (this) {
			case DIR:
				if (Files.isDirectory(newPath)) {
					return true;
				} else {
					throw new WhatIsTypeException("Is target FD must be DIR [%s]", newPath);
				}
			case FILE:
				if (Files.isRegularFile(newPath)) {
					return true;
				} else {
					throw new WhatIsTypeException("Is target FD  must be FILE [%s]", newPath);
				}
			default:
				throw new WhatIsTypeException("What is type ? [%s]", this);
		}
	}

	public List<Path> ls(Path dir, List<Path>... defRq) {
		return ls(dir, null, defRq);
	}

	public List<Path> ls(Path dir, LS_SORT sort, List<Path>... defRq) {
		return UDIR.ls(dir, this, defRq);
	}
}
