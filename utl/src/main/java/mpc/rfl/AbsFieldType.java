package mpc.rfl;

import mpu.X;
import mpc.types.abstype.AbsType;
import mpc.exception.NI;
import mpu.IT;

import java.lang.reflect.Field;

public class AbsFieldType extends AbsType {
	public final Field field;
	public final Class fromClass;
	public final Object fromObject;

	public AbsFieldType(Field field) {
		this(field.getName(), field, null, null);
	}

	public AbsFieldType(String name, Field field) {
		this(name, IT.NN(field), null, null);
	}

	public AbsFieldType(Field field, Object fromObject) {
		this(field.getName(), field, null, fromObject);
	}

	public AbsFieldType(String name, Field field, Object fromObject) {
		this(name, field, null, fromObject);
	}

	public AbsFieldType(String name, Field field, Class fromClass, Object fromObject) {
		super(name);
		this.field = field;
		this.fromClass = fromClass;
		this.fromObject = fromObject;
		if (!isStatic()) {
			IT.state(fromObject != null);
		}
	}
	public static AbsFieldType any_(Class fromClass, Object fromObject, String fieldname) throws NoSuchFieldException {
		return RFL.fieldAbsType_(fromClass, fromObject, fieldname, null, true, true);
	}

	public boolean isStatic() {
		return RFL.isStatic(field);
	}

	@Override
	public Class type() {
		return field.getType();
	}

	public Object value_() throws IllegalAccessException {
		return isStatic() ? field.get(null) : field.get(fromObject);
	}

	@Override
	public Object val() {
		try {
			return value_();
		} catch (IllegalAccessException e) {
			return X.throwException(e);
		}
	}

	@Override
	public AbsType setValue(Object value) {
		throw new NI();
	}
}
