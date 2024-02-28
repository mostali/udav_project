package mpc.types.tks;

import mpu.core.ARRi;
import mpu.str.SPLIT;

public class TokenInfo {
	public final String first0, first1, first2;
	public final String ext0, ext1, ext2;
	public final String original;
	public final String del;
	final String[] parts;

	public TokenInfo(String string, String del) {
		this.original = string;
		this.del = del;
		this.parts = SPLIT.bySC(original, del);

		this.first0 = ARRi.first(parts);
		this.first1 = ARRi.first(parts, 1, null);
		this.first2 = ARRi.first(parts, 2, null);

		this.ext0 = ARRi.last(parts);
		this.ext1 = ARRi.last(parts, 1, null);
		this.ext2 = ARRi.last(parts, 2, null);

	}

	public static TokenInfo ofSpace(String string) {
		return new TokenInfo(string, " ");
	}
}
