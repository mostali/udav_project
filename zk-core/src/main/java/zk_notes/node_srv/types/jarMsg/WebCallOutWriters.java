package zk_notes.node_srv.types.jarMsg;

import mpe.core.ERR;
import mpe.call_msg.srv.CallMsgOut;
import mpu.X;
import mpu.func.FunctionV1;
import mpu.func.FunctionV2;
import mpu.str.STR;
import org.zkoss.zk.ui.Component;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_notes.node_state.ObjState;
import zk_page.ZKRPush;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebCallOutWriters {

//	public WebCallOutWriter(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
//		super(writerState, writerInfo, writerAlert);
//
//	}

	public static class RestSync_CallOutWriter extends CallMsgOut.RestSync_CallOutWriter0 {
		public RestSync_CallOutWriter(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
			super(writerState, writerInfo, writerAlert);
		}

		public static RestSync_CallOutWriter buildSyncRest(ObjState state) {
			FunctionV2<String, String> writerState = ZKRPush.getFuncWriteState(state, ZKI.ViewType.LOGBACK);
			FunctionV1<CharSequence> writerInfo = ZKRPush.getFuncInfo(ZKI.ViewType.LOGBACK);
			FunctionV2<Throwable, CharSequence> writerAlert = ZKRPush.getFuncError(ZKI.ViewType.LOGBACK);
			return new RestSync_CallOutWriter(writerState, writerInfo, writerAlert);
		}
	}

	public static class RestAsync_CallOutWriter extends CallMsgOut.RestAsync_CallOutWriter0 {

		public RestAsync_CallOutWriter(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
			super(writerState, writerInfo, writerAlert);
		}

		public static RestAsync_CallOutWriter build(NodeDir node, HttpServletResponse response) {
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
			return new RestAsync_CallOutWriter(writerState, writerInfo, writerAlert);
		}
	}

	public static class WebSync_CallOutWriter extends CallMsgOut.WebSync_CallOutWriter0 {
		public WebSync_CallOutWriter(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
			super(writerState, writerInfo, writerAlert);
		}

		public static WebSync_CallOutWriter build(NodeDir state) {
			FunctionV2<String, String> writerState = ZKRPush.getFuncWriteState(state, ZKI.ViewType.VOID);
			FunctionV1<CharSequence> writerInfo = ZKRPush.getFuncInfo(ZKI.ViewType.MODAL_BW);
			FunctionV2<Throwable, CharSequence> writerAlert = ZKRPush.getFuncError(ZKI.ViewType.MB_EXT_ERR);
			return new WebSync_CallOutWriter(writerState, writerInfo, writerAlert);
		}
	}

	public static class WebAsync_CallOutWriter extends CallMsgOut.WebAsync_CallOutWriter0 {
		public WebAsync_CallOutWriter(FunctionV2<String, String> writerState, FunctionV1<CharSequence> writerInfo, FunctionV2<Throwable, CharSequence> writerAlert) {
			super(writerState, writerInfo, writerAlert);
		}

		public static WebAsync_CallOutWriter build(NodeDir node, Component activeExecutionCom) {
			FunctionV2<String, String> writerState = ZKRPush.getFuncWriteState(activeExecutionCom, node.state(), ZKI.ViewType.LOG);
			FunctionV1<CharSequence> writerInfo = ZKRPush.getFuncInfo(activeExecutionCom, ZKI.ViewType.BR_INFO);
			FunctionV2<Throwable, CharSequence> writerAlert = ZKRPush.getFuncError(activeExecutionCom, ZKI.ViewType.MB_ERR);
			return new WebAsync_CallOutWriter(writerState, writerInfo, writerAlert);
		}
	}

}
