package zk_notes.events;

import mpc.env.Env;
import mpc.fs.UFS;
import mpu.Sys;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_form.dirview.FileView;
import zk_form.dirview.SimpleDirView;
import zk_notes.ANI;

import java.nio.file.Path;

public class AppEventsFD extends AppEvents {


	//
	//

	public static Pare<String, SerializableEventListener> applyEvent_OPENDIR_OS(Component com, Path path, String... event) {
		if (!UFS.exist(path) || !Env.isLocalDevMashine()) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir OS", SimpleDirView.getEventOpenSimpleMenu_OS(path)), com, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENDIR(Component com, Path path, String... event) {
		if (!UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir", SimpleDirView.getEventOpenDirViewWithSimpleMenu(path)), com, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENFILE(Component com, Path path, String... event) {
		if (!UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir", FileView.getEventShowComInModal(path)), com, event);
	}


	public static Pare<String, SerializableEventListener> applyEvent_OPEN_IN_CODE(Component com, Path path, String... event) {
		return apply(Pare.of(ANI.OS_OPEN + " Open in Code", e -> Sys.open_Code(path)), com, event);
	}

}
