package mpc.fs.path;

import com.google.common.collect.Multimap;
import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpc.exception.WhatIsTypeException;
import mpc.fs.LS_SORT;
import mpc.fs.UDIR;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.fs.ext.MapExt;
import mpc.fs.fd.EFT;
import mpc.fs.fd.IFd;
import mpc.types.ruprops.RuProps;
import mpc.types.ruprops.URuProps;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface IPath extends IFd {

	default boolean fdEquals(Object path) {
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
	default Path fWriteIn(String value) {
		Path path = toPath();
//		if (ARG.isDefNotEqTrue(createIfNotExist)) {
		IT.state(fType(null) == EFT.FILE, "Except exist file '%s'", path);
//		}
		RW.write(path, value);
		return path;
	}

	default String fRead(String... defRq) {
		return RW.readString(toPath(), defRq);
	}

	default String fCat(String... defRq) {
		return RW.readString(toPath(), defRq);
	}

	default RuProps fCatAsRuProps() {
		return RW.readRuProps(toPath());
	}

	default Multimap fCatAsMMap() {
		return URuProps.getRuPropertiesMultiMap(toPath());
	}

	default List<Path> dLs(EFT eft, List<Path>... defRq) {
		return UDIR.ls(toPath(), eft, defRq);
	}

	default List<Path> dLs(EFT eft, LS_SORT lsSort, List<Path>... defRq) {
		return UDIR.ls(toPath(), eft, lsSort, defRq);
	}

	default List<Path> dLs(EFT eft, LS_SORT lsSort, Predicate<Path> predicatePath, List<Path>... defRq) {
		List<Path> ls = UDIR.ls(toPath(), eft, lsSort, defRq);
		return predicatePath == null ? ls : STREAM.filterToAll(ls, predicatePath);
	}

	default Map<GEXT, List<Path>> dMapGExt(Map<GEXT, List<Path>>... defRq) {
		MapExt mapExt = dMapExt(null);
		if (mapExt != null) {
			try {
				return mapExt.getGMap();
			} catch (Exception e) {
				return ARG.toDefThrow(e, defRq);

			}
		}
		return ARG.toDefThrowMsg(() -> "except dir map", defRq);
	}

	;

	default MapExt dMapExt(Map<Path, EXT>... defRq) {
		return MapExt.of(toPath(), defRq);
	}

	default List<Path> fLsGEXT(GEXT... gexts) {
		return dLs(null, null, p -> Arrays.stream(gexts).filter(ge -> ge == GEXT.of(p, null)).findAny().isPresent(), ARR.EMPTY_LIST);
	}

	default List<Path> dLsEXT(EXT... gexts) {
		return dLs(null, null, p -> Arrays.stream(gexts).filter(ext -> ext == EXT.of(p, null)).findAny().isPresent(), ARR.EMPTY_LIST);
	}

	default boolean fdRename(String newName) {
		return fdMv(toPath().getParent().resolve(newName));
	}

	default void fdRmIfExist() {
		UFS.RM.deleteDir(toPath());
	}

	default IPath fMkfile() {
		Path path = toPath();
		switch (fType()) {
			case DIR:
				if (!UFS.existDir(path)) {
					UFS.MKDIR.createDirs(path);
				}
				return this;
			case FILE:
				if (!UFS.existFile(path)) {
					UFS.MKFILE.createFile(path);
				}
				return this;
			default:
				throw new WhatIsTypeException(fType());
		}
	}

	default boolean fdMv(Path dstPath) {
		String fn = dstPath.getFileName().toString();
		Path path = toPath();
		if (path.getFileName().toString().equals(fn)) {
			return false;
		}
		Path resolve = path.getParent().resolve(IT.isFilename(fn));
		UFS.MV.move(path, resolve, null);
		return true;
	}

	default boolean fdCp(String toDst) {
		Path path = toPath();
		if (path.getFileName().toString().equals(toDst)) {
			return false;
		}
		Path resolve = path.getParent().resolve(IT.isFilename(toDst));
		UFS.COPY.copyDirContentWithReplace(path, resolve, null);
		return true;
	}

	default boolean fdCp(Path toDst, boolean... mkdirs_mkdir_orNot) {
		Path path = toPath();
		if (toPath().equals(toDst)) {
			return false;
		}
		UFS.COPY.copyDirContentWithReplace(path, toDst, mkdirs_mkdir_orNot);
		return true;
	}

	default long fdSize0() {
		Path path = toPath();
		IT.isFileExist(path, "IPath '%s' not exist", this);
		return path.toFile().length();
	}

	default Long fdSize(Long... defRq) {
		return X.sizeOf(toPath(), defRq);
	}

	static IPath ofDirExisted(Path dir) {
		return new IPath() {
			@Override
			public Path toPath() {
				return IT.isDirExist(dir);
			}
		};
	}

	static IPath ofFileExisted(Path file) {
		return new IPath() {
			@Override
			public Path toPath() {
				return IT.isFileExist(file);
			}
		};
	}

	static IPath of(Path file, EFT... eft) {
		return new IPath() {
			@Override
			public Path toPath() {
				return file;
			}

			@Override
			public EFT fType(EFT... defRq) {
				return ARG.isDef(eft) ? ARG.toDef(eft) : IPath.super.fType(defRq);
			}
		};
	}

}
