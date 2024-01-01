package mpc.exception;

import mpc.X;
import mpc.args.ARG;

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

	public static void throwError(String... message) {
		throw new NI(ARG.toDefOr("", message));
	}

	public static NI stop(Exception ex) {
		return new NI(ex, "stop");
	}

	public static NI stop(Object... msg) {
		NI ni;
		if (ARG.isDef(msg)) {
			ni = new NI(String.valueOf(ARG.toDef(msg)));
		} else {
			ni = new NI("stop");
		}
		X.throwException(ni);
		return ni;
	}
}
