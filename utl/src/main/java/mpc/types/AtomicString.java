package mpc.types;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicString extends AtomicReference<String> {

	public AtomicString() {
	}

	public AtomicString(String initialValue) {
		super(initialValue);
	}
}
