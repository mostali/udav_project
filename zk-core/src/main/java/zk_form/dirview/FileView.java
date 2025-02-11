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
import zk_com.base_ext.Listbox0;
import zk_com.win.EventShowComInModal;
import zk_form.notify.ZKI;
import zk_notes.coms.SingleNodeVideo;
import zk_os.AFCC;
import zk_os.sec.Sec;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

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

		EventShowComInModal eventOpenFileInModal = null;

		long fileSizeMb = X.sizeOf(path, ByteUnit.MB);
		boolean bigFile = fileSizeMb > FILE_MAX_SIZE_MB;

		if (!bigFile) {
			eventOpenFileInModal = getEventShowComInModal(path);
		}

		String filePfx = filePfx(level);
		appendLb(filePfx);

		Lb fileRoot = appendLb(FD_ICON.toNameFile(path));

		if (applierFileMenu != null) {
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

	public static EventShowComInModal getEventShowComInModal(Path path) {
//		String title = path.toString();
		String pathStr = AFCC.relativizeAppFile(path, null);
		if (pathStr == null) {
			pathStr = path.getFileName().toString();
		}
		String title = pathStr;
		EXT ext = EXT.of(path);
		EventShowComInModal eventOpenFileInModal;
		if (GEXT.IMG.has(ext)) {
			eventOpenFileInModal = new EventShowComInModal(title, () -> new Img(path));
		} else if (GEXT.EDITABLE.has(ext)) {
			eventOpenFileInModal = new EventShowComInModal(title, () -> new Tbxm(path, Tbx.DIMS.WH100).saveble());
		} else if (path.getFileName().toString().endsWith(".props..")) {
			eventOpenFileInModal = new EventShowComInModal(title, () -> new Tbxm(path, Tbx.DIMS.WH100).prettyjson(true).saveble(), true);
		} else if (GEXT.AUDIO.has(ext)) {
			eventOpenFileInModal = new EventShowComInModal(title, () -> new Mp3(path));
		} else if (GEXT.VIDEO.has(ext)) {
//				Video video = (Video) ZulLoader.loadComponent("<video src=\"zk.mp4\" controls=\"true\" autoplay=\"true\" loop=\"true\"/>");
			eventOpenFileInModal = new EventShowComInModal(title, () -> new SingleNodeVideo(path.toFile()));

		} else if (ext == EXT.SQLITE) {
			eventOpenFileInModal = new EventShowComInModal(title, () -> Listbox0.fromDb(path, Sec.isAdminOrOwner()));
//			ZKM.showModal("Found " + X.sizeOf(maps) + " rows", modalCom, ZKC.getFirstWindow(), new String[]{"90%", null});
		} else {
			String pathStrFinal = pathStr;
			Supplier getter = () -> {
				Div0 divWith = new Div0();
				divWith.setVflex("1");
				Lb lb = new Lb("What is type '%s'?", pathStrFinal);
				Bt btOpen = new Bt("Open");
				btOpen.onCLICK((SerializableEventListener<Event>) event -> {
					Tbx writable = new Tbxm(Paths.get(pathStrFinal), Tbx.DIMS.WH100).saveble();
					divWith.appendChild(writable);
					btOpen.detach();
					lb.detach();
				});
				divWith.appendChild(lb);
				divWith.appendChild(btOpen);
				return divWith;
			};

			eventOpenFileInModal = new EventShowComInModal(title, getter);
		}
		return eventOpenFileInModal;
	}

}
