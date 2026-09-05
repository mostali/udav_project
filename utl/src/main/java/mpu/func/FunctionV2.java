package mpu.func;

@FunctionalInterface
public interface FunctionV2<T, T2> {
	void apply(T t, T2 t2);
}