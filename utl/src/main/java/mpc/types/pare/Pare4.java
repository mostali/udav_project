package mpc.types.pare;

import mpc.X;

public class Pare4<K, V, E, O1> extends Pare3<K, V, E> {

	private final O1 o1;

	public Pare4(K key, V val, E ext, O1 o1) {
		super(key, val, ext);
		this.o1 = o1;
	}


	public O1 o1() {
		return getO1();
	}

	public O1 getO1() {
		return o1;
	}

	public static <K, V, E, O1> Pare4<K, V, E, O1> of(K key, V val, E ext, O1 o1) {
		return new Pare4(key, val, ext, o1);
	}

	@Override
	public String toString() {
		return X.f("Pare4[k=%s,v=%s,e=%s,o1=%s]", key(), val(), ext(), o1());
	}
}
