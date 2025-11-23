package zk_page;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.exception.WhatIsTypeException;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.func.FunctionV;
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
import zk_notes.node_state.ObjState;

import java.util.function.Supplier;

public class ZKRPush {

	public static final Logger L = LoggerFactory.getLogger(ZKRPush.class);

	public static <T> T newPushCollector(Component pushHolder, Supplier<T> action) {
		ZKRPush.activePushCom(pushHolder);
		try {
			return action.get();
		} finally {
			ZKRPush.deactivePushCom(pushHolder);
		}
	}

	public static PushAction newPushCollector(Component enablePushCom) {
		return new ZKRPush.PushAction(enablePushCom);
	}

//	public static PushAction newPushAction2log(Component pushHolder, String msg, Object... args) {
//		ZKRPush.activePushCom(pushHolder);
//		try {
//			ZKI.log2(X.f_(msg, args));
//		} finally {
//			ZKRPush.deactivePushCom(pushHolder);
//		}
//		new PushAction<>()
//	}

	@RequiredArgsConstructor
	public static class PushAction<T> {
		protected final Component pushHolder;

		public T action(Supplier<T> action) {
			ZKRPush.activePushCom(pushHolder);
			try {
				return action.get();
			} finally {
				ZKRPush.deactivePushCom(pushHolder);
			}
		}

		public void log(String msg, Object... args) {
			ZKRPush.activePushCom(pushHolder);
			try {
				ZKI.log2(X.f_(msg, args));
			} finally {
				ZKRPush.deactivePushCom(pushHolder);
			}

		}
	}

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

	public static void activePushCom(Component com, boolean... silent) {
		activePushCom(com.getDesktop(), silent);
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

	public static FunctionV wrapPushedAction(Component pushCom, FunctionV actionWithEnabledPush) {
		return () -> {
			try {
				ZKRPush.activePushCom(pushCom);
				actionWithEnabledPush.apply();
			} finally {
				ZKRPush.deactivePushCom(pushCom);
			}
		};
	}

	//
	//
	//

	public static void checkWebType(ZKI.ViewType type) {
		IT.state(!type.isWebType(), "For push or render component need active execution, but use %s", type);
	}


	public static FunctionV2<Throwable, CharSequence> getFuncError(ZKI.ViewType type) {
		return (err, head) -> type.showView(err, head);
	}

	public static FunctionV2<Throwable, CharSequence> getFuncError(Component pushCom, ZKI.ViewType type) {
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
 	//

	public static FunctionV1<CharSequence> getFuncInfo(ZKI.ViewType type) {
		return (msg) -> type.showView(msg);
	}

	public static FunctionV1<CharSequence> getFuncInfo(Component pushCom, ZKI.ViewType type) {
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
 	//

	public static FunctionV2<String, String> getFuncWriteState(NodeDir node, Integer... errMode0_1toOk_2okErr) {
		return getFuncWriteState(node.state(), null, errMode0_1toOk_2okErr);
	}

	public static FunctionV2<String, String> getFuncWriteState(NodeDir node, ZKI.ViewType type, Integer... errMode0_1toOk_2okErr) {
		return getFuncWriteState(null, node.state(), type, errMode0_1toOk_2okErr);
	}

	public static FunctionV2<String, String> getFuncWriteState(ObjState state, ZKI.ViewType viewType, Integer... errMode0_1toOk_2okErr) {
		return getFuncWriteState(null, state, viewType, errMode0_1toOk_2okErr);
	}

	public static FunctionV2<String, String> getFuncWriteState(Component com, NodeDir nodeDir, ZKI.ViewType viewType, Integer... errMode0_1toOk_2okErr) {
		return getFuncWriteState(com, nodeDir.state(), viewType, errMode0_1toOk_2okErr);
	}

	public static FunctionV2<String, String> getFuncWriteState(Component com, ObjState state, ZKI.ViewType viewType, Integer... errMode0_1toOk_2okErr) {
		int errMode0 = ARG.toDefOr(0, errMode0_1toOk_2okErr);
		FunctionV2<String, String> writerState = (okData, errData) -> {
			try {
				if (com != null) {
					ZKRPush.activePushCom(com);
				}
				if (X.notEmpty(okData)) {
					state.appendFcDataOk(okData + STR.NL);
					if (viewType != null) {
						viewType.showView(okData);
					}
				}
				if (X.notEmpty(errData)) {
					switch (errMode0) {
						case 0:
							state.appendFcDataErr(errData + STR.NL);
							break;
						case 1:
							state.appendFcDataOk(errData + STR.NL);
							break;
						case 2:
							state.appendFcDataErr(errData + STR.NL);
							state.appendFcDataOk(errData + STR.NL);
							break;
						default:
							throw new WhatIsTypeException("Undefined error mode: " + errMode0);
					}
					if (viewType != null) {
						viewType.showView(errData);
					}
				}
			} finally {
				if (com != null) {
					ZKRPush.deactivePushCom(com);
				}
			}
		};
		return writerState;
	}
}
