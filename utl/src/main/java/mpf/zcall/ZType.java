package mpf.zcall;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpc.rfl.RFL;
import mpe.wthttp.JarCallMsg;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ARRi;

import java.io.IOException;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ZType implements ZEntity {

	public static final Class[] ZCALL_TYPES_ALL = {ZMethodAno.class};//, ZCallMap.class, ZCallVoid.class, ZCallString.class

	public ZType(Class zType) {
		this.zType = IT.NN(zType, "set z-type");
	}

	public static boolean hasZMethods(Class classFromJar) {
		return X.notEmpty(new ZType(classFromJar).getAllZMethods());
	}

	@SneakyThrows
	public static ZType of(Path jarPath, String className) {
		Class classFromJar = RFL.loadClassFromJar(jarPath, className);
		ZType zType = new ZType(classFromJar);
		return zType;
	}

	@Override
	public String toString() {
		return "ZType:" + zType.getSimpleName();
	}

	public static void main(String[] args) throws IOException {
		Path jarFile = Paths.get("/opt/appVol/.bin/jira-mod.jar");
		ZJar zJar = ZJar.of(Paths.get("/opt/appVol/.bin/jira-mod.jar"), "mp.jira");
		List<ZType> ztypes = zJar.getAllZTypes();
		ztypes.forEach(zt -> {
			List<ZMethod> allZMethods = zt.getAllZMethods();
			X.p(allZMethods);
			X.exit(allZMethods.get(2).getZArgs());
		});
//		Map<ZType, List<ZMethod>> ztypes = zJar.getMapZTypes();
		X.exit(ztypes);


		String[] strings = {"mp.jira"};
		List<ZType> allPackageClassViaClassgraph = ZJar.findAll(jarFile, strings);
		List<ZMethod> zCalls = allPackageClassViaClassgraph.get(0).getAllZMethods();
		X.exit(zCalls);
//		List<Class> allPackageClassViaClassgraph = UReflScanner.getAllPackageClassViaClassgraph(Paths.get("/opt/appVol/.bin/jira-mod.jar"), strings);
//		ZType zType = new ZType(allPackageClassViaClassgraph.get(0));
//		List<ZType.ZCall0> calls = zType.getZCall(ZType.ZCallArgs.class);
//		X.exit(calls.get(0).call(new String[]{"-task", "SUP-1495556"}));
//		X.exit(calls.get(0).call(new String[]{"-task", "*"}));
		X.exit(allPackageClassViaClassgraph);
	}

	public String name() {
		return zType.getSimpleName();
	}

	//
	//

	public final Class zType;

	List<ZMethod> zMethods;

	public List<ZMethod> getAllZMethods() {
		return zMethods != null ? zMethods : (zMethods = findZMethods(ZType.ZCALL_TYPES_ALL));
	}

	@SneakyThrows
	public List<ZMethod> findZMethods(Class... zcallArgTypes) {
		List<Method> methods = RFL.methods_(false, zType, IT.NE(zcallArgTypes), null, true, true, false, ARR.EMPTY_LIST);
		return STREAM.mapToList(methods, ZMethod::new);
	}

	public ZMethod getMethodByName(String className) {
		return getAllZMethods().stream().filter(m -> m.name().equals(className)).findFirst().orElse(null);
	}


	@RequiredArgsConstructor
	public static class ZMethod implements ZEntity {

		public static Object invokeCallMsg(JarCallMsg jcm, ZMethod zMethod) {
			List<ZArg> zArgs = zMethod.getZArgs();

			List args = new ArrayList<>();

			for (int i = 0; i < zArgs.size(); i++) {

				ZArg zArg = zArgs.get(i);

				ZArgAno zAnoFirst = zArg.getZAnoFirst(null);
				if (zAnoFirst == null) {
					Class pType = zArg.getParameterType();
					if (pType == JarCallMsg.class) {
						args.add(jcm);
						continue;
					}
				}
				//				String val = (String) RFL.fieldValue(zAnoFirst, "value", false);
				//				String headerParam = jcm.getHeaderParam(zArg.name(), null);
				String headerParam = jcm.getHeaderParam(zAnoFirst.value(), null);

				args.add(IT.NN(headerParam, "Set 'task' param in node header"));
			}

			return zMethod.invokeWithArgs(args.toArray());
		}

		@Override
		public String toString() {
			return "ZCall:" + zMethod.getName();
		}

		private final Method zMethod;

		@SneakyThrows
		public Object invokeWithArgs(Object... args) {
			return zMethod.invoke(null, args);
		}

		public String name() {
			return zMethod.getName();
		}

		public List<ZType.ZArg> getZArgs() {
//			if(true){
			return STREAM.of(zMethod.getParameters()).map(ZArg::of).collect(Collectors.toList());
//			}
//			Parameter[] parameters = zMethod.getParameters();
//			if (X.empty(parameters)) {
//				return ARR.EMPTY_LIST;
//			}
//			for (Parameter parameter : parameters) {
//				ZArg zArg = ZArg.buildZArgFromParam(parameter);
////				if (zArg == null) {
////					zArg.
////					Annotation[] declaredAnnotations = parameter.getDeclaredAnnotations();
////				}
//			}
//			Annotation[] declaredAnnotations = parameters[0].getDeclaredAnnotations();
//			return Arrays.stream(declaredAnnotations).filter(a -> RFL.ANO.isEqualsAnnotation_byClassName(ZArgAno.class, a)).map(a -> ZArg.of((ZArgAno) a)).collect(Collectors.toList());
		}
	}

	@RequiredArgsConstructor
	public static class ZArg implements ZEntity {

//		public static ZArg buildZArgFromParam(Parameter parameter) {
//			Annotation[] declaredAnnotations = parameter.getDeclaredAnnotations();
//			List<ZArg> collect = Arrays.stream(declaredAnnotations).filter(a -> RFL.ANO.isEqualsAnnotation_byClassName(ZArgAno.class, a)).map(a -> ZArg.of((ZArgAno) a)).collect(Collectors.toList());
//			return ARRi.first(collect, null);
//		}

		final Parameter parameter;

		public ZArgAno getZAnoFirst(ZArgAno... defRq) {
			return (ZArgAno) ARRi.first(getZAno(), defRq);
		}

		public List<Annotation> getZAno() {
			Annotation[] declaredAnnotations = parameter.getDeclaredAnnotations();
			return Arrays.stream(declaredAnnotations).filter(a -> RFL.ANO.isAnnotationIsAssignableFrom(a, ZArgAno.class)).collect(Collectors.toList());
		}

		@Override
		public String toString() {
			return "ZArg:" + name();
		}

//		private final ZArgAno zArgAno;

		public static ZArg of(Parameter parameter) {
			return new ZArg(parameter);
		}
//		@SneakyThrows
//		public Object call(String[] args) {
//			return zMethod.invoke(null, new Object[]{args});
//		}

		public String name() {
			return parameter.getName();
		}

		public Class getParameterType() {
			return parameter.getType();
		}
	}
	//
	//

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ZTypeAno {
	}

	//
	//

//	@Retention(RetentionPolicy.RUNTIME)
//	@Target(ElementType.METHOD)
//	public @interface ZCallString {
//	}

//	@Retention(RetentionPolicy.RUNTIME)
//	@Target(ElementType.METHOD)
//	public @interface ZCallVoid {
//	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface ZMethodAno {
	}

//	@Retention(RetentionPolicy.RUNTIME)
//	@Target(ElementType.METHOD)
//	public @interface ZCallMap {
//	}

	//
	//

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface ZArgAno {
		String value();

//		Class type() default String.class;
	}

}
