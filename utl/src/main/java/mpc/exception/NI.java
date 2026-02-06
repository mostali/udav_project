package mpc.exception;

import mpu.X;
import mpu.core.ARG;
import mpu.str.STR;

/**
 * Need Impl
 */
public class NI extends RuntimeException {
	public NI() {
		super();
	}

	public NI(Enum message) {
		this(message == null ? "enum is null" : message.name());
	}

	public NI(Class message) {
		this(message == null ? "class is null" : message.getName());
	}

	public NI(String message) {
		super(message);
	}

	public NI(String message, Object... args) {
		this(String.format(message, args));
	}

	public NI(Throwable throwable, String message) {
		super(message, throwable);
	}

	public NI(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

	public static NI throwError(String msg, Object... args) {
		throw new NI(msg, args);
	}

	public static NI stop(Exception ex) {
		return new NI(ex, "stop");
	}

	public static <T> T stop0(Object... msg) {
		return X.throwException(stop(msg));
	}

	public static NI stop(Object... msg) {
		return X.throwException(new NI(STR.formatAllOr("stop(ni)", msg)));
	}

	public static NI wrongLogic(Object... msg) {
		return X.throwException(new NI(STR.formatAllOr("stop(ni)", msg)));
	}
}
