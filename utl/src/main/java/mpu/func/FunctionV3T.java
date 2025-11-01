package mpu.func;

@FunctionalInterface
public interface FunctionV3T<T, T2, T3, E extends Throwable> {
	void apply(T t, T2 t2, T3 t3) throws E;
}
