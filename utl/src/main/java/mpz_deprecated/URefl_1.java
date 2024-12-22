package mpz_deprecated;

import mpc.rfl.RFL;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Deprecated
public class URefl_1 {

	RFL NEW_REFL = new RFL();

	public static final Logger L = LoggerFactory.getLogger(URefl_1.class);

	public static Field getField(Object obj, String fieldName) {
		Class check = null;
		do {
			check = check == null ? obj.getClass() : check.getSuperclass();
			if (check == null) {
				return null;
			}
			try {
				return check.getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
			}
		} while (true);
	}

	public static void setFieldObject(Object obj, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException {
		Field f = getField(obj, fieldName);
		if (f != null) {
			f.setAccessible(true);
			f.set(obj, value);
		} else {
			throw new IllegalArgumentException("Filed :" + fieldName + " is null");
		}
	}

	public static String getFieldObjectAsString(Object obj, String fieldName) {
		Object o = getFieldObject(obj, fieldName);
		if (o == null) {
			return null;
		} else if (o instanceof String) {
			return (String) o;
		} else if (o instanceof Integer) {
			return Integer.toString((int) o);
		} else if (o instanceof Long) {
			return Long.toString((long) o);
		} else if (o instanceof Double) {
			return Double.toString((double) o);
		} else if (o instanceof Float) {
			return Float.toString((float) o);
		} else {
			if (L.isErrorEnabled()) {
				L.error("Field [{}] of object [{}] is not String.class", fieldName, o.getClass().getSimpleName());
			}
			return null;
		}
	}

	public static Object getFieldObject(Object obj, String fieldName) {
		Field f = getField(obj, fieldName);
		if (f != null) {
			f.setAccessible(true);
			try {
				Object o = f.get(obj);
				return o;
			} catch (IllegalArgumentException e) {
				if (L.isErrorEnabled()) {
					L.error("Field [{}] of object [{}], IllegalArgument::" + e.getMessage(), fieldName, obj.getClass().getSimpleName());
				}
			} catch (IllegalAccessException e) {
				if (L.isErrorEnabled()) {
					L.error("Field [{}] of object [{}], IllegalAccess::" + e.getMessage(), fieldName, obj.getClass().getSimpleName());
				}
			}
		} else {
			if (L.isErrorEnabled()) {
				L.error("Field [{}] of object [{}] not found", fieldName, obj.getClass().getSimpleName());
			}
		}
		return null;
	}

	public static List<String> getAllFieldsAsNames(Class<?> clazz, Class<? extends Annotation> ann) {
		List<Field> allFields = getAllFields(clazz, ann);
		return allFields.stream().map(o -> o.getName()).collect(Collectors.toList());
	}

	public static List<Field> getAllFields(Class<?> clazz, Class<? extends Annotation> ann, String... regex) {
		List<Field> fieldList = new ArrayList<Field>();
		Class tmpClass = clazz;
		while (tmpClass != null) {
			if (ann == null) {
				for (Field field : tmpClass.getDeclaredFields()) {
					if (regex.length == 0) {
						fieldList.add(field);
					} else if (STR.matches(field.getName(), regex)) {
						fieldList.add(field);
					}
				}
			} else {
				for (Field field : tmpClass.getDeclaredFields()) {
					if (field.isAnnotationPresent(ann)) {
						if (regex.length == 0) {
							fieldList.add(field);
						} else if (STR.matches(field.getName(), regex)) {
							fieldList.add(field);
						}
					}
				}
			}
			tmpClass = tmpClass.getSuperclass();
		}
		return fieldList;
	}

}
