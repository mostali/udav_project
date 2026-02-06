package zk_notes.events;

import mpu.core.ARG;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base_ctr.Menupopup0;

public class AppEvents {

	public static Pare<String, SerializableEventListener> apply(Pare<String, SerializableEventListener> linkEvent, Component clickableSrc, String... event) {
		if (clickableSrc == null) {
			return linkEvent;
		} else if (clickableSrc instanceof Menupopup0) {
			Menupopup0 menupopup0 = (Menupopup0) clickableSrc;
			menupopup0.addMI(linkEvent.key(), linkEvent.val());
		} else {
			clickableSrc.addEventListener(ARG.toDefOr(Events.ON_CLICK, event), linkEvent.val());
		}
		return linkEvent;
	}
}
