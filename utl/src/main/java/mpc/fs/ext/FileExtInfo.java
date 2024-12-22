package mpc.fs.ext;

import mpc.types.tks.TokenInfo;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileExtInfo extends TokenInfo {

	public final String filename;
	public final String path;

	public Path path() {
		return Paths.get(path);
	}

	public FileExtInfo(Path file) {
		this(file, ".");
	}

	public FileExtInfo(Path file, String del) {
		super(file.getFileName().toString(), ".");
		this.path = file.toString();
		this.filename = file.getFileName().toString();
	}

	public static FileExtInfo of(Path file) {
		return new FileExtInfo(file);
	}
}
