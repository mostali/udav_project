package mpc.fs.ext;

import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.rfl.RFL;
import mpu.core.ARG;
import mpu.core.ARR;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public enum GEXT {
	RUNNABLE, EDITABLE, AUDIO, IMG, GIF, VIDEO, DOC, ARC, BINARY;
	final EXT[] group;

	GEXT() {
		this.group = name().equals("UNDEFINED") ? new EXT[0] : (EXT[]) RFL.fieldValueSt(EXT.class, "G_" + name(), false);
	}

	public static GEXT getTypeFromUrl(String url) {
		String ext = UF.getExtFromDirtyUrl(url);
		return GEXT.of(EXT.ofExt(ext));
	}

	public boolean isNotPath(Path path) {
		return !isPath(path);
	}

	public boolean isPath(Path path) {
		return has(EXT.of(path));
	}

	public boolean has(String ext) {
		return has(EXT.ofExt(ext));
	}

	public boolean has(EXT ext) {
		return ARR.contains(group, ext);
	}

	public static GEXT of(String path, GEXT... defRq) {
		return of(EXT.of(path), defRq);
	}

	public static GEXT of(Path file, GEXT... defRq) {
		return of(EXT.of(file), defRq);
	}

	public static GEXT of(EXT ext, GEXT... defRq) {
		if (ext != null) {
			for (GEXT gExt : GEXT.values()) {
				if (gExt.has(ext)) {
					return gExt;
				}
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("File Group not found from ext '%s'", ext), defRq);
	}

	public List<Path> ls_filter(List<Path> ls) {
		return ls.stream().filter(this::isPath).collect(Collectors.toList());
	}

}
