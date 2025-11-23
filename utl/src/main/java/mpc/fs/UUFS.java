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
import java.nio.file.attribute.BasicFileAttributes;
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
		STREAM.flatMapToList(twins.values()).forEach(UFS.RM::fileQk);
	}

	public static Map<String, Path> toMapByFn(Collection<Path> pathes) {
		return pathes.stream().collect(Collectors.toMap(p -> p.getFileName().toString(), p -> p, (v1, v2) -> X.throwException("twins '%s' found", UF.fn(v1), UF.fn(v2)), LinkedHashMap::new));
	}

	@SneakyThrows
	public static Date[] getMinMaxFileModificationDatesNIO(Path directoryPath) {

		if (!Files.exists(directoryPath)) {
			throw new IllegalArgumentException("Директория не существует: " + directoryPath);
		}

		if (!Files.isDirectory(directoryPath)) {
			throw new IllegalArgumentException("Указанный путь не является директорией: " + directoryPath);
		}

		final Long[] minMaxTime = {Long.MAX_VALUE, Long.MIN_VALUE};
//		final Long[] maxTime = {Long.MIN_VALUE};

		Files.walk(directoryPath)
				.filter(Files::isRegularFile)
				.forEach(path -> {
					try {
						BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
						long lastModified = attrs.lastModifiedTime().toMillis();

						if (lastModified < minMaxTime[0]) {
							minMaxTime[0] = lastModified;
						}
						if (lastModified > minMaxTime[1]) {
							minMaxTime[1] = lastModified;
						}
					} catch (IOException e) {
						// Игнорируем файлы, к которым нет доступа
						System.err.println("Не удалось получить атрибуты файла: " + path + " - " + e.getMessage());
					}
				});

//		if (minMaxTime[0] == Long.MAX_VALUE || minMaxTime[1] == Long.MIN_VALUE) {
//			throw new IOException("В директории не найдено файлов или невозможно получить доступ к файлам");
//		}

		Date minModifyDate = minMaxTime[0] == null ? null : new Date(minMaxTime[0]);
		Date maxModifyDate = minMaxTime[1] == null ? null : new Date(minMaxTime[1]);
		return new Date[]{minModifyDate, maxModifyDate};
	}

	public static void rotateToHistory(Path fileToRotateClone, boolean skipChange) {
		QDate now = QDate.now();
		QDate plusDay = now.addDays(1);
		Path nextPath = UF.cloneParent(fileToRotateClone, ".day" + now.day);
		Path plusDayPath = UF.cloneParent(fileToRotateClone, ".day" + plusDay);
		UFS.RM.fileQk(plusDayPath);

		if (!skipChange) {
			UFS.RM.fileQk(nextPath);
			UFS.MV.move(fileToRotateClone, nextPath, false);
		}
	}
}
