package mpf.contract;

import mpu.X;
import mpc.rfl.RFL;

import java.lang.reflect.Method;

public class CMethodException extends RuntimeException {
	public CMethodException(Method method, String reason, Object... args) {
		super(messageOf(method, reason, args));
	}

	public CMethodException(Throwable cause, Method method, String reason, Object... args) {
		super(messageOf(method, reason, args), cause);
	}

	private static String messageOf(Method method, String reason, Object[] args) {
		return RFL.toString(method) + " " + X.f(reason, args);
	}

	public static CMethodException newException(Method method, Exception ex, String msg, Object... args) {
		return new CMethodException(ex, method, msg, args);

	}
}
