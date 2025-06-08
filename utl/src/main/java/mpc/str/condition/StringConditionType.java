package mpc.str.condition;

import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYM;
import org.apache.commons.lang3.StringUtils;

public enum StringConditionType {
	EQ, EQ_IGNORE_CASE, CONTAINS, REGEX, STARTS, STARTSIC, ENDS, ENDSIC, START_END;

	public boolean matchesSafe(String haystack, String needle) {
		return haystack == null ? false : matches(haystack, needle);
	}

	public boolean matches(String haystack, String needle_rx) {
		switch (this) {
			case CONTAINS:
				return haystack.contains(needle_rx);
			case EQ:
				return mpu.core.EQ.equalsString(haystack, needle_rx, false, false);
			case EQ_IGNORE_CASE:
				return mpu.core.EQ.equalsString(haystack, needle_rx, true, false);
			case REGEX:
				return haystack.matches(needle_rx);
			case STARTS:
				return haystack.startsWith(needle_rx);
			case STARTSIC:
				return StringUtils.startsWithIgnoreCase(haystack, needle_rx);
			case ENDS:
				return haystack.endsWith(needle_rx);
			case ENDSIC:
				return StringUtils.endsWithIgnoreCase(haystack, needle_rx);
			default:
				throw new IllegalStateException("What is type? " + this);
		}
	}

	public StringConditionPattern buildCondition(String pattern) {
		return new StringConditionPattern(pattern, this);
	}

	public String cutLine(String str, String pattern, String def) {
		for (String line : StringUtils.split(str, SYM.NEWLINE)) {
			if (matches(line, pattern)) {
				return line;
			}
		}
		return def;
	}

	public String getMatchesPattern(String body, String start, String end) {
		switch (this) {
			case START_END:
				if (body.contains(start) && body.contains(end)) {
					return body.substring(body.indexOf(start), body.lastIndexOf(end) + end.length());
				}
				return null;
			default:
				throw new UnsupportedOperationException("Type not supported:" + this);
		}
	}

	public boolean isIgnoreCase() {
		return isIgnoreCase(this);
	}

	private static boolean isIgnoreCase(StringConditionType conditionEq) {
		switch (conditionEq) {
			case EQ_IGNORE_CASE:
			case STARTSIC:
			case ENDSIC:
				return true;
			case STARTS:
			case EQ:
			case CONTAINS:
			case REGEX:
			case START_END:
			case ENDS:
				return false;
			default:
				throw new WhatIsTypeException(conditionEq);
		}
	}
}
