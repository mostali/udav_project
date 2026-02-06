//package mpc.fs.tmpfile;
//
//import lombok.SneakyThrows;
//import mpc.exception.FIllegalStateException;
//import mpu.core.QDate;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//@Deprecated //see new TmpFileOperation
//public abstract class TmpFile {
//
//	public abstract TmpFile handle(Path path);
//
//	public TmpFile doOperation() {
//		try {
//			return handle(createFile());
//		} finally {
//			if (tempFile != null) {
//				UFS_BASE.RM.removeQuicklyFileOrDir(tempFile);
//			}
//		}
//	}
//
//	private Path tempFile = null;
//
//	@SneakyThrows
//	public Path createFile() {
//		if (tempFile != null) {
//			throw new FIllegalStateException("TmpFile already created '%s'", tempFile);
//		}
//		Path tempFile = Files.createTempFile(getClass().getName(), QDate.now().ms() + "");
//		return tempFile;
//	}
//}
