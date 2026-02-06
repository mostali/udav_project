package mpu.paree;

import mpu.X;

import java.util.Objects;

public class Paree4<K, V, E, O1> extends Paree3<K, V, E> {

	private O1 o1;

	public Paree4(K key, V val, E ext, O1 o1) {
		super(key, val, ext);
		this.o1 = o1;
	}


	public O1 o1() {
		return getO1();
	}

	public O1 getO1() {
		return o1;
	}

	public Paree3 setO1(O1 o1) {
		this.o1 = o1;
		return this;
	}

	public static <K, V, E, O1> Paree4<K, V, E, O1> of(K key, V val, E ext, O1 o1) {
		return new Paree4(key, val, ext, o1);
	}

	@Override
	public String toString() {
		return X.f("Pare4[k=%s,v=%s,e=%s,o1=%s]", key(), val(), ext(), o1());
	}


	@Override
	public int hashCode() {
		return hc != null ? hc : (hc = Objects.hash(key(), val(), ext(), o1()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Paree4)) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		Object o1_1 = o1();
		Object o1_2 = ((Paree4) obj).o1();
		return o1_1 == null ? o1_2 == null : o1_1.equals(o1_2);
	}
}
