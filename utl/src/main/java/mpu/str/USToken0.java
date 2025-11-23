package mpu.str;


import mpc.exception.RequiredRuntimeException;
import mpu.X;
import mpu.core.ARG;

public class USToken0 {

	static <T> T toType(boolean firstLast, String str, Object splitDelStr, Class<T> type, String first, T... defRq) {
		return toType(firstLast, str, splitDelStr, -1, type, first, defRq);
	}

	static <T> T toType(boolean firstLast, String str, Object splitDelStr, int ind, Class<T> type, String first, T... defRq) {
		Exception err = null;
		if (first != null) {
			try {
				return UST.strTo(first, type);
			} catch (Exception ex) {
				err = ex;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		String flMark = firstLast ? "FIRST" : "LAST";
		flMark += ind == -1 ? "" : "*" + ind;
		String msg = X.f("Wrong type '%s' from %s/'%s' is %s, string '%s'", type, flMark, splitDelStr, err == null ? "NULL" : "ERROR", str);
		throw err == null ? new RequiredRuntimeException(msg) : new RequiredRuntimeException(err, msg);
	}

	public static String firstPath(String str, String... defRq) {
		return TKN.first(str, "/", defRq);
	}

	public static String firstSpace(String str, String... defRq) {
		return TKN.first(str, " ", defRq);
	}

	public static String firstComma(String str, String... defRq) {
		return TKN.first(str, ",", defRq);
	}

	public static String firstDot(String str, String... defRq) {
		return TKN.first(str, ".", defRq);
	}

	public static String firstNum(String heightForm, String... defRq) {
		return TKN.first(heightForm, TKN.ISDIGIT, defRq);
	}
}
