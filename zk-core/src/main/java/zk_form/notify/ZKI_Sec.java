package zk_form.notify;

import zk_os.coms.AFCC;
import zk_os.AppZos;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class ZKI_Sec {

	public static void log(CharSequence msg, Object... args) {
		if (!AppZos.isDebugEnable()) {
			return;
		}
		ZKI.log(msg, args);
	}

	public static void infoBottomRightFast(String msg, Object... args) {
		if (AppZos.isDebugEnable()) {
			ZKI.showMsgBottomRightFast_INFO(msg, args);
		}
	}

	public static void alert(Exception ex) {
		if (AppZos.isDebugEnable()) {
			ZKI.alert(ex);
		} else {
			ZKI.alert(ex.getMessage());
		}

	}

	public static void alertFileNotFound(Exception ex) {
		if (!AppZos.isDebugEnable()) {
			ZKI.alert("App file component not found");
			return;
		}
		if (ex.getCause() != null && ex.getCause() instanceof NoSuchFileException) {
			NoSuchFileException err = (NoSuchFileException) ex.getCause();
			ZKI.alert("App File not found '%s'", AFCC.relativizeAppFile(Paths.get(err.getMessage())));
		} else {
			ZKI.alert(ex);
		}
	}
}
