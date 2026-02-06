package zk_com.uploader;

import mpc.exception.FIllegalArgumentException;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.map.MAP;
import mpu.IT;
import mpu.X;
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
import zk_com.base.Tbx;
import zk_form.notify.ZKI;
import zk_notes.AxnTheme;
import zk_os.coms.AFC;
import zk_os.core.Sdn;
import zk_page.ZulLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Patterns/File_Upload_and_Download
public class NativeFileUploaderComposer extends GenericForwardComposer implements IUploaderCom {

	public static final String RSRC_ZUL = "/_com/_simple-file-uploader/simple-file-uploader.zul";

	public static void open(String title, Path uploadTo, int max, Component... parent) {
		open(title, uploadTo, max, null, parent);
	}

	public static void open(String title, Path uploadTo, int maxFiles, Function<String, Boolean> successCallback, Component... parent) {
		Map context = MAP.of(AK_UPLOAD_TO_DIR, uploadTo.toString(), AK_UPLOAD_TO_TITLE, title, AK_SUCCESS_CALLBACK, successCallback, AK_UPLOAD_MAX_FILES, maxFiles);
		ZulLoader.loadComponentFromRsrc(RSRC_ZUL, context, parent);
	}

	public static Media[] doNativeUploadEvent(Path path, Integer... maxFiles) {
		return Fileupload.get(ARG.toDefOr(AxnTheme.MAX_FILE_SIZE, maxFiles), getEventUpload(path.toString(), null, null));
	}

	public static Media[] doNativeUploadEvent(Path pathTo, FunctionV1<List<Path>> afterUpload, int maxFiles, boolean... mkdirs_mkdir_ornot) {
		return Fileupload.get(IT.isPosNotZero(maxFiles), getEventUpload(pathTo.toString(), null, afterUpload, mkdirs_mkdir_ornot));
	}

	@Listen(Events.ON_CLICK + "= a")
	public void handleUpload(MouseEvent e, Tbx nameTbx) {
		Map ctx = getZulLoaderContext();
		int max = MAP.getAs(ctx, AK_UPLOAD_MAX_FILES, Integer.class, 1);
		Path toDir = MAP.getAs(ctx, AK_UPLOAD_TO_DIR, Path.class, "tmp");
		Boolean needUnzip = MAP.getAs(ctx, AK_UPLOAD_AND_UNZIP, Boolean.class, null);
		Fileupload.get(max, NativeFileUploaderComposer.getEventUpload(toDir.toString(), needUnzip, null, null));
	}

	private Map getZulLoaderContext() {
		return super.arg;
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		Tbx nameTbx = Tbx.of("name");
		comp.appendChild(nameTbx);
		nameTbx.onOK(e -> {

		});
//		ZKC.getFirstWindow().appendChild(nameTbx);

		super.doAfterCompose(comp);
		comp.addEventListener(Events.ON_CLICK, e -> handleUpload(null, nameTbx));

//		name
	}

	public static SerializableEventListener<UploadEvent> getEventUpload(String pathUploadTo, Boolean unzip, FunctionV1<List<Path>> afterUpload, boolean... mkdirs_mkdir_ornot) {
		List<Path> rslt = afterUpload == null ? null : new LinkedList<>();
		SerializableEventListener<UploadEvent> uploadEvent = event -> {
			Media[] medias = event.getMedias();
			if (medias == null) {
				ZKI.alert("Media is empty");
				return;
			}

//			String itemName = tbx.getValue();
			Path path2upload;
//			if (X.notEmpty(itemName)) {
//				path2upload = AFC.FORMS.getParentPath(Sdn.get(), itemName);
//			} else {
			path2upload = Paths.get(pathUploadTo);
//			}

			boolean isUzip = unzip != null && unzip;
			if (isUzip) {
				IT.state(Arrays.stream(medias).anyMatch(m -> !EXT.ZIP.has(m.getName())), "Support only zip");
			}

			for (Media media : medias) {

//				String name = medias[0].getName();
				String name = media.getName();

				Path writedFile = Paths.get(pathUploadTo).resolve(name);

				if (UFS.existFile(writedFile)) {
					throw new FIllegalArgumentException("File already exist");
				}
				UFS.MKDIR.createDirsOrSingleDirOrCheckExist(path2upload, mkdirs_mkdir_ornot);
				if (media.isBinary()) {
					RW.write_(writedFile, media.getStreamData(), StandardCopyOption.REPLACE_EXISTING);
				} else {
					RW.write_(writedFile, media.getStringData());
				}
				if (rslt != null) {
					rslt.add(writedFile);
				}
				if (isUzip) {

				} else {
					ZKI.infoSingleLine("Upload&Write :" + writedFile);
				}

			}
			if (rslt != null) {
				afterUpload.apply(rslt);
			}
		};
		return uploadEvent;
	}
}