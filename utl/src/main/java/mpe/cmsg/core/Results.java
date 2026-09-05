package mpe.cmsg.core;

import mpc.exception.IErrorsCollector;
import mpu.X;
import mpu.core.ARR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Results<T extends IErrorsCollector> extends ErrorCollector {

	public static final Logger L = LoggerFactory.getLogger(Results.class);

	private List<T> _results;

	public void addResult(T... ex) {
		if (_results == null) {
			_results = new LinkedList<>();
		}
		for (T e : ex) {
//			if(re)
			if (e.hasErrors()) {
				addErrors(e.getErrors());
			}
		}
	}

	@Override
	public boolean hasErrors() {
		return _results.stream().anyMatch(IErrorsCollector::hasErrors);
	}

	@Override
	public List<Throwable> getErrors() {
		if (_results == null) {
			return ARR.EMPTY_LIST;
		}
		return _results.stream().map(r -> X.toObjOr(r.getErrors(), (List<Throwable>) ARR.EMPTY_LIST)).flatMap(Collection::stream).collect(Collectors.toList());
	}

	//
	//

	public List<T> getResults() {
		return _results;
	}


	@Override
	public String toString() {
		return "Results{" + ", results=" + X.sizeOf0(_results) + ", errors=" + X.sizeOf0(getErrors()) + '}';
	}

	public void clear() {
		if (X.notEmpty(getResults())) {
			getResults().clear();
		}
		if (X.notEmpty(getErrors())) {
			getErrors().clear();
		}
	}
}
