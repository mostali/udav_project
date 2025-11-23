package zk_com.uploader;

import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.map.MAP;
import mpu.X;
import mpu.core.RW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zul.*;
import zk_form.notify.ZKI;
import zk_notes.control.NotesSpace;
import zk_os.AppZosProps;
import zk_os.coms.AFC;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.ZulLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DDFileUploader {

	public static final Logger L = LoggerFactory.getLogger(DDFileUploader.class);

	public static final String ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_ZUL = "/_com/_dd-file-uploader/dd-file-uploader.zul";
	public static final String UPLOAD_TO = "uploadTo";


	public static Component open() {
		return open(null);
	}

	public static Component open(Path path) {
		Script script = (Script) Executions.createComponentsDirectly(ZulLoader.loadContentRsrc(ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_ZUL), null, ZKC.getFirstWindow(), MAP.of(UPLOAD_TO, path));
		script.setAttribute(UPLOAD_TO, path);
		return script;
	}

	private List<Media> uploadedFiles = new ArrayList<>();

	@NotifyChange("uploadedFiles")
	@Command
	public void onFileDropped(@BindingParam("event") DropEvent event, @ContextParam(ContextType.VIEW) Component view) {
		Object dragged = event.getDragged();
		if (dragged instanceof Media) {
			Media media = (Media) dragged;
			handleFile(media, view);
		}
	}

	@NotifyChange("uploadedFiles")
	@Command
	public void onFileUploaded(@BindingParam("media") Media media, @ContextParam(ContextType.VIEW) Component view) {
		handleFile(media, view);
	}

//	private Map getZulLoaderContext() {
//		return super.arg;
//	}

	@SneakyThrows
	private void handleFile(Media media, Component view) {
		if (media == null) {
			return;
		}

		Textbox znitem = (Textbox) view.getFellow("znitem");
		Checkbox zncb = (Checkbox) view.getFellow("cbIsReplaceIfExists");

		String newItemName = UF.clearFilename(znitem.getValue());

		String updateDoFolder = ((Script) view.getPreviousSibling()).getAttribute(UPLOAD_TO).toString();

		Path formParentPath = null;
		if (updateDoFolder == null) {
			//it form component
			formParentPath = AFC.FORMS.getParentPathCurrent(X.empty(newItemName) ? media.getName() : newItemName);
		} else {
			//is custom path
			formParentPath = Paths.get(updateDoFolder);
		}

		Path itemPath = formParentPath.resolve(UF.clearFilename(media.getName()));

		if (UFS.existDir(formParentPath) && !zncb.isChecked()) {
			ZKI.alert("Item '%s' exist", itemPath.getFileName());
			return;
		}

		UFS.MKDIR.createDirs(formParentPath);

		if (media.inMemory()) {
			//TODO use fileMediaData.getContentType().contains("text") for exclude handle throwable
			if (false) {
				boolean isText = media.getContentType().contains("text");
				if (isText) {
					String byteData = media.getStringData();
					RW.write_(itemPath, byteData);
				} else {
					byte[] byteData = media.getByteData();
					RW.write_(itemPath, byteData);
				}
			} else {
				try {
					byte[] byteData = media.getByteData();
					RW.write_(itemPath, byteData);
				} catch (IllegalStateException ex) {
					if (ex.getMessage().contains("Use getStringData() instead")) {
						String byteData = media.getStringData();
						RW.write_(itemPath, byteData);
					} else {
						X.throwException(ex);
					}
				}
			}
		} else if (media.isBinary()) {
			RW.write_(itemPath, media.getStreamData());
		} else {
			throw new FIllegalStateException("Unknown data type '%s'", media.getFormat() + "/" + media.getContentType());
		}

		if (L.isInfoEnabled()) {
			L.info("Загружен {} файл: {} ", " (" + media.getFormat() + ")", itemPath);
		}

		uploadedFiles.add(media);

		// Обновление списка файлов в UI
		Vlayout fileList = (Vlayout) view.getFellow("fileList");
		Label label = new Label("✅ " + media.getName() + " (" + media.getFormat() + ")");// + Hu.MB1(media.getByteData().length)
		fileList.appendChild(label);

		if (AppZosProps.APP_WEB_SYNC_RESTART_PAGE.getValueOrDefault(false)) {
			ZKR.restartPage();
		} else {
			NotesSpace.rerenderFirst();
		}

	}
}