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
import java.util.function.Function;
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
		String[] two1 = TKN.two("jql:https://job-jira.otr.ru/browse/NSI-3733", ':', null);
		X.exit(two1);
		Integer last = TKN.last("77i7-888-999", "-", Integer::parseInt, null);
		X.exit(last);
		X.exit(TKN.bw("\"777\"", "\"", "\"", true, false));
//		String[] еs = twoPreLast("е", ISDIGIT);
//		X.exit(еs);
//		String[] strings = twoByPos("", 0, null);
		test();


		X.d(TKN.twoByPos("7t", 0), "0"); // [ "", "t"]
		X.d(TKN.twoByPos("7t", 1), "1"); // [ "7", ""]
		X.d(TKN.twoByPos("7t", 2, null), "2"); // null

		X.d(TKN.twoByLen("7t", 0), "000"); // [ "", "7t"]
		X.d(TKN.twoByLen("7t", 1), "111"); // [ "7", "t"]
		X.d(TKN.twoByLen("78t", 1), "1118-1-78t"); //  [ "7", "8t"]
		X.d(TKN.twoByLen("78t", 2), "1118-2-78t"); //  [ "78", "t"]
		X.d(TKN.twoByLen("78t", 3), "1118-3-78t"); //  [ "78t", ""]
		X.d(TKN.twoByLen("7t", 2), "222"); // [ "7t", ""]
		X.d(TKN.twoByLen("7t", 3, null), "333"); // null

		X.exit();
		Predicate<Character> ISDIGIT = c -> Character.isDigit(c);
//		TKN.first("7t", ISDIGIT) //7

//		TKN.last("t7", ISDIGIT) //7

//		X.d(TKN.twoPreFirst("7t", ISDIGIT), ""); // [7,t]
//		X.d(TKN.twoPreLast("7t", ISDIGIT, null), ""); // null

//		X.d(TKN.twoByPos("7t", -1), "0"); // [ "", "7t"]

//		X.d(TKN.twoByPos("7t", 3, null), ""); // null


//		X.p(trimRight("7t", ISDIGIT));
//		X.p(twoFirstOrLastPredicat()FirstPredicat("7t", ISDIGIT)); // ["7", "t"]
//		X.p(twoFirstOrLastPredicat()FirstPredicat("7t", ISDIGIT)); // ["7", "t"]

//		twoGreedy("7t", ISDIGIT.negate()); // ["7", "t"]
		X.exit();
		Integer s1 = TKN.bw("777-888-999", "777-", "-999", Integer.class); // -888-
//		String s1 = TKN.bw("777-888-999", "777", "999"); // -888-
//		String s1 = TKN.bwStrFirst("777-888-999", "777", "999"); // -888-
		X.exit(s1);
		X.exit(TKN.last("777-888-99o9", '-', Integer.class, null));
		String[] two4 = TKN.two("a12", "1");
//		String[] two2 = TKN.twoByPos("a12", 1);

		String[] two = TKN.two("777-888-999", "-");
//		String[] two2 = TKN.twoExc("777-888-999", "-");
//		String[] two3 = TKN.twoExcGreedy("777-888-999", "-");
//		X.exit(ARR.as(two), ARR.as(two2), ARR.as(two3));
		String s = "fn.l";
		X.exit(last(s, "."));
//		X.exit(twoEx(" ", ));;
		X.exit();
//		X.exit(twoEx("a", 0));
//		X.exit(last("as:aa:asd", ":", 3));
		X.exit(first("as:1:asd", ":", 2, Integer.class));
//		X.exit(twoFirstOrLastPredicat("asaa", ISDIGIT));
		X.exit(twoFirst("1as1", ISDIGIT));
		X.exit(twoLast("as1", ISDIGIT));
		X.exit(first("111", ISDIGIT));
		X.exit(last("a111", ISDIGIT));
