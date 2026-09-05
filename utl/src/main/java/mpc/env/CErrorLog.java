package mpc.env;

import mpu.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CErrorLog {
	public static final Logger L = LoggerFactory.getLogger(CErrorLog.class);

	public static void error(String msg, Object... args) {
		L.error(msg, args);
	}

	public static void error(Throwable cause, String msg, Object... args) {
		L.error(X.fl(msg, args), cause);
	}
}
