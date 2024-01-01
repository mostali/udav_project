package mp.utl_ndb;

import lombok.SneakyThrows;
import mp.utl_odb.typedb.TypeDb;
import mpc.*;
import mpc.args.ARG;
import mpc.arr.Arr;
import mpc.arr.ArrItem;
import mpc.ERR;
import mpc.exception.NI;
import mpc.types.abstype.AbsType;
import mpc.fs.UFS_BASE;
import mpc.str.sym.SYMJ;
import mpc.types.abstype.tbl.AbsTypeRow;
import mpc.types.abstype.tbl.AbsTypeTbl;
import mpc.exception.FIllegalStateException;
import mpc.exception.WhatIsTypeException;
import mpe.sql.SqlQueryBuilder;
import mpv.sql_morpheus.SQLPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
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
////		db.createTable("test", SqlQueryBuilder.SqlType.TEXT.col("ct"), SqlQueryBuilder.SqlType.LONG.col("ci"));
////		U.exit(db.selectAll("test"));
//		U.exit(db.query_("select * from test where ci='2'"));
//
//		Ns ns = Ns.of("test/testdb");
//		JdbcUrl jdbc = new JdbcUrl(ns, "test", JdbcUrl.TypeName.NAME);
//
//		String sql = SqlQueryBuilder.CREATE_TABLE.createTable("test", true, SqlQueryBuilder.SqlType.TEXT.col("test"));
//		U.exit();
//	}


	public static final Logger L = LoggerFactory.getLogger(Db.class);

	public final JdbcUrl jdbcUrl;
	private final SQLPlatform sqlPlatform;

