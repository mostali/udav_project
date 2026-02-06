package mpe.db;

import lombok.Getter;
import lombok.SneakyThrows;
import mpc.arr.STREAM;
import mpc.fs.UFS;
import mpc.log.L;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.IT;
import mpc.exception.NI;
import mpc.types.abstype.AbsType;
import mpc.str.sym.SYMJ;
import mpc.types.abstype.tbl.AbsTypeRow;
import mpc.types.abstype.tbl.AbsTypeTbl;
import mpc.exception.WhatIsTypeException;
import mpe.sql.SqlQueryBuilder;
import mpu.X;
import mpu.func.*;
import mpv.sql_morpheus.SQLPlatform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Db implements AutoCloseable {

//	public static final Logger L = LoggerFactory.getLogger(Db.class);

	public static final Function2T<Path, String, List<List<Object>>, IOException> slLoader = (pathDb, tablename) -> Db.of(pathDb).getAllTableRows(tablename, true, null);
	public static final FunctionV3T<Path, String, List<List<Object>>, SQLException> slWriter = (pathDb, tablename, rows) -> Db.of(pathDb).writeToDb(tablename, rows, CleanOpt.TRUNCATE);

	public static final Function2T<List<String>, String, List<List<Object>>, IOException> pgLoader = (ulp, tablename) -> Db.of(ulp).getAllTableRows(tablename, true, null);
	public static final FunctionV3T<List<String>, String, List<List<Object>>, SQLException> pgWriter = (ulp, tablename, rows) -> Db.of(ulp).writeToDb(tablename, rows, CleanOpt.TRUNCATE);


	public final IDbUrl jdbcUrl;

	@Override
	public String toString() {
		return jdbcUrl.toString();
	}

	private final @Getter SQLPlatform sqlPlatform;

	@Deprecated//TODO remove sqlite from constuctor
	public Db(IDbUrl jdbcUrl, boolean... checkExistDb) {
		this(jdbcUrl, jdbcUrl.getSqlPlatform(), checkExistDb);
	}

	public Db(IDbUrl jdbcUrl, SQLPlatform sqlPlatform, boolean... checkExistDb) {
		this.jdbcUrl = jdbcUrl;
		this.sqlPlatform = sqlPlatform;
		if (jdbcUrl.getSqlPlatform() == SQLPlatform.SQLITE && ARG.isDefEqTrue(checkExistDb)) {
			JdbcUrl jdbcUrl0 = getSqliteJdbcUrl(jdbcUrl);
			JdbcUrl.isDbExistAndNotBlank(jdbcUrl0.toPath());
		}
	}

	public static JdbcUrl getSqliteJdbcUrl(IDbUrl jdbcUrl) {
		IT.state(jdbcUrl.getSqlPlatform() == SQLPlatform.SQLITE, "call method support only sqlite");
		return JdbcUrl.of(jdbcUrl);
	}

	public static List<String> getAllTableNames(JdbcUrl dbUrl, SQLPlatform sqlPlatform) throws SQLException {
		switch (sqlPlatform) {
			case SQLITE:
				return queryList_(dbUrl, "SELECT name FROM sqlite_master WHERE type = 'table'").stream().map(t -> t.get(0).getValue()).filter(X::notNull).map(String::valueOf).collect(Collectors.toList());
			default:
				throw new NI(sqlPlatform);
		}
	}

	public static AbsTypeTbl getAllTableColumns(JdbcUrl dbUrl, String tablename, SQLPlatform sqlPlatform) throws SQLException {
		switch (sqlPlatform) {
			case SQLITE:
				List<List<AbsType>> query = queryList_(dbUrl, "PRAGMA table_info('%s')", tablename);
				//		return query.stream().map(t -> new AbsType((String) t.get(1).getValue(), t.get(1).getValue(), null)).collect(Collectors.toList());
				List<AbsTypeRow> rows = query.stream().map(AbsTypeRow::of).collect(Collectors.toList());
				return AbsTypeTbl.of(rows, tablename);
			default:
				throw new NI(sqlPlatform);
		}
	}

	public static List<String> getAllTableColumnNames(JdbcUrl dbUrl, String tablename, SQLPlatform sqlPlatform) throws SQLException {
		switch (sqlPlatform) {
			case SQLITE:
				List<List<AbsType>> query = queryList_(dbUrl, "PRAGMA table_info('%s')", tablename);
				return query.stream().filter(X::notEmpty).map(r -> r.get(1).getValue()).filter(X::notNull).map(String::valueOf).collect(Collectors.toList());
			default:
				//return query(dbUrl, "SELECT sql FROM sqlite_master WHERE tbl_name = '%s' AND type = 'table'", tablename).stream().map(t -> t.get(0).getValue()).filter(X::notNull).map(String::valueOf).collect(Collectors.toList());
				throw new NI(sqlPlatform);
		}
	}

	public static List<Map<String, AbsType>> getAllTableRowsAsMap(JdbcUrl dbUrl, String tablename, Object ifValIsNull) throws SQLException {
		return queryMap_(dbUrl, "select * from %s", tablename);

	}

	public static List<List<Object>> getAllTableRows(IDbUrl dbUrl, String tablename, Object ifValIsNull, boolean... includeHead) throws SQLException {
		List<List<AbsType>> query = queryList_(dbUrl, "select * from %s", tablename);
		if (query.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		Function<List<AbsType>, List<Object>> func = row -> row.stream().map(t -> t.getValueOr(ifValIsNull)).collect(Collectors.toList());
		List rows = query.stream().map(func).collect(Collectors.toList());
		if (ARG.isDefNotEqTrue(includeHead)) {
			return rows;
		}
		List head = ARRi.first(query).stream().map(t -> t.name()).collect(Collectors.toList());
		rows.add(0, head);
		return rows;
	}

	public static Db of(Path fileDb) {
		JdbcUrl dbUrl = JdbcUrl.of(fileDb);
		return new Db(dbUrl, SQLPlatform.SQLITE);
	}

	public static Db of(List<String> ulp) {
		return of(IDbUrl.ofULP(ulp));
	}

	public static Db of(IDbUrl dbUrl) {
		return new Db(dbUrl, dbUrl.getSqlPlatform());
	}

	@Deprecated
	public Db writeToDb0(String tableName, List<List<Object>> rowsWithHeader, boolean... rmmDbBeforeWrite) throws SQLException {
		return writeToDb(tableName, rowsWithHeader, ARG.isDefEqTrue(rmmDbBeforeWrite) ? CleanOpt.REMOVE_DB : CleanOpt.SKIP);
	}

	public Db writeToDb(String tableName, List<List<Object>> rowsWithHeader, CleanOpt cleanOpt) throws SQLException {
		return writeToDb(this, tableName, rowsWithHeader, cleanOpt);
	}

//	public static Db writeToDb(Db db, String tableName, List<List<Object>> rowsWithHeader, boolean... rmmDbBeforeWrite) throws SQLException {
//		return writeToDb(db, tableName, rowsWithHeader, ARG.isDefEqTrue(rmmDbBeforeWrite) ? CleanOpt.REMOVE_DB : CleanOpt.SKIP);
//	}

	public enum CleanOpt {
		SKIP, TRUNCATE, DROP_TABLE, REMOVE_DB;

		public static void apply(Db db, String tableName, CleanOpt cleanOpt) {
			switch (cleanOpt) {
				case TRUNCATE:
					db.truncateTable(tableName);
					break;
				case REMOVE_DB:
					db.removeDbTotalQk();//TODO? why qk
					break;
				case DROP_TABLE:
					db.dropTable(tableName);
					break;
				case SKIP:
					//ok
					break;
				default:
					throw new WhatIsTypeException(cleanOpt);
			}
		}
	}

	public static Db writeToDb(Db db, String tableName, List<List<Object>> rowsWithHeader, CleanOpt cleanOpt) throws SQLException {

		CleanOpt.apply(db, tableName, cleanOpt);

		rowsWithHeader = new ArrayList(rowsWithHeader);//wrap to array

		ARR.removeEmptyRows(rowsWithHeader);
		ARR.trimListSizeByHead(rowsWithHeader);

		List<Object> head = ARR.cutHeadRow(rowsWithHeader);


//		List<String> decl = head.stream().map(col -> SqlQueryBuilder.SqlType.TEXT.col(String.valueOf(col))).collect(Collectors.toList());
		List<String> decl = STREAM.mapToList(head, col -> SqlQueryBuilder.SqlType.TEXT.col(String.valueOf(col), db.getSqlPlatform()));

		String createTableSql = SqlQueryBuilder.CREATE_TABLE.createTable(IT.notEmpty(tableName), true, decl);

		execute_(db.jdbcUrl, createTableSql);
		for (List<Object> row : rowsWithHeader) {
			String createInsertSql = SqlQueryBuilder.INSERT_INTO_TABLE.insertManyValuesToTable(IT.notEmpty(tableName), Arrays.asList(row), true);
			execute_(db.jdbcUrl, createInsertSql);
			L.info("InsertOneToDb {}", db);
		}
//		L.info("WriteToDb {}{}", SYMJ.FILE_DB, UF.ln(db.getSqliteJdbcUrl().toPath()));
		L.info("WriteToDb {}", db);
		return db;
	}

	private void removeTable(String tableName) {

	}

	public void close() throws SQLException {
		if (_conn != null) {
			_conn.close();
			_conn = null;
		}
	}

	public static void writeTable(Db db, String tableName, List<String> colNames, List<List> rows) throws IOException, SQLException {
		if (!rows.isEmpty()) {
			IT.isEq(colNames.size(), rows.get(0).size());
		}
		db.createParentDirIfNotExist();
		List<String> decl = colNames.stream().map(col -> {
			return SqlQueryBuilder.SqlType.TEXT.col(String.valueOf(col));
		}).collect(Collectors.toList());

		String createTableSql = SqlQueryBuilder.CREATE_TABLE.createTable(IT.notEmpty(tableName), false, decl);
		Db.execute_(db.jdbcUrl, createTableSql);
		for (List<Object> row : rows) {
			String createInsertSql = SqlQueryBuilder.INSERT_INTO_TABLE.insertManyValuesToTable(IT.notEmpty(tableName), Arrays.asList(row));
			Db.execute_(db.jdbcUrl, createInsertSql);
		}
	}

	public void createParentDirIfNotExist() throws IOException {
		UFS.MKDIR.createDirs_(Paths.get(getSqliteJdbcUrl(jdbcUrl).toPathString()).getParent().toFile(), true, false);
	}

	@SneakyThrows
	public Db createTable(String tablename, String... colDeclarations) {
		try (Statement st = conn().createStatement()) {
			String sql = SqlQueryBuilder.CREATE_TABLE.createTable(tablename, true, colDeclarations);
			if (L.isDebugEnabled()) {
				L.debug(SYMJ.FILE_DB_COLOR + "createTable with sql\n" + sql);
			}
			conn().createStatement().execute(sql);
			return this;
		}
	}


	public boolean execute_(String sql) throws SQLException {
		try (Statement st = conn().createStatement()) {
			if (L.isDebugEnabled()) {
				L.debug(SYMJ.FILE_DB_COLOR + "Create&Execute sql\n" + sql);
			}
			boolean execute = st.execute(sql);
			return execute;
		}
	}

	public List<?> query(String sql, boolean asMap) throws SQLException {
		return query(sql, asMap ? QMapResult.MAP_ABS_TYPE : QMapResult.LIST_ABSTYPE);
	}

	@SneakyThrows
	public boolean dropTable(String tablename) {
		String sql = SqlQueryBuilder.DropTableQuery.dropTable(true, IT.NB(tablename));
		return execute_(sql);
	}

	@SneakyThrows
	public boolean truncateTable(String tablename) {
		String sql;
		switch (getSqlPlatform()) {
			case POSTGRE:
				sql = SqlQueryBuilder.TruncateTableQuery.truncateTable(IT.NB(tablename));
				break;
			case SQLITE:
				sql = SqlQueryBuilder.TruncateTableQuery.truncateTable_SQLITE(IT.NB(tablename), false);
				break;
			default:
				throw new WhatIsTypeException(getSqlPlatform());

		}
		return execute_(sql);
	}

	public enum QMapResult {
		LIST_ABSTYPE, MAP_ABS_TYPE, MAP_OBJS, LIST_OBJS
	}

	@SneakyThrows
	public List<?> getTable(String tablename, QMapResult qm) {
		return query("select * from " + IT.notEmpty(tablename, "set table"), qm);
	}

	public List<?> query(String sql, QMapResult qm) throws SQLException {
		if (L.isDebugEnabled()) {
			L.debug(SYMJ.FILE_DB_COLOR + "Create&Execute sql\n" + sql);
		}
		try (Statement st = conn().createStatement()) {
			List<?> lists = null;
			try (ResultSet rs = st.executeQuery(sql)) {
				switch (qm) {
					case MAP_ABS_TYPE:
						lists = DbRsTo.toMapAsMap(rs);
						break;
					case LIST_ABSTYPE:
						lists = DbRsTo.toMapAsList(rs);
						break;
					case MAP_OBJS:
						lists = DbRsTo.toMapSimpleMap(rs);
						break;
					case LIST_OBJS:
						List<Map<String, ?>> query = (List<Map<String, ?>>) query(sql, QMapResult.MAP_OBJS);
						List header = ARR.newAL(ARRi.first(query).keySet());
						List<List<?>> rows = new ArrayList<>();
						rows.add(header);
						for (Map<String, ?> row : query) {
							ArrayList rowList = new ArrayList(row.values());
							rows.add(rowList);
						}
						lists = rows;
						break;
					default:
						throw new WhatIsTypeException(qm);
				}
				return lists;
			} finally {
				if (L.isDebugEnabled()) {
//					L.debug(SYMJ.FILE_DB2 + SYMJ.FILE_DB2 + "Create&Execute map({}):\n{}", X.sizeOf(lists), lists);
					L.debug(SYMJ.FILE_DB_COLOR + SYMJ.FILE_DB_COLOR + "Create&Execute map({})", X.sizeOf(lists));
				}
			}
		}
	}

	public List<List<AbsType>> selectAll(String tablename) throws SQLException {
		return (List<List<AbsType>>) query("select * from " + tablename, false);
	}

	@Deprecated
	public static boolean execute_(String dbName, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		return execute_(JdbcUrl.of(dbName, JdbcUrl.NameDbFileType.NAME), sqlf);
	}


	@SneakyThrows
	public static boolean execute(IDbUrl jdbcUrl, String sql, Object... args) {
		return execute_(jdbcUrl, sql, args);
	}

	public static boolean execute_(IDbUrl jdbcUrl, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		if (L.isInfoEnabled()) {
			if (L.isDebugEnabled()) {
				L.debug("Db:" + jdbcUrl);
			}
			L.info("Sql:" + sqlf);
		}
		try (Db db = new Db(jdbcUrl)) {
			return db.execute_(sqlf);
		}
	}

	@Deprecated
	public static List<List<AbsType>> queryList_(String dbName, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		return queryList_(JdbcUrl.of(dbName, JdbcUrl.NameDbFileType.NAME), sqlf);
	}


	@SneakyThrows
	public static AbsType queryFF_orNull(JdbcUrl jdbcUrl, String sql, Object... args) {
		return ARRi.firstFirst(queryList_(jdbcUrl, sql, args), null);
	}

	@SneakyThrows
	public static AbsType queryFF(JdbcUrl jdbcUrl, String sql, Object... args) {
		return ARRi.firstFirst(queryList_(jdbcUrl, sql, args));
	}

	public static List<List<AbsType>> queryList_(IDbUrl jdbcUrl, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		try (Db db = new Db(jdbcUrl, true)) {
			return (List<List<AbsType>>) db.query(sqlf, false);
		}
	}

	public static List<Map<String, AbsType>> queryMap_(IDbUrl jdbcUrl, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		try (Db db = new Db(jdbcUrl, true)) {
			return (List<Map<String, AbsType>>) db.query(sqlf, true);
		}
	}

	private Connection _conn;

	private Connection conn() throws SQLException {
		if (_conn != null) {
			return _conn;
		}
		try {
			switch (sqlPlatform) {
				case SQLITE:
					Class.forName("org.sqlite.JDBC");
					break;
				case POSTGRE:
					Class.forName("org.postgresql.Driver");
					break;
				default:
					throw new WhatIsTypeException(sqlPlatform);
			}
			String[] lp;
			switch (jdbcUrl.getSqlPlatform()) {
				case SQLITE:
					lp = jdbcUrl.toLoginPass(null);
					if (lp == null) {
						_conn = DriverManager.getConnection(jdbcUrl.getDbUrlString());
					} else {
						_conn = DriverManager.getConnection(jdbcUrl.getDbUrlString(), lp[0], lp[1]);
					}
					break;
				default:
					lp = jdbcUrl.toLoginPass();
					_conn = DriverManager.getConnection(jdbcUrl.getDbUrlString(), lp[0], lp[1]);
					break;

			}
			return _conn;
		} catch (ClassNotFoundException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	public boolean isFileDbWithContent() {
		return getSqliteJdbcUrl(jdbcUrl).isFileDbWithContent();
	}

	public void removeDbTotalQk() {
		try {
			removeDbTotal();
			if (getSqlPlatform() == SQLPlatform.SQLITE) {
				IT.isFileNotExist(getSqliteJdbcUrl(jdbcUrl).toPath().toString());
			}
		} catch (IOException ex) {
			if (L.isWarnEnabled()) {
				L.warn("removeDbQk", ex.getMessage());
			}
		}
	}

	public void removeDbTotal() throws IOException {
		switch (getSqlPlatform()) {
			case SQLITE:
				Files.delete(getSqliteJdbcUrl(jdbcUrl).toPath());
				break;
			case POSTGRE:
				JDBC.dropDB_ALL(jdbcUrl.toULP());
				break;
			default:
				throw new NI(getSqlPlatform());
		}
	}

	public JdbcUrl getSqliteJdbcUrl() {
		return getSqliteJdbcUrl(jdbcUrl);
	}

	@SneakyThrows
	public boolean existDbSqlite() {
		Path path = ((JdbcUrl) jdbcUrl).toPath();
		switch (getSqlPlatform()) {
			case SQLITE:
//				return !UFS.isFileWithContent(path);
				if (!UFS.isFileWithContent(path)) {
					return false;
				}
				break;
		}
		List<String> allTableNames = getAllSqliteTableNames(path);
		return X.notEmpty(allTableNames);
	}

	@SneakyThrows
	public List<List<Object>> getAllTableRows(String tablename, boolean includeHeadRow, String isColValueIsNullThat) {
		List<List<Object>> rows = getAllTableRows(jdbcUrl, tablename, null, includeHeadRow);
		if (isColValueIsNullThat != null) {
			rows = STREAM.mapToList(rows, yRow -> STREAM.mapToList(yRow, xCol -> X.emptyObj_Str(xCol) ? isColValueIsNullThat : xCol));
		}
		return rows;
	}

	@SneakyThrows
	public static List<String> getAllSqliteTableNames(Path dbFile) {
		return Db.getAllTableNames(JdbcUrl.of(dbFile), SQLPlatform.SQLITE);
	}


	@SneakyThrows
	public List<String> getAllTableNames() {
		switch (sqlPlatform) {
			case SQLITE:
				return getAllSqliteTableNames(getSqliteJdbcUrl().toPath());
			case POSTGRE:
				String all = "SELECT table_name\n" + "  FROM information_schema.tables\n" + " WHERE table_schema='public'\n" + "   AND table_type='BASE TABLE';";
				List<Map<String, ?>> objects = (List<Map<String, ?>>) query(all, QMapResult.MAP_OBJS);
				return STREAM.mapToList(objects, m -> X.toStringRq(m.get("table_name")));

			default:
				throw new WhatIsTypeException(sqlPlatform);
		}

	}


}