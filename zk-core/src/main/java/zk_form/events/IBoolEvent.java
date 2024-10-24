package zk_form.events;

import mpu.Sys;
import mpe.core.P;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;

import java.util.Map;

public interface IBoolEvent extends SerializableEventListener {


	@Override
	default void onEvent(Event event) throws Exception {
		onBoolEventImpl(this, event);
	}

	static void onBoolEventImpl(IBoolEvent srcEvent, Event event) throws Exception {

		Sys.say("bool eve");

		Map<String, Object> data = (Map) event.getData();
		P.p(data);
//
//		Integer x = (Integer) data.get("x");
//		x = UN.round20(x);
//
//		Integer y = (Integer) data.get("y");
//		y = UN.round20(y);
//
//		Boolean isCtrl = Boolean.parseBoolean(data.get("isCtrl") + "");
//		Boolean isShift = Boolean.parseBoolean(data.get("isShift") + "");


	}
}
