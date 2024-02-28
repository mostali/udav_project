package mpc.types.tks;

import mpu.IT;

public class ID2<T> extends ID<T> {

	public static final String PFX = "@";

	public ID2(String id) {
		super(id, PFX);
	}

	public static ID2 of(String id) {
		checkPfx(id, PFX);
		return new ID2(id);
	}

	public static <T> String to(T id) {
		return PFX + IT.NN(id);
	}

	public static String to(CharSequence id) {
		return PFX + IT.NE(id);
	}
}
