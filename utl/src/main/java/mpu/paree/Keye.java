package mpu.paree;

import java.util.Objects;

public class Keye<K> {
	private K key;

	public Keye(K key) {
		this.key = key;
	}

	public K key() {
		return getKey();
	}

	public K getKey() {
		return key;
	}

	public Keye setKey(K key) {
		this.key = key;
		return this;
	}

	protected Integer hc = null;

	@Override
	public int hashCode() {
		return hc != null ? hc : (hc = Objects.hashCode(key()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Keye)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		Object key1 = key();
		Object key2 = ((Keye) obj).key();
		return key1 == null ? key2 == null : key1.equals(key2);
	}
}
