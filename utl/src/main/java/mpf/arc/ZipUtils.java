package mpf.arc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ZipUtils {
	static void createJar(final URI jarURI, final File workDir, final File f) throws Exception {
		final Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		final URI uri = URI.create("jar:file://" + jarURI.getRawPath());
		try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
			final Path path = zipfs.getPath(workDir.toPath().relativize(f.toPath()).toString());
			if (path.getParent() != null) {
				Files.createDirectories(path.getParent());
			}
			Files.copy(f.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private static void updateZip(File zip, File entry, String entryName) throws IOException {
		Map<String, String> env = new HashMap<>();
		String uriPath = "jar:" + zip.toURI().toString();
		URI uri = URI.create(uriPath);
		try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
			fs.provider().checkAccess(fs.getPath(entryName), AccessMode.READ);
			Path target = fs.getPath(entryName);
			Path source = entry.toPath();
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw e;
		}
	}

	public static void copyResources(File dir) throws URISyntaxException, IOException {

		Path target = Paths.get(dir.toURI());
		try (FileSystem zipFs = FileSystems.newFileSystem(Paths.get("/stand.repo").toUri(), new HashMap<>());) {

			Path pathInZip = zipFs.getPath("/resources");

			Files.walkFileTree(pathInZip, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
					// Make sure that we conserve the hierachy of files and folders
					// inside the zip
					Path relativePathInZip = pathInZip.relativize(filePath);
					Path targetPath = target.resolve(relativePathInZip.toString());
					Files.createDirectories(targetPath.getParent());

					// And extract the file
					Files.copy(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

					return FileVisitResult.CONTINUE;
				}
			});
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

//	public static List<Path> getDirContent(Class clazz, String path) throws URISyntaxException, IOException {
//		List<Path> result = new ArrayList<>();
//		URL jar = clazz.getProtectionDomain().getCodeSource().getLocation();
//		Path jarFile = Paths.get(jar.toURI());
//		try (FileSystem fs = FileSystems.newFileSystem(jarFile, null); DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(path))) {
//			for (Path p : directoryStream) {
//				result.add(p);
//			}
//			result.sort((o1, o2) -> o1 == null || o2 == null ? 0 : o1.getFileName().toString().compareTo(o2.getFileName().toString()));
//			return result;
//		}
//	}

	static List jarFileSystems = new ArrayList();

	public static Path toPath(URL resource) throws IOException, URISyntaxException {
		if (resource == null) {
			return null;
		}

		final String protocol = resource.getProtocol();
		if ("file".equals(protocol)) {
			return Paths.get(resource.toURI());
		} else if ("jar".equals(protocol)) {
			final String s = resource.toString();
			final int separator = s.indexOf("!/");
			final String entryName = s.substring(separator + 2);
			final URI fileURI = URI.create(s.substring(0, separator));

			final FileSystem fileSystem;
			synchronized (jarFileSystems) {
				if (jarFileSystems.add(fileURI)) {
					fileSystem = FileSystems.newFileSystem(fileURI, Collections.<String, Object>emptyMap());
				} else {
					fileSystem = FileSystems.getFileSystem(fileURI);
				}
			}
			return fileSystem.getPath(entryName);
		} else {
			throw new IOException("Can't read " + resource + ", unknown protocol '" + protocol + "'");
		}
	}

}

