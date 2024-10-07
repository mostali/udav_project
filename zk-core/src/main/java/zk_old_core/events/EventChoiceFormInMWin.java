package zk_old_core.events;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.impl.XulElement;
import zk_old_core.old.mwin.MWin;
import zk_old_core.std.AbsVF;

@Deprecated
public class EventChoiceFormInMWin implements SerializableEventListener {
	final XulElement target;

	public EventChoiceFormInMWin(XulElement target) {
		this.target = target;
	}

	public EventChoiceFormInMWin() {
		this(null);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		event.stopPropagation();

		XulElement target = this.target != null ? this.target : (XulElement) event.getTarget();

		if (target instanceof AbsVF) {
			MWin.openForm((AbsVF) target);
		}
//		XulElement target = (XulElement) event.getTarget();
//		String style = "border: 3px solid #0093f9;";
//		if (target instanceof Span) {
//			style += "display:inline-block";
//		}
//		ZKC.addStyleAttr((XulElement) target.getParent().getParent(), style);
//		target.getParent().getParent().invalidate();
//		ZKS.addStyleAttr(target, style);
//		target.invalidate();
	}

	public static void applyOnDblClick(Component component) {
		component.addEventListener(Events.ON_DOUBLE_CLICK, new EventChoiceFormInMWin());
	}
}
