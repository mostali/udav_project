package mpc.types;

import mpc.exception.RequiredRuntimeException;
import mpu.core.ARG;

import java.util.Optional;
import java.util.function.Supplier;

public class OptLazySupplier<T> {

	private Optional<T> opt;
	public final Supplier<T> getter;

	public OptLazySupplier(Supplier<T> supplier) {
		opt = null;
		getter = supplier;
	}

//	public OptSupplier(Optional<T> initialValue) {
//		opt = initialValue;
//		getter = () -> opt.orElse(null);
//	}

	public T getFresh(T... defRq) {
		opt = null;
		return get(defRq);
	}

	public T get(T... defRq) {
		if (opt == null) {
			T value = getter.get();
			opt = value == null ? Optional.ofNullable(value) : Optional.of(value); //lazy init
		}
		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("Except supplier not null value "), opt, defRq);
	}
}
