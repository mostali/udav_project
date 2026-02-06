package zk_os.db;

import mpc.env.Env;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import zk_os.AppZosCore;

public class AppDs {

	public static final String HSQLDB_QZDB_NAME = "db-quartz";
	private static String FILE_HSQLDB_QZDB = null;

	public static String getFnQuartzHsqldb() {
		return FILE_HSQLDB_QZDB == null ? (FILE_HSQLDB_QZDB = Env.getDefaultAppDataDir() + "/" + HSQLDB_QZDB_NAME + "/" + HSQLDB_QZDB_NAME) : FILE_HSQLDB_QZDB;
	}

	public static @NotNull DriverManagerDataSource buildDataSource_App_Sqlite() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.sqlite.JDBC");
		dataSource.setUrl("jdbc:sqlite:" + AppZosCore.getAppDbFile());
		dataSource.setUsername("sa");
		dataSource.setPassword("sa");
		return dataSource;
	}


	public static DriverManagerDataSource buildDataSource_HSQLDB() {
		//			if (true) {
//				EmbeddedDatabaseBuilder dataSource = new EmbeddedDatabaseBuilder()
//						.setType(EmbeddedDatabaseType.HSQL)
////				.addScript("classpath:schema.sql")
//						.addScript("classpath:/org/quartz/impl/jdbcjobstore/tables_h2.sql");
////				dataSource.setDriverClass("org.hsqldb.jdbcDriver");
////				dataSource.setJdbcUrl("jdbc:hsqldb:mem:myDb");
//				return dataSource
//						.build();
//			}

//			Properties props = new Properties();
//			props.setProperty("","");

//		com.mchange.v2.c3p0.DriverManagerDataSource dataSource = new com.mchange.v2.c3p0.DriverManagerDataSource();
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//			dataSource.
//		dataSource.setDriverClass("org.hsqldb.jdbcDriver");
//		dataSource.setJdbcUrl("jdbc:hsqldb:file:myDb");
//		dataSource.setUser("sa");

		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl("jdbc:hsqldb:file:" + getFnQuartzHsqldb());
		dataSource.setUsername("sa");
		dataSource.setPassword("");
//			dataSource.
		return dataSource;
//		return DataSourceBuilder.create().type(com.mchange.v2.c3p0.DriverManagerDataSource.class).build();
//		return DataSourceBuilder.create().type(DriverManagerDataSource.class).build();
	}

	//
	//
	//
	public static @NotNull com.mchange.v2.c3p0.DriverManagerDataSource buildDataSourceH2() {
		//			HikariDataSource dataSource2 = new HikariDataSource();

		com.mchange.v2.c3p0.DriverManagerDataSource dataSource = new com.mchange.v2.c3p0.DriverManagerDataSource();
//			dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setDriverClass("org.h2.Driver");
//			dataSource.setJdbcUrl("jdbc:h2:mem:spring-quartz3;INIT=RUNSCRIPT FROM 'classpath:/org/quartz/impl/jdbcjobstore/tables_h2.sql'");
		dataSource.setJdbcUrl("jdbc:h2:mem:spring-quartz3;INIT=RUNSCRIPT FROM 'classpath:/org/quartz/impl/jdbcjobstore/tables_h2.sql'");
//			dataSource.setJdbcUrl("jdbc:h2:mem:spring-quartz4;");
		dataSource.setUser("sa");
//			dataSource.setUsername("sa");
		dataSource.setPassword("");
//			org.quartz.impl.jdbcjobstore.HSQLDBDelegate
		return dataSource;
	}

}
