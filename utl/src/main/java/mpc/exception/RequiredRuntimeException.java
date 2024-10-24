package mpc.exception;

public class RequiredRuntimeException extends IllegalStateException {
	public RequiredRuntimeException() {
		super();
	}

	public RequiredRuntimeException(String message) {
		super(message);
	}

	public RequiredRuntimeException(Object objectRq) {
		super(String.valueOf(objectRq));
	}

	public RequiredRuntimeException(Throwable throwable) {
		super(throwable);
	}

	public RequiredRuntimeException(String message, Object... args) {
		this(String.format(message, args));
	}

	public RequiredRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public RequiredRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

}
