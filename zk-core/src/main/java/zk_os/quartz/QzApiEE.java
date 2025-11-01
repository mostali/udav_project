package zk_os.quartz;

import lombok.SneakyThrows;
import mpc.exception.CleanMessageRuntimeException;
import mpc.exception.EException;
import mpc.exception.SimpleMessageRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpe.rt.SLEEP;
import mpu.IT;
import mpu.Sys;
import mpu.X;
import mpu.core.ARR;
import mpu.core.QDate;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utl_spring.AppContext;

import java.io.Serializable;
import java.util.*;

public class QzApiEE extends EException {

	public static final Logger L = LoggerFactory.getLogger(QzApiEE.class);

	public static final String SRV_MARK = SYMJ.TIME_SANDGLASS + "QZJ";

//	public void startAllSchedulers() {
//		List<SchedulerJobInfo> jobInfoList = schedulerRepository.findAll();
//		if (jobInfoList != null) {
//			Scheduler scheduler = schedulerFactoryBean.getScheduler();
//			jobInfoList.forEach(jobInfo -> {
//				try {
//					JobDetail jobDetail = JobBuilder.newJob(SampleCronJob.class)
//							.withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
//					if (!scheduler.checkExists(jobDetail.getKey())) {
//						Trigger trigger;
//						jobDetail = scheduleCreator.createJob(SampleCronJob.class,
//								false, context, jobInfo.getJobName(), jobInfo.getJobGroup());
//
//						if (jobInfo.getCronJob() && CronExpression.isValidExpression(jobInfo.getCronExpression())) {
//							trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
//									jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
//						} else {
//							trigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(),
//									jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
//						}
//
//						scheduler.scheduleJob(jobDetail, trigger);
//
//					}
//				} catch (SchedulerException e) {
//					log.error(e.getMessage(), e);
//				}
//			});
//		}
//	}

//	private static class SingletonHolder {
//		public static final QzApiEE HOLDER_INSTANCE = new QzApiEE();
//	}

//	public static QzApiEE get() {
//		return SingletonHolder.HOLDER_INSTANCE;
//	}

	@SneakyThrows
	public static void boot(Scheduler scheduler0) {

		scheduler = AppContext.getBean(Scheduler.class);
//		scheduler = scheduler0;
		if (false) {
//		if (true) {
//			new Thread(() -> {
//				try {
//					Thread.sleep(3000);
			runScheduleJob("j1", "g1", "t1", "*/30 * * ? * *", HelloJob.class);
//				} catch (QzApiEE e) {
//					throw new RuntimeException(e);
//				} catch (InterruptedException e) {
//					throw new RuntimeException(e);
//				}
//
//			}).start();
//			runScheduleJob("j1", "g1", "t1", "*/1 * * ? * *", HelloJob.class);
		}
	}

	public static Scheduler scheduler = null;

	@SneakyThrows
	public static Set<JobKey> getAllJobKeys()  {
		return getScheduler().getJobKeys(GroupMatcher.anyGroup());
	}

