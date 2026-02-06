package mpc.exception;

import mpc.arr.STREAM;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;

import java.util.Collection;
import java.util.List;

public interface IErrorsCollector {

	default String getMultiOrSingleErrorOrNullAsString() {
		Throwable multiOrSingleErrorOrNull = getMultiOrSingleErrorOrNull();
		return multiOrSingleErrorOrNull == null ? "null" : multiOrSingleErrorOrNull.getMessage();
	}

	default Throwable getMultiOrSingleErrorOrNull() {
		if (hasErrors()) {
			List<Throwable> errors = getErrors();
			return X.sizeOf(errors) == 1 ? errors.get(0) : new MultiCauseException(errors);
		}
		return null;
	}

//	default Throwable getMultiErrorOrNull() {
//		if (hasErrors()) {
//			return new MultiCauseException(getErrors());
//		}
//		return null;
//	}

	default IErrorsCollector throwIsErr() {
		return isValid() ? this : X.throwException(getMultiOrSingleErrorOrNull());
	}

	default boolean isValid() {
		return X.empty(getErrors());
	}

	default boolean hasErrors() {
		return X.notEmpty(getErrors());
	}

	List<Throwable> getErrors();

	default void addError(Collection<Throwable> errors) {
		if (X.notEmpty(errors)) {
			errors.forEach(this::addError);
		}
	}

	default void addError(String msg, Object... args) {
		addError(new FIllegalStateException(msg, args));
	}

	default void addErrorIfNotExists(String msg, Object... args) {
		String msg0 = X.f(msg, args);
		if (STREAM.anyMatch(getErrors(), e -> msg0.equals(e.getMessage()))) {
			return;
		}
		addError(new FIllegalStateException(msg, args));
	}

	default void addError(Throwable... ex) {
		List<Throwable> _errors = getErrors();
		IT.notNull(_errors, "init errors");
		for (Throwable e : ex) {
			_errors.add(e);
		}
	}

}
