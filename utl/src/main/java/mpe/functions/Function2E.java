package mpe.functions;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface Function2E<A, B, R, E extends Throwable> {

	default R get(A a, B b) throws E {
		return apply(a, b);
	}

	R apply(A a, B b) throws E;

	default <V> Function2E<A, B, V, E> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b) -> after.apply(apply(a, b));
	}
}