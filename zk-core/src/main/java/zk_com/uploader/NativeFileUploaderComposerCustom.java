package zk_com.uploader;

import lombok.SneakyThrows;
import mpc.exception.FIllegalArgumentException;
import mpc.fs.UFS;
import mpc.map.MAP;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.RW;
import mpu.func.FunctionV1;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Fileupload;
import zk_form.notify.ZKI;
import zk_notes.AxnTheme;
import zk_page.ZulLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Patterns/File_Upload_and_Download
public class NativeFileUploaderComposerCustom extends GenericForwardComposer implements IUploaderCom {

	public static final String RSRC_ZUL = "/_com/_simple-file-uploader/simple-file-uploader-custom.zul";

	public static void open(String title, Path uploadTo, int max, Component... parent) {
		open(title, uploadTo, max, null, parent);
	}

	public static void open(String title, Path uploadTo, int maxFiles, Function<String, Boolean> successCallback, Component... parent) {
		Map context = MAP.of(AK_UPLOAD_TO_DIR, uploadTo.toString(), AK_UPLOAD_TO_TITLE, title, AK_SUCCESS_CALLBACK, successCallback, AK_UPLOAD_MAX_FILES, maxFiles);
		ZulLoader.loadComponentFromRsrc(RSRC_ZUL, context, parent);
	}

	public static Media[] doNativeUploadEvent(Path path, Integer... maxFiles) {
		return Fileupload.get(ARG.toDefOr(AxnTheme.MAX_FILE_SIZE, maxFiles), getEventUpload(path.toString(), null));
	}

	public static Media[] doNativeUploadEvent(Path pathTo, FunctionV1<List<Path>> afterUpload, int maxFiles, boolean... mkdirs_mkdir_ornot) {
		return Fileupload.get(IT.isPosNotZero(maxFiles), getEventUpload(pathTo.toString(), afterUpload, mkdirs_mkdir_ornot));
	}

//	@Listen(Events.ON_CLICK + "= a")
//	public void handleUpload(MouseEvent e) {
//		int max = MAP.getAs(getZulLoaderContext(), AK_UPLOAD_MAX_FILES, Integer.class, 1);
//		Path toDir = MAP.getAs(getZulLoaderContext(), AK_UPLOAD_TO_DIR, Path.class, "tmp");
//		Fileupload.get(max, NativeFileUploaderComposer.getEventUpload(toDir.toString(), null));
//	}

//	@Command(value = Events.ON_CLICK)
//	public void handleFileUpload(@BindingParam("media") Media media) {
//		int max = MAP.getAs(getZulLoaderContext(), AK_UPLOAD_MAX_FILES, Integer.class, 1);
//		Path toDir = MAP.getAs(getZulLoaderContext(), AK_UPLOAD_TO_DIR, Path.class, "tmp");
////		Fileupload.get(max, NativeFileUploaderComposer.getEventUpload(toDir.toString(), null));
//		Fileupload.get(max, e -> {
//			Path uploadSingleMedia = uploadSingleMedia(toDir.toString(), media, true);
//			ZKI.infoSingleLine("Uploaded Finish:" + uploadSingleMedia);
//
//		});
//	}

//	private Map getZulLoaderContext() {
//		return super.arg;
//	}

//	@Override
//	public void doAfterCompose(Component comp) throws Exception {
//		Tbx nameTbx = Tbx.of("name");
//		comp.appendChild(nameTbx);
//		nameTbx.onOK(e -> {
//
//		});
////		ZKC.getFirstWindow().appendChild(nameTbx);
//
//		super.doAfterCompose(comp);
////		comp.addEventListener(Events.ON_CLICK, e -> handleUpload(null));
//	}

	/// /		name
//	}

	public static SerializableEventListener<UploadEvent> getEventUpload(String pathUploadTo, FunctionV1<List<Path>> afterUpload, boolean... mkdirs_mkdir_ornot) {
		List<Path> rslt = afterUpload == null ? null : new LinkedList<>();
		SerializableEventListener<UploadEvent> uploadEvent = event -> {
			Media[] medias = event.getMedias();
			if (medias == null) {
				ZKI.alert("Media is empty");
				return;
			}
			for (Media media : medias) {
				Path rsltOne = uploadSingleMedia(pathUploadTo, media, mkdirs_mkdir_ornot);
				rslt.add(rsltOne);
			}
			ZKI.infoSingleLine("Uploaded Finish:" + rslt);

			if (rslt != null) {
				afterUpload.apply(rslt);
			}
		};
		return uploadEvent;
	}

	@SneakyThrows
	private static Path uploadSingleMedia(String pathToStore, Media media, boolean... mkdirs_mkdir_ornot) {
		Path pathToStore0 = Paths.get(pathToStore);
		Path writedFile = pathToStore0.resolve(media.getName());
		if (UFS.existFile(writedFile)) {
			throw new FIllegalArgumentException("File already exist");
		}
		UFS.MKDIR.createDirsOrSingleDirOrCheckExist(pathToStore0, mkdirs_mkdir_ornot);
		if (media.isBinary()) {
			RW.write_(writedFile, media.getStreamData(), StandardCopyOption.REPLACE_EXISTING);
		} else {
			RW.write_(writedFile, media.getStringData());
		}

		return writedFile;
	}

	//
 	//
 	//

	private String message = "";
	private String messageStyle = "color: #666;";

	@Command
	public void upload(@BindingParam("media") Media media) {
		if (media == null) {
			setMessage("Файл не выбран.", "color: red;");
			return;
		}

//		uploadSingleMedia()
		// Пример: вывод имени и размера
		long sizeKB = media.getByteData().length / 1024;
		setMessage("Загружен: " + media.getName() + " (" + sizeKB + " КБ)", "color: green;");

		// TODO: сохраните файл на диск или в БД
		// saveFile(media);
	}

	// Геттеры для binding
	public String getMessage() {
		return message;
	}

	public String getMessageStyle() {
		return messageStyle;
	}

	private void setMessage(String msg, String style) {
		this.message = msg;
		this.messageStyle = style;
	}
}