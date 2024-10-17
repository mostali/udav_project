package zk_old_core.mdl;

import java.nio.file.Path;

public class UndefinedFdModel extends FdModel {

	public UndefinedFdModel(Path file) {
		super(file);
	}

	@Override
	public Path getFileRootProps() {
		return null;
	}

	public static UndefinedFdModel of(Path file) {
		return new UndefinedFdModel(file);
	}

}
