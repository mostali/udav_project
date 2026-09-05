package mpc.exception;

public class SimpleMessageRuntimeException extends RuntimeException implements ICleanMessage {
	public SimpleMessageRuntimeException() {
		super();
	}

	public SimpleMessageRuntimeException(String message) {
		super(message);
	}

	public SimpleMessageRuntimeException(Enum message) {
		super(message.name());
	}

	public SimpleMessageRuntimeException(String message, Object... args) {
		super(String.format(message, args));
	}

	public SimpleMessageRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public SimpleMessageRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

	@Override
	public String getCleanMessage() {
		return getMessage();
	}
}
