package mpu.str;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

//Analyze
public class STRA {


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

	public static boolean hasOnlySingleCharacter(String str, Character... chars) {
		return getIndexes(str, false, false, chars).size() == 1;
	}

	public static int countMatches(String str, String needle) {
		return StringUtils.countMatches(str, needle);
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

	/**
	 * *************************************************************
	 * ---------------------------- @ --------------------------
	 * *************************************************************
	 */

	public static int firstCharAsNum(int num, int... ind) {
		return Integer.parseInt(String.valueOf(String.valueOf(num).charAt(ind.length == 0 ? 0 : ind[0])));
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

	//
 	//


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

}
