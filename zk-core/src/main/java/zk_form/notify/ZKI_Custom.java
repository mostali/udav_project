package zk_form.notify;

import mpe.core.ERR;
import mpu.X;
import zk_com.win.HideBy;

public class ZKI_Custom {

	public static void info(CharSequence msg, Object... args) {
		NotifyCustom.ViewPosition.CENTER.show(X.f(msg, args), HideBy.DBL_CLICK);
	}

	public static void infoThrowable(Throwable error, CharSequence msg, Object... args) {
		NotifyCustom.ViewPosition.CENTER.show(X.f(msg, args) + "\n" + ERR.getStackTrace(error), HideBy.DBL_CLICK);
	}

	public static void infoBottomRight(CharSequence msg, Object... args) {
		NotifyCustom.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.DBL_CLICK);
	}

	public static void infoBottomRightFast(CharSequence msg, Object... args) {
		NotifyCustom.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.TIMEOUT_FAST);
	}

	public static void warnBottomRightFast(CharSequence msg, Object... args) {
		NotifyCustom.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.TIMEOUT_FAST, ZKI.Level.WARN);
	}

	public static void errorBottomRightFast(CharSequence msg, Object... args) {
		NotifyCustom.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.TIMEOUT_FAST, ZKI.Level.ERR);
	}

	public static void showMsgBottomRightSlow(ZKI.Level level, CharSequence msg, Object... args) {
		NotifyCustom.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.TIMEOUT_SLOW, level);
	}
}
