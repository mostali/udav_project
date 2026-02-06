package zk_notes.events;

import mpc.env.Env;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.str.sym.SYMJ;
import mpu.Sys;
import mpu.X;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_form.dirview.DirViewMenu;
import zk_form.dirview.FileView;
import zk_form.dirview.DirView0;
import zk_form.tree.CtxTreeView;
import zk_notes.ANI;
import zklogapp.logview.LogFileView;

import java.nio.file.Path;

public class AppEventsFD extends AppEvents {

	public static Pare<String, SerializableEventListener> applyEvent_LOG_SIMPLE_VIEW(Component clickableSrc, Path path, String... event) {
		if (!UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.LOGVIEW + " Log View", e -> LogFileView.openSingly("logs/server.log".toString())), clickableSrc, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENDIR_VIEW(Component clickableSrc, Path path, String... event) {
		if (!UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir", DirView0.getEventOpenDirViewWithSimpleMenu(path)), clickableSrc, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENFILE(Component clickableSrc, Path path, String... event) {
		if (!UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open File", FileView.getEventShowComInModal(path)), clickableSrc, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENTREE(Component clickableSrc, Path path, String... event) {
		if (!UFS.existFile(path)) {
			return null;
		}
		Pare<String, SerializableEventListener> enventDesc = Pare.of(SYMJ.FILE_DB + " Open Tree " + UF.sfn(path).toUpperCase(), e -> CtxTreeView.openAsModalWindow(path));
		return apply(enventDesc, clickableSrc, event);
	}


	//
	//

	public static Pare<String, SerializableEventListener> applyEvent_OPENDIR_OS(Component clickableSrc, Path path, String... event) {
		if (!Env.isLocalDevMashine() || !UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir - OS Manager", DirViewMenu.getEventOpenSimpleMenu_OS(path)), clickableSrc, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_OPENDIR_TERMINAL(Component clickableSrc, Path path, String... event) {
		if (!Env.isLocalDevMashine() || !UFS.exist(path)) {
			return null;
		}
		return apply(Pare.of(ANI.OS_OPEN + " Open Dir - OS Terminal", DirViewMenu.getEventOpenSimpleMenu_Terminal(path)), clickableSrc, event);
	}


	public static Pare<String, SerializableEventListener> applyEvent_OPEN_IN_CODE(Component clickableSrc, String overrideLabel, Path path, String... event) {
		return apply(Pare.of(X.toStringNE(overrideLabel, ANI.OS_OPEN + " Open in Code"), e -> Sys.open_Code(path)), clickableSrc, event);
	}

}
