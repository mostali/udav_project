package zk_com.base;


import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;
import zk_com.core.IZCom;

import java.util.Date;

public class Dtx extends Datebox implements IZCom {

	public Dtx addEventListener(EventListener<? extends Event> listener) {
		super.addEventListener(Events.ON_CLICK, listener);
		return this;
	}


	public Dtx() {

	}

	public Dtx(Date date) {
		super(date);
	}

//	public Dtx moldLongMedium() {
////		setMold("long+medium");
//		setValueInLocalDateTime(QDate.now().toLocalDateTime());
//		return this;
//	}
	public Dtx formatLongMedium() {
		setFormat("long+medium");
		return this;
	}

}
