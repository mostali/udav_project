package mpc.str.condition;

import mpu.core.EQ;

import java.util.Objects;

public class StringConditionPattern {

	public final String pattern;
	public final StringConditionType conditionType;

	public StringConditionPattern(String pattern, StringConditionType conditionType) {
		this.pattern = pattern;
		this.conditionType = conditionType;
	}

	public boolean matches(String original) {
		return conditionType.matches(original, pattern);
	}

	public static StringConditionPattern build(String pattern, StringConditionType type) {
		return type.buildCondition(pattern);
	}

	@Override
	public String toString() {
		return conditionType + ":" + pattern;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof StringConditionPattern)) {
			return false;
		}
		StringConditionPattern ssc = (StringConditionPattern) obj;
		if (!EQ.equalsUnsafe(pattern, ssc.pattern)) {
			return false;
		}
		if (!EQ.equalsUnsafe(conditionType, ssc.conditionType)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pattern, conditionType);
	}
}
