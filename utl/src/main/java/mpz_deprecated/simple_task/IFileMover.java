package mpz_deprecated.simple_task;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Deprecated
public interface IFileMover {

	default void moveFile(String srcFile, String destFile) throws IOException {
		FileUtils.moveFile(new File(srcFile), new File(destFile));
	}

	default void copyFile(String srcFile, String destFile) throws IOException {
		FileUtils.copyFile(new File(srcFile), new File(destFile));
	}
}
