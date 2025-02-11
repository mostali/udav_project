package zk_notes.node_srv.jarcall;

import lombok.SneakyThrows;
import mpc.exception.MultiCauseException;
import mpc.fs.ext.EXT;
import mpc.log.LogTailReader;
import mpe.core.ERR;
import mpe.rt.SLEEP;
import mpe.rt.Thread0;
import mpe.wthttp.CleanDataResponseException;
import mpe.wthttp.JarCallMsg;
import mpu.IT;
import mpu.X;
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
import zk_notes.node_srv.TrackMap;
import zk_notes.node_state.FormState;
import zk_os.AFCC;
import zk_page.ZKRPush;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.List;

public class JarCallService {
	public static final Logger L = LoggerFactory.getLogger(KafkaCallService.class);

	private static @NotNull JarCallMsg prepareNodeJarCallMsg(NodeDir node) {
		FormState nodeState = node.state();
		String nodeData = nodeState.nodeData();
		nodeData = InjectNode.inject(node, nodeData, TrackMap.getTrackId());
		final JarCallMsg jarCallMsg = (JarCallMsg) JarCallMsg.of(nodeData, true).setFromSrc(node.id());
		if (jarCallMsg.getJarPath(null) == null) {
			List<Path> nodeJars = node.fLsEXT(EXT.JAR);
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

	public static Object doJarCallSyncRest_VALUE(NodeDir node, boolean allowNullValue) {
		Pare3<JarCallMsg, Object, Throwable> jarCallrslt = doJarCall(node, null, JarOutWriter.buildSyncRest(node));
		if (jarCallrslt.getExt() != null) {
			X.throwException(jarCallrslt.getExt());
		}
		Object value = jarCallrslt.val();
		return allowNullValue ? value : IT.NN(value, "Except not null value from jar call node '%s'", node.id());
	}

	public static Pare3<JarCallMsg, Object, Throwable> doJarCallSyncRest(NodeDir node) {
		return doJarCall(node, null, JarOutWriter.buildSyncRest(node));
	}

	@SneakyThrows
	public static CleanDataResponseException doJarCallAsyncRest(NodeDir node, HttpServletResponse response) {
		JarCallMsg jarCallMsg;
		int code = 200;
		try {
			jarCallMsg = prepareNodeJarCallMsg(node);

			Pare3<JarCallMsg, Object, Throwable> jarCallMsgObjectThrowablePare3 = doJarCall(node, jarCallMsg, JarOutWriter.buildAsyncRest(node, response));
			if (jarCallMsgObjectThrowablePare3.ext() != null) {
				code = 500;
			}
		} catch (Exception ex) {
			response.getWriter().write(ERR.getMessagesAsStringWithHead(ex, "doJarCallAsyncRest:"));
			code = 400;
		}
		response.setStatus(code);
		response.getWriter().close();
		throw new CleanDataResponseException("doJarCallAsyncRest").nothing();
	}

	private static Pare3<JarCallMsg, Object, Throwable> doJarCall(NodeDir node, JarCallMsg jarCallMsgPrepared, JarOutWriter writerOut) {

		if (writerOut.getSyncAsyncCall() == SyncAsyncCall.WEB_ASYNC) {
			ZKRPush.activePush();
			ZKI.showMsgBottomRightFast_INFO("Enable push for node [%s]", node.id());
		}

		FormState nodeState = node.state();

		FunctionV2<String, String> writerState = writerOut.writerState;
		FunctionV1<CharSequence> writerInfo = writerOut.writerInfo;
		FunctionV2<Throwable, CharSequence> writerAlert = writerOut.writerAlert;

		final JarCallMsg jarCallMsg = jarCallMsgPrepared != null ? jarCallMsgPrepared : prepareNodeJarCallMsg(node);

		try {

			nodeState.deletePathFc_OkErr();

			if (writerOut.getSyncAsyncCall() == SyncAsyncCall.REST_SYNC || jarCallMsg.isSync()) {
				//run in sync sync mode
				Object rsltObj = jarCallMsg.invokeJarMethod();
				String rsltStr = String.valueOf(rsltObj);

				writerState.apply(rsltStr, null);
				writerInfo.apply(rsltStr);

				return Pare3.of(jarCallMsg, rsltObj, null);

			} else {

				LogTailReader logTailReader = new LogTailReader();

				logTailReader.readNextTailLogLines();//init first part log

				Thread0 thread0 = new Thread0(() -> {

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
					writerInfo.apply(X.f("Call Jar STOPPED in thread [%s]. Node [%s]", node.id(), Thread0.getNameId(Thread.currentThread())));


				}, true);


				if (writerOut.getSyncAsyncCall() == SyncAsyncCall.REST_ASYNC) {
					thread0.join();
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
			String msg = X.f("Jar Call unhandled error with node '%s'", node.id());
			L.error(msg, ex);
			String messagesAsStringWithHead = ERR.getMessagesAsStringWithHead(ex, msg);
			writerState.apply(null, messagesAsStringWithHead);
			writerAlert.apply(ex, msg);
			return Pare3.of(jarCallMsg, null, ex);
		}

	}


}
