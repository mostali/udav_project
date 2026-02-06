package mpc.map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import mpc.exception.DRQ;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.IT;
import mpu.core.EQ;
import mpe.core.P;
import mpc.exception.EmptyRuntimeException;
import mpc.exception.RequiredRuntimeException;
import mpu.core.UDbl;
import mpc.rfl.RFL;
import mpc.str.ObjTo;
import mpu.str.SPLIT;
import mpu.str.Sb;
import mpu.pare.Pare;
import mpc.types.ruprops.URuProps;
import mpu.str.STR;
import mpu.Sys;
import mpu.X;

import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MAP {

	public static void main(String[] args) {
		X.exit(ARR.asHSET(1, 2, 3, 2));

		Multimap<String, String> test = ArrayListMultimap.create();
		test.put("1", "a");
		test.put("1", "b");
		test.put("2", "a");
		Collection<String> strings = test.get("1");
		X.exit(test.containsValue("b"));
		X.exit(test.entries().size());

		List<Pare<Integer, Integer>> l = Pare.ofKeyValues(1, 11, 2, 22, 2, 22, 3, 33);
//		l.stream().collect(Collectors.toMap(p -> p.key(), e -> e.val()));

		l = Pare.ofKeyValues(1, 11, 2, 22, 2, 23, 3, 33);
		Map<Integer, Integer> collect = l.stream().collect(Collectors.toMap(p -> p.key(), e -> e.val(), (k1, k2) -> k2));
		P.exit(collect);
	}

	public static void testMap() {
		Map test = URuProps.getRuProperties("eq.k1=v1;eq.k2=v2", ";", "=", "#");
		Sys.p("Map>>>" + test);
		test = toMapByCutPrefix(test, "eq", ".", false, true);
		Sys.p("Map>>>" + test);
		Map result = URuProps.getRuProperties("k1=v1;k2=v2", ";", "=", "#");
		IT.isEq(test, result);
		Sys.p("test is ok");
	}

	public static void testMap2() {
		Map test = URuProps.getRuProperties("eq.k1=v1;eq.k2=v2", ";", "=", "#");
		Sys.p("Map>>>" + test);
		test = toMapByCutPrefix(test, "eq", ".");
		Sys.p("Map>>>" + test);
		Map result = URuProps.getRuProperties("k1=v1;k2=v2", ";", "=", "#");
		IT.isEq(test, result);
		Sys.p("test is ok");
	}

	//
	//
	//

	public static <K, V> V getByWhatIs(Map<K, V> map, Predicate whatIsKey, V... defRq) {
		Map.Entry<K, V> v = findEntry(map, whatIsKey, null, null);
		if (v != null) {
			return v.getValue();
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Value not found by key-tester [%s]", whatIsKey);
	}

	public static <K, V> V getByWhatIsValue(Map<K, V> map, Predicate whatIsValue, V... defRq) {
		Map.Entry<K, V> v = findEntry(map, null, whatIsValue, null);
		if (v != null) {
			return v.getValue();
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Value not found by value-tester [%s]", whatIsValue);
	}

	public static <K, V> Map.Entry<K, V> findEntry(Map<K, V> map, Predicate whatIsKey, Predicate whatIsValue, Map.Entry<K, V>... defRq) {
		IT.notNullAny(whatIsKey, whatIsValue);
		if (X.notEmpty(map)) {
			for (Map.Entry<K, V> key : map.entrySet()) {
				if (whatIsKey != null && !whatIsKey.test(key.getKey())) {
					continue;
				}
				if (whatIsValue != null && !whatIsValue.test(key.getValue())) {
					continue;
				}
				return key;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Value not found by key-tester [%s] & value-tester [%s]", whatIsKey, whatIsValue);
	}

	public static <K, V> V getByKeyAndWhatIs(Map<K, V> map, K key, Predicate whatIsValue, V... defRq) {
		return getByKeyAndValues(map, ARR.of(key), whatIsValue, defRq);
	}

	public static <K, V> V getByKeyAndValues(Map<K, V> map, K[] keys, Predicate whatIs, V... defRq) {
		for (K key : keys) {
			V val = get(map, key, null);
			if (!whatIs.test(val)) {
				continue;
			}
			return val;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Value [%s] by keys [%s] is empty", whatIs, Arrays.asList(keys));
	}

	public static <K, V> Map.Entry<K, V> getByValueFirst(Map<K, V> map, V[] keys, Map.Entry... defRq) {
		Function<V, Boolean> funcEqKey = t -> Arrays.stream(keys).anyMatch(v -> X.equals(v, t));
		Optional<Map.Entry<K, V>> findInMap = map.entrySet().stream().filter(e -> funcEqKey.apply(e.getValue())).findFirst();
		if (findInMap.isPresent()) {
			return findInMap.get();
		}
		return ARG.toDefThrow(() -> new DRQ("Value [%s] by keys [%s] is empty"), defRq);
	}

	public static <K, V> Map.Entry<K, V> getByValueTester(Map<K, V> map, V[] keys, Map.Entry... defRq) {
		Function<V, Boolean> funcEqKey = t -> Arrays.stream(keys).anyMatch(v -> X.equals(v, t));
		Optional<Map.Entry<K, V>> findInMap = map.entrySet().stream().filter(e -> funcEqKey.apply(e.getValue())).findFirst();
		if (findInMap.isPresent()) {
			return findInMap.get();
		}
		return ARG.toDefThrow(() -> new DRQ("Value [%s] by keys [%s] is empty"), defRq);
	}

	public static <K, V> V getOr(Map<K, ?> map, K key, Class<V> asType, Supplier<V> defRq) {
		try {
			return getAs(map, key, asType);
		} catch (Exception ex) {
			if (defRq != null) {
				return defRq.get();
			}
			throw ex;
		}
	}

	public static Boolean getAsBool(Map map, Object key, Boolean... defRq) {
		return getAs(map, key, Boolean.class, defRq);
	}

	public static Map getAsMap(Map map, Object key, Map... defRq) {
		return getAs(map, key, Map.class, defRq);
	}

	public static Integer getAsInt(Map map, Object key, Integer... defRq) {
		return getAs(map, key, Integer.class, defRq);
	}

	public static Long getAsLong(Map map, Object key, Integer... defRq) {
		return getAs(map, key, Long.class, defRq);
	}

	public static String getAsString(Map map, Object key, String... defRq) {
		return getAs(map, key, String.class, defRq);
	}

	//used Contract
	public static <K, V> V getAs(Map<K, ?> map, K key, Class<V> asType, Object... defRq) {
		Class primitiveClass = null;
		if (asType.isPrimitive()) {
			primitiveClass = asType;
			asType = RFL.convertPrimitiveClassToWrapperClass(asType);
		}
		Object val = get(map, key, null);
		if (!X.emptyObj_Str(val)) {
			V vl = ObjTo.objTo(val, asType, null);
			if (vl != null) {
				return vl;
			}
		}
		if (ARG.isDef(defRq)) {
			return (V) ARG.toDef(defRq);
		}
		if (primitiveClass == null) {
			throw new EmptyRuntimeException("Value by key '%s' is %s", key, val == null ? "null" : "empty");
		}
		return (V) ObjTo.getInitValueForPrimitive(primitiveClass);
	}

	public static <K, V> V getNE(Map<K, V> map, K key, V... defRq) {
		V val = get(map, key, null);
		if (!X.emptyObj_Str(val)) {
			return val;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		return ARG.toDefThrowMsg(() -> X.f("Value by key '%s' is empty", key), defRq);
	}

	public static <K, V> V get(Map<K, V> map, K key, V... defRq) {
		if (map != null) {
			V val = map.get(key);
			if (val != null) {
				return val;
			} else if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Value by key '%s' is null", key), defRq);
	}

	public static <K, V> V update(Map<K, V> map, K key, V value, PutGetRemove putGetRemove) {
		switch (putGetRemove) {
			case PUT:
				return map.put(key, value);
			case GET:
				return map.get(key);
			case REMOVE:
				return map.remove(key);
			default:
				throw new UnsupportedOperationException("What is type PutGetRemove? " + putGetRemove);
		}
	}

	/**
	 * *************************************************************
	 * ------------------------- MAP OF --------------------------
	 * *************************************************************
	 */

	public static <K, V> Map<K, V> ofKeyValuesSafeType(Class<K> keyType, Class<V> vlType, Object... keyValues) {
		IT.isEven2(keyValues.length);
		Map<K, V> pares = new LinkedHashMap<>();
		for (int i = 0; i < keyValues.length; i += 2) {
			IT.isClassOf(keyValues[i].getClass(), keyType);
			Object kvValue = keyValues[i + 1];
			IT.isClassOf(kvValue.getClass(), vlType);
			pares.put((K) keyValues[i].getClass(), (V) kvValue);
		}
		return pares;
	}


	public static Map as(Object... keyValues) {
		return of(keyValues);
	}

	public static Map of(Object... keyValues) {
		return mapOf(keyValues);
	}

	public static Map mapOf(Object... keyValues) {
		return fillMapEvenKeyValues(new HashMap(keyValues.length / 2), keyValues);
	}

	public static LinkedHashMap maplOf(Object... keyValues) {
		return fillMapEvenKeyValues(new LinkedHashMap(keyValues.length / 2), keyValues);
	}

	public static ConcurrentHashMap mapcOf(Object... keyValues) {
		return fillMapEvenKeyValues(new ConcurrentHashMap<>(keyValues.length / 2), keyValues);
	}

	public static <M extends Map> M fillMapEvenKeyValues(M map, Object... keyValues) {
		IT.isEven2(keyValues.length);
		for (int i = 0; i < keyValues.length; i += 2) {
			map.put(keyValues[i], keyValues[i + 1]);
		}
		return map;
	}

	/**
	 * key = value ; key2 = value2
	 */
	public static Map<String, String> mapOf(String patternMap, String delimetrPairs, String delimetrKeyValue) {
		Map map = URuProps.getRuProperties(patternMap, delimetrPairs, delimetrKeyValue);
		return map;
	}

	public static Map mapOf(String patternMap) {
		return mapOfDeprecated(patternMap, ";", "=");
	}

	public static Map mapOfAP(String patternMap) {
		return mapOfDeprecated(patternMap, "|", "=");
	}

	public static Map mapOfLines(String patternMap) {
		return mapOfDeprecated(patternMap, STR.NL, "=");
	}

	public static Map ofAsQTypes(String patternMap) {
		return mapOfDeprecated(patternMap, " ", ":");
	}

	@Deprecated
	public static Map<String, String> mapOfDeprecated(String patternMap, String delimetrPairs, String delimetrKeyValue) {
		String[] pairs = SPLIT.argsBy(patternMap, delimetrPairs); // patternMap.split(delimetrPairs);
		Map<String, String> mapMapping = new LinkedHashMap<String, String>();
		for (String rule : pairs) {
			String[] ruleKeyValue = rule.split(delimetrKeyValue, 2);
			if (ruleKeyValue.length == 1) {
				mapMapping.put(ruleKeyValue[0], "");
			} else {
				mapMapping.put(ruleKeyValue[0], ruleKeyValue[1]);
			}
		}
		return mapMapping;
	}

	public static Map<String, String> mapOf(String[] args, String delimetrKeyValue) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (String arg : args) {
			if (!arg.contains(delimetrKeyValue)) {
				map.put(arg, null);
			} else {
				String[] kv = arg.split(delimetrKeyValue, 2);
				map.put(kv[0], kv[1]);
			}
		}
		return map;
	}

	/**
	 * *************************************************************
	 * ------------------------- UTILS --------------------------
	 * *************************************************************
	 */

	public static Map<String, String> toMapByCutPrefix(Map<String, ?> keys, String prefix, String delimter, boolean putEmptyKey, boolean checkExistKey) {
		String startPrefix = prefix + delimter;
		Map map = new LinkedHashMap();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			boolean putted = false;
			if (STR.startsWith(entry.getKey(), startPrefix)) {
				String key = entry.getKey() == null ? null : entry.getKey().substring(startPrefix.length());
				if (X.empty(key) && putEmptyKey) {
					map.put(key, entry.getValue());
					putted = true;
				}
				if (checkExistKey && map.containsKey(key)) {
					throw new IllegalStateException("Map already contains key '" + key + "'");
				}
				if (!putted) {
					map.put(key, entry.getValue());
				}
			}
		}
		return map;
	}

	public static Map<String, ?> toMapByCutPrefix(Map<String, ?> map, String prefix, String delimetr, Class<Map>... classNewMap) {
		if (X.nullAll(prefix, delimetr)) {
			throw new IllegalArgumentException("Prefix & delimiter is null's");
		} else if (prefix == null) {
			prefix = "";
		} else if (delimetr == null) {
			delimetr = "";
		}
		IT.notNull(map);
		Map newMap = classNewMap.length > 0 ? RFL.instEmptyConstructor(classNewMap[0]) : RFL.instEmptyConstructor(map.getClass());
		String prefixrefixWithDelimter = Sb.init(prefix).append(delimetr).toString();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			if (X.empty(entry.getKey()) || !entry.getKey().startsWith(prefix)) {
				continue;
			}
			String key = entry.getKey().substring(prefixrefixWithDelimter.length());
			newMap.put(key, entry.getValue());
		}
		return newMap;
	}

	public static Set getAllFirstPrefix(Map<String, ?> mapAll, String delimter) {
		Set keys = new LinkedHashSet();
		for (Map.Entry<String, ?> entry : mapAll.entrySet()) {
			String key = entry.getKey();
			int indDel = -1;
			if (X.notEmpty(key) && ((indDel = key.indexOf(delimter)) != -1)) {
				String prefix = key.substring(0, indDel);
				keys.add(prefix);
			}
		}
		return keys;
	}

	public static Map<String, Double> sortBy(Map<String, Double> map, boolean byKeyOrByValue, boolean... ascOrDesc) {
		Boolean f = ARG.toDefBooleanOrNull(ascOrDesc);
		if (f == null) {
			return map;
		}
		Comparator<? super Map.Entry<String, Double>> comparator = byKeyOrByValue ? Map.Entry.comparingByKey() : Map.Entry.comparingByValue();
		if (!f) {
			comparator = comparator.reversed();
		}
		Map sortedMap = map.entrySet().stream().sorted(comparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return sortedMap;
	}

	public static <K, V> Map<K, V> sortBy(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
		List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(comparator);
		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static <K, V> Map<K, V> sortByKey(Map<K, V> map, Comparator<? super Map.Entry<K, V>> sortBy) {
		Map sortedMap = map.entrySet().stream().sorted(sortBy).//
				collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)//
		);
		return sortedMap;
	}

	public static <K> Map<K, Double> scale(Map<K, Double> map, int scale, boolean byKeyOrByValue, boolean... ascOrDesc) {
		Boolean f = ARG.toDefBooleanOrNull(ascOrDesc);
		Comparator comparator = f == null ? null : byKeyOrByValue ? Map.Entry.comparingByKey() : Map.Entry.comparingByValue();
		if (comparator != null && !f) {
			comparator = comparator.reversed();
		}
		Stream<Map.Entry<K, Double>> stream = map.entrySet().stream();
		if (comparator != null) {
			stream = stream.sorted(comparator);
		}
		Map sortedMap = stream.map(e -> new Map.Entry<K, Double>() {
			@Override
			public K getKey() {
				return e.getKey();
			}

			@Override
			public Double getValue() {
				return UDbl.scale(e.getValue(), scale, RoundingMode.HALF_UP);
			}

			@Override
			public Double setValue(Double value) {
				return e.setValue(UDbl.scale(e.getValue(), scale, RoundingMode.HALF_UP));
			}
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return sortedMap;
	}

	public static Map double2proc(Map<String, Double> factor, boolean... asStringOrInt) {
		LinkedHashMap map = new LinkedHashMap();
		boolean asStr = ARG.isDefEqTrue(asStringOrInt);
		for (Map.Entry<String, Double> f : factor.entrySet()) {
			Object val = asStr ? UDbl.double2procStr(f.getValue()) : UDbl.double2procInt(f.getValue());
			map.put(f.getKey(), val);
		}
		return map;
	}

	public static <K, V> K getKey(Map<K, V> map, V[] values, K... defRq) {
		for (V value : values) {
			for (Map.Entry<K, V> entry : map.entrySet()) {
				if (EQ.equalsUnsafe(entry.getValue(), value)) {
					return entry.getKey();
				}
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Key not found, by values [%s]", ARR.of(values));
	}

	public static <K, V> K getKey(Map<K, V> map, V value, K... defRq) {
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (EQ.equalsUnsafe(entry.getValue(), value)) {
				return entry.getKey();
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Key not found, by value [%s]", value);
	}

	public static Set<String> keySet(Map map, Set... defRq) {
		if (map != null) {
			return map.keySet();
		}
		return ARG.toDefRq(defRq);
	}

	public static <K, C extends Collection> void putWithMergeUnsafeTypeCollection(Map<K, C> src, Object key, Collection values) {
		if (X.empty(values)) {
			return;
		}
		C c = src.get(key);
		if (c == null) {
			src.put((K) key, c = (C) new ArrayList());
		}
		ARR.mergeUnsafeTypeCollection(c, values);
	}

	public static <V> void putByIndexOrAutoincrement(Map<Integer, V> map, Integer targetIndex, V value) {
		if (!map.containsKey(targetIndex)) {
			map.put(targetIndex, value);
			return;
		}
		V old = map.put(targetIndex, value);
		Integer oldTargetIndex = targetIndex;
		if (map.size() == 1) {
			map.put(targetIndex + 3, old);
			return;
		}
		List<Integer> indexPositions = new ArrayList(new HashSet(map.keySet()));
		Collections.reverse(indexPositions);
		int targetIndexPosition = IT.isPosOrZero(indexPositions.indexOf(targetIndex));
		for (int iPos = targetIndexPosition + 1; iPos < indexPositions.size(); iPos++) {
			Integer nextTargetIndex = indexPositions.get(iPos);
			if (oldTargetIndex < nextTargetIndex) {
				map.put(++oldTargetIndex, old);
				return;
			}
			boolean last = iPos == map.size() - 1;
			old = map.put(last ? oldTargetIndex = oldTargetIndex + 3 : ++oldTargetIndex, old);
			if (last) {
				map.put(oldTargetIndex + 3, old);
			}
		}

	}

	public static <K, V> void putToValue(Map<K, List<V>> twins, K key, V value) {
		List<V> val = twins.get(key);
		if (val == null) {
			twins.put(key, val = new ArrayList<>());
		}
		val.add(value);
	}

	public static List toKVList(Map map) {
		List l = new ArrayList();
		map.forEach((k, v) -> l.addAll(ARR.as(k, v)));
		return l;
	}

	public enum PutGetRemove {
		PUT, GET, REMOVE;
	}

}
