package mpf.contract;

import mpu.core.ENUM;

import java.lang.reflect.Method;

public enum DefObjMethod {

	toString, equals, hashCode, getClass, wait, notify, notifyAll;

	public static void main(String[] args) {

	}

	public static boolean isDefMethodName(Method method) {
		return of(method.getName(), null) != null;

	}

	public static DefObjMethod of(Method method, DefObjMethod... defRq) {
		return of(method.getName(), defRq);
	}

	public static DefObjMethod of(String name, DefObjMethod... defRq) {
		return ENUM.valueOf(name, DefObjMethod.class, defRq);
	}
}
