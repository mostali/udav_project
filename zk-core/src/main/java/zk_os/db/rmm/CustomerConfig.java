//package zkbae.db;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.jdbc.datasource.SingleConnectionDataSource;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//
////https://habr.com/ru/post/423697/
////https://docs.spring.io/spring-boot/docs/2.1.18.RELEASE/reference/html/howto-database-initialization.html
////https://www.baeldung.com/spring-data-jdbc-intro
//@Configuration
//@EnableJdbcRepositories//(1)
//public class CustomerConfig extends JdbcConfiguration {// (2)
//
//	@Bean
//	public NamedParameterJdbcOperations operations() { //(3)
//		return new NamedParameterJdbcTemplate(dataSource());
//	}
//
//	@Bean
//	public PlatformTransactionManager transactionManager() { //(4)
//		return new DataSourceTransactionManager(dataSource());
//	}
//
//	@Bean
//	public DataSource dataSource() {// (5)
//		return getPgSingleConnectionDataSource();
////		return new EmbeddedDatabaseBuilder()
////				.generateUniqueName(true)
////				.setType(EmbeddedDatabaseType.HSQL)
////				.addScript("create-customer-schema.sql")
////				.build();
//	}
//
//	public static DataSource getPgSingleConnectionDataSource() {
//
//		SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
//		dataSource.setDriverClassName("org.sqlite.JDBC");
//
////		dataSource.setUrl("jdbc:sqlite:memory:myDb?cache=shared");
//		dataSource.setUrl("jdbc:sqlite:mdb.sqlite");
//
//		dataSource.setUsername("sa");
//		dataSource.setPassword("sa");
//
//		return dataSource;
//	}
//}