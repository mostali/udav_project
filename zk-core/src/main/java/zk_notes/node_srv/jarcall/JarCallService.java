package zk_notes.node_srv.jarcall;

import mpc.exception.MultiCauseException;
import mpc.fs.ext.EXT;
import mpc.fs.path.IPath;
import mpc.log.LogTailReader;
import mpc.log.LogTailReaderThread0;
import mpe.core.ERR;
import mpe.core.U;
import mpe.rt.SLEEP;
import mpe.rt.Thread0;
import mpe.wthttp.JarCallMsg;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.func.FunctionT;
import mpu.func.FunctionV;
import mpu.func.FunctionV1;
import mpu.func.FunctionV2;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.InjectNode;
import zk_notes.node_srv.KafkaCallService;
import zk_notes.node_srv.SyncAsyncCall;
import zk_notes.node_state.FormState;
import zk_os.AFCC;
import zk_page.ZKC;
import zk_page.ZKRPush;
import zk_os.tasks.TaskPanel;

import java.nio.file.Path;
import java.util.List;

public class JarCallService {

	public static final Logger L = LoggerFactory.getLogger(KafkaCallService.class);

	public static @NotNull JarCallMsg prepareInjectedNodeJarCallMsg(NodeDir node) {
		FormState nodeState = node.state();
		String nodeData = nodeState.nodeData();
		nodeData = InjectNode.inject(node).nodeData;
		final JarCallMsg jarCallMsg = (JarCallMsg) JarCallMsg.of(nodeData, true).setFromSrc(node.nodeId());
		if (jarCallMsg.getJarPath(null) == null) {
			List<Path> nodeJars = node.dLsEXT(EXT.JAR);
			if (X.empty(nodeJars)) {
				Path dir = jarCallMsg.getDir(null);
				if (dir != null) {
					if (dir.toString().startsWith("./")) {
						//only for relative paths
						Path checkDir = node.toPath().resolve(jarCallMsg.getDir());
						nodeJars = IPath.of(checkDir).dLsEXT(EXT.JAR);
					}
				}
			}
			IT.notEmpty(nodeJars, "Add jar to node, or set header param [dir] with optional param [jar.filename]");
			IT.isLength(nodeJars, 1, "Except only one jar file, but found '%s'", AFCC.relativizeAppFile(nodeJars));
			jarCallMsg.setJarPath(nodeJars.get(0));
		}
		return jarCallMsg;
	}

	public static Pare3<JarCallMsg, Object, Throwable> doJarCallSyncWeb(NodeDir node) {
		return doJarCall(node, null, JarOutWriter.buildSyncWeb(node));
	}

	public static void doJarCallAsyncWeb(NodeDir node, Component activeExecutionCom) {
		doJarCall(node, null, JarOutWriter.buildAsyncWeb(node, activeExecutionCom));
	}

	public static Object doJarCallSyncRest_VALUE(NodeDir node, Object... defRq) {

		Pare3<JarCallMsg, Object, Throwable> jarCallrslt = doJarCall(node, null, JarOutWriter.buildSyncRest(node));
		if (jarCallrslt.getExt() != null) {
			X.throwException(jarCallrslt.getExt());
		}
		JarCallMsg jarCallMsg = prepareInjectedNodeJarCallMsg(node);
		if (jarCallMsg.isMainOrDefMethod()) {
			return node.state().readFcDataOk(U.__NULL__);
		}
		Object value = jarCallrslt.val();
		if (value != null) {
			return value;
		}
		return ARG.toDefThrowMsg(() -> X.f("Except not null value from jar call node '%s'", node.nodeId()), defRq);
	}

	private static Pare3<JarCallMsg, Object, Throwable> doJarCall(NodeDir node, JarCallMsg jarCallMsgPrepared, JarOutWriter writerOut) {

		if (writerOut.getSyncAsyncCall() == SyncAsyncCall.WEB_ASYNC) {
			ZKRPush.activePush();
//			ZKI.showMsgBottomRightFast_INFO("Enable push for node [%s]", node.nodeId());
		}

		FormState nodeState = node.state();

		FunctionV2<String, String> writerState = writerOut.writerState;
		FunctionV1<CharSequence> writerInfo = writerOut.writerInfo;
		FunctionV2<Throwable, CharSequence> writerAlert = writerOut.writerAlert;

		final JarCallMsg jarCallMsg = jarCallMsgPrepared != null ? jarCallMsgPrepared : prepareInjectedNodeJarCallMsg(node);

		try {

			nodeState.deletePathFc_OkErr();

			if (writerOut.getSyncAsyncCall() == SyncAsyncCall.REST_SYNC || jarCallMsg.isSync()) {
				//run in sync sync mode
				Object rsltObj = jarCallMsg.invokeJarMethod();

				String rsltStr = String.valueOf(rsltObj);

				writerState.apply(rsltStr, null);
				writerInfo.apply(rsltStr);

				if (!jarCallMsg.isMainOrDefMethod()) {
					return Pare3.of(jarCallMsg, rsltObj, null);

				}
				return Pare3.of(jarCallMsg, node.state().readFcDataOk("empty"), null);


			} else {

				LogTailReader logTailReader = new LogTailReader();
				logTailReader.setSkipFirstPart(true);

				logTailReader.readNextTailLogLines();//init first part log

				String tn = node.nodeId() + "//worker//" + Thread0.getNameSimple();

				LogTailReaderThread0 thread0 = new LogTailReaderThread0(tn, logTailReader, () -> {

					FunctionV readAndWriteState = () -> writerState.apply(JOIN.allByNL(logTailReader.readNextTailLogLines()), null);

					readAndWriteState.apply();//may be

					writerInfo.apply(X.f("Call Jar RUNNED in thread [%s]", Thread0.getNameId(Thread.currentThread())));

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
					writerInfo.apply(X.f("Call Jar STOPPED in thread [%s]. Node [%s]", node.nodeId(), Thread0.getNameId(Thread.currentThread())));


				}, true);

				TaskPanel.addTaskD(node, thread0);

				Thread0.observe(thread0, 5, () -> {
					TaskPanel.stopTaskLoggedD(thread0);
				});


				if (writerOut.getSyncAsyncCall() == SyncAsyncCall.REST_ASYNC) {
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


	public static FunctionT<NodeDir, Object> evalFunction() {
		Component activePushComHolder = ZKC.getFirstWindow();
		FunctionT<NodeDir, Object> evalFunc = node -> {
			JarCallMsg jarCallMsg = JarCallMsg.ofQk(node.state().nodeData(), true);
			if (jarCallMsg.isSync()) {
				JarCallService.doJarCallSyncWeb(node);
//				JarCallService.doJarCallSyncRest_VALUE(node, U.__NULL__);
			} else {
				JarCallService.doJarCallAsyncWeb(node, activePushComHolder);
			}
			return null;
		};
		return evalFunc;
	}
}
