package mpu.pare;

import mpu.X;
import mpu.core.ARG;

//Pare with Reason
public class Parer<T> extends Pare<T, String> {

	public Parer(T key, String val) {
		super(key, val);
	}

	public static <K> Parer<K> of(K key, String val) {
		return new Parer(key, val);
	}

	public static <T> Parer<T> ofKey(T result, String reason) {
		return of(result, null);
	}

	@Override
	public String toString() {
		return X.f("Parer[%sk=%s,%sv=%s]", POINT_SYMJ, key(), POINT_SYMJ, val());
	}

	public boolean hasErrors() {
		return hasVal();
	}

//	public String getErrAsStacktraceMsg() {
//		return ERR.getStackTraceShort3(val());
//	}

	public String getReasonOrNull() {
		return val();
	}

	public T getResult(T... defRq) {
		T key = key();
		if (key != null) {
			return key;
		}
		return ARG.throwMsg(() -> X.f("Except ParerObject. Reason [%s]", getReasonOrNull()), defRq);
	}

//	public Parer<K> throwIfHasErrors() {
//		if (hasErrors()) {
//			X.throwException(getError());
//		}
//		IT.notNull(key(), "Except key-object");
//		return this;
//	}
}

