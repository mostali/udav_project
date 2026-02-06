//package org.zkoss.zkspringboot.demo.db;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.env.Environment;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.stereotype.Service;
//import org.sqlite.SQLiteDataSource;
//
//import javax.sql.DataSource;
//
////@Service
//public class SqliteService {
//	@Autowired
//	Environment env;
//
////		@Bean
////	public DataSource dataSource() {
////		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
////		dataSource.setDriverClassName(env.getProperty("driverClassName"));
////		dataSource.setUrl(env.getProperty("url"));
////		dataSource.setUsername(env.getProperty("user"));
////		dataSource.setPassword(env.getProperty("password"));
////		return dataSource;
////	}
//
//	@Bean
//	public DataSource dataSource() {
//		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.sqlite.JDBC");
//		dataSource.setUrl(env.getProperty("jdbc:sqlite:mydb.sqlite"));
//		dataSource.setUsername("sa");
//		dataSource.setPassword("sa");
//		return dataSource;
//	}
//
////	@Bean
////	public DataSource dataSource() {
////		/**
////		 * Используем пул коннектов (в перспективе вынести в конфиг) - актуально для standalone приложения
////		 * Приложение в режиме портлет использует настройки datasorce из контейнера, то как WL
////		 * https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby
////		 */
////		final SQLiteDataSource ds = new SQLiteDataSource();
//////		config.setUsername("sa");
//////		config.setPassword("sa");
////		ds.setUrl("jdbc:sqlite:mydb.sqlite");
////		return ds;
////	}
////	@Bean
////	public DataSource dataSource() {
////		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
////		dataSourceBuilder.driverClassName("org.sqlite.JDBC");
////		dataSourceBuilder.url("jdbc:sqlite:your.db");
////		return dataSourceBuilder.build();
////	}
//}
