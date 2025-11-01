package zk_com.core;

import mpu.core.ARG;
import mpu.str.RANDOM;
import mpu.func.FunctionV;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_page.ZKJS;
import zk_page.ZKS;

public interface IZComFadeIO {
	int RANDOM_ID_CLASS_LENGTH = 5;
	int ONBIND_TIMEOUT_MS = 30;

	String EVENTOUT = "onFadeEventOut";
	String EVENTIN = "onFadeEventIn";
	String CLASS_NOIN = "noin";
	String CLASS_EHIO = "ehio";
	String CLASS_AOUT = "aout";

	default void addEffectIn(HtmlBasedComponent view, FunctionV... callbackIn) {
		addEffectInImpl(view, callbackIn);
	}

	static void addEffectInImpl(HtmlBasedComponent view, FunctionV... callbackIn) {
		String classUniq = ZKS.classRnd(view, "spv_", RANDOM_ID_CLASS_LENGTH);
		ZKS.addClassAttr(view, CLASS_EHIO + " " + CLASS_NOIN);
//			view.setAction("show: slideDown({duration:1000})");

		boolean hasInCallbackIn = ARG.isDef(callbackIn);

		String funcIn = "";
		if (hasInCallbackIn) {
			funcIn = ZKJS.js_zauSend("." + classUniq, EVENTIN, "{}");
		}

		ZKJS.bindJS(view, ONBIND_TIMEOUT_MS, ZKJS.js_removeClass("." + classUniq, CLASS_NOIN) + funcIn);

		if (hasInCallbackIn) {
			view.addEventListener(EVENTIN, (SerializableEventListener<Event>) event -> ARG.toDef(callbackIn).apply());
		}
	}


	default void addEffectOut(HtmlBasedComponent view, String eventActionOut, FunctionV... callbackOut) {
		addEffectOut(view, view, eventActionOut, callbackOut);
	}

	default void addEffectOut(HtmlBasedComponent view, HtmlBasedComponent comActionOut, String comActionOutEvent, FunctionV... callbackOut) {
		addEffectOutImpl(view, comActionOut, comActionOutEvent, callbackOut);
	}

	static void addEffectOutImpl(HtmlBasedComponent view, HtmlBasedComponent comActionOut, String comActionOutEvent, FunctionV... callbackOut) {
		String viewId = RANDOM.alpha_num(RANDOM_ID_CLASS_LENGTH);
		ZKS.addClassAttr(view, viewId);
		view.addEventListener(EVENTOUT, (SerializableEventListener<Event>) event -> {
			ZKS.rmClassAttr(view, CLASS_EHIO + " " + CLASS_AOUT);
			if (ARG.isDef(callbackOut)) {
				ARG.toDef(callbackOut).apply();
			}
		});

		if (view != comActionOut) {
			ZKS.addClassAttr(comActionOut, viewId);
		}

		comActionOut.addEventListener(comActionOutEvent, (SerializableEventListener<Event>) event -> {
			ZKS.addClassAttr(view, CLASS_EHIO + " " + CLASS_AOUT);
			String jsonStr = "{}";
			ZKJS.eval_sendEvent("." + viewId, EVENTOUT, jsonStr);
		});
	}
}
