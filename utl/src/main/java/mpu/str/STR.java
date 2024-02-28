package mpu.str;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import mpu.core.ARG;
import mpu.core.ARGn;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.IT;
import mpu.core.EQ;
import mpc.exception.FIllegalStateException;
import mpc.env.PidUtils;
import mpc.exception.RequiredRuntimeException;
import mpu.core.RW;
import mpe.core.P;
import mpe.rt.SLEEP;
import mpc.str.sym.SEP;
import mpu.pare.Pare;
import mpc.str.sym.SYMJ;
import mpu.core.QDate;
import mpu.Sys;
import mpu.X;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//String Utils
public class STR {

	public static final String NL = System.lineSeparator();
	public static final String TB = "\t";

	public static final String ZERO = "0";

	public static final String TAB = "    ";
	public static final String TAB2 = TAB + TAB;
	public static final String TAB3 = TAB2 + TAB;
	public static final String COL_DEL = SYMJ.COL_DEL;
	public static final String ARR_DEL = SYMJ.ARROW_RIGHT_SPEC;

	public static final String HR = "\n------------------------------------------------------------";
	//
	//
	public static final String ALPHABETIC_EN_LOWER = "abcdefghijklmnopqrstuvwxyz";
	public static final String ALPHABETIC_EN_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String ALPHABETIC_FULL = ALPHABETIC_EN_LOWER + ALPHABETIC_EN_UPPER;
	//
	public static final String ALPHABETIC_NUM = "1234567890";
	public static final String ALPHABETIC_FULL__NUM = ALPHABETIC_FULL + ALPHABETIC_NUM;
	//
	//
	static String s1 = "ZR|AD19125F-BA94-4F98-BD77-703418B525CD|1|62723|18.12.2020|001А9125|Федеральная служба судебных приставов (Управление по исполнению особо важных исполнительных производств)|05951А91250|||322|Федеральная служба судебных приставов|Федеральный бюджет|Министерство финансов Российской Федерации|9500|Межрегиональное операционное управление Федерального казначейства|11.01.2021||||1612175.92|643|0.00|0||5||(05951А91250) Переч. в другое ОСП по распоряж. от должника ОАО \"СГК-трансстройЯмал\", Взыск. по и/п № 220788/15/99001-ИП/СД/СВ выд. 24.05.2016 по Пост.об обращ. взыск. № 16/148044.Без НДС|УФК по Москве (ОСП по ЦАО № 3 УФССП России по Москве, л/с 05731D08000)|7704270863|770845001||40302810045251000079|ГУ БАНКА РОССИИ ПО ЦФО|044525000||05|0|45379000|АР|0|б/н||0|Заместитель начальника Управления|И.В. Кравцов|Ведущий специалист 3 разряда|М.М. Алборова|18.12.2020|ЗКР9500/20-7051|21.12.2020|главный специалист-эксперт|Л.Л. Макарова|(495) 214-88-39|";
	static String s2 = "ZR|A8D605E6-366D-445D-8B33-F02156CA5362|1|62809|21.12.2020|001А9125|Федеральная служба судебных приставов (Управление по исполнению особо важных исполнительных производств)|05951А91250|||322|Федеральная служба судебных приставов|Федеральный бюджет|Министерство финансов Российской Федерации|9500|Межрегиональное операционное управление Федерального казначейства|12.01.2021||||176334.04|643|0.00|0||4||(05951А91250) Переч. ДС в счет погаш. долга: АО \"Севкавказэнерго\" по и/п 6477044/19/99001-ИП, и/л ФС 031745124 от 17.09.19, МУП \"Моздокские электрические сети\" СПИ Малкина М. В. Без НДС|УФК по РСО-Алания (Межрайонный отдел по особым исполнительным производствам УФССП России по РСО -Алания л/с 05101833290)|1516607954|151603002|05101833290|40302810200001000001|ОТДЕЛЕНИЕ-НБ РЕСП. СЕВЕРНАЯ ОСЕТИЯ-АЛАНИЯ|049033001||05|0|90701000|АР|0|ФС 031745124|17.09.2019|0|Заместитель начальника Управления|И.В. Кравцов|Ведущий специалист 3 разряда|М.М. Алборова|21.12.2020|ЗКР9500/20-6183|22.12.2020|главный специалист-эксперт|Л.Л. Макарова|(495) 214-88-39|";

