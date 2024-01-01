package mpc.rt;

import lombok.SneakyThrows;
import mpc.X;
import mpc.arr.Arr;
import mpc.ERR;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RThread<R> extends Thread {

	public static final Logger L = LoggerFactory.getLogger(RThread.class);

	private RAtomicReference<R> _result;

	private AtomicReference<Throwable> _error;
	private AtomicReference<Throwable> _gerror;

	public RThread(@NotNull String name) {
		super(name);
	}

	public RThread(Runnable target) {
		super(target);
	}

	public static <R> RThread<R> runAndGet(int joinMs, Runnable runnable) {
		ERR.isPosOrZero(joinMs, "negative join always return null");
		RThread rThread = new RThread(runnable);
		Thread.UncaughtExceptionHandler h = (th, ex) -> rThread.set_result_gerror(ex);
		rThread.setUncaughtExceptionHandler(h);
		rThread.start();
		rThread.getResultReference(joinMs);
		return rThread;
	}

	protected void set_result_error(Throwable t) {
		if (_error == null) {
			this._error = new AtomicReference<>();
		} else {
			throw new IllegalStateException("Error already init");
		}
		this._error.set(t);
	}

	protected void set_result_gerror(Throwable t) {
		if (_gerror == null) {
			this._gerror = new AtomicReference<>();
		} else {
			throw new IllegalStateException("GError already init");
		}
		this._gerror.set(t);
	}

	protected void set_result_object(R r) {
		if (_result == null) {
			this._result = new RAtomicReference();
		} else {
			throw new IllegalStateException("Result already init");
		}
		this._result.set(r);
	}

	public static class RAtomicReference<R> extends AtomicReference<R> {
		public RAtomicReference(R initialValue) {
			super(initialValue);
		}

		public RAtomicReference() {
		}
	}

	public Boolean hasResult() {
		return _result == null ? null : (_result.get() != null);
	}

	public Boolean hasError() {
		if (_error == null && _gerror == null) {
			return null;
		}
		return _error != null ? _error.get() != null : _gerror.get() != null;
	}

	public List<Throwable> getErrors() {
		List<Throwable> errors = Arr.as();
		if (_error == null && _gerror == null) {
			return Arr.as();
		}
		if (_error != null && _error.get() != null) {
			errors.add(_error.get());
		}
		if (_gerror != null && _gerror.get() != null) {
			errors.add(_gerror.get());
		}
		return errors;
	}

	@SneakyThrows
	public RAtomicReference getResultReference() {
		return getResultReference(-1);
	}

	@SneakyThrows
	public RAtomicReference getResultReference(int joinMs) {
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
		Boolean hasError = hasError();
		if (hasError != null && hasError) {
			return X.throwException(_error.get());
		}
		Boolean rslt = hasResult();
		if (rslt != null) {
			return _result;
		}
		if (!isAlive) {
			set_result_object(null);
			return _result;
		}
		return null;//is alive
	}

}
