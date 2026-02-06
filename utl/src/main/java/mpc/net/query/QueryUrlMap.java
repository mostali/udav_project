package mpc.net.query;

import mpu.core.ARG;
import mpu.X;

import java.util.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

//https://stackoverflow.com/questions/2809877/how-to-convert-map-to-url-query-string
public class QueryUrlMap {

	//	public static void main(String[] args) {
	//		Map<String, Object> map = new HashMap<String, Object>();
	//		map.put("p1", 12);
	//		map.put("p2", "cat");
	//		map.put("p3", "a & b");
	//		System.out.println(toString(map));
	//	}

	static String urlEncodeUTF8(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	public static String toString(Map[] args, boolean... appendPrefixQuest) {
		return ARG.isNotDef(args) ? "" : toString(ARG.toDef(args), appendPrefixQuest);
	}

	public static String toString(Map<?, ?> args, boolean... appendPrefixQuest) {
		if (X.empty(args)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<?, ?> argEntry : args.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			if (argEntry.getValue() instanceof Iterable) {
				((List) argEntry.getValue()).forEach(arg -> append(sb, argEntry.getKey(), argEntry.getValue()));
			} else {
				append(sb, argEntry.getKey(), argEntry.getValue());
			}
		}
		return ARG.isDefEqTrue(appendPrefixQuest) ? "?" + sb : sb.toString();
	}

	private static void append(StringBuilder sb, Object key, Object value) {
		sb.append(urlEncodeUTF8(key.toString())).append("=").append(urlEncodeUTF8(value.toString()));
	}

}