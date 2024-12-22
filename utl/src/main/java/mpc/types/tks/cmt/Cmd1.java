package mpc.types.tks.cmt;

import mpc.rfl.R;
import mpc.str.sym.SYMJ;
import mpu.IT;

import java.util.Objects;

public class Cmd1<K> extends Cmd {

	public final OBJ<K> keyObj;

	public Cmd1(String cmd, OBJ keyObj) {
		super(cmd);
		this.keyObj = keyObj;
	}

	public K key() {
		return keyObj.val;
	}

	public String keyStr() {
		return Objects.toString(key(), null);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + original + "]" + SYMJ.ARROW_RIGHT_SPEC + "KEY" + keyObj.toDebugStringTypeVal();

	}

	public static <K> Cmd1<K> of1(String cmd, OBJ<K> key) {
		key = toEqOrAny(cmd, key);
		return new Cmd1<K>(cmd, key);
	}

	public static <K> Cmd1<K> strTo(String cmd) {
		return new Cmd1(cmd, toEqOrAny(cmd, null));
	}

	@Override
	public Cmd1 throwIsNotWhole() {
		super.throwIsNotWhole();
		IT.NN(key(), "except whole key");
		return this;
	}
}
