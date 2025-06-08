package zk_notes.events;

import mpc.env.Env;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpu.Sys;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_form.dirview.FileView;
import zk_form.dirview.SimpleDirView;
import zk_form.tree.CtxTreeView;
import zk_notes.ANI;

import java.nio.file.Path;

public class AppEventsFD extends AppEvents {


	//
	//

	public static Pare<String, SerializableEventListener> applyEvent_OPENDIR_OS(Component clickableSrc, Path path, String... event) {
		if (!UFS.exist(path) || !Env.isLocalDevMashine()) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir OS", SimpleDirView.getEventOpenSimpleMenu_OS(path)), clickableSrc, event);
	}
	public static Pare<String, SerializableEventListener> applyEvent_OPENDIR_TERMINAL(Component clickableSrc, Path path, String... event) {
		if (!UFS.exist(path) || !Env.isLocalDevMashine()) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir OS", SimpleDirView.getEventOpenSimpleMenu_Terminal(path)), clickableSrc, event);
	}
	public static Pare<String, SerializableEventListener> applyEvent_OPENDIR(Component clickableSrc, Path path, String... event) {
		if (!UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir", SimpleDirView.getEventOpenDirViewWithSimpleMenu(path)), clickableSrc, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENFILE(Component clickableSrc, Path path, String... event) {
		if (!UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir", FileView.getEventShowComInModal(path)), clickableSrc, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENTREE(Component clickableSrc, Path path, String... event) {
		if (!UFS.existFile(path)) {
			return null;
		}
		Pare<String, SerializableEventListener> enventDesc = Pare.of(ANI.OS_OPEN + " Open Tree " + UF.sfn(path).toUpperCase(), e -> CtxTreeView.openAsModalWindow(path));
		return apply(enventDesc, clickableSrc, event);
	}


	public static Pare<String, SerializableEventListener> applyEvent_OPEN_IN_CODE(Component clickableSrc, Path path, String... event) {
		return apply(Pare.of(ANI.OS_OPEN + " Open in Code", e -> Sys.open_Code(path)), clickableSrc, event);
	}

}
