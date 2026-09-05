package mpc.exception;

public class ConfigException extends IllegalStateException {
	public ConfigException() {
		super();
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Object objectRq) {
		super(String.valueOf(objectRq));
	}

	public ConfigException(Throwable throwable) {
		super(throwable);
	}

	public ConfigException(String message, Object... args) {
		this(String.format(message, args));
	}

	public ConfigException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public ConfigException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

}
