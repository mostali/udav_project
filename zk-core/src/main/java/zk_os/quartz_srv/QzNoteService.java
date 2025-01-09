package zk_os.quartz_srv;

import com.google.common.eventbus.Subscribe;
import lombok.SneakyThrows;
import mpe.eventbus.UEventBus;
import mpu.IT;
import mpu.Sys;
import mpu.core.QDate;
import mpu.pare.Pare3;
import mpu.str.Hu;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Log;
import zk_os.quartz.QzApiEE;
import zk_os.sec.Sec;
import zk_page.node.NodeDir;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QzNoteService {

	public static final Logger L = LoggerFactory.getLogger(QzNoteService.class);

	public static void registerEventBus() {
		UEventBus.register(new SaveTbxListener(), SaveTbxListener.BUSEVENT_NOTE_SAVE);
	}

	public static class SaveTbxListener {

		public static final String BUSEVENT_NOTE_SAVE = "NoteSaveEvent";

		@Subscribe
		public static void onChangeTbxEvent(Object event) {
			try {
				onChangeTbxEventImpl(event);
			} catch (Exception ex) {
				L.error("Error onChangeTbxEvent", ex);
				ZKI.alert(ex);
			}
		}

		public static void onChangeTbxEventImpl(Object event) {

			Pare3<NodeDir, Path, String> eventData = IT.isType0(event, Pare3.class);
			NodeDir node = eventData.key();
			Path nodePath = eventData.val();
			String nodeData = eventData.ext();

			if (!QzCallMsg.isValidFirstLine(nodeData)) {
				return;
			}
			QzCallMsg qzCallMsg = QzCallMsg.of(nodeData, true);
			if (qzCallMsg.hasErrors()) {
				qzCallMsg.getErrors().forEach(e -> L.error("QzCallMsg-FAIL", e));
				ZKI_Log.alert(qzCallMsg.getErrsAsMsg("Errors Quartz Service:", true));
				return;
			}

			for (int i = 0; i < qzCallMsg.jobs.size(); i++) {
				QzCallMsg.JobLinePattern jobLinePattern = qzCallMsg.jobs.get(i);
				addJob(qzCallMsg.jobClassName, jobLinePattern, node, i);
			}
			Sys.say("stored");
		}

	}

	@SneakyThrows
	private static void addJob(Class<? extends Job> jobClass, QzCallMsg.JobLinePattern jobLinePattern, NodeDir node, int lineIndex) {

		String msg = jobLinePattern.getMsg();

		Map<String, Serializable> jobData = new HashMap<>();
		jobData.put("userId", Sec.getUser().getId());
		jobData.put("job", jobLinePattern.line);

		JobDataMap jobCtx = new JobDataMap(jobData);

		String nodeSdnName = node.nodeNameWithSdn();

		String jGroup = "jGroup-" + nodeSdnName;

		String jName = "jName-" + nodeSdnName + "##" + lineIndex;

		String jTriggerId = jName;

		if (lineIndex == 0) {
			checkAndDeleteAllExistedJobOfGroup(jGroup);
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

	@SneakyThrows
	private static void checkAndDeleteAllExistedJobOfGroup(String jGroup) {
//		QzApiEE.deleteAll(ARR.as(new JobKey(jName, jGroup)), false);
		QzApiEE.deleteAll(jGroup, false);
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
