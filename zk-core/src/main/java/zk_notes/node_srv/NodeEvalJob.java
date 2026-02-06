package zk_notes.node_srv;

import mpc.rfl.RFL;
import mpe.core.ERR;
import mpe.call_msg.QzEvalMsg;
import mpu.IT;
import mpu.X;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_notes.node.NodeDir;
import zk_os.tasks.v1.TaskPanel_V1;

import java.util.Map;
import java.util.stream.Collectors;

public class NodeEvalJob implements InterruptableJob {

	public static final Logger L = LoggerFactory.getLogger(NodeEvalJob.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//		Sys.p("Job1[" + jobExecutionContext.getJobDetail().getKey().getName() + "]:" + QDate.now().f(QDate.F.MONO20NF) + jobExecutionContext.getMergedJobDataMap());
//		Sys.say("1s");
//		SLEEP.sleep(5_000);
//		Sys.say("1e");
//		Sys.p("Job1End" + CTR++);

		JobDataMap context = jobExecutionContext.getMergedJobDataMap();

		String nodeIdSrc = IT.NE(context.getString(QzEvalMsg.NODEID_SRC));

//		String trackId = TrackMap.getTrackIdManually(context.getWrappedMap());

		NodeDir nodeDir = NodeDir.ofNodeIdStr(nodeIdSrc);
		TaskPanel_V1.addTaskD(nodeDir, Thread.currentThread());

		String rslt = null;
		Exception err = null;

		try {
			String nodeIdDst = QzEvalMsg.of(nodeDir.nodeDataStr()).nodeId;

			NodeDir nodeDir1 = NodeDir.ofNodeIdStr(nodeIdDst);

			Map<String, Object> wrappedMap = context.getWrappedMap();

			wrappedMap = wrappedMap.entrySet().stream().filter(e -> e.getKey().startsWith("$$")) //
					.collect(Collectors.toMap(e -> e.getKey().substring(2), Map.Entry::getValue));

			rslt = EvalService.evalNodeNoWeb(nodeDir1, wrappedMap);

		} catch (Exception e) {
			err = e;
		}

		String jobType = RFL.scn(NodeEvalJob.class);
		try {
			EventsStateTree nodeEvents = nodeDir.stateEventsTree();
			if (err == null) {
				String msg = "StoreEvent result";
				L.info(msg + "\n" + rslt);
				nodeEvents.store(jobType, rslt, null);
			} else {
				String msg = X.f("SrcNode %s error on inject", nodeIdSrc);
				L.error(msg, err);
				nodeEvents.store(jobType, msg, err);
			}
		} catch (Exception ex) {
			L.error(ERR.UNHANDLED_ERROR + " on storeEvent result", ex);
		}
//		finally {
//			TrackMap.clear(trackId);
//		}

//			ZkApp.pushMsg("EvalNode", e);


	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {

	}
}
