//package mpc.types;
//
//import lombok.RequiredArgsConstructor;
//import mpc.exception.WhatIsTypeException;
//import mpu.IT;
//
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.atomic.AtomicReference;
//
//@RequiredArgsConstructor
//public class AtomicObj<T> {
//	final T atomicValue;
//	//		final Class<T> type;
//
//	public T get() {
//		return atomicValue;
//	}
//
//	public AtomicObj set(Object val) {
//		if (atomicValue instanceof AtomicString) {
//			((AtomicString) atomicValue).set(IT.isType0(val, String.class));
//		} else if (atomicValue instanceof AtomicBoolean) {
//			((AtomicBoolean) atomicValue).set(IT.isType0(val, Boolean.class));
//		} else if (atomicValue instanceof AtomicInteger) {
//			((AtomicInteger) atomicValue).set(IT.isType0(val, Integer.class));
//		} else if (atomicValue instanceof AtomicLong) {
//			((AtomicLong) atomicValue).set(IT.isType0(val, Long.class));
//		} else if (atomicValue instanceof AtomicReference) {
//			((AtomicReference) atomicValue).set(val);
//		} else {
//			throw new WhatIsTypeException(atomicValue.getClass());
//		}
//		return this;
//	}
//
//	public static <T> AtomicObj of(Object atomicImpl) {
//		return new AtomicObj(atomicImpl);
//	}
//
//}
