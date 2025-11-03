package mpu.core;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import mpc.rfl.RFL;
import mpu.X;
import mpu.IT;

import java.util.List;
import java.util.Map;
import java.util.Objects;

//Equals
public class EQ {

	public static boolean equals(Object o1, Object o2, boolean safeEquals) {
		return o1 == null && o2 == null ? !safeEquals : Objects.equals(o1, o2);
	}

	public static boolean equalsSafeAsStrings(Object o1, Object o2, boolean ignoreCase) {
		return o1 == null && o2 == null ? false : (o1 == null ? false : (ignoreCase ? o1.toString().equalsIgnoreCase(o2.toString()) : o1.toString().equals(o2.toString())));
	}

	public static boolean equalsSafe(Object o1, Object o2) {
		return o1 == null && o2 == null ? false : o1 == null ? false : o1.equals(o2);
	}

	public static boolean equalsUnsafe(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	public static boolean equalsUnsafe(String str1, String str2, boolean... ignoreCase) {
		return str1 == null ? str2 == null : ARG.isDefEqTrue(ignoreCase) ? str1.equalsIgnoreCase(str2) : str1.equals(str2);
	}

	public static boolean eq(Object v1, Object v2) {
		return equals(v1, v2);
	}

	public static boolean equals(Object v1, Object v2) {
		return equalsSafe(v1, v2);
	}

	public static boolean neq(Object v1, Object v2) {
		return notEquals(v1, v2);
	}

	public static boolean notEquals(Object v1, Object v2) {
		return !equalsSafe(v1, v2);
	}

	public static boolean notEqualsUnsafe(Object v1, Object v2) {
		return !equalsUnsafe(v1, v2);
	}

	public static boolean equalsAnySafe(Object checkable, Object... with) {
		return equalsAny(checkable, true, with);
	}

	public static boolean equalsAny(Object checkable, boolean safeEquals, Object... with) {
		for (Object w : with) {
			if (equals(checkable, w, safeEquals)) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean equalsAny(List items, boolean safeEquals, Object... any) {
		if (X.empty(items) || X.empty(any)) {
			return false;
		}
		for (Object item : items) {
			if (EQ.equalsAny(item, safeEquals, any)) {
				return true;
			}
		}
		return false;
	}

	public static boolean notEqualsAnySafe(Object checkable, Object... with) {
		return !equalsAnySafe(checkable, true, with);
	}

	public static boolean notEqualsAny(Object checkable, boolean safeEquals, Object... with) {
		return !equalsAny(checkable, safeEquals, with);
	}

	/**
	 * *************************************************************
	 * --------------------------- Equals Number's --------------------------
	 * *************************************************************
	 */

	public static boolean equalsNumbers(Number n1, Number n2) {
		return n1 == null || n2 == null ? false : (n1 == n2 ? true : (n1.equals(n2) ? true : n1.doubleValue() == n2.doubleValue()));
	}

	/**
	 * *************************************************************
	 * --------------------------- Equals String's --------------------------
	 * *************************************************************
	 */
	public static boolean equalsStringIgnoreCaseAny(String str1, String... str2) {
		if (X.empty(str2)) {
			return false;
		}
		for (String str : str2) {
			if (equalsStringIgnoreCase(str1, str)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equalsStringIgnoreCase(String str1, String str2) {
		return equalsString(str1, str2, true, true);
	}


	public static boolean equalsStringAny(String str1, String... any) {
		return equalsStringAny(str1, false, any);
	}

	public static boolean equalsStringsAny(List items, boolean ignoreCase, Object... any) {
		if (X.empty(items) || X.empty(any)) {
			return false;
		}
		for (Object item : items) {
			for (Object itemEnum : any) {
				if (equalsSafeAsStrings(item, itemEnum, ignoreCase)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean equalsStringAny(List items, boolean ignoreCase, String... any) {
		if (X.empty(items) || X.empty(any)) {
			return false;
		}
		for (Object item : items) {
			if (item != null && equalsStringAny(item.toString(), ignoreCase, any)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equalsStringAny(String str1, boolean ignoreCase, String... any) {
		IT.notEmpty(any);
		for (String str : any) {
			if (equalsString(str1, str, ignoreCase, true)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equalsString(String str1, String str2) {
		return equalsString(str1, str2, false, true);
	}

	public static boolean equalsString(String str1, String str2, boolean... ignoreCase) {
		return equalsString(str1, str2, ARG.isDefEqTrue(ignoreCase), true);
	}

	public static boolean equalsString(String str1, String str2, boolean ignoreCase, boolean safeEquals) {
		if (str1 == null || str2 == null) {
			return safeEquals ? false : str1 == null && str2 == null;
		}
		return str1 == null ? false : (ignoreCase ? str1.equalsIgnoreCase(str2) : str1.equals(str2));
	}

	public static boolean equalsMaps(Map map1, Map map2) {
		return equalsMaps(map1, map2, null, false);
	}

	public static boolean equalsMapsAsStrings(Map map1, Map map2) {
		return String.valueOf(map1).equals(String.valueOf(map2));
	}

	public static boolean equalsMaps(Map map1, Map map2, Boolean checkKeysMap1_or_KeysMap2_or_Equals, boolean... eqUnsafe) {
		if (map1 == null || map2 == null) {
			return map1 == null && map2 == null ? ARG.isDefEqTrue(eqUnsafe) : false;
		} else if (map1.size() != map2.size()) {
			return false;
		}
		if (checkKeysMap1_or_KeysMap2_or_Equals == null) {
			MapDifference diff = Maps.difference(map1, map2);
			Map<String, MapDifference.ValueDifference> entriesDiffering = diff.entriesDiffering();
			return entriesDiffering.isEmpty();
		}
		Map<Object, Object> src = checkKeysMap1_or_KeysMap2_or_Equals ? map1 : map2;
		Map<Object, Object> dst = checkKeysMap1_or_KeysMap2_or_Equals ? map2 : map1;
		return src.entrySet().stream().allMatch(e -> e.getValue().equals(dst.get(e.getKey())));

	}

	public static boolean equalsAll(String... line) {
		String bef = null;
		for (String s : line) {
			if (bef != null && !bef.equals(IT.NN(s))) {
				return false;
			}
			bef = s;
		}
		return true;
	}

	public static boolean gt(Long n1, Long n2) {
		if (n1 == null || n2 == null) {
			return false;
		}
		return n1 > n2;
	}

	public static boolean gt(Double n1, Double n2) {
		if (n1 == null || n2 == null) {
			return false;
		}
		return n1 > n2;
	}

	public static boolean gt(Integer n1, Integer n2) {
		if (n1 == null || n2 == null) {
			return false;
		}
		return n1 > n2;
	}

	public static boolean lt(Long n1, Long n2) {
		if (n1 == null || n2 == null) {
			return false;
		}
		return n1 < n2;
	}

	public static boolean lt(Integer n1, Integer n2) {
		if (n1 == null || n2 == null) {
			return false;
		}
		return n1 < n2;
	}

	public static boolean isTrueAll(Boolean... conditions) {
		for (Boolean condition : conditions) {
			if (condition == null || !condition) {
				return false;
			}
		}
		return true;
	}

	public static boolean isFalseAll(Boolean... conditions) {
		for (Boolean condition : conditions) {
			if (condition == null || condition) {
				return false;
			}
		}
		return true;
	}

	public static boolean equalsClasses(Class c1, Class<?> c2, boolean checkPrimitives) {
		if (c1.isAssignableFrom(c2)) {
			return true;
		} else if (!checkPrimitives) {
			return c1.isAssignableFrom(c2);
		} else if (!c1.isPrimitive() && !c2.isPrimitive()) {
			return c1.isAssignableFrom(c2);
		} else {
			c1 = !c1.isPrimitive() ? c1 : RFL.convertPrimitiveClassToWrapperClass(c1);
			c2 = !c2.isPrimitive() ? c2 : RFL.convertPrimitiveClassToWrapperClass(c2);
			return c1.isAssignableFrom(c2);
		}
	}
}
