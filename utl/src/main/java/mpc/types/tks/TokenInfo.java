package mpc.types.tks;

import mpc.arr.ArrItem;
import mpc.str.SPLIT;

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

		this.first0 = ArrItem.first(parts);
		this.first1 = ArrItem.first(parts, 1, null);
		this.first2 = ArrItem.first(parts, 2, null);

		this.ext0 = ArrItem.last(parts);
		this.ext1 = ArrItem.last(parts, 1, null);
		this.ext2 = ArrItem.last(parts, 2, null);

	}

	public static TokenInfo ofSpace(String string) {
		return new TokenInfo(string, " ");
	}
}
