package mpc.types.pare;

public class Key<K> {
	private final K key;

	public Key(K key) {
		this.key = key;
	}

	public K key() {
		return getKey();
	}

	public K getKey() {
		return key;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Key)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		Object key1 = key();
		Object key2 = ((Key) obj).key();
		return key1 == null ? key2 == null : key1.equals(key2);
	}
}
