package mpu.paree;

import java.util.Map;
import java.util.Objects;

public class PareeEntry<K, V> extends Paree<K, V> implements Map.Entry<K, V> {
	public PareeEntry(K key, V val) {
		super(key, val);
	}

	@Override
	public V getValue() {
		return val();
	}

	@Override
	public V setValue(V value) {
		throw new UnsupportedOperationException();
	}

	public static <K, V> PareeEntry<K, V> of(K key, V val) {
		return new PareeEntry(key, val);
	}

	@Override
	public String toString() {
		return "PareEntry[" + key() + "]=[" + val() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(new Object[]{key(), val()});
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		}
		PareeEntry pe = (PareeEntry) obj;
		return Objects.equals(key(), pe.key()) && Objects.equals(val(), pe.val());
	}
}
