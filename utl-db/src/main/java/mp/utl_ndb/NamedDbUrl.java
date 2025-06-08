package mp.utl_ndb;

import mpc.fs.UF;
import mpe.db.JdbcUrl;

import java.nio.file.Path;


//actual way
public class NamedDbUrl extends SqlDbUrl {

	//	private final String rootDir, parentDir;
	private final String fileName;

	private final boolean isFileOrDbName;

	public NamedDbUrl(String rootDir, String parentDir, String name) {
		this(rootDir, parentDir, name, false);
	}

	public NamedDbUrl(String rootDir, String parentDir, String fileOrName, boolean isFileOrDbName) {
		super(createDbFileName(rootDir = UF.normDir(rootDir), parentDir = UF.normDir(parentDir), fileOrName = UF.normFile(isFileOrDbName ? fileOrName : JdbcUrl.buildDbFileName(fileOrName))));
//		this.rootDir = rootDir;
//		this.parentDir = parentDir;
		this.fileName = fileOrName;
		this.isFileOrDbName = isFileOrDbName;
	}

	private static String createDbFileName(String rootDir, String parentDir, String key) {
		return rootDir + parentDir + key;
	}

	public NamedDbUrl(Path path) {
		super(path);
//		this.rootDir = null;
//		this.parentDir = path.getParent().toAbsolutePath().toString();
		this.fileName = path.getFileName().toString();
		this.isFileOrDbName = true;
	}

	public String getDbName() {
		return this.isFileOrDbName ? this.fileName : JdbcUrl.extractDbName(this.fileName);
	}
}
