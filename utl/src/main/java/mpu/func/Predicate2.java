package mpu.func;

@FunctionalInterface
public interface Predicate2<T, T2>  {
	boolean test(T t, T2 t2);
}
