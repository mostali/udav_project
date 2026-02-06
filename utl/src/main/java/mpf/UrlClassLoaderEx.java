/**
 * $Id: LibClassLoader.java,v 1.4 2005/08/16 15:49:57 sergeya Exp $
 * <p>
 * Copyright (C) 2004-2005 FK Ltd. All Rights Reserved.
 */
package mpf;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Поддержка построения classpath для содержимого каталога.
 */
public class UrlClassLoaderEx extends URLClassLoader {

	public static void main(String[] args) {

	}

	/**
	 * Создает URLClassLoader и добавляет в classpath все файлы из указанного каталога
	 *
	 * @param libDir
	 * @param parent
	 * @throws MalformedURLException
	 */
	public UrlClassLoaderEx(File libDir, ClassLoader parent) throws MalformedURLException {
		super(listURLs(new File[]{libDir}), parent);
	}

	/**
	 * Создает URLClassLoader и добавляет в classpath все файлы из указанного каталога
	 *
	 * @param libDirs
	 * @param parent
	 * @throws MalformedURLException
	 */
	public UrlClassLoaderEx(File[] libDirs, ClassLoader parent) throws MalformedURLException {
		super(listURLs(libDirs), parent);
	}

	// Получение списка файлов каталога в виде списка URL
	private static URL[] listURLs(File[] dirs) throws MalformedURLException {
		Collection<URL> urls = new ArrayList<>();

		urls.add(new File(System.getProperty("user.dir")).toURL());
		urls.add(new File(".").toURL());

		for (File dir : dirs) {
			File[] files = dir.listFiles(new JARFileFilter());

			if (files == null) {
				continue;
			}

			for (File file : files) {
				urls.add(file.toURL());
			}
		}
		return urls.toArray(new URL[urls.size()]);
	}

	private static class JARFileFilter implements FileFilter {
		@Override
		public boolean accept(File file) {
			return file.isFile();
		}
	}
}
