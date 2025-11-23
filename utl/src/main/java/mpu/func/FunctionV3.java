package mpu.func;

@FunctionalInterface
public interface FunctionV3<T, T2, T3> {
	void apply(T t, T2 t2, T3 t3);
}