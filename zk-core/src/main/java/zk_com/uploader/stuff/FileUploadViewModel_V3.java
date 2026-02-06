package zk_com.uploader.stuff;

import org.zkoss.bind.annotation.*;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

public class FileUploadViewModel_V3 {

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

	private void handleFile(Media media, Component view) {
		if (media == null) {
			return;
		}

		// Пример: сохранение в памяти или на диск
		System.out.println("Загружен файл: " + media.getName() + " (" + media.getFormat() + ")");

		// Можно сохранить файл на сервере:
		// try {
		//     Files.write(Paths.get("/uploads/" + media.getName()), media.getByteData());
		// } catch (IOException e) { ... }

		uploadedFiles.add(media);

		// Обновление списка файлов в UI
		Vlayout fileList = (Vlayout) view.getFellow("fileList");
		Label label = new Label("✅ " + media.getName() + " (" + media.getFormat() + ")");
		fileList.appendChild(label);
	}
}