	@SneakyThrows
	public static Scheduler getScheduler() {
		if (scheduler != null) {
			return scheduler;
		}
		try {
			int type = 0;
			switch (type) {
				case 0:
					Properties p = new Properties();
					p.put("org.quartz.threadPool.threadCount", "10");
					StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
					stdSchedulerFactory.initialize(p);
					scheduler = stdSchedulerFactory.getScheduler();
//					scheduler = StdSchedulerFactory.getDefaultScheduler();
					break;
//				case 1:
//					DirectSchedulerFactory schedulerFactory = DirectSchedulerFactory.getInstance();
//					schedulerFactory.createVolatileScheduler(35);
//					scheduler = schedulerFactory.getScheduler();
//					break;
				default:
					throw new WhatIsTypeException(type);
			}
			scheduler.start();
		} catch (SchedulerException ex) {
			scheduler = null;
			throw EE.INIT_RUN.I(ex, "Error init&run scheduler");
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					scheduler.shutdown();
				} catch (SchedulerException e) {
					X.throwException(e);
				}
			}

		});
		return scheduler;
	}

	/**
	 * *************************************************************
	 * ---------------------------- EXAMPLE --------------------------
	 * *************************************************************
	 */
	public static void main(String[] args) throws QzApiEE, SchedulerException {

		if (true) {
			runScheduleJob("j1", "g1", "t1", "*/1 * * ? * *", HelloJob.class);
			runScheduleJob("j2", "g2", "t2", "*/1 * * ? * *", HelloJob2.class);
		} else {
//			getScheduler();
			Set<JobKey> allJobKeys = getAllJobKeys();
			X.p("getAllJobKeys:" + allJobKeys);
		}
		Sys.p("go");

		while (true) {
			Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
			Sys.p(threadSet.size());
//			P.p(threadSet);
			SLEEP.ms(3000);
		}
	}

	public static void runScheduleJob(String jName, String jGroup, String jTrigger, String cron, Class<? extends Job> jClass) throws QzApiEE {
		runScheduleJob(jName, jGroup, jTrigger, cron, jClass, new HashMap<>());
	}

	public static void runScheduleJob(String jName, String jGroup, String jTrigger, String cron, Class<? extends Job> jClass, Map<String, Serializable> jobData) throws QzApiEE {
		if (L.isInfoEnabled()) {
			L.info("QZ:NEW:runScheduleJob:jbName=[{}] jbGrp[{}] jbTrg[{}] jbCron[{}] jbClass [{}] jobData [{}]", jName, jGroup, jTrigger, cron, jClass.getSimpleName(), jobData);
		}
		JobDataMap jobCtx = new JobDataMap(jobData);
		JobDetail job = JobBuilder.newJob(jClass).withIdentity(jName, jGroup).usingJobData(jobCtx).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jTrigger, jGroup).startNow()