//	@Deprecated//TODO remove sqlite from constuctor
//	public Db(String name) {
//		this(JdbcUrl.of(name, JdbcUrl.TypeName.NAME), SQLPlatform.SQLITE);
//	}

	@Deprecated//TODO remove sqlite from constuctor
	public Db(JdbcUrl jdbcUrl, boolean... checkExistDb) {
		this(jdbcUrl, jdbcUrl.getSqlPlatform(), checkExistDb);
	}

	public Db(JdbcUrl jdbcUrl, SQLPlatform sqlPlatform, boolean... checkExistDb) {
		this.jdbcUrl = jdbcUrl;
		this.sqlPlatform = sqlPlatform;
		if (ARG.isDefEqTrue(checkExistDb)) {
			JdbcUrl.isDbExistAndNotBlank(jdbcUrl.toPath());
		}
	}

	public static List<String> getAllTableNames(JdbcUrl dbUrl, SQLPlatform sqlPlatform) throws SQLException {
		switch (sqlPlatform) {
			case SQLITE:
				return query_(dbUrl, "SELECT name FROM sqlite_master WHERE type = 'table'").stream().map(t -> t.get(0).getValue()).filter(X::notNull).map(String::valueOf).collect(Collectors.toList());
			default:
				throw new NI(sqlPlatform);
		}
	}

	public static AbsTypeTbl getAllTableColumns(JdbcUrl dbUrl, String tablename, SQLPlatform sqlPlatform) throws SQLException {
		switch (sqlPlatform) {
			case SQLITE:
				List<List<AbsType>> query = query_(dbUrl, "PRAGMA table_info('%s')", tablename);
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
				List<List<AbsType>> query = query_(dbUrl, "PRAGMA table_info('%s')", tablename);
				return query.stream().filter(X::notEmpty).map(r -> r.get(1).getValue()).filter(X::notNull).map(String::valueOf).collect(Collectors.toList());
			default:
				//return query(dbUrl, "SELECT sql FROM sqlite_master WHERE tbl_name = '%s' AND type = 'table'", tablename).stream().map(t -> t.get(0).getValue()).filter(X::notNull).map(String::valueOf).collect(Collectors.toList());
				throw new NI(sqlPlatform);
		}
	}

	public static List<List<Object>> getAllTableRows(JdbcUrl dbUrl, String tablename, Object ifValIsNull, boolean... includeHead) throws SQLException {
		List<List<AbsType>> query = query_(dbUrl, "select * from %s", tablename);
		if (query.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		Function<List<AbsType>, List<Object>> func = row -> row.stream().map(t -> t.getValueOrDef(ifValIsNull)).collect(Collectors.toList());
		List rows = query.stream().map(func).collect(Collectors.toList());
		if (ARG.isDefNotEqTrue(includeHead)) {
			return rows;
		}
		List head = ArrItem.first(query).stream().map(t -> t.name()).collect(Collectors.toList());
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

	public static Db writeToDb(List<List<Object>> rows, Db db, String tableName) throws SQLException {

		rows = new ArrayList(rows);//wrap to array

		Arr.removeEmptyRows(rows);
		Arr.trimListSizeByHead(rows);

		List<Object> head = Arr.cutHeadRow(rows);
		List<String> decl = head.stream().map(col -> {
			return SqlQueryBuilder.SqlType.TEXT.col(String.valueOf(col));
		}).collect(Collectors.toList());

		String createTableSql = SqlQueryBuilder.CREATE_TABLE.createTable(ERR.notEmpty(tableName), true, decl);
		execute_(db.jdbcUrl, createTableSql);
		for (List<Object> row : rows) {
			String createInsertSql = SqlQueryBuilder.INSERT_INTO_TABLE.insertManyValuesToTable(ERR.notEmpty(tableName), Arrays.asList(row), true);
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
			ERR.isEq(colNames.size(), rows.get(0).size());
		}
		db.createParentDirIfNotExist();
		List<String> decl = colNames.stream().map(col -> {
			return SqlQueryBuilder.SqlType.TEXT.col(String.valueOf(col));
		}).collect(Collectors.toList());

		String createTableSql = SqlQueryBuilder.CREATE_TABLE.createTable(ERR.notEmpty(tableName), false, decl);
		Db.execute_(db.jdbcUrl, createTableSql);
		for (List<Object> row : rows) {
			String createInsertSql = SqlQueryBuilder.INSERT_INTO_TABLE.insertManyValuesToTable(ERR.notEmpty(tableName), Arrays.asList(row));
			Db.execute_(db.jdbcUrl, createInsertSql);
		}
	}

	public void createParentDirIfNotExist() throws IOException {
		UFS_BASE.MKDIR.createDirs_(Paths.get(jdbcUrl.toPathString()).getParent().toFile(), true, false);
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

	public static AbsType toAbsType(int colIndex, String name, int sqlType, ResultSet rs) throws SQLException {
		switch (sqlType) {
			case Types.VARCHAR://12
			{
				String val = rs.getString(colIndex);
				AbsType typ = new AbsType(name, val, String.class);
				return typ;
			}
			case Types.INTEGER://4
			case Types.BIGINT://-5
			{
				long valL = rs.getLong(colIndex);
				AbsType typ = new AbsType(name, valL, Long.class);
				return typ;
			}
			case Types.NUMERIC://2
			{
				BigDecimal val = rs.getBigDecimal(colIndex);
				AbsType typ = new AbsType(name, val, BigDecimal.class);
				return typ;
			}
			case Types.DATE://91
			{
				Date val = rs.getDate(colIndex);
				AbsType typ = new AbsType(name, val, Date.class);
				return typ;
			}
			case Types.TIMESTAMP://93
			{
				long val = rs.getLong(colIndex);
				AbsType typ = new AbsType(name, val, Long.class);
				return typ;
			}
			case Types.BINARY://-2
			{
				byte[] val = rs.getBytes(colIndex);
				AbsType typ = new AbsType(name, val, byte[].class);
				return typ;
			}
			case Types.CHAR://1
			{
				byte[] val = rs.getBytes(colIndex);
				AbsType typ = new AbsType(name, val, byte[].class);
				return typ;
			}
			case Types.BIT://-7
			{
				boolean val = rs.getBoolean(colIndex);
				AbsType typ = new AbsType(name, val, boolean.class);
				return typ;
			}
			case Types.NULL://0
			{
				return new AbsType(name, null, Object.class);
			}

		}
		Object object = rs.getObject(colIndex);
		throw new WhatIsTypeException("What is SqlType of column [%s], object [%s]", sqlType, object == null ? null : object.getClass());
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

	public List<List<AbsType>> query_(String sql) throws SQLException {
		if (L.isDebugEnabled()) {
			L.debug(SYMJ.FILE_DB2 + "Create&Execute sql\n" + sql);
		}
		try (Statement st = conn().createStatement()) {
			List<List<AbsType>> lists = null;
			try (ResultSet rs = st.executeQuery(sql)) {
				lists = toMapAsList(rs);
				return lists;
			} finally {
				if (L.isDebugEnabled()) {
					L.debug(SYMJ.FILE_DB2 + SYMJ.FILE_DB2 + "Create&Execute map({}):\n{}", X.sizeOf(lists), lists);
				}
			}
		}
	}

	public List<List<AbsType>> selectAll(String tablename) throws SQLException {
		return query_("select * from " + tablename);
	}

	public static List<List<AbsType>> toMapAsList(ResultSet rs) throws SQLException {
		return toMap0(rs, true);
	}

	public static List<Map<String, AbsType>> toMapAsMap(ResultSet rs) throws SQLException {
		return toMap0(rs, false);
	}

	public static List toMap0(ResultSet rs, boolean asList) throws SQLException {
		List rows = new ArrayList<>();
		while (rs.next()) {
			ResultSetMetaData metaData = rs.getMetaData();
			int cols = metaData.getColumnCount();
			List<AbsType> rowAsList = null;
			Map<String, AbsType> rowAsMap = null;
			if (asList) {
				rows.add(rowAsList = new ArrayList<>());
			} else {
				rows.add(rowAsMap = new LinkedHashMap<>());

			}
			for (int ci = 1; ci <= cols; ci++) {
				String cName = metaData.getColumnName(ci);
				int cType = metaData.getColumnType(ci);
				AbsType val = toAbsType(ci, cName, cType, rs);
				if (asList) {
					rowAsList.add(val);
				} else {
					rowAsMap.put(cName, val);
				}
			}
		}
		return rows;
	}

	@Deprecated
	public static boolean execute_(String dbName, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		return execute_(JdbcUrl.of(dbName, JdbcUrl.TypeName.NAME), sqlf);
	}

	public static boolean execute_(TypeDb db, String sql, Object... args) throws SQLException {
		return execute_(db.getNamedDbUrl().getJdbcUrlType(), sql, args);
	}

	@SneakyThrows
	public static boolean execute(JdbcUrl jdbcUrl, String sql, Object... args) {
		return execute_(jdbcUrl, sql, args);
	}

	public static boolean execute_(JdbcUrl jdbcUrl, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		try (Db db = new Db(jdbcUrl)) {
			return db.execute_(sqlf);
		}
	}

	@Deprecated
	public static List<List<AbsType>> query_(String dbName, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		return query_(JdbcUrl.of(dbName, JdbcUrl.TypeName.NAME), sqlf);
	}

	public static AbsType queryFF_(TypeDb typeDb, String sql, Object... args) throws SQLException {
		return ArrItem.firstFirst(query_(typeDb, sql, args));
	}

	@SneakyThrows
	public static AbsType queryFF(JdbcUrl jdbcUrl, String sql, Object... args) {
		return ArrItem.firstFirst(query_(jdbcUrl, sql, args));
	}

	public static AbsType queryFFfm_(TypeDb typeDb, String sql, Object... args) throws SQLException {
		return ArrItem.firstFirst(query_(typeDb, X.fm(sql, args)));
	}

	public static List<List<AbsType>> query_(TypeDb typeDb, String sql, Object... args) throws SQLException {
		return query_(typeDb.getNamedDbUrl().getJdbcUrlType(), sql, args);
	}

	public static List<List<AbsType>> query_(JdbcUrl jdbcUrl, String sql, Object... args) throws SQLException {
		String sqlf = ARG.isDefNotEmpty(args) ? X.f(sql, args) : sql;
		try (Db db = new Db(jdbcUrl, true)) {
			return db.query_(sqlf);
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
			_conn = DriverManager.getConnection(jdbcUrl.toJdbcUrl());
			return _conn;
		} catch (ClassNotFoundException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	public boolean isFileDbWithContent() {
		return jdbcUrl.isFileDbWithContent();
	}

	public void removeDbQk() {
		try {
			removeDb();
			ERR.isFileNotExist(jdbcUrl.toPath().toString());
		} catch (IOException ex) {
			if (L.isWarnEnabled()) {
				L.warn("removeDbQk", new IllegalStateException(ex));
			}
		}
	}

	public void removeDb() throws IOException {
		Files.delete(jdbcUrl.toPath());
	}

	@Override
	public String toString() {
		return "Db{" +
				"jdbcUrl=" + jdbcUrl +
				'}';
	}
}