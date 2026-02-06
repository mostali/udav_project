package mpc.rfl;

import mpu.core.ARG;

public class R extends RFL {

	public static String sn(Object val, String... defRq) {
		return sn(val == null ? null : val.getClass());
	}

	public static String sn(Class val, String... defRq) {
		if (val != null) {
			return val.getSimpleName();
		}
		return ARG.toDefRq(defRq);
	}

	public static String cn(Object val, String... defRq) {
		return cn(val == null ? null : val.getClass());
	}

	public static String cn(Class clazz, String... defRq) {
		if (clazz != null) {
			return clazz.getName();
		}
		return ARG.toDefRq(defRq);
	}
}