	public static void main(String[] args) {

		P.exit(removeEndsWith("asd", "asda", true));
		P.exit(USToken.two("a", 1));

//		Sys.exit(substr("hello", 1, 2));
		Sys.exit(substrEnd("hello", 0, 3));

		Date truncate = DateUtils.truncate(new Date(), Calendar.DATE);
		P.exit(truncate);

		String pt = "По заявке № 159  от 16.10.2023. Внесение наличными";
		Predicate<Character> noDigitPredicate = character -> !Character.isDigit(character);
		String numWithDate = USToken.trim(pt, noDigitPredicate);//обрежем все что не цифры
		Sys.exit(numWithDate);

		Sys.exit(X.f_("Шаг. 7.1. Расчетный счет плательщика \"%s\" не соответствует маске «40116%».", 12));
		Sys.exit(X.f("hello »%s", 12));
		List list1 = ARR.as(1, 2, 3);
		P.exit(ARRi.lastMany(list1, 0));
		Sys.exit(ARRi.lastMany("1234", 3));
		Sys.exit(ARR.removeLast("12"));
		Sys.exit(cutFirstLast("1abc2fgh3", "1", "3"));
		Sys.exit(rand(0, 3));
//		U.exit(US.getBA("hello)(123)last", ")", false));
//		U.exit(US.getBBA("hello(123)", "(", ")", true));
		Sys.exit(USToken.bwStr("\"id\"", "\"", "\"", false));
		;
		Sys.exit(USToken.bwStr("hello(world)!", "(", ")", true));
		Sys.exit(STR.insertBetween("hello(world)!", "(", ")", true, "ok", true));

		Sys.exit("a;b;d".replace(';', ','));
		List<Integer> list = ARR.list(1, 2, 3);
//		boolean obj = list.retainAll(AR.as(1, 2, 54));
		boolean obj = list.removeAll(ARR.as(1, 2, 54));
		Sys.exit(list);

		while (true) {
			Sys.p(QDate.now() + ":" + PidUtils.getPid_v0());
			if (false) {
				break;
			}
			SLEEP.sleep(1000);
		}
		Sys.exit(X.fm("hello ''{0}''", "as"));
		Sys.exit(UST.BD("01"));
		Sys.exit(ARRi.item(ARR.as(1, 2), 12, null));
		Sys.exit(UST.INT("7d"));
		P.exit(STR.hasOnlySingleCharacter("29000,", '.', ','));
		List columnsText = new ArrayList<>(10);
		columnsText.set(5, 7);
		P.exit(columnsText);
		Sys.exit(substrTo("1234", 5, "a"));
		String s1 = "1s1";
		String s2 = "s2";
		Sys.exit(Objects.hashCode(s1) + ":" + Objects.hashCode(s2));
		P.exit(Integer.MAX_VALUE / 1024);
		int i = 0;
		Sys.p(QDate.now().f(QDate.F.MONO20NF));
		while (true) {
			if (i++ == Integer.MAX_VALUE) {
				Sys.p(QDate.now().f(QDate.F.MONO20NF));
				break;
			}
		}
		Sys.exit(STR.getIndex("simple_code()", true, false, '.', '('));
		Sys.p(s1.split("\\|")[37]);
		Sys.p(s1.split("\\|")[38]);
		Sys.exit(s1.split("\\|")[39]);
//		U.exit(s.split("\\|")[38]);


		Sys.p(Arrays.hashCode(new String[]{"1", "2"}));
		Sys.p(Arrays.hashCode(new String[]{"1", "2"}));
		Sys.exit(new String[]{"1", "2"}.hashCode());//1504109395
		Sys.exit(SPLIT.bySC(",,,,,", ",", 5).length);
		Sys.exit(SPLIT.bySC(",,,,,", ",", 6).length);
		StringBuilder sb = new StringBuilder("123");
		sb.setLength(5);
		Sys.exit(sb.toString());
		Sys.exit(toListCharactersString("123asd"));
//		System.out.println("Aa".hashCode());
//		System.out.println("BB".hashCode());
//		System.out.println("Aa".hashCode());
//		System.out.println(hash("BB".hashCode()));
//
//
//		System.out.println(hash("AaAa".hashCode()));
//		System.out.println(hash("BBBB".hashCode()));
//		System.out.println(hash("AaBB".hashCode()));
//		System.out.println(hash("BBAa".hashCode()));
		Sys.exit();
		USToken.testFirstLastGreedy();
		Sys.exit();
		//https://www.baeldung.com/java-regex-token-replacement
		{
//			String original = "A|B|C\nD|E|F\n";
			String original = "A|B|C||";
			Pattern p = Pattern.compile(".?\\|");

			int lastIndex = 0;
			StringBuilder output = new StringBuilder();
			Matcher matcher = p.matcher(original);
			int col = 0;
			while (matcher.find()) {
				output.append(original, lastIndex, matcher.start()).append(convert(matcher.group(0), col++));
				lastIndex = matcher.end();
			}
//			if (lastIndex < original.length()) {
//				output.append(original, lastIndex, original.length());
//			}

			Sys.exit(output);
		}
		{
			String pat = "A|B|C\nD|E|F\n";
//			String[] ms = pat.split("b");
			Pattern p = Pattern.compile("[\\|]{0,1}(.?)[\\|]{0,1}", Pattern.DOTALL);
			Matcher matcher = p.matcher(pat);
			List<String> d = new ArrayList<>();
			StringBuilder output = new StringBuilder();

			while (matcher.find()) {
//				U.p("g0:" + matcher.group(0));
//				ma
//				d.add(matcher.group(0));
			}
			SEP.EQ__("");
			d = new ArrayList<>();
			matcher = p.matcher(pat);
			while (matcher.find()) {
				Sys.p("g1:" + matcher.group(1));
//				d.add(matcher.group(1));
			}
			Sys.exit(">>>>>>>" + d.size());
		}

		{
			String pat = "abcbc";
//			String[] ms = pat.split("b");
			Pattern p = Pattern.compile("(b.+?){1}", Pattern.DOTALL);
			Matcher matcher = p.matcher(pat);
			List<String> d = new ArrayList<>();
			while (matcher.find()) {
				Sys.p("g0:" + matcher.group(0));
				Sys.p("g1:" + matcher.group(1));
				d.add(matcher.group(1));
			}
			Sys.exit(">>>>>>>" + d.size());
		}
		String pat = "head\nhead2\nZR|contextZKR0\nrow0\nZR|contextZKR1\nrow1\n";
		String[] ms = pat.split("ZR\\|");
		ms = StringUtils.splitByWholeSeparator(pat, "\nZR|");
		Sys.exit(ms.length + ":::" + Arrays.asList(ms));
//		Pattern p=Pattern.compile("(ZR\\|^[ZR\\|]++)");
		Pattern p = Pattern.compile("(ZR\\|.+?){1}^(ZR\\|)", Pattern.DOTALL);
		Matcher matcher = p.matcher(pat);
		List<String> d = new ArrayList<>();
		while (matcher.find()) {
			Sys.p("g0:" + matcher.group(0));
			Sys.p("g1:" + matcher.group(1));
			d.add(matcher.group(1));
		}
		Sys.exit(">>>>>>>" + d.size());


//		U.exit(substrLength("abcde", 1, 5, false));
//
//		U.exit(substrLength("abcde", 1, 5, false));
		Sys.exit(untrim_with_char("\"123\"", "\""));
//		String[] two = two_split("abc-+1ad", "7", true);
		String[] two = USToken.twoByChars("abcd-+1ad", "dabc-+2", USToken.SplitByChars.ALLOWED);

		Sys.p("0:" + two[0]);
		Sys.p("1:" + two[1]);
//		U.exit("2:" + two[2]);
	}

