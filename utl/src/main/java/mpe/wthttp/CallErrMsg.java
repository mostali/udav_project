package mpe.wthttp;

import mpc.exception.IErrorsCollector;
import mpe.core.ERR;
import mpu.X;

import java.util.LinkedList;
import java.util.List;

public class CallErrMsg implements IErrorsCollector {

	private List<Throwable> _errors;


	@Override
	public void addError(Throwable... ex) {
		if (_errors == null) {
			_errors = new LinkedList<>();
		}
		for (Throwable e : ex) {
			_errors.add(e);
		}
	}

	//
	//

	public String getErrsAsMsg(String head, boolean ol) {
		return isValid() ? "" : ERR.getMessagesAsStringWithHead(getErrors(), head, ol);
	}

	public List<Throwable> getErrors() {
		return _errors;
	}


	@Override
	public String toString() {
		return "CallErrMsg{" +
				", errs=" + X.sizeOf0(_errors) +
				'}';
	}
}
