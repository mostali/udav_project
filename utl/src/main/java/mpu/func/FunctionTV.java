package mpu.func;

@FunctionalInterface
public interface FunctionTV<T> {
	void apply(T t) throws Throwable;
}
