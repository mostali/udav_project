package mpc.types.pare;

import java.util.Map;
import java.util.Objects;

public class PareEntry<K, V> extends Pare<K, V> implements Map.Entry<K, V> {
	public PareEntry(K key, V val) {
		super(key, val);
	}

	@Override
	public V getValue() {
		return getVal();
	}

	@Override
	public V setValue(V value) {
		throw new UnsupportedOperationException();
	}

	public static <K, V> PareEntry<K, V> of(K key, V val) {
		return new PareEntry(key, val);
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
		PareEntry pe = (PareEntry) obj;
		return Objects.equals(key(), pe.key()) && Objects.equals(val(), pe.val());
	}
}
