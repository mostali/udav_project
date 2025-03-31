//package mpe.rt;
//
//import mpc.num.UNum;
//import org.apache.commons.io.FileUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.nio.channels.FileChannel;
//import java.nio.channels.FileLock;
//import java.nio.channels.OverlappingFileLockException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.Semaphore;
//
//public class SyncProcess {
//
////	public static void main(String[] args) {
////		if (tryLock("f1", 10000)) {
////			new Thread() {
////				@Override
////				public void run() {
////					SLEEP.sleep(10000);
////					releaseProcessAll();
////					super.run();
////				}
////			}.start();
////		}
////		if (tryLock("f2", 5000)) {
////			P.p("process2 started");
////		} else {
////			L.error("Lock is FAIL");
////		}
////	}
//
//
//	private static File lockTempDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "app-mp-locks");
//	private static final SyncProcess INS = new SyncProcess();
//	public static final Logger L = LoggerFactory.getLogger(SyncProcess.class);
//
//	public static SyncProcess get() {
//		return INS;
//	}
//
//	static {
//		try {
//			FileUtils.forceMkdir(lockTempDir);
//		} catch (IOException e) {
//			throw new IllegalStateException("error create dir with lock's", e);
//		}
//	}
//
//	private SyncProcess() {
//	}
//
//	public static class LockSyncProcessException extends Exception {
//	}
//
//	private static final Semaphore LOCK_THREAD = new Semaphore(1);
//
//	public static void tryLock(String processName) throws SyncProcessLocked {
//		if (!tryLock(processName, 100000)) {
//			throw new SyncProcessLocked("Locked:" + processName);
//		}
//	}
//
//	public static class SyncProcessLocked extends Exception {
//		public SyncProcessLocked(String message) {
//			super(message);
//		}
//	}
//
//	public static boolean tryLock(String processName, long waitMs) {
//		try {
//
//			LOCK_THREAD.acquire();
//
//			File file = new File(lockTempDir, "_" + processName + ".lock");
//			RandomAccessFile randomAccessFile;
//			randomAccessFile = new RandomAccessFile(file, "rw");
//			FileChannel fileChannel = randomAccessFile.getChannel();
//			FileLock lock = null;
//			long dropDeadTime = System.currentTimeMillis() + waitMs;
//
//			long son = 100;
//
//			while (System.currentTimeMillis() < dropDeadTime) {
//
//				try {
//					lock = fileChannel.tryLock();
//				} catch (OverlappingFileLockException ex) {
//					L.warn("SyncProcess [{}] is BUSY after wait {}", processName, UNum.MShu(waitMs));
//					Thread.sleep(250); // 4 attempts/sec
//					continue;
//				}
//				if (lock != null) {
//					get()._LOCKS.put(processName, lock);
//					L.info("SyncProcess [{}] RUN", processName);
//					return true;
//				} else {
//					L.warn("SyncProcess NOT get sleep:" + son);
//					SLEEP.sleep(son);
//					if (son < 1200) {
//						son *= 1.65;
//					}
//				}
//			}
//
//		} catch (FileNotFoundException fex) {
//			throw new RuntimeException(fex);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		} catch (InterruptedException e) {
//			throw new RuntimeException(e);
//		} finally {
//			LOCK_THREAD.release();
//		}
//		return false;
//	}
//
//	private Map<String, FileLock> _LOCKS = new ConcurrentHashMap<String, FileLock>();
//
//	static {
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			public void run() {
//				releaseProcessAll();
//			}
//		});
//	}
//
//	private static void releaseProcessAll() {
//		get().crossProcessLockReleaseAll();
//	}
//
//	public static void releaseProcess(String processName) {
//		get().crossProcessLockRelease(processName);
//	}
//
//	private void crossProcessLockReleaseAll() {
//		for (String key : _LOCKS.keySet()) {
//			crossProcessLockRelease(key);
//		}
//	}
//
//	private void crossProcessLockRelease(String processName) {
//		FileLock _LOCK = _LOCKS.get(processName);
//		if (_LOCK != null) {
//			try {
//				_LOCK.release();
//				_LOCKS.remove(processName);
//				L.info("SyncProcess [{}] RELEASED", processName);
//			} catch (IOException e) {
//				L.error("crossProcessLockRelease:" + processName, e);
//			}
//		}
//	}
//
//}
