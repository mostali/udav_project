package mpu.paree;

import mpu.X;

import java.util.Objects;

public class Paree3<K, V, E> extends Paree<K, V> {

	private E ext;

	public Paree3() {
		super(null, null);
	}

	public Paree3(K key, V val, E ext) {
		super(key, val);
		this.ext = ext;
	}


	public E ext() {
		return getExt();
	}

	public E getExt() {
		return ext;
	}

	public Paree3 setExt(E ext) {
		this.ext = ext;
		return this;
	}

	public static <K, V, E> Paree3<K, V, E> of(K key, V val, E ext) {
		return new Paree3(key, val, ext);
	}

	@Override
	public String toString() {
		return X.f("Pare3[k=%s,v=%s,e=%s]", key(), val(), ext());
	}

	@Override
	public int hashCode() {
		return hc != null ? hc : (hc = Objects.hash(key(), val(), ext()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Paree3)) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		Object ext1 = ext();
		Object ext2 = ((Paree3) obj).ext();
		return ext1 == null ? ext2 == null : ext1.equals(ext2);
	}
}
