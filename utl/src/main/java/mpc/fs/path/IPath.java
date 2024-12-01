package mpc.fs.path;

import lombok.SneakyThrows;
import mpc.fs.ext.MapExt;
import mpu.X;
import mpu.core.ARG;
import mpu.IT;
import mpc.arr.ST;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.*;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpu.core.ARR;
import mpu.core.RW;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

//private String _noteDirPath;
//private Path noteDirPath0;
//public Path path() {
//return noteDirPath0 != null ? noteDirPath0 : (noteDirPath0 = Paths.get(_noteDirPath));
//}
public interface IPath {
	Path fPath();

	default String fName() {
		return fPath().getFileName().toString();
	}

	default Path fParent() {
		return fPath().getParent();
	}

	default IPath fParent0() {
		return IPath.this::fParent;
	}

	default EXT fExt() {
		return EXT.of(fPath());
	}

	default GEXT fGext(GEXT... defRq) {
		return GEXT.of(fExt(), defRq);
	}

	default MapExt fMapExt() {
		return MapExt.of(fPath());
	}

	default EFT ft() {
		return EFT.of(fPath());
	}

	default boolean fExist() {
		return Files.exists(fPath());
	}

	default boolean fEquals(Object path) {
		if (path instanceof IPath) {
			return fPath().equals(((IPath) path).fPath());
		} else if (path instanceof Path) {
			return fPath().equals(path);
		} else if (path instanceof File) {
			return fPath().toFile().equals(path);
		}
		return false;
	}

	static Path path(Object path, Path... defRq) {
		if (path instanceof IPath) {
			return ((IPath) path).fPath();
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except object '%s' with path", defRq));
	}

	@SneakyThrows
	default Path writeIn(String value) {
		Path path = fPath();
		RW.write(path, value);
		return path;
	}

	default String fRead(String... defRq) {
		return RW.readContent(fPath(), defRq);
	}

	default String fCat(String... defRq) {
		return RW.readContent(fPath(), defRq);
	}

	default List<Path> fLs(EFT eft, List<Path>... defRq) {
		return UDIR.ls(fPath(), eft, defRq);
	}

	default List<Path> fLs(EFT eft, LS_SORT lsSort, List<Path>... defRq) {
		return UDIR.ls(fPath(), eft, lsSort, defRq);
	}

	default List<Path> fLs(EFT eft, LS_SORT lsSort, Predicate<Path> predicatePath, List<Path>... defRq) {
		List<Path> ls = UDIR.ls(fPath(), eft, lsSort, defRq);
		return predicatePath == null ? ls : ST.filter(ls, predicatePath);
	}

	default List<Path> fLsGEXT(GEXT... gexts) {
		return fLs(null, null, p -> Arrays.stream(gexts).filter(ge -> ge == GEXT.of(p, null)).findAny().isPresent(), ARR.EMPTY_LIST);
	}

	default List<Path> fLsEXT(EXT... gexts) {
		return fLs(null, null, p -> Arrays.stream(gexts).filter(ext -> ext == EXT.of(p, null)).findAny().isPresent(), ARR.EMPTY_LIST);
	}

	default boolean fRename(String newName) {
		return fMv(fPath().getParent().resolve(newName));
	}

	default IPath fMkfile() {
		Path path = fPath();
		switch (ft()) {
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
				throw new WhatIsTypeException(ft());
		}
	}

	default boolean fMv(Path dstPath) {
		String fn = dstPath.getFileName().toString();
		Path path = fPath();
		if (path.getFileName().toString().equals(fn)) {
			return false;
		}
		Path resolve = path.getParent().resolve(IT.isFilename(fn));
		UFS_BASE.MV.move(path, resolve, null);
		return true;
	}

	default boolean fCp(String toDst) {
		Path path = fPath();
		if (path.getFileName().toString().equals(toDst)) {
			return false;
		}
		Path resolve = path.getParent().resolve(IT.isFilename(toDst));
		UFS_BASE.COPY.copyDirContentWithReplace(path, resolve, null);
		return true;
	}

	default boolean fCp(Path toDst, boolean... mkdirs_mkdir_orNot) {
		Path path = fPath();
		if (fPath().equals(toDst)) {
			return false;
		}
		UFS_BASE.COPY.copyDirContentWithReplace(path, toDst, mkdirs_mkdir_orNot);
		return true;
	}

	default long fSize0() {
		Path path = fPath();
		IT.isFileExist(path, "IPath '%s' not exist", this);
		return path.toFile().length();
	}

	default Long fSize(Long... defRq) {
		return X.sizeOfFile(fPath(), defRq);
	}


}
