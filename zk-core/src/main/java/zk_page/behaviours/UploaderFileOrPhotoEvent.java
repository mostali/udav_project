package zk_page.behaviours;

import lombok.RequiredArgsConstructor;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.uploader.ClipboardLoaderComposer;
import zk_com.uploader.FileUploaderComposer;

import java.nio.file.Paths;
import java.util.function.Function;

@RequiredArgsConstructor
public class UploaderFileOrPhotoEvent implements SerializableEventListener {

	final String uploadTo;
	final boolean isImg;
	final Function<String, Boolean> successCallback;

	@Override
	public void onEvent(Event event) throws Exception {
		if (isImg) {
			ClipboardLoaderComposer.loadComponent("Upload Clipboard Image", Paths.get(uploadTo), successCallback);
		} else {
			FileUploaderComposer.loadComponent("Upload File", Paths.get(uploadTo));
		}
	}

}
