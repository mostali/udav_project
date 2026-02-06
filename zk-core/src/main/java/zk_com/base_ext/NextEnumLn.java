package zk_com.base_ext;

import mpu.core.ENUM;
import zk_com.base.Lb;
import zk_com.base.Ln;
import zk_form.notify.ZKI;

public class NextEnumLn<T extends Enum> extends Ln {
	final T[] evalView;

	public NextEnumLn(T... start) {
		super();
		evalView = start;
		Lb lb = new Lb(evalView[0].name());
		appendChild(lb);
		onCLICK(e -> {
			evalView[0] = ENUM.next(this.evalView[0]);
			lb.setValue(evalView[0].name());
			onNextEnum(evalView[0]);
//					switch (evalView[0]) {
//						case R:
//							ZKR.restartPage();
//							break;
//						default:
//					}
		});
	}

	protected void onNextEnum(T t) {
		ZKI.showMsgBottomRightFast_INFO("Enable view " + t);
	}

}
