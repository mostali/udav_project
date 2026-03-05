package zk_form.notify;

import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.core.ARG;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Messagebox;

import java.util.function.Function;

public class ZKI_Quest {

	public static void showMessageBox(String title, String message, String messageboxType) {
		Messagebox.show(message, title, 0, messageboxType);
	}

	public static void showMessageBoxBlueYN(String title, String message, FunctionV1<Boolean> handler) {
		Messagebox.show(message, X.toStringNN(title, "Confirmation.."), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, (SerializableEventListener<Event>) evt -> {
			handler.apply(Messagebox.ON_YES.equals(evt.getName()));
		});
	}

	public static void showMessageBoxErrorYN(String title, String message, Function<Boolean, Void> handler) {
		Messagebox.show(message, title, Messagebox.YES | Messagebox.NO, Messagebox.ERROR, (SerializableEventListener<Event>) evt -> {
			try {
				handler.apply(Messagebox.ON_YES.equals(evt.getName()));
			} catch (Exception e) {
				X.throwException(e);
			}
		});
	}

	public static void showMessageBoxYNC_ofLevel(String title, String message, FunctionV1<Boolean> handler, String... levelMsgBox) {
		Messagebox.show(message, title, Messagebox.YES | Messagebox.NO | Messagebox.CANCEL, ARG.toDefOr(Messagebox.INFORMATION, levelMsgBox), (SerializableEventListener<Event>) evt -> {
			Boolean rslt;
			switch (evt.getName()) {
				case Messagebox.ON_YES:
					rslt = true;
					break;
				case Messagebox.ON_NO:
					rslt = false;
					break;
				case Messagebox.ON_CANCEL:
					rslt = null;
					break;
				default:
					throw new WhatIsTypeException(evt.getName());
			}
			handler.apply(rslt);
		});
	}
}
