package mpc.types.ruprops;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import mpu.core.ARG;
import mpu.X;
import mpc.str.sym.SYM;
import mpu.core.ARRi;
import mpu.core.RW;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

//Utility for RuProps
public class URuProps {

	public static final Logger L = LoggerFactory.getLogger(URuProps.class);
	public static final String SEP_OR = "|";
	public static final String SEP_COMMENT = "#";

	/**
	 * *************************************************************
	 * ---------------------------- GET ----------------------
	 * *************************************************************
	 */
	public static Map getRuPropertiesClassic(InputStream is, Charset... charset) throws IOException {
		List<String> lines = IOUtils.readLines(is, ARG.isDefNNF(charset) ? charset[0] : Charset.defaultCharset());
		return getRuPropertiesClassic(lines);
	}

	public static Map getRuPropertiesClassic(Path filePathWithProperties) throws IOException {
		List<String> lines = Files.readAllLines(filePathWithProperties);
		return getRuPropertiesClassic(lines);
	}

	public static Map getRuPropertiesClassic(String cnt) {
		return getRuPropertiesClassic(StringUtils.split(cnt, System.lineSeparator()));
	}

	public static Map getRuPropertiesClassic(String[] lines) {
		return getRuPropertiesClassic(Arrays.asList(lines));
	}

	public static Map getRuPropertiesClassic(List<String> lines) {
		return getRuProperties(lines, "=", SEP_COMMENT, true, true);
	}

	public static Map getRuPropertiesHeaders(List<String> lines) {
		return getRuProperties(lines, ":", SEP_COMMENT, true, true);
	}

	public static Map getRuProperties(String lines, String separatorLine, String separatorKeys) {
		return getRuProperties(Arrays.asList(StringUtils.split(lines, separatorLine)), separatorKeys, SEP_COMMENT, true, true);
	}

	public static Map getRuProperties(String lines, String separatorLine, String separatorKeys, String charComment) {
		return getRuProperties(Arrays.asList(StringUtils.split(lines, separatorLine)), separatorKeys, charComment, true, true);
	}

	public static Map getRuProperties(List<String> lines, String separatorKeys, String charComment, boolean trimKeys, boolean trimValues) {
		return getRuProperties(lines, separatorKeys, charComment, trimKeys, trimValues, true);
	}

	public static Map getRuProperties(List<String> lines, String separatorKeys, String charComment, boolean trimKeys, boolean trimValues, Boolean ifValNull__Null_Blank_Skip) {
		Map tcp = new LinkedHashMap<>();
		for (String line : lines) {
			if (X.empty(line) || (charComment != null && line.startsWith(charComment))) {
				continue;
			}
			String[] kv = StringUtils.split(line, separatorKeys, 2);
			String key = trimKeys ? kv[0].trim() : kv[0];
			if (kv.length == 1) {
				tcp.put(key, null);
			} else {
				tcp.put(key, X.empty(kv[1]) ? (ifValNull__Null_Blank_Skip ? null : "") : trimValues ? kv[1].trim() : kv[1]);
			}
		}
		return tcp;
	}

	public static Multimap getRuPropertiesMultiMap(Path file) {
		return getRuPropertiesMultiMap(RW.readLines(file));
	}

	public static Multimap getRuPropertiesMultiMap(List<String> lines) {
		return getRuPropertiesMultiMap(lines, "=", SEP_COMMENT, true, true, true);
	}

	public static Multimap getRuPropertiesMultiMap(List<String> lines, String separatorKeys, String charComment, boolean trimKeys, boolean trimValues, Boolean ifValNull__Null_Blank_Skip) {
		Multimap mm = ArrayListMultimap.create();
//		Multimap mm = LinkedHashMultimap.create();
		for (String line : lines) {
			if (X.empty(line) || (charComment != null && line.startsWith(charComment))) {
				continue;
			}
			String[] kv = StringUtils.split(line, separatorKeys, 2);
			String key = trimKeys ? kv[0].trim() : kv[0];
			if (kv.length == 1) {
				mm.put(key, null);
			} else {
				mm.put(key, X.empty(kv[1]) ? (ifValNull__Null_Blank_Skip ? null : "") : trimValues ? kv[1].trim() : kv[1]);
			}
		}
		return mm;
	}

