package mpc.fs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FilenameFilterByExt implements FilenameFilter {
	final List<String> exts;
	final boolean isFile;
	final List<String> exceptDirs;

	public FilenameFilterByExt(List<String> exts) {
		this(exts, true);
	}

	public FilenameFilterByExt(List<String> exts, boolean isFile) {
		this(null, exts, true);

	}

	public FilenameFilterByExt(List<String> exceptDirs, List<String> exts, boolean isFile) {
		this.exts = exts;
		this.isFile = isFile;
		this.exceptDirs = exceptDirs == null ? new ArrayList<>() : exceptDirs;
	}

	public static FilenameFilter build(String... fileExts) {
		return (dir, name) -> {
			for (String fileExt : fileExts) {
				if (name.endsWith(fileExt)) {
					return true;
				}
			}
			return false;
		};
	}

	@Override
	public boolean accept(File dir, String name) {
		File f = new File(dir, name);
		for (String edir : exceptDirs) {
			if (dir.getName().equals(edir)) {
				return false;
			}
		}
		if (isFile && !f.isFile()) {
			return false;
		} else if (!isFile && !f.isDirectory()) {
			return false;
		}

		for (String e : exts) {
			if (name.endsWith(e)) {
				return true;
			}
		}
		return false;
	}

}
