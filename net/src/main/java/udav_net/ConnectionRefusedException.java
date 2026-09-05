package udav_net;

public class ConnectionRefusedException extends Exception {
	public ConnectionRefusedException() {
		super();
	}

	public ConnectionRefusedException(String message) {
		super(message);
	}

	public ConnectionRefusedException(String message, Object... args) {
		this(String.format(message, args));
	}

	public ConnectionRefusedException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public ConnectionRefusedException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

	public static boolean isConnectionRefusedException(Throwable ex) {
		return ex.getMessage().contains("(Connection refused)");
	}

	public static boolean isTooManyRedirectException(Throwable ex) {
		return ex.getMessage().contains("Too many redirects occurred trying to load URL");
	}
}