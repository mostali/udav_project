package zk_com.uploader;

import mpc.exception.FIllegalArgumentException;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.map.MAP;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.func.FunctionV1;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Fileupload;
import zk_form.notify.ZKI;
import zk_page.ZulLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Patterns/File_Upload_and_Download
public class FileUploaderComposer extends GenericForwardComposer implements IUploaderCom {

	public static final String ZUL_WEB_COM_SIMPLE_FILE_LOADER_CLIPBOARD_LOADER_ZUL = "/_com/simple-file-uploader/simple-file-uploader.zul";

	public static void loadComponent(String title, Path uploadTo, Component... parent) {
		loadComponent(title, uploadTo, 10, null, parent);
	}

	public static void loadComponent(String title, Path uploadTo, int max, Function<String, Boolean> successCallback, Component... parent) {
		Map context = MAP.of(AK_UPLOAD_TO_DIR, uploadTo.toString(), AK_UPLOAD_TO_TITLE, title, AK_SUCCESS_CALLBACK, successCallback, AK_UPLOAD_MAX, max);
		ZulLoader.loadComponentFromRsrc(ZUL_WEB_COM_SIMPLE_FILE_LOADER_CLIPBOARD_LOADER_ZUL, context, parent);
	}

	public static Media[] doChoiceFileAdnDownloadMedia(Path path, Integer... max) {
		return Fileupload.get(ARG.toDefOr(10, max), getEventUpload(path.toString(), null));
	}

	public static Media[] doChoiceFileAdnDownloadMedia(Path pathTo, FunctionV1<List<Path>> afterUpload, int max, boolean... mkdirs_mkdir_ornot) {
		return Fileupload.get(IT.isPosNotZero(max), getEventUpload(pathTo.toString(), afterUpload, mkdirs_mkdir_ornot));
	}

	@Listen(Events.ON_CLICK + "= a")
	public void handleUpload(MouseEvent e) {
		int max = MAP.getAs(getZulLoaderContext(), AK_UPLOAD_MAX, Integer.class, 1);
		Path toDir = MAP.getAs(getZulLoaderContext(), AK_UPLOAD_TO_DIR, Path.class, "tmp");
		Fileupload.get(max, FileUploaderComposer.getEventUpload(toDir.toString(), null));
	}

	private Map getZulLoaderContext() {
		return super.arg;
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		comp.addEventListener(Events.ON_CLICK, e -> handleUpload(null));
	}

	public static SerializableEventListener<UploadEvent> getEventUpload(String pathUploadTo, FunctionV1<List<Path>> afterUpload, boolean... mkdirs_mkdir_ornot) {
		List<Path> rslt = afterUpload == null ? null : new LinkedList<>();
		SerializableEventListener<UploadEvent> uploadEvent = event -> {
			Media[] medias = event.getMedias();
			if (medias == null) {
				ZKI.alert("Media is empty");
				return;
			}
			for (Media media : medias) {
				Path writedFile = Paths.get(pathUploadTo).resolve(medias[0].getName());
				if (UFS.existFile(writedFile)) {
					throw new FIllegalArgumentException("File already exist");
				}
				UFS_BASE.MKDIR.createDirsOrSingleDirOrCheckExist(Paths.get(pathUploadTo), mkdirs_mkdir_ornot);
				if (media.isBinary()) {
					RW.write_(writedFile, media.getStreamData(), StandardCopyOption.REPLACE_EXISTING);
				} else {
					RW.write_(writedFile, media.getStringData());
				}
				if (rslt != null) {
					rslt.add(writedFile);
				}
				ZKI.infoSingleLine("Upload&Write :" + writedFile);
			}
			if (rslt != null) {
				afterUpload.apply(rslt);
			}
		};
		return uploadEvent;
	}
}