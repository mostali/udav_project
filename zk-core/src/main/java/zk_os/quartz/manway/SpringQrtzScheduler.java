////ooold
//package zk_os.quartz;
//
//import org.quartz.JobDetail;
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.quartz.Trigger;
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
//import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//import org.springframework.scheduling.quartz.SpringBeanJobFactory;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableAutoConfiguration
////https://www.baeldung.com/spring-quartz-schedule
//public class SpringQrtzScheduler {
//
//	@Bean
//	@QuartzDataSource
//	public DataSource quartzDataSource() {
//		return DataSourceBuilder.create().build();
//	}
//
//	@Bean
//	public Scheduler scheduler(Trigger trigger, JobDetail job, SchedulerFactoryBean factory)
//			throws SchedulerException {
//		Scheduler scheduler = factory.getScheduler();
//		scheduler.scheduleJob(job, trigger);
//		scheduler.start();
//		return scheduler;
//	}
//
//	@Bean
//	public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job, DataSource quartzDataSource) {
//		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
//		schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));
//
//		schedulerFactory.setJobFactory(springBeanJobFactory());
//		schedulerFactory.setJobDetails(job);
//		schedulerFactory.setTriggers(trigger);
//		schedulerFactory.setDataSource(quartzDataSource);
//		return schedulerFactory;
//	}
//
//	@Bean
//	public SpringBeanJobFactory springBeanJobFactory() {
//		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
//		jobFactory.setApplicationContext(applicationContext);
//		return jobFactory;
//	}
//
//}
