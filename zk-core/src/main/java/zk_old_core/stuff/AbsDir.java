package zk_old_core.stuff;

import lombok.Getter;
import mpc.fs.UDIR;
import mpc.fs.fd.EFT;
import mpc.fs.fd.UFD;

import java.nio.file.Path;
import java.util.List;

@Deprecated
public class AbsDir {

	public static final String COMMON = "common";
	@Getter
	private final Path dir;

	public AbsDir(Path dir) {
		this.dir = dir;
	}

	public static AbsDir of(Path dir) {
		return new AbsDir(dir);
	}

	public List<Path> getPaths(EFT fileType, Boolean isAscOrDescOrNull, String... child) {
		Path path = UFD.pathWith(getDir(), child);
		return UDIR.lsNativeSort(path, fileType, isAscOrDescOrNull);
	}

	@Override
	public String toString() {
		return "AbsDir{" +
			   "dir=" + dir +
			   '}';
	}

}
