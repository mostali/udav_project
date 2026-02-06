package zk_com.uploader.stuff;

import org.zkoss.bind.BindUtils;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Fileupload;
import zk_page.ZulLoader;

import java.util.HashMap;
import java.util.Map;

//https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Patterns/File_Upload_and_Download
public class FileUploaderDadComposer extends SelectorComposer {

	public static final String ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_ZUL = "/_com/_simple-file-uploader/simple-file-uploader-dd.zul";

	public static void loadComponent(Component... parent) {
		ZulLoader.loadComponentFromRsrc(ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_ZUL, parent);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		comp.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Fileupload.get(new EventListener<UploadEvent>() {
					@Override
					public void onEvent(UploadEvent event) throws Exception {
						Media media = event.getMedia();
						Map<String, Object> argMap = new HashMap();
						Media[] filesArray = {media};
						argMap.put("files", filesArray);
						// notify VM
						BindUtils.postGlobalCommand(null, EventQueues.DESKTOP, "doUploadFiles", argMap);
					}
				});

			}
		});
	}
}
