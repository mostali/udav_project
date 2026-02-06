package mpc.html;

import mpu.core.ARR;
import mpu.X;
import mpu.str.SPLIT;
import mpu.str.TKN;

import java.util.*;
import java.util.stream.Collectors;

public class STYLE {
	public static Set<String> getKeys(String style) {
		List<String> props = getProps(style);
		Set<String> keys = new LinkedHashSet<>();
		for (String prop : props) {
			if (X.empty(prop)) {
				continue;
			}
			String[] two = TKN.two(prop, ":", null);
			keys.add(two == null ? prop : two[0]);
		}
		return keys;
	}

	public static List<String> getProps(String style) {
		return X.empty(style) ? ARR.asAL() : SPLIT.allBy(style, ";");
	}

	public static String rmStyle(String srcStyle, String removedStyle) {
		return rmStyle(srcStyle, getKeys(removedStyle));
	}

	public static String rmStyle(String srcStyle, Collection<String> removeKeys) {
		if (X.empty(srcStyle) || X.empty(removeKeys)) {
			return srcStyle;
		}
		Map srcStyleMap = toMap(srcStyle);
		removeKeys.forEach(srcStyleMap::remove);
		return toString(srcStyleMap);
	}

	public static String addStyleAttr(String srcStyle, String key, String value) {
		Map srcStyleMap = toMap(srcStyle);
		srcStyleMap.put(key, value);
		return toString(srcStyleMap);
	}

	public static String addStyle(String srcStyle, String newStyle) {
		return toString(toMap(srcStyle, newStyle));
	}

	public static Map<String, String> toMap(String... styles) {
		Map<String, String> map = new LinkedHashMap();
		for (String style : styles) {
			if (X.empty(style)) {
				continue;
			}
			map.putAll(toMap(style));
		}
		return map;
	}

	public static Map<String, String> toMap(String style) {
		List<String> srcKV = getProps(style);
		Map<String, String> map = new LinkedHashMap();
		for (String prop : srcKV) {
			String[] two = TKN.two(prop, ":", null);
			if (two == null) {
				map.put(prop.trim(), null);
			} else {
				map.put(two[0].trim(), two[1]);
			}
		}
		return map;
	}

	public static String toString(Map<String, String> map) {
		String collect = map.entrySet().stream().map(e -> e.getValue() == null ? e.getKey() : e.getKey() + ":" + e.getValue()).collect(Collectors.joining(";"));
		return collect;
	}
}
