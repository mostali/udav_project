package zk_form.events;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import zk_com.base.Bt;
import zk_com.base.Mi;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Div0;
import zk_page.ZKC;
import zk_page.ZKM;

import java.util.function.Function;

@RequiredArgsConstructor
public class Tbxm_CfrmSEL implements SerializableEventListener {

	public static Mi toMenuItemComponent(Object title_cap_com, String label, String initValue, Function<String, Object> handlerInputValue) {
		return toMenuItemComponent(title_cap_com, label, initValue, null, handlerInputValue);
	}

	public static Mi toMenuItemComponent(Object title_cap_com, String label, String initValue, String placeholder, Function<String, Object> handlerInputValue) {
		Mi lb = new Mi(label);
		lb.onCLICK(new Tbxm_CfrmSEL(title_cap_com, initValue, placeholder, handlerInputValue));
		return lb;
	}

	final Object title_cap_com;
	final String initText, placeholder;
	@NotNull
	final Function handlerInputValue;

//	public static void openInModal(String title, String text, String placeholder, Function<String, Object> handlerInputValue1) {
//		Tbxm_CfrmSerializableEventListener tbxCfrmSerializableEventListener = new Tbxm_CfrmSerializableEventListener(title, text, placeholder, handlerInputValue1);
//		Div0M.openModal(ARR.as());
//	}

	public Window window;


	public void onEvent() {
		onEvent(null);
	}

	@Override
	public void onEvent(Event event) {
		Tbxm tbx = (Tbxm) new Tbxm(Tbx.DIMS.W80).placeholder(placeholder).height(300);
		Bt ok = new Bt("Ok").onCLICK(e -> handlerInputValue.apply(tbx.getValue()));
//		tbx.setValue(initText);
//		tbx.onOK((SerializableEventListener<Event>) event1 -> {
//			ZKC.removeParentWindowForChild(event1);
//			handlerInputValue.apply(tbx.getValue());
//		});
		window = ZKM.showModal(title_cap_com, Div0.of(tbx, ok), ZKC.getFirstWindow());
	}
}
