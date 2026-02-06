package mpu.str;

import mpu.core.ARG;
import mpu.IT;
import mpu.core.ARR;
import mpc.exception.RequiredRuntimeException;
import mpu.Sys;
import mpu.X;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;

//```
//Кусает строки спереди, сзади по делиметру, попалам, между, по предикату, trim

/// / Во всех случаях если делиметр в строке не найден - кидаем ошибку - либо возвращаем дефолтное значение ( если передали ..defRq )
/// / Основные методы умеют кастить до типов - если передать соотв. тип
//
/// /получить токен до/после делиметра
/// /
//USToken.first("777-888-999", '-') // 777
//USToken.firstGreedy("777-888-999", '-') // 777-888
//
//USToken.last("777-888-999", '-') // 999
//USToken.lastGreedy("777-888-999", '-') // 888-999
//
/// /получить токен следующий за делиметром
/// /
//USToken.startWith("777-888-999", '777') // '-888-999'
//USToken.endsWith("777-888-999", '999') // '777-888-'
//
//
/// /разделить строку на два токена по делиметру
/// /
//USToken.two("777-888-999", '-') // {"777","888-999"}
//USToken.twoGreedy("777-888-999", '-') // {"777-888","999"}
//
/// /получаем токены по предикату
/// /
//Predicate<Character> ISDIGIT = c -> Character.isDigit(c);
//
/// /получить первый/последний
//USToken.first("7t", ISDIGIT) //7
//USToken.last("t7", ISDIGIT) //7
//

/// /получить два токена - до и после предиакта
//two("7t", ISDIGIT) // ["7", "t"]
//twoFromEnd("7t", ISDIGIT.negate()) // ["7", "t"]
//```

//Util String - Parse Token
public class TKN {

	public static void main(String[] args) {

		String s = "fn.l";
		X.exit(last(s, "."));
//		X.exit(twoEx(" ", ));;
		test();
		X.exit();
//		X.exit(two("a1", 1));
//		X.exit(twoEx("a", 0));
//		X.exit(last("as:aa:asd", ":", 3));
		X.exit(first("as:1:asd", ":", 2, Integer.class));
		X.exit(twoFirstOrLastPredicat("asaa", ISDIGIT));
		X.exit(twoFirstPredicat("1as1", ISDIGIT));
		X.exit(twoLastPredicat("as1", ISDIGIT));
		X.exit(first("111", ISDIGIT));
		X.exit(last("a111", ISDIGIT));
//		P.exit(two("7ta", "t"));
	}

	private static void test() {
		testTwoByIndex();
		testTwo();
		testTwoEx();
		testTwoPredicate();
		testPredicate();
		testFirstLastGreedy();
	}

	//
	//*****************************First & Last *****************************
	//
	private static void testTwoByIndex() {
		IT.state(Arrays.equals(two(" ", 0), new String[]{"", " "}));
		IT.state(Arrays.equals(two(" ", 1), new String[]{" ", ""}));
		IT.exceptError(() -> two("", 0));
		Sys.p("testTwoByIndex OK");
	}

	private static void testTwo() {
		IT.state(Arrays.equals(two("7,", ","), new String[]{"7", ""}), "7,!=" + ARR.as(two("7,", ",")));
		IT.state(Arrays.equals(two("7,,", ","), new String[]{"7", ","}), "7,,");
		IT.state(Arrays.equals(twoGreedy("7,,", ","), new String[]{"7,", ""}), "greedy 7,," + " != " + ARR.as(twoGreedy("7,,", ",")));
		Sys.p("testTwo OK");
	}

	private static void testTwoEx() {
		IT.state(Arrays.equals(twoExc("7", 0), new String[]{"", "7"}));
		IT.state(Arrays.equals(twoExc("7,", 1), new String[]{"7", ""}));
		IT.state(Arrays.equals(twoExc("7,", 0), new String[]{"", "7,"}));
		IT.state(Arrays.equals(twoExc("7,", 1), new String[]{"7", ""}));
//		IT.state(Arrays.equals(, new String[]{"7", ""}));
		IT.exceptError(() -> twoExc("", 0));

//		IT.state(Arrays.equals(twoGreedy("7,,", ","), new String[]{"7,", ""}), "greedy 7,," + " != " + ARR.as(twoGreedy("7,,", ",")));
		Sys.p("testTwo OK");
	}

