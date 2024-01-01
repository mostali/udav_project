package mpe.str;

import mpc.*;
import mpc.args.ARG;
import mpc.arr.Arr;
import mpc.ERR;
import mpc.str.STR;
import mpc.str.sym.SYMJ;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//ToString
public class ToString {
	public static String strOrStrNull(Object val) {
		return strOr(val, "null");
	}

	public static String strOrNull(Object val) {
		return strOr(val, null);
	}

	public static String strOrNpe(Object val) {
		return ERR.NN(val).toString();
	}

	public static String strOrEmp(Object val) {
		return strOr(val, "");
	}

	public static String strOr(Object o, String isNullThat) {
		return o == null ? isNullThat : o.toString();
	}

	public static String toNiceString(Collection val) {
		return toNiceString(val, ".");
	}

	public static String toString(Object val, boolean... ifNullThatNullStrOrEmptyStr) {
		if (val == null) {
			return toStrNull_Or_TrueEmpty_Or_FalseNull(ifNullThatNullStrOrEmptyStr);
		}
		return val == null ? null : toNiceString(Arr.as(".", val));
	}

	private static String toStrNull_Or_TrueEmpty_Or_FalseNull(boolean... ifNullThatNullStrOrEmptyStr) {
		Boolean bool = ARG.toDefBoolean(ifNullThatNullStrOrEmptyStr);
		return bool == null ? "null" : (bool ? "" : null);

	}

	public static String toNiceString(Object[] val) {
		return val == null ? null : toNiceString(Arr.as(".", val));
	}

	public static String toNiceStringCompact(Collection val) {
		return toNiceString(val, "");
	}

	public static String toNiceString(Collection val, String verticalDelimetr) {
		String line = val == null ? "null" : (String) val.stream().map(ToString::strOrStrNull).collect(Collectors.joining(STR.NL + strOr(verticalDelimetr, "") + (X.empty(verticalDelimetr) ? "" : STR.NL), STR.NL, STR.NL));
		return STR.removeStartEndString(line, STR.NL, false);
	}

	public static String toNiceStringLine(Collection val) {
		return val == null ? "null" : (String) val.stream().map(ToString::strOrStrNull).collect(Collectors.joining("", "~", ""));
	}

	public static String toStringDebugAnyCacheValue(Object value) {
		String val;
		if (value == null) {
			val = "NULL";
		} else if (value instanceof Collection) {
			val = value.getClass().getSimpleName() + "(" + X.sizeOf((Collection) value) + "):" + value;
		} else if (value instanceof Map) {
			val = value.getClass().getSimpleName() + "(" + X.sizeOf((Map) value) + "):" + value;
		} else if (value instanceof CharSequence) {
			val = value.getClass().getSimpleName() + "(" + X.sizeOf((CharSequence) value) + "):" + value;
		} else if (value instanceof Number || value instanceof Boolean) {
			val = value.toString();
		} else {
			val = value.getClass().getName() + "(" + value.hashCode() + ")";
		}
		val = STR.substrTo(val, 100, val);
		return val;
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

	public static String toStringSE(String token, int count) {
		return STR.substrStartEndInsert(token, count, count, "...", token);
	}

	public static String toStringRfl(Object o) {
		return o == null ? "null" : (o instanceof CharSequence ? o.toString() : ToStringBuilder.reflectionToString(o));
	}

	public static String toString(Object impl) {
		return impl.toString();
	}

	public static String join(Object... args) {
		return Stream.of(args).map(ToString::strOrStrNull).collect(Collectors.joining());
	}

	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	public static UUID uuid(String uuid) {
		return UUID.fromString(uuid);
	}
}
