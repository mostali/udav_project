package mpc.rfl;


import com.google.common.reflect.ClassPath;
import lombok.SneakyThrows;
import mpu.core.ARR;
import mpu.IT;
import mpu.str.Rt;
import mpu.str.Sb;
import mpu.str.STR;
import mpu.Sys;
import mpu.X;
import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.util.QueryFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

/**
 * Another Example
 * https://linuxtut.com/en/c46b7811f56f22e6588a/
 */

//https://dzone.com/articles/spring-boot-classloader-and-class-override
@Deprecated
public class UReflExt {
	public static final Logger L = LoggerFactory.getLogger(UReflExt.class);
	public static final String EXT_CLASS = "class";

	private static String SPRING_BOOT_JAR_PATH_PFX = "/BOOT-INF/classes";

	public static Predicate<Class> createPredicateSingleAno(Class<? extends Annotation> annotationClass) {
		return aClass -> annotationClass == null ? true : aClass.isAnnotationPresent(annotationClass);
	}

	public static void test(String pkg, ClassLoader classLoader) throws IOException, ClassNotFoundException {
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
			test(pkg, IT.NN(classLoader));
			classLoader = ClassLoader.getSystemClassLoader();
			test(pkg, IT.NN(classLoader));
			return;
		}
		List<Class> classes = null;

