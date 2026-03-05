package mpc.fs.ext;

import mpc.exception.RequiredRuntimeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.rfl.RFL;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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

	public boolean hasInDir(Path dir, boolean... checkFilesOrDir) {
		boolean b = ARG.isDefEqTrue(checkFilesOrDir);
		return UFS.ls(dir, p -> (b ? Files.isDirectory(p) : Files.isRegularFile(p)) && has(p)).stream().findAny().isPresent();
	}

	public boolean has(Path path) {
		return path == null ? false : has(EXT.of(path));
	}

	public boolean has(String ext) {
		return X.empty(ext) ? false : has(EXT.of(ext));
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
		return ARG.throwErr(() -> new RequiredRuntimeException("File Group not found from ext '%s'", ext), defRq);
	}

	public List<Path> ls(Path path, List<Path>... defRq) {
		return Arrays.stream(group).map(e -> e.lsAll(path, defRq)).flatMap(List::stream).collect(Collectors.toList());
	}
}