	private static void testTwoPredicate() {
		IT.state(Arrays.equals(twoFirstPredicat("7t", ISDIGIT), new String[]{"7", "t"}), "7t");
		IT.state(Arrays.equals(twoFirstPredicat("7", ISDIGIT), new String[]{"7", ""}), "7");
		IT.state(Arrays.equals(twoLastPredicat("t7", ISDIGIT), new String[]{"t", "7"}), "t7");
		IT.state(Arrays.equals(twoLastPredicat("7", ISDIGIT), new String[]{"", "7"}), "7");
		IT.state(Arrays.equals(twoLastPredicat("7t", ISDIGIT.negate()), new String[]{"7", "t"}), "!7t");
		Sys.p("testTwoPredicate OK");
	}

	private static void testPredicate() {
		IT.state("7".equals(first("7t", ISDIGIT)));
		IT.state("7".equals(last("t7", ISDIGIT)));
		Sys.p("testPredicate OK");
	}


	public static void testFirstLastGreedy() {
//		Sys.exit(two("/a.b.c", "/a.", null));
		IT.isEq(first("a.b.c", "."), "a", "first error");//a
		IT.isEq(firstGreedy("a.b.c", "."), "a.b", "firstGreedyQk error");//a.b
		IT.isEq(firstGreedy("a.b.c", '.'), "a.b", "firstGreedyQk char error");//a.b

		IT.isEq(last("a.b.c", "."), "c", "lastQk error");//c
		IT.isEq(lastGreedy("a.b.c", "."), "b.c", "lastGreedyQk error");//b.c
//		U.p(UC.isEq(lastRegex("a.b.c", "\\."), "c", "lastRegex error"));//c
		Sys.p("testFirstLastGreedy OK");

	}

	public static final Predicate<Character> ISDIGIT = c -> Character.isDigit(c);

	/**
	 * First Token
	 * return "a.b.c" - > "a"
	 */