		try {
			Sys.p("First way:DBL");
			classes = getAllPackageClassess_viaNativeSearch(null, classLoader, pkg);
			Sys.p("Found:" + classes);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			Sys.p("First way:RONMANO");
			classes = getAllPackageClassess_viaRonmamoSearch(null, pkg);
			Sys.p("Found:" + classes);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			Sys.p("First way:SpringBootPackScanner");
			SpringBootPackScanner.getAllJarPackageClassess(pkg, classLoader, null);
			Sys.p("Found:" + ARR.as(classes));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			Sys.p("First way:SimplePackScaner");
			classes = ARR.as(SimplePackScanner.getClasses(pkg, classLoader, null));
			Sys.p("Found:" + classes);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			Sys.p("First way:GuavaPackScanner");
			classes = ARR.as(GuavaPackScanner.package_classes_guava(pkg, classLoader, null));
			Sys.p("Found:" + classes);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			Sys.p("First way:GuavaPackScanner");
			classes = new ArrayList<>(SimplePackScanner_OneLEvel.findAllClassesUsingClassLoader(pkg, classLoader, null));
			Sys.p("Found:" + classes);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	//use double search - because ronmamo lib not found classes in jar in inner package level 2
	public static List<Class> getAllPackageClassess_viaDoubleSearch(boolean firstNative_Or_RonmamoSearch, Class ano, ClassLoader classLoader, String... routePackages) {
		List<Class> routeClasses = new LinkedList<>();
		for (String pkg : routePackages) {
			List<Class> classes;
			if (firstNative_Or_RonmamoSearch) {
				classes = getAllPackageClassess_viaNativeSearch(ano, classLoader, pkg);
			} else {
				classes = getAllPackageClassess_viaRonmamoSearch(ano, pkg);
			}
			if (X.empty(classes)) {
				if (firstNative_Or_RonmamoSearch) {
					classes = getAllPackageClassess_viaRonmamoSearch(ano, pkg);
				} else {
					classes = getAllPackageClassess_viaNativeSearch(ano, classLoader, pkg);
				}
			}
			if (L.isInfoEnabled()) {
				Sb sb = Rt.buildReport(classes, X.f("\nPackage '%s' has '%s' entities (%s)", pkg, X.sizeOf(classes), ano.getSimpleName()));
				L.info(sb.toString());
				if (X.empty(classes)) {
					if (L.isErrorEnabled()) {
						L.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						L.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						L.error("!!!!!!!!!!!!! WARNING !!!!!! ANO ENTITY IS EMPTY !!!!!");
						L.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						L.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					}
				}
			}
			if (X.notEmpty(classes)) {
				routeClasses.addAll(classes);
			}
		}
		return routeClasses;
	}

	private static List<Class> getAllPackageClassess_viaRonmamoSearch(Class ano, String... routePackages) {
		List<Class> routeClasses = new LinkedList<>();
		for (String pkg : routePackages) {
			Reflections reflections = new Reflections(pkg);
			QueryFunction<Store, ?> of = ano == null ? SubTypes.of() : SubTypes.of(TypesAnnotated.with(ano));
			Set<Class<?>> annotated = reflections.get(of.asClass());
			routeClasses.addAll(annotated);
		}
		return routeClasses;
	}

	@SneakyThrows
	@Deprecated // this way not found class , when jar in jar - throw FileSystemAlreadyExist
	public static List<Class> getAllPackageClassess_viaNativeSearch(Class ano, ClassLoader classLoader, String... routePackages) {
		List<Class> routeClasses = new LinkedList<>();
		for (String routePackage : routePackages) {
			routePackage = "/" + STR.removeStartString(routePackage, "/");
			URL resource = UReflExt.class.getResource(routePackage);
			boolean isJar = resource == null ? false : resource.toURI().getScheme().equals("jar");
			Collection<Class> routeClasses0;
			if (isJar) {
				routeClasses0 = SpringBootPackScanner.getAllJarPackageClassess(routePackage, classLoader, ano);
			} else {
				routeClasses0 = ARR.as(SimplePackScanner.getClasses(routePackage.substring(1), classLoader, createPredicateSingleAno(ano)));
			}
			routeClasses.addAll(routeClasses0);
		}
		return routeClasses;
	}

	/**
	 * *************************************************************
	 * ------------------ SpringBootPackScanner --------------------
	 * *************************************************************
	 */

	//https://github.com/spring-projects/spring-boot/issues/7161
	private static class SpringBootPackScanner {
		@SneakyThrows
		public static List<Class> getAllJarPackageClassess(String pkg, ClassLoader classLoader, Class annotation) {

			List classes = new ArrayList();

			pkg = "/" + STR.removeStartString(pkg, "/");

			URL resource = classLoader.getResource(pkg);

			IT.NN(resource, "Resource '%s' is null", pkg);

			URI uri = resource.toURI();

			boolean isJar = uri.getScheme().equals("jar");
			IT.state(isJar, "only jar");

			FileSystem fileSystem;
			try {
				fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
			} catch (FileSystemAlreadyExistsException ex) {
				//it may be case with jar in jar
				if (L.isWarnEnabled()) {
					L.warn("ERROR handle uri '{}' when founding class in package '{}' with ano '{}' ( it may be jar in jar?)", uri, pkg, annotation.getSimpleName());
				}
				return classes;
			}
			Path path;

			path = fileSystem.getPath(SPRING_BOOT_JAR_PATH_PFX + pkg);
			Files.walk(path).forEach(p -> {
				if (Files.isDirectory(p)) {
					return;
				} else if (!p.startsWith(SPRING_BOOT_JAR_PATH_PFX)) {
					return;
				} else if (!p.getFileName().toString().endsWith(EXT_CLASS)) {
					if (L.isWarnEnabled()) {
						L.warn("Scan package (SpringBoot) resource '{}' SKIP ", p);
					}
					return;
				}
				String clazz = p.toString().substring(SPRING_BOOT_JAR_PATH_PFX.length() + 1).replace("/", ".");
				clazz = clazz.substring(0, clazz.length() - (EXT_CLASS.length() + 1));
				Class<?> checkedClass = null;
				try {
					checkedClass = classLoader.loadClass(clazz);
				} catch (ClassNotFoundException e) {
					if (L.isWarnEnabled()) {
						L.warn("Scan package (SpringBoot) class '{}' NOT FOUND ", clazz);
					}
					return;
				}
				if (annotation == null || checkedClass.getAnnotation(annotation) != null) {
					classes.add(checkedClass);
				}
			});
			return classes;
		}
	}

	/**
	 * *************************************************************
	 * ------------------- SimplePackScanner -----------------------
	 * *************************************************************
	 */

	//https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
	private static class SimplePackScanner {
		private static Class[] getClasses(String packageName, ClassLoader classLoader, Predicate<Class> predicate) throws ClassNotFoundException, IOException {
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			ArrayList<Class> classes = new ArrayList<Class>();
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName, predicate));
			}
			return classes.toArray(new Class[classes.size()]);
		}

		/**
		 * Recursive method used to find all classes in a given directory and subdirs.
		 *
		 * @param directory   The base directory
		 * @param packageName The package name for classes found inside the base directory
		 * @return The classes
		 * @throws ClassNotFoundException
		 */
		private static List<Class> findClasses(File directory, String packageName, Predicate<Class> predicate) throws ClassNotFoundException {
			List<Class> classes = new ArrayList<Class>();
			if (!directory.exists()) {
				return classes;
			}
			File[] files = directory.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					assert !file.getName().contains(".");
					classes.addAll(findClasses(file, packageName + "." + file.getName(), predicate));
				} else if (file.getName().endsWith(".class")) {
					String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
					try {
						Class<?> class0 = Class.forName(className);
						if (predicate == null || predicate.test(class0)) {
							classes.add(class0);
						}
					} catch (ClassNotFoundException e) {
						if (L.isWarnEnabled()) {
							L.warn("Scan package (simple) class '{}' not found", className);
						}
						return null;
					}
				}
			}
			return classes;
		}
	}

	/**
	 * *************************************************************
	 * ------------------- GuavaPackScanner ------------------------
	 * *************************************************************
	 */
	private static class GuavaPackScanner {
		//https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
		//https://stackoverflow.com/questions/15720822/how-to-get-names-of-classes-inside-a-jar-file
		public static Class[] package_classes_guava(String packageName, ClassLoader classLoader, Predicate<Class> classPredicate) throws ClassNotFoundException, IOException {
			List<Class> packClasses = new ArrayList<>();
			ClassPath cp = ClassPath.from(classLoader);
			for (ClassPath.ClassInfo info : cp.getTopLevelClassesRecursive(packageName)) {
				Class clazz = info.load();
				if (classPredicate == null || classPredicate.test(clazz)) {
					packClasses.add(clazz);
				}
				Class[] inners = clazz.getDeclaredClasses();
				for (Class inner : inners) {
					if (classPredicate == null || classPredicate.test(inner)) {
						packClasses.add(inner);
					}
				}
			}
			if (true) {
				return packClasses.toArray(new Class[0]);
			}
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			ArrayList<Class> classes = new ArrayList<Class>();
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName, classPredicate));
			}
			return classes.toArray(new Class[classes.size()]);
		}

		public static List<Class> findClasses(File directory, String packageName, Predicate<Class> classPredicate) throws ClassNotFoundException {
			List<Class> classes = new ArrayList<Class>();
			if (!directory.exists()) {
				return classes;
			}
			File[] files = directory.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					assert !file.getName().contains(".");
					classes.addAll(findClasses(file, packageName + "." + file.getName(), classPredicate));
				} else if (file.getName().endsWith(".class")) {
					Class<?> clazz = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
					if (classPredicate == null || classPredicate.test(clazz)) {
						classes.add(clazz);
					}
				}
			}
			return classes;
		}

	}

	/**
	 * *************************************************************
	 * --------------- BaeldungPackScanner (OneLevel) --------------
	 * *************************************************************
	 */
	//https://www.baeldung.com/java-find-all-classes-in-package
	private static class SimplePackScanner_OneLEvel {

		public static Set<Class> findAllClassesUsingClassLoader(String packageName, ClassLoader classLoader, Predicate<Class> predicate) {
			packageName = STR.removeStartString(packageName, "/");
			String name = packageName.replaceAll("[.]", "/");
			InputStream stream = classLoader.getResourceAsStream(name);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			Stream<String> lines = reader.lines();
			String finalPackageName = packageName;
			return lines.filter(line -> line.endsWith(EXT_CLASS)).map(line -> getClass(line, finalPackageName)).filter(c -> predicate == null ? true : predicate.test(c)).collect(Collectors.toSet());
		}

		private static Class getClass(String className, String packageName) {
			String className0 = packageName + "." + className.substring(0, className.lastIndexOf('.'));
			try {
				return Class.forName(className0);
			} catch (ClassNotFoundException e) {
				if (L.isWarnEnabled()) {
					L.warn("Scan package (simple) class '{}' not found", className0);
				}
				return null;
			}
		}

	}


}
