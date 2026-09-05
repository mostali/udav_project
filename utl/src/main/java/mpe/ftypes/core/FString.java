package mpe.ftypes.core;

public class FString {

	public final String original;

	public FString() {
		this.original = null;
	}

	public FString(String original) {
		this.original = original;
	}

	@Override
	public String toString() {
		return original;
	}
}