	public static String first(String str, Predicate<Character> testCharacter, String... defRq) {
		if (str != null) {
			List<Character> last = new ArrayList<>();
			for (int i = 0; i < str.length(); i++) {
				char _char = str.charAt(i);
				if (!testCharacter.test(_char)) {
					break;
				}
				last.add(_char);
			}
			if (!last.isEmpty()) {
				//		return last.stream().map(c -> c.toString()).collect(Collectors.joining());
				return JOIN.all(last).toString();
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except first string for predicate, from pattern '%s'", str), defRq);
	}


	public static <T> T first(String str, char splitDelStr, Class<T> type, T... defRq) {
		String first = first(str, splitDelStr, null);
		return USToken0.toType(true, str, splitDelStr, type, first, defRq);
	}

	public static <T> T first(String str, String splitDelStr, int index, Class<T> type, T... defRq) {
		String first = first(str, splitDelStr, index, null);
		return USToken0.toType(true, str, splitDelStr, index, type, first, defRq);
	}

	public static String first(String str, String splitDelStr, int index, String... defRq) {
		IT.isPosOrZero(index);
		String[] tokens = SPLIT.argsBy(str, splitDelStr);
		IT.isIndex(index, tokens);
		if (index <= tokens.length) {
			return tokens[index];
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Token(first) by index '%s' not found from string '%s'", index, str), defRq);
	}

	public static <T> T first(String str, String splitDelStr, Class<T> type, T... defRq) {
		String first = first(str, splitDelStr, null);
		return USToken0.toType(true, str, splitDelStr, type, first, defRq);
	}

	public static String first(String str, char splitDelStr, String... defRq) {
		return firstOrFirstGreedy(true, str, splitDelStr, defRq);
	}

	public static String first(String str, String splitDelStr, String... defRq) {
		return firstOrFirstGreedy(true, str, splitDelStr, defRq);
	}

	/**
	 * First Token Greedy
	 * return "a.b.c" - > "a.b"
	 */
	public static String firstGreedy(String str, char splitDelStr, String... defRq) {
		return firstOrFirstGreedy(false, str, splitDelStr, defRq);
	}

	public static String firstGreedy(String str, String splitDelStr, String... defRq) {
		return firstOrFirstGreedy(false, str, splitDelStr, defRq);
	}

	private static String firstOrFirstGreedy(boolean firstOrGreedy, String str, Object splitDelStr, String... defRq) {
		if (str != null) {
			boolean isChar = splitDelStr instanceof Character;
			int ind = firstOrGreedy ? (isChar ? str.indexOf((char) splitDelStr) : str.indexOf((String) splitDelStr)) : (isChar ? str.lastIndexOf((char) splitDelStr) : str.lastIndexOf((String) splitDelStr));
			if (ind != -1) {
				return str.substring(0, ind);
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("First%s token is null in string `%s`, Delimeter `%s`", firstOrGreedy ? "" : "(Greedy)", str, splitDelStr);
	}

	/**
	 * Last Token
	 * return "a.b.c" - > "c"
	 */

	public static String last(String str, Predicate<Character> testCharacter, String... defRq) {
		if (str != null) {
			List<Character> last = new ArrayList<>();
			for (int i = str.length() - 1; i > -1; i--) {
				char _char = str.charAt(i);
				if (!testCharacter.test(_char)) {
					break;
				}
				last.add(_char);
			}
			Collections.reverse(last);
			if (!last.isEmpty()) {
				//		return last.stream().map(c -> c.toString()).collect(Collectors.joining());
				return JOIN.all(last).toString();
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except last string for predicate, from pattern '%s'", str), defRq);
	}

	public static <T> T last(String str, String splitDelStr, int index, Class<T> type, T... defRq) {
		String last = last(str, splitDelStr, index, null);
		return USToken0.toType(false, str, splitDelStr, index, type, last, defRq);
	}

	public static String last(String str, String splitDelStr, int index, String... defRq) {
		IT.isPosOrZero(index);
		String[] tokens = SPLIT.argsBy(str, splitDelStr);
		IT.isIndex(index, tokens);
		if (index <= tokens.length) {
			return tokens[tokens.length - 1 - index];
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Token(last) by index '%s' not found from string '%s'", index, str), defRq);
	}

	public static <T> T last(String str, char splitDelStr, Class<T> type, T... defRq) {
		String last = last(str, splitDelStr, null);
		return USToken0.toType(false, str, splitDelStr, type, last, defRq);
	}

	public static <T> T last(String str, String splitDelStr, Class<T> type, T... defRq) {
		String last = last(str, splitDelStr, null);
		return USToken0.toType(false, str, splitDelStr, type, last, defRq);
	}

	public static <T> T lastGreedy(String str, String splitDelStr, Class<T> type, T... defRq) {
		String last = lastGreedy(str, splitDelStr, null);
		return USToken0.toType(false, str, splitDelStr, type, last, defRq);
	}

	public static String last(String str, char splitDelStr, String... defRq) {
		return lastOrLastGreedy(true, str, splitDelStr, 1, defRq);
	}

	public static String last(String str, String splitDelStr, String... defRq) {
		return lastOrLastGreedy(true, str, splitDelStr, splitDelStr.length(), defRq);
	}

	/**
	 * Last Token Greedy
	 * return "a.b.c" - > "b.c"
	 */
	public static String lastGreedy(String str, char splitDelStr, String... defRq) {
		return lastOrLastGreedy(false, str, splitDelStr, 1, defRq);
	}

	public static String lastGreedy(String str, String splitDelStr, String... defRq) {
		return lastOrLastGreedy(false, str, splitDelStr, splitDelStr.length(), defRq);
	}

	private static String lastOrLastGreedy(boolean lastOrLastGreedy, String str, Object splitDelStr, int size, String... defRq) {
		if (str != null) {
			boolean isChar = splitDelStr instanceof Character;
			int ind;
			if (lastOrLastGreedy) {
				ind = isChar ? str.lastIndexOf((char) splitDelStr) : str.lastIndexOf((String) splitDelStr);
			} else {
				ind = isChar ? str.indexOf((char) splitDelStr) : str.indexOf((String) splitDelStr);
			}
			if (ind != -1) {
				return str.substring(ind + size);
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Last%s token is null in string [%s], Delimeter [%s]", lastOrLastGreedy ? "" : "(Greedy)", str, splitDelStr);
	}

	/**
	 * *************************************************************
	 * ---------------------------- NEXT -----------------------
	 * *************************************************************
	 */

	public static String startWith(String str, String startWith, String... defRq) {
		return startWith(str, startWith, false, defRq);
	}

	public static String startWith(String str, String startWith, boolean ignoreCase, String... defRq) {
		boolean isStartWith = ignoreCase ? StringUtils.startsWithIgnoreCase(str, startWith) : StringUtils.startsWith(str, startWith);
		if (isStartWith) {
			return str.substring(startWith.length());
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Substr%s token not found in string [%s], StartPart [%s]", ignoreCase ? "" : "(IgnoreCase)", str, startWith);
	}

	public static String endsWith(String str, String endsWith, String... defRq) {
		return endsWith(str, endsWith, false, defRq);
	}

	public static String endsWith(String str, String endsWith, boolean ignoreCase, String... defRq) {
		boolean isEndWith = ignoreCase ? StringUtils.endsWithIgnoreCase(str, endsWith) : StringUtils.endsWith(str, endsWith);
		if (isEndWith) {
			return str.substring(0, str.length() - endsWith.length());
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("SubstrEnd%s token not found in string [%s], StartPart [%s]", ignoreCase ? "" : "(IgnoreCase)", str, endsWith);
	}


	/**
	 * *************************************************************
	 * ------------------------- BETWEEN ----------------------
	 * *************************************************************
	 */


	public static String bw(String str, String start, String end, String... defRq) {
		return bw(str, start, end, String.class, defRq);
	}

	public static <T> T bw(String str, String start, String end, Class<T> asType, T... defRq) {
		String s = bwStr(str, start, end, true);
		if (s != null) {
			return UST.strTo(s, asType, defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Error cutting token between start&end '%s'<->'%s', string:%s", start, end, str));
	}

	public static String bwStrFirst(String pattern, String start, String end, String... defRq) {
		return bwStr(pattern, start, end, true, defRq);
	}

	public static String bwStrLast(String str, String start, String end, String... defRq) {
		return bwStr(str, start, end, false, defRq);
	}

	//StringUtils.substringBetween(String.valueOf(targetValueParamValue), "#{", "}");
	//US.substringBetween("hello(world)!", "(", ")", true)) --> 'world'
	//analog cutFirstLast
	public static String bwStr(String str, String start, String end, boolean firstLast, String... defRq) {
		try {
			int indStart = str.indexOf(start);
			IT.isPosOrZero(indStart, "Start string not found", start, str);
			indStart += start.length();
			int indEnd = firstLast ? str.indexOf(end) : str.lastIndexOf(end);
			IT.isPosOrZero(indEnd, "End string not found", start, str);
			IT.isLT(indStart, indEnd, "Start-index must be great that end-index");
			return str.substring(indStart, indEnd);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	/**
	 * *************************************************************
	 * ------------------------- TWO ----------------------
	 * *************************************************************
	 */

	public static <T> T[] twoAs(String str, String splitDelStr, Class<T> asType, T[]... defRq) {
		try {
			String[] two = two(str, splitDelStr, new String[0]);
			T[] twoAs = (T[]) Array.newInstance(asType, 2);
			twoAs[0] = UST.strTo(two[0], asType);
			twoAs[1] = UST.strTo(two[1], asType);
			return twoAs;
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error get two as type '%s' from pattern '%s' wit del '%s'", asType, str, splitDelStr), defRq);
		}
	}

	public static String[] twoFirstOrLastPredicat(String str, Predicate test, String[]... defRq) {
		if (X.empty(str)) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Except not empty string for predicate"), defRq);
		}
		boolean firstOrLast = test.test(str.charAt(0));
		return firstOrLast ? twoFirstPredicat(str, test, defRq) : twoLastPredicat(str, test, defRq);
	}

	public static String[] twoLastOrFirstPredicat(String str, Predicate test, String[]... defRq) {
		if (X.empty(str)) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Except not empty string for predicate"), defRq);
		}
		boolean firstOrLast = test.test(str.charAt(str.length() - 1));
		return firstOrLast ? twoFirstPredicat(str, test, defRq) : twoLastPredicat(str, test, defRq);
	}

	public static String[] twoFirstPredicat(String str, Predicate test, String[]... defRq) {
		String first = first(str, test, null);
		if (first != null) {
			return new String[]{first, str.substring(first.length())};
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Error get two by predicate from pattern '%s'", str), defRq);
	}

	public static String[] twoLastPredicat(String str, Predicate test, String[]... defRq) {
		String last = last(str, test, null);
		if (last != null) {
			return new String[]{str.substring(0, str.length() - last.length()), last};
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Error get two(last) by predicate from pattern '%s'", str), defRq);
	}

	public static String[] two(String str, String splitDelStr, String[]... defRq) {
		return two(str, splitDelStr, false, defRq);
	}

	@Deprecated
	public static String[] twoQk(String str, String splitDelStr, String[]... defRq) {
		int si = str.indexOf(" ");
		if (si == -1) {
			return new String[]{str, ""};
		}
		return two(str, splitDelStr, false, defRq);
	}

	public static String[] twoGreedy(String str, String splitDelStr, String[]... defRq) {
		return two(str, splitDelStr, true, defRq);
	}

	public static String[] two(String str, String splitDelStr, boolean greedy, String[]... defRq) {
		String first = greedy ? firstGreedy(str, splitDelStr, null) : first(str, splitDelStr, null);
		if (first != null) {
			int len = first.length() + splitDelStr.length();
			String second = len == str.length() ? "" : str.substring(len);
			return new String[]{first, second};
		}
		return ARG.toDefThrow(new RequiredRuntimeException("Pattern '%s' need delimiter '%s'", str, splitDelStr), defRq);
	}


	public static String[] two(String str, int splitByPos, String[]... two) {
		if (str.length() > 0 && splitByPos <= str.length() && splitByPos >= 0) {
			return new String[]{str.substring(0, splitByPos), str.substring(splitByPos)};
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Error getting two token's from string '%s' by split position '%s'", str, splitByPos), two);
	}

	//exclude
	public static String[] twoExc(String str, int splitByPos, String[]... two) {
		if (str.length() > 0 && splitByPos <= str.length() && splitByPos >= 0) {
			if (splitByPos == 0) {
				return new String[]{"", str.substring(splitByPos)};
			}
			if (splitByPos == str.length() - 1) {
				return new String[]{str.substring(0, splitByPos), ""};
			}
			return new String[]{str.substring(0, splitByPos), str.substring(splitByPos + 1)};
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Error getting two token's from string '%s' by split position '%s'", str, splitByPos), two);
	}

	public static String[] twoByChars(String str, String chars, SplitByChars typeSplitChars) {
		IT.notEmpty(str, "String is empty");
		IT.notEmpty(chars, "String with chars is empty");
		for (int i = 0; i < str.length(); i++) {
			int ind = chars.indexOf(str.codePointAt(i));
			boolean isContainsChar = ind != -1;
			if (isContainsChar && SplitByChars.ALLOWED == typeSplitChars) {
				continue;
			} else if (!isContainsChar && SplitByChars.DELIMETER == typeSplitChars) {
				continue;
			} else {
				return new String[]{str.substring(0, i), str.substring(i)};
			}
		}
		return new String[]{str, ""};
	}

	/**
	 * *************************************************************
	 * ------------------------- TRIM ----------------------
	 * *************************************************************
	 */


	public static String trim(String str, Predicate<Character> trimCharacter) {
		str = trimLeft(str, trimCharacter);
		str = trimRight(str, trimCharacter);
		return str;
	}

	public static String trimLeft(String str, Predicate<Character> trimCharacter) {
		while (str.length() > 0 && trimCharacter.test(str.charAt(0))) {
			str = str.substring(1);
		}
		return str;
	}

	public static String trimRight(String str, Predicate<Character> trimCharacter) {
		while (str.length() > 0 && trimCharacter.test(str.charAt(str.length() - 1))) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * *************************************************************
	 * ------------------------- TWO ----------------------
	 * *************************************************************
	 */

	public enum SplitByChars {
		DELIMETER, ALLOWED;

		public String[] two(String string, String chars) {
			return twoByChars(string, chars, this);
		}
	}
}
