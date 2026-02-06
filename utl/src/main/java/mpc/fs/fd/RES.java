package mpc.fs.fd;

import lombok.SneakyThrows;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.FIllegalStateException;
import mpc.exception.StackTraceRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.core.ARG;
import mpu.IT;
import mpc.fs.UF;
import mpc.fs.UFS;
import org.apache.commons.io.IOUtils;
import mpc.fs.UPackageResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class RES extends Fd {
	public final Class clazz;
	public final boolean isFileOrDir;

	public RES(Class clazz) {
		this(clazz, ".", false);
	}

	public RES(Class clazz, String fileOrDir, boolean isFileOrDir) {
		super(fileOrDir);
		this.clazz = clazz;
		this.isFileOrDir = isFileOrDir;
	}

	public RES(Class clazz, Path path, boolean isFileOrDir) {
		super(path);
		this.clazz = clazz;
		this.isFileOrDir = isFileOrDir;
	}

	public static String loadFileFromRunLocationOrResources(Class resClass, String file, boolean copy, String... defRq) {
		try {
			FILE f = FILE.of(file);
			if (f.fdExist()) {
				return f.readAs(String.class);
			}
			RES res = of(resClass, "/" + UF.normFileStartRootAndRel(file), true);
			if (copy) {
				res.copyToRunLocation_(UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST);
				return f.readAs(String.class);
			}
			return res.cat();
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static String readString(Class rsrc, String file, String... defRq) {
		try {
			return RES.of(rsrc, file).cat_(defRq);
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static String readString(String file, String... defRq) {
		try {
			return RES.of(file).cat_(defRq);
		} catch (Exception e) {
			return ARG.toDefThrow(e, defRq);
		}
	}


	@Override
	@SneakyThrows
	public String cat_(String... defRq) throws IOException {
//		if (exist()) { //TODO check why not work for RES
		try {
			return IOUtils.toString(toInputStream(), Charset.defaultCharset());
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> ex, defRq);
		}
	}

	@Override
	public String catWith_(String child) throws IOException {
		return IOUtils.toString(toInputStream(child), Charset.defaultCharset());
	}

	public static Properties readProperties(Class resClass, String filename) throws IOException {
		Properties props = new Properties();
		props.load(resClass.getResourceAsStream(filename));
		return props;
	}

	@Override
	public String toString() {
		return "Res{" +
				"clazz=" + clazz +
				", isJar=" + isJarResourceQk() +
				", fileOrDir=" + super.toString() +
				'}';
	}

	public static RES of(Class clazz, String fileOrDir, EFT type) {
		return new RES(clazz, fileOrDir, IT.notNull(type) == EFT.FILE);
	}

	public static RES of(Class clazz, String fileOrDir, Class<? extends Fd> fileOrDirClass) {
		return new RES(clazz, fileOrDir, isFILEorDIR(fileOrDirClass));
	}

	private static boolean isFILEorDIR(Class<? extends Fd> fileOrDirClass) {
		if (FILE.class == fileOrDirClass) {
			return true;
		} else if (DIR.class == fileOrDirClass) {
			return false;
		}
		throw new WhatIsTypeException("What is FILE/DIR type? " + fileOrDirClass);
	}

	public static RES of(Class clazz, Path fileOrDir, EFT type) {
		return new RES(clazz, fileOrDir, IT.notNull(type) == EFT.FILE);
	}

	@Deprecated
	public static RES of(String fileOrDir) {
		return of(RES.class, fileOrDir);
	}

	public static RES ofRootLocal(String fileOrDir) {
		return ofRoot(RES.class, fileOrDir);
	}

	public static RES ofRoot(Class from, String fileOrDir) {
		return new RES(from, "/" + UF.normFileStart(fileOrDir), UF.isFileOrDirNameByEnd(fileOrDir));
	}

	public static RES of(Class clazz, String fileOrDir) {
		return new RES(clazz, fileOrDir, UF.isFileOrDirNameByEnd(fileOrDir));
	}

	public static RES of(Class clazz, String fileOrDir, boolean isFileOrDir) {
		return new RES(clazz, fileOrDir, isFileOrDir);
	}

	public static RES of(Class clazz, Path path, boolean isFileOrDir) {
		return new RES(clazz, path, isFileOrDir);
	}

	public Boolean isJarResourceQk() {
		try {
			return isJarResource();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isJarResource() throws URISyntaxException {
		URL url = toUrl();
		if (url == null) {
			throw new FIllegalArgumentException("isJarResource:Resource [%s] from class [%s] not found", super.getFileOrDir(), clazz);
		}
		final URI uri = url.toURI();
		return "jar".equals(uri.getScheme());
	}

	private URL url_ = null;

	public URL toUrl() {
		if (url_ != null) {
			return url_;
		}
		URL url = clazz.getResource(super.getFileOrDir());
		if (url == null) {
			throw new FIllegalArgumentException("toUrl:Resource [%s] from class [%s] not found", super.getFileOrDir(), clazz);
		}
		return url_ = url;
	}

	private Path resourcePath_ = null;

	public Path toResourcePath() throws URISyntaxException, IOException {
		if (resourcePath_ != null) {
			return resourcePath_;
		}
		if (isJarResource()) {
			final FileSystem fileSystem = FileSystems.newFileSystem(toUrl().toURI(), Collections.emptyMap(), null);
			return resourcePath_ = fileSystem.getPath(getFileOrDir());
		} else {
			return resourcePath_ = Paths.get(toUrl().toURI());
		}
	}

	@Override
	protected InputStream toInputStream(String[] child, InputStream... defRq) {
		boolean hasChild = ARG.isDef(child);
		switch (fType()) {
			case DIR:
				if (hasChild) {
					return clazz.getResourceAsStream(fPathWith(child).toString());
				}
				throw UFD.newExceptionFileWithoutChild(toPath());
			case FILE:
				if (!hasChild) {
					return clazz.getResourceAsStream(toPath().toString());
				}
				throw UFD.newExceptionFileWithChild(toPath(), child);
			default:
				throw new WhatIsTypeException(fType());
		}
	}

	@Override
	public boolean fdExist(String... child) {
		Path checkedPath;
		try {
			checkedPath = toResourcePath();
		} catch (Exception ex) {
			if (ex instanceof FileSystemAlreadyExistsException) {
				URL resource = clazz.getResource(toPath().toString());
				if (resource != null) {
					return true;
				}
			} else {
				L.error("bug with fs", new StackTraceRuntimeException());
			}
			return false;
		}
		boolean hasChild = ARG.isDef(child);
		switch (fType()) {
			case DIR:
				if (hasChild) {
					checkedPath = checkedPath.resolve(IT.NE(child[0]));
				}
				break;
			case FILE:
				if (hasChild) {
					throw UFD.newExceptionFileWithChild(toPath(), child);
				}
				break;
			default:
				throw new WhatIsTypeException(fType());
		}
		try {
			return Files.exists(toResourcePath().resolve(checkedPath));
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	public EFT fType(EFT... defRq) {
		return isFileOrDir ? EFT.FILE : EFT.DIR;
	}

	/**
	 * *************************************************************
	 * --------------------------- COPY RESOURCE ---------------------
	 * *************************************************************
	 */
	public static Path copyToRunLocation_(Class clazz, List<String> resources, UFS.COPY.CopyOpt copyOpt) throws IOException {
		Path path = null;
		for (String file : resources) {
			RES copiedTo = RES.of(clazz, file);
			copiedTo.copyToRunLocation_(copyOpt);
		}
		return path;
	}

	public static Path copyToLocation_(Class clazz, Path dstDir, List<String> resources, boolean createDstDir, UFS.COPY.CopyOpt copyOpt) throws IOException {
		Path path = null;
		for (String fileOrDir : resources) {
			RES copiedTo = RES.of(clazz, fileOrDir);
			path = copiedTo.copyToLocation_(dstDir, createDstDir, copyOpt);
		}
		return path;
	}

	public Path copyToRunLocation_(UFS.COPY.CopyOpt copyOpt) throws IOException {
		Path dstDir = Paths.get(".");
		return copyToLocation_(dstDir, false, copyOpt);
	}

	public Path copyToLocation_(Path dstDir, boolean createDstDir, UFS.COPY.CopyOpt copyOpt) throws IOException {
		if (createDstDir) {
			UFS.MKDIR.mkdirsIfNotExist(dstDir);
		}
		switch (copyOpt) {
			case FD_SKIP_IF_EXIST:
				if (isFileOrDir) {
					String resourcePath = UF.normFileStart(getFileOrDir());//если указан ресурс через рут, надо удалить его , иначе exist не увидит
					Path dstResourcePath = dstDir.resolve(resourcePath);
					if (EFT.FILE.existSave(dstResourcePath)) {
						if (L.isWarnEnabled()) {
							L.warn("Resource '{}' (file={}) exist", dstResourcePath, isFileOrDir);
						}
						return dstResourcePath;
					}
				} else {
					//идем дальше в метод копирования директории и чекаем exist для merge
				}
				break;

		}
		Path copiedTo = copyToDir(dstDir, createDstDir, copyOpt);
		if (L.isInfoEnabled()) {
			L.info("Resource '{}' ${}$ copied to '{}'", getFileOrDir(), copyOpt, copiedTo);
		}
		return copiedTo;
	}

	public Path copyToDirIfNotExist(Path dstDir, boolean crateDstDir) throws IOException {
		String res = UF.normFileStart(getFileOrDir());//если указан ресурс через рут, надо удалить его , иначе exist не увидит
		Path newPath = dstDir.resolve(res);
		if (EFT.existSave(newPath, isFileOrDir)) {
			return newPath;
		}
		return copyToDir(dstDir, crateDstDir, UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST);
	}

	@Override
	public Path copyToDir(Path dstDir, boolean createDstDir, UFS.COPY.CopyOpt copyOpt) throws IOException {
		try {
			Path dst;
			if (isJarResource()) {
				dst = copyToDirFromJar(dstDir, createDstDir, copyOpt);
			} else {
				dst = UFS.COPY.copyToDir(toResourcePath(), dstDir, createDstDir, copyOpt);
			}
			if (L.isDebugEnabled()) {
				L.debug("copyToDir '{}' ok:{}", getFileOrDir(), dst);
			}
			return dst;
		} catch (URISyntaxException e) {
			throw new FIllegalStateException("Error copy [%s] to dst [%s]", toUrl(), dstDir);
		}
	}

	private Path copyToDirFromJar(Path dstDir, boolean createDstDir, UFS.COPY.CopyOpt copyOpt) throws IOException {
		Path destFileOrDir = dstDir.resolve(toPath().getFileName());
		boolean existSave = EFT.existSave(destFileOrDir, isFileOrDir);
		if (copyOpt == UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST && existSave) {
			return destFileOrDir;
		}
		UFS.MKDIR.createDirs_(dstDir, createDstDir);
		if (isFileOrDir) {
			try (InputStream stream = clazz.getResourceAsStream(getFileOrDir())) {
				if (stream == null) {
					throw new FIllegalArgumentException("Class [%s] invalid resource [%s]. InputStream from package is null", clazz, getFileOrDir());
				}
				Files.copy(stream, destFileOrDir, StandardCopyOption.REPLACE_EXISTING);
				return destFileOrDir;
			}
		} else {
			URL urlRes = clazz.getResource(getFileOrDir());
			UPackageResource.copyResourcesRecursivelyToDirectory(urlRes, destFileOrDir.toFile(), copyOpt);
			return dstDir;

		}
	}

	@SneakyThrows
	public List ls(String... child) throws IOException {
		if (isJarResource()) {
			if (isFileOrDir && ARG.isDef(child)) {
				throw new FIllegalStateException("RES '%s' is FILE must be without child %s", getFileOrDir(), Arrays.asList(child));
			}
			String path = UFD.createDirWithChild(toPath(), child).toString();
			URL urlRes = clazz.getResource(path);
			return UPackageResource.walk(clazz, (JarURLConnection) urlRes.openConnection());
		} else {
			Path p = toResourcePath();
			return UFD.CHILDS.getAllChildPaths(p, null);
		}

	}

}