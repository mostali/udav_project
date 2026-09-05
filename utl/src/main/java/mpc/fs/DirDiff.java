package mpc.fs;

import mpu.str.Rt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

public class DirDiff {

	// Пример использования
	public static void main(String[] args) {
		try {
//			Path dir1 = Path.of("/path/to/directory1");
//			Path dir2 = Path.of("/path/to/directory2");

			List<Path>[] result = compareDirectories(Paths.get("/home/dav/Документы/RC8.DEV2/universe-integration"), Paths.get("/home/dav/Документы/RC8.DEV/universe-integration"));

			Rt.buildReport(result[0], "Файлы только в dir1: ", UFS_BASE.L);
			Rt.buildReport(result[1], "Файлы только в dir2: ", UFS_BASE.L);
			Rt.buildReport(result[2], "Файлы с отличиями: ", UFS_BASE.L);
			Rt.buildReport(result[3], "Одинаковые файлы: ", UFS_BASE.L);

		} catch (IOException e) {
			System.err.println("Ошибка: " + e.getMessage());
		}
	}

	/**
	 * Сравнивает содержимое двух директорий и возвращает разницу.
	 *
	 * @param dir1 первая директория
	 * @param dir2 вторая директория
	 * @return массив List<Path>[], где:
	 * [0] - файлы только в dir1
	 * [1] - файлы только в dir2
	 * [2] - файлы, которые отличаются (разные дата/размер)
	 * [3] - одинаковые файлы
	 * @throws IOException если возникла ошибка при чтении директорий
	 */
	public static List<Path>[] compareDirectories(Path dir1, Path dir2) throws IOException {
		// Проверяем, что оба пути существуют и являются директориями
		if (!Files.exists(dir1) || !Files.isDirectory(dir1)) {
			throw new IllegalArgumentException("Path1 должен существовать и быть директорией: " + dir1);
		}
		if (!Files.exists(dir2) || !Files.isDirectory(dir2)) {
			throw new IllegalArgumentException("Path2 должен существовать и быть директорией: " + dir2);
		}

		// Собираем информацию о файлах в обеих директориях
		Map<String, FileInfo> filesInDir1 = collectFileInfo(dir1);
		Map<String, FileInfo> filesInDir2 = collectFileInfo(dir2);

		// Инициализируем результирующие списки
		List<Path> onlyInDir1 = new ArrayList<>();
		List<Path> onlyInDir2 = new ArrayList<>();
		List<Path> differentFiles = new ArrayList<>();
		List<Path> identicalFiles = new ArrayList<>();

		// Находим файлы только в dir1 и отличающиеся
		for (Map.Entry<String, FileInfo> entry : filesInDir1.entrySet()) {
			String fileName = entry.getKey();
			FileInfo info1 = entry.getValue();

			if (!filesInDir2.containsKey(fileName)) {
				onlyInDir1.add(info1.path);
			} else {
				FileInfo info2 = filesInDir2.get(fileName);
				if (info1.equals(info2)) {
					identicalFiles.add(info1.path);
				} else {
					differentFiles.add(info1.path);
				}
			}
		}

		// Находим файлы только в dir2
		for (Map.Entry<String, FileInfo> entry : filesInDir2.entrySet()) {
			String fileName = entry.getKey();
			if (!filesInDir1.containsKey(fileName)) {
				onlyInDir2.add(entry.getValue().path);
			}
		}

		// Возвращаем массив списков
		@SuppressWarnings("unchecked") List<Path>[] result = new List[]{onlyInDir1,    // [0] - только в dir1
				onlyInDir2,    // [1] - только в dir2
				differentFiles,// [2] - отличаются
				identicalFiles // [3] - одинаковые
		};

		return result;
	}

	private static Map<String, FileInfo> collectFileInfo(Path directory) throws IOException {
		Map<String, FileInfo> fileInfoMap = new HashMap<>();

		try (Stream<Path> walk = Files.walk(directory)) {
			walk.filter(p -> !p.equals(directory)) // Исключаем саму корневую директорию
					.forEach(path -> {
						try {
							BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

							// Пропускаем симлинки, спецфайлы и т.д., оставляем только файлы и папки
							if (!attrs.isRegularFile() && !attrs.isDirectory()) {
								return;
							}

							String relativePath = directory.relativize(path).toString();
							// Для директорий размер не имеет практического смысла для UI, ставим 0
							long size = attrs.isRegularFile() ? attrs.size() : 0L;

							fileInfoMap.put(relativePath, new FileInfo(path, relativePath, attrs.lastModifiedTime().toMillis(), size));
						} catch (IOException e) {
							System.err.println("Ошибка при чтении: " + path + " - " + e.getMessage());
						}
					});
		}

		return fileInfoMap;
	}
	/**
	 * Собирает информацию о всех файлах в директории.
	 */
//		private static Map<String, FileInfo> collectFileInfo(Path directory) throws IOException {
//			Map<String, FileInfo> fileInfoMap = new HashMap<>();
//
//			try (Stream<Path> walk = Files.walk(directory)) {
//				walk.filter(Files::isRegularFile).forEach(path -> {
//					try {
//						String relativePath = directory.relativize(path).toString();
//						BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
//						fileInfoMap.put(relativePath, new FileInfo(path, relativePath, attrs.lastModifiedTime().toMillis(), attrs.size()));
//					} catch (IOException e) {
//						System.err.println("Ошибка при чтении файла: " + path + " - " + e.getMessage());
//					}
//				});
//			}
//
//			return fileInfoMap;
//		}

	/**
	 * Вспомогательный класс для хранения информации о файле.
	 */
	private static class FileInfo {
		final Path path;
		final String relativePath;
		final long lastModified;
		final long size;

		FileInfo(Path path, String relativePath, long lastModified, long size) {
			this.path = path;
			this.relativePath = relativePath;
			this.lastModified = lastModified;
			this.size = size;
		}

		boolean withLastModified = false;

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			FileInfo that = (FileInfo) obj;
			if (withLastModified && lastModified != that.lastModified) {
				return false;
			}
			return size == that.size && Objects.equals(relativePath, that.relativePath);
		}

		@Override
		public int hashCode() {
			return withLastModified ? Objects.hash(relativePath, lastModified, size) : Objects.hash(relativePath, size);
		}
	}

}
