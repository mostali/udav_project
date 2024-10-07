package zk_com.uploader;

import mpu.core.ARGn;
import mpc.exception.FIllegalArgumentException;
import mpu.core.RW;
import mpc.fs.UFS;
import mpc.map.UMap;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.*;
import zk_os.AppZosCore;
import zk_form.notify.ZKI;
import zk_page.ZulLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.function.Function;

//https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Patterns/File_Upload_and_Download
public class FileUploaderComposer extends SelectorComposer implements IUploaderCom {

	public static final String ZUL_WEB_COM_SIMPLE_FILE_LOADER_CLIPBOARD_LOADER_ZUL = "/_com/simple-file-uploader/simple-file-uploader.zul";

	public static void loadComponent(String title, Path uploadTo, Component... parent) {
		loadComponent(title, uploadTo, null, parent);
	}

	public static void loadComponent(String title, Path uploadTo, Function<String, Boolean> successCallback, Component... parent) {
		Map context = UMap.of(AK_UPLOAD_TO_DIR, uploadTo.toString(), AK_UPLOAD_TO_TITLE, title, AK_SUCCESS_CALLBACK, successCallback);
		ZulLoader.loadComponentFromRsrc(ZUL_WEB_COM_SIMPLE_FILE_LOADER_CLIPBOARD_LOADER_ZUL, context, parent);
	}

	@Listen(Events.ON_CLICK + "= a")
	public void handleUpload(MouseEvent e) {
//		Fileupload.get(1, getEventUploadImpl(AppZosCore.getRpaUploadPath()));
		getEventOpenMenuUpload(AppZosCore.getRpaUploadPath().toString(), 1);
	}

	public static SerializableEventListener<UploadEvent> getEventOpenMenuUpload(String pathUploadTo, int... max) {
		return event -> Fileupload.get(ARGn.toDefOr(1, max), FileUploaderComposer.getEventUpload(pathUploadTo));
	}

	public static SerializableEventListener<UploadEvent> getEventUpload(String pathUploadTo) {
		SerializableEventListener<UploadEvent> uploadEvent = event -> {
			Media[] medias = event.getMedias();
			for (Media media : medias) {
				Path writedClpImg = Paths.get(pathUploadTo).resolve(medias[0].getName());
				if (UFS.existFile(writedClpImg)) {
					throw new FIllegalArgumentException("File already exist");
				}
				if (media.isBinary()) {
					RW.write_(writedClpImg, media.getStreamData(), StandardCopyOption.REPLACE_EXISTING);
				} else {
					RW.write_(writedClpImg, media.getStringData());
				}
				ZKI.infoSingleLine("Upload&Write :" + writedClpImg);

			}
		};
		return uploadEvent;
	}
}