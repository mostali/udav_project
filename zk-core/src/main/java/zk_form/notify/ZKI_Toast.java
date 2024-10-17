package zk_form.notify;

import mpe.core.ERR;
import mpu.X;
import zk_com.win.HideBy;

public class ZKI_Toast {

	public static void info(CharSequence msg, Object... args) {
		NotifyPanel.ViewPosition.CENTER.show(X.f(msg, args), HideBy.DBL_CLICK);
	}

	public static void infoThrowable(Throwable error, CharSequence msg, Object... args) {
		NotifyPanel.ViewPosition.CENTER.show(X.f(msg, args) + "\n" + ERR.getStackTrace(error), HideBy.DBL_CLICK);
	}

	public static void infoBottomRight(CharSequence msg, Object... args) {
		NotifyPanel.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.DBL_CLICK);
	}

	public static void infoBottomRightFast(CharSequence msg, Object... args) {
		NotifyPanel.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.TIMEOUT_FAST);
	}

	public static void warnBottomRightFast(CharSequence msg, Object... args) {
		NotifyPanel.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.TIMEOUT_FAST, NtfLevel.WARN);
	}

	public static void errBottomRightFast(CharSequence msg, Object... args) {
		NotifyPanel.ViewPosition.BOTTOM_RIGHT.show(X.f(msg, args), HideBy.TIMEOUT_FAST, NtfLevel.ERR);
	}
}
