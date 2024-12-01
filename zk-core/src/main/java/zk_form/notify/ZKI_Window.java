package zk_form.notify;

import mpe.core.ERR;
import mpu.X;
import mpc.exception.NotifyMessageRtException;
import mpc.exception.WhatIsTypeException;
import mpe.INotifictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.zkoss.zk.ui.util.Clients;
import zk_os.AppZosConfig;
import zk_os.AppZosWeb;

public class ZKI_Window {

	public static final Logger L = LoggerFactory.getLogger(ZKI_Window.class);

	public static final INotifictor NOTIFYCATOR_LOG = new INotifictor() {
		@Override
		public void notify(Object message) {
			info(String.valueOf(message));
		}
	};

	public static void info(CharSequence msg, Object... args) {
		//		SpVM sp = null;
		//		if (Sec.secOn && (((sp = SpVM.get()) != null && sp.isUserAdmin()))) {
		if (!AppZosConfig.ZK_LOG_ENABLE) {
			L.debug(X.f(msg, args));
		} else {
			ZKI_Log.log(msg, args);
		}
	}

	public static void info(NotifyMessageRtException message) {
		NotifyMessageRtException nm = message;
		NotifyMessageRtException.LEVEL lev = nm.type();
		String causeMessageOrMessage = nm.getCauseMessageOrMessage();
		info(lev, causeMessageOrMessage);
	}

	public static void info(NotifyMessageRtException.LEVEL level, String causeMessageOrMessage) {
		switch (level) {
			case BLUE:
				ZKI_Modal.showMessageBoxBlue(causeMessageOrMessage);
				break;
			case GREEN:
				ZKI.infoSingleLine(causeMessageOrMessage);
				break;
			case RED:
				ZKI_Log.alert(causeMessageOrMessage);
				break;
			case LOG:
				info(causeMessageOrMessage);
				break;
			default:
				throw new WhatIsTypeException(level);
		}
	}

	public static void errorIQ(Throwable err) {
		if (err instanceof NotifyMessageRtException) {
			ZKI_Window.info((NotifyMessageRtException) err);
		} else {
			ZKI_Log.alert(err.getMessage());
		}
	}

	public static void errorIQ(Throwable err, String message, Object... args) {
		if (err instanceof NotifyMessageRtException) {
			ZKI_Window.info((NotifyMessageRtException) err);
		} else {
			ZKI_Log.alert(X.f(message, args) + "\n" + ERR.getStackTrace(err));
		}
	}

	public static void error(Throwable error, CharSequence msg, Object... args) {
		NtfLevel.ERR.toDivMsg(X.f(msg, args) + "\n" + ERR.getStackTrace(error))._popup()._modal()._showInWindow();
	}
}
