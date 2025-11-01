package zk_com.win;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_page.ZKC;
import zk_page.ZKM;

import java.util.function.Supplier;

public class EventShowFileComInModal implements SerializableEventListener {
	final Object title_cap_com;
	final Component parent;
	final Supplier<HtmlBasedComponent> modalComGetter;
	private final boolean darkTheme;

	public EventShowFileComInModal(Object title, Supplier<HtmlBasedComponent> modalComGetter, boolean... darkTheme) {
		this(title, ZKC.getFirstWindow(), modalComGetter, darkTheme);
	}

	public EventShowFileComInModal(Object title_cap_com, Component parent, Supplier<HtmlBasedComponent> modalComGetter, boolean... darkTheme) {
		this.title_cap_com = title_cap_com;
		this.parent = parent;
		this.modalComGetter = modalComGetter;
		this.darkTheme = ARG.isDefEqTrue(darkTheme);
	}

	@Override
	public void onEvent(Event arg0) throws Exception {

		ZKM.showModal(title_cap_com, modalComGetter.get(), parent, darkTheme);
//		ZKC.showModalZUL(title, modalCom, parent);
//		ModalWindowComposer.loadComponent(title, modalCom, parent);

//		Window win = (Window) Executions.createComponents(ModalWindowComposer.RSRC_ZUL, ZKC.getFirstWindow(), null);
//		Window win = (Window) Executions.createComponentsDirectly(RES.readString(ModalWindowComposer.RSRC_ZUL), null,ZKC.getFirstWindow(), null);
//		win.doModal();
	}

}
