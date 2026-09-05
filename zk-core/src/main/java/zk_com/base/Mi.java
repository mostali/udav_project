package zk_com.base;


import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import zk_com.core.IZCom;
import zk_page.ZKS;

/**
 * @author dav 12.01.2022   18:50
 */
public class Mi extends Menuitem implements IZCom {

	public Mi addEventListener(EventListener<? extends Event> listener) {
		super.addEventListener(Events.ON_CLICK, listener);
		return this;
	}

	public Mi(String label) {
		this(label, null, null);
	}

	public Mi(String label, String href) {
		this(label, href, null);
	}

	public Mi(String label, String href, String bgColor) {
		this(label, href, bgColor, null);
	}

	public Mi(String label, String href, String bgColor, String color) {
		super(label);

		if (href != null) {
			setHref(href);
		}

		if (bgColor != null) {
			ZKS.BGCOLOR(this, bgColor);
		}
		if (color != null) {
			ZKS.COLOR(this, color);
		}
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);

//		ZKS.addSTYLE((XulElement) getFirstChild(), "background-color:" + ZKColor.REDS.nextColor());

	}
}
