package zk_com.base_ext;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base_ctr.Div0;
import zk_page.ZKC;

public class GrayScreenCatcher extends Div0 {
	@Override
	protected void init() {
		super.init();
		setSTYLE("width:100%;height:100%;position:absolute;left:0px;top:0px;z-index:10000;background-color:gray;opacity:.2");
		onCLICK((SerializableEventListener<Event>) event -> {
			onCatch(event);
		});
	}

	protected void onCatch(Event event) {
		doClose();
	}

	protected void doClose() {
		ZKC.removeMeReturnParentWithEffect(this);
	}
}
