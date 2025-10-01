package mpc.env;

import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpc.log.L;
import mpc.map.BootContext;
import mpc.rfl.RFL;
import mpc.str.ObjTo;
import mpe.core.U;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;

import java.lang.reflect.Field;
import java.util.List;

public class AutoInitClassProperty {

	public static final Class<AutoInitValue> AVI_CLASS = AutoInitValue.class;

	@SneakyThrows
	public static void initClass(Class clazz) {
		List<Field> values = RFL.fields(clazz, ARR.as(AVI_CLASS));
		for (Field field : values) {
			field.setAccessible(true);
			AutoInitValue avi = field.getAnnotation(AVI_CLASS);
			setValueFromAP(field, avi);
		}
	}


	public static void setValueFromAP(Field field, AutoInitValue avi) throws IllegalAccessException {

		String prop = avi.prop();
//		Class anoType = avi.type();
		Class fieldType = field.getType();

//		if (anoType != field.getType()) { //TODO wth?
//			//				if(Integer.class!=field.getType())
//			throw new FIllegalArgumentException("Type of field '%s' is defference from avi-annotation '%s'", field.getType(), anoType);
//		}

		String strVl;
		BootContext bootContext = BootContext.get();
		if (bootContext == null) {
			strVl = AP.getAs(prop, String.class, null);
		} else {
			strVl = bootContext.getAs(prop, String.class, null);
		}
		strVl = AP.getValueWoDef(strVl);

//		if (false) {
//			setFieldValue(field, avi, strVl);
//		}

		field.setAccessible(true);

		if (strVl == null) {
			strVl = avi.def();
		}
		if (strVl == null || U.__NULL__.equals(strVl)) {
			IT.state(!field.getType().isPrimitive(), "Primitive type '%s' not support NULL", field.getType());
			//TODO mb init with default primititve value?
			field.set(null, null);
		} else {
			field.set(null, ObjTo.objTo(strVl, fieldType));
		}
	}

	public static void setValueObject(Class clazz, String property, Object newValue) {
		List<Field> anoFields = getAnoFields(clazz, property);
		IT.stateNot(anoFields.isEmpty(), "Class '%s' must contain field '%s'", clazz, property);
		anoFields.forEach(field -> {
			field.setAccessible(true);
			try {
				field.set(null, newValue);
				L.info("Update class '%s' field OBJECT '%s' value '%s'", clazz, property, newValue);
			} catch (IllegalAccessException e) {
				X.throwException(e);
			}
		});
	}

	private static List<Field> getAnoFields(Class clazz, String property) {
		return STREAM.filterToList(RFL.fields(clazz, ARR.as(AVI_CLASS), ARR.EMPTY_LIST), f -> f.getAnnotation(AVI_CLASS).prop().equals(property));
	}

//	public static void setValue(Class clazz, String property, String newValue) throws IllegalAccessException {
//		getAnoFields(clazz, property).forEach(field -> {
//			field.setAccessible(true);
//			setFieldValue(field, field.getAnnotation(AVI_CLASS), newValue);
//		});
//	}


//	@SneakyThrows
//	private static void setFieldValue(Field field, AutoInitValue avi, String newValue) {
//
//		String prop = avi.prop();
//		Class anoType = avi.type();
//		String defVal = avi.def();
//
//		if (newValue == null) {
//			if (U.__NULL__.equals(defVal)) {
//				//ok, set null
//			} else if (X.empty(defVal)) {
//				throw new FIllegalArgumentException("AP value '%s' is null & AVI 'defVal' is empty", prop);
//			} else {
//				newValue = defVal;
//			}
//		}
//
//		Object objVl = newValue == null ? null : UST.strTo(newValue, anoType);
//
//		boolean isWrapperType = Primitives.isWrapperType(field.getType());
//		if (objVl == null && isWrapperType) {
//			throw new FIllegalArgumentException("Field '%s' is primitive. But value is null", field);
//		}
//
//		if (objVl != null && objVl instanceof CharSequence) {
//			objVl = AppPH.replaceAppPlaceholders(objVl.toString());
//		}
//
//		field.set(null, objVl);
//
//		L.info("Update class '%s' field '%s' value '%s'", anoType, prop, newValue);
//	}


	//TODO wth?
//	@Deprecated
//	public static class AppPH {
//		public static final String RPA_PLACEHOLDER = "{{RPA}}";
//
//		private static String replaceAppPlaceholders(String newValue) {
//			if (X.empty(newValue)) {
//				return newValue;
//			}
//			if (newValue.contains(RPA_PLACEHOLDER)) {
//				newValue = newValue.replace(RPA_PLACEHOLDER, Env.RPA.toString());
//			}
//			return newValue;
//		}
//	}

}
