package mpc.types;

import mpu.core.QDate;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicQDate extends AtomicReference<QDate> {

	public AtomicQDate() {
	}

	public AtomicQDate(QDate initialValue) {
		super(initialValue);
	}

	public void setNow() {
		set(QDate.now());
	}
}
