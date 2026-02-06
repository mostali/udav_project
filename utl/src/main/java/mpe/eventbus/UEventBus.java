package mpe.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import mpu.Sys;
import mpu.core.ARG;
import mpu.pare.Pare;

import java.util.HashMap;
import java.util.Map;

public class UEventBus {

	private static Map<String, EventBus> eventBusMap;

	public static EventBus getEventBus(String... eventBusName) {
		if (eventBusMap == null) {
			eventBusMap = new HashMap<>();
		}
		String busName = ARG.toDefOr("default", eventBusName);
		return eventBusMap.computeIfAbsent(busName, (busNameKey) -> new EventBus(busNameKey));

	}

	public static void register(Object listener, String... eventBusName) {
		getEventBus(eventBusName).register(listener);
	}

	public static void post(Object object, String... eventBusName) {
		getEventBus(eventBusName).post(object);
	}

	public static void main(String[] args) throws InterruptedException {
		EventListener listener = new EventListener();
		getEventBus("ddd").register(listener);
		getEventBus("ddd").register(new EventListener());

		post(Pare.of("1", "2"), "ddd");
//		Thread.sleep(1000);
	}

	public static class EventListener {

		private static int eventsHandled;

		@Subscribe
		public static void stringEvent(Object event) {
			eventsHandled++;
			Sys.p(event);
		}
	}
}
