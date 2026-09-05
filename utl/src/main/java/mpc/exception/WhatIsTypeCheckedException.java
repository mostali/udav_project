package mpc.exception;

public class WhatIsTypeCheckedException extends Exception {
	public WhatIsTypeCheckedException() {
		super();
	}

	public WhatIsTypeCheckedException(Enum type) {
		this(type == null ? null : type.name());
	}

	public WhatIsTypeCheckedException(Number type) {
		this(type == null ? null : type.toString());
	}

	public WhatIsTypeCheckedException(Class type) {
		this(type.getName());
	}

	public WhatIsTypeCheckedException(String message) {
		super(message);
	}

	public WhatIsTypeCheckedException(String message, Object... args) {
		this(String.format(message, args));
	}

	public WhatIsTypeCheckedException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public WhatIsTypeCheckedException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

}
