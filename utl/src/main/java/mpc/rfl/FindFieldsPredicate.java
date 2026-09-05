package mpc.rfl;

import mpu.X;
import mpc.str.condition.StringConditionPattern;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

public class FindFieldsPredicate implements Predicate<Field> {
	public final boolean OR;

	public final Boolean isStatic;

	public final String fieldName;
	public final StringConditionPattern fieldNameStringCondition;
	public final Class fieldType;
	public final List<Class> ano;
	public final RFL.FieldValue fieldValue;

	public final Predicate predicate;

//	public FindFieldsPredicate(boolean isStatic, List<Class> ano) {
//		this(isStatic, null, ano, null, null, null, null);
//	}

	public FindFieldsPredicate(Boolean isStatic, Predicate<Field> predicate) {
		this(isStatic, null, null, null, null, null, predicate);
	}

	public FindFieldsPredicate(Class fieldType, List<Class> ano, String fieldName, StringConditionPattern fieldNameStringCondition) {
		this(null, fieldType, ano, fieldName, fieldNameStringCondition, null, null);
	}

	public FindFieldsPredicate(Boolean isStatic, Class fieldType, List<Class> ano, String fieldName, StringConditionPattern fieldNameStringCondition) {
		this(isStatic, fieldType, ano, fieldName, fieldNameStringCondition, null, null);
	}

	public FindFieldsPredicate(Boolean isStatic, Class fieldType, List<Class> ano, String fieldName, StringConditionPattern fieldNameStringCondition, RFL.FieldValue fieldValue, Predicate<Field> predicate) {
		this.isStatic = isStatic;
		this.fieldName = fieldName;
		this.fieldNameStringCondition = fieldNameStringCondition;
		this.fieldType = fieldType;
		this.ano = ano;
		this.predicate = predicate;
		this.fieldValue = fieldValue;
		this.OR = X.notNullOnlyOne(fieldName, fieldNameStringCondition, fieldType, ano, predicate, fieldValue);
	}

	@Override
	public boolean test(Field f) {

		if (!RFL.isMaybeFieldByStaticPreidicate(isStatic, f)) {
			return false;
		}

		Boolean okPredicate = predicate == null ? null : predicate.test(f);
		if (okPredicate != null) {
			if (!okPredicate) {
				return false;
			} else if (OR) {
				return true;
			}
		}


		Boolean okFieldName = fieldName == null ? null : fieldName.equals(f.getName());
		if (okFieldName != null) {
			if (!okFieldName) {
				return false;
			} else if (OR) {
				return true;
			}
		}


		Boolean okStringCondition = fieldNameStringCondition == null ? null : fieldNameStringCondition.matches(f.getName());
		if (okStringCondition != null) {
			if (!okStringCondition) {
				return false;
			} else if (OR) {
				return true;
			}
		}

		Boolean okFieldType = fieldType == null ? null : fieldType.isAssignableFrom(f.getType());
		if (okFieldType != null) {
			if (!okFieldType) {
				return false;
			} else if (OR) {
				return true;
			}
		}

		Boolean okAno = ano == null ? null : RFL.ANO.isFieldWithAnotation(f, ano);
		if (okAno != null) {
			if (!okAno) {
				return false;
			} else if (OR) {
				return true;
			}
		}

		Boolean okFieldValue = fieldValue == null ? null : fieldValue.isEquals(f, false);
		if (okFieldValue != null) {
			if (!okFieldValue) {
				return false;
			} else if (OR) {
				return true;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "FindFieldsPredicate{" +
				"OR=" + OR +
				", fieldName='" + fieldName + '\'' +
				", fieldNameStringCondition=" + fieldNameStringCondition +
				", fieldType=" + fieldType +
				", ano=" + ano +
				", fieldValue=" + fieldValue +
				", predicate=" + predicate +
				'}';
	}
}
