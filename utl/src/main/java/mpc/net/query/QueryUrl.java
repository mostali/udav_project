package mpc.net.query;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UUrl;
import mpc.map.MAP;
import mpc.map.WhatIs;
import mpu.str.Rt;
import mpu.str.SPLIT;
import mpu.str.UST;
import mpu.X;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

//https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection
@RequiredArgsConstructor
public class QueryUrl {

	final String query;
	private Map<String, List<String>> map;

	public static QueryUrl of(String query) {
		return new QueryUrl(query);
	}

	public static String applyQueryArgs(String url, Object... queryArgs) {
		if (queryArgs.length == 0) {
			return url;
		}
		char last = ARRi.last(url);
		String firstDel = last == '?' || last == '&' ? "" : (url.indexOf('?') == -1 ? "?" : "&");
		IT.isEven2(queryArgs.length);
		for (int i = 0; i < queryArgs.length; i++) {
			if (i == 0) {
				url += firstDel + queryArgs[i];
			} else {
				url += (i % 2 == 0 ? "&" : "=") + queryArgs[i];
			}
		}
		return url;
	}

	public static QueryUrl ofUrl(String url) {
		return of(UUrl.getQueryString(url));
	}


	public Map<String, List<String>> getMap() {
		return map == null ? map = getQueryStringAsMap(query) : map;
	}

//	public static Map<String, List<String>> getQueryStringAsMap(URL url) {
//		if (X.empty(url.getQuery())) {
//			return Collections.emptyMap();
//		}
//		return getQueryStringAsMap(url.getQuery());
//	}


	public static Multimap<String, String> getQueryStringAsMultiMap(String query) {
		Multimap<String, String> mmap = LinkedListMultimap.create();
		if (X.empty(query)) {
			return mmap;
		}
		String[] args = SPLIT.argsBy(query, "&");
		Arrays.stream(args).filter(X::NE).map(QueryUrl::splitQueryParameter).filter(p -> p[1] != null).forEach(p -> mmap.put(p[0], p[1]));
		return mmap;
	}

	public static Map<String, List<String>> getQueryStringAsMap(String query) {
		if (X.empty(query)) {
			return Collections.emptyMap();
		}
		String[] args = SPLIT.argsBy(query, "&");
		Map<String, List<String>> mm = new LinkedHashMap();
		Arrays.stream(args).filter(X::NE).map(QueryUrl::splitQueryParameter).filter(p -> p[1] != null).forEach(p -> mm.computeIfAbsent(p[0], (key) -> new LinkedList<>()).add(p[1]));
		return mm;
	}

//	public static Map<String, List<String>> getQueryStringAsMapOrg(String query) {
//		if (X.empty(query)) {
//			return Collections.emptyMap();
//		}
//		String[] args = SPLIT.argsBy(query, "&");
//		Collector<AbstractMap.SimpleImmutableEntry<String, String>, ?, List<String>> mapping = mapping(Map.Entry::getValue, toList());
//		Collector<AbstractMap.SimpleImmutableEntry<String, String>, ?, LinkedHashMap<String, List<String>>> collector = Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping);
//		return Arrays.stream(args).map(QueryUrl::splitQueryParameterEntry).collect(collector);
//	}
//
//	@SneakyThrows
//	public static AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameterEntry(String param) {
//		String[] pare = splitQueryParameter(param);
//		return new AbstractMap.SimpleImmutableEntry<>(pare[0], pare[1]);
//	}

	@SneakyThrows
	public static String[] splitQueryParameter(String queryParam) {
		final int i = queryParam.indexOf("=");
		final String key = i > 0 ? queryParam.substring(0, i) : queryParam;
		final String value = i > 0 && queryParam.length() > i + 1 ? queryParam.substring(i + 1) : null;
		String decodeKey = URLDecoder.decode(key, StandardCharsets.UTF_8.name());
		String decodeValue = value == null ? "" : URLDecoder.decode(value, StandardCharsets.UTF_8.name());
		return new String[]{decodeKey, decodeValue};
	}

	public String buildReport() {
		return Rt.buildReport(getMap(), "", 0).toString();
	}

	public boolean hasBlank(String key) {
		Map<String, List<String>> map = getMap();
		return !map.containsKey(key) ? false : X.empty(map.get(key));
	}

	public List<String> get(String key, WhatIs whatIsValue, List<String>... defRq) {
		return MAP.getByKeyAndWhatIs(getMap(), key, whatIsValue, defRq);
	}

	public String getFirstAny(String key, WhatIs whatIs, String... defRq) {
		return getFirst(key, whatIs, true, defRq);
	}

	public String getFirstAsStr(String key, String... defRq) {
		return getFirstAs(key, String.class, defRq);
	}

	public <T> T getFirstAs(String key, Class<T> asType, T... defRq) {
		String first = getFirst(key, WhatIs.NN, false, null);
		if (first != null) {
			return UST.strTo(first, asType, defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Query Arg (First) '%s' is null", key), defRq);
	}

	public String getFirst(String key, WhatIs whatIs, String... defRq) {
		return getFirst(key, whatIs, false, defRq);
	}

	private String getFirst(String key, WhatIs whatIs, boolean checkAll, String... defRq) {
		List<String> vls = get(key, whatIs, null);
		if (vls != null) {
			if (vls.isEmpty()) {
				return "";
			}
			for (String vl : vls) {
				if (whatIs.test(vl)) {
					return vl;
				}
				if (!checkAll) {
					break;
				}
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Required value by key '%s' (%s), check-only-first-key (%s)", key, whatIs, !checkAll), defRq);
	}

	public Map<String, List<String>> getMapWithKeyPfx(String withPfx, boolean unwrapKey) {
		Map<String, List<String>> map0 = getMap();
		if (!unwrapKey) {
			return map0;
		}
		Map<String, List<String>> map = new LinkedHashMap<>();
		map0.entrySet().stream().filter(p->p.getKey().startsWith(withPfx)).forEach((e) -> map.put(e.getKey().substring(withPfx.length()), e.getValue()));
//		map0.forEach((k, v) -> map.put(k.substring(withPfx.length()), v));
		return map;
	}
}
