package mpu.pare;

import mpu.X;
import mpu.core.ARR;

import java.util.List;
import java.util.Objects;

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
		return X.f("Pare4[%sk=%s,%sv=%s,%se=%s,%so1=%s]", POINT_SYMJ, key(), POINT_SYMJ, val(), POINT_SYMJ, ext(), POINT_SYMJ, o1());
	}

	@Override
	public boolean empty() {
		return super.empty() && emptyDef(o1());
	}


	@Override
	public int hashCode() {
		return hc != null ? hc : (hc = Objects.hash(key(), val(), ext(), o1()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Pare4)) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		Object o1_1 = o1();
		Object o1_2 = ((Pare4) obj).o1();
		return o1_1 == null ? o1_2 == null : o1_1.equals(o1_2);
	}

	@Override
	public List toList() {
		return ARR.as(key(), val(), ext(), o1());
	}
}
