package mpe.tpl_engine;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceUtils {

	public static void main(String[] args) {
		// Копируем папку "my_templates" из src/main/resources/my_templates
		// во внешнюю папку /opt/app/external_templates
		ResourceUtils.copyResourceDirectory("my_templates", Paths.get("/opt/app/external_templates"));
		System.out.println("Копирование успешно завершено!");
	}

	/**
	 * Копирует директорию из ресурсов приложения (classpath) во внешнюю директорию на файловой системе.
	 *
	 * @param resourcePath путь к директории в ресурсах (например, "static/templates" или "/config")
	 * @param destDirPath  абсолютный или относительный путь к внешней директории на диске
	 * @throws IOException если произошла ошибка ввода-вывода или ресурс не найден
	 */
	@SneakyThrows
	public static void copyResourceDirectory(String resourcePath, Path destDir) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		// Убираем ведущий слэш, если он есть, чтобы ClassLoader.getResource работал корректно
		if (resourcePath.startsWith("/")) {
			resourcePath = resourcePath.substring(1);
		}

		URL url = classLoader.getResource(resourcePath);
		if (url == null) {
			throw new IllegalArgumentException("Ресурс не найден в classpath: " + resourcePath);
		}

		Files.createDirectories(destDir);

		String protocol = url.getProtocol();

		if ("file".equals(protocol)) {
			// Вариант 1: Приложение запущено из IDE или распакованного WAR (ресурсы на диске)
			try {
				copyFromFileSystem(Paths.get(url.toURI()), destDir);
			} catch (URISyntaxException e) {
				throw new IOException("Неверный синтаксис URI для файлового ресурса", e);
			}
		} else if ("jar".equals(protocol)) {
			// Вариант 2: Приложение запущено из упакованного WAR/JAR
			copyFromJar(url, resourcePath, destDir);
		} else {
			throw new UnsupportedOperationException("Неподдерживаемый протокол доступа к ресурсам: " + protocol);
		}
	}

	/**
	 * Копирование из обычной файловой системы (рекурсивно).
	 */
	private static void copyFromFileSystem(Path sourceDir, Path destDir) throws IOException {
		Files.walk(sourceDir).forEach(source -> {
			try {
				Path dest = destDir.resolve(sourceDir.relativize(source).toString());
				if (Files.isDirectory(source)) {
					Files.createDirectories(dest);
				} else {
					Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (IOException e) {
				throw new RuntimeException("Ошибка при копировании файла: " + source, e);
			}
		});
	}

	/**
	 * Копирование изнутри JAR/WAR архива.
	 */
	private static void copyFromJar(URL url, String resourcePath, Path destDir) throws IOException {
		JarURLConnection jarConnection = (JarURLConnection) url.openConnection();

		try (JarFile jarFile = jarConnection.getJarFile()) {
			Enumeration<JarEntry> entries = jarFile.entries();

			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();

				// Проверяем, что запись находится внутри нужной директории ресурсов
				if (entryName.startsWith(resourcePath + "/") || entryName.equals(resourcePath)) {

					// Вычисляем относительный путь внутри целевой директории
					String relativePath = entryName.substring(resourcePath.length());
					if (relativePath.startsWith("/")) {
						relativePath = relativePath.substring(1);
					}

					// Пропускаем саму корневую запись директории ресурса
					if (relativePath.isEmpty()) {
						continue;
					}

					Path destPath = destDir.resolve(relativePath);

					if (entry.isDirectory()) {
						Files.createDirectories(destPath);
					} else {
						if (destPath.getParent() != null) {
							Files.createDirectories(destPath.getParent());
						}
						try (InputStream is = jarFile.getInputStream(entry)) {
							Files.copy(is, destPath, StandardCopyOption.REPLACE_EXISTING);
						}
					}
				}
			}
		}
	}
}