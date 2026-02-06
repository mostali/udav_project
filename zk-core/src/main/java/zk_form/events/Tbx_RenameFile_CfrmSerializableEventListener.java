package zk_form.events;

import lombok.RequiredArgsConstructor;
import mpc.str.sym.SYMJ;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Mi;
import zk_com.editable.RenameFileTextbox;
import zk_notes.ANI;
import zk_page.ZKC;
import zk_page.ZKM;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public class Tbx_RenameFile_CfrmSerializableEventListener implements SerializableEventListener {

	public static Mi toMenuItemComponent(String file, FunctionV1<String> successCallback) {
		return toMenuItemComponent(file, successCallback, false);
	}

	public static Mi toMenuItemComponent(String file, FunctionV1 successCallback, boolean renameChildsDir) {
		Mi lb = new Mi(ANI.RENAME + (renameChildsDir ? " Rename many" : " Rename"));
		lb.onCLICK(new Tbx_RenameFile_CfrmSerializableEventListener(file, successCallback, renameChildsDir));
		return lb;
	}

	final String file;
	final FunctionV1 successCallback;
	final boolean renameChildsDir;

	@Override
	public void onEvent(Event event) throws Exception {
		Path filePath = Paths.get(file);
		HtmlBasedComponent renameFileTextbox = renameChildsDir ? RenameFileTextbox.buildComsForChildsDir(filePath, successCallback) : RenameFileTextbox.buildCom(filePath, successCallback);
		ZKM.showModal(SYMJ.EDIT_PENCIL + " Rename '" + file + "'", renameFileTextbox, ZKC.getFirstWindow()).setClosable(true);
	}
}
