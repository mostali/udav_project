package mpu.pare;

import mpc.str.sym.SYMJ;
import mpu.X;

import java.util.Objects;

public class Key<K> {

	public static final String POINT_SYMJ = SYMJ.POINT;

	private final K key;

	public Key(K key) {
		this.key = key;
	}

	public K key() {
		return key;
	}

	public K keyOr(K defaultIfNull) {
		return X.toObjOr(defaultIfNull, key());
	}

	public String keyStr() {
		return key() == null ? null : key().toString();
	}

	protected Integer hc = null;

	@Override
	public int hashCode() {
		return hc != null ? hc : (hc = Objects.hashCode(key()));
	}

	@Override
	public String toString() {
//		return POINT + "Key[" + getKey() + "]";
		return X.f("Key[%sk=%s]", POINT_SYMJ, key());
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

	public boolean empty() {
		return emptyDef(key());
	}

	public static boolean emptyDef(Object vl) {
		return X.emptyObj_Str_Cll_Num(vl);
	}

	public Key<K> clone(K val) {
		return new Key<>(val);
	}
}
