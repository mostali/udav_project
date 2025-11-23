package mpc.rfl;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpc.env.Env;
import mpc.types.abstype.AbsType;
import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.core.ARR;
import mpu.IT;
import mpu.core.EQ;
import mpe.core.UBool;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.map.MAP;
import mpu.str.STR;
import mpc.str.condition.StringConditionPattern;
import mpu.Sys;
import mpu.X;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;

//Reflection
//St = Static
//методы с *_ = checked exception's
public class RFL {

	public static final Class[] MAIN_PARAMETER_TYPES = {new String[0].getClass()};
	public static final Object[] MAIN_PARAMTER_OBJECTS = {new String[0]};

//	public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
//		Path jar = Env.RL_PJM.resolve("utl-gdb/target").resolve("gdb-mod.jar");
//		try {
//			invokeJarSt_(jar, "mp.utl_gdb.GdbMod", "main", MAIN_PARAMETER_TYPES, MAIN_PARAMTER_OBJECTS);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}

	/// /		test(null);
//	}


	/**
	 * *************************************************************
	 * ----------------------- Write To Field ----------------------
	 * *************************************************************
	 */

	public static void writeSt(Class clazz, String fieldName, Object fieldValue, boolean isFullSearch, boolean modifyAccessibleToTrue, boolean... noThrowError) {
		try {
			writeSt_(clazz, fieldName, fieldValue, isFullSearch, modifyAccessibleToTrue);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			if (ARG.isDefEqTrue(noThrowError)) {
				return;
			}
			throw new RequiredRuntimeException(ex, "Write ST '%s', fullSearch '%s', modifyAccessibleToTrue '%s', class '%s'", fieldName, isFullSearch, modifyAccessibleToTrue, clazz);
		}
	}

	public static void write(Object obj, String fieldName, Object fieldValue, boolean isFullSearch, boolean modifyAccessibleToTrue, boolean... noThrowError) {
		try {
			write_(obj, fieldName, fieldValue, isFullSearch, modifyAccessibleToTrue);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			if (ARG.isDefEqTrue(noThrowError)) {
				return;
			}
			throw new RequiredRuntimeException(ex, "Write '%s', fullSearch '%s', modifyAccessibleToTrue '%s', object '%s'", fieldName, isFullSearch, modifyAccessibleToTrue, obj);
		}
	}

	public static void writeSt_(Class clazz, String fieldName, Object fieldValue, boolean isFullSearch, boolean modifyAccessibleToTrue) throws NoSuchFieldException, IllegalAccessException {
		Field f = fieldSt_(clazz, fieldName, isFullSearch, modifyAccessibleToTrue);
		f.set(null, fieldValue);
	}

	public static void write_(Object object, String fieldName, Object fieldValue, boolean isFullSearch, boolean modifyAccessibleToTrue) throws IllegalAccessException, NoSuchFieldException {
		Field f = field_(object, fieldName, isFullSearch, modifyAccessibleToTrue);
		f.set(object, fieldValue);
	}

	/**
	 * *************************************************************
	 * ------------------------- Read Field ------------------------
	 * *************************************************************
	 */
	public static Object readSt(Class clazz, String fieldName, boolean isFullSearch, boolean modifyAccessibleToTrue, Object... defRq) {
		try {
			return readSt_(clazz, fieldName, isFullSearch, modifyAccessibleToTrue);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(ex, "Read ST '%s', fullSearch '%s', class '%s'", fieldName, isFullSearch, clazz);
		}
	}

	public static Object readSt_(Class clazz, String fieldName, boolean isFullSearch, boolean modifyAccessibleToTrue) throws NoSuchFieldException, IllegalAccessException {
		return fieldSt_(clazz, fieldName, isFullSearch, modifyAccessibleToTrue).get(null);
	}

	public static Object read(Object object, String fieldName, boolean isFullSearch, boolean modifyAccessibleToTrue, Object... defRq) {
		try {
			return read_(object, fieldName, isFullSearch, modifyAccessibleToTrue);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(ex, "Read '%s', fullSearch '%s', object '%s'", fieldName, isFullSearch, object);
		}
	}

	public static Object read_(Object object, String fieldName, boolean isFullSearch, boolean modifyAccessibleToTrue) throws IllegalAccessException, NoSuchFieldException {
		return field_(object, fieldName, isFullSearch, modifyAccessibleToTrue).get(object);
	}

	public static Object readSt_(Object object, String fieldName, boolean isFullSearch, boolean modifyAccessibleToTrue) throws IllegalAccessException, NoSuchFieldException {
		return fieldSt_(object.getClass(), fieldName, isFullSearch, modifyAccessibleToTrue).get(object);
	}

	/**
	 * *************************************************************
	 * -------------------------- Get METHOD ------------------------
	 * *************************************************************
	 */

