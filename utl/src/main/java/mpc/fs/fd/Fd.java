package mpc.fs.fd;

import mpc.exception.FIllegalStateException;
import mpc.exception.WhatIsTypeException;
import lombok.Getter;
import mpu.core.RW;
import mpu.core.ARR;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import mpc.fs.UDIR;
import mpc.fs.UFS_BASE;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpc.fs.UFS;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class Fd {
	public static final Logger L = LoggerFactory.getLogger(Fd.class);

	@Getter
	private final String fileOrDir;

	private Path fdPath;

	protected final EFT fileType;

	public Fd(String fileOrDir, EFT... eft) {
		this.fileOrDir = fileOrDir;
		this.fdPath = Paths.get(fileOrDir);
		this.fileType = ARG.isDef(eft) ? ARG.toDef(eft) : EFT.of(fdPath, null);
	}

	public Fd(Path fdPath, EFT... eft) {
		this.fdPath = fdPath;
		this.fileOrDir = fdPath.toString();
		this.fileType = ARG.isDef(eft) ? ARG.toDef(eft) : EFT.of(fdPath);
	}

	public static Fd of(String file) {
		return new Fd(file);
	}

	public static Fd of(Path path) {
		return new Fd(path);
	}

	public EFT type(EFT... defRq) {
		return fileType != null ? fileType : ARG.toDefRq(defRq);
	}

	public Path path() {
		return fdPath != null ? fdPath : (fdPath = Paths.get(fileOrDir));
	}

	public String name() {
		return path().getFileName().toString();
	}

	protected Path pathWith(String... child) {
		if (!ARG.isDef(child)) {
			return path();
		} else if (type() == EFT.FILE) {
			throw newExceptionFileWithChild(child);
		}
		return UFD.pathWith(path(), type(), child);
	}

	protected FIllegalStateException newExceptionFileWithChild(String... child) {
		return UFD.newExceptionFileWithChild(path(), child);
	}

	protected FIllegalStateException newExceptionFileWithoutChild() {
		return UFD.newExceptionFileWithoutChild(path());
	}

	private Path createDirWithChild(String... child) {
		return UFD.createDirWithChild(path(), child);
	}

	public boolean exist(String... child) {
		return Files.exists(pathWith(child));
	}

	public boolean empty(String... child) {
		Path entry = pathWith(child);
		if (!Files.exists(entry)) {
			return false;
		}
		EFT typeFile = EFT.of(entry);
		switch (typeFile) {
			case DIR:
				return X.empty(UDIR.ls_paths(entry));
			case FILE:
				return !UFS.isFileWithContent(entry);
			default:
				throw new WhatIsTypeException(typeFile);
		}
	}

	public boolean deleteQk(String... child) {
		try {
			delete(child);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public String delete(String... child) throws IOException {
		Path entry = pathWith(child);
		FileUtils.forceDelete(entry.toFile());
		return entry.toString();
	}

	public String cat_(String... defRq) throws IOException {
		return cat_(path(), defRq);
	}

	public String catWith_(String child) throws IOException {
		return cat_(pathWith(child));
	}

	public static String cat_(Path path, String... defRq) throws IOException {
		if (UFS.existFile(path)) {
			return RW.readContent_(path, Charset.defaultCharset());
		}
		return ARG.toDefRq(defRq);
	}

	@Deprecated
	public List<Path> lsQk(String... child) {
		try {
			return ls(child);
		} catch (Exception ex) {
			return null;
		}
	}

	@Deprecated
	public List<Path> lsWithSort(boolean isAsc, String... child) throws IOException {
		return ARR.sort(ls(child), isAsc);
	}

	@Deprecated
	public List<Path> ls(String... child) throws IOException {
		return UDIR.ls_paths(pathWith(child));
	}

	public boolean isFile(String... child) {
		return Files.isRegularFile(pathWith(child));
	}

	public boolean isDir(String... child) {
		return Files.isDirectory(pathWith(child));
	}

	public boolean isFileType() {
		return EFT.FILE == type();
	}

	public boolean isDirType() {
		return EFT.DIR == type();
	}

	@Override
	public String toString() {
		return "FileOrDir{" + "fileOrDir='" + fileOrDir + '\'' + ", fileOrDirPath=" + fdPath + ", fileDirType=" + type(null) + '}';
	}

	public Path copyToDir(Path dst, boolean createDstDir, UFS.COPY.CopyOpt copyOpt) throws IOException {
		return UFS_BASE.COPY.copyToDir(path(), dst, createDstDir, copyOpt);
	}

	public Fd createIfNotExist() throws IOException {
		throw new UnsupportedOperationException("Use dir or file");
	}

	public InputStream toInputStream(InputStream... defRq) {
		return toInputStream(ARR.EMPTY_STR, defRq);
	}

	public InputStream toInputStream(String child, InputStream... defRq) {
		return toInputStream(child, defRq);
	}

	protected InputStream toInputStream(String[] child, InputStream... defRq) {
		return child.length == 0 ? UFD.toInputStream(path(), defRq) : UFD.toInputStream(path(), child[0], defRq);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Fd fd = (Fd) o;
		return Objects.equals(this.fileOrDir, fd.fileOrDir);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fileOrDir);
	}

	public static boolean checkExist(Fd fd, boolean... RETURN) {
		return checkExistOrNotExist(fd, true, RETURN);
	}

	public static boolean checkNotExist(Fd fd, boolean... RETURN) {
		return checkExistOrNotExist(fd, false, RETURN);
	}

	public static boolean checkExistOrNotExist(Fd fd, boolean checkExistNotExist, boolean... RETURN) {
		boolean exists;
		if (fd instanceof FILE) {
			exists = UFS.existFile(fd.path());
		} else if (fd instanceof DIR) {
			exists = UFS.existDir(fd.path());
		} else {
			throw new WhatIsTypeException(fd.getClass());
		}
		boolean cond = checkExistNotExist ? exists : !exists;
		if (checkExistNotExist == cond || ARG.isDefEqTrue(RETURN)) {
			return cond;
		}
		throw new IT.CheckException(fd.path().toString() + " " + (checkExistNotExist ? " not exist" : " exist"));
	}

	public static boolean checkTypeIsFile(Fd fd, boolean... RETURN) {
		return checkIs(fd, EFT.FILE, RETURN);
	}

	public static boolean checkTypeIsDir(Fd fd, boolean... RETURN) {
		return checkIs(fd, EFT.DIR, RETURN);
	}

	public static boolean checkIs(Fd fd, EFT eft, boolean... RETURN) {
		if (fd != null && fd.type() == IT.NN(eft)) {
			return true;
		} else if (ARG.isDefEqTrue(RETURN)) {
			return false;
		}
		throw new UnsupportedOperationException("Unsupported EFT '" + (fd == null ? null : fd.type()) + "'");
	}

}
