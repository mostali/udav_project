package mpu.paree;

import mpc.exception.FIllegalStateException;
import mpu.IT;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Paree<K, V> extends Keye<K> {
	private V val;

	public Paree(K key, V val) {
		super(key);
		this.val = val;
	}

	public static <K, V> List<Paree<K, V>> ofKeyValues(Object... keyValues) {
		IT.isEven2(keyValues.length);
		List<Paree<K, V>> pares = new LinkedList();
		for (int i = 0; i < keyValues.length; i += 2) {
			pares.add((Paree<K, V>) of(keyValues[i], keyValues[i + 1]));
		}
		return pares;
	}

	public V val() {
		return val;
	}

	public Paree setVal(V val) {
		this.val = val;
		return this;
	}

	public static <K, V> Paree<K, V> ofMap(Map from, String... keys) {
		switch (keys.length) {
			case 1:
				return (Paree<K, V>) Paree.of(from.get(keys[0]));
			case 2:
				return (Paree<K, V>) Paree.of(from.get(keys[0]), from.get(keys[1]));
			case 3:
				return (Paree<K, V>) Paree3.of(from.get(keys[0]), from.get(keys[1]), from.get(keys[2]));
			case 4:
				return (Paree<K, V>) Paree4.of(from.get(keys[0]), from.get(keys[1]), from.get(keys[2]), from.get(keys[3]));
			default:
				throw new FIllegalStateException("need impl, pare is max 4");
		}
	}

	public static <K, V> Paree<K, V> of(K key) {
		return new Paree(key, null);
	}

	public static <K, V> Paree<K, V> of(K key, V val) {
		return new Paree(key, val);
	}

	public static <K, V, E> Paree3<K, V, E> of3(K key, V val, E ext) {
		return new Paree3(key, val, ext);
	}

	public static <K, V, E, O1> Paree4<K, V, E, O1> of4(K key, V val, E ext, O1 o1) {
		return new Paree4(key, val, ext, o1);
	}

	@Override
	public String toString() {
		return "Pare[" + key() + "]=[" + val() + "]";
	}

	@Override
	public int hashCode() {
		return hc != null ? hc : (hc = Objects.hash(key(), val()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Paree)) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		Object val1 = val();
		Object val2 = ((Paree3) obj).val();
		return val1 == null ? val2 == null : val1.equals(val2);
	}
}
