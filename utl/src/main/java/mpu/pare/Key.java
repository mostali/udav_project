package mpu.pare;

import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.STR;

import java.util.List;
import java.util.Objects;

public class Key<K> {

	public static final String POINT_SYMJ = STR.ARR_DEL_POINT;

	private final K key;

	public Key(K key) {
		this.key = key;
	}

	public K key() {
		return key;
	}

	public K keyOr(K defaultIfNull, boolean... emptyIsNull) {
		return X.toObjOr(defaultIfNull, (ARG.isDefEqTrue(emptyIsNull) ? (X.emptyObj_Str(key()) ? null : key()) : key()));
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

	public boolean hasKey() {
		return key() != null;
	}
	public boolean empty() {
		return emptyDef(key());
	}

	public static boolean emptyDef(Object vl) {
		return X.emptyObj_Str_Cll_Num(vl);
	}

	public Key<K> clonePage(K val) {
		return new Key<>(val);
	}

	public List toList() {
		return ARR.as(key());
	}

}
