package mpe.ftypes;


import mpe.ftypes.core.FString;

/**
 * BLANK String
 */
public class BlankString extends FString {
	public BlankString() {
	}

	public BlankString(String original) {
		super(original);
	}

	@Override
	public String toString() {
		return original == null ? "" : original;
	}
}
