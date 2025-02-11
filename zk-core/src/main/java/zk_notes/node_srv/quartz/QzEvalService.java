package zk_notes.node_srv.quartz;

import lombok.SneakyThrows;
import mpc.map.MMAP;
import mpe.ftypes.core.FDate;
import mpe.wthttp.QzEvalMsg;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.core.QDate;
import mpu.pare.Pare3;
import mpu.str.Hu;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_notes.node.NodeDir;
import zk_os.quartz.QzApiEE;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QzEvalService {

	public static final Logger L = LoggerFactory.getLogger(QzEvalService.class);

	public static int[] deleteAll(NodeDir nodeDir, boolean interrupt) {
		return QzApiEE.deleteAll(findAllJobKeys(nodeDir), interrupt);
	}

	public static Set<JobKey> findAllJobKeys(NodeDir node) {
		Pare3<String, String, String> jobKeys = getJobKeys(node);
		return QzApiEE.findAllJobKeys(jobKeys.key());
	}

	private static Pare3<String, String, String> getJobKeys(NodeDir node) {
		//		String nodeSdnName = node.nodeNameWithSdn();
		String nodeSdnName = node.nodeId().string();
		String jGroup = "G[" + nodeSdnName + "]";
		String jName = "N[" + nodeSdnName + "]";
		String jTriggerId = jName;
		return Pare3.of(jGroup, jName, jTriggerId);
	}

	public static void runAll(NodeDir srcNode) {
		runAll(srcNode, QzEvalMsg.of(srcNode.state().nodeData()));
	}

	public static void runAll(NodeDir srcNode, QzEvalMsg qzCallMsg) {
		qzCallMsg.throwIsErr();
		addJob(IT.isClassOf(qzCallMsg.jobClassName, Job.class), srcNode, qzCallMsg.getBodyAsPropertiesMap());
	}

	@SneakyThrows
	private static void addJob(Class<? extends Job> jobClass, NodeDir srcNode, Map jobConfigureContext) {

//		Map jobConfigureContext = bodyAsPropertiesMultimap.asMap();

		Map<String, Serializable> jobData = new HashMap<>();
		jobData.put(QzEvalMsg.NODEID_SRC, srcNode.id());
//		jobData.put(QzEvalMsg.NODEID_DST, nodeId);
		jobData.putAll(jobConfigureContext);

		JobDataMap jobCtx = new JobDataMap(jobData);

		Pare3<String, String, String> keys = getJobKeys(srcNode);

		String jGroup = keys.key();
		String jName = keys.val();
		String jTriggerId = keys.ext();

		String cron = MMAP.getFirstAsString(jobConfigureContext, "cron", null);
		Trigger qzTrigger;

		if (X.empty(cron)) {
			QDate startDate = MMAP.getFirstAsQDate(jobConfigureContext, "start", ARR.of(FDate.YYYY_DB_ISO_STANDART, FDate.MONO14));
			QDate endDate = MMAP.getFirstAsQDate(jobConfigureContext, "end", ARR.of(FDate.YYYY_DB_ISO_STANDART, FDate.MONO14));
			long everyMs = MMAP.getFirstAsTimeMarkMs(jobConfigureContext, "every");
			qzTrigger = buildFixedTrigger(jTriggerId, jGroup, startDate, endDate, everyMs);
			if (L.isInfoEnabled()) {
				L.info("BuildJob/SIMPLE jbName=[{}] jbGrp[{}] jbTrg[{}] jbClass [{}] at START/END [{}/{}] EVERY [{}] jobData [{}]" //
						, jName, jGroup, qzTrigger, jobClass, Hu.DATE(startDate), Hu.DATE(endDate), Hu.TIME(everyMs), jobData);
			}
		} else {

			qzTrigger = buildTriggerWithCron(jTriggerId, jGroup, cron);

			if (L.isInfoEnabled()) {
				L.info("BuildJob/CRON jbName=[{}] jbGrp[{}] jbTrg[{}] jbClass [{}] CRON [{}] jobData [{}]" //
						, jName, jGroup, qzTrigger, jobClass, cron, jobData);
			}
		}

		JobDetail qzJobDetail = JobBuilder.newJob(jobClass).withIdentity(jName, jGroup).usingJobData(jobCtx).build();

		QzApiEE.getScheduler().scheduleJob(qzJobDetail, qzTrigger);

	}


	private static Trigger buildFixedTrigger(String jTrigger, String jGroup, Date startDate, Date endDate, long intervalMs) {

		SimpleScheduleBuilder schedBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(intervalMs).repeatForever()
				.withMisfireHandlingInstructionFireNow();
//				.withMisfireHandlingInstructionIgnoreMisfires();

		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jTrigger, jGroup)
				.startAt(startDate).endAt(endDate)
				.withSchedule(schedBuilder)
				.build();

		return trigger;
	}

	//
	private static Trigger buildTriggerWithCron(String jTrigger, String jGroup, String cron) {
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jTrigger, jGroup) // .startNow
				.withSchedule(CronScheduleBuilder.cronSchedule(cron))//ss mm hh dd MM dayWeek
				.build();
		return trigger;
	}

}
