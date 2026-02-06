package mpu.pare;

import mpu.X;
import mpu.core.ARR;

import java.util.List;
import java.util.Objects;

public class Pare3<K, V, E> extends Pare<K, V> {

	private final E ext;

	public Pare3(K key, V val, E ext) {
		super(key, val);
		this.ext = ext;
	}

	public boolean hasExt() {
		return ext() != null;
	}

	public E ext() {
		return getExt();
	}

	public E extOr(E defaultIfNull) {
		return X.toObjOr(defaultIfNull, ext());
	}

	public String extStr() {
		return getExt() == null ? null : getExt().toString();
	}

	public E getExt() {
		return ext;
	}

	public static <K, V, E> Pare3<K, V, E> of(K key, V val, E ext) {
		return new Pare3(key, val, ext);
	}

	@Override
	public String toString() {
		return X.f("Pare3[%sk=%s,%sv=%s,%se=%s]", POINT_SYMJ, key(), POINT_SYMJ, val(), POINT_SYMJ, ext());
	}

	@Override
	public int hashCode() {
		return hc != null ? hc : (hc = Objects.hash(key(), val(), ext()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Pare3)) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		Object ext1 = ext();
		Object ext2 = ((Pare3) obj).ext();
		return ext1 == null ? ext2 == null : ext1.equals(ext2);
	}

	@Override
	public boolean empty() {
		return super.empty() && emptyDef(ext());
	}

	@Override
	public List toList() {
		return ARR.as(key(), val(), ext());
	}
}
