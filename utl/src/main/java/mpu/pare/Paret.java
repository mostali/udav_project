package mpu.pare;

import mpc.exception.IEE;
import mpc.types.tks.FID;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;

//PareThree: Result(Object), Throwable, Log
public class Paret<K> extends Pare3<K, Throwable, String> {

	public Paret(K key, Throwable val, String ext) {
		super(key, val, ext);
	}

	public static <K> Paret<K> of(K key, Throwable val, String ext) {
		return new Paret(key, val, ext);
	}

	public static <K, T> Paret of(K key) {
		return new Paret(key, null, null);
	}

	public static Paret<FID> ofErr(Throwable ex, String... desc) {
		return new Paret(null, ex, ARG.toDefOrNull(desc));
	}

	@Override
	public String toString() {
		return X.f("Paret[%sk=%s,%sv=%s,%se=%s]", POINT_SYMJ, key(), POINT_SYMJ, val(), POINT_SYMJ, ext());
	}

	public boolean hasErrors() {
		return hasVal();
	}

	public String getErrAsMsgShortStacktrace() {
		return ERR.getStackTraceShort3(val());
	}

	public String getErrMsgOrNull(boolean... withCauseType) {
		boolean isDefEqTrue = ARG.isDefEqTrue(withCauseType);
		Throwable val = val();
		return isDefEqTrue ? ERR.getMessageWithTypeOrNull(val, null) : ERR.getMessage(val, null);
	}

	public Throwable getError(Throwable... defRq) {
		return ARG.throwNN0(val(), defRq);
	}

	public Paret<K> throwIfHasErrors() {
		if (hasErrors()) {
			X.throwException(getError());
		}
		IT.notNull(key(), "Except key-object");
		return this;
	}

	public K getResultObject() {
		return key();
	}

	public String getResultObjectAsString(String... defRq) {
		return ARG.throwNN(keyStr(), "except result", defRq).toString();
	}

	public <T> T getResultObjectAs(Class<T> asType, T... defRq) {
		return X.toObj(key(), asType, defRq);
	}
}

