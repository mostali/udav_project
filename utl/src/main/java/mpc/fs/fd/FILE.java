package mpc.fs.fd;

import lombok.SneakyThrows;
import mpu.core.RW;
import mpu.core.ARG;
import mpu.core.ARGn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class FILE extends Fd {

	public FILE(String filename) {
		super(filename, EFT.FILE);
	}

	public FILE(Path path) {
		super(path, EFT.FILE);
	}

	public static long getSecondsAgoModified(Path file) {
		long secondsAgoModify = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - file.toFile().lastModified());
		return secondsAgoModify;
	}

	@Override
	public boolean fdExist(String... child) {
		return Files.isRegularFile(fPathWith(child));
	}

	public static FILE of(String file) {
		return new FILE(file);
	}

	public static FILE of(Path path) {
		return new FILE(path);
	}

	public FILE appendLine(String line) throws IOException {
		RW.write_AppendOrCreateNew_(toPath(), line);
		return this;
	}

	public FILE writeContent(String content, Boolean mkdirs_mkdir_orNot, OpenOption... openOptions) throws IOException {
		RW.write_(toPath(), content, mkdirs_mkdir_orNot, openOptions);
		return this;
	}

	public String readLine(int... index) throws IOException {
		int ind = ARGn.toDefOr(0, index);
		return RW.readLine(toPath(), ind);
	}

	@SneakyThrows
	public String[] readRunArgs(String[]... defRq) {
		String content = ARG.isDef(defRq) ? readContent_(null) : readContent_();
		if (content != null) {
			return content.replace("\n", " ").split("\\s++");
		}
		return ARG.toDef(defRq);
	}

	public String readContent_(String... defRq) throws IOException {
		return RW.readContent_(toPath(), null, defRq);
	}

	public Properties readProperties() throws IOException {
		return RW.readProperties_(toPath());
	}

	public List<String> readLines() throws IOException {
		return RW.readLines_(toPath());
	}

	public <T> T readAs(Class<T> cnt, T... defRq) throws IOException {
		return RW.readAs(toPath(), cnt, defRq);
	}

	public FILE createIfNotExist() throws IOException {
		if (!Files.exists(toPath())) {
			Files.createFile(toPath());
		}
		return this;
	}

	public DIR parentDIR() {
		return DIR.of(parent());
	}

	public Path parent() {
		return toPath().getParent();
	}
}
