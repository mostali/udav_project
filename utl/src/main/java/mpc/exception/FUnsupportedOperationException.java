package mpc.exception;

public class FUnsupportedOperationException extends UnsupportedOperationException {
	public FUnsupportedOperationException() {
		super();
	}

	public FUnsupportedOperationException(Enum type) {
		this(type == null ? null : type.name());
	}

	public FUnsupportedOperationException(Number type) {
		this(type == null ? null : type.toString());
	}

	public FUnsupportedOperationException(Class type) {
		this(type.getName());
	}

	public FUnsupportedOperationException(String message) {
		super(message);
	}

	public FUnsupportedOperationException(String message, Object... args) {
		this(String.format(message, args));
	}

	public FUnsupportedOperationException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public FUnsupportedOperationException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

}
