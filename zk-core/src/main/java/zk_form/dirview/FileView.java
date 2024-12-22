package zk_form.dirview;

import lombok.SneakyThrows;
import mpu.X;
import mpc.fs.ext.EXT;
import mpc.fs.ext.GEXT;
import mpc.str.sym.FD_ICON;
import mpc.str.sym.SYMJ;
import mpv.byteunit.ByteUnit;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.*;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.win.EventShowComInModal;
import zk_form.notify.ZKI_Log;

import java.nio.file.Path;
import java.nio.file.Paths;

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
		String pathStr = path.toString();
		EXT ext = EXT.of(path);

		EventShowComInModal eventOpenFileInModal = null;

		long fileSizeMb = X.sizeOf(path, ByteUnit.MB);
		boolean bigFile = fileSizeMb > FILE_MAX_SIZE_MB;

		if (!bigFile) {

			if (GEXT.IMG.has(ext)) {
				eventOpenFileInModal = new EventShowComInModal(path.toString(), new Img(path));
			} else if (GEXT.EDITABLE.has(ext)) {
				eventOpenFileInModal = new EventShowComInModal(path.toString(), new Tbxm(path, Tbx.DIMS.WH100).saveble());
			} else if (GEXT.AUDIO.has(ext)) {
				eventOpenFileInModal = new EventShowComInModal(path.toString(), new Mp3(path));
			} else if (GEXT.VIDEO.has(ext)) {
//				Video video = (Video) ZulLoader.loadComponent("<video src=\"zk.mp4\" controls=\"true\" autoplay=\"true\" loop=\"true\"/>");
				eventOpenFileInModal = new EventShowComInModal(path.toString(), new Lb("need impl"));

			} else {
				Div0 divWith = new Div0();
				divWith.setVflex("1");
				Lb lb = new Lb("What is type '%s'?", pathStr);
				Bt btOpen = new Bt("Open");
				btOpen.onCLICK((SerializableEventListener<Event>) event -> {
					Tbx writable = new Tbxm(Paths.get(pathStr), Tbx.DIMS.WH100).saveble();
					divWith.appendChild(writable);
					btOpen.detach();
					lb.detach();
				});
				divWith.appendChild(lb);
				divWith.appendChild(btOpen);
				eventOpenFileInModal = new EventShowComInModal(path.toString(), divWith);
			}
		}

		String filePfx = filePfx(level);
		appendLb(filePfx);

		Lb fileRoot = appendLb(FD_ICON.toNameFile(path));

		if (applierFileMenu != null) {
			Menupopup0 menupopup = Menupopup0.createMenupopup(this, fileRoot, null);
			applierFileMenu.apply(menupopup, path);
		}

		appendCbFdView();

		if (!bigFile) {
			fileRoot.onDblClick(eventOpenFileInModal);//file dbl-click
		} else {
			fileRoot.onDblClick(event -> ZKI_Log.alert(SYMJ.WARN + "File has size %sMb more that %sMb", fileSizeMb, FILE_MAX_SIZE_MB));
		}

	}

}
