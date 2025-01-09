package mpc.exception;

import mpu.IT;
import mpu.X;

import java.util.Collection;
import java.util.List;

public interface IErrorsCollector {

	List<Throwable> getErrors();

	default void addError(Collection<Throwable> errors) {
		if (X.notEmpty(errors)) {
			errors.forEach(this::addError);
		}
	}

	default void addError(String msg, Object... args) {
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
