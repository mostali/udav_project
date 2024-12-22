package zk_page;

import lombok.SneakyThrows;
import mpu.core.ARG;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;

public class ZKRPush {

	public static void activePush(boolean... enable) {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (!desktop.isServerPushEnabled()) {
			desktop.enableServerPush(ARG.isDefNotEqFalse(enable));
		}
	}

	@SneakyThrows
	public static void activePushCom(Desktop desktop) {
		Executions.activate(desktop);
	}

	@SneakyThrows
	public static void deactivePushCom(Desktop desktop) {
		Executions.deactivate(desktop);
	}

}
