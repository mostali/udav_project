package mpc.fs;

import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpu.X;
import mpu.core.QDate;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class UUFS {
	//return asc by lastModified
	@SneakyThrows
	public static Map<Path, Long> toMapByAscCreated(Path dir) {
		Comparator<Path> pathComparator = (p1, p2) -> p1.toFile().lastModified() > p2.toFile().lastModified() ? 1 : -1;
		return Files.list(dir).sorted(pathComparator).collect(Collectors.toMap(p -> p, p -> p.toFile().lastModified(), (t1, t2) -> t1, LinkedHashMap::new));
	}

	@SneakyThrows
	public static Map<Path, String> toMapByAscCreatedText(Path dir) {
		Comparator<Path> pathComparator = (p1, p2) -> p1.toFile().lastModified() > p2.toFile().lastModified() ? 1 : -1;
		return Files.list(dir).sorted(pathComparator).collect(Collectors.toMap(p -> p, p -> QDate.of(p.toFile().lastModified()).f(QDate.F.MONO20NF), (t1, t2) -> t1, LinkedHashMap::new));
	}

	public static void convertCharset(Path fileIn, Path fileOut, Charset charsetIn, Charset charsetOut) throws IOException {
		String content = FileUtils.readFileToString(fileIn.toFile(), charsetIn);
		FileUtils.write(fileOut.toFile(), content, charsetOut);
	}

	public static void removeTwins(Map<Path, List<Path>> twins) {
		STREAM.flatMapToList(twins.values()).forEach(UFS.RM::removeFileQk);
	}

	public static Map<String, Path> toMapByFn(Collection<Path> pathes) {
		return pathes.stream().collect(Collectors.toMap(p -> p.getFileName().toString(), p -> p, (v1, v2) -> X.throwException("twins '%s' found", UF.fn(v1), UF.fn(v2)), LinkedHashMap::new));
	}
}
