package mp.utl_ndb;

import lombok.SneakyThrows;
import mp.utl_odb.typedb.TypeDb;
import mpe.db.IJdbcUrl;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.IT;
import mpc.exception.NI;
import mpc.types.abstype.AbsType;
import mpc.fs.UFS_BASE;
import mpc.str.sym.SYMJ;
import mpc.types.abstype.tbl.AbsTypeRow;
import mpc.types.abstype.tbl.AbsTypeTbl;
import mpc.exception.FIllegalStateException;
import mpc.exception.WhatIsTypeException;
import mpe.sql.SqlQueryBuilder;
import mpu.X;
import mpv.sql_morpheus.SQLPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Db implements AutoCloseable {

//	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//
//		{
//			AbsTypeTbl tbl = Db.getAllTableColumns(of("test.sqlite").jdbcUrl, "customer", SQLPlatform.SQLITE);
//			//			P.exit(tbl);
//			//			P.exit(tbl.getColumnValue("type",true));
//			//			P.exit(tbl.getColumnNames());
//			//			P.exit(tbl.getColumnSqlTypeNames());
//			P.exit(tbl.getColumnSqlTypes());
//		}
//
//		{
//			//			List<List<Object>> data = Db.getAllTableRows(of("test.sqlite").jdbcUrl, "customer", null);
//			//			P.exit(data);
//		}
//
//		{
//			//			List<String> head = Db.getAllTableColumnNames(of("test.sqlite").jdbcUrl, "customer");
//			//			P.exit(X.sizeOf(head));
//			//			P.exit(head);
//		}
//
//		{
//			P.exit(Db.query_("test", "select * from test where ci='2'"));
//		}
////		U.exit(Namespace.);
//		Db db = new Db("tt");
	/// /		db.createTable("test", SqlQueryBuilder.SqlType.TEXT.col("ct"), SqlQueryBuilder.SqlType.LONG.col("ci"));
	/// /		U.exit(db.selectAll("test"));
//		U.exit(db.query_("select * from test where ci='2'"));
//
//		Ns ns = Ns.of("test/testdb");
//		JdbcUrl jdbc = new JdbcUrl(ns, "test", JdbcUrl.TypeName.NAME);
//
//		String sql = SqlQueryBuilder.CREATE_TABLE.createTable("test", true, SqlQueryBuilder.SqlType.TEXT.col("test"));
//		U.exit();
//	}


	public static final Logger L = LoggerFactory.getLogger(Db.class);

	public final IJdbcUrl jdbcUrl;

	private final SQLPlatform sqlPlatform;

//	@Deprecated//TODO remove sqlite from constuctor
//	public Db(String name) {
//		this(JdbcUrl.of(name, JdbcUrl.TypeName.NAME), SQLPlatform.SQLITE);
//	}

	@Deprecated//TODO remove sqlite from constuctor
	public Db(IJdbcUrl jdbcUrl, boolean... checkExistDb) {
		this(jdbcUrl, jdbcUrl.getSqlPlatform(), checkExistDb);
	}

	public Db(IJdbcUrl jdbcUrl, SQLPlatform sqlPlatform, boolean... checkExistDb) {
		this.jdbcUrl = jdbcUrl;
		this.sqlPlatform = sqlPlatform;
		if (jdbcUrl.getSqlPlatform() == SQLPlatform.SQLITE && ARG.isDefEqTrue(checkExistDb)) {
			JdbcUrl jdbcUrl0 = getSqliteJdbcUrl(jdbcUrl);
			JdbcUrl.isDbExistAndNotBlank(jdbcUrl0.toPath());
		}
	}

	public static JdbcUrl getSqliteJdbcUrl(IJdbcUrl jdbcUrl) {
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

	public static List<List<Object>> getAllTableRows(JdbcUrl dbUrl, String tablename, Object ifValIsNull, boolean... includeHead) throws SQLException {
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

	public static Db of(String fileDb) {
		return of(Paths.get(fileDb));
	}

	public static Db of(Path fileDb) {
		JdbcUrl dbUrl = JdbcUrl.of(fileDb);
		return new Db(dbUrl, SQLPlatform.SQLITE);
	}

	public static Db writeToDb(List<List<Object>> rows, Db db, String tableName, boolean... rmmDbBeforeWrite) throws SQLException {

		if (ARG.isDefEqTrue(rmmDbBeforeWrite)) {
			db.removeDbQk();
		}

		rows = new ArrayList(rows);//wrap to array

		ARR.removeEmptyRows(rows);
		ARR.trimListSizeByHead(rows);

		List<Object> head = ARR.cutHeadRow(rows);
		List<String> decl = head.stream().map(col -> {
			return SqlQueryBuilder.SqlType.TEXT.col(String.valueOf(col));
		}).collect(Collectors.toList());

		String createTableSql = SqlQueryBuilder.CREATE_TABLE.createTable(IT.notEmpty(tableName), true, decl);
		execute_(db.jdbcUrl, createTableSql);
		for (List<Object> row : rows) {
			String createInsertSql = SqlQueryBuilder.INSERT_INTO_TABLE.insertManyValuesToTable(IT.notEmpty(tableName), Arrays.asList(row), true);
			execute_(db.jdbcUrl, createInsertSql);
		}
		return db;
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
		UFS_BASE.MKDIR.createDirs_(Paths.get(getSqliteJdbcUrl(jdbcUrl).toPathString()).getParent().toFile(), true, false);
	}

	private Db createTable(String tablename, String... colDecl) {
		try {
			String sql = SqlQueryBuilder.CREATE_TABLE.createTable(tablename, true, colDecl);
			conn().createStatement().execute(sql);
			return this;
		} catch (SQLException e) {
			throw new FIllegalStateException(e, "createTable %s", tablename);
		}
	}


	public boolean execute_(String sql) throws SQLException {
		try (Statement st = conn().createStatement()) {
			if (L.isDebugEnabled()) {
				L.debug(SYMJ.FILE_DB2 + "Create&Execute sql\n" + sql);
			}
			boolean execute = st.execute(sql);
			return execute;
		}
	}

	public List<?> queryAs_(String sql, boolean asMap) throws SQLException {
		if (L.isDebugEnabled()) {
			L.debug(SYMJ.FILE_DB2 + "Create&Execute sql\n" + sql);
		}
		try (Statement st = conn().createStatement()) {
			List<?> lists = null;
			try (ResultSet rs = st.executeQuery(sql)) {
				lists = asMap ? toMapAsMap(rs) : toMapAsList(rs);
				return lists;
			} finally {
				if (L.isDebugEnabled()) {
//					L.debug(SYMJ.FILE_DB2 + SYMJ.FILE_DB2 + "Create&Execute map({}):\n{}", X.sizeOf(lists), lists);
					L.debug(SYMJ.FILE_DB2 + SYMJ.FILE_DB2 + "Create&Execute map({})", X.sizeOf(lists));
				}
			}
		}
	}

	public List<List<AbsType>> selectAll(String tablename) throws SQLException {
		return (List<List<AbsType>>) queryAs_("select * from " + tablename, false);
	}

	public static List<List<AbsType>> toMapAsList(ResultSet rs) throws SQLException {
		return toMap0(rs, true);
	}

	public static List<Map<String, AbsType>> toMapAsMap(ResultSet rs) throws SQLException {
		return toMap0(rs, false);
	}

	public static List toMap0(ResultSet rs, boolean asList_orMap) throws SQLException {
		List rows = new ArrayList<>();
		while (rs.next()) {
			rows.add(toMapSingleResult(rs, asList_orMap));
		}
		return rows;
	}

	public static List<AbsType> toMapSingleResultAsList(ResultSet rs) throws SQLException {
		return (List<AbsType>) toMapSingleResult(rs, true);
	}

	public static Map<String, ?> toMapSingleResultAsSimpleMap(ResultSet rs) throws SQLException {
		return AbsType.asMapWithObject(toMapSingleResultAsList(rs));
	}

	public static Map<String, AbsType> toMapSingleResultAsMap(ResultSet rs) throws SQLException {
		return (Map<String, AbsType>) toMapSingleResult(rs, false);
	}

	private static Object toMapSingleResult(ResultSet rs, boolean asList_orMap) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int cols = metaData.getColumnCount();
		List<AbsType> rowAsList = null;
		Map<String, AbsType> rowAsMap = null;
		if (asList_orMap) {
			rowAsList = new ArrayList<>();
		} else {
			rowAsMap = new LinkedHashMap<>();
		}
		for (int ci = 1; ci <= cols; ci++) {
			String cName = metaData.getColumnName(ci);
			int cType = metaData.getColumnType(ci);
			AbsType val = AbsType.toAbsTypeSql(ci, cName, cType, rs);
			if (asList_orMap) {
				rowAsList.add(val);
			} else {
				rowAsMap.put(cName, val);
			}
		}
		return asList_orMap ? rowAsList : rowAsMap;
	}

	@Deprecated
	public static boolean execute_(String dbName, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		return execute_(JdbcUrl.of(dbName, JdbcUrl.NameDbFileType.NAME), sqlf);
	}

	public static boolean execute_(TypeDb db, String sql, Object... args) throws SQLException {
		return execute_(db.getNamedDbUrl().getJdbcUrlType(), sql, args);
	}

	@SneakyThrows
	public static boolean execute(IJdbcUrl jdbcUrl, String sql, Object... args) {
		return execute_(jdbcUrl, sql, args);
	}

	public static boolean execute_(IJdbcUrl jdbcUrl, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		try (Db db = new Db(jdbcUrl)) {
			return db.execute_(sqlf);
		}
	}

	@Deprecated
	public static List<List<AbsType>> queryList_(String dbName, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		return queryList_(JdbcUrl.of(dbName, JdbcUrl.NameDbFileType.NAME), sqlf);
	}

	public static AbsType queryFF_(TypeDb typeDb, String sql, Object... args) throws SQLException {
		return ARRi.firstFirst(queryList_(typeDb, sql, args));
	}

	@SneakyThrows
	public static AbsType queryFF_orNull(JdbcUrl jdbcUrl, String sql, Object... args) {
		return ARRi.firstFirst(queryList_(jdbcUrl, sql, args), null);
	}

	@SneakyThrows
	public static AbsType queryFF(JdbcUrl jdbcUrl, String sql, Object... args) {
		return ARRi.firstFirst(queryList_(jdbcUrl, sql, args));
	}

	public static AbsType queryFFfm_(TypeDb typeDb, String sql, Object... args) throws SQLException {
		return ARRi.firstFirst(queryList_(typeDb, X.fm(sql, args)));
	}

	public static List<List<AbsType>> queryList_(TypeDb typeDb, String sql, Object... args) throws SQLException {
		return queryList_(typeDb.getNamedDbUrl().getJdbcUrlType(), sql, args);
	}

	public static List<List<AbsType>> queryList_(IJdbcUrl jdbcUrl, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		try (Db db = new Db(jdbcUrl, true)) {
			return (List<List<AbsType>>) db.queryAs_(sqlf, false);
		}
	}

	public static List<Map<String, AbsType>> queryMap_(IJdbcUrl jdbcUrl, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		try (Db db = new Db(jdbcUrl, true)) {
			return (List<Map<String, AbsType>>) db.queryAs_(sqlf, true);
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
						_conn = DriverManager.getConnection(jdbcUrl.getJdbcUrlString());
					} else {
						_conn = DriverManager.getConnection(jdbcUrl.getJdbcUrlString(), lp[0], lp[1]);
					}
					break;
				default:
					lp = jdbcUrl.toLoginPass();
					_conn = DriverManager.getConnection(jdbcUrl.getJdbcUrlString(), lp[0], lp[1]);
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

	public void removeDbQk() {
		try {
			removeDb();
			IT.isFileNotExist(getSqliteJdbcUrl(jdbcUrl).toPath().toString());
		} catch (IOException ex) {
			if (L.isWarnEnabled()) {
				L.warn("removeDbQk", new IllegalStateException(ex));
			}
		}
	}

	public void removeDb() throws IOException {
		Files.delete(getSqliteJdbcUrl(jdbcUrl).toPath());
	}

	@Override
	public String toString() {
		return "Db{" +
				"jdbcUrl=" + jdbcUrl +
				'}';
	}

	public JdbcUrl getSqliteJdbcUrl() {
		return getSqliteJdbcUrl(jdbcUrl);
	}

	@SneakyThrows
	public boolean existDb() {
		return X.notEmpty(getAllTableNames(((JdbcUrl) jdbcUrl).toPath()));
	}

	@SneakyThrows
	public static List<String> getAllTableNames(Path dbFile) {
		return Db.getAllTableNames(JdbcUrl.of(dbFile), SQLPlatform.SQLITE);
	}
}