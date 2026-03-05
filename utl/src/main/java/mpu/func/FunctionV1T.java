package mpu.func;

@FunctionalInterface
public interface FunctionV1T<T, E extends Throwable> {
	void apply(T t) throws E;
}
