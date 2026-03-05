package zk_com.editable;

import mpc.fs.UDIR;
import mpc.fs.UFS;
import mpu.core.ARG;
import mpu.func.FunctionV1;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Textbox;
import zk_com.base_ctr.Div0;
import zk_form.notify.ZKI;
import zk_page.ZKC;
import zk_page.ZKR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class RenameFileTextbox extends Textbox {

	public final String file;
	public final FunctionV1 successCallback;
	public transient Path path;

	public RenameFileTextbox(String file, FunctionV1... successCallback) {
		this(Paths.get(file), successCallback);
	}

	public RenameFileTextbox(Path file, FunctionV1... successCallback) {
		super(file.getFileName().toString());
		this.file = file.toString();
		this.path = file;
		this.successCallback = ARG.toDefOrNull(successCallback);
	}

	public static RenameFileTextbox buildCom(Path path, FunctionV1... successCallback) {
		return new RenameFileTextbox(path, successCallback);
	}

	public static Div0 buildComsForChildsDir(Path path, FunctionV1... successCallback) {
		RenameFileTextbox renameDir = new RenameFileTextbox(path);
		List renameInner = RenameFileTextbox.buildComs(UDIR.lsAll(path), successCallback);
		renameInner.add(0, renameDir);
		Div0 div = Div0.of(renameInner);
		return div;
	}

	public static List<RenameFileTextbox> buildComs(List<Path> lsAll, FunctionV1... successCallback) {
		List l = new LinkedList();
		lsAll.stream().forEach(p -> l.add(RenameFileTextbox.buildCom(p, successCallback)));
		return l;
	}


	public Path path() {
		return path == null ? Paths.get(file) : path;
	}

	public String name() {
		return path().getFileName().toString();
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {

		addEventListener(Events.ON_CANCEL, e -> ZKC.detachFirstParentWindow(this));
		addEventListener(Events.ON_OK, (SerializableEventListener) event -> onRename(event));

		setFocus(true);

//		ZKE.addEventListenerCtrl(this, Events.ON_CTRL_KEY, ZKE.SHORTCUT_STORE_ALT_V, (SerializableEventListener) event -> {
//			String newName = getValue();
//			try {
//				Path renamed = UFS_BASE.MV.rename(path(), newName, null);
//				ZKI.infoSingleLine("File '%s' renamed successfully to '%s'", name(), newName);
//				if (successCallback != null) {
//					successCallback.apply(renamed);
//				}
//			} catch (Exception ex) {
//				L.error("rename " + path(), ex);
//				if (AppZos.isDebugEnable()) {
//					ZKI.alert(ex);
//				}
//			}
//		});
	}

	protected void onRename(Event event) {
		Path src = path();
		String srcName = src.getFileName().toString();

		String dstName = getValue();
		Path dst = src.getParent().resolve(dstName);

		if (dstName.equals(srcName)) {
//			L.info("Existed file");
			return;
		} else if (UFS.exist(dst)) {
			ZKI.alert("Already exist '%s'", dstName);
			return;
		} else if (!UFS.exist(src)) {
			ZKI.alert("Not exist '%s'", srcName);
			return;
		}

		UFS.MV.rename(src, dstName, null);

		if (successCallback != null) {
			successCallback.apply(dstName);
			return;
		}

		ZKI.infoSingleLine("File '%s' renamed successfully to '%s'", name(), dstName);
		ZKR.restartPage();
	}

}
