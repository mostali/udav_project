package zk_form.dirview;

import lombok.SneakyThrows;
import mpu.X;
import mpc.str.sym.FD_ICON;
import mpc.str.sym.SYMJ;
import mpv.byteunit.ByteUnit;
import zk_com.base.*;
import zk_com.base_ctr.Menupopup0;
import zk_com.win.EventShowFileComInModal;
import zk_form.SLib;
import zk_form.notify.ZKI;
import zk_os.sec.SecMan;

import java.nio.file.Path;

public class FileView extends FdView {

	public static final int FILE_MAX_SIZE_MB = 20;

	public FileView(Path file, int level) {
		super(file, level);
	}

	@SneakyThrows
	@Override
	protected void init() {
		super.init();

		Path path = path();

		EventShowFileComInModal eventOpenFileInModal = null;

		long fileSizeMb = X.sizeOf(path, ByteUnit.MB);
		boolean bigFile = fileSizeMb > FILE_MAX_SIZE_MB;

		if (!bigFile) {
			eventOpenFileInModal = getEventShowComInModal(path);
		}

		String filePfx = filePfx(level);
		appendLb(filePfx);

		Lb fileRoot = appendLb(FD_ICON.toNameFile(path));

		if (applierFileMenu != null && SecMan.isOwnerOrAdmin()) {
			Menupopup0 menupopup = Menupopup0.createMenupopup(this, fileRoot, null);
			applierFileMenu.apply(menupopup, path);
		}

		if (!bigFile) {
			fileRoot.onDBLCLICK(eventOpenFileInModal);//file dbl-click
		} else {
			fileRoot.onDBLCLICK(event -> ZKI.alert(SYMJ.WARN + "File has size %sMb more that %sMb", fileSizeMb, FILE_MAX_SIZE_MB));
		}

		appendCbFdView();

	}

	public static EventShowFileComInModal getEventShowComInModal(Path path) {
		return SLib.of(path).toEventShowInModal();
	}

}
