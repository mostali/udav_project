package mpf.zcall;

import lombok.RequiredArgsConstructor;
import mpc.rfl.RFL;
import mpc.rfl.UReflScanner;
import mpu.X;
import mpu.core.ARR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ZJar implements ZEntity {

	final Path jarFile;
	final String[] packages;

	private List<ZType> zTypesCached;

	public static ZJar of(String... jarPathWithPackages) {
		return jarPathWithPackages.length == 1 ? of(Paths.get(jarPathWithPackages[0])) : of(Paths.get(jarPathWithPackages[0]), jarPathWithPackages);
	}

	public static ZJar of(Path jarFile, String... packages) {
		return new ZJar(jarFile, packages);
	}

//	public static List<Path> ofDir(String... packages) {
//		List<Class> allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(packages, ZType.ZTypeAno.class);
//		return allPackageClassViaClassgraph.stream().map(ZType::new).collect(Collectors.toList());
//	}
	public static List<ZType> findAll(String... packages) {
		List<Class> allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(packages, ZType.ZTypeAno.class);
		return allPackageClassViaClassgraph.stream().map(ZType::new).collect(Collectors.toList());
	}

	public static List<ZType> findAll(Path jarFile, String... packages) {
		List<Class> allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(jarFile, packages);
		return allPackageClassViaClassgraph.stream().filter(c -> RFL.getClassAnnotation(c, ZType.ZTypeAno.class, null) != null).map(ZType::new).collect(Collectors.toList());
//		return allPackageClassViaClassgraph.stream().map(ZType::new).collect(Collectors.toList());
	}


	public Map<ZType, List<ZType.ZMethod>> getMapZTypes() {
		return getAllZTypes().stream().collect(Collectors.toMap(t -> t, t -> t.getAllZMethods()));
	}

	public List<ZType> getAllZTypes() {
		Supplier<List<ZType>> findAllGet = () -> jarFile != null ? findAll(jarFile, X.empty(packages) ? ARR.EMPTY_ARGS : packages) : findAll(X.empty(packages) ? ARR.EMPTY_ARGS : packages);
		return zTypesCached != null ? zTypesCached : (zTypesCached = findAllGet.get());
	}


	public List<ZType> getAllZTypesFrom(String... packages) {
		return findAll(packages);
	}

	@Override
	public String toString() {
		return "ZJar:" + jarFile;
	}

	public String name() {
		return jarFile.getFileName().toString();
	}
}
