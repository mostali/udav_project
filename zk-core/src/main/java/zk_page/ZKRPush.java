package zk_page;

import lombok.SneakyThrows;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.func.FunctionV1;
import mpu.func.FunctionV2;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import zk_form.notify.ZKI;
import zk_notes.node.NodeDir;
import zk_notes.node_state.FormState;

public class ZKRPush {

	public static final Logger L = LoggerFactory.getLogger(ZKRPush.class);

	public static void activePush(boolean... enable) {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		activePush(desktop);
	}

	public static void activePush(Desktop desktop, boolean... enable) {
		if (!desktop.isServerPushEnabled()) {
			desktop.enableServerPush(ARG.isDefNotEqFalse(enable));
		}
	}
//	@SneakyThrows
//	public static void activePushCom(boolean... silent) {
//		activePushCom(ZKC.getFirstDesktop(null), silent);
//	}

	public static void activePushCom(Component desktop, boolean... silent) {
		activePushCom(desktop.getDesktop(), silent);
	}

	@SneakyThrows
	public static void activePushCom(Desktop desktop, boolean... silent) {
		try {
			Executions.activate(desktop);
		} catch (Exception ex) {
			if (ARG.isDefNotEqTrue(silent)) {
				throw ex;
			} else {
//				L.error("ActivePushCom", ex);
			}
		}
	}

//	@SneakyThrows
//	public static void deactivePushCom(boolean... silent) {
//		deactivePushCom(ZKC.getFirstDesktop(null), silent);
//	}

	@SneakyThrows
	public static void deactivePushCom(Component desktopHolder, boolean... silent) {
		deactivePushCom(desktopHolder == null ? null : desktopHolder.getDesktop(), silent);
	}

	public static void deactivePushCom(Desktop desktop, boolean... silent) {
		try {
			Executions.deactivate(desktop);
		} catch (Exception ex) {
			if (ARG.isDefNotEqTrue(silent)) {
				throw ex;
			} else {
				L.warn("DeactivePushCom:" + ex.getMessage());
			}
		}
	}

	//
	//
	//

	public static void checkWebType(ZKI.Type type) {
		IT.state(!type.isWebType(), "For push or render component need active execution, but use %s", type);
	}


	public static FunctionV2<Throwable, CharSequence> getFuncError(ZKI.Type type) {
		return (err, head) -> type.showView(err, head);
	}

	public static FunctionV2<Throwable, CharSequence> getFuncError(Component pushCom, ZKI.Type type) {
		FunctionV2<Throwable, CharSequence> pushInfo = (err, head) -> {
			try {
				ZKRPush.activePushCom(pushCom);
				type.showView(err, head);
			} finally {
				ZKRPush.deactivePushCom(pushCom);
			}
		};
		return pushInfo;
	}

	//
	//

	public static FunctionV1<CharSequence> getFuncInfo(ZKI.Type type) {
//		checkWebType(type);
		return (msg) -> type.showView(msg);
	}

	public static FunctionV1<CharSequence> getFuncInfo(Component pushCom, ZKI.Type type) {
		FunctionV1<CharSequence> pushInfo = (msg) -> {
			try {
				ZKRPush.activePushCom(pushCom);
				type.showView(msg);
			} finally {
				ZKRPush.deactivePushCom(pushCom);
			}
		};
		return pushInfo;
	}

	//
	//

	public static FunctionV2<String, String> getFuncWriteState(NodeDir node, ZKI.Type type) {
		return (okData, errData) -> {
			FormState state = node.state();
			if (X.notEmpty(okData)) {
				state.appendFcDataOk(okData + STR.NL);
				type.showView(okData);
			}
			if (X.notEmpty(errData)) {
				state.appendFcDataErr(errData + STR.NL);
				type.showView(errData);
			}
		};
	}

	public static FunctionV2<String, String> getFuncWriteState(Component com, NodeDir node, ZKI.Type type) {
		FunctionV2<String, String> writerState = (okData, errData) -> {
			try {
				ZKRPush.activePushCom(com);
				FormState state = node.state();
				if (X.notEmpty(okData)) {
					state.appendFcDataOk(okData + STR.NL);
					type.showView(okData);
				}
				if (X.notEmpty(errData)) {
					state.appendFcDataErr(errData + STR.NL);
					type.showView(errData);
				}
			} finally {
				ZKRPush.deactivePushCom(com);
			}
		};
		return writerState;
	}
}
