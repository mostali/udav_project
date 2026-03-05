package mpu.pare;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Tuplet<T> extends Tuplee<T> {

	//	final @Getter Map map = new LinkedHashMap();
	final @Getter AtomicReference<Tuplet> inTuple;

	public Tuplet(T[] obs) {
		super(obs);
		inTuple = new AtomicReference<>();
	}

	private List<Throwable> _errors;

	public static Tuplet of(Object[] objects) {
		return new Tuplet(objects);
	}

	@Override
	public List<Throwable> getErrors() {
		return _errors != null ? _errors : (_errors = new LinkedList<>());
	}
}
