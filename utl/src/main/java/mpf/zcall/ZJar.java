package mpf.zcall;

import lombok.RequiredArgsConstructor;
import mpc.arr.STREAM;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.rfl.RFL;
import mpc.rfl.UReflScanner;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ZJar implements ZEntity {

	final Path jarFile;
	final String[] packages;

	private List<ZType> zTypesCached;

	public static List<ZJar> ls(Path dir) {
		return STREAM.filterMapToList(UFS.ls(dir), EXT.JAR::has, ZJar::of);
	}

	public Map<String[], ZType> getZTypeApps() {
		List<ZType> allZTypesFrom = getAllZTypes();
		return allZTypesFrom.stream().collect(Collectors.toMap(z -> z.getAppVersion(), z -> z));
	}

	public static ZJar of(String... jarPathWithPackages) {
		return jarPathWithPackages.length == 1 ? of(Paths.get(jarPathWithPackages[0])) : of(Paths.get(jarPathWithPackages[0]), jarPathWithPackages);
	}

	public static ZJar of(Path jarFile, String... packages) {
		return new ZJar(jarFile, packages);
	}

	public static List<ZType> findAll(String... packages) {
		List<Class> allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(packages, ZType.ZTypeAno.class);
		return allPackageClassViaClassgraph.stream().map(ZType::new).collect(Collectors.toList());
	}

	public static List<ZType> findAll(Path jarFile, String... packages) {
		List<Class> allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(jarFile, packages);
		return allPackageClassViaClassgraph.stream().filter(c -> RFL.getClassAnnotation(c, ZType.ZTypeAno.class, null) != null).map(ZType::new).collect(Collectors.toList());
	}


	public Map<ZType, List<ZType.ZMethod>> getMapZTypes() {
		return getAllZTypes().stream().collect(Collectors.toMap(t -> t, t -> t.getAllZMethods()));
	}

	public List<ZType> getAllZTypes() {
		Supplier<List<ZType>> findAllGet = () -> {
			String[] packages = X.empty(this.packages) ? ARR.EMPTY_ARGS : this.packages;
			return jarFile != null ? findAll(jarFile, packages) : findAll(packages);
		};
		return zTypesCached != null ? zTypesCached : (zTypesCached = findAllGet.get());
	}

	@Override
	public String toString() {
		return "ZJar:" + jarFile + ":" + (zTypesCached == null ? "undefined" : zTypesCached);
	}

	public String name() {
		return jarFile.getFileName().toString();
	}

	public Object invokeWithArgs1(String methodName, Object arg1) {
		return invokeWithArgs(methodName, new Object[]{arg1});
	}

	public Object invokeWithArgs(String methodName, Object... args) {
		List<Class> allPackageClassViaClassgraph;
		if (jarFile == null) {
			allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(packages, ZType.ZTypeAno.class);
		} else {
			allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(jarFile, ZType.ZTypeAno.class, packages);
		}
		IT.notEmpty(allPackageClassViaClassgraph, "Not found method '%s', JAR:%s", methodName, jarFile);
		ZType zType = new ZType(allPackageClassViaClassgraph.get(0));
		return zType.invokeWithArgs(methodName, args);
	}


}
