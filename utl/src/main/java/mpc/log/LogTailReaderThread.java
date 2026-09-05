//package mpc.log;
//
//import com.google.common.base.Stopwatch;
//import mpe.rt.SLEEP;
//import mpu.IT;
//import mpu.X;
//import mpu.core.ARG;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public abstract class LogTailReaderThread extends Thread {
//
//	private Throwable globalError;
//
//	final String fileName;
//	final long checkEveryMs;
//	final long maxLifeTimeMs;
//
//	final int countTailLines;
//
//	volatile boolean runState = true;
//
//	public void stopThread() {
//		this.runState = false;
//	}
//
//	public LogTailReaderThread(boolean... started) {
//		this(null, LogTailReader.DEF_LOCATION_SERVER_LOG, 3000, 100, 300_000, started);
//	}
//
//	public LogTailReaderThread(String threadName, String logFile, long checkEveryMs, int countTailLines, long maxLifeTimeMs, boolean... started) {
//		super(threadName == null ? LogTailReaderThread.class.getSimpleName() : threadName);
//
//		this.fileName = IT.isFileExist(logFile);
//		this.checkEveryMs = IT.isPosNotZero(checkEveryMs);
//		this.countTailLines = IT.isPosNotZero(countTailLines);
//		this.maxLifeTimeMs = IT.isPosNotZero(maxLifeTimeMs);
//
//		setUncaughtExceptionHandler((th, ex) -> set_result_globalError(ex));
//
//		if (ARG.isDefEqTrue(started)) {
//			start();
//		}
//	}
//
//	protected void set_result_globalError(Throwable t) {
//		this.globalError = t;
//	}
//
//	private Stopwatch startedTimer;
//
//	@Override
//	public void run() {
//
//		startedTimer = maxLifeTimeMs <= 0 ? null : Stopwatch.createStarted();
//
//		LogTailReader logTailReader = new LogTailReader(fileName);
//		while (runState) {
//			List<String> tailLines = logTailReader.readNextTailLogLines(countTailLines);
//			if (X.notEmpty(tailLines)) {
//				walkNextLogLines(tailLines);
//			}
//			if (startedTimer != null && startedTimer.elapsed(TimeUnit.MILLISECONDS) >= maxLifeTimeMs) {
//				runState = false;
//				break;
//			}
//			SLEEP.sleep(checkEveryMs);
//		}
//		L.info("Thread {} stoped", this);
//	}
//
//	public abstract void walkNextLogLines(List<String> tailLines);
//}
