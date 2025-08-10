package mpc.exception;

public class DRQ extends RequiredRuntimeException {
	public DRQ() {
		super();
	}

	public DRQ(String message) {
		super(message);
	}

	public DRQ(Object objectRq) {
		super(String.valueOf(objectRq));
	}

	public DRQ(Throwable throwable) {
		super(throwable);
	}

	public DRQ(String message, Object... args) {
		this(String.format(message, args));
	}

	public DRQ(Throwable throwable, String message) {
		super(message, throwable);
	}

	public DRQ(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

}
