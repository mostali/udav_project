package mpc.fs.path;

import lombok.SneakyThrows;
import mpc.fs.ext.MapExt;
import mpc.fs.fd.IFd;
import mpu.X;
import mpu.core.ARG;
import mpu.IT;
import mpc.arr.STREAM;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.*;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpu.core.ARR;
import mpu.core.RW;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface IPath extends IFd {

	default boolean fEquals(Object path) {
		if (path instanceof IPath) {
			return toPath().equals(((IPath) path).toPath());
		} else if (path instanceof Path) {
			return toPath().equals(path);
		} else if (path instanceof File) {
			return toPath().toFile().equals(path);
		} else if (path instanceof IFd) {
			return toPath().toFile().equals(((IFd) path).toFile());
		}
		return false;
	}

	@SneakyThrows
	default Path writeIn(String value) {
		Path path = toPath();
		IT.state(EFT.of(path) == EFT.FILE, "Except exist file '%s'", path);
		RW.write(path, value);
		return path;
	}

	default String fRead(String... defRq) {
		return RW.readContent(toPath(), defRq);
	}

	default String fCat(String... defRq) {
		return RW.readContent(toPath(), defRq);
	}

	default List<Path> fLs(EFT eft, List<Path>... defRq) {
		return UDIR.ls(toPath(), eft, defRq);
	}

	default List<Path> fLs(EFT eft, LS_SORT lsSort, List<Path>... defRq) {
		return UDIR.ls(toPath(), eft, lsSort, defRq);
	}

	default List<Path> fLs(EFT eft, LS_SORT lsSort, Predicate<Path> predicatePath, List<Path>... defRq) {
		List<Path> ls = UDIR.ls(toPath(), eft, lsSort, defRq);
		return predicatePath == null ? ls : STREAM.filterToAll(ls, predicatePath);
	}

	default Map<GEXT, List<Path>> fMapGExt(){
		return fMapExt().getGMap();
	};

	default MapExt fMapExt() {
		return MapExt.of(toPath());
	}

	default List<Path> fLsGEXT(GEXT... gexts) {
		return fLs(null, null, p -> Arrays.stream(gexts).filter(ge -> ge == GEXT.of(p, null)).findAny().isPresent(), ARR.EMPTY_LIST);
	}

	default List<Path> fLsEXT(EXT... gexts) {
		return fLs(null, null, p -> Arrays.stream(gexts).filter(ext -> ext == EXT.of(p, null)).findAny().isPresent(), ARR.EMPTY_LIST);
	}

	default boolean fRename(String newName) {
		return fMv(toPath().getParent().resolve(newName));
	}

	default IPath fMkfile() {
		Path path = toPath();
		switch (fType()) {
			case DIR:
				if (!UFS.existDir(path)) {
					UFS_BASE.MKDIR.createDirs(path);
				}
				return this;
			case FILE:
				if (!UFS.existFile(path)) {
					UFS_BASE.MKFILE.createFile(path);
				}
				return this;
			default:
				throw new WhatIsTypeException(fType());
		}
	}

	default boolean fMv(Path dstPath) {
		String fn = dstPath.getFileName().toString();
		Path path = toPath();
		if (path.getFileName().toString().equals(fn)) {
			return false;
		}
		Path resolve = path.getParent().resolve(IT.isFilename(fn));
		UFS_BASE.MV.move(path, resolve, null);
		return true;
	}

	default boolean fCp(String toDst) {
		Path path = toPath();
		if (path.getFileName().toString().equals(toDst)) {
			return false;
		}
		Path resolve = path.getParent().resolve(IT.isFilename(toDst));
		UFS_BASE.COPY.copyDirContentWithReplace(path, resolve, null);
		return true;
	}

	default boolean fCp(Path toDst, boolean... mkdirs_mkdir_orNot) {
		Path path = toPath();
		if (toPath().equals(toDst)) {
			return false;
		}
		UFS_BASE.COPY.copyDirContentWithReplace(path, toDst, mkdirs_mkdir_orNot);
		return true;
	}

	default long fSize0() {
		Path path = toPath();
		IT.isFileExist(path, "IPath '%s' not exist", this);
		return path.toFile().length();
	}

	default Long fSize(Long... defRq) {
		return X.sizeOf(toPath(), defRq);
	}

	static IPath of(Path file) {
		return new IPath() {
			@Override
			public Path toPath() {
				return file;
			}
		};
	}

}
