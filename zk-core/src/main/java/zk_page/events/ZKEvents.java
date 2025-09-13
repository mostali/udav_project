package zk_page.events;

import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_form.notify.ZKI;
import zk_page.ZKR;

@Deprecated
public class ZKEvents {
	public static SerializableEventListener getEventLogout() {
		return event -> ZKR.redirectToPage("/logout");
	}

	public static SerializableEventListener getEventLogin() {
		return event -> ZKR.redirectToPage("/login");
	}

	public static SerializableEventListener getEventRestartPageFull() {
		return event -> ZKR.redirectToPage("/logout");
	}

	public static SerializableEventListener getEventOpenUserSettings() {
		return (SerializableEventListener) -> ZKI.alert("Usr setting's need impl");
	}
}
