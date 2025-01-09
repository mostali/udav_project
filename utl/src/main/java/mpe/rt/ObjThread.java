package mpe.rt;

import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ObjThread<R> extends Thread {

	public static final Logger L = LoggerFactory.getLogger(ObjThread.class);

//	public ObjThread() {
//		this._result = new ObjAtomicReference();
//	}

	private ObjAtomicReference<R> _result;

	private AtomicReference<Throwable> _error;
	private AtomicReference<Throwable> _gerror;


	public ObjThread(@NotNull String name, Runnable runnable, boolean... started) {
		super(runnable);
		setName(name);
		setUncaughtExceptionHandler((th, ex) -> set_result_globalError(ex));
		if (ARG.isDefEqTrue(started)) {
			start();
		}
	}

	public ObjThread(@NotNull String name, boolean... started) {
		super(name);
		setUncaughtExceptionHandler((th, ex) -> set_result_globalError(ex));
		if (ARG.isDefEqTrue(started)) {
			start();
		}
	}

	@SneakyThrows
	public static <R> ObjThread<R> runAndGet(int joinMs, Runnable runnable) {
		IT.isPosNotZero(joinMs, "Negative or zero time ms (%s) for join to Thread always return null", joinMs);
		ObjThread rThread = new ObjThread("RunAndGetThread_" + Thread.currentThread().getName(), runnable, true);
		rThread.getAndWaitResult(joinMs);
		return rThread;
	}

	public void set_result_error(Throwable t) {
		if (_error == null) {
			this._error = new AtomicReference<>();
			this._error.set(t);
		}
		IllegalStateException err = new IllegalStateException("ResultError already init", t);
		err.printStackTrace(System.err);
		throw err;
	}

	protected void set_result_globalError(Throwable t) {
		if (_gerror == null) {
			this._gerror = new AtomicReference<>();
			this._gerror.set(t);
		}
		IllegalStateException err = new IllegalStateException("GlobalError already init", t);
		err.printStackTrace(System.err);
		throw err;
	}

	protected void set_result_object(R r) {
		if (_result == null) {
			this._result = new ObjAtomicReference();
			this._result.set(r);
		}
		IllegalStateException err = new IllegalStateException("Result already init");
		err.printStackTrace(System.err);
		throw err;
	}

	public String getErrorMessage() {
		return getErrors().stream().map(ERR::getStackTrace).collect(Collectors.joining("\n-----------\n"));
	}

	public Object getResultObject(Object... defRq) {
		if (_result != null && _result.get() != null) {
			return _result.get();
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except not null result"), defRq);
	}

	public static class ObjAtomicReference<R> extends AtomicReference<R> {
		public ObjAtomicReference(R initialValue) {
			super(initialValue);
		}

		public ObjAtomicReference() {
		}
	}

	public boolean hasResult() {
		Boolean b = hasResultOrNull();
		return b == null ? false : b;
	}

	public Boolean hasResultOrNull() {
		return _result == null ? null : (_result.get() != null);
	}

	public Boolean hasErrors() {
		Boolean has = hasErrorsOrNull();
		return has != null && has;
	}

	public Boolean hasErrorsOrNull() {
		if (_error == null && _gerror == null) {
			return null;
		}
		return _error != null ? _error.get() != null : _gerror.get() != null;
	}

	public List<Throwable> getErrors() {
		List<Throwable> errors = ARR.asAL();
		if (_error == null && _gerror == null) {
			return ARR.as();
		}
		if (_error != null && _error.get() != null) {
			errors.add(_error.get());
		}
		if (_gerror != null && _gerror.get() != null) {
			errors.add(_gerror.get());
		}
		return errors;
	}

//	@SneakyThrows
//	public ObjAtomicReference getAndWaitResult() {
//		return getAndWaitResult(-1);
//	}

	public Object getAndWaitResult(int joinMs, Object... defRq) {
		boolean skipErr = ARG.isDef(defRq);
		getAndWait(joinMs, skipErr);
		if (hasResult()) {
			return getResultObject(defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except not null result"), defRq);

	}

	@SneakyThrows
	private void getAndWait(int joinMs, boolean skipThrow) {
		boolean isAlive = super.isAlive();
		if (isAlive) {
			if (joinMs == 0) {
				if (L.isDebugEnabled()) {
					L.info("Join to '{}'..", super.getName());
				}
				super.join();
			} else if (joinMs > 0) {
				if (L.isDebugEnabled()) {
					L.info("Join to '{}' for '{}ms'..", super.getName(), joinMs);
				}
				super.join(joinMs);
			}
		}
		Boolean hasError = hasErrorsOrNull();
		if (hasError != null && hasError) {
			if (ARG.isDefNotEqTrue(skipThrow)) {
				X.throwException(_error.get());
			}
			return;
		}
		Boolean rslt = hasResultOrNull();
		if (rslt != null) {
			return;
		}
		if (!isAlive) {
			set_result_object(null);
			return;
		}
		return;//is alive
	}

}
