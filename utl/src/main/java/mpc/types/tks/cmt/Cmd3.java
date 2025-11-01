package mpc.types.tks.cmt;

import mpu.IT;
import mpu.core.ARRi;
import mpc.str.sym.SYMJ;

public class Cmd3<K, V, E> extends Cmd2<K, V> {

	public final OBJ<E> extObj;

	public Cmd3(String cmd, OBJ<K> keyObj, OBJ<V> valObj, OBJ<E> extObj) {
		super(cmd, keyObj, valObj);
		this.extObj = extObj;
	}

	@Override
	public String toString() {
		return super.toString() + SYMJ.ARROW_RIGHT_SPEC + "EXT" + extObj.toDebugStringTypeVal();

	}

	public E ext() {
		return extObj.val;
	}

	public static Cmd3 strTo(String cmd) {
		return of3(cmd);
	}

	public static <K, V, E> Cmd3<K, V, E> of3(String cmd) {
		return of3(cmd, toArgs(cmd), null, null, null);
	}

	public static <K, V, E> Cmd3<K, V, E> of3(String cmd, String split_regex) {
		return of3(cmd, cmd.split(split_regex), null, null, null);
	}

	public static <K, V, E> Cmd3<K, V, E> of3(String cmd, K key, V val, E ext) {
		return of3(cmd, PTRX_DEF_SPACE_SEP, OBJ.of(key), OBJ.of(val), OBJ.of(ext));
	}

	public static <K, V, E> Cmd3<K, V, E> of3(String cmd, String regex, OBJ<K> key, OBJ<V> val, OBJ<E> ext) {
		return of3(cmd, cmd.split(regex), key, val, ext);
	}

	public static <K, V, E> Cmd3<K, V, E> of3(String cmd, OBJ<K> key, OBJ<V> val, OBJ<E> ext) {
		return of3(cmd, toArgs(cmd), key, val, ext);
	}

	public static <K, V, E> Cmd3<K, V, E> of3(String cmd, String[] cmd3, OBJ<K> key, OBJ<V> val, OBJ<E> ext) {
		key = toEqOrAny(ARRi.item(cmd3, 0, null), key);
		val = toEqOrAny(ARRi.item(cmd3, 1, null), val);
		ext = toEqOrAny(ARRi.item(cmd3, 2, null), ext);
		return new Cmd3(cmd, key, val, ext);
	}

	@Override
	public Cmd3 throwIsNotWhole() {
		super.throwIsNotWhole();
		IT.NN(ext(), "except whole ext");
		return this;
	}

	public boolean isEmptyAll() {
		return key() != null && val() != null && ext() != null;
	}
}
