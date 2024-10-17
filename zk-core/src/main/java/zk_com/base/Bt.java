package zk_com.base;


import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Button;
import zk_com.core.IZCom;
import zk_page.ZKS;

/**
 * @author dav 12.01.2022   18:50
 */
public class Bt extends Button implements IZCom {

	public Bt() {
		this("ok");
	}

	public Bt(String label) {
		this(label, null, null);
	}

	public Bt(String label, String bgColor) {
		this(label, bgColor, null);
	}

	public Bt(String label, String bgColor, String color) {
		super(label);

		if (bgColor != null) {
			ZKS.BGCOLOR(this, bgColor);
		}
		if (color != null) {
			ZKS.COLOR(this, color);
		}
	}

	public static Bt of(String label, SerializableEventListener<Event> eventSerializableEventListener) {
		return new Bt(label).onCLICK(eventSerializableEventListener);
	}

	@Override
	public Bt onCLICK(EventListener listener) {
		return (Bt) IZCom.super.onCLICK(listener);
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	public void init() {
	}
}
