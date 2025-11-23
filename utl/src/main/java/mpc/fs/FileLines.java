package mpc.fs;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import mpu.str.STR;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import mpu.core.RW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileLines {
	public static final Logger L = LoggerFactory.getLogger(FileLines.class);

	private final Path file;

	public FileLines(Path file) {
		this.file = file;
		exist = Files.isRegularFile(file);
	}

	public Path path() {
		return file;
	}

	@Getter
	@Setter
	private boolean exist;

	public void removeMe() throws IOException {
		UFS.RM.deleteDir(file);
		if (L.isInfoEnabled()) {
			L.info("File-index '{}' removed", file);
		}
	}

	public static FileLines of(Path pathFile) {
		return new FileLines(pathFile);
	}

	public boolean containLine(String line, Boolean... defRq) {
		try {
			return containLine_(line);
		} catch (Exception ex) {
			return ARG.toDefRq(defRq);
		}
	}

	public boolean containLine_(String line) throws IOException {
		return getLines_().contains(IT.NN(line));
	}

	@SneakyThrows
	public void appendLineIfNotExist(String line) {
		if (!containLine(line)) {
			RW.write_Append_(file, IT.NN(line));
		}
	}

	@SneakyThrows
	public void appendLine(String line) {
		RW.write_Append_(file, STR.NL + IT.NN(line));
	}

	public void appendLine_(String line) throws IOException {
		RW.write_Append_(file, STR.NL + IT.NN(line));
	}

	public List<String> getLines(List<String>... defRq) {
		try {
			return getLines_();
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public List<String> getLines_() throws IOException {
		return RW.readLines_(file);
	}


	public Set<String> getIndex(Set<String>... defRq) {
		try {
			return getIndex_();
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public Set<String> getIndex_() throws IOException {
		return new LinkedHashSet(getLines_());
	}

	@SneakyThrows
	public FileLines writeIndex(Collection<String> set) {
		writeIndex_(set);
		return this;
	}

	public FileLines writeIndex_(Collection<String> set) throws IOException {
		RW.write_(file, set);
		if (L.isInfoEnabled()) {
			L.info("Write file-index({}), '{}'", X.sizeOf(set), UF.ln(file));
		}
		return this;
	}

	public FileLines createFile() throws IOException {
		UFS.MKFILE.createFileIfNotExist_(file);
		return this;
	}

	public static final int TOSTRING_CASE = 2;

	@Override
	public String toString() {
		switch (TOSTRING_CASE) {
			case 0:
				return "FileLines{" +
						"file=" + file +
						'}';
			case 1:
			case 2:
			default:
				List<String> lines = getLines(null);
				return X.f("FileLines(%s):%s\n%s", X.sizeOf(lines), file, lines);
		}

	}

	public void addToIndex_(Collection<String> indexAppend) throws IOException {
		Collection<String> index = getIndex(null);
		if (index == null) {
			index = indexAppend;
		} else {
			index.addAll(indexAppend);
		}
		writeIndex_(index);
	}


	@SneakyThrows
	public void clearAndWrite() {
		writeIndex_(Collections.EMPTY_LIST);
	}
}
