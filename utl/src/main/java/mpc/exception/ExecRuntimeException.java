package mpc.exception;

public class ExecRuntimeException extends RuntimeException {
	public ExecRuntimeException() {
		super();
	}

	public ExecRuntimeException(String message) {
		super(message);
	}

	public ExecRuntimeException(String message, Object... args) {
		this(String.format(message, args));
	}

	public ExecRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public ExecRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
