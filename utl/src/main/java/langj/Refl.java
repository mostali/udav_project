package langj;

import mpu.X;
import mpu.core.ARG;
import mpu.IT;
import mpc.types.abstype.AbsType;
import mpc.exception.RequiredRuntimeException;
import mpc.rfl.RFL;
import mpc.str.ObjTo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Refl {


	public static void main(String[] args) {
//		Object Math=null;
		Math.min(1, 2);
	}

	public static <T> T inst(Class<T> clazz, List<AbsType> args, T... defRq) {
		try {
			return inst_(clazz, args);
		} catch (Exception e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			return X.throwException(e);
		}
	}

	public static <T> T inst_(Class<T> clazz, List<AbsType> args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		IT.notEmpty(args);
		Class[] conInTypes = new Class[args.size()];
		Object[] objs = new Object[conInTypes.length];
		for (int i = 0; i < objs.length; i++) {
			AbsType absType = args.get(i);
			conInTypes[i] = absType.type();
			objs[i] = absType.val();
		}
		Constructor constructor = findConstructor(clazz, conInTypes);
		Object[] conTypes = cast(args.toArray(new AbsType[0]), constructor.getParameterTypes());
		return (T) constructor.newInstance(conTypes);
	}

	private static Object[] cast(AbsType[] absTypes, Class[] parameterTypes) {
		IT.isEq(absTypes.length, parameterTypes.length);
		Object[] objs = new Object[absTypes.length];
		for (int i = 0; i < objs.length; i++) {
			objs[i] = cast(absTypes[i], parameterTypes[i]);
		}
		return objs;
	}

//	private final static Object NULL = new Object();

	private static Object cast(AbsType obj, Class parameterType) {
		Object newObj = ObjTo.convertObjectToObject(obj.type(), obj.val(), parameterType);
		return newObj;
//		return ObjTo.anyTo(obj, parameterType);
	}

	public static Constructor findConstructor(Class clazz, Class[] types) {
		Constructor constructor = RFL.con(clazz, types, null);
		if (constructor != null) {
			return constructor;
		}
		List<Class[]> variants = findAllVariants(types);
		for (Class[] variantTypes : variants) {
			constructor = RFL.con(clazz, variantTypes, null);
			if (constructor != null) {
				return constructor;
			}
		}
		throw new RequiredRuntimeException("Constructor not found from class '" + clazz + "', types (%s) >>> %s", types.length, Arrays.asList(types));
	}

	private static List<Class[]> findAllVariants(Class[] types) {
		Map<Class, List<Class>> all = new LinkedHashMap<>();
		for (Class clazz : types) {
			List<Class> classVariants = findClassVariants(clazz);
			all.put(clazz, classVariants);
		}
		return createAllClassVariants(all);
	}

	private static List<Class[]> createAllClassVariants(Map<Class, List<Class>> all) {
		List list = new LinkedList();
		int lev = -1;
		for (Map.Entry<Class, List<Class>> entry : all.entrySet()) {
			lev++;
			list.addAll(createAllClassVariants(lev, entry, all));
		}
		return list;
	}

	private static List<Class[]> createAllClassVariants(int level, Map.Entry<Class, List<Class>> entry0, Map<Class, List<Class>> all) {
		List<Class[]> list = new LinkedList();
		for (Class single0 : entry0.getValue()) {
			Class[] types = new Class[all.size()];
			int lev = -1;
			for (Map.Entry<Class, List<Class>> entryAll : all.entrySet()) {
				lev++;
				if (level == lev) {
					types[lev] = single0;
				} else {
//					types[lev] = entry0.getKey();
					types[lev] = entryAll.getKey();
				}
			}
			list.add(types);
		}
		return list;
	}

	private static List<Class> findClassVariants(Class clazz) {
		if (clazz == Long.class) {
			return Arrays.asList(Long.class, long.class, Integer.class, int.class, Short.class, short.class, Byte.class, byte.class);
		} else if (clazz == Integer.class) {
			return Arrays.asList(Integer.class, int.class, Long.class, long.class, Short.class, short.class, Byte.class, byte.class);
		}
		return Arrays.asList(clazz);
	}
}
