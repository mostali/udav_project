package mpc.types.tks.cmt;

import mpu.IT;
import mpu.core.ARRi;
import mpc.str.sym.SYMJ;

public class Cmd7<K, V, E, O1, O2, O3, O4> extends Cmd6<K, V, E, O1, O2, O3> {

	public final OBJ<O4> opt4Obj;

	public Cmd7(String cmd, OBJ<K> keyObj, OBJ<V> valObj, OBJ<E> extObj, OBJ<O1> opt1Obj, OBJ<O2> opt2Obj, OBJ<O3> opt3Obj, OBJ<O4> opt4Obj) {
		super(cmd, keyObj, valObj, extObj, opt1Obj, opt2Obj, opt3Obj);
		this.opt4Obj = opt4Obj;
	}

	@Override
	public String toString() {
		return super.toString() + SYMJ.ARROW_RIGHT_SPEC + "O4" + opt4Obj.toDebugStringTypeVal();

	}

	public O4 opt4() {
		return opt4Obj.val;
	}

	public static Cmd7 strTo(String cmd) {
		return of7(cmd);
	}

	public static <K, V, E, O1, O2, O3, O4> Cmd7<K, V, E, O1, O2, O3, O4> of7(String cmd, K key, V val, E ext, O1 opt1, O2 opt2, O3 opt3, O4 opt4) {
		return of7(cmd, toArgs(cmd), OBJ.of(key), OBJ.of(val), OBJ.of(ext), OBJ.of(opt1), OBJ.of(opt2), OBJ.of(opt3), OBJ.of(opt4));
	}

	public static <K, V, E, O1, O2, O3, O4> Cmd7<K, V, E, O1, O2, O3, O4> of7(String cmd) {
		return of7(cmd, null, null, null, null, null, null, null);
	}

	public static <K, V, E, O1, O2, O3, O4> Cmd7<K, V, E, O1, O2, O3, O4> of7(String cmd, OBJ<K> key, OBJ<V> val, OBJ<E> ext, OBJ<O1> opt1, OBJ<O2> opt2, OBJ<O3> opt3, OBJ<O4> opt4) {
		return of7(cmd, toArgs(cmd), key, val, ext, opt1, opt2, opt3, opt4);
	}

	public static <K, V, E, O1, O2, O3, O4> Cmd7<K, V, E, O1, O2, O3, O4> of7(String cmd, String[] cmd5, OBJ<K> key, OBJ<V> val, OBJ<E> ext, OBJ<O1> opt1, OBJ<O2> opt2, OBJ<O3> opt3, OBJ<O4> opt4) {
		key = toEqOrAny(ARRi.item(cmd5, 0, null), key);
		val = toEqOrAny(ARRi.item(cmd5, 1, null), val);
		ext = toEqOrAny(ARRi.item(cmd5, 2, null), ext);
		opt1 = toEqOrAny(ARRi.item(cmd5, 3, null), opt1);
		opt2 = toEqOrAny(ARRi.item(cmd5, 4, null), opt2);
		opt3 = toEqOrAny(ARRi.item(cmd5, 5, null), opt3);
		opt4 = toEqOrAny(ARRi.item(cmd5, 6, null), opt4);
		return new Cmd7(cmd, key, val, ext, opt1, opt2, opt3, opt4);
	}

	public int size() {
		if (keyObj.isEmpty()) {
			return 0;
		} else if (valObj.isEmpty()) {
			return 1;
		} else if (extObj.isEmpty()) {
			return 2;
		} else if (opt1Obj.isEmpty()) {
			return 3;
		} else if (opt2Obj.isEmpty()) {
			return 4;
		} else if (opt3Obj.isEmpty()) {
			return 5;
		} else if (opt4Obj.isEmpty()) {
			return 6;
		}
		return 7;
	}

	@Override
	public Cmd7 throwIsNotWhole() {
		super.throwIsNotWhole();
		IT.NN(opt4(), "except whole opt4");
		return this;
	}
}
