package mpc.env.boot;


import mpu.IT;
import mpc.net.INetRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AppErrorJournal {

	public static final Logger L = LoggerFactory.getLogger(AppErrorJournal.class);

	public static void ERROR(UUID uuid, Object ex) {
		IT.NN(ex, "set context");
		if (L.isErrorEnabled()) {
			if (ex == null) {
				L.error(ex.getClass().getSimpleName() + ": CError '" + uuid + "'");
			} else if (ex instanceof Throwable) {
				L.error("ERR: CError '" + uuid + "'", (Throwable) ex);
			} else if (ex instanceof INetRsp) {
				INetRsp iNetRsp = (INetRsp) ex;
				L.error(ex.getClass().getSimpleName() + ": CError '" + uuid + "' \n" + iNetRsp);
			} else {
				L.error(ex.getClass().getSimpleName() + ": CError '" + uuid + "' \n" + ex);
			}
		}
	}
}
