package mpe.functions;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface Function3E<A, B, C, R, E extends Throwable> {

	default R get(A a, B b, C c) throws E {
		return apply(a, b, c);
	}

	R apply(A a, B b, C c) throws E;

	default <V> Function3E<A, B, C, V, E> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c) -> after.apply(apply(a, b, c));
	}
}