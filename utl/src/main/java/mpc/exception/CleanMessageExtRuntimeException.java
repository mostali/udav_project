package mpc.exception;

public class CleanMessageExtRuntimeException extends RuntimeException implements ICleanMessage {
	public CleanMessageExtRuntimeException() {
		super();
	}

	public CleanMessageExtRuntimeException(String message) {
		super(message);
	}

	public CleanMessageExtRuntimeException(Enum message) {
		super(message.name());
	}

	public CleanMessageExtRuntimeException(String message, Object... args) {
		super(String.format(message, args));
	}

	public CleanMessageExtRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public CleanMessageExtRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

	private Throwable cleanCause;

	public CleanMessageExtRuntimeException setCleanCause(Throwable cleanCause) {
		this.cleanCause = cleanCause;
		return this;
	}

	public CleanMessageExtRuntimeException setCleanCause(String cleanCause) {
		this.cleanCause = new CleanMessageRuntimeException(this, cleanCause);
		return this;
	}

	@Override
	public String getCleanMessage() {
		return cleanCause.getMessage();
	}
}
