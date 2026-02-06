package mpu.str;

import mpu.core.ARG;
import mpu.core.ARR;
import mpc.str.sym.SYMJ;
import mpu.X;

import java.util.Collection;
import java.util.stream.Collectors;

public class ToString {

	public static String toNiceString(Collection val) {
		return toNiceString(val, ".");
	}

	public static String toString(Object val, boolean... ifNullThatNullStrOrEmptyStr) {
		if (val == null) {
			return toStrNull_Or_TrueEmpty_Or_FalseNull(ifNullThatNullStrOrEmptyStr);
		}
		return val == null ? null : toNiceString(ARR.as(".", val));
	}

	private static String toStrNull_Or_TrueEmpty_Or_FalseNull(boolean... ifNullThatNullStrOrEmptyStr) {
		Boolean bool = ARG.toDefBooleanOrNull(ifNullThatNullStrOrEmptyStr);
		return bool == null ? "null" : (bool ? "" : null);

	}

	public static String toNiceString(Object[] val) {
		return val == null ? null : toNiceString(ARR.as(".", val));
	}

	public static String toNiceStringCompact(Collection val) {
		return toNiceString(val, "");
	}

	public static String toNiceString(Collection val, String verticalDelimetr) {
		String line = val == null ? "null" : (String) val.stream().map(X::toString).collect(Collectors.joining(STR.NL + X.toStringNN(verticalDelimetr, "") + (X.empty(verticalDelimetr) ? "" : STR.NL), STR.NL, STR.NL));
		return STR.removeStartEndString(line, STR.NL, false);
	}

	public static String toNiceStringLine(Collection val) {
		return val == null ? "null" : (String) val.stream().map(X::toString).collect(Collectors.joining("", "~", ""));
	}

	public static <T> String toStrNullOrEmp(Object obj) {
		return obj == null ? "NULL" : "EMPTY";
	}

	public static String toStringPfx(Class clazz) {
		return toStringPfx(clazz.getSimpleName());
	}

	public static String toStringPfx(String name) {
		return SYMJ.ARROW_RIGHT_SPEC + name;
	}

	public static String toStringSE(String str, int count) {
		return STR.substrKeepStartEndAndInsertBetween(str, count, count, " ... ", str);
	}

}
