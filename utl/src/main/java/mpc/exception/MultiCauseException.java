package mpc.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

public class MultiCauseException extends RuntimeException {
	public static final String NEXT_EXCEPTION_IN_CHAIN = "⤵Next exception in chain:\n";
	Throwable inner;
	MultiCauseException nextInChain;

	public MultiCauseException(List<Throwable> cause) {
		boolean selfCause = true;
		MultiCauseException nextInChain = null;
		MultiCauseException prevInChain = nextInChain;
		for (Throwable throwable : cause) {
			if (selfCause) {
				inner = throwable;
				selfCause = false;
				prevInChain = this;
				if (inner.getCause() == null) {
					continue;
				}
			}
			prevInChain = addCause(prevInChain, throwable);
			if (throwable.getCause() != null) {
				prevInChain = addCause(prevInChain, throwable.getCause());
			}
		}
	}

	private MultiCauseException addCause(MultiCauseException prevInChain, Throwable throwable) {
		MultiCauseException nextInChain = new MultiCauseException(throwable);
		prevInChain.setNextInChain(nextInChain);
		prevInChain = nextInChain;
		return prevInChain;
	}

	MultiCauseException(Throwable throwable) {
		inner = throwable;
	}

	public void setNextInChain(MultiCauseException nextInChain) {
		this.nextInChain = nextInChain;
	}

	@Override
	public String getMessage() {
		return inner.getMessage();
	}


	@Override
	public Throwable getCause() {
		return nextInChain;
	}

	@Override
	public void printStackTrace(PrintStream s) {
		inner.printStackTrace(s);
		MultiCauseException nextInChain = this.nextInChain;
		while (nextInChain != null) {
			s.print(NEXT_EXCEPTION_IN_CHAIN);
			nextInChain.inner.printStackTrace(s);
			nextInChain = nextInChain.nextInChain;
		}
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		inner.printStackTrace(s);
		MultiCauseException nextInChain = this.nextInChain;
		while (nextInChain != null) {
			s.print(NEXT_EXCEPTION_IN_CHAIN);
			nextInChain.inner.printStackTrace(s);
			nextInChain = nextInChain.nextInChain;
		}
	}
}
