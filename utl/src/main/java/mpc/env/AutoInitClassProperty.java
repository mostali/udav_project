package mpc.env;

import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpc.env.boot.AppBoot;
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
import java.util.function.Function;

public class AutoInitClassProperty {

	public static final Class<AutoInitValue> AVI_CLASS = AutoInitValue.class;
	public static Function<String, String> SPRING_ENV_PROP_LOADER = null;
	public static Function<String, String> GNC_LOADER = null;

	@SneakyThrows
	public static void initClass(Class clazz) {
		List<Field> values = RFL.fields(clazz, ARR.as(AVI_CLASS));
		for (Field field : values) {
			field.setAccessible(true);
			AutoInitValue avi = field.getAnnotation(AVI_CLASS);
			setValueFromSEA(field, avi);
		}
	}


	//system,env,application
	public static void setValueFromSEA(Field field, AutoInitValue avi) throws IllegalAccessException {

		String prop = avi.prop();
//		Class anoType = avi.type();
		Class fieldType = field.getType();

//		if (anoType != field.getType()) { //TODO wth?
//			//				if(Integer.class!=field.getType())
//			throw new FIllegalArgumentException("Type of field '%s' is defference from avi-annotation '%s'", field.getType(), anoType);
//		}

		String strVl = null;
		if (SPRING_ENV_PROP_LOADER != null) {
			strVl = SPRING_ENV_PROP_LOADER.apply(prop);
			if (strVl != null && AppBoot.L.isInfoEnabled()) {
				AppBoot.L.info("Loaded APP-property '{}' from [ENV_PROP_LOADER] with value '{}'", prop, strVl);
			}
		}

		if (GNC_LOADER != null) {
			strVl = GNC_LOADER.apply(prop);
			if (strVl != null && AppBoot.L.isInfoEnabled()) {
				AppBoot.L.info("Loaded APP-property '{}' from [GNC_LOADER] with value '{}'", prop, strVl);
			}
		}

		if (strVl == null) {
			BootContext bootContext = BootContext.get();
			if (bootContext == null) {
//			strVl = AP.getAs(prop, String.class, null);
				strVl = APP.getPropFrom_Sys_Env_AP(prop, null);
				if (strVl != null && AppBoot.L.isInfoEnabled()) {
					AppBoot.L.info("Loaded APP-property '{}' from [SEA] with value '{}'", prop, strVl);
				}
			} else {
				strVl = bootContext.getAs(prop, String.class, null);
				if (strVl != null && AppBoot.L.isInfoEnabled()) {
					AppBoot.L.info("Loaded APP-property '{}' from [BOOT_CONTEXT] with value '{}'", prop, strVl);
				}
			}
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

}
