package mpu.core;

import mpu.X;

/**
 * Ловит состояние множественных аргументов/предикатов для примитивов
 * для объектов используется класс ARG
 */
public class ARGn {

	/**
	 * *************************************************************
	 * --------------------------- Predicat Arg INTEGER---------------------
	 * *************************************************************
	 */
	public static boolean isDef(byte... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDef(int... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDefNN(int... predicat) {
		return predicat != null || predicat.length > 0;
	}

	public static boolean isNotDef(int... predicat) {
		return predicat == null || predicat.length == 0;
	}

	public static Integer toDefOr(Integer def, int... predicat) {
		return isNotDef(predicat) ? def : predicat[0];
	}

	public static Integer toDef(int... predicat) {
		return predicat[0];
	}

//	public static Byte toDef(byte... predicat) {
//		return predicat[0];
//	}

	public static Integer toDefOrThrow(Throwable ex, int... predicat) {
		if (isDef(predicat)) {
			return toDef(predicat);
		}
		return X.throwException(ex);
	}

	/**
	 * *************************************************************
	 * --------------------------- Predicat Arg LONG---------------------
	 * *************************************************************
	 */
	public static boolean isDef(long... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDefNN(long... predicat) {
		return predicat != null || predicat.length > 0;
	}

	public static boolean isNotDef(long... predicat) {
		return predicat == null || predicat.length == 0;
	}

	public static Long toDefOr(Long def, long... predicat) {
		return isNotDef(predicat) ? def : predicat[0];
	}

	public static Long toDef(long... predicat) {
		return predicat[0];
	}

	public static Long toDefOrThrow(Throwable ex, long... predicat) {
		if (isDef(predicat)) {
			return toDef(predicat);
		}
		return X.throwException(ex);
	}

	/**
	 * *************************************************************
	 * --------------------------- Predicat Arg BOOLEAN ---------------------
	 * *************************************************************
	 */
	public static boolean isDef(boolean... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDefEqTrue(boolean... predicat) {
		return isNotDef(predicat) ? false : predicat[0];
	}

	public static boolean isDefEqFalse(boolean... predicat) {
		return isNotDef(predicat) ? false : !predicat[0];
	}

	public static boolean isDefNN(boolean... predicat) {
		return predicat != null || predicat.length > 0;
	}

	public static boolean isNotDef(boolean... predicat) {
		return predicat == null || predicat.length == 0;
	}


	public static Boolean toDef(boolean... predicat) {
		return predicat == null ? null : predicat[0];
	}

	public static Boolean toDefOr(Boolean def, boolean... predicat) {
		return isNotDef(predicat) ? def : predicat[0];
	}

	public static Boolean toDefOrThrow(Throwable ex, boolean... predicat) {
		if (isDef(predicat)) {
			return toDef(predicat);
		}
		return X.throwException(ex);
	}

}