	public static String TAB(int tabLevel) {
		return tabLevel <= 0 ? "" : STR.repeat(TAB, tabLevel);
	}

	/**
	 * *************************************************************
	 * ------------------ ADD HEAD/FOOT (SFX/PFX) ------------------
	 * *************************************************************
	 */

	public static StringBuilder addSfxPfx(CharSequence body, String... pfx_sfx) {
		return addHeadFoot(body, pfx_sfx);
	}

	public static StringBuilder addHeadFoot(CharSequence body, String... head_foot) {
		String head = "";
		String foot = "";
		if (head_foot != null) {
			if (head_foot.length > 1) {
				foot = head_foot[1];
				head = head_foot[0];
			} else if (head_foot.length > 0) {
				head = head_foot[0];
			}
		}
		return new StringBuilder(head).append(body).append(foot);
	}

	/**
	 * *************************************************************
	 * ---------------------------- CONTAINS --------------------------
	 * *************************************************************
	 */


	public static boolean contains(CharSequence seq, String needle, boolean... ignoreCase) {
		return ignoreCase.length > 0 && ignoreCase[0] ? StringUtils.containsIgnoreCase(seq, needle) : StringUtils.contains(seq, needle);
	}

	public static boolean contains(String str, boolean ignoreCase, String... needle) {
		if (str == null || str.isEmpty() || needle == null || needle.length == 0) {
			return false;
		}
		return Stream.of(needle).anyMatch(n -> (ignoreCase ? StringUtils.containsIgnoreCase(str, n) : str.contains(n)));
	}


	public static boolean containsAll(CharSequence val, boolean ignoreCase, String... needles) {
		return Stream.of(IT.NE(needles)).noneMatch(n -> !contains(val, n, ignoreCase));
	}

	/**
	 * *************************************************************
	 * ---------------------------- STARTS --------------------------
	 * *************************************************************
	 */

	public static boolean startsWith(CharSequence seq, boolean ignoreCase, String... needles) {
		return seq == null ? false : (Stream.of(needles).anyMatch(needle -> startsWith(seq, needle, ignoreCase)));
	}

	public static boolean startsWith(CharSequence seq, String needle, boolean... ignoreCase) {
		return seq == null || needle == null ? false : (ignoreCase.length > 0 && ignoreCase[0] ? StringUtils.startsWithIgnoreCase(seq, needle) : StringUtils.startsWith(seq, needle));
	}

	/**
	 * *************************************************************
	 * ---------------------------- ENDS --------------------------
	 * *************************************************************
	 */
	public static boolean endsWith(CharSequence seq, boolean ignoreCase, String... needles) {
		return seq == null ? false : (Stream.of(needles).anyMatch(needle -> endsWith(seq, needle, ignoreCase)));
	}