//		P.exit(two("7ta", "t"));
	}

	private static void test() {
		testTwoByIndex();
		testTwo();
		testTwoByLen();
		testTwoByPos();
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
		IT.state(Arrays.equals(two("7A", "A"), new String[]{"7", ""}), "7A!=" + ARR.as(two("7A", "A")));
		IT.state(Arrays.equals(two("7AA", "A"), new String[]{"7", "A"}), "7AA");
		IT.state(Arrays.equals(twoGreedy("7AA", "A"), new String[]{"7A", ""}), "greedy 7AA" + " != " + ARR.as(twoGreedy("7AA", "A")));
		Sys.p("testTwo OK");
	}

	private static void testTwoByLen() {
		IT.state(Arrays.equals(twoByLen("7", 0), new String[]{"", "7"}));
		IT.state(Arrays.equals(twoByLen("7B", 1), new String[]{"7", "B"}));
		IT.state(Arrays.equals(twoByLen("7B", 0), new String[]{"", "7B"}));
		IT.state(Arrays.equals(twoByLen("7B", 1), new String[]{"7", "B"}));
		IT.state(Arrays.equals(twoByLen("7B", 2), new String[]{"7B", ""}));
		IT.state(Arrays.equals(twoByLen("7B", 3, null), null));

		IT.state(Arrays.equals(twoByLen("7", -1, null), null));
		IT.exceptError(() -> twoByPos("", -1));
		IT.exceptError(() -> twoByPos("", 1));
		IT.exceptError(() -> twoByPos("1", 2));

		X.p("testTwoByLen OK");
	}

	private static void testTwoByPos() {
		IT.state(Arrays.equals(twoByPos("", 0, null), null));
		IT.state(Arrays.equals(twoByPos("7", 0), new String[]{"", ""}));
		IT.state(Arrays.equals(twoByPos("7", 1, null), null));
		IT.state(Arrays.equals(twoByPos("7,", 1), new String[]{"7", ""}));
		IT.state(Arrays.equals(twoByPos("7,B", 1), new String[]{"7", "B"}));
		IT.state(Arrays.equals(twoByPos("7,", 0), new String[]{"", ","}));
		IT.state(Arrays.equals(twoByPos("", -1, null), null));

		IT.exceptError(() -> twoByPos("", 0));
		IT.exceptError(() -> twoByPos("", -1));
		IT.exceptError(() -> twoByPos("", 1));
		IT.exceptError(() -> twoByPos("1", 1));

		X.p("testTwoByPos OK");
	}

	private static void testTwoPredicate() {
		IT.state(Arrays.equals(twoFirst("7t", ISDIGIT), new String[]{"7", "t"}), "7t");
		IT.state(Arrays.equals(twoFirst("7", ISDIGIT), new String[]{"7", ""}), "7");
		IT.state(Arrays.equals(twoLast("t7", ISDIGIT), new String[]{"t", "7"}), "t7");
		IT.state(Arrays.equals(twoLast("7", ISDIGIT), new String[]{"", "7"}), "7");
		IT.state(Arrays.equals(twoLast("7t", ISDIGIT.negate()), new String[]{"7", "t"}), "!7t");

		IT.state(Arrays.equals(twoLast("е", ISDIGIT, null), null), "except err");

		IT.exceptError(() -> twoLast("е", ISDIGIT, null), "no err");

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

	public static String first(String str, Predicate<Character> test, String... defRq) {
		if (str != null) {
			List<Character> last = new ArrayList<>();
			for (int i = 0; i < str.length(); i++) {
				char _char = str.charAt(i);
				if (!test.test(_char)) {
					break;
				}
				last.add(_char);
			}
			if (!last.isEmpty()) {
				//		return last.stream().map(c -> c.toString()).collect(Collectors.joining());
				return JOIN.all(last).toString();
			}
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("Except first string for predicate, from pattern '%s'", str), defRq);
	}


	public static <T> T first(String str, char splitDelStr, Class<T> type, T... defRq) {
		String first = first(str, splitDelStr, null);
		return TKN0.toType(true, str, splitDelStr, type, first, defRq);
	}

	public static <T> T first(String str, String splitDelStr, int index, Class<T> type, T... defRq) {
		String first = first(str, splitDelStr, index, null);
		return TKN0.toType(true, str, splitDelStr, index, type, first, defRq);
	}

	public static String first(String str, String splitDelStr, int index, String... defRq) {
		IT.isPosOrZero(index);
		String[] tokens = SPLIT.argsBy(str, splitDelStr);
		IT.isIndex(index, tokens);
		if (index <= tokens.length) {
			return tokens[index];
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("Token(first) by index '%s' not found from string '%s'", index, str), defRq);
	}

	public static <T> T first(String str, String splitDelStr, Class<T> type, T... defRq) {
		String first = first(str, splitDelStr, null);
		return TKN0.toType(true, str, splitDelStr, type, first, defRq);
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

	public static <T> T first(String str, String splitDelStr, Function<String, T> converter, T... defRq) {
		String last = first(str, splitDelStr, null);
		return TKN0.toType(false, str, splitDelStr, converter, last, defRq);
	}

	public static <T> T firstGreedy(String str, String splitDelStr, Function<String, T> converter, T... defRq) {
		String last = firstGreedy(str, splitDelStr, null);
		return TKN0.toType(false, str, splitDelStr, converter, last, defRq);
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
		return ARG.throwErr(() -> new RequiredRuntimeException("Except last string for predicate, from pattern '%s'", str), defRq);
	}

	public static <T> T last(String str, String splitDelStr, int index, Class<T> type, T... defRq) {
		String last = last(str, splitDelStr, index, null);
		return TKN0.toType(false, str, splitDelStr, index, type, last, defRq);
	}

	public static String last(String str, String splitDelStr, int index, String... defRq) {
		IT.isPosOrZero(index);
		String[] tokens = SPLIT.argsBy(str, splitDelStr);
		IT.isIndex(index, tokens);
		if (index <= tokens.length) {
			return tokens[tokens.length - 1 - index];
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("Token(last) by index '%s' not found from string '%s'", index, str), defRq);
	}

	public static <T> T last(String str, char splitDelStr, Class<T> type, T... defRq) {
		String last = last(str, splitDelStr, null);
		return TKN0.toType(false, str, splitDelStr, type, last, defRq);
	}

	public static <T> T last(String str, String splitDelStr, Class<T> type, T... defRq) {
		String last = last(str, splitDelStr, null);
		return TKN0.toType(false, str, splitDelStr, type, last, defRq);
	}

	public static <T> T last(String str, String splitDelStr, Function<String, T> converter, T... defRq) {
		String last = last(str, splitDelStr, null);
		return TKN0.toType(false, str, splitDelStr, converter, last, defRq);
	}

	public static <T> T lastGreedy(String str, String splitDelStr, Class<T> type, T... defRq) {
		String last = lastGreedy(str, splitDelStr, null);
		return TKN0.toType(false, str, splitDelStr, type, last, defRq);
	}

	public static <T> T lastGreedy(String str, String splitDelStr, Function<String, T> converter, T... defRq) {
		String last = lastGreedy(str, splitDelStr, null);
		return TKN0.toType(false, str, splitDelStr, converter, last, defRq);
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

	public static <T> T bw(String str, String start, String end, Class<T> asType, T... defRq) {
		String s = TKN.bw(str, start, end, true, true);
		if (s != null) {
			return UST.strTo(s, asType, defRq);
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("Error cutting token between start&end '%s'<->'%s', string:%s", start, end, str));
	}

	public static String bw(String pattern, String start, String end, String... defRq) {
		return bw(pattern, start, end, true, true, defRq);
	}

	public static String bw(String str, String start, String end, boolean firstLast_StartIndex, boolean firstLast_EndIndex, String... defRq) {
		try {
			int indStart = firstLast_StartIndex ? str.indexOf(start) : str.lastIndexOf(start);
			IT.isPosOrZero(indStart, "Start string not found", start, str);
			indStart += start.length();
			int indEnd = firstLast_EndIndex ? str.indexOf(end) : str.lastIndexOf(end);
			IT.isPosOrZero(indEnd, "End string not found", start, str);
			IT.isLT(indStart, indEnd, "Start-index must be great that end-index");
			if (indStart == indEnd) {
				return "";
			}
			IT.state(indStart < indEnd, "start needle index [%s] > end needle index [%s] in string:%s", str, start, end);
			return str.substring(indStart, indEnd);
		} catch (Exception ex) {
			return ARG.throwMsg(ex, defRq);
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
			return ARG.throwErr(() -> new RequiredRuntimeException(ex, "Error get two as type '%s' from pattern '%s' wit del '%s'", asType, str, splitDelStr), defRq);
		}
	}

	public static String[] twoFirst(String str, Predicate test, String[]... defRq) {
		String first = first(str, test, null);
		if (first != null) {
			return new String[]{first, str.substring(first.length())};
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("Error get two by predicate from pattern '%s'", str), defRq);
	}

	public static String[] twoLast(String str, Predicate test, String[]... defRq) {
		String last = last(str, test, null);
		if (last != null) {
			return new String[]{str.substring(0, str.length() - last.length()), last};
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("Error get two(last) by predicate from pattern '%s'", str), defRq);
	}

	public static String[] two(String str, String splitDelStr, String[]... defRq) {
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
		return ARG.throwMsg(new RequiredRuntimeException("Pattern '%s' need delimiter '%s'", str, splitDelStr), defRq);
	}


	public static String[] two(String str, int splitByPos, String[]... defRq) {
		if (str.length() > 0 && splitByPos <= str.length() && splitByPos >= 0) {
			return new String[]{str.substring(0, splitByPos), str.substring(splitByPos)};
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("Error getting two token's from string '%s' by split position '%s'", str, splitByPos), defRq);
	}


	public static String[] twoByPos(String str, int pos, String[]... defRq) {
		if (str != null && pos >= 0 && pos < str.length()) {
			if (str.length() == pos) {
				return new String[]{"", pos == 0 ? "" : str};
			}
			if (pos == 0) {
				return new String[]{"", str.substring(1)};
			}
			if (pos == str.length() - 1) {
				return new String[]{str.substring(0, pos), ""};
			}
			return new String[]{str.substring(0, pos), str.substring(pos + 1)};
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("Error getting two token's from string '%s' by split position '%s'", str, pos), defRq);
	}

	public static String[] twoByLen(String str, int len, String[]... defRq) {
		if (str.length() > 0 && len >= 0 && len <= str.length()) {
			if (len == 0) {
				return new String[]{"", str};
			}
			if (len == str.length()) {
				return new String[]{str, ""};
			}
			if (len == str.length() - 1) {
				return new String[]{str.substring(0, len), str.substring(len)};
			}
			return new String[]{str.substring(0, len), str.substring(len)};
		}
		return ARG.throwErr(() -> new RequiredRuntimeException("Error getting two token's from string '%s' by split position '%s'", str, len), defRq);
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

	public static String firstOr(String str, String defIfNotFound, Character... charsDel) {
		if (str == null || str.isEmpty()) {
			return defIfNotFound;
		}
		char[] chars = str.toCharArray();
		int endIndex = -1;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (ARR.contains(charsDel, c)) {
//			if (c == ' ' || c == ':' || c == '\n') {
				endIndex = i;
				break;
			}
		}
		return endIndex == -1 ? defIfNotFound : new String(chars, 0, endIndex);
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
