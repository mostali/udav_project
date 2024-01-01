package mpc.types.pare;

import mpc.ERR;
import mpc.exception.FIllegalStateException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Pare<K, V> extends Key<K> {
	private final V val;

	public Pare(K key, V val) {
		super(key);
		this.val = val;
	}

	public static <K, V> List<Pare<K, V>> ofKeyValues(Object... keyValues) {
		ERR.isEven2(keyValues.length);
		List<Pare<K, V>> pares = new LinkedList();
		for (int i = 0; i < keyValues.length; i += 2) {
			pares.add((Pare<K, V>) of(keyValues[i], keyValues[i + 1]));
		}
		return pares;
	}

	public V val() {
		return getVal();
	}

	public V getVal() {
		return val;
	}

	public static <K, V> Pare<K, V> ofMap(Map from, String... keys) {
		switch (keys.length) {
			case 1:
				return (Pare<K, V>) Pare.of(from.get(keys[0]));
			case 2:
				return (Pare<K, V>) Pare.of(from.get(keys[0]), from.get(keys[1]));
			case 3:
				return (Pare<K, V>) Pare3.of(from.get(keys[0]), from.get(keys[1]), from.get(keys[2]));
			case 4:
				return (Pare<K, V>) Pare4.of(from.get(keys[0]), from.get(keys[1]), from.get(keys[2]), from.get(keys[3]));
			default:
				throw new FIllegalStateException("need impl, pare is max 4");
		}
	}

	public static <K, V> Pare<K, V> of(K key) {
		return new Pare(key, null);
	}

	public static <K, V> Pare<K, V> of(K key, V val) {
		return new Pare(key, val);
	}

	@Override
	public String toString() {
		return "Pare[" + getKey() + "]=[" + getVal() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Pare)) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		Object val1 = val();
		Object val2 = ((Pare3) obj).val();
		return val1 == null ? val2 == null : val1.equals(val2);
	}
}