	public static boolean endsWith(CharSequence seq, String needle, boolean... ignoreCase) {
		return seq == null || needle == null ? false : (ignoreCase.length > 0 && ignoreCase[0] ? StringUtils.endsWithIgnoreCase(seq, needle) : StringUtils.endsWith(seq, needle));
	}

	/**
	 * *************************************************************
	 * ------------------ removeStartsWith & EndsWith  -------------
	 * *************************************************************
	 */
	public static String removeStartsWith(String pattern, String startsWith, boolean... skipError) {
		return removeStartsWith(pattern, false, startsWith, skipError);
	}

	public static String removeStartsWith(String pattern, boolean ignoreCase, String startsWith, boolean... skipError) {
		boolean hasStartWith = startsWith(pattern, ignoreCase, startsWith);
		if (hasStartWith) {
			return pattern.substring(startsWith.length());
		} else if (ARG.isDefEqTrue(skipError)) {
			return pattern;
		}
		throw new RequiredRuntimeException("Except pattern '%s' startsWith '%s'", pattern, startsWith);
	}

	public static String removeEndsWith(String pattern, String endsWith, boolean... skipError) {
		return removeEndsWith(pattern, false, endsWith, skipError);
	}

	public static String removeEndsWith(String pattern, boolean ignoreCase, String endsWith, boolean... skipError) {
		boolean hasEndsWith = endsWith(pattern, ignoreCase, endsWith);
		if (hasEndsWith) {
			return pattern.substring(0, pattern.length() - endsWith.length());
		} else if (ARG.isDefEqTrue(skipError)) {
			return pattern;
		}
		throw new RequiredRuntimeException("Except pattern '%s' endsWith '%s'", pattern, endsWith);
	}

	/**
	 * *************************************************************
	 * ---------------------------- @ --------------------------
	 * *************************************************************
	 */

	public static int firstCharAsNum(int num, int... ind) {
		return Integer.parseInt(String.valueOf(String.valueOf(num).charAt(ind.length == 0 ? 0 : ind[0])));
	}

	public static Integer findFirstSE(String str, char start, char end, boolean checkProtect) {
		boolean first = false;
		int lastIndex = str.length() - 1;
		for (int i = 0; i < lastIndex; i++) {
			if (!first) {
				if (isEqualsCharUnsafe(str, i, start, checkProtect)) {
					first = true;
				}
			} else {
				if (isEqualsCharUnsafe(str, i, end, checkProtect)) {
					return i;
				}
			}
		}
		return null;
	}

	public static boolean isEqualsCharUnsafe(String str, int index, char needle, boolean checkProtected) {
		if (str.charAt(index) != needle) {
			return false;
		} else if (!checkProtected || index == 0) {
			return true;
		}
		return '\\' == str.charAt(index - 1);
	}

	public static boolean isEqualsChar(String str, int index, char needle, boolean checkProtected) {
		return !ARR.isIndex(index, str) ? false : isEqualsCharUnsafe(str, index, needle, checkProtected);
	}

	public static boolean isProtectedChar(String str, int index) {
		if (X.empty(str) || index == 0) {
			return false;
		}
		IT.isIndex(index, str);
		return '\\' == str.charAt(index - 1);
	}


	public static Pare<Character, Integer> getIndex(String str, boolean firstOrLastIndex, boolean skipProtected, Character... chars) {
		IT.notEmpty(str);
		IT.notEmpty(chars);
		int startInd = skipProtected ? 1 : 0;
		if (firstOrLastIndex) {
			for (int i = startInd; i < str.length(); i++) {
				//isEqChar
				if (skipProtected && str.charAt(i - 1) == '\\') {
					continue;
				}
				for (int k = 0; k < chars.length; k++) {
					if (str.charAt(i) == chars[k]) {
						return new Pare(chars[k], i);
					}
				}
			}
		} else {
			for (int i = str.length() - 1; i >= startInd; i--) {
				if (skipProtected && str.charAt(i - 1) == '\\') {
					continue;
				}
				for (int k = 0; k < chars.length; k++) {
					if (str.charAt(i) == chars[k]) {
						return new Pare(chars[k], i);
					}
				}
			}
		}
		return null;
	}

	public static Map<Character, Integer> getIndexes(String str, boolean firstOrLastIndex, boolean skipProtected, Character... chars) {
		IT.notEmpty(str);
		IT.notEmpty(chars);
		Map<Character, Integer> indexes = new HashMap();
//		HashBiMap<Character, Integer> indexes = HashBiMap.create();
//		for (Character ch : chars) {
//			indexes.put(ch, null);
//		}
		for (int i = (skipProtected ? 1 : 0); i < str.length(); i++) {
			if (skipProtected && str.charAt(i - 1) == '\\') {
				continue;
			}
			for (int k = 0; k < chars.length; k++) {
				if (str.charAt(i) != chars[k]) {
					continue;
				}
				Integer ind = indexes.get(chars[k]);
				if (ind == null || !firstOrLastIndex) {
					indexes.put(chars[k], ind);
				}
			}
		}
		return indexes;
	}

