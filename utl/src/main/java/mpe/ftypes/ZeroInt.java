package mpe.ftypes;

public class ZeroInt {

	public final Integer original;

	public ZeroInt() {
		this.original = null;
	}

	public ZeroInt(Integer original) {
		this.original = original;
	}

	public int toInt() {
		return original == null ? 0 : original;
	}

	@Override
	public String toString() {
		return original == null ? String.valueOf(0) : String.valueOf(original);
	}
}
