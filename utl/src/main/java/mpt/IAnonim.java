package mpt;

public interface IAnonim {
	static boolean isAnonimUnsafeTrue(IaUser usr) {
		return isAnonimUnsafe(usr, true);
	}

	static boolean isAnonimUnsafe(IaUser usr, boolean isNullThat) {
		return usr == null ? isNullThat : usr instanceof IAnonim;
	}

	static boolean isAnonimSafe(IaUser usr) {
		return usr instanceof IAnonim;
	}
}
