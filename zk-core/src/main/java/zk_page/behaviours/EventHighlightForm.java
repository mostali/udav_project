package zk_page.behaviours;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Span;
import org.zkoss.zul.impl.XulElement;
import zk_page.ZKS;

import java.util.Collection;

public class EventHighlightForm implements SerializableEventListener {
	final XulElement target;
	final boolean over_out;

	private EventHighlightForm(XulElement target, boolean over_out) {
		this.target = target;
		this.over_out = over_out;
	}

	public EventHighlightForm(boolean over_out) {
		this(null, over_out);
	}

	Component alreadyAdded = null;

	@Override
	public void onEvent(Event event) throws Exception {
		event.stopPropagation();
		XulElement target = this.target != null ? this.target : (XulElement) event.getTarget();
		if (over_out) {
			String style = "border: 2px dashed #0093f9;";
			if (target instanceof Span) {
				style += "display:inline-block";
			}
			if (alreadyAdded != target) {
				ZKS.addStyle(target, style);
				alreadyAdded = target;
			}
		} else {
			ZKS.rmStyleAttr(target, "display");
			ZKS.rmStyleAttr(target, "border");
			alreadyAdded = null;
		}
//		target.invalidate();
	}

	public static void applyOnOff_MouseOverOut(Component component, boolean... forChilds) {
		if (ARG.isDefEqTrue(forChilds)) {
			applyOnOff_MouseOverOut(component.getChildren());
		} else {
			applyOnOff_MouseOverOut(component, forChilds);
		}
	}

	public static void applyOnOff_MouseOverOut(Collection<Component> components) {
		components.forEach(EventHighlightForm::applyOnOff_MouseOverOut);
	}

	public static void applyOnOff_MouseOverOut(Component component) {
		component.addEventListener(Events.ON_MOUSE_OVER, new EventHighlightForm(true));
//		component.addEventListener(Events.ON_MOUSE_OUT, new EventHighlightForm(false));
	}


}
