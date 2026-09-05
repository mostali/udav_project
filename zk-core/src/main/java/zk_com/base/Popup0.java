package zk_com.base;

import mpu.core.ARR;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Popup;
import zk_form.notify.NotifyRef;
import zk_os.core.Sdn;
import zk_page.ZKC;
import zk_page.core.SpVM;

public class Popup0 extends Popup {

	public static @NotNull SerializableEventListener openPopupEventWithComLn(Component... coms) {
		Sdn sdn = SpVM.get().sdn();
		Popup popup = new Popup();
		for (int i = 0; i < coms.length; i++) {
			Component value = coms[i];
			popup.appendChild(value);
			if (!ARR.isLast(i, coms)) {
				popup.appendChild(Xml.NBSP(2));
			}
		}
		ZKC.getFirstWindow().appendChild(popup);
		SerializableEventListener eventHandler = ev -> popup.open(ZKC.getFirstWindow(), NotifyRef.Pos.at_pointer.name());
		return eventHandler;
	}
}
