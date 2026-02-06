package mpc.map;

import mpu.X;
import mpc.exception.RequiredRuntimeException;
import mpu.core.ARG;
import mpu.IT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ManyMap<K, V> implements Map {

	public final Map[] maps;

	public ManyMap(Map[] maps) {
		this.maps = IT.notEmpty(maps);
	}

	public static ManyMap of(Map... maps) {
		return new ManyMap(maps);
	}

	public V get(K key, WhatIs what, V... defRq) {
		return get(this.maps, key, what, defRq);
	}

	public V get(K[] key, WhatIs what, V... defRq) {
		return get(this.maps, key, what, defRq);
	}

	public static <K, V> V get(Map[] maps, K[] key, WhatIs what, V... defRq) {
		for (K key0 : key) {
			V v = get(maps, key0, WhatIs.NN, null);
			if (what == null) {
				if (v == null) {
					return null;
				}
			} else if (what != null && !what.test(v)) {
				continue;
			}
			return v;
		}
		return ARG.toDefThrowMsg(() -> X.f("Set value by keys '%s'", Arrays.asList(key)), defRq);
	}

	public static <K, V> V get(Map[] maps, K key, WhatIs what, V... defRq) {
		for (Map<K, V> map : maps) {
			for (K k : map.keySet()) {
				if (!key.equals(k)) {
					continue;
				}
				Object v = map.get(key);
				if (what == null) {
					if (v == null) {
						return null;
					}
				} else if (what != null && !what.test(v)) {
					continue;
				}
				return (V) v;
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Set value by key '%s'", key), defRq);
	}

	public static int size(Map[] maps) {
		return Arrays.stream(maps).collect(Collectors.summingInt(Map::size));
	}

	public static boolean isEmpty(Map... maps) {
		Optional<Map> anyNotEmpty = Arrays.stream(maps).filter(m -> !m.isEmpty()).findAny();
		return anyNotEmpty.isPresent() ? false : true;

	}

	public static boolean containsKey(Object key, Map... maps) {
		return Arrays.stream(maps).filter(m -> m.containsKey(key)).findAny().isPresent();
	}

	public static boolean containsValue(Object value, Map... maps) {
		return Arrays.stream(maps).filter(m -> m.containsValue(value)).findAny().isPresent();
	}

	@Override
	public int size() {
		return size(maps);
	}

	@Override
	public boolean isEmpty() {
		return isEmpty(maps);
	}

	@Override
	public boolean containsKey(Object key) {
		return containsKey(key, maps);
	}

	@Override
	public boolean containsValue(Object value) {
		return containsKey(value, maps);
	}

	public static boolean get(Object value, Map... maps) {
		return Arrays.stream(maps).filter(m -> m.containsValue(value)).findAny().isPresent();
	}

	@Override
	public Object get(Object key) {
		return get(maps, key, null);
	}

	@Nullable
	@Override
	public Object put(Object key, Object value) {
		return maps[0].put(key, value);
	}

	public static Object[] remove(Object key, Map... maps) {
		Object[] removed = new Object[maps.length];
		for (int i = 0; i < maps.length; i++) {
			Map map = maps[i];
			if (map.containsKey(key)) {
				removed[i] = map.remove(key);
			}
		}
		return removed;
	}

	@Override
	public Object remove(Object key) {
		Object[] removed = remove(key, maps);
		return Arrays.stream(removed).filter(X::notNull).findFirst().orElse(null);
	}

	@Override
	public void putAll(@NotNull Map m) {
		maps[0].putAll(m);
	}

	@Override
	public void clear() {
		Arrays.stream(maps).forEach(m -> m.clear());
	}

	public static Set keySet(Map... maps) {
		return (Set) Arrays.stream(maps).flatMap(m -> m.keySet().stream()).collect(Collectors.toSet());
	}

	@NotNull
	@Override
	public Set keySet() {
		return keySet(maps);
	}

	public static Collection values(Map... maps) {
		return (Collection) Arrays.stream(maps).flatMap(m -> m.values().stream()).collect(Collectors.toList());
	}

	@NotNull
	@Override
	public Collection values() {
		return values(maps);
	}

	public static Set<Entry> entrySet(Map... maps) {
		return (Set<Entry>) Arrays.stream(maps).flatMap(m -> m.entrySet().stream()).collect(Collectors.toList());
	}

	@NotNull
	@Override
	public Set<Entry> entrySet() {
		return entrySet(maps);
	}
}
