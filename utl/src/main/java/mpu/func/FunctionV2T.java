package mpu.func;

@FunctionalInterface
public interface FunctionV2T<T, T2, E extends Throwable> {
	void apply(T t, T2 t2) throws E;
}
