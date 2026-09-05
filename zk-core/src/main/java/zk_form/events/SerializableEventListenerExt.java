package zk_form.events;

import lombok.SneakyThrows;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_os.AppZos;
import zk_form.notify.NotifyRef;

public interface SerializableEventListenerExt extends SerializableEventListener<Event> {

	@Override
	default void onEvent(Event event) throws Exception {
		try {
			onEventImpl(event);
		} catch (Exception ex) {
			AppZos.L.error("Intercept unwatchable error", ex);
//			ZKL.alert(ex);
//			Clients.alert(ex.getMessage());
//			ZKL.log("wtffff", ex);
//			ZKI.errorSingleLine(ex.getMessage());
//			ZKI.infoSingleLine(ex.getMessage());

			NotifyRef.ERR(ex.getMessage()).ref(event.getTarget()).show();
		}
	}

	@SneakyThrows
	void onEventImpl(Event event);
}
