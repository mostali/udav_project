package mpu.str;

import mpu.core.ARG;
import mpu.core.ARR;
import mpu.IT;
import mpu.X;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SPLIT {

	public static String[] by_(String str, String del) {
		return X.empty(str) ? ARR.EMPTY_STR : StringUtils.split(str, del);
	}

	public static List<String> by(String str, String del) {
		return X.empty(str) ? Collections.EMPTY_LIST : ARR.as(by_(str, del));
	}

	public static List<String> bySpace(String str) {
		return byRx(str, "\\s++");
	}

	public static List<String> byRx(String str, String regex) {
		return X.empty(str) ? Collections.EMPTY_LIST : ARR.as(str.split(regex));
	}

	public static List<String> bySpaceFull(String str) {
		return X.empty(str) ? Collections.EMPTY_LIST : ARR.as(str.split(" "));
	}

	public static List<String> byComma(String str) {
		return X.empty(str) ? Collections.EMPTY_LIST : ARR.as(str.split(","));
	}

	public static List<String> bySemicol(String str) {
		return X.empty(str) ? Collections.EMPTY_LIST : ARR.as(str.split(";"));
	}

	public static List<String> byRx(String str, String regex, boolean trim, boolean removeEmpty) {
		String[] ms = str.split(regex);
		List<String> list = new ArrayList<String>();
		for (String s : ms) {
			if (trim) {
				s = s.trim();
			}
			if (!s.isEmpty()) {
				list.add(s);
			} else if (!removeEmpty) {
				list.add(s);
			}
		}
		return list;
	}


	public static String[] bySC(String str, char del, String[]... defRq) {
		return bySC(str, String.valueOf(del), defRq);
	}

	public static String[] bySC(String str, String del, String[]... defRq) {
		return bySC(str, del, -1, defRq);
	}

	public static String[] bySC(String str, char del, int count, String[]... defRq) {
		return bySC(str, String.valueOf(del), count, defRq);
	}

	public static String[] bySC(String str, String del, int count, String[]... defRq) {
		try {
			if (count < 0) {
				String[] ms = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, del, count);
				return ms;
			}
			String[] strings = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, del);
			IT.isLength(strings, count);
			return strings;
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			return X.throwException(ex);
		}
	}

	public static <T> T[] byRxAs(String data, String regex, Class<T> asType, T[]... defRq) {
		try {
			String[] split = data.split(regex);
			return Stream.of(split).map(s -> UST.strTo(s, asType)).toArray(ARR.newArrayIntFunction(asType, split.length));
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static List<String> byNL(String lines) {
		return SPLIT.by(lines, STR.NL);
	}

	public static List<String> byInnerNL(List<String> process) {
		return process.stream().flatMap(s -> byNL(s).stream()).collect(Collectors.toList());
	}

	public static String[] bySpace_(String s) {
		return bySpace(s).toArray(new String[0]);
	}
}
