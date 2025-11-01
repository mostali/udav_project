package zk_form.events;

import lombok.RequiredArgsConstructor;
import mpu.core.ARRi;
import mpu.func.Function2;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.*;
import zk_com.base_ctr.Div0;
import zk_page.ZKC;
import zk_page.ZKM;

@RequiredArgsConstructor
public class Tbx2_CfrmSerializableEventListener implements SerializableEventListener {

	public static Mi toMI(String miLabel, String winTitle, String[] initValues, String[] placeholders, Function2<String, String, Object> handlerInputValues) {
		Mi lb = new Mi(miLabel);
		lb.onCLICK(new Tbx2_CfrmSerializableEventListener(winTitle, initValues, placeholders, handlerInputValues));
		return lb;
	}
	public static Bt toBt(String miLabel, String winTitle, String[] initValues, String[] placeholders, Function2<String, String, Object> handlerInputValues) {
		Bt lb = new Bt(miLabel);
		lb.onCLICK(new Tbx2_CfrmSerializableEventListener(winTitle, initValues, placeholders, handlerInputValues));
		return lb;
	}
	public static Ln toLn(String miLabel, String winTitle, String[] initValues, String[] placeholders, Function2<String, String, Object> handlerInputValues) {
		Ln lb = new Ln(miLabel);
		lb.onCLICK(new Tbx2_CfrmSerializableEventListener(winTitle, initValues, placeholders, handlerInputValues));
		return lb;
	}

	final String title;
	final String[] initValue, placeholder;
	@NotNull
	final Function2 handlerInputValue;

	@Override
	public void onEvent(Event event2) throws Exception {

		Tbx tbx = (Tbx) new Tbx(Tbx.DIMS.W80);
		tbx.setValue(ARRi.item(initValue, 0, ""));
		tbx.placeholder(ARRi.item(placeholder, 0, ""));

		Tbxm tbx2 = (Tbxm) new Tbxm(6, Tbx.DIMS.W80);
		tbx2.setValue(ARRi.item(initValue, 1, ""));
		tbx2.placeholder(ARRi.item(placeholder, 1, ""));

		SerializableEventListener<Event> eventOk = event1 -> {
			ZKC.removeParentWindowForChild(event1);
			handlerInputValue.apply(tbx.getValue(), tbx2.getValue());
		};

		tbx.onOK(eventOk);
		tbx2.onOK(eventOk);

		ZKM.showModal(title, Div0.of(tbx, tbx2), ZKC.getFirstWindow());
	}
}
