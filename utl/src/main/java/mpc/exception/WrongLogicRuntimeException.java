package mpc.exception;

public class WrongLogicRuntimeException extends IllegalStateException {
	public WrongLogicRuntimeException() {
		super();
	}

	public WrongLogicRuntimeException(Enum type) {
		this(type.name());
	}

	public WrongLogicRuntimeException(String message) {
		super(message);
	}

	public WrongLogicRuntimeException(String message, Object... args) {
		this(String.format(message, args));
	}

	public WrongLogicRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public WrongLogicRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

}
