package mpc.types.tks.cmt;

import mpu.IT;
import mpu.core.ARRi;
import mpc.str.sym.SYMJ;

public class Cmd6<K, V, E, O1, O2, O3> extends Cmd5<K, V, E, O1, O2> {

	public final OBJ<O3> opt3Obj;

	public Cmd6(String cmd, OBJ<K> keyObj, OBJ<V> valObj, OBJ<E> extObj, OBJ<O1> opt1Obj, OBJ<O2> opt2Obj, OBJ<O3> opt3Obj) {
		super(cmd, keyObj, valObj, extObj, opt1Obj, opt2Obj);
		this.opt3Obj = opt3Obj;
	}

	@Override
	public String toString() {
		return super.toString() + SYMJ.ARROW_RIGHT_SPEC + "O3" + opt3Obj.toDebugStringTypeVal();

	}

	public O3 opt3() {
		return opt3Obj.val;
	}

	public static Cmd6 strTo(String cmd) {
		return of6(cmd);
	}

	public static <K, V, E, O1, O2, O3> Cmd6<K, V, E, O1, O2, O3> of6(String cmd, K key, V val, E ext, O1 opt1, O2 opt2, O3 opt3) {
		return of6(cmd, toArgs(cmd), OBJ.of(key), OBJ.of(val), OBJ.of(ext), OBJ.of(opt1), OBJ.of(opt2), OBJ.of(opt3));
	}

	public static <K, V, E, O1, O2, O3> Cmd6<K, V, E, O1, O2, O3> of6(String cmd) {
		return of6(cmd, null, null, null, null, null, null);
	}

	public static <K, V, E, O1, O2, O3> Cmd6<K, V, E, O1, O2, O3> of6(String cmd, OBJ<K> key, OBJ<V> val, OBJ<E> ext, OBJ<O1> opt1, OBJ<O2> opt2, OBJ<O3> opt3) {
		return of6(cmd, toArgs(cmd), key, val, ext, opt1, opt2, opt3);
	}

	public static <K, V, E, O1, O2, O3> Cmd6<K, V, E, O1, O2, O3> of6(String cmd, String[] cmd5, OBJ<K> key, OBJ<V> val, OBJ<E> ext, OBJ<O1> opt1, OBJ<O2> opt2, OBJ<O3> opt3) {
		key = toEqOrAny(ARRi.item(cmd5, 0, null), key);
		val = toEqOrAny(ARRi.item(cmd5, 1, null), val);
		ext = toEqOrAny(ARRi.item(cmd5, 2, null), ext);
		opt1 = toEqOrAny(ARRi.item(cmd5, 3, null), opt1);
		opt2 = toEqOrAny(ARRi.item(cmd5, 4, null), opt2);
		opt3 = toEqOrAny(ARRi.item(cmd5, 5, null), opt3);
		return new Cmd6(cmd, key, val, ext, opt1, opt2, opt3);
	}

	@Override
	public Cmd6 throwIsNotWhole() {
		super.throwIsNotWhole();
		IT.NN(opt3(), "except whole opt3");
		return this;
	}

}
