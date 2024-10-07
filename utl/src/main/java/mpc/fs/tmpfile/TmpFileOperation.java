package mpc.fs.tmpfile;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpc.exception.FIllegalStateException;
import mpc.fs.UFS;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public abstract class TmpFileOperation {

	final boolean isFolder;

	public TmpFileOperation() {
		this(false);
	}

	protected TmpFileOperation(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public void doOperation() {
		Path file = doOperationBefore();
		try {
			doOperationImpl(file);
		} catch (Throwable ex) {
			doOperationError(file, ex);
		} finally {
			doOperationAfter(file);
		}
	}

	public abstract void doOperationImpl(Path tmpFile);

	@SneakyThrows
	public Path doOperationBefore() {
		Path path = Paths.get("/tmp/TmpFileOperation." + (skipRemove ? "SKIP_REMOVE" : UUID.randomUUID()));
		return !skipRemove ?//
				(isFolder ? Files.createDirectory(path) : Files.createFile(path)) ://
				(Files.exists(path) ? path : (isFolder ? Files.createDirectory(path) : Files.createFile(path)));
	}

	@SneakyThrows
	public Path doOperationError(Path tmpFile, Throwable ex) {
		throw new FIllegalStateException(ex, "TmpFileOperation 'file://%s' throw error", tmpFile);
	}

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
