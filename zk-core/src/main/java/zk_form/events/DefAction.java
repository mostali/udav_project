package zk_form.events;

import mpu.func.FunctionV1;
import org.zkoss.zk.ui.event.Event;

public interface DefAction extends FunctionV1<Event> {
	void onDefAction(Event event);

	default void apply(Event e) {
		onDefAction(e);
	}
}
