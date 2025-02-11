package mpe.wthttp;

import mpc.exception.IErrorsCollector;
import mpc.exception.RequiredRuntimeException;
import mpe.core.ERR;
import mpu.X;
import mpu.core.ARG;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import mpu.str.USToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
