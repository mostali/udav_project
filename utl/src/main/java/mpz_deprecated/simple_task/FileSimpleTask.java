package mpz_deprecated.simple_task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Deprecated
public abstract class FileSimpleTask<TYPE extends AbsTask, CONTEXT, ERROR extends Exception> extends AbsTask<TYPE, CONTEXT, ERROR> {

	protected FileSimpleTask(String typeTT, String typeName) {
		super(typeTT, typeName);
	}

	public static boolean notEmpty(Path... path) throws IOException {
		return !empty(path);
	}

	public static boolean empty(Path... path) throws IOException {
		for (Path p : path) {
			if (!empty(p)) {
				return false;
			}
		}
		return true;
	}

	public static boolean empty(Path path) throws IOException {
		if (!exist(path)) {
			return true;
		} else {
			if (Files.isDirectory(path) && path.toFile().listFiles().length == 0) {
				return true;
			} else if (Files.isRegularFile(path) && Files.size(path) == 0L) {//
				return true;
			}
		}
		return false;
	}

	public static boolean notExist(Path... path) {
		return !exist(path);
	}

	public static boolean exist(Path... path) {
		for (Path p : path) {
			if (!Files.exists(p)) {
				return false;
			}
		}
		return true;
	}


}
