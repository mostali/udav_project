package mpc.fs.fd;

import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpe.ftp.FtpFile;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.QDate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface IFd extends Comparable {

	default String fDir() {
		switch (fType()) {
			case FILE:
				return toPath().getParent().toString();
			case DIR:
				return toPath().toString();
			default:
				throw new WhatIsTypeException(fType());
		}
	}

	default String fName() {
		return toPath().getFileName().toString();
	}

	default Path fParent() {
		return toPath().getParent();
	}

	default EFT fType(EFT... defRq) {
		return EFT.of(toPath(), defRq);
	}

	default EXT fExt() {
		return EXT.of(toPath());
	}

	default GEXT fGext(GEXT... defRq) {
		return GEXT.of(fExt(), defRq);
	}

	default QDate fCreated() {
		return QDate.of(toPath().toFile().lastModified());
	}

	default boolean fdExist(String... child) {
		return Files.exists(fPathWith(child));
	}

	default Path fPathWith(String... child) {
		if (!ARG.isDef(child)) {
			return toPath();
		} else if (fType() == EFT.FILE) {
			throw UFD.newExceptionFileWithChild(toPath(), child);
		}
		return UFD.pathWith(toPath(), fType(), child);
	}

	default String toStringWithDate() {
		return fType() + ":" + fDir() + "/" + fName() + "[" + fCreated() + "]";
	}

	default FILE toFILE() {
		if (this instanceof FILE) {
			return (FILE) this;
		}
		switch (IT.NN(fType())) {
			case FILE:
				return FILE.of(toPath());
			default:
				throw new WhatIsTypeException(fType());
		}
	}

	default DIR toDIR() {
		if (this instanceof DIR) {
			return (DIR) this;
		}
		switch (IT.NN(fType())) {
			case DIR:
				return DIR.of(fDir());
			default:
				throw new WhatIsTypeException(fType());
		}
	}

	default Path toPath() {
		switch (fType()) {
			case FILE:
				return Paths.get(fDir()).resolve(fName());
			case DIR:
				return Paths.get(fDir());
			default:
				throw new WhatIsTypeException(fType());
		}
	}

	default File toFile() {
		return toPath().toFile();
	}

	default int compareTo(Object o) {
		if (o == this) {
			return 0;
		} else if (o == null || !IFd.class.isAssignableFrom(o.getClass())) {
			return -1;
		}
		IFd o2 = (IFd) o;
		if (o2.fCreated().equals(fCreated())) {
			return o2.toPath().compareTo(toPath());
		}
		return o2.fCreated().compareTo(fCreated());
	}

}
