package zk_com.tabs;

import mpc.console.ConsoleInput;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabpanel;

import java.util.Collection;

public class Tabpanel0 extends Tabpanel {

	public static final Logger L = LoggerFactory.getLogger(Tabpanel0.class);

	public Tabpanel0() {
		this(null);
	}

	public Object tab0as;
	public Tab0 tab0;

	public Tabpanel0(Object tab0as) {
		super();
		this.tab0as = tab0as;
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
//		X.p("Tp inited (no loaded):" + getClass().getSimpleName());
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
		Tab0 tab = getTab0(null);
//		if (tab != null) {
		tab.addEventListener(Events.ON_SELECT, eventSerializableEventListener);
//		}
	}

	public void onHappensEventSelect() {
	}

	public boolean equalsBy(String label) {
		Tab0 tab0 = getTab0(null);
		return tab0 != null && label.equals(tab0.getLabel());
	}

//	public boolean isInitedTab() {
//		return getTab0().inited();
//	}
//
//	public Tabpanel0 isInitedTab(boolean inited) {
//		getTab0().inited(inited);
//		return this;
//	}


	private Collection<Component> refreshableChildren;

	protected void clearLazyTabContent() {
		//not work native Children - happens error - ConcurrentModification
		//getChildren().forEach(c -> c.detach());
		if (X.notEmpty(refreshableChildren)) {
			refreshableChildren.forEach(c -> c.detach());
		}
		refreshableChildren = ARR.asAD();
	}

	protected void appendChildLazyTabContent(Component lazyCom) {
		if (refreshableChildren != null) {
			refreshableChildren.add(lazyCom);
		}
		appendChild(lazyCom);
	}

}
