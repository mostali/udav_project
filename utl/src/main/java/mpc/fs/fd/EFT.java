package mpc.fs.fd;

import mpc.exception.FIllegalStateException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.str.sym.SYMJ;
import mpu.IT;
import mpu.core.ARG;
import mpc.fs.LS_SORT;
import mpc.fs.UDIR;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;

//EnumFileType
public enum EFT {
	FILE, DIR;

	public static EFT of(String path, EFT... defRq) {
		return EFT.of(Paths.get(path), defRq);
	}

	public static EFT of(Path path, EFT... defRq) {
		if (path != null) {
			if (Files.isRegularFile(path)) {
				return FILE;
			} else if (Files.isDirectory(path)) {
				return DIR;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new WhatIsTypeException("What is type of file? [%s]. Exist:[%s]", path, path == null ? false : Files.exists(path));
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

	public static void checkExitedOrNot(Path path, EFT checkExitedOrNotIfNull) {
		if (checkExitedOrNotIfNull != null) {
			IT.state(checkExitedOrNotIfNull.existSave(path), "Except existed path [%s]", path);
			return;
		}
		//checkExitedOrNotIfNull=null - that required is not exist
		EFT eft = EFT.of(path, null);
		if (eft != null) {
			throw new FIllegalStateException("Except not existed path [%s], but path is existed as [%s]", eft);
		}
		//ok not exist
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
		return ls(dir, (LS_SORT) null, defRq);
	}

	public List<Path> ls(Path dir, LS_SORT sort, List<Path>... defRq) {
		return UDIR.ls(dir, this, sort, defRq);
	}

	public List<Path> ls(Path dir, LS_SORT sort, Predicate<Path> filter, List<Path>... defRq) {
		return UDIR.ls(dir, this, sort, filter, defRq);
	}

	public List<Path> ls(Path dir, Predicate<Path> filter, List<Path>... defRq) {
		return UDIR.ls(dir, this, null, filter, defRq);
	}

}
