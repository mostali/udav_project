package zk_com.tabs;

import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.IT;
import mpu.core.ARG;
import org.bouncycastle.cert.ocsp.Req;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabpanel;

public class Tabpanel0 extends Tabpanel {

	public Tabpanel0() {
		this(null);
	}

	public final Object tab0as;
	public Tab0 tab0;

	public Tabpanel0(Object tab0as) {
		super();
		this.tab0as = tab0as;
	}

	public Tab0 getTab0(Tab0... defRq) {
		if (tab0 != null) {
			return tab0;
		} else if (tab0as != null) {
			return tab0 = Tab0.of(tab0as);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except component for tab0 OR tab0"), defRq);
	}

	public static Tabpanel0 of(Object head, Object com) {
		if (com instanceof Tabpanel0) {
			return (Tabpanel0) com;
		} else if (com instanceof CharSequence) {
			Tabpanel0 tab = new Tabpanel0(head);
			tab.appendChild(new Label(com.toString()));
			return tab;
		} else if (com instanceof Component) {
			Tabpanel0 tab = new Tabpanel0(head);
			tab.appendChild((Component) com);
			return tab;
		}
		throw new WhatIsTypeException(com.getClass());
	}

	public static Tabpanel0 of(Object com) {
		if (com instanceof Tabpanel0) {
			return (Tabpanel0) com;
		} else if (com instanceof CharSequence) {
			Tabpanel0 tab = new Tabpanel0();
			tab.appendChild(new Label(com.toString()));
			return tab;
		} else if (com instanceof Component) {
			Tabpanel0 tab = new Tabpanel0();
			tab.appendChild((Component) com);
			return tab;
		}
		throw new WhatIsTypeException(com.getClass());
	}

	public void onEventSelect(SerializableEventListener<Event> eventSerializableEventListener) {
		getTab0().addEventListener(Events.ON_SELECT, eventSerializableEventListener);
	}

	public boolean inited() {
		return getTab0().inited();
	}

	public Tabpanel0 inited(boolean inited) {
		getTab0().inited(inited);
		return this;
	}
}