	public static Multimap<Character, Integer> getIndexes(String str, boolean skipProtected, Character... chars) {
		IT.notEmpty(str);
		IT.notEmpty(chars);
		Multimap<Character, Integer> indexes = ArrayListMultimap.create();
		for (int i = (skipProtected ? 1 : 0); i < str.length(); i++) {
			if (skipProtected && str.charAt(i - 1) == '\\') {
				continue;
			}
			for (int k = 0; k < chars.length; k++) {
				if (str.charAt(i) != chars[k]) {
					continue;
				}
				indexes.put(chars[k], i);
			}
		}
		return indexes;
	}

//	public static boolean isCharWord(char ch) {
//		return Character.isJ
//	}

	//
	//
	//
//	public static List<String> asList(String string) {
//		List<String> ts = new ArrayList<String>();
//		if (UQ.isEmpty(string)) {
//			return ts;
//		}
//		String[] ms = string.split("\\s+");
//		for (String s : ms)
//			if (!UQ.isEmpty(s)) {
//				ts.add(s);
//			}
//		return ts;
//	}

	public static String unicode16(String unicode16) {
		return unicode10(Integer.parseInt(unicode16, 16));
	}

	public static String unicode10(int unicode) {
		return new String(Character.toChars(unicode));
	}

	public static String trim(String string) {
		if (string == null) {
			return null;
		}
		string = string.replaceAll("\u00A0", "");
		return string.trim();
	}

	public static String trim(String string, boolean removeControlSymbol) {
		if (string == null) {
			return null;
		}
		if (removeControlSymbol) {
			string = removeControlSymbol(string);
		}
		return string.trim();
	}

	/**
	 * Remove control characters
	 */
	public static String removeControlSymbol(String string) {
		if (string == null) {
			return null;
		}
		string = string.replaceAll("\\p{Cc}", "");
		return string;
	}

	public static String removeStartString(String string, String first, boolean... rekursive) {
		while (string.startsWith(first)) {
			string = string.substring(first.length());
			if (ARG.isDefNotEqTrue(rekursive)) {
				break;
			}
		}
		return string;
	}

	public static String removeEndString(String string, String end, boolean... rekursive) {
		while (string.endsWith(end)) {
			string = string.substring(0, string.length() - end.length());
			if (ARG.isDefNotEqTrue(rekursive)) {
				break;
			}
		}
		return string;
	}

	public static String normalizeSpace(String s) {
		return normalizeSpace(s, " ");
	}

