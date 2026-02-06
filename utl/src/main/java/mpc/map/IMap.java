package mpc.map;

import java.util.Map;
import java.util.Set;

public interface IMap<K, V> {

	V get(K key);

	void put(K key, V value);

	Map<K, V> toMap();
}
