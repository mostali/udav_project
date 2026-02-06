package mpc.exception;

public class WhatIsTypeException extends IllegalArgumentException {
	public WhatIsTypeException() {
		super();
	}

	public WhatIsTypeException(Enum type) {
		this(type == null ? null : type.name());
	}

	public WhatIsTypeException(Number type) {
		this(type == null ? null : type.toString());
	}

	public WhatIsTypeException(Class type) {
		this(type.getName());
	}

	public WhatIsTypeException(String message) {
		super(message);
	}

	public WhatIsTypeException(String message, Object... args) {
		this(String.format(message, args));
	}

	public WhatIsTypeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public WhatIsTypeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

}
