package mp.utl_ndb;

import lombok.SneakyThrows;
import mpc.X;
import mpc.core.P;

import java.sql.*;

public class JDBC {




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

	public static boolean dropDB(String[] dbUrlWitlLP) {
		return execute_void(dbUrlWitlLP, "DROP SCHEMA PUBLIC CASCADE;CREATE SCHEMA PUBLIC;");
	}
}
