package zk_form.notify;

import mpu.X;
import mpe.core.ERR;
import mpc.exception.WhatIsTypeException;
import mpt.TrmRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.Clients;
import zk_com.win.HideBy;

public class ZKI_Log {

	public static final Logger L = LoggerFactory.getLogger(ZKI_Log.class);
	public static final Logger ZLOG = new ZkLogger();
	public static final SerializableEventListener<? extends Event> ZLOG_EVENT = event -> log(event + "");


	public static void alert(Throwable err, CharSequence message, Object... args) {
		String msg = X.f(message, args);
		L.error(msg, err);
		Clients.alert(msg);
	}

	public static void alert(Throwable err) {
		L.error("alert", err);
		Clients.alert(ERR.getMessageWithType(err));
	}

	public static void alert(CharSequence message, Object... args) {
		Clients.alert(X.f(message, args));
	}

	public static void log(CharSequence msg, Object... args) {
		//Clients.evalJavaScript("zk.log('" + message.replaceAll("\'", "\\'") + "');");
		if (Executions.getCurrent() != null) {
			Clients.log(X.fa(msg, args));
		}
	}

	public static void status(TrmRsp.Status status, String str) {
		switch (status) {
			case OK:
				NotifyPanel.ViewPosition.CENTER.show(str, HideBy.DBL_CLICK);
				return;
			case ERR:
				ZKI_Modal.showMessageBoxBlue(str);
				return;
			case FAIL:
				alert(str);
				return;
			default:
				throw new WhatIsTypeException(status);
		}
	}


}
