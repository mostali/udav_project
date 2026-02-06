//https://stackoverflow.com/questions/28275448/multiple-data-source-and-schema-creation-in-spring-boot
package zk_os.db;

import mpc.fs.UFS;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = "zk_os.db_ext",
		entityManagerFactoryRef = "externalEntityManagerFactory",
		transactionManagerRef = "externalTransactionManager")
public class DBConfigExternal {

	public static final String EXTERNAL = "external";

	@Bean(name = "externalDataSource")
//	@ConfigurationProperties(prefix = "spring.datasource-external")
//	@ConfigurationProperties(prefix = "database1")
	public DataSource externalDataSource() {
//		return DataSourceBuilder.create().build();
		return AppDs.buildDataSource_HSQLDB();
	}

	@Bean(name = "externalEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean externalEntityManagerFactory(
			EntityManagerFactoryBuilder builder) {
		Map<String, Object> properties = new HashMap<String, Object>();
//		properties.put("hibernate.hbm2ddl.auto", "create");
//		properties.put("spring.jpa.hibernate.ddl-auto", "create");
//		properties.put("hibernate.ddl-auto", "create");
//		properties.put("spring.datasource.initialization-mode", "always");
//		properties.put("datasource.initialization-mode", "always");
////		properties.put("datasource.schema", "../.data/qzs.sql");
//		properties.put("datasource.schema", "classpath:/schema_qz.sql");
////		properties.put("spring.datasource.schema", "../.data/qzs.sql");
//		properties.put("spring.datasource.schema", "classpath:/schema_qz.sql");


//		properties.put("spring.quartz.jdbc.initialize-schema", "never");
//		properties.put("jdbc.initialize-schema", "never");
//		properties.put("initialize-schema", "never");

//		spring.quartz.jdbc.initialize-schema. never
		return builder
				.dataSource(externalDataSource())
				.packages("zk_os.db_ext")
				.persistenceUnit(EXTERNAL)
				.properties(properties)
				.build();
	}

	@Bean(name = "externalTransactionManager")
	public PlatformTransactionManager externalTransactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setDataSource(externalDataSource());
		jpaTransactionManager.setPersistenceUnitName(EXTERNAL);
		return jpaTransactionManager;
	}


	//
	//
	//

	public static class IsDbAlreadyExistCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context,
							   AnnotatedTypeMetadata metadata) {
			boolean existNotEmptyDb = !UFS.existFile(AppDs.getFnQuartzHsqldb() + ".script", true);
			return existNotEmptyDb;
//			Environment env = context.getEnvironment();
//			return null != env
//					&& "true".equals(env.getProperty("createWebSocket"));
		}
	}

	@Bean
	@Conditional(IsDbAlreadyExistCondition.class)
	public DataSourceInitializer dataSourceInitializer() {
		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
//		resourceDatabasePopulator.addScript(new ClassPathResource("/schema_qz.sql"));
		resourceDatabasePopulator.addScript(new ClassPathResource("/org/quartz/impl/jdbcjobstore/tables_hsqldb.sql"));
		DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
		dataSourceInitializer.setDataSource(externalDataSource());
		dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
		return dataSourceInitializer;
	}

//	@EventListener(ApplicationReadyEvent.class)
//	public void loadData() {
//		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource("data.sql"));
//		resourceDatabasePopulator.execute(dataSource);
//	}
}
