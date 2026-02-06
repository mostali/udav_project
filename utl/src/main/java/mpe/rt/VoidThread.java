//package mpe.rt;
//
//import mpc.log.LogTailReaderThread;
//import mpu.core.ARG;
//
//import java.util.concurrent.atomic.AtomicReference;
//
//public class VoidThread extends Thread {
//
//	private AtomicReference<Throwable> _gerror;
//
//	protected void set_result_globalError(Throwable t) {
//		if (_gerror == null) {
//			this._gerror = new AtomicReference<>();
//			this._gerror.set(t);
//			return;
//		}
//		IllegalStateException err = new IllegalStateException("GlobalError already init", t);
//		err.printStackTrace(System.err);
//		throw err;
//	}
//
//	private volatile boolean runState = true;
//
//	public void stopThread() {
//		this.runState = false;
//	}
//
//	public VoidThread(Runnable runnable, boolean... started) {
//		super(runnable, initName(null));
//		initGlobalErrorHandler();
//		if (ARG.isDefEqTrue(started)) {
//			start();
//		}
//	}
//
//	public VoidThread(boolean... started) {
//		this((String) null, started);
//	}
//
//	public VoidThread(String threadName, boolean... started) {
//		super(initName(threadName));
//		initGlobalErrorHandler();
//		if (ARG.isDefEqTrue(started)) {
//			start();
//		}
//	}
//
//	private void initGlobalErrorHandler() {
//		setUncaughtExceptionHandler((th, ex) -> set_result_globalError(ex));
//	}
//
//	private static String initName(String threadName) {
//		return threadName == null ? LogTailReaderThread.class.getSimpleName() : threadName;
//	}
//
//	public String getNameWithId() {
//		return getName() + "#" + getId();
//	}
//
//}
