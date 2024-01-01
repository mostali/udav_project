package mpc.fs.path;

import lombok.SneakyThrows;
import mpc.args.ARG;
import mpc.ERR;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.*;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpf.ns.space.ST;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface IPath {
	Path path();

	default String name() {
		return path().getFileName().toString();
	}

	default EXT ext() {
		return EXT.of(path());
	}

	default EFT ft() {
		return EFT.of(path());
	}

	default boolean isGExt(GEXT gExt) {
		return gExt.is(path());
	}

	default boolean exist() {
		return Files.exists(path());
	}

	default boolean equals(Path path) {
		return path.equals(path());
	}

	public static Path path(Object path, Path... defRq) {
		if (path instanceof IPath) {
			return ((IPath) path).path();
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except object '%s' with path", defRq));
	}

	@SneakyThrows
	default Path writeIn(String value) {
		Path path = path();
		RW.write(path, value);
		return path;
	}

	default String readOut(String... defRq) {
		return RW.readContent(path(), defRq);
	}

	default String cat(String... defRq) {
		return RW.readContent(path(), defRq);
	}

	default List<Path> ls(EFT eft, List<Path>... defRq) {
		return UDIR.ls(path(), eft, defRq);
	}

	default List<Path> ls(EFT eft, LS_SORT lsSort, List<Path>... defRq) {
		return UDIR.ls(path(), eft, lsSort, defRq);
	}

	default boolean rename(String newName) {
		return mv(path().getParent().resolve(newName));
	}

	default IPath mk() {
		Path path = path();
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

	default boolean mv(Path dstPath) {
		String fn = dstPath.getFileName().toString();
		Path path = path();
		if (path.getFileName().toString().equals(fn)) {
			return false;
		}
		Path resolve = path.getParent().resolve(ERR.isFilename(fn));
		UFS_BASE.MV.move(path, resolve, null);
		return true;
	}

	default boolean cp(String filname) {
		Path path = path();
		if (path.getFileName().toString().equals(filname)) {
			return false;
		}
		Path resolve = path.getParent().resolve(ERR.isFilename(filname));
		UFS_BASE.COPY.copyDirContentWithReplace(path, resolve, null);
		return true;
	}

	default boolean cp(Path filname, boolean... mkdirs_mkdir_orNot) {
		Path path = path();
		if (path().equals(filname)) {
			return false;
		}
		UFS_BASE.COPY.copyDirContentWithReplace(path, filname, mkdirs_mkdir_orNot);
		return true;
	}

	default long file_size() {
		Path path = path();
		ERR.isFileExist(path, "IPath '%s' not exist", this);
		return path.toFile().length();
	}

	default boolean isProps() {
		return name().startsWith(ST.PROPS_SFX);
	}

}
