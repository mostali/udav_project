package zk_com.base_ext;

import mpu.core.ARRi;
import mpu.X;
import mpc.exception.FIllegalArgumentException;
import mpc.rfl.RFL;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import mpu.IT;
import zk_com.base.Lb;

import java.util.Collection;
import java.util.List;

public abstract class Bandbox0 extends Bandbox {
	public Bandbox0() {
		super();
	}


	public static Bandbox0 buildComponent(Collection elements) {
		return new Bandbox0() {

			@Override
			public void fillDropDownChild(Bandpopup dropdown) {
				if (X.empty(elements)) {
					return;
				}
				Object el = ARRi.first(elements, null);
				if (el == null) {
					return;
				}
				if (el instanceof CharSequence) {
					elements.stream().forEach(item -> {
						String vl = item.toString();
						Lb lb = Lb.line(vl);
						lb.addEventListener(Events.ON_CLICK, new SerializableEventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								onClickDropDownitem(item);
							}
						});
						dropdown.appendChild(lb);
					});
				} else if (el instanceof Component) {
					elements.stream().forEach(item -> dropdown.appendChild((Component) item));
				} else {
					throw new FIllegalArgumentException("Illegal type for ddItem ", RFL.scn(el));
				}
			}
		};
	}

	public void onClickDropDownitem(Object com) {
		setValue(com.toString());
		invalidate();
		close();
	}

	private Bandpopup bandpopup = null;

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected Bandbox0 init() {
		IT.isNull(bandpopup, "bandpopup already inited");
		appendChild(bandpopup = new Bandpopup());

		fillDropDownChild(getDropdown());

		return this;
	}

	public abstract void fillDropDownChild(Bandpopup dropdown);

}
