package zk_old_core.events;

import mpu.X;
import mpc.fs.UFS_BASE;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Messagebox;
import zk_form.notify.ZKI;
import zk_old_core.mdl.FormDirModel;
import zk_page.ZKR;

import java.nio.file.Path;

public class EventRmmForm implements SerializableEventListener {
	final String file;

	public EventRmmForm(Path rootFormPath) {
		file = rootFormPath.toString();
	}

	@Override
	public void onEvent(Event event) throws Exception {
		event.stopPropagation();
		SerializableEventListener onCloseEventListener = new SerializableEventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals(Messagebox.ON_YES)) {
					UFS_BASE.RM.removeQuicklyFileOrDir(file);
					ZKI.infoSingleLine("Removed");
					ZKR.restartPage();
				}
			}
		};
		FormDirModel fdm = FormDirModel.of(file);
		String message = X.f("Remove form '%s'?", fdm.name());
		Messagebox.show(message, "Removing...", new Messagebox.Button[]{Messagebox.Button.YES, Messagebox.Button.NO}, Messagebox.QUESTION, onCloseEventListener);
	}
}
