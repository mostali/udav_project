package mpc.types.tks.cmt;

import mpu.IT;
import mpu.core.ARRi;
import mpc.str.sym.SYMJ;

public class Cmd5<K, V, E, O1, O2> extends Cmd4<K, V, E, O1> {

	public final OBJ<O2> opt2Obj;

	public Cmd5(String cmd, OBJ<K> keyObj, OBJ<V> valObj, OBJ<E> extObj, OBJ<O1> opt1Obj, OBJ<O2> opt2Obj) {
		super(cmd, keyObj, valObj, extObj, opt1Obj);
		this.opt2Obj = opt2Obj;
	}

	@Override
	public String toString() {
		return super.toString() + SYMJ.ARROW_RIGHT_SPEC + "O2" + opt2Obj.toDebugStringTypeVal();
	}

	public O2 opt2() {
		return opt2Obj.val;
	}

	public static Cmd5 strTo(String cmd) {
		return of5(cmd);
	}

	public static <K, V, E, O1, O2> Cmd5<K, V, E, O1, O2> of5(String cmd, K key, V val, E ext, O1 opt1, O2 opt2) {
		return of5(cmd, toArgs(cmd), OBJ.of(key), OBJ.of(val), OBJ.of(ext), OBJ.of(opt1), OBJ.of(opt2));
	}

	public static <K, V, E, O1, O2> Cmd5<K, V, E, O1, O2> of5(String cmd) {
		return of5(cmd, null, null, null, null, null);
	}

	public static <K, V, E, O1, O2> Cmd5<K, V, E, O1, O2> of5(String cmd, String split_regex) {
		return of5(cmd, cmd.split(split_regex), null, null, null, null, null);
	}

	public static <K, V, E, O1, O2> Cmd5<K, V, E, O1, O2> of5(String cmd, OBJ<K> key, OBJ<V> val, OBJ<E> ext, OBJ<O1> opt1, OBJ<O2> opt2) {
		return of5(cmd, toArgs(cmd), key, val, ext, opt1, opt2);
	}

	public static <K, V, E, O1, O2> Cmd5<K, V, E, O1, O2> of5(String cmd, String[] cmd5, OBJ<K> key, OBJ<V> val, OBJ<E> ext, OBJ<O1> opt1, OBJ<O2> opt2) {
		key = toEqOrAny(ARRi.item(cmd5, 0, null), key);
		val = toEqOrAny(ARRi.item(cmd5, 1, null), val);
		ext = toEqOrAny(ARRi.item(cmd5, 2, null), ext);
		opt1 = toEqOrAny(ARRi.item(cmd5, 3, null), opt1);
		opt2 = toEqOrAny(ARRi.item(cmd5, 4, null), opt2);
		return new Cmd5(cmd, key, val, ext, opt1, opt2);
	}

	@Override
	public Cmd5 throwIsNotWhole() {
		super.throwIsNotWhole();
		IT.NN(opt2(), "except whole opt2");
		return this;
	}
}
