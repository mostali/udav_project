package mpc.types;

import lombok.RequiredArgsConstructor;
import mpc.exception.RequiredRuntimeException;
import mpu.core.ARG;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class SupplierAtomic<T> {

	public final AtomicReference<Optional<T>> ref;
	public final Supplier<T> getter;


	public SupplierAtomic(Supplier<T> supplier) {
		ref = new AtomicReference<>();
		getter = supplier;
	}

	public SupplierAtomic(Optional<T> initialValue) {
		ref = new AtomicReference<>(initialValue);
		getter = () -> ref.get().orElse(null);
	}


	public T get(T... defRq) {
		Optional<T> opt = ref.get();
		if (opt == null) {
			opt = Optional.of(getter.get()); //lazy init
		}
		return ARG.throwErrOpt(() -> new RequiredRuntimeException("Except supplier not null value "), opt, defRq);
	}
}
