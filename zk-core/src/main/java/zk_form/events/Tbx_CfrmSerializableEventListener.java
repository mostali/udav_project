package zk_form.events;

import lombok.RequiredArgsConstructor;
import mpu.core.ARR;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Mi;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0M;
import zk_page.ZKC;
import zk_page.ZKM;

import java.util.function.Function;

@RequiredArgsConstructor
public class Tbx_CfrmSerializableEventListener implements SerializableEventListener {

	public static Mi toMenuItemComponent(Object title_cap_com, String label, String initValue, Function<String, Object> handlerInputValue) {
		return toMenuItemComponent(title_cap_com, label, initValue, null, handlerInputValue);
	}

	public static Mi toMenuItemComponent(Object title_cap_com, String label, String initValue, String placeholder, Function<String, Object> handlerInputValue) {
		Mi lb = new Mi(label);
		lb.onCLICK(new Tbx_CfrmSerializableEventListener(title_cap_com, initValue, placeholder, handlerInputValue));
		return lb;
	}

	final Object title_cap_com;
	final String initText, placeholder;
	@NotNull
	final Function handlerInputValue;

	public static void openInModal(Function<String, Object> handlerInputValue1) {
		Tbx_CfrmSerializableEventListener tbxCfrmSerializableEventListener = new Tbx_CfrmSerializableEventListener(null, null, null, handlerInputValue1);
		Div0M.openModal(ARR.as());
	}

	@Override
	public void onEvent(Event event) throws Exception {
		Tbx tbx = (Tbx) new Tbx(Tbx.DIMS.W80).placeholder(placeholder);
		tbx.setValue(initText);
		tbx.onOK((SerializableEventListener<Event>) event1 -> {
			Object bool = handlerInputValue.apply(tbx.getValue());
			if (bool instanceof Boolean && !((Boolean) bool)) {
				return;
			}
			ZKC.removeParentWindowForChild(event1);
		});
		ZKM.showModal(title_cap_com, tbx, ZKC.getFirstWindow());
	}
}
