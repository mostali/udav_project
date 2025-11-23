package mpc.fs.fd;

import mpc.exception.WhatIsTypeException;
import lombok.Getter;
import mpc.types.opts.SeqOptions;
import mpc.types.ruprops.RuProps;
import mpu.core.QDate;
import mpu.core.RW;
import mpu.core.ARR;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import mpc.fs.UDIR;
import mpu.str.SPLIT;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpc.fs.UFS;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

public class Fd implements IFd {
	public static final Logger L = LoggerFactory.getLogger(Fd.class);

	@Getter
	private final String fileOrDir;

	private transient Path fdPath;

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

	public String fDir() {
		switch (fType()) {
			case FILE:
				return toPath().getParent().toString();
			case DIR:
				return toPath().toString();
		}
		throw new WhatIsTypeException(fType());
	}

	public QDate fCreated() {
		return QDate.of(toPath().toFile().lastModified());
	}

	public EFT fType(EFT... defRq) {
		return fileType != null ? fileType : ARG.toDefRq(defRq);
	}

	public Path toPath() {
		return fdPath != null ? fdPath : (fdPath = Paths.get(fileOrDir));
	}

	public String fName() {
		return toPath().getFileName().toString();
	}

	public boolean fdExist(String... child) {
		return Files.exists(fPathWith(child));
	}

	public boolean fEmpty(String... child) {
		Path entry = fPathWith(child);
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

	public boolean fDeleteQk(String... child) {
		try {
			fDelete(child);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public String fDelete(String... child) throws IOException {
		Path entry = fPathWith(child);
		FileUtils.forceDelete(entry.toFile());
//		Files.delete(entry);
		return entry.toString();
	}


	public boolean isFile(String... child) {
		return Files.isRegularFile(fPathWith(child));
	}

	public boolean isDir(String... child) {
		return Files.isDirectory(fPathWith(child));
	}

	@Override
	public String toString() {
		return "FileOrDir{" + "fileOrDir='" + fileOrDir + '\'' + ", asPath=" + fdPath + ", fileDirType=" + fType(null) + '}';
	}

	public Path copyToDir(Path dst, boolean createDstDir, UFS.COPY.CopyOpt copyOpt) throws IOException {
		return UFS.COPY.copyToDir(toPath(), dst, createDstDir, copyOpt);
	}

	public Fd createIfNotExist() throws IOException {
		throw new UnsupportedOperationException("Use dir or file");
	}

	public InputStream toInputStream(InputStream... defRq) {
		return toInputStream(ARR.EMPTY_ARGS, defRq);
	}

	public InputStream toInputStream(String child, InputStream... defRq) {
		return toInputStream(child, defRq);
	}

	protected InputStream toInputStream(String[] child, InputStream... defRq) {
		return child.length == 0 ? UFD.toInputStream(toPath(), defRq) : UFD.toInputStream(toPath(), child[0], defRq);
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
			exists = UFS.existFile(fd.toPath());
		} else if (fd instanceof DIR) {
			exists = UFS.existDir(fd.toPath());
		} else {
			throw new WhatIsTypeException(fd.getClass());
		}
		boolean cond = checkExistNotExist ? exists : !exists;
		if (checkExistNotExist == cond || ARG.isDefEqTrue(RETURN)) {
			return cond;
		}
		throw new IT.CheckException(fd.toPath().toString() + " " + (checkExistNotExist ? " not exist" : " exist"));
	}

	public String cat_(String... defRq) throws IOException {
		return cat_(toPath(), defRq);
	}

	public String catWith_(String child) throws IOException {
		return cat_(fPathWith(child));
	}

	public static String cat_(Path path, String... defRq) throws IOException {
		if (UFS.existFile(path)) {
			return RW.readContent_(path, Charset.defaultCharset());
		}
		return ARG.toDefRq(defRq);
	}

	public String cat(String... defRq) {
		try {
			return IOUtils.toString(toInputStream(), Charset.defaultCharset());
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public SeqOptions toSeqOpt(SeqOptions... defRq) {
		IT.state(fType() == EFT.FILE, "except FILE resource for convert to SeqOptions '%s' ", this);
		String cat = cat(null);
		if (cat != null) {
			return SeqOptions.of(SPLIT.argsBySpace(cat));
		}
		return ARG.toDefThrowMsg(() -> X.f("Content not found from '%s'", this), defRq);
	}

	public Map<String, String> toMap(Map<String, String>... defRq) {
		String cat = cat(null);
		if (cat != null) {
			return RuProps.of(cat).toMap();
		}
		return ARG.toDefThrowMsg(() -> X.f("Content not found from '%s'", this), defRq);
	}

	public static Fd ofAsFd(String file) {
		return new Fd(file);
	}

	public static Fd of(String path, EFT... asType) {
		return of(Paths.get(path), asType);
	}

	public static Fd of(Path path, EFT... asType) {
		EFT targetEft = ARG.toDefOr(null, asType);
		if (targetEft == null) {
			targetEft = EFT.of(path, asType);
		}
		switch (IT.NN(targetEft, "File not exist or set EFT manually")) {
			case FILE:
				return FILE.of(path);
			case DIR:
				return DIR.of(path);
			default:
				throw new WhatIsTypeException(targetEft);
		}
	}

	public <T extends IFd> T resolve(String file, EFT... asType) {
		return (T) of(toPath().resolve(file), asType);
	}
}
