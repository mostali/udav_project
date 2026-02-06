package mpc.exception;

public class WarnMessageRuntimeException extends RuntimeException implements ICleanMessage {
	public WarnMessageRuntimeException() {
		super();
	}

	public WarnMessageRuntimeException(String message) {
		super(message);
	}

	public WarnMessageRuntimeException(Enum message) {
		super(message.name());
	}

	public WarnMessageRuntimeException(String message, Object... args) {
		super(String.format(message, args));
	}

	public WarnMessageRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public WarnMessageRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

	@Override
	public String getCleanMessage() {
		return getMessage();
	}
}
