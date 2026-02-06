package mpu.pare;

import mpu.X;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class PareEntry<K, V> extends Pare<K, V> implements Map.Entry<K, V> {
	public PareEntry(K key, V val) {
		super(key, val);
	}

//	public static <K, V extends Comparable<? super V>> Comparator<PareEntry<K, V>> comparingByValue() {
//		return (Comparator<PareEntry<K, V>> & Serializable) (c1, c2) -> c1.getValue().compareTo(c2.getValue());
//	}

	@Override
	public K getKey() {
		return key();
	}

	@Override
	public V getValue() {
		return val();
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
//		return "PareEntry[" + key() + "]=[" + val() + "]";
		return X.f("PareEntry[%sk=%s,%sv=%s]", POINT_SYMJ, key(), POINT_SYMJ, val());
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

	public static PareEntry ofMapEntry(Object o) {
		Map.Entry e = (Map.Entry) o;
		return PareEntry.of(e.getKey(), e.getValue());
	}
}
