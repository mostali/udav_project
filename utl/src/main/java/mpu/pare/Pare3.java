package mpu.pare;

import mpu.X;

import java.util.Objects;

public class Pare3<K, V, E> extends Pare<K, V> {

	private final E ext;

	public Pare3(K key, V val, E ext) {
		super(key, val);
		this.ext = ext;
	}


	public E ext() {
		return getExt();
	}

	public E getExt() {
		return ext;
	}

	public static <K, V, E> Pare3<K, V, E> of(K key, V val, E ext) {
		return new Pare3(key, val, ext);
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
}