//					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
//							.withIntervalInSeconds(10)
//							.repeatForever())
				.withSchedule(CronScheduleBuilder.cronSchedule(cron))//ss mm hh dd MM dayWeek
				.build();
		try {
			getScheduler().scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			throw EE.SCHEDULE_JOB.I(e);
		}
	}

	public static void example_run() {

		try {
			// Grab the Scheduler instance from the Factory
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

			// and start it off
			scheduler.start();


			// define the job and tie it to our HelloJob class
			JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity("job1", "group1").build();
			JobDetail job2 = JobBuilder.newJob(HelloJob.class).withIdentity("job2", "group1")

					.build();

			// Trigger the job to run now, and then repeat every 40 seconds
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").startNow()
//					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
//							.withIntervalInSeconds(10)
//							.repeatForever())
					.withSchedule(CronScheduleBuilder.cronSchedule("10 */1 * * * ?"))//ss mm hh dd MM dayWeek
					.build();

			Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("trigger2", "group1").startNow()
//					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
//							.withIntervalInSeconds(10)
//							.repeatForever())
					.withSchedule(CronScheduleBuilder.cronSchedule("10 16-19 * * * ?"))//ss mm hh dd MM dayWeek
					.build();

//n			CronExpression d;

			// Tell quartz to schedule the job using our trigger
			scheduler.scheduleJob(job, trigger);
			scheduler.scheduleJob(job2, trigger2);

//			Thread.sleep(Tim);
//			scheduler.shutdown();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						scheduler.shutdown();
					} catch (SchedulerException e) {
						X.throwException(e);
					}
				}

			});
		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}

	@SneakyThrows
	public static int[] deleteAllTotal(boolean interrupt) {
		Set<JobKey> allJobKeys = getAllJobKeys();
		return deleteAll(allJobKeys, interrupt);

	}

	@SneakyThrows
	public static int[] deleteAll(String jobGroup, boolean interrupt) {
		return deleteAll(findAllJobKeys(jobGroup), interrupt);
	}

	@SneakyThrows
	public static Set<JobKey> findAllJobKeys(String jobGroup) {
		return QzApiEE.getScheduler().getJobKeys(GroupMatcher.groupEquals(jobGroup));
	}

	@SneakyThrows
	public static int[] deleteAll(Collection<JobKey> all, boolean interrupt) {
		int[] removed = new int[]{X.sizeOf(all), 0};
		for (JobKey existedJobKey : all) {
			JobDetail jobDetail = scheduler.getJobDetail(existedJobKey);
			if (jobDetail != null) {
				if (interrupt) {
					Class<? extends Job> jobClass = jobDetail.getJobClass();
					if (InterruptableJob.class.isAssignableFrom(jobClass)) {
						boolean isInterrupted = scheduler.interrupt(existedJobKey);
						L.info("Interrupt existed Job '{}' -> {} <- {}", existedJobKey, isInterrupted, jobClass);
					}
				}
				IT.state(scheduler.deleteJob(existedJobKey), "error delete Job '%s'", existedJobKey);
				L.info("Deleted existed Job '{}'", existedJobKey);
				removed[1]++;
			}
		}
		L.info("Deleted '{}' existed Job", ARR.ofInt(removed));
		return removed;
	}


	//	public static class HelloJob implements Job {
	public static class HelloJob implements InterruptableJob {
		public static int CTR = 0;

		@Override
		public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
			Sys.p("Job1[" + jobExecutionContext.getJobDetail().getKey().getName() + "]:" + QDate.now().f(QDate.F.MONO20NF) + jobExecutionContext.getMergedJobDataMap());
			Sys.say("1s");
			SLEEP.ms(5_000);
			Sys.say("1e");
			Sys.p("Job1End" + CTR++);

		}

		@Override
		public void interrupt() throws UnableToInterruptJobException {

		}
	}

	public static class HelloJob2 implements Job {
		public static int CTR = 0;

		@SneakyThrows
		@Override
		public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
			Sys.p("Job2[" + jobExecutionContext.getJobDetail().getKey().getName() + "]:" + QDate.now().f(QDate.F.MONO20NF));
			SLEEP.sec(20);
			Sys.p("Job2End" + CTR++);
//			P.p(jobExecutionContext.getScheduler().getContext());
//			P.p(jobExecutionContext.getScheduler().getPausedTriggerGroups());
//			P.p(jobExecutionContext.getScheduler().getMetaData().getNumberOfJobsExecuted());
//			P.p(jobExecutionContext.getScheduler().getMetaData().getNumberOfJobsExecuted());
//			P.p(jobExecutionContext.getScheduler().getListenerManager().);
			Sys.say("2");

		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- INIT --------------------------
	 * *************************************************************
	 */
	@Override
	public EE type() {
		return super.type(EE.class);
	}

	public enum EE {
		NOSTATUS, INIT_RUN, SCHEDULE_JOB, JOB_EXISTED;

		public QzApiEE I() {
			return new QzApiEE(this);
		}

		public QzApiEE I(Throwable ex) {
			return new QzApiEE(this, ex);
		}

		public QzApiEE I(Throwable ex, String msg, Object... args) {
			return new QzApiEE(this, new SimpleMessageRuntimeException(ex, msg, args));
		}

		public QzApiEE I(String message) {
			return new QzApiEE(this, new SimpleMessageRuntimeException(message));
		}

		public QzApiEE I(String message, Object... args) {
			return new QzApiEE(this, new SimpleMessageRuntimeException(X.f(message, args)));
		}

		public QzApiEE M(String message, Object... args) {
			return new QzApiEE(this, new CleanMessageRuntimeException(X.f(message, args)));
		}
	}

	public QzApiEE() {
		super(EE.NOSTATUS);
	}

	public QzApiEE(EE error) {
		super(error);
	}

	public QzApiEE(EE error, Throwable cause) {
		super(error, cause);
	}


}