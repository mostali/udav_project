package mpe.db;

import lombok.SneakyThrows;
import mpc.env.Env;
import mpc.types.abstype.AbsType;
import mpe.core.P;
import mpe.sql.SeqBuilder;
import mpu.X;
import mpu.core.RW;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JDBC {

	public static void main(String[] args) {

	}

	@SneakyThrows
	public static ResultSet execute_query(String[] dbUrlWitlLP, String sql, Object... args) {
		Class.forName("org.postgresql.Driver");
		Connection connection = null;
		connection = DriverManager.getConnection(dbUrlWitlLP[0], dbUrlWitlLP[1], dbUrlWitlLP[2]);
		Statement statement = connection.createStatement();
//		ResultSet resultSet = statement.executeQuery(U.f(sql, args));
		return statement.executeQuery(X.f(sql, args));
	}

	@SneakyThrows
	public static boolean execute_void(String[] dbUrlWitlLP, String sql, Object... args) {
		Class.forName("org.postgresql.Driver");
		Connection connection = null;
		connection = DriverManager.getConnection(dbUrlWitlLP[0], dbUrlWitlLP[1], dbUrlWitlLP[2]);
		Statement statement = connection.createStatement();
//		ResultSet resultSet = statement.executeQuery(U.f(sql, args));
		return statement.execute(X.f(sql, args));
	}

	public static boolean dropDB_ALL(String[] dbUrlWitlLP) {
		return execute_void(dbUrlWitlLP, "DROP SCHEMA PUBLIC CASCADE;CREATE SCHEMA PUBLIC;");
	}

	//
	//
	//

	static class AttachRemover_EXAMPLE {

		public static final String SQL_GET_ATTACHIDS = "select * from \"attach\" where attachid in (select attachid from attach_doc where docid in (select docid from doc where globaldocid = '%s'))";
		public static final String SQL_GET_ATTTACH_DOC = "select * from attach_doc where attachid in %s";

		public static void main(String[] args) throws SQLException {
			//		String[] urlLP = {""};
			Path pathUsr = Env.HOME_LOCATION.resolve(".env.tlp/otr/dav/" + "dbat");
			String[] ulp = RW.readLine(pathUsr, 0).split("\\s+");
			//		ResultSet resultSet = JDBC.execute_query(urlLP, "select 1 from doc limit 1");
			String docGuid = "1e01908f-6108-48bb-b38a-d76c2b01f47b";
			ResultSet resultSet = JDBC.execute_query(ulp, SQL_GET_ATTACHIDS, docGuid);
			List<Map<String, AbsType>> rsMap = DbRsTo.toMapAsMap(resultSet);
			List<Long> attachIds = rsMap.stream().map(m -> ((BigDecimal) m.get("attachid").val()).longValue()).collect(Collectors.toList());
			StringBuilder stringBuilder = SeqBuilder.generateSequence(attachIds, true, true);
			P.p("Found attachIds: " + stringBuilder);
			P.exit(X.f(SQL_GET_ATTTACH_DOC, stringBuilder));
			P.exit(attachIds);
		}
	}
}
