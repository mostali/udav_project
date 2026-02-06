package zk_notes.node_srv.types.jarMsg;

import mpc.exception.MultiCauseException;
import mpc.log.LogTailReader;
import mpc.log.LogTailReaderThread0;
import mpe.call_msg.injector.TrackMap;
import mpe.core.ERR;
import mpe.core.U;
import mpe.rt.SLEEP;
import mpe.rt.Thread0;
import mpe.call_msg.JarCallMsg;
import mpe.call_msg.core.INode;
import mpe.call_msg.srv.CallMsgOut;
import mpu.X;
import mpu.core.ARG;
import mpu.func.*;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.types.kafkaMsg.KafkaECS;
import zk_notes.node_state.ObjState;
import mpe.call_msg.injector.NodeData;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.ZKRPush;
import zk_os.tasks.v1.TaskPanel_V1;

public class JarECS {

	public static final Logger L = LoggerFactory.getLogger(KafkaECS.class);

	public static Pare3<JarCallMsg, Object, Throwable> doJarCallSyncWeb(JarCallMsg jarCallMsg) {
		INode nodeDir = ((NodeData) jarCallMsg.getNode()).nodeDir;
		NodeDir node = (NodeDir) nodeDir;
		return doJarCall(jarCallMsg, WebCallOutWriters.WebSync_CallOutWriter.build(node));
	}

	public static void doJarCallAsyncWeb(JarCallMsg jarCallMsg, Component activeExecutionCom) {
		INode nodeDir = ((NodeData) jarCallMsg.getNode()).nodeDir;
		NodeDir node = (NodeDir) nodeDir;
		CallMsgOut writerOut = WebCallOutWriters.WebAsync_CallOutWriter.build(node, activeExecutionCom);
		doJarCall(jarCallMsg, writerOut);
	}

	public static class EVAL {

		public static Object doJarCallSyncRest_VALUE(NodeData<NodeDir> nodeData, Object... defRq) {

			NodeDir node = nodeData.nodeDir;

			ObjState formState = node.state();

			JarCallMsg jarCallMsg = JarCallMsg.of(nodeData);

			Pare3<JarCallMsg, Object, Throwable> jarCallrslt;
			if (jarCallMsg.isSync()) {
				jarCallrslt = doJarCall(jarCallMsg, WebCallOutWriters.RestSync_CallOutWriter.buildSyncRest(formState));
			} else {
				jarCallrslt = doJarCall(jarCallMsg, WebCallOutWriters.RestAsync_CallOutWriter.build(node, ZKR.getResponse()));
			}

			if (jarCallrslt.getExt() != null) {
				X.throwException(jarCallrslt.getExt());
			}

			//why it?
			if (jarCallMsg.isMainOrInvokeLinesMethod()) {
				return formState.readFcDataOk("Not found OK result:" + U.__NULL__);
			}

			Object value = jarCallrslt.val();
			return value != null ? value : ARG.toDefThrowMsg(() -> X.f("Except not null value from jar call node '%s'", node.nodeId()), defRq);
		}
	}

