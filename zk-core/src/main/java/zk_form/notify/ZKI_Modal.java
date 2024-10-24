package zk_form.notify;

import mpe.core.ERR;
import mpu.X;
import mpf.SimpleHandler;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Messagebox;

import java.util.function.Function;

public class ZKI_Modal {
	public static void showMessageBoxBlue(String message) {
		Messagebox.show(message);
	}

	public static void showMessageBoxRed(Throwable error, String message, Object... args) {
		showMessageBoxRed(X.f(message, args) + "\n" + ERR.getStackTrace(error));
	}

	public static void showMessageBoxRed(String message, Object... args) {
		Messagebox.show(X.f(message, args), null, 0, Messagebox.ERROR);
	}

	@Deprecated
	public static void showMessageBoxBlueYN_RMM(String title, String message, SimpleHandler handler) {
		Messagebox.show(message, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, (SerializableEventListener<Event>) evt -> {
			try {
				handler.handle(Messagebox.ON_YES.equals(evt.getName()));
			} catch (Exception e) {
				X.throwException(e);
			}
		});
	}

	public static void showMessageBoxBlueYN(String title, String message, Function<Boolean, Void> handler) {
		Messagebox.show(message, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, (SerializableEventListener<Event>) evt -> {
			try {
				handler.apply(Messagebox.ON_YES.equals(evt.getName()));
			} catch (Exception e) {
				X.throwException(e);
			}
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
}
