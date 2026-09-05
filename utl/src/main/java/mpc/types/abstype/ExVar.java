package mpc.types.abstype;

import mpc.exception.FIllegalArgumentException;

public class ExVar extends ExtStringType {
	public ExVar(String name, Object value) {
		super(name, value);
	}

	public static ExVar of(boolean bool) {
		return new ExVar(null, bool);
	}

	public static ExVar ofAny(Object var) {
		return new ExVar(null, var);
	}

	public Boolean toBooleanType() {
		if (val() == null) {
			return null;
		}
		if (val() instanceof Boolean) {
			return (Boolean) val();
		}
		if (val() instanceof CharSequence) {
			return Boolean.parseBoolean(val().toString());
		}
		throw new FIllegalArgumentException("How to convert this value to Boolean? [%s]", val());
	}
}
