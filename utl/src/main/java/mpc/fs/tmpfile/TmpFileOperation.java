package mpc.fs.tmpfile;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import mpu.X;
import mpu.core.ARG;
import mpc.fs.UFS;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public abstract class TmpFileOperation<T> {

	final boolean isFolder;

	private @Setter @Getter Optional<T> operationResult;

	public TmpFileOperation() {
		this(false);
	}

	protected TmpFileOperation(boolean isFolder) {
		this.isFolder = isFolder;
	}

	//		private static final ExecutorService DELETE_SERVICE = Executors.newSingleThreadExecutor();

	//		public static void deleteFile(final File file) {
//			if (file != null) {
//				UFS_BASE.RM.removeFileQk(file);

	/// /				DELETE_SERVICE.submit(new Runnable() {
	/// /					@Override
	/// /					public void run() {
	/// /						file.delete();
	/// /					}
	/// /				});
//			}
//		}
	public static void deleteAsync(Path tmpFile, int sleepDelayMs) {
		new Thread() {
			@SneakyThrows
			@Override
			public void run() {
				try {
					Thread.sleep(sleepDelayMs);
				} finally {
					UFS.RM.fileQk(tmpFile);
				}
			}
		}.start();
	}

	public static String of(Function<Path, String> operationImpl) {
		AtomicReference<String> st = new AtomicReference<>();
		new TmpFileOperation() {
			@Override
			public void doOperationImpl(Path tmpFile) {
				st.set(operationImpl.apply(tmpFile));
			}
		}.doOperation();
		return st.get();
	}

	public TmpFileOperation doOperation() {
		Path file = doOperationBefore();
		try {
			doOperationImpl(file);
		} catch (Throwable ex) {
//			doOperationError(file, ex);
			X.throwException(ex);
		} finally {
			doOperationAfter(file);
		}
		return this;
	}

	public abstract void doOperationImpl(Path tmpFile) throws Exception;

	@SneakyThrows
	public Path doOperationBefore() {
//		Path path = Paths.get("/tmp/TmpFileOperation." + (skipRemove ? "SKIP_REMOVE" : UUID.randomUUID()));
		Path path = Paths.get("./tmp/TmpFileOperation." + (skipRemove ? "SKIP_REMOVE" : UUID.randomUUID()));
		return !skipRemove ?//
				(isFolder ? Files.createDirectory(path) : Files.createFile(path)) ://
				(Files.exists(path) ? path : (isFolder ? Files.createDirectory(path) : Files.createFile(path)));
	}

//	@SneakyThrows
//	public Path doOperationError(Path tmpFile, Throwable ex) {
//		throw new FIllegalStateException(ex, "TmpFileOperation 'file://%s' throw error", tmpFile);
//	}

	@SneakyThrows
	public void doOperationAfter(Path tmpFile) {
		if (skipRemove) {
			return;
		}
		if (isFolder) {
			UFS.RM.deleteDir(tmpFile);
		} else {
			Files.delete(tmpFile);
		}
	}

	protected boolean skipRemove = false;

	public TmpFileOperation skipRemove(boolean... skipRemove) {
		this.skipRemove = ARG.isDefNotEqFalse(skipRemove);
		return this;
	}
}
