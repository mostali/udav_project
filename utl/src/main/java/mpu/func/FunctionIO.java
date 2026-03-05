package mpu.func;

@FunctionalInterface
public interface FunctionIO<T, R> {
	R apply(T t) throws Throwable;
}
