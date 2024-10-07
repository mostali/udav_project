package zk_com.win;

import lombok.Getter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Span;
import org.zkoss.zul.impl.XulElement;

public class ModalComponentRender<VIEW extends XulElement, MODAL extends XulElement> extends Span {//HtmlMacroComponent

	@Getter
	final VIEW viewCom;

	@Getter
	final MODAL modalCom;

	public ModalComponentRender(Component parent, VIEW viewCom, MODAL modalCom, String title) {
		this.viewCom = viewCom;
		this.modalCom = modalCom;

		parent.appendChild(this);

		super.appendChild(this.viewCom);

		this.viewCom.addEventListener(Events.ON_CLICK, new EventShowComInModal(title, parent, modalCom));
	}

	protected void onAfterSize(Event event) {
	}
}


