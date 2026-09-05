package mpc.types.tks.cmt;

import mpu.IT;
import mpu.core.ARRi;
import mpc.str.sym.SYMJ;

public class Cmd4<K, V, E, O1> extends Cmd3<K, V, E> {

	public final OBJ<O1> opt1Obj;

	public Cmd4(String cmd, OBJ<K> keyObj, OBJ<V> valObj, OBJ<E> extObj, OBJ<O1> opt1Obj) {
		super(cmd, keyObj, valObj, extObj);
		this.opt1Obj = opt1Obj;
	}

	@Override
	public String toString() {
		return super.toString() + SYMJ.ARROW_RIGHT_SPEC + "O1" + opt1Obj.toDebugStringTypeVal();

	}

	public O1 opt1() {
		return opt1Obj.val;
	}

	public static Cmd4 strTo(String cmd) {
		return of4(cmd);
	}

	public static <K, V, E, O1> Cmd4<K, V, E, O1> of4(String cmd, K key, V val, E ext, O1 opt1) {
		return of4(cmd, toArgs(cmd), OBJ.of(key), OBJ.of(val), OBJ.of(ext), OBJ.of(opt1));
	}

	public static <K, V, E, O1> Cmd4<K, V, E, O1> of4(String cmd, OBJ<K> key, OBJ<V> val, OBJ<E> ext, OBJ<O1> opt1) {
		return of4(cmd, toArgs(cmd), key, val, ext, opt1);
	}

	public static <K, V, E, O1> Cmd4<K, V, E, O1> of4(String cmd, String split_regex) {
		return of4(cmd, cmd.split(split_regex), null, null, null, null);
	}

	public static <K, V, E, O1> Cmd4<K, V, E, O1> of4(String cmd) {
		return of4(cmd, null, null, null, null);
	}

	public static <K, V, E, O1> Cmd4<K, V, E, O1> of4(String cmd, String[] cmd4, OBJ<K> key, OBJ<V> val, OBJ<E> ext, OBJ<O1> opt1) {
		key = toEqOrAny(ARRi.item(cmd4, 0, null), key);
		val = toEqOrAny(ARRi.item(cmd4, 1, null), val);
		ext = toEqOrAny(ARRi.item(cmd4, 2, null), ext);
		opt1 = toEqOrAny(ARRi.item(cmd4, 3, null), opt1);
		return new Cmd4(cmd, key, val, ext, opt1);
	}

	@Override
	public Cmd4 throwIsNotWhole() {
		super.throwIsNotWhole();
		IT.NN(opt1(), "except whole opt1");
		return this;
	}
}
