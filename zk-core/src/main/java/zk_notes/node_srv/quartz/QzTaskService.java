package zk_notes.node_srv.quartz;

import lombok.SneakyThrows;
import mpe.wthttp.QzTaskMsg;
import mpu.IT;
import mpu.core.QDate;
import mpu.pare.Pare3;
import mpu.str.Hu;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_notes.node.NodeDir;
import zk_os.quartz.QzApiEE;
import zk_os.sec.Sec;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QzTaskService {

	public static final Logger L = LoggerFactory.getLogger(QzTaskService.class);

	public static int[] deleteAll(NodeDir nodeDir, boolean interrupt) {
		return QzApiEE.deleteAll(findAllJobKeys(nodeDir), interrupt);
	}

	public static Set<JobKey> findAllJobKeys(NodeDir node) {
		Pare3<String, String, String> jobKeys = getJobKeys(node, 0);
		return QzApiEE.findAllJobKeys(jobKeys.key());
	}

	public static void runAll(NodeDir node) {
		runAll(node, QzTaskMsg.of(node.state().nodeData()));
	}

	public static void runAll(NodeDir node, QzTaskMsg qzCallMsg) {
		qzCallMsg.throwIsErr();
		for (int i = 0; i < qzCallMsg.jobs.size(); i++) {
			QzTaskMsg.JobLinePattern jobLinePattern = qzCallMsg.jobs.get(i);
			addJob(IT.isClassOf(qzCallMsg.jobClassName, Job.class), jobLinePattern, node, i);
		}
	}


	@SneakyThrows
	private static void addJob(Class<? extends Job> jobClass, QzTaskMsg.JobLinePattern jobLinePattern, NodeDir node, int lineIndex) {

		String msg = jobLinePattern.getMsg();

		Map<String, Serializable> jobData = new HashMap<>();
		jobData.put("userId", Sec.getUser().getId());
		jobData.put("job", jobLinePattern.line0);

		JobDataMap jobCtx = new JobDataMap(jobData);

		Pare3<String, String, String> keys = getJobKeys(node, lineIndex);

		String jGroup = keys.key();
		String jName = keys.val();
		String jTriggerId = keys.ext();

		if (lineIndex == 0) {
			QzApiEE.deleteAll(jGroup, false);
		}

		QDate startDate = jobLinePattern.getTargetDate(true);
		QDate endDate = jobLinePattern.getTargetDate(false);

		long everyMs = jobLinePattern.getEveryMs();

		Trigger jTrigger = buildTrigger(jTriggerId, jGroup, startDate, endDate, everyMs);

		JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jName, jGroup).usingJobData(jobCtx).build();

		QzApiEE.getScheduler().scheduleJob(job, jTrigger);

		if (L.isInfoEnabled()) {
			L.info("BuildJob jbName=[{}] jbGrp[{}] jbTrg[{}] jbClass [{}] at [{}] before/after [{}/{}] every [{}] jobData [{}]"
					, jName, jGroup, jTrigger, "jjjjjj", Hu.DATE(jobLinePattern.getTargetDate()), Hu.DATE(startDate), Hu.DATE(endDate), Hu.TIME(everyMs), jobData);
		}
	}

	private static Pare3<String, String, String> getJobKeys(NodeDir node, int lineIndex) {
		//		String nodeSdnName = node.nodeNameWithSdn();
		String nodeSdnName = node.nodeId().string();
		String jGroup = "G[" + nodeSdnName + "]";
		String jName = "N[" + nodeSdnName + "##" + lineIndex + "]";
		String jTriggerId = jName;
		return Pare3.of(jGroup, jName, jTriggerId);
	}

	private static Trigger buildTrigger(String jTrigger, String jGroup, Date startDate, Date endDate, long intervalMs) {

		SimpleScheduleBuilder schedBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(intervalMs).repeatForever()
				.withMisfireHandlingInstructionFireNow();

		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jTrigger, jGroup)
				.startAt(startDate).endAt(endDate)
				.withSchedule(schedBuilder)
				.build();

		return trigger;
	}

//
//	private static Trigger buildTriggerWithCron() {
//		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jTrigger, jGroup).startNow()
////					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
////							.withIntervalInSeconds(10)
////							.repeatForever())
//				.withSchedule(CronScheduleBuilder.cronSchedule(cron))//ss mm hh dd MM dayWeek
//				.build();
//		return trigger;
//	}

}
