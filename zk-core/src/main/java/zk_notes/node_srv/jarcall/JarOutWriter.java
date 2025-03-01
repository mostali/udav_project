package zk_notes.node_srv.jarcall;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpe.core.ERR;
import mpu.X;
import mpu.func.FunctionV1;
import mpu.func.FunctionV2;
import mpu.str.STR;
import org.zkoss.zk.ui.Component;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_notes.node_srv.SyncAsyncCall;
import zk_page.ZKRPush;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@RequiredArgsConstructor
public class JarOutWriter {

//		FunctionV2<String, String> writerState = !isExecutionEnable ? ZKRPush.getFuncWriteState(node, ZKI.Type.VOID) : ZKRPush.getFuncWriteState(activeExecutionCom, node, ZKI.Type.LOG);
//
//		FunctionV1<CharSequence> writerInfo = !isExecutionEnable ? ZKRPush.getFuncInfo(isReturn ? ZKI.Type.LOGBACK : ZKI.Type.MODAL_BW) : ZKRPush.getFuncInfo(activeExecutionCom, ZKI.Type.INFO_BOTTOM_RIGHT);
//		FunctionV2<Throwable, CharSequence> writerAlert = !isExecutionEnable ? ZKRPush.getFuncError(isReturn ? ZKI.Type.LOGBACK : ZKI.Type.ERR_EXT) : ZKRPush.getFuncError(activeExecutionCom, ZKI.Type.ERR_MB);

	public final FunctionV2<String, String> writerState;
	public final FunctionV1<CharSequence> writerInfo;
	public final FunctionV2<Throwable, CharSequence> writerAlert;

	private @Getter SyncAsyncCall syncAsyncCall;


	public JarOutWriter(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
		this.writerState = writerState;
		this.writerInfo = writerInfo;
		this.writerAlert = writerAlert;
	}

	public static class SyncRestJarOutWriter extends JarOutWriter {
		public SyncRestJarOutWriter(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
			super(writerState, writerInfo, writerAlert);
		}
	}

	public static class AsyncRestJarOutWriter extends JarOutWriter {
		public AsyncRestJarOutWriter(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
			super(writerState, writerInfo, writerAlert);
		}
	}

	public static JarOutWriter buildSyncWeb(NodeDir node) {
		FunctionV2<String, String> writerState = ZKRPush.getFuncWriteState(node, ZKI.Type.VOID);
		FunctionV1<CharSequence> writerInfo = ZKRPush.getFuncInfo(ZKI.Type.MODAL_BW);
		FunctionV2<Throwable, CharSequence> writerAlert = ZKRPush.getFuncError(ZKI.Type.ERR_EXT);
		return new JarOutWriter(writerState, writerInfo, writerAlert).syncAsyncCall(SyncAsyncCall.WEB_SYNC);
	}

	public static JarOutWriter buildAsyncWeb(NodeDir node, Component activeExecutionCom) {
		FunctionV2<String, String> writerState = ZKRPush.getFuncWriteState(activeExecutionCom, node, ZKI.Type.LOG);
		FunctionV1<CharSequence> writerInfo = ZKRPush.getFuncInfo(activeExecutionCom, ZKI.Type.INFO_BOTTOM_RIGHT);
		FunctionV2<Throwable, CharSequence> writerAlert = ZKRPush.getFuncError(activeExecutionCom, ZKI.Type.ERR_MB);
		return new JarOutWriter(writerState, writerInfo, writerAlert).syncAsyncCall(SyncAsyncCall.WEB_ASYNC);
	}

	@SneakyThrows
	public static JarOutWriter buildSyncRest(NodeDir node) {
		FunctionV2<String, String> writerState = ZKRPush.getFuncWriteState(node, ZKI.Type.LOGBACK);
		FunctionV1<CharSequence> writerInfo = ZKRPush.getFuncInfo(ZKI.Type.LOGBACK);
		FunctionV2<Throwable, CharSequence> writerAlert = ZKRPush.getFuncError(ZKI.Type.LOGBACK);
		return new SyncRestJarOutWriter(writerState, writerInfo, writerAlert).syncAsyncCall(SyncAsyncCall.REST_SYNC);
	}

	JarOutWriter syncAsyncCall(SyncAsyncCall syncAsyncCall) {
		this.syncAsyncCall = syncAsyncCall;
		return this;
	}

	@SneakyThrows
	public static JarOutWriter buildAsyncRest(NodeDir node, HttpServletResponse response) {
		FunctionV1<CharSequence> writerCom = (data) -> {
			try {
//				JarCallService.L.info("writerCom:" + data);
				response.getWriter().write("INFO:" + String.valueOf(data));
				response.getWriter().write(STR.NL);
				response.flushBuffer();
//					response.getWriter().flush();
			} catch (IOException e) {
				X.throwException(e);
			}
		};
		FunctionV2<String, String> writerState = (ok, err) -> {
			if (X.notEmpty(ok)) {
				writerCom.apply(ok);
			}
			if (X.notEmpty(err)) {
				writerCom.apply(err);
			}
		};
		FunctionV1<CharSequence> writerInfo = (info) -> writerCom.apply(info);
		FunctionV2<Throwable, CharSequence> writerAlert = (err, head) -> {
			writerCom.apply("ALERT:" + head);
			writerCom.apply(ERR.getStackTrace(err));
		};
		return new AsyncRestJarOutWriter(writerState, writerInfo, writerAlert).syncAsyncCall(SyncAsyncCall.REST_ASYNC);
	}

	public void state(String okData, String errData) {
		writerState.apply(okData, errData);
	}

	public void info(CharSequence okData) {
		writerInfo.apply(okData);
	}

	public void alert(Throwable err, CharSequence head) {
		writerAlert.apply(err, head);
	}
}
