package mpu.func;

@FunctionalInterface
public interface Predicate3<T, T2, T3> {
	boolean test(T t, T2 t2, T3 t3);
}
