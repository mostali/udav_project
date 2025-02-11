package mpe.rt;

import lombok.SneakyThrows;
import mpc.exception.MultiCauseException;
import mpc.exception.RequiredRuntimeException;
import mpc.log.LogTailReaderThread;
import mpe.core.ERR;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Thread0<R> extends Thread {

	public static final Logger L = LoggerFactory.getLogger(Thread0.class);

	public static final Object NULL = new Object();

	private AtomicReference<R> _result;

	private AtomicReference<Throwable> _error;
	private AtomicReference<Throwable> _gerror;


//	private volatile boolean runStateFlag = true;
//
//	public void stopThreadFlag() {
//		this.runStateFlag = false;
//	}

	//
	//

	private void initGlobalErrorHandler() {
		setUncaughtExceptionHandler((th, ex) -> set_result_globalError(ex));
	}

	private static String initName(String threadName) {
		return threadName == null ? LogTailReaderThread.class.getSimpleName() : threadName;
	}

	public static String getNameId(Thread thread) {
		return thread.getName() + "#" + thread.getId();
	}

	public Thread0(String name, boolean... started) {
		super(initName(name));
		initGlobalErrorHandler();
		if (ARG.isDefEqTrue(started)) {
			start();
		}
	}

	public Thread0(String name, Runnable runnable, boolean... started) {
		super(runnable, initName(name));
		initGlobalErrorHandler();
		if (ARG.isDefEqTrue(started)) {
			start();
		}
	}

	public Thread0(Runnable runnable, boolean... started) {
		super(runnable, initName(null));
		initGlobalErrorHandler();
		if (ARG.isDefEqTrue(started)) {
			start();
		}
	}

	//
	//
	//

	@SneakyThrows
	public static <R> Thread0<R> runAndGet(int joinMs, Runnable runnable) {
		IT.isPosNotZero(joinMs, "Negative or zero time ms (%s) for join to Thread always return null", joinMs);
		Thread0 rThread = new Thread0("RunAndGetThread_" + Thread.currentThread().getName(), runnable, true);
		rThread.getAndWaitResult(joinMs);
		return rThread;
	}

	protected void set_result_object(R r) {
		if (_result == null) {
			this._result = new AtomicReference();
			this._result.set(r);
			return;
		}
		IllegalStateException err = new IllegalStateException("Result already init");
		err.printStackTrace(System.err);
		throw err;
	}

	public void set_result_error(Throwable t) {
		if (_error == null) {
			L.error("set_result_error", t);
			this._error = new AtomicReference<>();
			this._error.set(IT.NN(t));
			return;
		}
		IllegalStateException err = new IllegalStateException("ResultError already init", t);
		err.printStackTrace(System.err);
		throw err;
	}

	protected void set_result_globalError(Throwable t) {
		if (_gerror == null) {
			L.error("set_result_globalError", t);
			this._gerror = new AtomicReference<>();
			this._gerror.set(IT.NN(t));
			return;
		}
		IllegalStateException err = new IllegalStateException("GlobalError already init", t);
		err.printStackTrace(System.err);
		throw err;
	}


	public String getErrorMessage() {
		return getErrorsAny().stream().map(ERR::getStackTrace).collect(Collectors.joining("\n-----------\n"));
	}


	public boolean hasResultAny() {
		return _result != null;
	}

	public boolean hasResultNotNull() {
		return _result == null ? false : (_result.get() != null);
	}

	public R getResultObject(R... defRq) {
		return _result != null && _result.get() != null ? _result.get() : ARG.toDefThrowMsg(() -> "Except not null result", defRq);
	}

	public AtomicReference<R> getResultObjectAsRef(AtomicReference<R>... defRq) {
		return _result != null ? _result : ARG.toDefThrowMsg(() -> "Except not null result", defRq);
	}

	public Thread0<R> throwIfHasErrors() {
		if (hasErrorsAny()) {
			throw getErrorsAsMultiException();
		}
		return this;
	}

	public MultiCauseException getErrorsAsMultiException(MultiCauseException... defRq) {
		return hasErrorsAny() ? new MultiCauseException(getErrorsAny()) : ARG.toDefThrowMsg(() -> X.f("Errors not found"), defRq);
	}


	public boolean hasErrorsAny() {
		if (_error == null && _gerror == null) {
			return false;
		} else if (_error != null && _error.get() != null) {
			return true;
		}
		return _gerror != null && _gerror.get() != null;
	}

	public List<Throwable> getErrorsAny() {
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

	public R getAndWaitResult(long joinMs, R... defRq) {
		boolean skipErr = ARG.isDef(defRq);
		getAndWait(joinMs, skipErr);
		if (hasResultNotNull()) {
			return getResultObject(defRq);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except not null result, but happens timeout"), defRq);
	}

	@SneakyThrows
	private void getAndWait(long joinMs, boolean skipThrow) {
		boolean isAlive = super.isAlive();
		if (isAlive) {
			if (joinMs == 0) {
				if (L.isDebugEnabled()) {
					L.info("Join to '{}'..", getNameId(this));
				}
				super.join();
			} else if (joinMs > 0) {
				if (L.isDebugEnabled()) {
					L.info("Join to '{}' for '{}ms'..", getNameId(this), joinMs);
				}
				super.join(joinMs);
			}
		}
		if (hasErrorsAny()) {
			if (ARG.isDefNotEqTrue(skipThrow)) {
				X.throwException(getErrorsAsMultiException());
			}
			return;
		}
		if (hasResultAny()) {
			interrupt();
			return;
		}
//		if (!isAlive) {
//			set_result_object(null);
//			return;
//		}
//		return;//is alive
	}


}
