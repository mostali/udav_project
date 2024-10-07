package mpe.ftypes;

public class NInt {

	public final Integer original;

	public NInt() {
		this.original = null;
	}

	public NInt(Integer original) {
		this.original = original;
	}

	public int toInt() {
		return original == null ? null : original;
	}

	@Override
	public String toString() {
		return original == null ? null : String.valueOf(original);
	}
}
