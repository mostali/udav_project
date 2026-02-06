package mpc.rfl;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import lombok.SneakyThrows;
import mpc.log.L;
import mpu.X;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UReflScanner {

	@SneakyThrows
	public static void main(String[] args) {
		X.exit(getAllPackageClassViaClassgraph(Paths.get("/opt/appVol/.bin/jira-mod.jar"), new String[]{"mp.jira"}));
//		X.exit(getAllPackageClassViaClassgraph(new String[]{"mp.jira"}));
//		try (ScanResult scanResult = new ClassGraph().acceptPathsNonRecursive("META-INF/config").scan()) {
//			scanResult.getResourcesWithExtension("json")
//					.forEachByteArray((Resource res, byte[] content) -> {
//						readJson(res.getPath(), new String(content, StandardCharsets.UTF_8));
//					});
//		}
	}

	public static <T> List<T> getAllPackageClassViaClassgraph(Function<ClassInfo, T> loadClass, String... routePackages) {
		return new ClassGraph().enableAnnotationInfo().whitelistPackages(routePackages).scan(1).getAllClasses().stream().map(loadClass).collect(Collectors.toList());
	}


	public static List<Class> getAllPackageClassViaClassgraph(String[] routePackages) {
		return new ClassGraph().enableAnnotationInfo().whitelistPackages(routePackages).scan(1).getAllClasses().stream().map(ClassInfo::loadClass).collect(Collectors.toList());
	}

	public static List<Class> getAllPackageClassViaClassgraph(String[] routePackages, Class routeAnoClass) {
		String anoName = routeAnoClass.getName();
		return new ClassGraph().enableAnnotationInfo().whitelistPackages(routePackages).scan(1).getAllClasses().stream().filter(i -> i.hasAnnotation(anoName)).map(ClassInfo::loadClass).collect(Collectors.toList());
	}

	@SneakyThrows
	public static List<Class> getAllPackageClassViaClassgraph(Path jar, String... routePackages) {
		return getAllPackageClassViaClassgraph(jar, null, routePackages);
	}

	@SneakyThrows
	public static List<Class> getAllPackageClassViaClassgraph(Path jar, Class routeAnoClass, String... routePackages) {
		ClassGraph classGraph = new ClassGraph().enableAnnotationInfo();
		classGraph.addClassLoader(RFL.getUrlClassLoader(jar));
		if (X.notEmpty(routePackages)) {
			classGraph.whitelistPackages(routePackages);
		}
		String anoName = routeAnoClass == null ? null : routeAnoClass.getName();
		return classGraph.scan(1).getAllClasses().stream().map(ci -> {
			try {
				Class<?> aClass = ci.loadClass();
				return anoName == null || ci.hasAnnotation(anoName) ? aClass : null;
			} catch (Exception ex) {
				L.warn(X.fl("ClassInfo error in jar {} - {}", jar, ex.getMessage()));
				return null;
			}
		}).filter(X::NN).collect(Collectors.toList());
	}

}
