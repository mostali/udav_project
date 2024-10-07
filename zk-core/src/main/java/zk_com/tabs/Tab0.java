package zk_com.tabs;

import mpc.exception.WhatIsTypeException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Tab;

public class Tab0 extends Tab {

	private boolean inited = false;

	public boolean inited() {
		return inited;
	}

	public Tab0 inited(boolean inited) {
		this.inited = inited;
		return this;
	}

	public Tab0() {
		super();
	}

	public Tab0(String label) {
		super(label);
	}

	public static Tab0 of(Object com) {
		if (com instanceof Tab0) {
			return (Tab0) com;
		} else if (com instanceof CharSequence || com instanceof Enum) {
			Tab0 tab = new Tab0(com.toString());
			return tab;
		} else if (com instanceof Component) {
			Tab0 tab = new Tab0();
			tab.appendChild((Component) com);
			return tab;
		}
		throw new WhatIsTypeException(com.getClass());
	}

	public void onEventSelect(SerializableEventListener<Event> eventSerializableEventListener) {
		addEventListener(Events.ON_SELECT, eventSerializableEventListener);
	}
}