	public static Method methodAny(Object object, String methodName, boolean modifyAccessibleToTrue, Method... defRq) {
		try {
			return method_(object.getClass(), methodName, null, true, modifyAccessibleToTrue);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static Method method(Class clazz, String methodName, Class[] paramTypes_Or_MbAnyPAramas, Boolean isStaticMethod, boolean isFullSearch, boolean modifyAccessibleToTrue, Method... defRq) {
		try {
			return paramTypes_Or_MbAnyPAramas == null ? method_(clazz, methodName, isStaticMethod, true, modifyAccessibleToTrue) : method_(clazz, methodName, paramTypes_Or_MbAnyPAramas, isStaticMethod, true, modifyAccessibleToTrue);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static Method method_(Class clazz, String methodName, Boolean isStaticMethod, boolean isFullSearch, boolean modifyAccessibleToTrue) throws NoSuchMethodException {
		while (clazz != null) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (!method.getName().equals(methodName)) {
					continue;
				}
				if (!isMaybeMethodByStaticPreidicate(isStaticMethod, method)) {
					continue;
				}
				if (modifyAccessibleToTrue && !method.isAccessible()) {//f.canAcessible(from)
					method.setAccessible(true);
				}
				return method;
			}
			if (!isFullSearch) {
				break;
			}
			clazz = clazz.getSuperclass();
		}
		throw new NoSuchMethodException(STR.f("Class [%s]. Method%s [%s]. FullSearch [%s]. Not found.", clazz, getStaticLogMark(isStaticMethod), methodName, isFullSearch));
	}

	public static Method method_(Class clazz, String methodName, Class[] parameterTypes, Boolean isStaticMethod, boolean isFullSearch, boolean modifyAccessibleToTrue) throws NoSuchMethodException {
		Class clazzOrg = clazz;
		while (clazz != null) {
			out:
			try {
				Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
				if (!isMaybeMethodByStaticPreidicate(isStaticMethod, method)) {
					clazz = clazz.getSuperclass();
					break out;
				}
				if (ARG.isDefEqTrue(modifyAccessibleToTrue) && !method.isAccessible()) {//f.canAcessible(from)
					method.setAccessible(true);
				}
				return method;
			} catch (NoSuchMethodException ex) {
				clazz = clazz.getSuperclass();
			}
			if (!isFullSearch) {
				break;
			}
		}
		throw new NoSuchMethodException(STR.f("Class [%s]. Method%s [%s]. WithParams [%s]. FullSearch [%s]. Not found.", clazz, isStaticMethod ? "(St)" : "", methodName, Arrays.asList(parameterTypes), isFullSearch));
	}


	public static List<Method> methods_(Class clazz, Class[] withAnnotation, Class[] parameterTypes, Boolean isStaticMethod, boolean isFullSearch, boolean modifyAccessibleToTrue) throws NoSuchMethodException {
		return methods_(false, clazz, withAnnotation, parameterTypes, isStaticMethod, isFullSearch, modifyAccessibleToTrue);
	}


	public static List<Method> methods_(boolean onlyFirst, Class clazz, Class[] withAnnotation, Class[] parameterTypes, Boolean isStaticMethod, boolean isFullSearch, boolean modifyAccessibleToTrue, List<Method>... defRq) throws NoSuchMethodException {
		IT.NN(clazz, "set clazz");
		List<Method> methods = new LinkedList<>();
		Class clazzOrg = clazz;
		while (clazz != null) {
			Method[] methods0 = clazz.getDeclaredMethods();
			for (Method method : methods0) {
				if (!isMaybeMethodByStaticPreidicate(isStaticMethod, method)) {
					continue;
				}
				if (ARG.isDefEqTrue(modifyAccessibleToTrue) && !method.isAccessible()) {//f.canAcessible(from)
					method.setAccessible(true);
				}
				if (X.notEmpty(withAnnotation)) {
					List<Annotation> anoFromMethod = ANO.getAnoFromMethod(method, withAnnotation, null);
					if (anoFromMethod == null) {
						continue;
					}
				}
				methods.add(method);
				if (onlyFirst) {
					return methods;
				}
			}
			if (!isFullSearch) {
				break;
			}
			clazz = clazz.getSuperclass();
		}
		if (X.notEmpty(methods)) {
			return methods;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new NoSuchMethodException(X.f("Class [%s]. Ano[%s]. WithParams [%s]. FullSearch [%s]. Not found.", //
				clazzOrg, isStaticMethod ? "(St)" : "", STREAM.mapToList(ARR.asSafeNPE(withAnnotation), //
						RFL::scn), STREAM.mapToList(ARR.asSafeNPE(parameterTypes), RFL::scn), //
				isFullSearch));
	}

	public static class ANO {

		public static List<Annotation> getAnoFromMethod(Method method, Class[] needleAnnotations, List<Annotation>... defRq) {
			List<Annotation> finded = new LinkedList<>();
			for (Class aClass : IT.notEmpty(needleAnnotations)) {
				if (true) {
					Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
					Optional<Annotation> first = Arrays.stream(declaredAnnotations).filter(a -> isAnnotationIsAssignableFrom(a, aClass)).findFirst();
					if (first.isPresent()) {
						finded.add(first.get());
					}

				} else {
					Annotation annotation = method.getAnnotation(aClass);
					if (annotation != null) {
						finded.add(annotation);
					}
				}
			}
			//		if (finded.size() == needleAnnotations.length) {
			if (X.notEmpty(finded)) {
				return finded;
			}
			return ARG.toDefThrowMsg(() -> X.f("Method [%s] will be contain all annotation [%s] vs [%s]", method, ARR.as(needleAnnotations), finded), defRq);

		}

		public static List<Annotation> getAnoFromMethodParameter(Method method, Class[] needleAnnotations, List<Annotation>... defRq) {
			List<Annotation> finded = new LinkedList<>();
			for (Class aClass : IT.notEmpty(needleAnnotations)) {
				if (true) {
					Annotation[][] declaredAnnotations = method.getParameterAnnotations();
					Optional<Annotation> first = Arrays.stream(declaredAnnotations).flatMap(pa -> Arrays.stream(pa)).filter(a -> isAnnotationIsAssignableFrom(a, aClass)).findFirst();
					if (first.isPresent()) {
						finded.add(first.get());
					}

				} else {
					Annotation annotation = method.getAnnotation(aClass);
					if (annotation != null) {
						finded.add(annotation);
					}
				}
			}
			if (X.notEmpty(finded)) {
				return finded;
			}
			return ARG.toDefThrowMsg(() -> X.f("Method parameter [%s] will be contain all annotation [%s] vs [%s]", method, ARR.as(needleAnnotations), finded), defRq);

		}

		public static boolean isAnnotationIsAssignableFrom(Annotation a, Class aClass) {
			if (a.annotationType() == aClass) {
				return true;
			}
//			else if (a.annotationType().getName().equals(aClass.getName())) {
//				return true;
//			}
			return false;
		}

		@Deprecated// need add simple equals
		public static boolean isFieldWithAnotation(Field f, List<Class> ano) {
			for (Class anoClass : ano) {
				if (f.getAnnotation(anoClass) != null) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * *************************************************************
	 * -------------------------- Get Field ------------------------
	 * *************************************************************
	 */

	public static Field field(Object object, String fieldName, boolean isFullSearch, boolean modifyAccessibleToTrue, Field... defRq) {
		try {
			return field_(object, fieldName, isFullSearch, modifyAccessibleToTrue);
		} catch (NoSuchFieldException e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static Field fieldSt(Class clazz, String fieldName, boolean isFullSearch, boolean modifyAccessibleToTrue, Field... defRq) {
		try {
			return fieldSt_(clazz, fieldName, isFullSearch, modifyAccessibleToTrue);
		} catch (Exception e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("Field with name [%s] not found from Class [%s]", fieldName, clazz);
		}
	}

	public static Field field_(Object object, String fieldName, boolean isFullSearch, boolean modifyAccessibleToTrue) throws NoSuchFieldException {
		return field_(object.getClass(), fieldName, isFullSearch, modifyAccessibleToTrue);
	}

	public static Field field_(Class clazz, String fieldName, boolean isFullSearch, boolean modifyAccessibleToTrue) throws NoSuchFieldException {
		return fieldFind_(clazz, null, null, fieldName, null, false, isFullSearch, modifyAccessibleToTrue);
	}

	public static Field fieldSt_(Class clazz, String fieldName, boolean isFullSearch, boolean modifyAccessibleToTrue) throws NoSuchFieldException {
		return fieldFind_(clazz, null, null, fieldName, null, true, isFullSearch, modifyAccessibleToTrue);
	}

	public static List<Field> fields(Class clazz, Class fieldType, List... defRq) {
		return fields(clazz, fieldType, null, defRq);
	}

	public static List<Field> fields(Class clazz, List<Class> ano, List... defRq) {
		return fields(clazz, null, ano, defRq);
	}

	public static List<Field> fields(Class clazz, Class fieldType, List<Class> ano, List... defRq) {
		try {
			return fieldsFind_(clazz, fieldType, ano);
		} catch (NoSuchFieldException e) {
			return ARG.toDefThrow(e, defRq);
		}
	}


	//--------------------------------
	private static Field fieldFind_(Class clazz, Class fieldType, List<Class> ano, String fieldName, StringConditionPattern fieldNameStringCondition, Boolean isStatic, boolean isFullSearch, boolean modifyAccessibleToTrue) throws NoSuchFieldException {
		List<Field> first = fieldsFind_(clazz, fieldType, ano, IT.NN(fieldName), fieldNameStringCondition, isStatic, isFullSearch, true, modifyAccessibleToTrue);
		return first.get(0);
	}

	private static Field fieldFind_(Class clazz, String fieldName, Boolean isStatic, boolean isFullSearch, boolean modifyAccessibleToTrue) throws NoSuchFieldException {
		List<Field> first = fieldsFind_(clazz, null, null, IT.NN(fieldName), null, isStatic, isFullSearch, true, modifyAccessibleToTrue);
		return first.get(0);
	}

	public static AbsFieldType fieldAbsType_(Class clazz, Object from, String fieldName, Boolean isStatic_orNot_orAny, boolean ifFullSearch, boolean modifyAccessibleToTrue) throws NoSuchFieldException {
		if (from == null) {
			IT.state(clazz != null);
			IT.state(UBool.isTrueSafe(isStatic_orNot_orAny));
		}
		Field field = fieldFind_(clazz == null ? from.getClass() : clazz, fieldName, isStatic_orNot_orAny, ifFullSearch, modifyAccessibleToTrue);
		return new AbsFieldType(field, from);
	}

	public static String scn(Object obj, String... defRq) {
		return obj != null ? obj.getClass().getSimpleName() : ARG.toDefThrow(() -> new RequiredRuntimeException("Object is NULL"), defRq);
	}

	public static String scn(Class clazz, String... defRq) {
		return clazz != null ? clazz.getSimpleName() : ARG.toDefThrow(() -> new RequiredRuntimeException("Class is NULL"), defRq);
	}

	public static <T> T getClassAnnotation(Class clazz, Class ano, T... defRq) {
		T annotation = (T) clazz.getAnnotation(ano);
		return annotation != null ? annotation : ARG.toDefThrow(() -> new RequiredRuntimeException("Except '%s' ano from page class '%s'", ano, clazz), defRq);
	}

	public static String threadName() {
		return Thread.currentThread().getName();
	}

	public static Class convertPrimitiveClassToWrapperClass(Class primitiveClass, Class... defRq) {
//		Primitives.wrap()
		if (primitiveClass == Integer.TYPE) {
			return Integer.class;
		} else if (primitiveClass == Long.TYPE) {
			return Long.class;
		} else if (primitiveClass == Boolean.TYPE) {
			return Boolean.class;
		} else if (primitiveClass == Double.TYPE) {
			return Double.class;
		} else if (primitiveClass == Character.TYPE) {
			return Character.class;
		} else if (primitiveClass == Float.TYPE) {
			return Float.class;
		} else if (primitiveClass == Byte.TYPE) {
			return Byte.class;
		} else if (primitiveClass == Short.TYPE) {
			return Short.class;
		} else if (primitiveClass == Void.TYPE) {
			return Void.class;
		}
		return ARG.toDefThrowMsg(() -> X.f("Class '%s' is not primitive class", primitiveClass.getName()), defRq);
	}

	public static String pn(Class clazz) {
		return clazz.getPackage().getName();
	}

	@RequiredArgsConstructor
	public static class FieldValue<T> {
		public final Class<T> type;
		public T value;
		boolean isStatic = true;
		Object from = null;

		public FieldValue setValue(T value) {
			this.value = value;
			return this;
		}

		public T get_(Object object, Field field) throws IllegalAccessException {
			return (T) field.get(object);
		}

		public T getSt_(Field field) throws IllegalAccessException {
			return (T) field.get(null);
		}

		public boolean isEquals(Field f, boolean... defRq) {
			try {
				return isEquals_(f);
			} catch (Exception ex) {
				return ARGn.toDefOrThrow(ex, defRq);
			}
		}

		public boolean isEquals_(Field f) throws IllegalAccessException {
			if (isStatic) {
				return EQ.equalsSafe(value, getSt_(f));
			} else {
				return EQ.equalsSafe(value, get_(from, f));
			}
		}
	}

	private static List<Field> fieldsFind_(Class clazz, Class fieldType, List<Class> ano) throws NoSuchFieldException {
		return fieldsFind_(clazz, fieldType, ano, null, null, null, true, false, true);
	}

	private static List<Field> fieldsFind_(Class clazz, Class fieldType, List<Class> ano, String fieldName, StringConditionPattern fieldNameStringCondition, Boolean isStatic, boolean isFullSearch, boolean onlyFirst, boolean modifyAccessibleToTrue) throws NoSuchFieldException {
		return fieldsFind_(clazz, new FindFieldsPredicate(fieldType, ano, fieldName, fieldNameStringCondition), isStatic, isFullSearch, onlyFirst, modifyAccessibleToTrue);
	}

	private static List<Field> fieldsFind_(Class clazz, FindFieldsPredicate findFieldsPredicate, boolean isFullSearch, boolean onlyFirst, boolean modifyAccessibleToTrue) throws NoSuchFieldException {
		return fieldsFind_(clazz, findFieldsPredicate, null, isFullSearch, onlyFirst, modifyAccessibleToTrue);
	}

	private static List<Field> fieldsFind_(Class clazz, FindFieldsPredicate findFieldsPredicate, Boolean isStaticFields, boolean isFullSearch, boolean onlyFirst, boolean modifyAccessibleToTrue) throws NoSuchFieldException {
		Class clazz_ = clazz;
		List<Field> all = new ArrayList<>();
		while (clazz != null) {
			for (Field f : clazz.getDeclaredFields()) {
				//it already in ff-predicate
				if (!isMaybeFieldByStaticPreidicate(isStaticFields, f)) {
					continue;
				}
				if (findFieldsPredicate != null && !findFieldsPredicate.test(f)) {
					continue;
				}
				if (modifyAccessibleToTrue && !f.isAccessible()) {//f.canAcessible(from)
					f.setAccessible(true);
				}
				all.add(f);
				if (onlyFirst) {
					return all;
				}
			}
			if (!isFullSearch) {
				break;
			}
			clazz = clazz.getSuperclass();
		}
		if (all.isEmpty()) {
			throw newNoSuchFieldException("Field with name [%s] not found from Class [%s]", clazz_, findFieldsPredicate.fieldName);
		}
		return all;
	}

	/**
	 * *************************************************************
	 * -------------------- Find Fieldname By Object ---------------
	 * *************************************************************
	 */
	public static <T> T fieldValueSet(Object obj, String fieldname, T value, boolean fullSearch, T... defRq) {
		try {
			RFL.field_(obj, fieldname, fullSearch, true).set(obj, value);
			return value;
		} catch (IllegalAccessException | NoSuchFieldException e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(e, "SET Field-Value not found from Object-Class [%s] with Field-Name [%s], FullSearch[%s", scn(obj, null), fieldname, fullSearch);
		}
	}

	public static <T> T fieldValueStSet(Class clazz, String fieldname, T value, boolean fullSearch, T... defRq) {
		try {
			RFL.fieldSt_(clazz, fieldname, fullSearch, true).set(null, value);
			return value;
		} catch (IllegalAccessException | NoSuchFieldException e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(e, "SET Field-Value ST not found from Class [%s] with Field-Name [%s], FullSearch[%s", clazz, fieldname, fullSearch);
		}
	}

	/**
	 * *************************************************************
	 * -------------------- Find Fieldname By Object ---------------
	 * *************************************************************
	 */
	public static Object fieldValue(Object obj, String fieldname, boolean fullSearch, Field... defRq) {
		try {
			return RFL.field_(obj, fieldname, fullSearch, true).get(obj);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(e, "Field-Value not found from Object-Class [%s] with Field-Name [%s], FullSearch[%s", scn(obj, null), fieldname, fullSearch);
		}
	}

	public static Object fieldValueSt(Class clazz, String fieldname, boolean fullSearch, Object... defRq) {
		try {
			return RFL.fieldSt_(clazz, fieldname, fullSearch, true).get(null);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(e, "Field-Value not found from Class [%s] with Field-Name [%s], FullSearch[%s", clazz, fieldname, fullSearch);
		}
	}

	public static <T> List<T> fieldValuesSt(Class clazz, StringConditionPattern fieldNameStringCondition, boolean fullSearch, List<Object>... defRq) {
		return (List<T>) fieldValuesSt(clazz, null, fieldNameStringCondition, fullSearch, defRq);
	}

	public static List<Object> fieldValues(Object object, StringConditionPattern fieldNameStringCondition, boolean fullSearch, List<Object>... defRq) {
		return fieldValues(null, object, new FindFieldsPredicate(false, null, null, null, fieldNameStringCondition), fullSearch, defRq);
	}

	public static <T> List<T> fieldValuesSt(Class clazz, Class<T> fieldType, boolean fullSearch, List<T>... defRq) {
		return fieldValuesSt(clazz, fieldType, null, fullSearch, defRq);
	}

	public static <T> List<T> fieldValuesSt(Class clazz, Class<T> fieldType, StringConditionPattern fieldNameStringCondition, boolean fullSearch, List<T>... defRq) {
		return fieldValues(clazz, null, new FindFieldsPredicate(true, fieldType, null, null, fieldNameStringCondition), fullSearch, defRq);
	}

	public static <T> List<T> fieldValuesSt(Class clazz, Class<T> fieldType, Boolean isStatic, List<Class> anoTypes, List<T>... defRq) {
		return fieldValues(clazz, null, new FindFieldsPredicate(isStatic, fieldType, anoTypes, null, null), true, defRq);
	}

	public static <T> List<T> fieldValues(Class clazz, Object obj, Class<T> fieldType, boolean fullSearch, List<T>... defRq) {
		return fieldValues(clazz, obj, new FindFieldsPredicate(false, fieldType, null, null, null), fullSearch, defRq);
	}

	public static <T> List<T> fieldValues(Class clazz, Object obj, FindFieldsPredicate predicate, boolean fullSearch, List<T>... defRq) {
		try {
			return fieldValues_(clazz, obj, predicate, fullSearch);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(ex, "Field-Values not found from Class [%s] with Type [%s], FindFieldsPredicate [%s], fullSearch=[%s]", clazz, predicate.fieldType, predicate, fullSearch);
		}
	}

	public static <T> List<T> fieldValues_(Class clazz, Object obj, FindFieldsPredicate predicate, boolean fullSearch) throws NoSuchFieldException, IllegalAccessException {
		List<Field> fields = fieldsFind_(clazz == null ? obj.getClass() : clazz, predicate, fullSearch, false, true);
		List<T> list = new ArrayList<>();
		for (Field field : fields) {
			if (predicate.isStatic) {
				list.add((T) field.get(null));
			} else {
				if (obj == null) {
					throw new FIllegalStateException("Get field value need object typeof '%s'", clazz);
				}
				T e = (T) field.get(obj);
				list.add(e);
			}
		}
		return IT.notEmpty(list);
	}

	@SneakyThrows
	public static <T> Map<String, T> fieldValuesMap(Class clazz, Object obj, Class<T> fieldType, boolean fullSearch, Map<String, T>... defRq) {
		FindFieldsPredicate findFieldsPredicate = new FindFieldsPredicate(obj == null, fieldType, null, null, null);
		List<Field> fields = fieldsFind_(clazz == null ? obj.getClass() : clazz, findFieldsPredicate, fullSearch, false, true);
		Map<String, T> fnMap = new LinkedHashMap<>();
		for (Field field : fields) {
			String fname = field.getName();
			if (findFieldsPredicate.isStatic) {
				fnMap.put(fname, (T) field.get(null));
			} else {
				if (obj == null) {
					throw new FIllegalStateException("Get field value need object typeof '%s'", clazz);
				}
				T e = (T) field.get(obj);
				fnMap.put(fname, e);
			}
		}
		return !fnMap.isEmpty() ? fnMap : ARG.toDefThrowMsg(() -> X.f("Except field values"), defRq);
	}

	public static Object fieldValue_(Object obj, String fieldname, boolean fullSearch) throws NoSuchFieldException, IllegalAccessException {
		Field field = RFL.field_(obj, fieldname, fullSearch, true);
		return field.get(obj);
	}

	/**
	 * *************************************************************
	 * -------------------- Find Fieldname By Object ---------------
	 * *************************************************************
	 */
	public static String fieldNameAsEnumRq(Object obj) {
		return fieldNameAsEnumRq(obj, true, false);
	}

	public static String fieldNameAsEnumRq(Object value, boolean fullSearch, boolean isPareNullEquals) {
		return fieldByValueSt(value.getClass(), value, fullSearch, isPareNullEquals).getName();
	}

	public static String fieldNameStRq(Class clazz, Object value, boolean fullSearch, boolean isPareNullEquals) {
		return fieldByValueSt(clazz, value, fullSearch, isPareNullEquals).getName();
	}

	public static <T> String fieldNameRq(T inst, Object value, boolean fullSearch, boolean isPareNullEquals) {
		return fieldByValue((Class<T>) inst.getClass(), inst, value, fullSearch, isPareNullEquals).getName();
	}

	/**
	 * *************************************************************
	 * -------------------- Find Field By Object -------------------
	 * *************************************************************
	 */
	@Deprecated
	public static Field fieldByValueSt(Class clazz, Object value, boolean fullSearch, boolean isPareNullEquals, Field... defRq) {
		try {
			return fieldByValueSI_(clazz, null, value, fullSearch, isPareNullEquals);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(ex, "Field not found from Class [%s] with Value [%s]", clazz, value);
		}
	}

	@Deprecated
	public static <T> Field fieldByValue(Class<T> clazz, T inst, Object value, boolean fullSearch, boolean isPareNullEquals, Field... defRq) {
		try {
			return fieldByValueSI_(clazz, inst, value, fullSearch, isPareNullEquals);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(ex, "Field not found from Class [%s] with Value [%s]", clazz, value);
		}
	}

	@Deprecated
	private static <T> Field fieldByValueSI_(Class<T> clazz, T inst, Object value, boolean fullSearch, boolean isPareNullEquals) throws IllegalAccessException, NoSuchFieldException {
		List<Field> first = fieldsByValueStIn_(clazz, inst, value, fullSearch, isPareNullEquals, true);
		return first.get(0);
	}

	@Deprecated
	public static <T> List<Field> fieldsByValue(Class<T> clazz, T inst, Object value, boolean fullSearch, boolean isPareNullEquals, List<Field>... defRq) {
		try {
			return fieldsByValueStIn_(clazz, inst, value, fullSearch, isPareNullEquals, false);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(ex, "Fields not found from Class [%s] with Value [%s]", clazz, value);
		}
	}

	private static <T> List<Field> fieldsByValueStIn_(Class<T> clazz, T inst, Object value, boolean fullSearch, boolean isPareNullEquals, boolean onlyFirst) throws IllegalAccessException, NoSuchFieldException {
		List<Field> all = new ArrayList<>();
		Class clazz_ = clazz;
		if (fullSearch) {
			while (clazz_ != null) {
				for (Field field : clazz_.getDeclaredFields()) {
					if (ifFieldContainsValueStOrInst_(inst, field, true, value, isPareNullEquals)) {
						all.add(field);
						if (onlyFirst) {
							return all;
						}
					}
				}
				clazz_ = clazz_.getSuperclass();
			}
		} else {
			Field[] fields = clazz_.getDeclaredFields();
			for (Field field : fields) {
				if (ifFieldContainsValueStOrInst_(inst, field, true, value, isPareNullEquals)) {
					all.add(field);
					if (onlyFirst) {
						return all;
					}
				}
			}
		}
		if (all.isEmpty()) {
			throw newNoSuchFieldException("Field with value [%s] not found from Class [%s]", clazz_, value);
		}
		return all;
	}

	@NotNull
	private static NoSuchFieldException newNoSuchFieldException(String msg, Class clazz_, Object value) {
		return new NoSuchFieldException(STR.f(msg, value, clazz_));
	}

	public static boolean ifFieldContainsValueStOrInst_(Object inst, Field field, boolean modifyAccessible, Object value, boolean isPareNullEquals) throws IllegalAccessException {
		boolean isStaticField = inst == null;
		boolean isStaticModifier = isStatic(field);
		if ((isStaticModifier && !isStaticField) || (!isStaticModifier && isStaticField)) {
			return false;
		}
		if (modifyAccessible) {
			field.setAccessible(true);
		}
		return EQ.equals(field.get(inst), value, !isPareNullEquals);
	}


	public static boolean isMaybeFieldByStaticPreidicate(Boolean isStaticField, Field field) {
		return isStaticField == null ? true : isStatic(field) == isStaticField;
	}

	public static boolean isMaybeMethodByStaticPreidicate(Boolean isStaticMethod, Method method) {
		return isStaticMethod == null ? true : isStatic(method) == isStaticMethod;
	}

	public static boolean isStatic(Field field) {
		return java.lang.reflect.Modifier.isStatic(field.getModifiers());
	}

	public static boolean isStatic(Method method) {
		return java.lang.reflect.Modifier.isStatic(method.getModifiers());
	}

	/**
	 * *************************************************************
	 * ------------------------ Invoke Method ----------------------
	 * *************************************************************
	 */

	@Deprecated //NEED TEST
	public static Object invokeJarStWith(Path jarFile, String clazzFullName, String methodName, Object... keyValues) {
		Map<Class, Object> classObjectMap = MAP.ofKeyValuesSafeType(Class.class, Object.class, keyValues);
		Class[] parameterTypes = classObjectMap.keySet().toArray(new Class[classObjectMap.size()]);
		Object[] paramterObjects = classObjectMap.values().toArray(new Object[classObjectMap.size()]);
		return invokeJarSt(jarFile, clazzFullName, methodName, parameterTypes, paramterObjects);
	}

	public static Object invokeJarInvokeArgs(Path jarFile, String clazzFullName, String methodName, String... args) {
		Class[] types = {ARR.EMPTY_ARGS.getClass()};
		Object[] vls = {args};
		Object o = RFL.invokeJarSt(jarFile, clazzFullName, methodName, types, vls);
		return o;
	}

	public static Object invokeJarSt(Path jarFile, String clazzFullName, String methodName, Class[] parameterTypes, Object[] paramterObjects, Object... defRq) {
		try {
			return invokeJarSt_(jarFile, clazzFullName, methodName, parameterTypes, paramterObjects);
		} catch (Exception e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			String msg = "invokeFromJarStRq with :: jarFile:[%s] , clazzFullName[%s], methodName[%s], parameterTypes [%s], paramterObjects[%s]";
			throw new RequiredRuntimeException(e, msg, jarFile, clazzFullName, methodName, Arrays.asList(parameterTypes), Arrays.asList(paramterObjects));
		}
	}

	public static Object invokeJarSt_(Path jarFile, String clazzFullName, String methodName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, MalformedURLException {
		return invokeJarSt_(jarFile, clazzFullName, methodName, new Class[0]);
	}

	@RequiredArgsConstructor
	public static class JarCall {
		private final JCT jct;

		@SneakyThrows
		public Object invokeArgs(String[] args) {
			return invokeJarSt_(jarPath(), jct.ctx[1], jct.ctx[2], new Class[]{String[].class}, new Object[]{args});
		}

		private Path jarPath() {
			return Env.getNativeBinLibsPath(jct.ctx[0]);
		}
	}

	public static class JCP {
		public static final String[] JRC_MAIL_CTX = {"sendmail-mod-v2.jar", "mod_sendmail.SendMailMod", "main"};
//		public static final String[] JRC_MAIL_CTX = {"sendmail-mod-v2.jar", "mod_sendmail.SendMailMod", "invokeContext0"};

	}

	@RequiredArgsConstructor
	public enum JCT {
		//		MAIL(Env.getBinPath()""),
		MAIL(JCP.JRC_MAIL_CTX);
		public final String[] ctx;

		public JarCall newJarCall() {
			return new JarCall(this);
		}
//		public JarCall newJarCall(String[] args) {
//			return newJarCall(;
//		}

	}

//	public static Object invokeJarSt_(JarCall jarCall, Class[] parameterTypes, Object... paramterObjects) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, MalformedURLException {
//		invokeJarSt_(jarCall.)
//	}

	public static Object invokeJarSt_(Path jarFile, String clazzFullName, String methodName, Class[] parameterTypes, Object... paramterObjects) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, MalformedURLException {
		if (parameterTypes.length != paramterObjects.length) {
			throw new IT.CheckException("parameterTypes.length (%s) must be equals paramterObjects.length (%s)", parameterTypes.length, paramterObjects.length);
		}
		Class classToLoad = loadClassFromJar(jarFile, clazzFullName);
		Method method = method_(classToLoad, methodName, parameterTypes, true, true, false);
		return paramterObjects.length == 0 ? method.invoke(null) : method.invoke(null, paramterObjects);
	}

	public static Class loadClassFromJar(Path jarFile, String clazzFullName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, MalformedURLException {
		URLClassLoader urlClassLoader = getUrlClassLoader(jarFile);
		Class classToLoad = Class.forName(clazzFullName, true, urlClassLoader);
		return classToLoad;
	}

	public static @NotNull URLClassLoader getUrlClassLoader(Path jarFile) throws MalformedURLException {
		URLClassLoader child = new URLClassLoader(new URL[]{IT.isFileExist(jarFile).toUri().toURL()}, RFL.class.getClassLoader());
		return child;
	}

	public static Object invokeSimple(Object object, String methodName, Object... defRq) {
		try {
			return invoke_(object, methodName, new Class<?>[0], new Object[0]);
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			String msg = "Error invoke object '%s' method '%s'";
			throw new RequiredRuntimeException(ex, msg, object.getClass().getSimpleName(), methodName);
		}
	}

	public static Object invoke_(Object object, String methodName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		return invoke_(object, methodName, new Class<?>[0], new Object[0]);
	}

	public static Object invokeWithSingleArg(Object object, String methodName, Object argNN, Object... defRq) {
		return invoke(object, methodName, new Class<?>[]{argNN.getClass()}, new Object[]{argNN}, defRq);
	}

	public static Object invoke(Object object, String methodName, Object... defRq) {
		return invoke(object, methodName, new Class<?>[0], new Object[0], defRq);
	}

	public static Object invokeRq(Object object, String methodName, Class[] types, Object... args) {
		return invoke(object, methodName, types, args);
	}

	public static Object invoke(Object object, String methodName, Class[] types, Object[] args, Object... defRq) {
		try {
			return invokeImpl(object, object.getClass(), methodName, types, args);
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			String msg = "Error invoke object '%s' method '%s' with types %s , args %s";
			throw new RequiredRuntimeException(ex, msg, object.getClass().getSimpleName(), methodName, Arrays.asList(types), Arrays.asList(args));
		}
	}

	public static Object invoke_(Object object, String methodName, Class[] types, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		return invokeImpl(object, object.getClass(), methodName, types, args);
	}

	public static Object invokeSt(Class clazz, String methodName, Object... defRq) {
		return invokeSt(clazz, methodName, new Class[0], new Object[0]);
	}

	public static Object invokeSt(Class clazz, String methodName, Class[] types, Object[] args, Object... defRq) {
		try {
			return invokeStImpl(null, clazz, methodName, types, args);
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(ex, "Error invoke STATIC method/types/args = " + methodName + "/" + types + "/" + args);
		}
	}

	public static Object invokeSt_(Class clazz, String methodName, Class[] types, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		return invokeStImpl(null, clazz, methodName, types, args);
	}

	private static Object invokeStImpl(Object object, Class clazz, String methodName, Class[] types, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method declaredMethod = clazz.getDeclaredMethod(methodName, types);
		checkInvokeTypes(types, args);
		return declaredMethod.invoke(object, args);
	}

	private static Object invokeImpl(Object object, Class clazz, String methodName, Class[] types, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//		Method declaredMethod = clazz.getDeclaredMethod(methodName, types);
		Method declaredMethod = clazz.getMethod(methodName, types);
		checkInvokeTypes(types, args);
		return declaredMethod.invoke(object, args);
	}


	/**
	 * *************************************************************
	 * ----------------------- Create Instance ---------------------
	 * *************************************************************
	 */

	public static <T> T instEmptyConstructor(Class<T> type, T... defRq) {
		return instEmptyConstructor(type, true, defRq);
	}

	public static <T> T instEmptyConstructor(Class<T> type, boolean isPrivate, T... defRq) {
		try {
			return inst_(type, isPrivate);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
				 InvocationTargetException ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error create instance type '%s', private '%s'", type.getName(), isPrivate), defRq);
		}
	}

//	public static <T> T instByNotNullValues(Class<T> type, Object... classes_i_values) {
//		List<AbsType> absTypes = AbsType.ofSafeTypes(classes_i_values);
//		Class[] classes = absTypes.stream().map(t -> t.type()).toArray(Class[]::new);
//		Object[] values = absTypes.stream().map(t -> t.type()).toArray(Object[]::new);
//		return inst(type, classes, values);
//	}

	public static <T> T instByKeyValues(Class<T> type, Object... classes_i_values) {
		List<AbsType> absTypes = AbsType.ofSafeTypes(classes_i_values);
		Class[] classes = absTypes.stream().map(t -> t.type()).toArray(Class[]::new);
		Object[] values = absTypes.stream().map(t -> t.val()).toArray(Object[]::new);
		return inst(type, classes, values);
	}


	public static <T> T inst(Class<T> type, Class<?>[] class_arg, Object[] obj_arg, T... defRq) {
		try {
			return con_(type, class_arg).newInstance(obj_arg);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error create instance type '%s', class(arg) '%s', class_obj(arg) '%s'", type.getName(), ARR.as(class_arg), ARR.as(obj_arg)), defRq);
		}
	}

	public static <T> T inst(Class<T> type, Class<?> class_arg, Object obj_arg, T... defRq) {
		try {
			return con_(type, class_arg).newInstance(obj_arg);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error create instance type '%s', class(arg) '%s', class_obj(arg) '%s'", type.getName(), class_arg, obj_arg), defRq);
		}
	}

	public static <T> T inst_(Class<T> type, boolean... isPrivate) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return inst_(type, ARG.isDefEqTrue(isPrivate), new Class<?>[0], new Object[0]);
	}

	public static <T> T inst_(Class<T> type, boolean isPrivate, Class<?>[] class_args, Object[] init_args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return con_(type, isPrivate, class_args).newInstance(init_args);
	}

	public static <T> T inst_(Class<T> type, Class<?> class_args, Object init_args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return con_(type, class_args).newInstance(init_args);
	}

	/**
	 * *************************************************************
	 * ----------------------- Get Constructor ---------------------
	 * *************************************************************
	 */
	public static <T> Constructor<T> con(Class<T> type, boolean isPrivate, Class<?> class_args, Constructor... defRq) {
		try {
			return con_(type, isPrivate, class_args);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			return X.throwException(ex);
		}
	}

	public static <T> Constructor<T> con(Class<T> type, Class<?>[] class_args, Constructor... defRq) {
		try {
			return con_(type, class_args);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			return X.throwException(ex);
		}
	}

	public static <T> Constructor<T> con_(Class<T> type, boolean isPrivate, Class<?>... class_args) throws NoSuchMethodException {
		Constructor<T> con = con_(type, class_args);
		if (isPrivate) {
			con.setAccessible(true);
		}
		return con;
	}

	public static <T> Constructor<T> con_(Class<T> type, Class<?>... class_args) throws NoSuchMethodException {
		return type.getDeclaredConstructor(class_args);
	}


	public static List<String> invokeForAllAndGet(List objs, String method) {
		List r = new ArrayList();
		for (Object o : objs) {
			r.add(invokeSimple(o, method));
		}
		return r;
	}

	/**
	 * *************************************************************
	 * ----------------------------- Class -------------------------
	 * *************************************************************
	 */

	public static Class clazz_native(String className, boolean standart_load, boolean from_pack_lang, boolean from_pack_math, Class... defRq) {
		Class classType = null;
		if (standart_load) {
			classType = clazz(className, null);
		}
		if (classType != null) {
			return classType;
		} else if (from_pack_lang) {
			classType = clazz("java.lang." + className, null);
		}
		if (classType != null) {
			return classType;
		} else if (from_pack_math) {
			classType = clazz("java.math." + className, null);
		}
		if (classType != null) {
			return classType;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(classType);
		}
		throw new RequiredRuntimeException("clazz_native not found:" + className);
	}

	public static Class clazz(Class className, String child) {
		return clazz(className.getName() + "." + child);
	}

	public static Class clazz(String className, Class... defRq) {
		try {
			return clazz_(className);
		} catch (ClassNotFoundException e) {
			return ARG.toDefThrow(e, defRq);
		}
	}

	public static Class clazz_(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

	public static Class getGenericType(Class clazz, Class... defRq) {
		return getGenericType(clazz, 0, defRq);
	}

	public static Class getGenericType(Class clazz, int indGeneric, Class... defRq) {
		Type genericSuperclass = clazz.getGenericSuperclass();
		if (!(genericSuperclass instanceof ParameterizedType)) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new FIllegalStateException("Set generic type (%s) for class '%s'", indGeneric, clazz);
		}
		Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
		if (indGeneric >= actualTypeArguments.length) {
			throw new FIllegalStateException("Set correct index of generic type (%s) for class '%s' (%s)", indGeneric, clazz, actualTypeArguments.length);
		}
		Type actualTypeArgument = actualTypeArguments[indGeneric];
		return RFL.clazz(actualTypeArgument.getTypeName(), defRq);
	}

	public static String getStaticLogMark(boolean isStatic) {
		return isStatic ? "(St)" : "";
	}

	public static boolean isVoid(Class type) {
		return type == Void.TYPE || type == Void.class;
	}

	public static String toString(Method method) {
		return X.f("Method%s '%s' <<< [%s].", getStaticLogMark(isStatic(method)), method.getName(), method.getReturnType().getName());
	}

	public static class CallToMethod<R> {
		public final Object callToObject;
		public final Class callToClass;
		public final String method;
		public final Class[] methodTypes;

		public CallToMethod(Class callToClass, String method, Class... methodTypes) {
			this(null, callToClass, method, methodTypes);
		}

		public CallToMethod(Object callToObject, String method, Class... methodTypes) {
			this(callToObject, null, method, methodTypes);
		}

		private CallToMethod(Object callToObject, Class callToClass, String method, Class... methodTypes) {
			this.callToObject = callToObject;
			this.callToClass = callToObject != null ? callToObject.getClass() : IT.NN(callToClass);
			this.method = method;
			this.methodTypes = methodTypes;
		}

		public R call(Object... methodObjects) {
			checkInvokeTypes(methodTypes, methodObjects);
			if (callToObject != null) {
				return (R) invoke(callToObject, method, methodTypes, methodObjects);
			} else {
				return (R) invokeSt(callToClass, method, methodTypes, methodObjects);
			}
		}

	}

	public static void checkInvokeTypes(Class[] methodTypes, Object[] methodObjects) {
		IT.isEq(methodObjects.length, methodTypes.length);
		for (int i = 0; i < methodTypes.length; i++) {
			Object obj = methodObjects[i];
			if (obj != null) {
				if (!methodTypes[i].isAssignableFrom(obj.getClass())) {
					throw new FIllegalArgumentException("Method arg [%s] type '%s' is not assignable from '%s'", i, obj.getClass(), methodTypes[i]);
				}
			}
		}
	}

	/**
	 * *************************************************************
	 * ----------------------------- Test --------------------------
	 * *************************************************************
	 */

	static void runTest() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
		Test t = new Test();
		RFL.write_(t, "field", 123, true, true);
		RFL.writeSt_(TestStatic.class, "field", 123, true, true);
		Sys.p("Test read field is ok ::: " + UTest.testEquals(read_(t, "field", true, true), t.field));
		Sys.p("Test invoke method is ok ::: " + invoke_(t, "m"));
		Sys.p("Test read field is ok ::: " + UTest.testEquals(read_(t, "field", true, true), TestStatic.field));
		Sys.p("Test read static field is ok ::: " + UTest.testEquals(readSt_(TestStatic.class, "field", true, true), TestStatic.field));
		Sys.p("Test invoke static method is ok ::: " + invokeSt_(TestStatic.class, "m", new Class[0], new Object[0]));
	}


	static class UTest {

		public static Object testEquals(Object orgValue, Object testValue) {
			if (!Objects.equals(orgValue, testValue)) {
				throw newExceptionEquals(orgValue, testValue);
			}
			return testValue;
		}

		private static IllegalArgumentException newExceptionEquals(Object orgValue, Object testValue) {
			throw new IllegalArgumentException("Org-Value not equals Test-Value/" + orgValue + "/" + testValue + "/");
		}
	}

	static class Test {
		Object field = null;

		void m() {
			Sys.p("Method runned & return field with value ::: " + field);
		}
	}

	static class TestStatic {
		static Object field = null;

		static void m() {
			Sys.p("Method runned & return field with value ::: " + field);
		}
	}

}
