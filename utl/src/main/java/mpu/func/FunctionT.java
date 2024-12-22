package mpu.func;

@FunctionalInterface
public interface FunctionT<T, R> {
	R apply(T t) throws Throwable;
}
