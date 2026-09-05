package zk_com.win;

import lombok.Getter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Span;
import org.zkoss.zul.impl.XulElement;

import java.util.function.Supplier;

public class ModalComponentRender<VIEW extends XulElement, MODAL extends XulElement> extends Span {//HtmlMacroComponent

	@Getter
	final VIEW viewCom;

	@Getter
	final Supplier<MODAL> modalCom;

	public ModalComponentRender(Component parent, VIEW viewCom, Supplier<MODAL> modalComGetter, String title) {
		this.viewCom = viewCom;
		this.modalCom = modalComGetter;

		parent.appendChild(this);

		super.appendChild(this.viewCom);

		this.viewCom.addEventListener(Events.ON_CLICK, new EventShowFileComInModal(title, parent, (Supplier) modalComGetter));
	}

	protected void onAfterSize(Event event) {
	}
}


