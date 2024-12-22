//package mpe.rt;
//
//import lombok.SneakyThrows;
//import mpe.core.ERR;
//import mpu.IT;
//import mpu.X;
//import mpu.core.ARR;
//import org.jetbrains.annotations.NotNull;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//
//public class SimpleThread<R> extends Thread {
//
//	public static final Logger L = LoggerFactory.getLogger(SimpleThread.class);
//
//	private ObjAtomicReference<R> _result;
//
//	private AtomicReference<Throwable> _error;
//	private AtomicReference<Throwable> _gerror;
//
//	public SimpleThread() {
//		super();
//		start();
//	}
//
//	public SimpleThread(@NotNull String name) {
//		super(name);
//	}
//
//	//
//	public SimpleThread(Runnable target) {
//		super(target);
//	}
//
//	@SneakyThrows
//	public static <R> SimpleThread<R> runAndGet(int joinMs, Runnable runnable) {
//		IT.isPosOrZero(joinMs, "negative join always return null");
//		SimpleThread rThread = new SimpleThread(runnable);
//		UncaughtExceptionHandler h = (th, ex) -> rThread.set_result_gerror(ex);
//		rThread.setUncaughtExceptionHandler(h);
//		rThread.start();
//		rThread.getAndWaitResultReference(joinMs);
////		rThread.join(joinMs);
//		return rThread;
//	}
//
//	public void set_result_error(Throwable t) {
//		if (_error == null) {
//			this._error = new AtomicReference<>();
//		} else {
//			throw new IllegalStateException("Error already init");
//		}
//		this._error.set(t);
//	}
//
//	protected void set_result_gerror(Throwable t) {
//		if (_gerror == null) {
//			this._gerror = new AtomicReference<>();
//		} else {
//			throw new IllegalStateException("GError already init");
//		}
//		this._gerror.set(t);
//	}
//
//	protected void set_result_object(R r) {
//		if (_result == null) {
//			this._result = new ObjAtomicReference();
//		} else {
//			throw new IllegalStateException("Result already init");
//		}
//		this._result.set(r);
//	}
//
//	public String getErrorMessage() {
//		return getErrors().stream().map(ERR::getStackTrace).collect(Collectors.joining("\n-----------\n"));
//	}
//
//	public static class ObjAtomicReference<R> extends AtomicReference<R> {
//		public ObjAtomicReference(R initialValue) {
//			super(initialValue);
//		}
//
//		public ObjAtomicReference() {
//		}
//	}
//
//	public Boolean hasResult() {
//		return _result == null ? null : (_result.get() != null);
//	}
//
//	public Boolean hasErrors() {
//		Boolean has = hasErrorsOrNull();
//		return has != null && has;
//	}
//
//	public Boolean hasErrorsOrNull() {
//		if (_error == null && _gerror == null) {
//			return null;
//		}
//		return _error != null ? _error.get() != null : _gerror.get() != null;
//	}
//
//	public List<Throwable> getErrors() {
//		List<Throwable> errors = ARR.as();
//		if (_error == null && _gerror == null) {
//			return ARR.as();
//		}
//		if (_error != null && _error.get() != null) {
//			errors.add(_error.get());
//		}
//		if (_gerror != null && _gerror.get() != null) {
//			errors.add(_gerror.get());
//		}
//		return errors;
//	}
//
//	@SneakyThrows
//	public ObjAtomicReference getAndWaitResultReference() {
//		return getAndWaitResultReference(-1);
//	}
//
//	@SneakyThrows
//	public ObjAtomicReference getAndWaitResultReference(int joinMs) {
//		boolean isAlive = super.isAlive();
//		if (isAlive) {
//			if (joinMs == 0) {
//				if (L.isDebugEnabled()) {
//					L.info("Join to '{}'..", super.getName());
//				}
//				super.join();
//			} else if (joinMs > 0) {
//				if (L.isDebugEnabled()) {
//					L.info("Join to '{}' for '{}ms'..", super.getName(), joinMs);
//				}
//				super.join(joinMs);
//			}
//		}
//		Boolean hasError = hasErrorsOrNull();
//		if (hasError != null && hasError) {
//			return X.throwException(_error.get());
//		}
//		Boolean rslt = hasResult();
//		if (rslt != null) {
//			return _result;
//		}
//		if (!isAlive) {
//			set_result_object(null);
//			return _result;
//		}
//		return null;//is alive
//	}
//
//}
