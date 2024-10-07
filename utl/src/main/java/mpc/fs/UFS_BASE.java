package mpc.fs;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.EFT;
import mpc.fs.fd.UFD;
import mpc.json.UGson;
import mpu.X;
import mpu.core.RW;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UFS_BASE {

	public static final Logger L = LoggerFactory.getLogger(UFS.class);

	public static class MKDIR {

		public static Path mkdirsIfNotExistForParent(String file) {
			return mkdirsIfNotExistForParent(Paths.get(file));
		}

		public static Path mkdirsIfNotExistForParent(Path file) {
			Path parent = file.getParent();
			mkdirsIfNotExist(parent);
			return parent;

		}

		public static Path mkdirIfNotExistForParent(Path file) {
			Path parent = file.getParent();
			mkdirIfNotExist(parent);
			return parent;
		}

		public static void mkdirParentIfNotExist(Path file, int mkdirCount) {
			mkdirIfNotExist(file.getParent(), mkdirCount);
		}

		public static void mkdirIfNotExist(Path file, int mkdirCount) {
			Deque<Path> collection = getAllParents(file, mkdirCount, true);
			for (Path dir : collection) {
				mkdirIfNotExist(dir);
			}
		}

		private static Deque<Path> getAllParents(Path file, int countPath, boolean includeChild) {
			Deque<Path> deque = new ConcurrentLinkedDeque<>();
			Path parent = null;
			int org = countPath;
			if (includeChild) {
				countPath--;
			}
			while (countPath-- > 0) {
				deque.addFirst(parent == null ? parent = file.getParent() : (parent = parent.getParent()));
				if (parent == null) {
					throw new NullPointerException("parent if null from length:" + org);
				}
			}
			if (includeChild) {
				deque.addLast(file);
			}
			return deque;
		}

		public static void mkdirsIfNotExist(Path source) {
			mkdirsIfNotExistImpl(source, true);
		}

		public static void mkdirIfNotExist(Path source) {
			mkdirsIfNotExistImpl(source, false);
		}

		public static void createDirsOrSingleDirOrCheckExist(Path directory, boolean... mkdirsOrSingleDirOrCheck) {
			createDirsOrSingleDirOrCheckExist(directory, ARG.toDefBoolean(mkdirsOrSingleDirOrCheck));
		}

		public static Path createDirsOrSingleDirOrCheckExist(Path directory, Boolean mkdirsOrSingleDirOrCheck) {
			if (mkdirsOrSingleDirOrCheck == null) {
				IT.isDirExist(directory);
			} else {
				mkdirsIfNotExistImpl(directory, mkdirsOrSingleDirOrCheck);
			}
			return directory;
		}

		public static void mkdirsIfNotExistImpl(Path source, boolean mkdirsOrSingleDir) {
			if (Files.isDirectory(source)) {
				return;
			} else {
				IT.isFileNotExist(source);
			}

			boolean resMkdir = mkdirsOrSingleDir ? source.toFile().mkdirs() : source.toFile().mkdir();
			if (!resMkdir) {
				throw new RequiredRuntimeException("Mkdir/Mkdirs (%s) '%s' return FALSE - it maybe when path has many not existing child directory. %s", mkdirsOrSingleDir, source, mkdirsOrSingleDir ? "" : "May be use MKDIRS instead MKDIR?");
			}
		}

		@SneakyThrows
		public static void createDirs(Path directory) {
			createDirs_(directory.toFile(), true, false);
		}

		public static void createDirs_(Path directory) throws IOException {
			createDirs_(directory.toFile(), true, false);
		}

		public static void createDirs_(Path directory, boolean createDirIfNotExist) throws IOException {
			createDirs_(directory.toFile(), createDirIfNotExist, false);
		}

		@SneakyThrows
		public static void createDirs(File directory, boolean createDirIfNotExist, boolean cleanDirIfExists) {
			createDirs_(directory, createDirIfNotExist, cleanDirIfExists);
		}

		//Copy+refactoring  FileUtils#forceMkdir
		public static void createDirs_(File directory, boolean... cleanDirIfExists) throws IOException {
			createDirs_(directory, true, ARG.isDefEqTrue(cleanDirIfExists));
		}

		@SneakyThrows
		public static void createDir(File directory) {
			createDIR_(directory, true, true, false);
		}

		public static void createDirs_(File directory, boolean createDirIfNotExist, boolean cleanDirIfExists) throws IOException {
			createDIR_(directory, false, createDirIfNotExist, cleanDirIfExists);
		}

		public static void createDIR_(File directory, boolean mkdir_mkdirs, boolean createDirIfNotExist, boolean cleanDirIfExists) throws IOException {
			String message;
			if (directory.exists()) {
				if (directory.isFile()) {
					message = "File [" + directory + "] exists and is " + "not a directory. Unable to create directory.";
					throw new IOException(message);
				}
				if (cleanDirIfExists) {
					FileUtils.cleanDirectory(directory);
				}
			} else {
				if (!createDirIfNotExist) {
					message = "Dir [" + directory + "] not exists, createDirIfNotExist=false";
					throw new IOException(message);
				} else {
					boolean rslt = mkdir_mkdirs ? directory.mkdir() : directory.mkdirs();
					if (!rslt) {
						message = "Unable to create directory [" + directory + "]";
						throw new IOException(message);
					}
				}
			}
		}

		@SneakyThrows
		public static void mkDirMkDirs(Path dir, boolean... mkDirs_mkDir_orSkip) {
			if (ARG.isDefEqTrue(mkDirs_mkDir_orSkip)) {
				Files.createDirectories(dir);
			} else if (ARG.isDefEqFalse(mkDirs_mkDir_orSkip)) {
				Files.createDirectory(dir);
			}
		}
	}

	public static class MKFILE {
		@SneakyThrows
		public static boolean createFileRmIfExist(Path file, boolean... mkDirs_mkDir) {
			if (Files.isRegularFile(file)) {
				Files.delete(file);
			}
			MKDIR.mkDirMkDirs(file.getParent(), mkDirs_mkDir);
			Files.createFile(file);
			return true;
		}

		@SneakyThrows
		public static boolean createFile(Path file, String... content) {
			Files.createFile(file);
			if (ARG.isDef(content)) {
				RW.write(file, ARG.toDef(content));
			}
			return true;
		}

		public static boolean createFileIfNotExist(String file) throws IOException {
			return createFileIfNotExist_(Paths.get(file));
		}

		public static boolean createFileIfNotExist(Path file, Boolean mkDirsOrDirOrNot, boolean... defRq) {
			try {
				if (Files.isRegularFile(file)) {
					return false;
				}
				MKDIR.createDirsOrSingleDirOrCheckExist(file.getParent(), mkDirsOrDirOrNot);
				Files.createFile(file);
				return true;
			} catch (Exception ex) {
				return ARG.toDefBooleanOrThrow(ex, defRq);
			}
		}

		@SneakyThrows
		public static boolean createEmptyFileMkdirsIfNotExist(Path file, boolean... json) {
			MKDIR.createDirs(file.getParent());
			return createEmptyFileIfNotExist(file, json);
		}

		@SneakyThrows
		public static boolean createEmptyFileIfNotExist(Path file, boolean... json) {
			if (!Files.exists(file)) {
				Files.createFile(file);
				RW.write(file, ARG.isDefEqTrue(json) ? UGson.EMPTY : "");
				return true;
			}
			IT.isFileExist(file);
			String cnt = RW.readContent(file);
			if (X.notEmpty(cnt)) {
				return false;
			}
			RW.write(file, ARG.isDefEqTrue(json) ? UGson.EMPTY : "");
			return false;
		}

		public static boolean createFileIfNotExistWithContentMkdirs(Path file, String withContent) {
			if (!Files.exists(file)) {
				MKDIR.createDirs(file.getParent());
				RW.write(file, IT.NN(withContent), StandardOpenOption.CREATE_NEW);
				return true;
			}
			return false;
		}

		@SneakyThrows
		public static boolean createFileIfNotExist(Path file) {
			return createFileIfNotExist_(file);
		}

		public static boolean createFileIfNotExist_(Path file) throws IOException {
			if (Files.exists(file)) {
				return false;
			}
			MKDIR.createDirs(file.getParent());
			Files.createFile(file);
			return true;
		}
	}

	public static class MV {

		@SneakyThrows
		public static Path moveIn(Path moved, Path dst, boolean... mkdirs_mkdir_ornot) {
			return moveIn_(moved, dst, mkdirs_mkdir_ornot);
		}

		public static Path moveIn_(Path moved, Path dst, boolean... mkdirs_mkdir_ornot) throws IOException {
			MKDIR.createDirsOrSingleDirOrCheckExist(dst, mkdirs_mkdir_ornot);
			Path resolve = Files.move(moved, dst.resolve(moved.getFileName()), StandardCopyOption.ATOMIC_MOVE);
			if (L.isInfoEnabled()) {
				L.info("Move In " + moved + " >>> " + dst + " >>> " + resolve);
			}
			return resolve;
			//FileUtils.moveDirectoryToDirectory(moved.toFile(), dst.toFile(), false);
			//return dst.resolve(moved.getFileName());
		}

		public static Path rename(Path moved, String newName, Boolean mkdirs_mkdir_ornot, CopyOption... copyOptions) {
			return move(moved, moved.getParent().resolve(newName), mkdirs_mkdir_ornot, copyOptions);
		}

		public static Path moveWithChangeExisting_(Path moved, Path dst, boolean... mkdirs_mkdir_ornot) throws IOException {
			MKDIR.createDirsOrSingleDirOrCheckExist(dst.getParent(), mkdirs_mkdir_ornot);
			if (UFS.exist(dst)) {
//				Path newDst = UFS.generateAutoFilename(dst);

			}
			return Files.move(moved, dst);////StandardCopyOption.REPLACE_EXISTING
		}

		public static Path moveWithReplaceExisting_(Path moved, Path dst, boolean... mkdirs_mkdir_ornot) throws IOException {
			MKDIR.createDirsOrSingleDirOrCheckExist(dst.getParent(), mkdirs_mkdir_ornot);
			return Files.move(moved, dst);////StandardCopyOption.REPLACE_EXISTING
		}

		@SneakyThrows
		public static Path move(Path moved, Path dst, Boolean mkdirs_mkdir_ornot, CopyOption... copyOptions) {
			MKDIR.createDirsOrSingleDirOrCheckExist(dst.getParent(), mkdirs_mkdir_ornot);
			Path dstMoved = Files.move(moved, dst, copyOptions);
			if (L.isInfoEnabled()) {
				L.info("Move " + moved + " >>> " + dst + " >>> " + dstMoved);
			}
			return dstMoved;
		}
	}

	public static class RM {
		public static void removeQuicklyFileOrDir(Path... files) {
			for (int i = 0; i < files.length; i++) {
				FileUtils.deleteQuietly(files[i].toFile());
			}
		}

		public static void removeQuicklyFileOrDir(String... files) {
			for (int i = 0; i < files.length; i++) {
				String f = files[i];
				FileUtils.deleteQuietly(new File(f));
			}
		}

		//
		//

		public static void removeFileQk(List<String> files) {
			for (String file : files) {
				if (X.notEmpty(file)) {
					removeFileQk(Paths.get(file));
				}
			}
		}

		@SneakyThrows
		public static void removeFileQk(Path file) {
			removeFileQk_(file);
		}

		@SneakyThrows
		public static void removeFileQk(File file) {
			removeFileQk_(file.toPath());
		}

		@SneakyThrows
		public static void removeFileQk(String file) {
			if (X.empty(file)) {
				return;
			}
			removeFileQk_(Paths.get(file));
		}


		public static void removeFileQk_(Path file) throws IOException {
			if (Files.isRegularFile(file)) {
				Files.delete(file);
				if (L.isDebugEnabled()) {
					L.debug("File '{}' removed", file);
				}
			} else {
				if (L.isDebugEnabled()) {
					L.debug("File '{}' not removed (if not regular file)", file);
				}
			}
		}

		public static void deleteDir(String dir) throws IOException {
			FileUtils.deleteDirectory(new File(dir));
		}

		@SneakyThrows
		public static void deleteDir(Path dir) {
			deleteDir_(dir);
		}

		public static void deleteDir_(Path dir) throws IOException {
			FileUtils.deleteDirectory(dir.toFile());
		}

//		@SneakyThrows
//		public static void deleteDir(Path directory, boolean... quitly) {
//			try {
//				Files.walkFileTree(ERR.isDirExist(directory), new SimpleFileVisitor<Path>() {
//					@Override
//					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//						Files.delete(file);
//						return FileVisitResult.CONTINUE;
//					}
//
//					@Override
//					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//						Files.delete(dir);
//						return FileVisitResult.CONTINUE;
//					}
//				});
//			} catch (IOException ex) {
//				if (ARG.isDefEqTrue(quitly)) {
//					return;
//				}
//				throw ex;
//			}
//		}

	}

	public static class IS {

		public static String toString(InputStream is, String... defRq) {
			try {
				return toString_(is, Charset.defaultCharset());
			} catch (Exception e) {
				if (ARG.isDef(defRq)) {
					return ARG.toDef(defRq);
				}
				return X.throwException(e);
			}
		}

		public static String toString_(InputStream is, Charset charset) throws IOException {
			return IOUtils.toString(is, charset);
		}

		public static void toFile(InputStream is, String targetFile) throws IOException {
			MKDIR.mkdirsIfNotExistForParent(targetFile);
			Files.copy(is, Paths.get(targetFile), StandardCopyOption.REPLACE_EXISTING);
			IOUtils.closeQuietly(is);
		}
	}

	public static class COPY {
		@SneakyThrows
		public static Path copyFileAs(Path srcFile, Path dstFile, boolean... mkdirs_dir_not) {
			if (L.isDebugEnabled()) {
				L.debug("copyFileAs src '{}' to dst '{}'", srcFile, dstFile);
			}
			FileUtils.copyFile(srcFile.toFile(), dstFile.toFile());
			return dstFile;
		}

		public static Path copyFileToDirectory(Path srcFile, Path destDir, boolean createDstDir, boolean... skipIfExist) throws IOException {
			if (L.isDebugEnabled()) {
				L.debug("copyFileToDirectory src:[{}] >>> dst:[{}]", srcFile, destDir);
			}
			MKDIR.createDirs_(destDir.toFile(), createDstDir, false);
			Path dst = destDir.resolve(srcFile.getFileName().toString());
			if (ARG.isDefEqTrue(skipIfExist) && Files.exists(dst)) {
				if (Files.isRegularFile(dst)) {
					return dst;
				}
				throw new WhatIsTypeException("Is not file : " + dst);
			}
			FileUtils.copyFileToDirectory(srcFile.toFile(), destDir.toFile());
			return dst;
		}

		@SneakyThrows
		public static Path copyDirectory(Path srcDir, Path destDir) {
			FileUtils.copyDirectory(srcDir.toFile(), destDir.toFile());
			return destDir;
		}

		@SneakyThrows
		public static Path copyDirectoryToDirectory(Path srcDir, Path destDir, boolean createDestDir, boolean mergeOrReplaceFilesAndSkipExist) {
			if (mergeOrReplaceFilesAndSkipExist) {
				return copyDirectoryToDirectoryWithMerge(srcDir, destDir, createDestDir);
			}
			return copyDirectoryToDirectoryWithReplaceFilesSkipExist(srcDir, destDir, createDestDir);
		}

		public static Path copyDirectoryToDirectoryWithMerge(Path srcDir, Path destDir, boolean... createDestDir) throws IOException {
			Path targetDir = destDir.resolve(srcDir.getFileName());
			if (!Files.exists(targetDir)) {
				return copyDirectoryToDirectoryWithReplaceFilesSkipExist(srcDir, destDir, createDestDir);
			}
			if (!Files.isDirectory(targetDir)) {
				throw new WhatIsTypeException("What is file entity '%s' ?", targetDir);
			}
			mergeDir(srcDir, targetDir, createDestDir);
			return targetDir;
		}

		public static Path copyDirectoryToDirectoryWithReplaceFilesSkipExist(Path srcDir, Path destDir, boolean... createDestDir) throws IOException {
			if (L.isDebugEnabled()) {
				L.debug("copyDirectoryToDirectoryWithReplace src:[{}] >>> dst:[{}]", srcDir, destDir);
			}
			MKDIR.createDirs_(destDir, ARG.isDefEqTrue(createDestDir));
			FileUtils.copyDirectoryToDirectory(srcDir.toFile(), destDir.toFile());
			Path dst = destDir.resolve(srcDir.getFileName().toString());
			return dst;
		}

		public static void mergeDir(Path srcDir, Path destDir, boolean... createDstDirIfNotExist) throws IOException {
			if (L.isDebugEnabled()) {
				L.debug("mergeDir src:{} >>> dst:{}", srcDir, destDir);
			}
			Boolean mk = ARG.toDefBoolean(createDstDirIfNotExist);
			if (mk == null || mk == false) {
				IT.isDirExist(destDir);
			} else {
				MKDIR.mkdirsIfNotExistImpl(destDir, true);
			}
			List<Path> diffInDst = UFD.CHILDS.findDiffInDstRelative(srcDir, destDir);
			if (diffInDst.isEmpty()) {
				if (L.isDebugEnabled()) {
					L.debug("mergeDir src:{} >>> dst:{} @DIFFERENCE NOT FOUND@", srcDir, destDir);
				}
				return;
			} else {
				if (L.isDebugEnabled()) {
					L.debug("mergeDir src:{} >>> dst:{} @DIFFERENCE FOUND@:{}", srcDir, destDir, diffInDst);
				}
			}
			nextSrcInner:
			for (Path relative : diffInDst) {
				Path srcInner = srcDir.resolve(relative);
				EFT srcInnerType = EFT.of(srcInner);
				Path dstInner = destDir.resolve(relative);
				switch (srcInnerType) {
					case DIR:
						mergeDir(srcInner, dstInner, true);
						continue nextSrcInner;
					case FILE: {
						boolean isExist = Files.exists(dstInner);
						boolean replace = false;
						if (isExist) {
							if (replace) {
								FileUtils.copyFileToDirectory(srcInner.toFile(), relative.getParent().toFile());
								if (L.isDebugEnabled()) {
									L.debug("mergeDir src:{}:{} @REPLACE@ (exist file)", srcInnerType, srcInner);
								}
							} else {
								if (L.isDebugEnabled()) {
									L.debug("mergeDir src:{}:{} @SKIP@ (exist file)", srcInnerType, srcInner);
								}
							}
						} else {
							if (L.isDebugEnabled()) {
								L.debug("mergeDir src:{}:{} @MERGE@ to dst:{}", srcInnerType, srcInner, destDir);
							}
							FileUtils.copyFileToDirectory(srcInner.toFile(), dstInner.getParent().toFile());
						}

						continue nextSrcInner;
					}
					default:
						throw new WhatIsTypeException(srcInnerType);
				}
			}

		}

		/**
		 * @param srcPackagePathFile, e.g. pack1/pack2/filename
		 */
		public static void copyFileFromPackageToDestFile(Class clas, Path srcPackagePathFile, Path destFile) throws IOException {
			try (InputStream stream = clas.getClassLoader().getResourceAsStream(srcPackagePathFile.toString())) {
				IT.notNull(stream, "InputStream from package", srcPackagePathFile, "is NULL");
				Files.copy(stream, destFile, StandardCopyOption.REPLACE_EXISTING);
			}
		}

		@SneakyThrows
		public static Path copyToDir(Path src, Path dst) {
			FileUtils.copyDirectory(src.toFile(), dst.toFile());
			return dst;
		}

		public static Path copyToDir(Path src, Path dst, boolean createDstDir, CopyOpt copyOpt) throws IOException {
			EFT fileDirType = EFT.of(src);
			switch (fileDirType) {
				case DIR:
					switch (copyOpt) {
						case FD_SKIP_IF_EXIST:
							Path dstTarget = dst.resolve(src.getFileName());
							if (fileDirType.existSave(dstTarget)) {
								return dstTarget;
							}
						case DIR_MERGE:
						case DIR_REPLACE_SKIP_EXIST:
							return copyDirectoryToDirectory(src, dst, createDstDir, copyOpt == CopyOpt.DIR_MERGE);
						default:
							throw new WhatIsTypeException(fileDirType + ":" + copyOpt);
					}
				case FILE:
					switch (copyOpt) {
						case FD_SKIP_IF_EXIST:
						case FD_REPLACE_IF_EXIST:
							return copyFileToDirectory(src, dst, createDstDir, copyOpt == CopyOpt.FD_SKIP_IF_EXIST);
						default:
							throw new WhatIsTypeException(fileDirType + ":" + copyOpt);
					}
				default:
					throw new WhatIsTypeException("What is type (copyToDir) ? [%s]", src);
			}
		}

		@SneakyThrows
		public static Path copyDirContentWithReplace(Path src, Path dst, boolean... mkdirs_mkdir_ornot) {
			FileUtils.copyDirectory(src.toFile(), dst.toFile());
			return dst;
		}

		public enum CopyOpt {
			FD_SKIP_IF_EXIST, FD_REPLACE_IF_EXIST, DIR_MERGE, DIR_REPLACE_SKIP_EXIST,
		}
	}
}
