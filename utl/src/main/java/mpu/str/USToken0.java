package mpu.str;


import mpc.exception.RequiredRuntimeException;
import mpu.X;
import mpu.core.ARG;
import org.jetbrains.annotations.Nullable;

public class USToken0 {

	@Nullable
	static <T> T firstType(boolean firstLast, String str, Object splitDelStr, Class<T> type, String first, T[] defRq) {
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
		String msg = X.f("Wrong type '%s' from %s/'%s' is %s, string '%s'", type, firstLast ? "FIRST" : "LAST", splitDelStr, err == null ? "NULL" : "ERROR", str);
		throw err == null ? new RequiredRuntimeException(msg) : new RequiredRuntimeException(err, msg);
	}

	public static String firstPath(String str, String... defRq) {
		return USToken.first(str, "/", defRq);
	}

	public static String firstSpace(String str, String... defRq) {
		return USToken.first(str, " ", defRq);
	}

	public static String firstComma(String str, String... defRq) {
		return USToken.first(str, ",", defRq);
	}

	public static String firstDot(String str, String... defRq) {
		return USToken.first(str, ".", defRq);
	}

	public static String firstNum(String heightForm, String... defRq) {
		return USToken.first(heightForm, USToken.ISDIGIT, defRq);
	}
}