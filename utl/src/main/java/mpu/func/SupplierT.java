package mpu.func;

@FunctionalInterface
public interface SupplierT<T> {
	T get() throws Throwable;
}
