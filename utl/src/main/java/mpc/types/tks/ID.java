package mpc.types.tks;

import mpc.exception.RequiredRuntimeException;
import mpc.rfl.RFL;
import mpu.str.UST;

public class ID<T> {
	public final String id;
	public final String pfx;

	public ID(String id, String pfx) {
		this.id = id;
		this.pfx = pfx;
	}

	public T id() {
		return (T) UST.strTo(id, RFL.getGenericType(ID.class));
	}

	public static ID of(String id, String pfx) {
		checkPfx(id, pfx);
		return new ID(id.substring(1), pfx);
	}

	public static void checkPfx(String id, String pfx) {
		if (!id.startsWith(pfx)) {
			throw new RequiredRuntimeException(pfx + ":::" + id);
		}
	}
}
