package mpu.pare;

import mpu.IT;
import mpc.exception.FIllegalStateException;
import mpu.X;
import mpu.core.ARR;
import mpu.str.TKN;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Pare<K, V> extends Key<K> {
	private final V val;

	public Pare(K key, V val) {
		super(key);
		this.val = val;
	}

	public static <K, V> List<Pare<K, V>> ofKeyValues(Object... keyValues) {
		IT.isEven2(keyValues.length);
		List<Pare<K, V>> pares = new LinkedList();
		for (int i = 0; i < keyValues.length; i += 2) {
			pares.add((Pare<K, V>) of(keyValues[i], keyValues[i + 1]));
		}
		return pares;
	}

	public static boolean isEmpty(Pare pare) {
		return pare == null || pare.empty();
	}

	public static Pare<String, String> two(String str, String del) {
		String[] two = TKN.two(str, del);
		return of(two[0], two[1]);
	}

	public V val() {
		return val;
	}

	public V valOr(V defaultIfNull) {
		return X.toObjOr(val, defaultIfNull);
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

	public static <K, V, E> Pare3<K, V, E> of3(K key, V val, E ext) {
		return new Pare3(key, val, ext);
	}

	public static <K, V, E, O1> Pare4<K, V, E, O1> of4(K key, V val, E ext, O1 o1) {
		return new Pare4(key, val, ext, o1);
	}

	//
	//

	public static <K, V> Pare<K, V> of2(Object[] object) {
		IT.hasLength(object, 2);
		return new Pare(object[0], object[1]);
	}

	public static <K, V, E> Pare3<K, V, E> of3(Object[] object) {
		IT.hasLength(object, 3);
		return new Pare3(object[0], object[1], object[2]);
	}

	public static <K, V, E, O1> Pare4<K, V, E, O1> of4(Object[] object) {
		IT.hasLength(object, 4);
		return new Pare4(object[0], object[1], object[2], object[3]);
	}

	@Override
	public String toString() {
//		return "Pare[" + getKey() + "]=[" + getVal() + "]";
		return X.f("Pare[%sk=%s,%sv=%s]", POINT_SYMJ, key(), POINT_SYMJ, val());
	}

	@Override
	public int hashCode() {
		return hc != null ? hc : (hc = Objects.hash(key(), val()));
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
		Object val2 = ((Pare) obj).val();
		return val1 == null ? val2 == null : val1.equals(val2);
	}

	public String valStr() {
		return val() == null ? null : val().toString();
	}

	@Override
	public boolean empty() {
		return super.empty() && emptyDef(val());
	}

	public Pare<K, V> clonePage(K val) {
		return (Pare<K, V>) Pare.of(key(), val);
	}

	public Pare<K, V> clonePlane(K val) {
		return (Pare<K, V>) Pare.of(key(), val);
	}

	public boolean hasVal() {
		return val() != null;
	}

	@Override
	public List toList() {
		return ARR.as(key(), val());
	}
}
