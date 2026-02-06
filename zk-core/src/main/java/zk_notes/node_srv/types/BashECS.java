package zk_notes.node_srv.types;

import mpc.str.sym.SEP;
import mpe.call_msg.BashCallMsg;
import mpe.call_msg.srv.CallMsgOut;
import mpu.X;
import mpu.core.QDate;
import mpu.func.FunctionV2;
import mpu.pare.Pare3;
import mpu.str.STR;
import org.jetbrains.annotations.Nullable;
import org.zkoss.zul.Window;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import mpe.call_msg.injector.TrackMap;
import zk_os.sec.UO;
import zk_os.tasks.TaskManager;
import zk_page.ZKC;
import zk_page.ZKRPush;

import java.util.function.Supplier;

//EvalCoreService
public class BashECS {

	public static @Nullable Object call(NodeDir node, TrackMap.TrackId trackId) {
//		try {
		UO.RUN.isAllowed(node, true);
//		} catch (FIllegalStateException ex) {
//			L.warn("!isAllowedRunProtectedMode", ex);
////						ZKI.alert(ex.getMessage());
//			ZKI.infoAfterPointer(ex.getMessage(), ZKI.Level.WARN);
//			return node;
//		}

		BashCallMsg callMsg;
//		if (true) {
		callMsg = BashCallMsg.of(node, trackId);
//		} else {
//			NodeData nodeData = node.nodeDataInjected(trackId);
//			callMsg = BashCallMsg.of(nodeData.nodeDataStr());
//			nodeData.setCallMsg(callMsg);
//			callMsg.setWorkDir(node.toPath());
//	}

		Window activePushCom = ZKC.getFirstWindow();

		CallMsgOut callOutWriter;

		FunctionV2<String, String> writerState;

		if (callMsg.isSync()) {

			writerState = ZKRPush.getFuncWriteState(node, ZKI.ViewType.LOG, 2);

//			FunctionV1<CharSequence> writerInfo = i -> {
//				ZKI.log(i);
//			};

//			FunctionV2<Throwable, CharSequence> writerAlert = (t, e) -> {
//				node.state().appendFcDataErr(e + "", t);
////				ZKI.log(i);
//				ZKI.log(ERR.getMessagesAsStringWithHead(t, e.toString()));
//			};


			callOutWriter = new CallMsgOut(CallMsgOut.SyncAsyncCallType.WEB_SYNC, writerState, null, null);

		} else {

			writerState = ZKRPush.getFuncWriteState(activePushCom, node, ZKI.ViewType.LOG, 2);

			ZKRPush.activePush();

			{
//				FunctionV2<String, String> writerInfo = ZKRPush.getFuncWriteState(activePushCom, node, ZKI.ViewType.LOG);

//				FunctionV1<CharSequence> writerInfo2 = i -> {
//					try {
//						ZKRPush.activePushCom(activePushCom);
//						ZKI.log(i);
//					} finally {
//						ZKRPush.deactivePushCom(activePushCom);
//					}
//				};

//				FunctionV2<Throwable, CharSequence> writerAlert = (t, e) -> ZKI.log(ERR.getMessagesAsStringWithHead(t, e.toString()));

				callOutWriter = new CallMsgOut(CallMsgOut.SyncAsyncCallType.WEB_ASYNC, writerState, null, null);

			}

		}

		Supplier<Pare3<Integer, String, String>> runner = () -> callMsg.invokeShFile_V3(callOutWriter, true);

		if (callMsg.isSync()) {
			Pare3<Integer, String, String> rsp = runner.get();
			String runnersp;
			String now = " ( " + QDate.now().f(QDate.F.MONO24NF_MS) + " )";
			if (rsp.key() == 0) {
//							return "OK:" + rsp.key() + STR.NL + rsp.val();
				runnersp = "OK:" + rsp.key() + now//
						+ STR.NL + SEP.DASH.__str1__("OUT") + STR.NL + rsp.val();
			} else {
				runnersp = "ERR:" + rsp.key() + now //
						+ STR.NL + SEP.DASH.__str1__("OUT") + STR.NL + rsp.val() //
						+ STR.NL + SEP.DASH.__str1__("ERR") + STR.NL + rsp.ext();
			}

			return runnersp;

		}

		String name = Thread.currentThread().getName();

		TaskManager.addTaskAsync(BashECS.class.getSimpleName() + "_" + X.toStringSE(name, 5), runner);

		return null;

	}
}
