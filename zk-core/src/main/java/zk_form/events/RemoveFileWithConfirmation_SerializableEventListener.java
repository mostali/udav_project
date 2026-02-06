package zk_form.events;

import lombok.RequiredArgsConstructor;
import mpc.fs.UFS;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Mi;
import zk_notes.ANI;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public class RemoveFileWithConfirmation_SerializableEventListener implements SerializableEventListener {

	public static Mi toMenuItemComponent(String file, DefAction successCallback) {
		Mi lb = new Mi(ANI.REMOVE + " Remove");
		lb.onCLICK(new RemoveFileWithConfirmation_SerializableEventListener(file, successCallback));
		return lb;
	}

	final String file;
	final DefAction successCallback;

	@Override
	public void onEvent(Event event) throws Exception {
		Path filePath = Paths.get(file);
		ZKI_Quest.showMessageBoxBlueYN("Remove..", "Remove '" + filePath.getFileName().toString() + "' ?", (rslt) -> {
			if (rslt) {
				UFS.RM.fdQk(filePath);
				ZKI.infoSingleLine("Removed '" + filePath.getFileName().toString() + "' ");
				if (successCallback != null) {
					successCallback.onDefAction(event);
				}
			}
		});
	}
}
