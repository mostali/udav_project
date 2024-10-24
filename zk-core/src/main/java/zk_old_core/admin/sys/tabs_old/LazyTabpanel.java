package zk_old_core.admin.sys.tabs_old;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Tabpanel;
import mpu.core.ARG;
import zk_com.core.LazyBuilder;

import java.io.Serializable;


@Deprecated
public class LazyTabpanel extends Tabpanel implements Serializable {

	private LazyBuilder lazyBuilder;

	public LazyTabpanel(LazyBuilder... lazyBuilder) {
		this.lazyBuilder = ARG.toDefOr(null, lazyBuilder);
		addEventListener(Events.ON_AFTER_SIZE, (SerializableEventListener<Event>) event -> getLazyBuilder().buildAndAppend(LazyTabpanel.this));
	}

	public LazyBuilder getLazyBuilder() {
		return lazyBuilder;
	}
}

