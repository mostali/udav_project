package mpc.types.tks;

import mpc.ERR;

public class ID3<T> extends ID<T> {

	public static final String PFX = "#";

	public ID3(String id) {
		super(id, PFX);
	}

	public static ID3 of(String id) {
		checkPfx(id, PFX);
		return new ID3(id);
	}

	public static <T> String to(T id) {
		return PFX + ERR.NN(id);
	}

	public static String to(CharSequence id) {
		return PFX + ERR.NE(id);
	}
}
