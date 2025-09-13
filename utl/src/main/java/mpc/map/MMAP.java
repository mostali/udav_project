package mpc.map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import lombok.RequiredArgsConstructor;
import mpc.str.ObjTo;
import mpe.core.P;
import mpu.X;
import mpu.core.*;
import mpu.pare.Pare;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MMAP {
	final Map src;

	public static MMAP of(Map from) {
		return new MMAP(from);
	}

	public static void main(String[] args) {
//		Multimap<String, String> test = ArrayListMultimap.create();//class com.google.common.collect.AbstractMapBasedMultimap$RandomAccessWrappedList
//		Multimap<String, String> test = LinkedHashMultimap.create();//class com.google.common.collect.AbstractMapBasedMultimap$WrappedSet
//		Multimap<String, String> test = LinkedListMultimap.create();//List
		Multimap<String, String> test = LinkedListMultimap.create();//List
		test.put("1", "a");
		test.put("1", "b");
		X.exit(test.get("1"));
		X.exit(test.get("1").getClass().getSimpleName());
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

	public static Long getFirstAsLong(Map map, Object key, Long... defRq) {
		return getFirstAs(map, key, Long.class, defRq);
	}

	public static Integer getFirstAsInteger(Map map, Object key, Integer... defRq) {
		return getFirstAs(map, key, Integer.class, defRq);
	}

	public static String getFirstAsString(Map map, Object key, String... defRq) {
		return getFirstAs(map, key, String.class, defRq);
	}

	public static Boolean getFirstAsBoolean(Map map, Object key, Boolean... defRq) {
		return getFirstAs(map, key, Boolean.class, defRq);
	}

	public static QDate getFirstAsQDate(Map map, Object key, String[] formats, QDate... defRq) {
		for (String format : formats) {
			QDate firstAsQDate = getFirstAsQDate(map, key, format, null);
			if (firstAsQDate != null) {
				return firstAsQDate;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Date not found by key '%s' and formats %s", key, ARR.of(formats)), defRq);
	}

	public static QDate getFirstAsQDate(Map map, Object key, String format, QDate... defRq) {
		String firstAsString = getFirstAsString(map, key, null);
		if (firstAsString != null) {
			return QDate.of(firstAsString, format, defRq);
		}
		return ARG.toDefThrowMsg(() -> X.f("Date not found by key '%s'", key), defRq);
	}

	public static long getFirstAsTimeMarkMs(Map map, Object key, Long... defRq) {
		String firstAsString = getFirstAsString(map, key, null);
		if (firstAsString != null) {
			return TimeMark.convertToMs(firstAsString, defRq);
		}
		return ARG.toDefThrowMsg(() -> X.f("TimeMark not found by key '%s'", key), defRq);
	}

	public static <K, V, T> V getFirstAs(Map<K, V> map, K key, Class<T> asType, V... defRq) {
		Supplier<V> get = () -> (V) ObjTo.objTo(getFirst(map, key), asType);
		if (ARG.isNotDef(defRq)) {
			return get.get();
		} else {
			try {
				return get.get();
			} catch (Exception ex) {
				return ARG.toDefThrow(ex, defRq);
			}
		}
	}

	public static <K, V> V getFirst(Map<K, V> map, K key, V... defRq) {
		if (map != null) {
			V val = map.get(key);
			if (val != null) {
				if (val instanceof Collection) {
					Collection c = (Collection) val;
					return (V) ARRi.first(c, defRq);
				}
				return val;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Value by key '%s' not found", key), defRq);
	}

	public static <K, V> Multimap<K, V> toArrayListMultimap(Map<K, Collection<V>> map) {
		Multimap<K, V> multimap = ArrayListMultimap.create();
		map.entrySet().forEach(e -> multimap.putAll(e.getKey(), e.getValue()));
		return multimap;
	}


//	@Override
//	public int size() {
//		return src.size();
//	}
//
//	@Override
//	public boolean isEmpty() {
//		return src.isEmpty();
//	}
//
//	@Override
//	public boolean containsKey(Object key) {
//		return src.containsKey(key);
//	}
//
//	@Override
//	public boolean containsValue(Object value) {
//
//		V val = map.get(key);
//		if (val != null) {
//			if (val instanceof Collection) {
//				Collection c = (Collection) val;
//				return (V) ARRi.first(c, defRq);
//			}
//			return val;
//		}
//
//		for (Object entry0 : src.entrySet()) {
//			Map.Entry entry = (Entry) entry0;
//			Object entryValue = entry.getValue();
//			entryValue.equals(value)
//
//		}
//
//		return false;
//	}
//
//	@Override
//	public Object get(Object key) {
//		for (Object entry0 : src.entrySet()) {
//			Map.Entry entry = (Entry) entry0;
//			Object entryValue = entry.getValue();
//			entryValue.equals(value)
//
//		}
//		return null;
//	}
//
//	@Override
//	public @Nullable Object put(Object key, Object value) {
//		return null;
//	}
//
//	@Override
//	public Object remove(Object key) {
//		return null;
//	}
//
//	@Override
//	public void putAll(@NotNull Map m) {
//
//	}
//
//	@Override
//	public void clear() {
//
//	}
//
//	@Override
//	public @NotNull Set keySet() {
//		return Set.of();
//	}
//
//	@Override
//	public @NotNull Collection values() {
//		return List.of();
//	}
//
//	@Override
//	public @NotNull Set<Entry> entrySet() {
//		return Set.of();
//	}
}

