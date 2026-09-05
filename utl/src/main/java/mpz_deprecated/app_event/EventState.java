package mpz_deprecated.app_event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

public class EventState {
	private Multimap<AppEvent.Type, AppEvent> events;


	public Multimap<AppEvent.Type, AppEvent> getEvents() {
		return events != null ? events : (this.events = ArrayListMultimap.create());
	}

	public boolean has(AppEvent.Type type) {
		return events == null ? false : events.containsKey(type);
	}

	public Collection<AppEvent> get(AppEvent.Type type) {
		return events == null ? null : events.get(type);
	}
}
