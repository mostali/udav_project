package zk_form.notify;

import lombok.RequiredArgsConstructor;
import mpc.exception.NotifyMessageRtException;
import mpe.INotifictor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;

import java.io.Serializable;

@RequiredArgsConstructor
public class NotifictorCom implements INotifictor, Serializable {
	public final Component com;

	@Override
	public void notify(Object message) {
		Desktop desktop = com.getDesktop();
		if (desktop == null) {
			return;
		}
		try {
			Executions.activate(desktop);
			if (message instanceof Exception) {
				if (message instanceof NotifyMessageRtException) {
					ZKI_Window.info((NotifyMessageRtException) message);
				} else {
					ZKI_Log.alert(((Exception) message).getMessage());
				}
			} else {
				ZKI_Window.info(message == null ? "NULL" : message.toString());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			Executions.deactivate(desktop);
		}
	}
}
