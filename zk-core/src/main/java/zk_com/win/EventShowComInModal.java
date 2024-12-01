package zk_com.win;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_page.ZKC;
import zk_page.ZKM;

public class EventShowComInModal implements SerializableEventListener {
	final Object title_cap_com;
	final Component parent;
	final HtmlBasedComponent modalCom;

	public EventShowComInModal(Object title, HtmlBasedComponent modalCom) {
		this(title, ZKC.getFirstWindow(), modalCom);
	}

	public EventShowComInModal(Object title_cap_com, Component parent, HtmlBasedComponent modalCom) {
		this.title_cap_com = title_cap_com;
		this.parent = parent;
		this.modalCom = modalCom;
	}

	@Override
	public void onEvent(Event arg0) throws Exception {
		ZKM.showModal(title_cap_com, modalCom, parent);
//		ZKC.showModalZUL(title, modalCom, parent);
//		ModalWindowComposer.loadComponent(title, modalCom, parent);

//		Window win = (Window) Executions.createComponents(ModalWindowComposer.RSRC_ZUL, ZKC.getFirstWindow(), null);
//		Window win = (Window) Executions.createComponentsDirectly(RES.readString(ModalWindowComposer.RSRC_ZUL), null,ZKC.getFirstWindow(), null);
//		win.doModal();
	}

}
