package mpc.exception;

public class CleanMessageRuntimeException extends RuntimeException implements ICleanMessage {
	public CleanMessageRuntimeException() {
		super();
	}

	public CleanMessageRuntimeException(String message) {
		super(message);
	}

	public CleanMessageRuntimeException(Enum message) {
		super(message.name());
	}

	public CleanMessageRuntimeException(String message, Object... args) {
		super(String.format(message, args));
	}

	public CleanMessageRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public CleanMessageRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

	@Override
	public String getCleanMessage() {
		return getMessage();
	}
}
