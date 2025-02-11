package mpc.rfl;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UReflScanner {
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
}