	/**
	 * *************************************************************
	 * ---------------------------- TO ----------------------
	 * *************************************************************
	 */
	public static String toRuPropertiesClassic(Map map) {
		return toRuProperties(map, System.lineSeparator(), "=", null, false).toString();
	}

	public static String toRuProperties(Map map, String lineSeparator, String kvSeparator) {
		return toRuProperties(map, lineSeparator, kvSeparator, null, false).toString();
	}

	public static <K, V> CharSequence toRuProperties(Map<K, V> props, String separatorKeys, Boolean ifKeyNull__Null_Blank_Skip, Boolean ifValNull__Null_Blank_Skip) {
		return toRuProperties(props, SYM.NEWLINE, separatorKeys, ifKeyNull__Null_Blank_Skip, ifValNull__Null_Blank_Skip);
	}

	public static <K, V> CharSequence toRuProperties(Map<K, V> props, String separatorLine, String separatorKeys, Boolean ifKeyNull__Null_Blank_Skip, Boolean ifValNull__Null_Blank_Skip) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<K, V> line : props.entrySet()) {
			K k = line.getKey();
			V v = line.getValue();
			String key = null;
			if (k == null) {
				if (ifKeyNull__Null_Blank_Skip == null) {
					if (L.isErrorEnabled()) {
						L.error("Skip map entry. Key is null. Value=" + v);
					}
					continue;
				}
				key = k == null ? (ifKeyNull__Null_Blank_Skip ? null : "") : k.toString();
			} else {
				key = k.toString();
			}
			String val = null;
			if (v == null) {
				if (ifValNull__Null_Blank_Skip == null) {
					if (L.isErrorEnabled()) {
						L.error("Skip map entry. Val is null. Key=" + k);
					}
					continue;
				}
				val = v == null ? (ifValNull__Null_Blank_Skip ? null : "") : v.toString();
			} else {
				val = v.toString();
			}
			sb.append(key).append(separatorKeys).append(val).append(separatorLine);
		}
		return sb;
	}

	public static String[] toLinesMultimapAsArgsClassic(Map mmap) {
		List<String> arr = new ArrayList();
		mmap.forEach((k, val) -> {
			if (val == null) {
				arr.add(k + "=" + X.toStringNN(val, ""));
			} else {
				if (val instanceof Collection) {
					Collection c = (Collection) val;
					arr.add(k + "=" + X.toStringNN(ARRi.first(c, null), ""));
				} else {
					arr.add(k + "=" + X.toStringNN(val, ""));

				}
			}
		});
		return arr.toArray(String[]::new);
	}

	public static String[] toLinesMultimapAsSeq(Map<String, Object> mmap) {
		List<String> arr = new ArrayList();
		Function<String, String[]> dblGet = (k) -> {
			if (!k.startsWith("--")) {
				return null;
			} else {
				return new String[]{k.substring(2), true + ""};
			}
		};
		mmap.forEach((k, val) -> {
			if (X.empty(k)) {
				return;
			}
			String[] apply = dblGet.apply(k);
			if (apply != null) {
				arr.add(apply[0]);
				arr.add(apply[1]);
				return;
			}
			if (val instanceof Collection) {
				Collection c = (Collection) val;
				for (Object o : c) {
//				Object first = ARRi.first(c, null);
//				if (first != null) {
					arr.add("-" + k);
					arr.add(String.valueOf(o));
//				}
				}
			} else {
				arr.add("-" + k);
				arr.add(X.emptyObj_Str(val) ? "\"\"" : String.valueOf(val));
			}
		});
		return arr.toArray(String[]::new);
	}
}
