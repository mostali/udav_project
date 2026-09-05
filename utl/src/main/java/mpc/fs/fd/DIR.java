package mpc.fs.fd;

import mpc.fs.UFS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class DIR extends Fd {

	public DIR(String dir) {
		super(dir, EFT.DIR);
	}

	public DIR(Path path) {
		super(path, EFT.DIR);
	}

	public static List<IFd> sort(List<IFd> list, EFT type, boolean isAscDesc) {
		int rslt = isAscDesc ? 1 : -1;
		return list.stream().filter( //
				f -> type == null ? true : f.fType(null) == type //
		).sorted( //
				(f1, f2) -> {
//					boolean isAfter = f1.fCreated().isAfter(f2.fCreated());
					return f1.compareTo(f2) > 0 ? rslt : -rslt;
				} //
		).collect(Collectors.toList());
	}

	@Override
	public boolean fdExist(String... child) {
		return Files.isDirectory(fPathWith(child));
	}

	public boolean existChildFile(String child) {
		return Files.isRegularFile(toPath().resolve(child));
	}

	public static DIR of(String dir) {
		return new DIR(dir);
	}

	public static DIR of(Path path) {
		return new DIR(path);
	}

	public DIR createIfNotExist() throws IOException {
		UFS.MKDIR.createDirs_(toPath().toFile(), true, false);
		return this;
	}

}
