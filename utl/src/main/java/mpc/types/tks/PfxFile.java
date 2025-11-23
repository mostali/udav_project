package mpc.types.tks;

import mpc.fs.UF;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PfxFile {

	public static Path toPath(String file) {
		return Paths.get(file.substring(UF.PFX_FILE.length()));
	}

	public static boolean has(String str) {
		return str.startsWith(UF.PFX_FILE);
	}
}
