package mpu.str;

import mpu.core.ARG;
import mpu.core.ARR;
import mpu.IT;
import mpu.X;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SPLIT {

	public static void main(String[] args) {
		X.exit(allByLength("abc", 0));
	}

	public static List<String> allBy(String str, String del) {
		return X.empty(str) ? Collections.EMPTY_LIST : ARR.as(argsBy(str, del));
	}

	public static List<String> allByPreserve(String str, String del) {
		return X.empty(str) ? ARR.EMPTY_LIST : ARR.as(argsByPreserve(str, del));
	}

	public static List<String> allBySpace(String str) {
		return allByRx(str, "\\s++");
	}

	public static List<String> allByRx(String str, String regex) {
		return X.empty(str) ? Collections.EMPTY_LIST : ARR.as(str.split(regex));
	}

	public static List<String> allBySpaceStrict(String str) {
		return X.empty(str) ? Collections.EMPTY_LIST : ARR.as(str.split(" "));
	}

	public static List<String> allByComma(String str) {
		return X.empty(str) ? Collections.EMPTY_LIST : ARR.as(str.split(","));
	}

	public static List<String> allByRx(String str, String regex, boolean trim, boolean removeEmpty) {
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

	public static List<String> allByNL(String lines) {
		return SPLIT.allBy(lines, STR.NL);
	}

	public static List<String> allByInnerNL(List<String> process) {
		return process.stream().flatMap(s -> allByNL(s).stream()).collect(Collectors.toList());
	}

	public static List<String> allByLength(String input, int length) {
		IT.notNull(input, "Except not null pattern for split");
		IT.isPosNotZero(length, "Except positive length of part, instead %s", length);
		if (input.length() <= length) {
			return Arrays.asList(input);
		}
		ArrayList<String> result = new ArrayList<>();
		for (int i = 0; i < input.length(); i += length) {
			// Проверяем, чтобы не выйти за пределы строки
			if (i + length <= input.length()) {
				result.add(input.substring(i, i + length));
			} else {
				result.add(input.substring(i, input.length()));
				// Прерываем цикл, если оставшиеся символы меньше заданной длины
				break;
			}
		}
		return result;
	}

	//
	//
	//

	public static String[] argsBy(String str, char del, String[]... defRq) {
		return argsByRq(str, String.valueOf(del), defRq);
	}

	public static String[] argsBy(String str, String del) {
		return X.empty(str) ? ARR.EMPTY_ARGS : StringUtils.split(str, del);
	}

	public static String[] argsByPreserve(String str, String del) {
		return X.empty(str) ? ARR.EMPTY_ARGS : StringUtils.splitByWholeSeparatorPreserveAllTokens(str, del);
	}

	public static String[] argsByNL(String str) {
		return argsBy(str, STR.NL);
	}


	public static String[] argsByRq(String str, String del, String[]... defRq) {
		return argsByRq(str, del, -1, defRq);
	}

	public static String[] argsByRq(String str, char del, int count, String[]... defRq) {
		return argsByRq(str, String.valueOf(del), count, defRq);
	}

	public static String[] argsByRq(String str, String del, int count, String[]... defRq) {
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

	public static <T> T[] argsByRxAsRq(String data, String regex, Class<T> asType, T[]... defRq) {
		try {
			String[] split = data.split(regex);
			return Stream.of(split).map(s -> UST.strTo(s, asType)).toArray(ARR.newArrayIntFunction(asType, split.length));
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static String[] argsBySpace(String s) {
		return allBySpace(s).toArray(new String[0]);
	}

	public static String[] argsByComma(String s) {
		return allByComma(s).toArray(new String[0]);
	}

	public static String[] argsByLength(String input, int length) {
		return allByLength(input, length).toArray(new String[0]);
	}

}
