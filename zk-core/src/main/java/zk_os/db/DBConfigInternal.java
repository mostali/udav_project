package zk_os.db;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "zk_os.db",
		entityManagerFactoryRef = "internalEntityManagerFactory",
		transactionManagerRef = "internalTransactionManager")
public class DBConfigInternal {

	public static final String INTERNAL = "internal";

	@Bean(name = "internalDataSource")
	@Primary
//	@ConfigurationProperties(prefix = "spring.datasource-internal")
	public DataSource internalDataSource() {
//		return DataSourceBuilder.create().build();
		return AppDs.buildDataSource_App_Sqlite();
	}

	@Bean(name = "internalEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean internalEntityManagerFactory(
			EntityManagerFactoryBuilder builder) {

		Map<String, Object> properties = new HashMap<String, Object>();
//		properties.put("jpa.generate-ddl", "true");
//		properties.put("spring.jpa.generate-ddl", "true");
//		properties.put("spring.batch.initialize-schema", "always");
//
//		properties.put("hibernate.hbm2ddl.auto", "create");
//		properties.put("jpa.hibernate.ddl-auto", "create");
//		properties.put("spring.jpa.hibernate.ddl-auto", "create");
//		properties.put("hibernate.ddl-auto", "create");
//		properties.put("spring.datasource.initialization-mode", "always");
//		properties.put("datasource.initialization-mode", "always");
//		properties.put("initialization-mode", "always");
//		properties.put("datasource.schema", "../.data/qzs.sql");
//		properties.put("datasource.schema", "classpath:/schema_qz.sql");
//		properties.put("schema", "classpath:/schema_qz.sql");
//		properties.put("spring.datasource.schema", "../.data/qzs.sql");
//		properties.put("spring.datasource.schema", "classpath:/schema_qz.sql");

		return builder
				.dataSource(internalDataSource())
				.packages("zk_os.db")
				.persistenceUnit(INTERNAL)
				.properties(properties)
				.build();
	}

	@Bean(name = "internalTransactionManager")
	@Primary
	public PlatformTransactionManager internalTransactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setDataSource(internalDataSource());
		jpaTransactionManager.setPersistenceUnitName(INTERNAL);
		return jpaTransactionManager;
	}
}
