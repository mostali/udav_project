package mpc.fs;

import mpu.IT;
import mpc.exception.FIllegalStateException;
import mpc.exception.WhatIsTypeException;
import mpc.fs.fd.EFT;
import mpc.fs.fd.RES;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UPackageResource {

	public static final Logger L = LoggerFactory.getLogger(UPackageResource.class);

	/**
	 * https://stackoverflow.com/questions/1386809/copy-directory-from-a-jar-file
	 */
	public static void copyFile(final File srcFile, final File destFile, UFS.COPY.CopyOpt copyOpt) throws IOException {
		boolean exist = EFT.FILE.existSave(destFile);
		switch (copyOpt) {
			case FD_SKIP_IF_EXIST:
				if (exist) {
					if (L.isDebugEnabled()) {
						L.debug("Dst File '%s' $EXIST$ $%s$", destFile, copyOpt);
					}
					return;
				}
			case FD_REPLACE_IF_EXIST:
				if (L.isDebugEnabled()) {
					L.debug("Dst File '%s' $COPY$ $%s$", destFile, copyOpt);
				}
				IOUtils.copy(new FileInputStream(srcFile), new FileOutputStream(destFile));
				break;
			default:
				throw new WhatIsTypeException(copyOpt);
		}
	}

	private static void copyFdRr(final File srcFileOrDir, final File destDir, UFS.COPY.CopyOpt copyOpt) throws IOException {
		IT.isTrue(destDir.isDirectory(), "dest dir must be dir", destDir);
		if (!srcFileOrDir.isDirectory()) {
			copyFile(srcFileOrDir, new File(destDir, srcFileOrDir.getName()), copyOpt);
		} else {
			final File newDestDir = new File(destDir, srcFileOrDir.getName());
			if (!newDestDir.exists() && !newDestDir.mkdir()) {
				throw new FIllegalStateException("Error mkdir [%s]", newDestDir);
			}
			for (final File child : srcFileOrDir.listFiles()) {
				copyFdRr(child, newDestDir, copyOpt);
			}
		}
	}

	public static List<String> getChilds(final JarURLConnection jarConnection) throws IOException {
		final JarFile jarFile = jarConnection.getJarFile();
		List<String> files = new LinkedList<>();
		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
			final JarEntry entry = e.nextElement();
			if (entry.getName().startsWith(jarConnection.getEntryName())) {
				final String filename = StringUtils.removeStart(entry.getName(), jarConnection.getEntryName());
				files.add(entry.getName() + (entry.isDirectory() ? "/" : ""));
			}
		}
		return files;
	}

	public static boolean copyJarResourcesRecursively(final JarURLConnection srcFileOrDirConnection, final File destDir, UFS.COPY.CopyOpt copyOpt) throws IOException {

		final JarFile jarFile = srcFileOrDirConnection.getJarFile();

		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
			final JarEntry entry = e.nextElement();
			if (entry.getName().startsWith(srcFileOrDirConnection.getEntryName())) {
				final String filename = StringUtils.removeStart(entry.getName(), srcFileOrDirConnection.getEntryName());

				final File f = new File(destDir, filename);
				boolean exist = EFT.existSaveRq(f.toPath(), !entry.isDirectory());
				switch (copyOpt) {
					case FD_SKIP_IF_EXIST:
						if (exist) {
							continue;
						}
					case FD_REPLACE_IF_EXIST:
						break;
					default:
						throw new WhatIsTypeException("Copy opt from jar supported only '%s' or %s", UFS.COPY.CopyOpt.FD_SKIP_IF_EXIST, UFS.COPY.CopyOpt.FD_REPLACE_IF_EXIST);
				}

				if (!entry.isDirectory()) {
					try (final InputStream entryInputStream = jarFile.getInputStream(entry)) {
						IOUtils.copy(entryInputStream, new FileOutputStream(f));
					}
				} else {
					if (!ensureDirectoryExists(f)) {
						throw new IOException("Could not create directory: " + f.getAbsolutePath());
					}
				}
			}
		}
		return true;
	}

	public static List<RES> walk(Class res, final JarURLConnection srcFileOrDirConnection) throws IOException {

		List<RES> fds = new ArrayList<>();

		final JarFile jarFile = srcFileOrDirConnection.getJarFile();

		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
			final JarEntry entry = e.nextElement();
			if (entry.getName().startsWith(srcFileOrDirConnection.getEntryName())) {
				final String filename = StringUtils.removeStart(entry.getName(), srcFileOrDirConnection.getEntryName());

				fds.add(RES.of(res, filename, !entry.isDirectory()));
			}
		}
		return fds;
	}

	public static void copyResourcesRecursivelyToDirectory(final URL srcFileOrDir, final File dstDir, UFS.COPY.CopyOpt copyOpt) throws IOException {
		final URLConnection fileOrDirConnection = srcFileOrDir.openConnection();
		if (fileOrDirConnection instanceof JarURLConnection) {
			copyJarResourcesRecursively((JarURLConnection) fileOrDirConnection, dstDir, copyOpt);
		} else {
			copyFdRr(new File(srcFileOrDir.getPath()), dstDir, copyOpt);
		}
	}

	private static boolean ensureDirectoryExists(final File f) {
		return f.exists() || f.mkdir();
	}

}
