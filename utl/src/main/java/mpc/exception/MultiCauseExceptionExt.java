package mpc.exception;

import mpc.args.ARG;
import mpc.arr.ArrItem;
import mpc.ERR;
import mpc.core.UErr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

public class MultiCauseExceptionExt extends RuntimeException {

	public static final Logger L = LoggerFactory.getLogger(MultiCauseExceptionExt.class);

	public static final String CHAIN_ELEMENTS_DELIM = "⤵Next exception in chain:\n";

	protected Throwable inner;
	protected MultiCauseExceptionExt nextInChain;

	private int typeOverrideMessage = 1;

	public MultiCauseExceptionExt setTypeOverrideMessage(int typeOverrideMessage) {
		this.typeOverrideMessage = typeOverrideMessage;
		return this;
	}

	protected MultiCauseExceptionExt(Throwable throwable) {
		this.inner = throwable;
	}

	public static Throwable createSingleOrMultiThrowable(Collection<Throwable> catchErrors) {
		ERR.notEmpty(catchErrors);
		switch (catchErrors.size()) {
			case 1:
				return ArrItem.first(catchErrors);
			default:
				return new MultiCauseExceptionExt(catchErrors);
		}
	}

	public static MultiCauseExceptionExt createMultiThrowable(Collection<Throwable> throwables) {
		Throwable t = createSingleOrMultiThrowable(throwables);
		if (t instanceof MultiCauseExceptionExt) {
			return (MultiCauseExceptionExt) t;
		}
		return new MultiCauseExceptionExt(Arrays.asList(t));
	}

	public MultiCauseExceptionExt getCause() {
		return this.nextInChain;
	}

	public Throwable getFirstCause() {
		return inner;
	}

	public MultiCauseExceptionExt(Throwable... errors) {
		this(Arrays.asList(errors));
	}

	public MultiCauseExceptionExt(Collection<Throwable> errors) {
		boolean selfCause = true;
		MultiCauseExceptionExt nextInChain = null;
		MultiCauseExceptionExt prevInChain = nextInChain;
		Iterator errorsIterator = errors.iterator();

		while (true) {
			Throwable throwable;
			do {
				if (!errorsIterator.hasNext()) {
					return;
				}

				throwable = (Throwable) errorsIterator.next();
				if (!selfCause) {
					break;
				}

				this.inner = throwable;
				selfCause = false;
				prevInChain = this;
			} while (this.inner.getCause() == null);

			prevInChain = addCause(prevInChain, throwable);
			if (throwable.getCause() != null) {
				prevInChain = addCause(prevInChain, throwable.getCause());
			}
		}
	}

	private static MultiCauseExceptionExt addCause(MultiCauseExceptionExt prevInChain, Throwable throwable) {
		MultiCauseExceptionExt nextInChain = new MultiCauseExceptionExt(throwable);
		prevInChain.setNextInChain(nextInChain);
		return nextInChain;
	}

	public void setNextInChain(MultiCauseExceptionExt nextInChain) {
		this.nextInChain = nextInChain;
	}

	public void printStackTrace(PrintStream s) {
		this.inner.printStackTrace(s);
		for (MultiCauseExceptionExt nextInChain = this.nextInChain; nextInChain != null; nextInChain = nextInChain.nextInChain) {
			s.print(CHAIN_ELEMENTS_DELIM);
			nextInChain.inner.printStackTrace(s);
		}
	}

	public void printStackTrace(PrintWriter s) {
		this.inner.printStackTrace(s);
		for (MultiCauseExceptionExt nextInChain = this.nextInChain; nextInChain != null; nextInChain = nextInChain.nextInChain) {
			s.print(CHAIN_ELEMENTS_DELIM);
			nextInChain.inner.printStackTrace(s);
		}
	}

	public <T> T getCauseOfType(Class<T> type, boolean... required) {
		Throwable first = getFirstCause();
		if (type.isAssignableFrom(first.getClass())) {
			return (T) first;
		}
		for (MultiCauseExceptionExt nextInChain = this.nextInChain; nextInChain != null; nextInChain = nextInChain.nextInChain) {
			if (type.isAssignableFrom(nextInChain.inner.getClass())) {
				return (T) nextInChain.inner;
			}
		}
		if (ARG.isDefEqTrue(required)) {
			throw new RequiredRuntimeException("Cause of type not found:" + type);
		}
		return null;
	}

	public Iterator<Throwable> getAllThrowablesIterator(boolean... includeChildCauses) {
		return getAllThrowables(includeChildCauses).iterator();
	}

	public List<Throwable> getAllThrowables(boolean... includeChildCauses) {
		List<Throwable> list = new ArrayList<>();
//		Throwable first = getFirstCause();
//		if (first == null) {
//			L.error("FirstCause is NULL");
//		} else {
//			list.add(first);
//		}
		boolean includeChildCauses_or_onlyHostError = ARG.isDefEqTrue(includeChildCauses);
		Throwable cause = null;
		for (MultiCauseExceptionExt nextInChain = this.nextInChain; nextInChain != null; nextInChain = nextInChain.nextInChain) {
			Throwable inner = nextInChain.inner;
//			if (inner == null) {//по идее такого не должно быть
//				L.error("Попытка добавить null из MultiCauseExceptionExt..", new InvalidLogicRuntimeException("Попытка добавить null из MultiCauseExceptionExt.." + nextInChain, this));
//				continue;
//			}
			if (includeChildCauses_or_onlyHostError || inner != cause) {
				list.add(inner);
			}
			cause = inner.getCause();

		}
		return list;
	}

	@Override
	public String getMessage() {
		switch (typeOverrideMessage) {
			case 0:
				return this.inner.getMessage();
			case 1:
			case 2:
				return getMessage(typeOverrideMessage == 2);
			default:
				throw new WhatIsTypeException(typeOverrideMessage);
		}
	}

	public String getMessage(boolean... includeChildCauses_or_onlyHostError) {
		List<Throwable> errors = getAllThrowables(includeChildCauses_or_onlyHostError);
		return UErr.getMessagesAsString(errors, ";");
	}

}