	public static String normalizeSpace(String s, String ns) {
		if (s == null) {
			return null;
		}
		//		if (true) {
		//			return s.replaceAll("\\s+", ns);
		//		}
		s = s.replaceAll("\u00A0", ns);
		StringBuilder sb = new StringBuilder();
		boolean foundSpace = true;
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if (c == ' ') {
				if (foundSpace) {
					sb.append(ns);
					foundSpace = false;
				}
			} else {
				sb.append(c);
				foundSpace = true;
			}
		}
		return sb.toString();
	}

	public static Integer cutFirstInt(String s, Integer def) {
		return UST.getInt(cutFirst(s), def);
	}

	public static String cutFirst(String s) {
		if (X.empty(s)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Boolean isFirstDigitalMaster = null;
		do {
			String f = s.substring(0, 1);
			boolean isDigital = f.matches("\\d");
			isFirstDigitalMaster = isFirstDigitalMaster == null ? isDigital : isFirstDigitalMaster;
			if (isFirstDigitalMaster != isDigital) {
				break;
			} else {
				sb.append(f);
			}
			s = s.substring(1);
		} while (s.length() != 0);
		return sb.toString();
	}

	public static Object firstCharUp(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public static String implode(String del, Object... args) {
		return args.length == 0 ? "" : StringUtils.join(args, del);
	}

	public static String replace(Path file, String from, String to) {
		String cnt = RW.readContent(file);
		cnt = cnt.replace(from, to);
		RW.write(file, cnt);
		return cnt;
	}

	public static String[] getBBA(String str, String start, String end, boolean greedyLast, String[]... defRq) {
		try {
			int indStart = str.indexOf(start);
			IT.isNumber(indStart, -1, IT.EQ.NE, "Start string not found", start, str);
			int indEnd = greedyLast ? str.lastIndexOf(end) : str.indexOf(end);
			IT.isNumber(indEnd, -1, IT.EQ.NE, "End string not found", start, str);
			IT.isLT(indStart, indEnd);
			String before = str.substring(0, indStart);
			String body = str.substring(indStart + 1, indEnd);
			String after = str.substring(indEnd + 1);
			return new String[]{before, body, after};
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	//BeforeAfter
	public static String[] getBA(String data, String start, boolean greedyLast, String[]... defRq) {
		try {
			int indStart = greedyLast ? data.lastIndexOf(start) : data.indexOf(start);
			IT.isNumber(indStart, -1, IT.EQ.NE, "Del index not found", start, data);
			String before = data.substring(0, indStart);
			String after = data.substring(indStart + 1);
			return new String[]{before, after};
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	//US.insertBetween("hello(world)!", "(", ")", true, "ok") --> 'hello(ok)!' || 'hellook!' (is keepStartEndString=false)
	public static String insertBetween(String data, String start, String end, boolean greedyLast, String insertData, boolean keepStartEndString, String... defRq) {
		try {
			int indStart = data.indexOf(start);
			IT.isPosOrZero(indStart, "Start string not found", start, data);
			int indEnd = greedyLast ? data.lastIndexOf(end) : data.indexOf(end);
			IT.isPosOrZero(indEnd, "End string not found", start, data);
			IT.isLT(indStart, indEnd, "Start-index must be great that end-index");
			if (keepStartEndString) {
				return data.substring(0, indStart + start.length()) + insertData + data.substring(indEnd);
			} else {
				return data.substring(0, indStart) + insertData + data.substring(indEnd + end.length());
			}
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static String sort(String string) {
		return string.chars().sorted().mapToObj(c -> Character.valueOf((char) c).toString()).collect(Collectors.joining());
	}

	public static Collection<CharSequence> appendToEach(Collection<? extends CharSequence> values, String prefix, String suffix, boolean... returnAsString) {
		List<CharSequence> list = new ArrayList<>();
		boolean isReturnAsString = ARG.isDefEqTrue(returnAsString);
		for (CharSequence value : values) {
			StringBuilder sb = new StringBuilder();
			if (prefix != null) {
				sb.append(prefix);
			}
			sb.append(value);
			if (suffix != null) {
				sb.append(suffix);
			}
			list.add(isReturnAsString ? sb.toString() : sb);
		}
		return list;
	}

	public static String formatAll(Object... messages) {
		return formatAllOr("", messages);
	}

	public static String formatAllOr(String def, Object... messages) {
		if (messages == null) {
			return def;
		}
		switch (messages.length) {
			case 0:
				return def;
			case 1:
				return String.valueOf(messages[0]);
			default:
				Object first = messages[0];
				if (first instanceof CharSequence) {
					String pattern = first.toString();
					if (pattern.contains("%s")) {
						Object[] args = ARR.removeFirst(messages, 1);
						return X.f_(pattern, args);
					}
				}
				StringBuilder sb = new StringBuilder();
				String del = ";;;";
				for (Object message : messages) {
					sb.append(message).append(del);
				}
				return STR.removeEndString(sb.toString(), del);
		}
	}

	public static String substrStartEnd(String str, int startCount, int endCount, String... defRq) {
		int cutLen = startCount + endCount;
		if (cutLen > str.length()) {
			if (ARG.isDef(defRq)) {
				return ARG.toDefRq(defRq);
			}
			throw new RequiredRuntimeException("Too many cut-size (%s) vs string (%s) '%s'", cutLen, str.length(), cutLen <= 20 ? str : toStringSE(str, 10, str));
		} else if (cutLen == str.length()) {
			return "";
		}
		String __str = str.substring(startCount);
		return __str.substring(0, __str.length() - endCount);
	}

	public static String toStringSE(String str, int pfxSfxLen, String... defRq) {
		return substrStartEndInsert(str, pfxSfxLen, pfxSfxLen, "...", defRq);
	}

	public static int countMatches(String str, String needle) {
		return StringUtils.countMatches(str, needle);
	}


	/**
	 * E.g. String p = "-0cut1-"<br>
	 * If call substringOtherPart(p, p.indexOf('0'),p.indexOf('1'))<br>
	 * return "--"<br>
	 */
	public static String substrStartEndInsert(String arg0, int indStartDeletable, int indEndDeletable, String insertBetween, String... defRq) {
		try {
			String res = arg0.substring(0, indStartDeletable) + (insertBetween == null ? "" : insertBetween) + arg0.substring(arg0.length() - indEndDeletable);
			return res;
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static CharSequence randAlpha(int length) {
		return RANDOM.VARIOS(length, RANDOM.RandomStringMode.ALPHA);
	}

	public static CharSequence randAlphaNum(int length) {
		return RANDOM.VARIOS(length, RANDOM.RandomStringMode.ALPHANUMERIC);
	}

	public static int rand(int min, int max) {
		return new Random().nextInt(max - min + 1) + min;
	}

	public static long rand() {
		return new Random().nextLong();
	}

	public static int randINT() {
		return new Random().nextInt();
	}

	public static String randstr(int min, int max) {
		return randAlpha(rand(min, max)).toString();
	}

	public static String getPatternString(String text, Pattern... patterns) {
		return getPatternString(text, 1, patterns);
	}

	public static String getPatternString(String text, int group, Pattern... patterns) {
		if (text == null) {
			return null;
		}
		for (Pattern pattern : patterns) {
			Matcher m = pattern.matcher(text);
			boolean contain = m.find();
			if (contain) {
				return m.group(group);
			}
		}
		return null;
	}

	public static String removeSpaces(String text) {
		return normalizeSpace(text, "");
	}

	public static boolean matches(String name, String... regex) {
		for (String rx : regex) {
			if (name.matches(rx)) {
				return true;
			}
		}
		return false;
	}

	public static String code2base64(String text) {
		byte[] encodedBytes = Base64.getUrlEncoder().encode(text.getBytes());
		return new String(encodedBytes);
	}

	public static String decode2base64(String codedString) {
		byte[] decodedBytes = Base64.getUrlDecoder().decode(codedString);
		return new String(decodedBytes);
	}

	private static String convert(String token, int i) {
		return "*" + i + "|";
	}

	public static String format(String pattern, Object... args) {
		String[] args_ = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			args_[i] = String.valueOf(args[i]);
		}
		return MessageFormat.format(pattern, args_);
	}

	public static String f(String message, Object... args) {
		return String.format(message, args);
	}

	public static String fl(String message, Object... args) {
		return f(message.replace("{}", "%s"), args);
	}

	public static String fm(String message, Object... args) {
		MessageFormat mf = new MessageFormat(message);
		return mf.format(args);
	}

	public static void removeLast(StringBuilder sb, String str) {
		if (str.length() <= sb.length()) {
			sb.delete(sb.length() - str.length(), sb.length());
		}
	}

	public static boolean startsWithMulti(String str, List<String> startPrefix1, List<String> startPrefix2) {
		for (String _startPrefix1 : startPrefix1) {
			for (String _startPrefix2 : startPrefix2) {
				if (startsWith(str, _startPrefix1 + _startPrefix2)) {
					return true;
				}
			}
		}
		return false;
	}

	//https://stackoverflow.com/questions/4052840/most-efficient-way-to-make-the-first-character-of-a-string-lower-case
	public static String decapitalize(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}
		char c[] = string.toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		return new String(c);
	}

	public static String unwrap(String src, String rmm) {
		return removeStartEndString(src, rmm);
	}

	public static String removeStartEndString(String src, String rmm, boolean... recursive) {
		boolean rr = ARG.isDefEqTrue(recursive);
		src = removeStartString(src, rmm, rr);
		src = removeEndString(src, rmm, rr);
		return src;
	}

	public static List<Character> toListCharacters(String str) {
		return str.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
	}

	public static List<String> toListCharactersString(String str) {
//		if (.isEmpty()) {
//			return Collections.EMPTY_LIST;
//		}
		return IT.notNull(str).chars().mapToObj(e -> (char) e).map(String::valueOf).collect(Collectors.toList());
	}

	public static String charAt(String str, int i) {
		return str.charAt(i) + "";
	}

	public static int charAtInt(String str, int i) {
		return charAtInt(str.charAt(i));
	}

	public static int charAtInt(char charAt) {
		return charAt - '0';
	}

	public static String repeat(char c, int length) {
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = c;
		}
		return new String(chars);
	}

	public static String repeat(String str, int count) {
		IT.isPosNotZero(count);
		StringBuilder sb = new StringBuilder();
		do {
			sb.append(str);
		} while (--count > 0);
		return sb.toString();
	}

	public static String nonl(CharSequence rowMsg, String... replaceOn) {
		if (rowMsg == null) {
			return null;
		}
		String rpl = ARG.toDefOr("", replaceOn);
		return rowMsg.toString().replace(NL, rpl);
	}

	public static String toStrLine(String str) {
		return no(str, true, true, true, " ");
	}

	public static String no(String str, boolean trim, boolean removeControlSymbols, boolean normSpaces, String replaceNL) {
		if (str == null) {
			return null;
		}
		if (replaceNL != null) {
			str = nonl(str, replaceNL);
		}
		str = trim ? str.trim() : str;
		str = removeControlSymbols ? removeControlSymbol(str) : str;
		str = normSpaces ? normalizeSpace(str) : str;
		return str;
	}

	public static boolean hasSingleCharacter(String str, char ch) {
		return str.indexOf(ch) == str.lastIndexOf(ch);
	}

	public static boolean hasOnlySingleCharacter(String str, Character... chars) {
		return getIndexes(str, false, false, chars).size() == 1;
	}

	public static boolean isLengthBetwenEq(String str, int min, int max) {
		return str.length() >= min && str.length() <= max;
	}

	public static boolean isLengthBetwen(String str, int min, int max) {
		return str.length() > min && str.length() < max;
	}

	public static boolean isYes(String answer, String... any) {
		return EQ.equalsAny(answer, true, any);
	}

	public static String pfile(String file) {
		return pfile(new File(file));
	}

	public static String pfile(File file) {
		return "file://" + file.getAbsolutePath();
	}

	public static String pfile(Path file) {
		return "file://" + file.toAbsolutePath();
	}

	public static String wrapTag(Object data, String tag) {
		return "<" + tag + ">" + data + "</" + tag + ">";
	}

	public static String wrapTag(Object data, String tag, Object... tagAttrs) {
		if (tagAttrs.length == 0) {
			return wrapTag(data, tag);
		}
		return "<" + tag + " " + JOIN.bySpace(tagAttrs) + ">" + data + "</" + tag + ">";
	}

//	public static String joinNL(List fresh) {
//		return U.join(fresh, NL).toString();
//	}

	public static Charset defaultCharset(Charset... charset) {
		return ARG.isDefNNF(charset) ? charset[0] : Charset.defaultCharset();
	}

	public static String capitalize(String string) {
		return StringUtils.capitalize(string);
	}

	public static String wrapSize(Collection collection, String pfx) {
		return pfx + wrap(X.sizeOf(collection), "(");
	}

	public static String wrap(Object str, String pt) {
		return pt + str + pt;
	}

	public static String wrapIf(Object str, String pt, boolean... wrap) {
		return ARG.isDefEqTrue(wrap) ? pt + str + pt : str.toString();
	}

	public static String wrapIfNot(CharSequence str, String pt, boolean... strict) {
		String vl = str.toString();
		boolean startsWith = vl.startsWith(pt);
		boolean endsWith = vl.endsWith(pt);
		if (startsWith && endsWith) {
			return vl;
		} else if (!startsWith && !endsWith) {
			return pt + vl + pt;
		}
		if (ARG.isDefEqTrue(strict)) {
			throw new FIllegalStateException("Except String with start&end or not part '%s', string:\n", pt, vl.length() < 200 ? vl : ToString.toStringSE(vl, 10));
		}
		return startsWith ? vl + pt : pt + vl;
	}

	public static String wrap(Object str, String start, String end) {
		return start + str + end;
	}

	public static String substrIf(String str, int... maxLength) {
		return ARGn.isDef(maxLength) ? STR.substr(str, 0, ARGn.toDef(maxLength), str) : str;
	}

	public static String addLineIfNN(String msg, String line1) {
		return line1 == null ? msg : msg + "\n" + line1;
	}

	public static Collection<String> toStringList(Collection collection) {
		return (Collection<String>) collection.stream().map(String::valueOf).collect(Collectors.toList());
	}

	public static Collection<String> toStringListFrom(Object[] objects) {
		return Stream.of(objects).map(String::valueOf).collect(Collectors.toList());
	}

	public static String cutFirstLast(String str, String start, String end, String... defRq) {
		String orgStr = str;
		if (str.startsWith(start)) {
			str = str.substring(start.length());
			if (str.endsWith(end)) {
				return str.substring(0, str.length() - end.length());
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("String '%s' must start with '%s' and ends with '%s'", orgStr, start, end), defRq);
	}


	/**
	 * *************************************************************
	 * ---------------------------- SUBSTR --------------------------
	 * *************************************************************
	 */

	public static String substrTo(String str, int length, String... defRq) {
		return length == 0 ? "" : substr(str, 0, length - 1, defRq);
	}

	public static String substr(String str, int startOrEnd, String... defRq) {
		try {
			if (startOrEnd == 0) {
				return str;
			} else if (str.length() == startOrEnd) {
				return "";
			}
			return startOrEnd > 0 ? str.substring(startOrEnd) : str.substring(0, str.length() - -startOrEnd);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error substring length(%s) from pattern '%s'", startOrEnd, str), defRq);
		}
	}

	public static String substr(String str, int startIndex, int endIndex, String... defRq) {
		try {
			IT.isPosOrZero(startIndex);
			if (startIndex == endIndex) {
				return "";
			}
			IT.isGT(endIndex, startIndex);
			return str.substring(startIndex, endIndex + 1);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error substring start(%s) and end(%s) from pattern '%s'", startIndex, endIndex, str), defRq);
		}
	}

	public static String substrEnd(String str, int start, int end, String... defRq) {
		try {
			if (start == end) {
				return "";
			}
			IT.isPosOrZero(start);
			IT.isGT(end, start);
			return str.substring(str.length() - end, str.length() - start);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw ex;
		}
	}

	public static boolean isUpperCase(String str, int... index) {
		return str != null && !str.isEmpty() && Character.isUpperCase(str.charAt(ARGn.toDefOr(0, index)));
	}


//	public static String decapitalizeASCII(String string) {
//		char c[] = string.toCharArray();
//		c[0] += 32;
//		return new String(c);
//	}

	public static String untrim_with_char(String str, String... chars) {
		for (String chs : chars) {
			str = untrim_with_char(str, chs);
		}
		return str;
	}

	public static String untrim_with_char(String str, String chars) {
		if (str.startsWith(chars)) {
			str = str.substring(1);
		}
		if (str.endsWith(chars)) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}


}
