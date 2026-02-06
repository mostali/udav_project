package mpc.fs.path;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathEntity implements Serializable, IPath {
	private final String file;
	private transient Path path;

	public static PathEntity of(Path path) {
		return new PathEntity(path);
	}

	public Path toPath() {
		return path == null ? (path = Paths.get(file)) : path;
	}

	public PathEntity(Path file) {
		this.file = file.toString();
		this.path = file;
	}
	public PathEntity(String file) {
		this.file = file;
	}
}

