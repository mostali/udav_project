package zk_com.base;


import mpu.core.ARG;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import zk_com.core.IZComExt;

public class  Win extends Window implements IZComExt {

	boolean closable = true;

	public Win closable(boolean... closable) {
		setClosable( ARG.isDefNotEqFalse(closable));
		return this;
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
		if (isClosable()) {
			setClosable(true);
			appendBt((SerializableEventListener<Event>) event -> onClose(), "X");
		}
	}
}
