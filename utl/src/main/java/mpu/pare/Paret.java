package mpu.pare;

import mpe.core.ERR;
import mpu.IT;
import mpu.X;

//PareThree
public class Paret<K> extends Pare3<K, Throwable, String> {

	public Paret(K key, Throwable val, String ext) {
		super(key, val, ext);
	}


	public static <K> Paret<K> of(K key, Throwable val, String ext) {
		return new Paret(key, val, ext);
	}

	public static <T> Paret<T> ofKey(T postId) {
		return of(postId, null, null);
	}

	@Override
	public String toString() {
		return X.f("Paret[%sk=%s,%sv=%s,%se=%s]", POINT_SYMJ, key(), POINT_SYMJ, val(), POINT_SYMJ, ext());
	}

	public boolean hasErrors() {
		return hasVal();
	}

	public String getErrAsStacktraceMsg() {
		return ERR.getStackTraceShort3(val());
	}

	public String getErrMsgOrNull() {
		return ERR.getMessageOr(val(), null);
	}

	public Throwable getError() {
		return val();
	}

	public Paret<K> throwIfHasErrors() {
		if (hasErrors()) {
			X.throwException(getError());
		}
		IT.notNull(key(), "Except key-object");
		return this;
	}
}