	private static Pare3<JarCallMsg, Object, Throwable> doJarCall(JarCallMsg jarCallMsg, CallMsgOut writerOut) {

		if (writerOut.getSyncAsyncCall() == CallMsgOut.SyncAsyncCallType.WEB_ASYNC) {
			ZKRPush.activePush();
			//ZKI.showMsgBottomRightFast_INFO("Enable push for node [%s]", node.nodeId());
		}

		FunctionV2<String, String> writerState = writerOut.getWriterState();
		FunctionV1<CharSequence> writerInfo = writerOut.getWriterInfo();
		FunctionV2<Throwable, CharSequence> writerAlert = writerOut.getWriterAlert();

		INode nodeDir = ((NodeData) jarCallMsg.getNode()).nodeDir;

		NodeDir node = (NodeDir) nodeDir;

		ObjState nodeState = node.state();

		try {

			nodeState.deletePathFc_OkErr();

			if (writerOut.getSyncAsyncCall() == CallMsgOut.SyncAsyncCallType.REST_SYNC || jarCallMsg.isSync()) {
				//run in sync sync mode
				Object rsltObj = jarCallMsg.invokeJarMethod();

				String rsltStr = String.valueOf(rsltObj);

				writerState.apply(rsltStr, null);
				writerInfo.apply(rsltStr);

				if (!jarCallMsg.isMainOrInvokeLinesMethod()) {
					return Pare3.of(jarCallMsg, rsltObj, null);

				}
				return Pare3.of(jarCallMsg, node.state().readFcDataOk("empty"), null);


			} else {

				LogTailReader logTailReader = LogTailReader.newLoggedTask();

				String tn = node.nodeId() + "//worker//" + Thread0.getNameSimple();

				LogTailReaderThread0 thread0 = new LogTailReaderThread0(tn, logTailReader, () -> {

					FunctionV readAndWriteState = () -> writerState.apply(JOIN.allByNL(logTailReader.readNextTailLogLines()), null);

					readAndWriteState.apply();//may be

					writerInfo.apply(X.f("Call Jar RUNNED in thread [%s]", Thread0.getNameWithId(Thread.currentThread())));

					Thread0 jarThread = jarCallMsg.invokeJarMethodAsync();

					readAndWriteState.apply();

					while (jarThread.isAlive()) {
						SLEEP.sec(2);
						readAndWriteState.apply();
					}

					readAndWriteState.apply();//last part

					if (jarThread.hasErrorsAny()) {
						writerAlert.apply(jarThread.getErrorsAsMultiException(), "Jar invoked with errors:");
					}

					String rsltInvokedObj = "INVOKE RESULT:" + jarThread.getResultObject(null);

					writerState.apply(rsltInvokedObj, null);
					writerInfo.apply(X.f("Call Jar STOPPED in thread [%s]. Node [%s]", node.nodeId(), Thread0.getNameWithId(Thread.currentThread())));


				}, true);

				TaskPanel_V1.addTaskD(node, thread0);

				Thread0.observe(thread0, 5, () -> {
					TaskPanel_V1.stopTaskLoggedD(thread0);
				});


				if (writerOut.getSyncAsyncCall() == CallMsgOut.SyncAsyncCallType.REST_ASYNC) {
					if (jarCallMsg.getAsyncWaitMs() == 0) {
						thread0.join();
					} else {
						thread0.join(jarCallMsg.getAsyncWaitMs());
					}
				}

				if (thread0.hasErrorsAny()) {
					MultiCauseException err = thread0.getErrorsAsMultiException();
					writerState.apply(null, ERR.getStackTrace(err));
					writerAlert.apply(err, "Jar call has unhandled errors:");
					return Pare3.of(jarCallMsg, null, err);
				}

				Object invokeRsltObject = thread0.getResultObject(null);
				if (thread0.hasResultAny()) {
					L.info("Jar Call END and has invoke result:\n" + invokeRsltObject);
				} else {
					L.info("Jar Call END without result");
				}
				return Pare3.of(jarCallMsg, invokeRsltObject, null);

			}

		} catch (Exception ex) {
			String msg = X.f("Jar Call unhandled error with node '%s'", node.nodeId());
			L.error(msg, ex);
			String messagesAsStringWithHead = ERR.getMessagesAsStringWithHead(ex, msg);
			writerState.apply(null, messagesAsStringWithHead);
			writerAlert.apply(ex, msg);
			return Pare3.of(jarCallMsg, null, ex);
		}

	}

	public static @NotNull JarCallMsg newCallMsg(NodeDir node, TrackMap.TrackId trackId) {
		NodeData nodeData = node.inject(trackId);
		return JarCallMsg.of(nodeData);
	}

	public static FunctionT<NodeDir, Object> evalFunction(TrackMap.TrackId trackId) {
		Component activePushComHolder = ZKC.getFirstWindow();
		FunctionT<NodeDir, Object> evalFunc = node -> {
			JarCallMsg jarCallMsg = newCallMsg(node, trackId);
			if (jarCallMsg.isSync()) {
				JarECS.doJarCallSyncWeb(jarCallMsg);
			} else {
				JarECS.doJarCallAsyncWeb(jarCallMsg, activePushComHolder);
			}
			return null;
		};
		return evalFunc;
	}
}
