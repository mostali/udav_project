package mpu.core;

import mpu.X;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpe.core.XX;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Ловит состояние множественных аргументов/предикатов
 * не используется для примитивов, будут баги ( используется класс ARGn )
 */
public class ARG {

	public static void main(String[] args) {
		//		ARG.testPredicat(3, 57);
		//		Object ch = toPredicatDef('a', 'b');
		//		U.exit(ch);

		testPredicat();
	}

	static void testPredicat(int... def) {
//		Boolean b=null;
		IT.isTrue(someBoolean(null));
//		U.exit(toPredicatDef_(1, 2));
//		U.exit(toPredicatDef(1, 2));
//		U.exit(isPredicatNN_(1, 2));
//		U.exit(toPredicatDefCHAR('a', 'b'));
//		U.exit(toPredicatDef('a', 'b'));
//		UC.isTrue(isPredicat(null));
//		UC.isFalse(isPredicat());
//		UC.isTrue(isPredicat(true));
//		UC.isTrue(isPredicat((int) 1));

		IT.isTrue(isDefNotEmpty(1L));
		IT.isTrue(isDefNotEmpty(1L));
	}

	private static boolean someBoolean(boolean... def) {
		boolean is = isDef(def);
		return is;
	}

	/**
	 * *************************************************************
	 * --------------------------- Predicat Arg---------------------
	 * *************************************************************
	 */

	public static boolean isDef(Object... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isNotDef(Object... predicat) {
		return predicat != null && predicat.length == 0;
	}

	public static boolean isDefNotEmpty(Object... predicat) {
		return predicat != null && predicat.length > 0;
	}

	//Is Default Not Null First
	public static boolean isDefNNF(Object... predicat) {
		return isDefNotEmpty(predicat) && predicat[0] != null;
	}

	//Is Null First
	public static boolean isDefNF(Object... predicat) {
		return predicat == null || (predicat.length > 0 && predicat[0] == null);
	}

	public static boolean isDefNotEqTrue(boolean... predicat) {
		return !isDefEqTrue(predicat);
	}

	public static boolean isDefEqTrue(boolean... predicat) {
		return predicat != null && predicat.length > 0 && predicat[0];
	}

	public static boolean isDefNotEqFalse(boolean... predicat) {
		return !isDefEqFalse(predicat);
	}

	public static boolean isDefEqFalse(boolean... predicat) {
		return predicat != null && predicat.length > 0 && !predicat[0];
	}


	/**
	 * *************************************************************
	 * --------------------------- Primitive Arg---------------------
	 * *************************************************************
	 */

	public static boolean isDefNum(long... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDefNum(int... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDefNum(boolean... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDefNum(short... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDefNum(byte... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDefNum(double... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDefNum(float... predicat) {
		return predicat == null || predicat.length > 0;
	}

	public static boolean isDefChar(char... predicat) {
		return predicat == null || predicat.length > 0;
	}

	/**
	 * *************************************************************
	 * --------------------------- Predicat Arg---------------------
	 * *************************************************************
	 */

	public static boolean toDefNum(boolean... predicat) {
		return predicat == null ? null : predicat[0];
	}

	public static short toDefNum(short... predicat) {
		return predicat == null ? null : predicat[0];
	}

	public static byte toDefNum(byte... predicat) {
		return predicat == null ? null : predicat[0];
	}

	public static int toDefNum(int... predicat) {
		return predicat == null ? null : predicat[0];
	}

	public static long toDefNum(long... predicat) {
		return predicat == null ? null : predicat[0];
	}

	public static float toDefNum(float... predicat) {
		return predicat == null ? null : predicat[0];
	}

	public static double toDefNum(double... predicat) {
		return predicat == null ? null : predicat[0];
	}

	public static char toDefNum(char... predicat) {
		return predicat == null ? null : predicat[0];
	}

	//
	//
	//
	public static <T> T toDef(T... predicat) {
		return predicat == null ? null : predicat[0];
	}

	public static <T> T toDef(Supplier<T> def, T... predicat) {
		return ARG.isNotDef(predicat) ? def.get() : predicat[0];
	}

	public static <T> T toDefOrNull(T... predicat) {
		return toDefOr(null, predicat);
	}

	public static <T> T toDefOrGet(Supplier<T> defIfNot, T... predicat) {
		return isDef(predicat) ? toDef(predicat) : defIfNot.get();
	}

	public static <T> T toDefOr(T defIfNot, T... predicat) {
		return isDef(predicat) ? toDef(predicat) : defIfNot;
	}

	public static <T> T toDefNNF(T defIfNull, T... predicat) {
		T val = toDefQk(predicat);
		return val == null ? defIfNull : val;
	}

	//
	//
	//

	public static <T> T toDefQk(T... predicat) {
		return predicat == null || predicat.length == 0 ? null : predicat[0];
	}

	public static <T> T toDefRq(T... defRq) {
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException();
	}


	public static <T> T toDefThrowAsCause(String error_message, Throwable ex, T... predicat) {
		if (isDef(predicat)) {
			return toDef(predicat);
		} else if (error_message == null && ex == null) {
			throw new RequiredRuntimeException();
		} else if (ex == null) {
			throw new RequiredRuntimeException(error_message);
		} else if (error_message == null) {
			throw new RequiredRuntimeException(ex);
		}
		throw new RequiredRuntimeException(ex, error_message);
	}

	public static <T> T toDefThrow(String message, T... defRq) {
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException(message);
	}

	public static <T> T toDefThrow(Throwable ex, T... predicat) {
		return ARG.isDef(predicat) ? toDef(predicat) : X.throwException(ex);
	}

	public static boolean toDefBooleanOrThrow(Throwable ex, boolean... predicat) {
		Boolean bool = toDefBooleanOrNull(predicat);
		if (bool != null) {
			return bool;
		}
		return X.throwException(IT.notNull(ex));
	}

	public static <T, X extends Throwable> T toDefThrowOpt(Supplier<? extends X> supplier, Optional<T> obj, T... defRq) throws X {
		if (obj != null && obj.isPresent()) {
			return obj.get();
		}
		return isDef(defRq) ? toDef(defRq) : XX.throwException(supplier.get());
	}

	public static <T, Str extends CharSequence> T toDefThrowOptMsg(Supplier<? extends Str> supplier, Optional<T> obj, T... defRq) {
		if (obj != null && obj.isPresent()) {
			return obj.get();
		}
		return isDef(defRq) ? toDef(defRq) : (T) supplier.get();
	}

	public static <T, Str extends CharSequence> T toDefThrowMsg(Supplier<? extends Str> supplier, T... defRq) throws RequiredRuntimeException {
		return isDef(defRq) ? toDef(defRq) : XX.throwException(new RequiredRuntimeException(supplier.get()));
	}

	public static <T, X extends Throwable> T toDefThrow(Supplier<? extends X> supplier, T... defRq) throws X {
		return isDef(defRq) ? toDef(defRq) : XX.throwException(supplier.get());
	}

	public static Boolean toDefBooleanOrNull(boolean... bool) {
		return bool == null || bool.length == 0 ? null : bool[0];
	}

	public static boolean[] toDefBooleanArgsOrEmpty(Boolean bool) {
		return bool == null ? new boolean[0] : new boolean[]{bool};
	}

	public static <T> T ofNN(T object) {
		return object != null ? object : (T) ARR.of();
	}

}
