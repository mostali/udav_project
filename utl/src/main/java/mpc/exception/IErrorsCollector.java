package mpc.exception;

import mpc.arr.STREAM;
import mpu.IT;
import mpu.X;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface IErrorsCollector {

	static boolean hasErrors(List<IErrorsCollector> results) {
		return hasErrors(results, null);
	}

	static boolean hasErrors(List<IErrorsCollector> operations, Predicate<Throwable> checkError) {
		return X.notEmpty(operations) && operations.stream().anyMatch(oper -> {
			List<Throwable> operErrors = oper.getErrors();
			if (X.empty(operErrors)) {
				return false;
			} else if (checkError == null) {
				return true;
			}
			return operErrors.stream().anyMatch(checkError);
		});
	}

	default boolean checkNotEmpty(CharSequence str, String msg, Object... args) {
		if (str != null && str.length() > 0) {
			return true;
		}
		addError(new FIllegalArgumentException(msg, args));
		return false;
	}

	default boolean checkNotNull(Object obj, String msg, Object... args) {
		if (obj != null) {
			return true;
		}
		addError(new FIllegalArgumentException(msg, args));
		return false;
	}

	default String getMultiOrSingleErrorOrNullStr() {
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

	default void addErrors(Collection<Throwable> errors) {
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
