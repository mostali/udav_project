package zk_com.win;

import mpc.exception.WhatIsTypeException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;
import org.zkoss.zul.ext.Framable;
import zk_page.ZKJS;

public enum HideBy {
	DEFAULT, DBL_CLICK, TIMEOUT_FAST, TIMEOUT_SLOW;

	public void apply(Component com) {
		switch (this) {
			case DBL_CLICK:
				com.addEventListener(Events.ON_DOUBLE_CLICK, (SerializableEventListener<Event>) event ->
				{
					ZKJS.eval_fadeOutBySelector("#" + com.getUuid(), 2000);
					if (com instanceof Framable) {
						if (com instanceof Window) {
							((Window) com).onClose();
						} else if (com instanceof Panel) {
							((Panel) com).onClose();
						} else {
							throw new WhatIsTypeException(com.getClass());
						}
					} else {
						com.detach();
					}
				});
				break;
			case TIMEOUT_SLOW:
			case TIMEOUT_FAST:
				//String javascript3 = "setTimeout(function(){ document.querySelector(\"." + clasZKS + "\").remove(); } , 3600);";
				int ms = this == HideBy.TIMEOUT_SLOW ? 12000 : 5000;
				String javascript3 = "setTimeout(function(){ jq(\"#" + com.getUuid() + "\").fadeOut(); } , " + ms + ");";
				ZKJS.eval(javascript3);
//				Sys.p(javascript3);
				break;
			case DEFAULT:
				break;
			default:
				throw new WhatIsTypeException(this);
		}
	}
}
