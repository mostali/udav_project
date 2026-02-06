package zk_page;

import mpu.X;
import mpc.exception.WhatIsTypeException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.*;
import zk_form.events.DefAction;

import java.nio.file.Path;

public class ADDC {

	public static Cb CB(Component c, String label, Object... args) {
		Cb cb = new Cb(X.f(label, args));
		c.appendChild(cb);
		return cb;
	}

	public static Lb LB(Component c, String label, Object... args) {
		Lb lb = new Lb(X.f(label, args));
		c.appendChild(lb);
		return lb;
	}

	public static Img IMG(Component c, Object path_file_href) {
		Img lb = Img.of(path_file_href);
		c.appendChild(lb);
		return lb;
	}

	public static Ln LN(Component c, String name, Object href_Or_actionListener) {
		Ln ln = new Ln(name);
		if (href_Or_actionListener == null) {
			//ok
		} else if (href_Or_actionListener instanceof CharSequence) {
			ln.setHref(href_Or_actionListener.toString());
		} else if (href_Or_actionListener instanceof EventListener) {
			ln.addEventListener((EventListener) href_Or_actionListener);
		} else if (href_Or_actionListener instanceof DefAction) {
			ln.addEventListener((SerializableEventListener<Event>) event -> ((DefAction) href_Or_actionListener).onDefAction(event));
		} else {
			throw new WhatIsTypeException("Except LN href|eventListener|defAction, but cam:" + href_Or_actionListener);
		}
		c.appendChild(ln);
		return ln;
	}

	public static Bt BT(Component c, String name, Object href_listener_action) {
		Bt bt = new Bt(name);
		if (href_listener_action == null) {
			//ok
		} else if (href_listener_action instanceof CharSequence) {
			bt.setHref(href_listener_action.toString());
		} else if (href_listener_action instanceof EventListener) {
			bt.addEventListener(Events.ON_CLICK, (EventListener) href_listener_action);
		} else if (href_listener_action instanceof DefAction) {
			bt.addEventListener(Events.ON_CLICK, (SerializableEventListener<Event>) event -> ((DefAction) href_listener_action).onDefAction(event));
		} else {
			throw new WhatIsTypeException("Except BT href|eventListener|defAction, but cam:" + href_listener_action);
		}
		c.appendChild(bt);
		return bt;
	}

	public static Xml BR(Component c) {
		Xml xml = Xml.ofXml("<br/>");
		c.appendChild(xml);
		return xml;
	}

	public static Xml HR(Component c) {
		Xml xml = Xml.ofXml("<hr/>");
		c.appendChild(xml);
		return xml;
	}
}
