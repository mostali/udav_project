package zk_os.quartz;


import com.mchange.v2.c3p0.DriverManagerDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import zk_os.db.AppDs;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableAutoConfiguration
//@ConditionalOnExpression("'${using.spring.schedulerFactory}'=='false'")
public class SpringQrtzScheduler {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void init() {
		logger.info("SpringQrtzScheduler inited...");
	}

	@Bean
	public SpringBeanJobFactory springBeanJobFactory() {
		AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
		logger.info("Configuring Job factory");
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean
	public SchedulerFactoryBean scheduler(@Qualifier("externalDataSource") DataSource quartzDataSource) {
//	public SchedulerFactoryBean scheduler(@Qualifier("dsqz") DataSource quartzDataSource) {
//	public SchedulerFactoryBean scheduler(@Qualifier("externalDataSource") DataSource quartzDataSource) {
//	public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job, @Qualifier("dsqz") DataSource quartzDataSource) {
//	public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job) {

//		DataSource quartzDataSource=DataSourceBuilder.create().
//		NI.stop("");


		Properties properties = new Properties();

		properties.setProperty("org.quartz.scheduler.instanceName", "ZNJob");
		properties.setProperty("org.quartz.scheduler.instanceId", "Instance1");
//		properties.setProperty("org.quartz.scheduler.interruptJobsOnShutdownWithWait", "true");

		properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		properties.setProperty("org.quartz.threadPool.threadCount", "5");
		properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
		properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.HSQLDBDelegate");

		//		properties.setProperty("spring.quartz.properties.org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?");
//		properties.setProperty("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?");

//		properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.HSQLDBDelegate");
//		org.quartz.impl.jdbcjobstore.HSQLDBDelegate

		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
//		schedulerFactory.getScheduler().get
		if (false) {
			schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));
		} else {
			schedulerFactory.setQuartzProperties(properties);
		}

		logger.info("Setting the Scheduler up");
		schedulerFactory.setJobFactory(springBeanJobFactory());
//		schedulerFactory.setJobDetails(job);
//		schedulerFactory.setTriggers(trigger);

		// Comment the following line to use the default Quartz job store.
		schedulerFactory.setDataSource(quartzDataSource);

		return schedulerFactory;
	}

//	@Bean
//	public JobDetailFactoryBean jobDetail() {
//
//		JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
//		jobDetailFactory.setJobClass(SampleJob.class);
//		jobDetailFactory.setName("Qrtz_Job_Detail");
//		jobDetailFactory.setDescription("Invoke Sample Job service...");
//		jobDetailFactory.setDurability(true);
//		return jobDetailFactory;
//	}

//	@Bean
//	public SimpleTriggerFactoryBean trigger(JobDetail job) {
//
//		SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
//		trigger.setJobDetail(job);
//
//		int frequencyInSec = 10;
//		logger.info("Configuring trigger to fire every {} seconds", frequencyInSec);
//
//		trigger.setRepeatInterval(frequencyInSec * 1000);
//		trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
//		trigger.setName("Qrtz_Trigger");
//		return trigger;
//	}

	@QuartzDataSource
//	@ConfigurationProperties(prefix = "spring.datasource")
	@ConfigurationProperties("database1.datasource")
	@Bean(name = "dsqz")
	public DataSource quartzDataSource() {

		if (true) {
			return AppDs.buildDataSource_HSQLDB();
		}

		if (true) {
			//				.driverClassName("org.sqlite.JDBC")
//				.url("jdbc:sqlite:../.data/qz.sqlite")

//			HikariDataSource dataSource = new HikariDataSource();
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClass("org.sqlite.JDBC");
			dataSource.setJdbcUrl("jdbc:sqlite:../.data/qz.sqlite");
			dataSource.setUser("sa");
//			dataSource.setUsername("sa");
			dataSource.setPassword("");
//			org.quartz.impl.jdbcjobstore.HSQLDBDelegate
			return dataSource;
		}

		if (true) {
			return AppDs.buildDataSourceH2();
		}

//		if (true) {
//			return DataSourceBuilder.create().build();
//		}

//		Properties properties = new Properties();
//		properties.setProperty("org.quartz.scheduler.instanceName", "MyInstanceName");
//		properties.setProperty("org.quartz.scheduler.instanceId", "Instance1");
//
//		properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
//		properties.setProperty("org.quartz.threadPool.threadCount", "2");
//		properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

		DataSource quartzDataSource = DataSourceBuilder.create()
//				.username(environment.getProperty("spring.datasource.username"))
//				.password(environment.getProperty("spring.datasource.password"))

//				.driverClassName("org.sqlite.JDBC")
//				.url("jdbc:sqlite:../.data/qz.sqlite")

//				.driverClassName("org.hsqldb.jdbcDriver")
//				.url("jdbc:hsqldb:hsql://localhost:")

				.driverClassName("org.h2.Driver")
				.url("jdbc:h2:mem:spring-quartz2;INIT=RUNSCRIPT FROM 'classpath:/org/quartz/impl/jdbcjobstore/tables_h2.sql'")
//				.password("sa")

				.type(HikariDataSource.class)

				.build();

		return quartzDataSource;
	}

	//	@Bean
//	LazyInitializationExcludeFilter hikariMetricsLazyInitializationExcludeFilter() {
//		return (name, definition, type) -> name.equals(
//				"org.springframework.boot.actuate.autoconfigure.metrics.jdbc.DataSourcePoolMetricsAutoConfiguration$HikariDataSourceMetricsConfiguration");
//	}

}