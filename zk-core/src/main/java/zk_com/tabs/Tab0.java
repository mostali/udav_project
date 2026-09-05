package zk_com.tabs;

import mpc.exception.WhatIsTypeException;
import mpu.IT;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Tab;
import zk_com.base.Lb;
import zk_com.core.IZCom;

public class Tab0 extends Tab implements IZCom {

//	private boolean inited = false;
//
//	public boolean inited() {
//		return inited;
//	}
//
//	public Tab0 inited(boolean inited) {
//		this.inited = inited;
//		return this;
//	}

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
			String name = null;
			if (com instanceof Lb) {
				name = ((Lb) com).getValue();
			}
			Tab0 tab = new Tab0(IT.NE(name, "set lb com"));
			tab.appendChild((Component) com);
			return tab;
		}
		throw new WhatIsTypeException(com.getClass());
	}

	public boolean equalsBy(String label) {
		return label.equals(getLabel());
	}

	//	public void onEventSelect(SerializableEventListener<Event> eventSerializableEventListener) {
//		addEventListener(Events.ON_SELECT, eventSerializableEventListener);
//	}
}
