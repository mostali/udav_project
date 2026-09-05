package mpu.func;

@FunctionalInterface
public interface FunctionV4<T, T2, T3, T4> {
	void apply(T t, T2 t2, T3 t3, T4 t4);
}