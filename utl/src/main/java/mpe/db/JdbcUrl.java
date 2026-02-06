package mpe.db;

import mpu.core.ARG;
import mpc.fs.Ns;
import mpc.exception.RequiredRuntimeException;
import mpc.str.sym.SYM;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpv.sql_morpheus.SQLPlatform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JdbcUrl implements IDbUrl {

	public final static String DEL_NAME_DOLLAR = SYM.DOLLAR;
	public final static String PREFIX_DB = "db" + DEL_NAME_DOLLAR;
	public final static String EXT_DB = "sqlite";
	public final static String EXT_DB_FULL = "." + EXT_DB;
	public final String PART_JDBC_URL = "jdbc:sqlite:";

	public final Ns ns;
	public final String name;
	public final NameDbFileType typeName;

	public static String buildDbFileName(String dbName) {
		return PREFIX_DB + dbName + EXT_DB_FULL;
	}

	public static String extractDbName(String dbName) {
		dbName = dbName.substring(PREFIX_DB.length());
		dbName = dbName.substring(0, dbName.length() - EXT_DB_FULL.length());
		return dbName;
	}

	public static JdbcUrl of(Path dbFile, boolean... checkIsNotBlank) {
		if (ARG.isDefEqTrue(checkIsNotBlank)) {
			JdbcUrl.isDbExistAndNotBlank(dbFile);
		}
		String filename = dbFile.getFileName().toString();
		Path rootDir = dbFile.getParent();
		rootDir = rootDir == null ? Paths.get(".") : rootDir;
		return new JdbcUrl(Ns.ofUnsafe(rootDir), filename, NameDbFileType.FILE);
	}

	public static JdbcUrl of(String name, NameDbFileType typeName) {
		return of((Ns) null, name, typeName);
	}

	public static JdbcUrl of(String namespace, String name, NameDbFileType typeName) {
		return new JdbcUrl(Ns.of(namespace), name, typeName);
	}

	public static JdbcUrl of(String rootDir, String namespace, String name, NameDbFileType typeName) {
		return new JdbcUrl(Ns.of(rootDir, namespace), name, typeName);
	}

	public static JdbcUrl of(Ns ns, String name, NameDbFileType typeName) {
		return new JdbcUrl(ns, name, typeName);
	}

	public static boolean isDbExistAndNotBlank(Path dbFile, boolean... RETURN) {
		if (UFS.isFileWithContent(dbFile)) {
			return true;
		} else if (ARG.isDefEqTrue(RETURN)) {
			return false;
		} else if (UFS.existFile(dbFile)) {
			throw new RequiredRuntimeException("Db file '%s' exist, but has zero size", dbFile);
		} else if (!Files.isRegularFile(dbFile)) {
			throw new RequiredRuntimeException("Db file '%s' - is NOT file ", dbFile);
		} else {
			throw new RequiredRuntimeException("Db file '%s' - not exist ", dbFile);
		}
	}

	public static JdbcUrl of(IDbUrl jdbcUrl) {
		return jdbcUrl instanceof JdbcUrl ? (JdbcUrl) jdbcUrl : JdbcUrl.of(jdbcUrl.toPath(null));
	}

	@Override
	public String toString() {
		return "JdbcUrl{" +
				"db=" + getDbUrlString() +
				",file://" + toPath() +
//			   ", ns=" + ns +
//			   ", name='" + name + '\'' +
//			   ", typeName=" + typeName +
				'}';
	}

	public boolean isFileDbWithContent() {
		return UFS.isFileWithContent(toPath());
	}

	public Ns getNs() {
		return ns;
	}

	public SQLPlatform getSqlPlatform() {
		return SQLPlatform.getPlatform(getDbUrlString());
	}

	public enum NameDbFileType {
		FILE, NAME
	}

	public JdbcUrl(Ns ns, String name, NameDbFileType typeName) {
		this.ns = ns;
		this.name = name;
		this.typeName = typeName;
	}

	public String getDbUrlString() {
		return PART_JDBC_URL + toPathString();
	}

	public Path toPath() {
		return Paths.get(toPathString());
	}

	public String toPathString() {
		final String child;
		switch (typeName) {
			case NAME:
				String dbFileName = buildDbFileName(name);
				child = ns == null ? dbFileName : ns.getPathStr(dbFileName);
				break;
			case FILE:
				child = ns == null ? name : ns.getPathStr(name);
				break;
			default:
				throw new WhatIsTypeException(typeName);
		}
		return child;
	}

}
