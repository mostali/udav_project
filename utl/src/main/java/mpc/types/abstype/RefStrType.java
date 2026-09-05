package mpc.types.abstype;

import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpu.X;
import mpc.log.L;
import mpc.rfl.RFL;
import mpu.str.UST;
import mpu.str.TKN;

import java.lang.reflect.Field;

public class RefStrType<T> extends AbsType<T> {

	//	public static void main(String[] args) {
//		RefStrType ref = RefStrType.ofSimpleValue("staticBoolean@Boolean@true");
//
//		P.p(RefStrTypeTest.staticBoolean);
//		ref.setTo(RefStrTypeTest.class, null);
//		P.p(RefStrTypeTest.staticBoolean);
//
//		ref = RefStrType.ofSimpleValue("staticInteger@Integer@42");
//		P.p(RefStrTypeTest.staticInteger);
//		ref.setTo(RefStrTypeTest.class, null);
//		P.p(RefStrTypeTest.staticInteger);
//
//		ref = RefStrType.ofSimpleValue("staticString@String@Str");
//		P.p(RefStrTypeTest.staticString);
//		ref.setTo(RefStrTypeTest.class, null);
//		P.p(RefStrTypeTest.staticString);
//
//		ref = RefStrType.ofSimpleValue("staticString@@Str2");
//		P.p(RefStrTypeTest.staticString);
//		ref.setTo(RefStrTypeTest.class, null);
//		P.p(RefStrTypeTest.staticString);
//
//		ref = RefStrType.ofSimpleValue("staticString@@null");
//		P.p(RefStrTypeTest.staticString);
//		ref.setTo(RefStrTypeTest.class, null);
//		P.p(RefStrTypeTest.staticString);
//	}

	private boolean simple;

	public RefStrType(String name) {
		super(name);
	}

	public RefStrType(String name, Number value) {
		super(name, value);
	}

	public RefStrType(String name, String value) {
		super(name, value);
	}

	public RefStrType(String name, T value, Class<T> type) {
		super(name, value, type);
	}

	//fieldname@Boolean@1
	//@Boolean@1
	public static <T> RefStrType<T> ofSimpleValue(String exp) {

		String nextExp = exp;

		//---------------------NAME---------------------
		String fieldName;
		if (nextExp.charAt(0) == '@') {
			fieldName = null;
			nextExp = nextExp.substring(1);
		} else {
			String nameStr = TKN.first(nextExp, '@', "");
			if (X.blank(nameStr)) {
				throw new RequiredRuntimeException("Exp '%s' has empty field name", exp);
			}
			nextExp = TKN.startWith(nextExp, nameStr, "").trim();
			fieldName = nameStr.trim();
		}

		//---------------------TYPE---------------------
		if (nextExp.charAt(0) != '@') {
			throw new RequiredRuntimeException("Exp '%s' must have pfx '@'", exp);
		}
		nextExp = nextExp.substring(1);

		Class type;
		Object val;
		if (nextExp.charAt(0) == '@') {
			type = String.class;
			nextExp = nextExp.substring(1).trim();
		} else {
			String typeStr = TKN.first(nextExp, '@', null);
			if (typeStr == null) {
				throw new RequiredRuntimeException("Exp '%s' type not found", exp);
			}
			type = UST.load_class_by_name(typeStr, true, true);
			nextExp = TKN.startWith(nextExp, typeStr, "").trim();
			if (nextExp.charAt(0) != '@') {
				throw new RequiredRuntimeException("Exp '%s' type VALUE must have pfx '@'", exp);
			}
			nextExp = nextExp.substring(1);
		}

		//---------------------VALUE---------------------
		if (X.empty(nextExp)) {
			throw new RequiredRuntimeException("Exp '%s' type VALUE not found", exp);
		}
		boolean isNull = nextExp.equalsIgnoreCase("null");
		if (isNull) {
			val = null;
		} else {
			if (type == String.class) {
				val = nextExp;
			} else {
				val = UST.strTo(nextExp, type);
			}
		}
		return RefStrType.of(fieldName, val, type);

	}

	public static <T> RefStrType<T> of(String name, T value, Class<T> type) {
		return new RefStrType(name, value, type);
	}

	public static <T> RefStrType<T> of(String field, String value) {
		Class type = String.class;
		String typeStr = TKN.last(field, '@', null);
		if (typeStr != null) {
			type = RFL.clazz_native(typeStr, true, true, true);
			field = TKN.firstGreedy(field, '@');
		}
		boolean isNull = value == null || value.equals("null");
		Object val = null;
		if (!isNull) {
			if (type == String.class) {
				val = value;
			} else {
				val = UST.strTo(value, type);
			}
		}
		RefStrType of = RefStrType.of(field, val, type);
		of.simple = typeStr == null;
		return of;
	}

	public void setTo(Class clazz) {
		setTo(clazz, null);
	}

	public void setTo(Class clazz, Object inst) {
		try {
			Class cls = clazz != null ? clazz : inst.getClass();
			Field f = cls.getDeclaredField(name());
			f.setAccessible(true);
			if (L.isInfoEnabled()) {
				L.info("Field was setTo '{}' as '{}'='{}'", clazz.getSimpleName(), name(), getValue());
			}
			if (clazz != null) {
				f.set(null, val());
			} else if (inst != null) {
				f.set(inst, val());
			} else {
				throw new FIllegalStateException("Class and inst is null, wtf");
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RequiredRuntimeException("Field '%s' not found:", toString());
		}
	}

	@Override
	public String toString() {
		return simple ? name() : name() + "@" + type();
	}
}
