package mpv.sql_morpheus;

public enum SQLPlatform {

	POSTGRE, H2, HSQL, SQLITE, MYSQL, MSSQL, GENERIC;

	/**
	 * Returns the database platfoem based on the JDBC driver class name
	 *
	 * @param driverClassName the fully qualified JDBC driver name
	 * @return the sql platform for driver
	 */
	public static SQLPlatform getPlatform(String driverClassName) {
		final String className = driverClassName.toLowerCase();
		if (className.contains("sqlite")) {
			return SQLITE;
		} else if (className.contains("postgresql")) {
			return POSTGRE;
		} else if (className.contains("h2")) {
			return H2;
		} else if (className.contains("hsql")) {
			return HSQL;
		} else if (className.contains("sqlserver")) {
			return MSSQL;
		} else if (className.contains("mysql")) {
			return MSSQL;
		} else {
			return GENERIC;
		}
	}


}
