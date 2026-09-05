package mpc.fs;

import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.EFT;
import mpu.core.ARG;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public enum LS_SORT {
	NOT, NATIVE, NATURAL, NATIVE_DESC, NATURAL_DESC, NATURAL_IGNORE_CASE, NATURAL_DESC_IGNORE_CASE;

	public List<Path> ls(Path path, EFT fileType, List... defRq) {
		return ls(path, fileType, null, defRq);
	}

	public List<Path> ls(Path path, EFT fileType, Predicate<Path> filter, List... defRq) {
		try {
			return ls_(path, fileType, filter);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(ex);
		}
	}

	public List<Path> ls_(Path path, EFT fileType) throws IOException {
		return ls_(path, fileType, null);
	}

	public List<Path> ls_(Path path, EFT fileType, Predicate<Path> filter) throws IOException {
		switch (this) {
			case NOT:
				return UDIR.ls_(path, fileType, null, null, null, filter);
			case NATIVE:
				return UDIR.ls_(path, fileType, true, null, null, filter);
			case NATIVE_DESC:
				return UDIR.ls_(path, fileType, true, true, null, filter);
			case NATURAL:
				return UDIR.ls_(path, fileType, false, null, true, filter);
			case NATURAL_DESC:
				return UDIR.ls_(path, fileType, false, true, true, filter);
			case NATURAL_IGNORE_CASE:
				return UDIR.ls_(path, fileType, false, null, false, filter);
			case NATURAL_DESC_IGNORE_CASE:
				return UDIR.ls_(path, fileType, false, true, false, filter);
			default:
				throw new WhatIsTypeException(this);
		}
	}
}
