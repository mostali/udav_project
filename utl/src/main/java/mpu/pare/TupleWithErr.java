package mpu.pare;

import mpc.exception.IErrorsCollector;

import java.util.LinkedList;
import java.util.List;

public class TupleWithErr<T> extends Tuple<T> implements IErrorsCollector {

	public TupleWithErr(T[] obs) {
		super(obs);
	}

	private List<Throwable> _errors;

	@Override
	public List<Throwable> getErrors() {
		return _errors != null ? _errors : (_errors = new LinkedList<>());
	}
}
