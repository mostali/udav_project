package mpf.zcall;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.arr.NaturalOrderComparator;
import mpc.arr.STREAM;
import mpc.exception.RequiredRuntimeException;
import mpc.rfl.RFL;
import mpe.call_msg.ILongCall;
import mpe.call_msg.JarCallMsg;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
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
import java.util.Optional;
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

	public static ZType findLast(List<ZType> zTypes) {
		return zTypes.stream().sorted((z1, z2) -> z1.isVersionAfter(z2.getAppVersion()[1]) ? 1 : -1).findFirst().get();
	}

	public static ZType getZTypeByVersion(List<ZType> zTypes, String version) {
		return zTypes.stream().filter(t -> version.equals(t.getAppVersion()[1])).findFirst().get();
	}

	public static String findLastVersion(List<ZType> zTypes) {
		List<String> versions = STREAM.mapToList(zTypes, t -> t.getAppVersion()[1]);
		String last = findLastVersionIn(versions);
		return last;
	}

	public static String findLastVersionIn(List<String> versions) {
		return versions.stream().sorted(NaturalOrderComparator.NUMERICAL_ORDER).findFirst().get();
	}

	@Override
	public String toString() {
		Optional<Annotation> first = Arrays.stream(zType.getDeclaredAnnotations()).filter(t -> t instanceof ZTypeAno).findFirst();
		String[] appVersion = getAppVersion();
		return "ZType:" + zType.getSimpleName() + " / " + appVersion[0] + "@" + appVersion[1];
	}

	public String[] getAppVersion() {
		Optional<Annotation> first = Arrays.stream(zType.getDeclaredAnnotations()).filter(t -> t instanceof ZTypeAno).findFirst();
		ZTypeAno zTypeAno = ZType.castToZTypeAno(first.get());
		return new String[]{zTypeAno.app(), zTypeAno.version()};
	}

	static ZTypeAno castToZTypeAno(Annotation annotation) {
		return (ZTypeAno) annotation;
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
		List<ZMethod> allZMetods = zMethods != null ? zMethods : (zMethods = findZMethods(ZType.ZCALL_TYPES_ALL));
		IT.state(allZMetods.size() == allZMetods.stream().map(m -> m.zMethod.getName()).collect(Collectors.toSet()).size());
		return allZMetods;
	}

	@SneakyThrows
	public List<ZMethod> findZMethods(Class... zcallArgTypes) {
		List<Method> methods = RFL.methods_(false, zType, IT.NE(zcallArgTypes), null, true, true, false, ARR.EMPTY_LIST);
		return STREAM.mapToList(methods, ZMethod::new);
	}

	public ZMethod getZMethod_ByName_FirstUniq(String methodName) {
		return getAllZMethods().stream().filter(m -> m.name().equals(methodName)).findFirst().orElse(null);
	}

	@SneakyThrows
	public ZMethod getZMethod_ByName_FirstAny(String methodName, ZMethod... defRq) {
		Method method = RFL.method(zType, methodName, null, true, true, true, null);
		return method != null ? new ZMethod(method) : ARG.toDefThrowMsg(() -> X.f("Method '%s' not found from type '%s'", methodName, zType), defRq);
	}

	public Object invokeWithArgs(String methodName, Object... args) {
		ZType.ZMethod zMethod = this.getZMethod_ByName_FirstAny(methodName);
		return zMethod.invokeWithArgs(args);
	}

	public Object invokeWithArgs1(String methodName, Object arg1) {
		ZType.ZMethod zMethod = this.getZMethod_ByName_FirstAny(methodName);
		return zMethod.invokeWithArgs1(arg1);
	}

	public boolean isVersionAfter(String s) {
		return s.compareTo(getAppVersion()[1]) == 1;
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
					if (ILongCall.class.isAssignableFrom(pType)) {
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
			return "ZCall:" + zMethod.getName() + "//" + getZArgs();
		}

		private final Method zMethod;

		@SneakyThrows
		public Object invokeWithArgs1(Object arg1) {
			return zMethod.invoke(null, arg1);
		}

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

		final Parameter parameter;

		public ZArgAno getZAnoFirst(ZArgAno... defRq) {
			return (ZArgAno) ARRi.first(getAllZArgAno(), defRq);
		}

		public List<ZArgAno> getAllZArgAno() {
			Annotation[] declaredAnnotations = parameter.getDeclaredAnnotations();
			return (List) Arrays.stream(declaredAnnotations).filter(a -> RFL.ANO.isAnnotationIsAssignableFrom(a, ZArgAno.class)).collect(Collectors.toList());
		}

		public ZArgAno geZArgAnoFirst(ZArgAno... defRq) {
			Optional<ZArgAno> first = (Optional) Arrays.stream(parameter.getDeclaredAnnotations()).filter(a -> RFL.ANO.isAnnotationIsAssignableFrom(a, ZArgAno.class)).findFirst();
			return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("Except zArgAno in parameter %s", parameter), first, defRq);
		}

		@Override
		public String toString() {
			ZArgAno zArgAno = geZArgAnoFirst(null);
			Object anoVal = zArgAno == null ? zArgAno : zArgAno.value();
//			return getClass().getSimpleName() + "*" + name() + "=" + anoVal + "<-" + parameter.getType().getSimpleName();
			String type = "::" + parameter.getType().getSimpleName();
			return getClass().getSimpleName() + type + "(" + anoVal + ")";
		}

		public static ZArg of(Parameter parameter) {
			return new ZArg(parameter);
		}

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
		String app();

		String version() default "latest";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface ZMethodAno {
	}

	//
	//

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface ZArgAno {
		String value();
		//Class type() default String.class;
	}

}
