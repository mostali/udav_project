package mp.utl_ndb;

import lombok.SneakyThrows;
import mp.utl_odb.DBU;
import mp.utl_odb.DbEE;
import mp.utl_odb.mdl.AModel;
import mpc.exception.FIllegalArgumentException;
import mpc.fs.UFS;
import mpe.db.JdbcUrl;
import mpu.str.STR;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//Old way
public class SqlDbUrl {

	public static final Logger L = LoggerFactory.getLogger(SqlDbUrl.class);

	private static final String prefixBase = "jdbc:sqlite:";

	private final String dbFileName;

	public SqlDbUrl(Path dbFilePath) {
		this(dbFilePath.toAbsolutePath().toString());
	}

	public SqlDbUrl(String dbFilePath) {
		this.dbFileName = dbFilePath;
	}

	public static SqlDbUrl ofFile(String file) {
		return new SqlDbUrl(file);
	}

	public static String filenameCreate(String dbName) {
		return JdbcUrl.PREFIX_DB + dbName + JdbcUrl.EXT_DB_FULL;
	}

	public static String filenameExtract(String fileWithDbName) {
		return STR.removeStartEndString(fileWithDbName, JdbcUrl.PREFIX_DB.length(), JdbcUrl.EXT_DB_FULL.length());
	}

	public String getDbFile() {
		return dbFileName;
	}

	public Path getDbFilePath() {
		return Paths.get(getDbFile());
	}

	/**
	 * @return only file name (without extenssion) , eg db-group
	 */
	public String getSimpleName() {
		String f = getSimpleFileName();
		return f.substring(0, f.lastIndexOf('.'));
	}

	/**
	 * @return only file name , eg db-group.sqlite
	 */
	public String getSimpleFileName() {
		String dbFile = getDbFile();
		int indS = dbFile.lastIndexOf("/");
		String file = indS == -1 ? dbFile : dbFile.substring(indS + 1, dbFile.length());
		return file;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SqlDbUrl) {
			return equalsDb((SqlDbUrl) obj);
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return getDbFile();
	}

	@Deprecated
	public String getName() {

		String file = getSimpleFileName();

		String n = file.startsWith(JdbcUrl.PREFIX_DB) ? file.substring(3) : file;

		if (n.endsWith(JdbcUrl.EXT_DB_FULL)) {
			n = n.substring(0, n.length() - 7);
		}
		return n;
	}

	public String getJdbcUrl() {
		return getJdbcUrl(getDbFile());
	}

	public JdbcUrl getJdbcUrlType() {
		return JdbcUrl.of(getDbFilePath());
	}

	public static String getJdbcUrl(String fileName) {
		return prefixBase + fileName;
	}

	public void deleteDb() {
		deleteDb(this);
	}

	public static void deleteDb(SqlDbUrl dbUrl) {
		if (FileUtils.deleteQuietly(new File(dbUrl.getDbFile()))) {
			DBU.L.info("DB deleted:" + dbUrl.getDbFile());
		} else {
			DBU.L.error("ERROR DELETE DB:" + dbUrl.getDbFile());
		}
	}

	public void deleteOnly(Class<? extends AModel>... classes) {
		if (!isExistDb()) {
			throw new IllegalStateException("Sql db not found :" + getDbFile());
		}
		for (Class clas : classes) {
			if (DBU.isExistTable(this, clas)) {
				DBU.modifyTableRq(this, clas, DBU.EModifyTable.DROP);
			}
			DBU.modifyTableRq(this, clas, DBU.EModifyTable.CREATE_TABLE);
		}
	}

	public SqlDbUrl createDbIfNotExists() {
		if (isExistDb()) {
			return this;
		}
		if (L.isTraceEnabled()) {
			L.trace("DB createDbIfNotExists [" + this.getDbFile() + "]");
		}

		File newFileDb = new File(this.getDbFile());
		if (newFileDb.isFile()) {
			return this;
		} else if (newFileDb.isDirectory()) {
			throw new FIllegalArgumentException("FileDb '%s' is directory", newFileDb);
		}
		try {
			FileUtils.touch(newFileDb);
			if (L.isTraceEnabled()) {
				L.trace("DB WAS CREATED[" + getDbFile() + "]");
			}
		} catch (IOException e) {
			throw DbEE.EE.CREATE_DB_IF_NOT_EXIST.I(e);
		}

		return this;
	}

	public void checkExistBase() {
		if (!isExistDb()) {
			throw DbEE.EE.DB_NOT_EXIST.I();
		}
	}

	public boolean isExistDb() {
		return isExistDb(this.getDbFile());
	}

	public static boolean isExistDb(String db) {
		return UFS.isFileWithContent(db);
	}

	@SneakyThrows
	public void removeDb() {
		Files.deleteIfExists(Paths.get(getDbFile()));
	}

	public boolean equalsDb(SqlDbUrl url) {
		return Paths.get(getDbFile()).equals(Paths.get(url.getDbFile()));
	}

	public SqlDbUrl createTableIfNotExistsQk(Class<? extends AModel> classModel) {
		DBU.createTableIfNotExistsQk(this, classModel);
		return this;
	}

	public void cleanTableRq(Class<? extends AModel> classModel) {
		if (DBU.isExistTable(this, classModel)) {
			DBU.truncateTable(this, classModel);
		}
	}

	public void dropTableRq(Class<? extends AModel> classModel) {
		if (DBU.isExistTable(this, classModel)) {
			DBU.dropTable(this, classModel);
		}
	}

}
