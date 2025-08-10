package mpc.types.tks.cmt;

import mpu.IT;
import mpu.core.ARRi;
import mpc.str.sym.SYMJ;

import java.util.Objects;

public class Cmd2<K, V> extends Cmd1<K> {

	public final OBJ<V> valObj;

	public Cmd2(String cmd, OBJ<K> keyObj, OBJ<V> valObj) {
		super(cmd, keyObj);
		this.valObj = valObj;
	}

	@Override
	public String toString() {
		return super.toString() + SYMJ.ARROW_RIGHT_SPEC + "VAL" + valObj.toDebugStringTypeVal();

	}

	public V val() {
		return valObj.val;
	}

	public static Cmd2 strTo(String cmd) {
		return of2(cmd);
	}

	public static <K, V> Cmd2<K, V> of2(String cmd, OBJ<K> key, OBJ<V> val) {
		return of2(cmd, toArgs(cmd), key, val);
	}

	public static <K, V> Cmd2<K, V> of2(String cmd) {
		return of2(cmd, toArgs(cmd), null, null);
	}

	public static <K, V> Cmd2<K, V> of2(String cmd, String del, boolean... normSpace) {
		return of2(cmd, toArgs(cmd, del, normSpace), null, null);
	}

	public static <K, V> Cmd2<K, V> of2(String cmd, String[] cmd2, OBJ<K> key, OBJ<V> val) {
		key = toEqOrAny(ARRi.item(cmd2, 0, null), key);
		val = toEqOrAny(ARRi.item(cmd2, 1, null), val);
		return new Cmd2(cmd, key, val);
	}

	@Override
	public boolean isOnlyOne() {
		return valObj.val == null;
	}

	public String valStr() {
		return Objects.toString(val(), null);
	}

	@Override
	public Cmd2 throwIsNotWhole() {
		super.throwIsNotWhole();
		IT.NN(val(), "except whole val");
		return this;
	}
}
