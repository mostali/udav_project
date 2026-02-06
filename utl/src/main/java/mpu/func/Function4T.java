package mpu.func;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface Function4T<A, B, C, D, R, E extends Throwable> {

//	default R get(A a, B b, C c, D d) throws E {
//		return apply(a, b, c, d);
//	}

	R apply(A a, B b, C c, D d) throws E;

	default <V> Function4T<A, B, C, D, V, E> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c, D d) -> after.apply(apply(a, b, c, d));
	}
}