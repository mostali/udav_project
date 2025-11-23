package zk_com.uploader.stuff;

import lombok.extern.slf4j.Slf4j;
import mpc.fs.UFS;
import mpc.fs.path.UPath;
import mpc.html.EHtml5Head;
import mpc.log.L;
import mpc.map.MAP;
import mpu.X;
import mpu.core.RW;
import mpu.func.FunctionV1;
import mpu.str.STR;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;
import zk_com.base.Tbx;
import zk_com.base.Xml;
import zk_com.uploader.IUploaderCom;
import zk_com.win.WinPos;
import zk_form.notify.ZKI;
import zk_os.AppZos;
import zk_page.ZulLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//https://zkfiddle.org/sample/3g4588e/13-upload-canvas-paste#source-1
@Slf4j
public class DdFileUploaderComposer extends GenericForwardComposer implements IUploaderCom {

	public static final String ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_ZUL = "/_com/_dd-file-uploader/stuff/dd-file-uploader.zul";
	public static final String ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_JS = "/_com/_dd-file-uploader/stuff/dd-file-uploader.js";
	public static final String DEFAULT_PANEL_TITLE = "Upload file";


//	public static void loadComponent(Component... parent) {
//		SdRsrc.LocRsrc globalUploads = SdRsrc.LocRsrc.SITE_UPLOADS;
//		loadComponent("Upload image to '" + globalUploads.nameru() + "'", globalUploads.getParentOfStdLocation(null), parent);
//	}

	public static void loadComponent(String title, Path uploadTo, Component... parent) {
		loadComponent(title, uploadTo, null, parent);
	}

	public static void loadComponent(String title, Path uploadTo, Function<String, Boolean> successCallback, Component... parent) {
		Map context = MAP.of(AK_UPLOAD_TO_DIR, uploadTo.toString(), AK_UPLOAD_TO_TITLE, title, AK_SUCCESS_CALLBACK, successCallback);
		Component com = ZulLoader.loadComponentFromRsrc(ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_ZUL, context, parent);
	}


	public void doAfterCompose(Component comp0) throws Exception {
		super.doAfterCompose(comp0);

//		ClipboardLoaderComposer composer = (ClipboardLoaderComposer) UMap.getByValue(comp.getAttributes(), new Predicate() {
//			@Override
//			public boolean test(Object o) {
//				return o == null ? false : ClipboardLoaderComposer.class.isAssignableFrom(o.getClass());
//			}
//		});

		Window comp = (Window) comp0;
//		Wrapper comp = new Wrapper(pn);
//		Panel pn = (Panel) comp0;

		comp.doModal();
		comp.doPopup();
		comp.setPosition(WinPos.center.getPattern());

		String title = MAP.getAs(getZulLoaderContext(), AK_UPLOAD_TO_TITLE, String.class);
		comp.setTitle(title);


//		comp.appendChild(new Html("<canvas width=\"400px\" height=\"300px\" id=\"upload-clp-img\"></canvas>"));
		comp.appendChild(Xml.buildComponentFromFileRsrc(ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_JS, EHtml5Head.script));

		Image testOutput = new Image();

		testOutput.setWidth("200px");
		comp.appendChild(new Separator());

//		Button drawButton = new Button("draw");
//		drawButton.setWidgetListener("onClick", "draw(document.getElementById('upload-clp-img'));");
//		comp.appendChild(drawButton);

//		Button uploadButton = new Button("upload");
//		uploadButton.setWidgetListener("onClick", "uploadCanvas(this, document.getElementById('upload-clp-img'));");

		Tbx tbx_wgtPasteArea = new Tbx();
		tbx_wgtPasteArea.setWidth("200px");
		tbx_wgtPasteArea.setPlaceholder("focus & paste image here");
//		tbx_wgtPasteArea.onChange((SerializableEventListener<Event>) event -> {
//			String org = ((InputEvent) event).getValue();
//			tbx_wgtPasteArea.setValue(org);
//		});
		tbx_wgtPasteArea.onChangingUpdateValue();

		EventListener<Event> imageUploadListener = (SerializableEventListener<Event>) event -> {
			Map data = (Map) event.getData();
//			Clients.log(data.toString());
			List<org.zkoss.image.Image> uploads = (List<org.zkoss.image.Image>) desktop.removeAttribute(event.getTarget().getUuid() + "." + data.get("sid"));
			//use uploads.get(0).getByteData() / getStreamData() to get the actual data
			org.zkoss.image.Image image = uploads.get(0);
			try {
				do {//
					String fn = tbx_wgtPasteArea.getValue();
					if (X.empty(fn)) {
						fn = STR.randAlphaNum(10) + ".png";
					} else if (fn.indexOf('.') == -1) {
						fn += ".png";
					}
					Path uploadTo = MAP.getAs(getZulLoaderContext(), AK_UPLOAD_TO_DIR, Path.class);

					Path writedClpImg = uploadTo.resolve(fn);
					if (UFS.existFile(writedClpImg)) {
						ZKI.alert("File '%s' already exist, before remove", UPath.getTwo_ParentWithChild(writedClpImg));
						break;
					}
					UFS.MKDIR.mkdirsIfNotExist(writedClpImg.getParent());
					RW.write_(writedClpImg, image.getStreamData(), StandardCopyOption.REPLACE_EXISTING);

					ZKI.infoSingleLine("Uploaded '%s'", AppZos.isDebugEnable() ? writedClpImg : UPath.getTwo_ParentWithChild(writedClpImg));

					FunctionV1<Path> callbackSuccess = MAP.getAs(getZulLoaderContext(), AK_SUCCESS_CALLBACK, FunctionV1.class, null);

					if (callbackSuccess != null) {
						callbackSuccess.apply(writedClpImg);
					}

					log.info("File is uploaded to {}", writedClpImg);
//					pn.onClose();

					break;
				} while (true);

			} catch (IOException e) {
				L.info("Upload image happens errors", e);
				throw new RuntimeException(e);
			}

			testOutput.setContent(image);

		};
//		uploadButton.addEventListener("onImageUpload", imageUploadListener);
//		comp.appendChild(uploadButton);

		comp.appendChild(tbx_wgtPasteArea);

//		Div wgtPasteArea = new Div();

		tbx_wgtPasteArea.setWidth("200px");
		tbx_wgtPasteArea.setHeight("40px");
		tbx_wgtPasteArea.setSclass("focussablePasteArea");
		tbx_wgtPasteArea.setTabindex(0);
//		wgtPasteArea.appendChild(new Label("focus & paste image here"));
		tbx_wgtPasteArea.setWidgetListener("onBind", "initPasteImage(this);"); //initialize client side paste listener
		tbx_wgtPasteArea.addEventListener("onImageUpload", imageUploadListener);
		comp.appendChild(tbx_wgtPasteArea);

		comp.appendChild(new Separator());
//		comp.appendChild(new Label("Test Output"));
		comp.appendChild(testOutput);
	}

	private Map getZulLoaderContext() {
		return super.arg;
	}

//	@Command
//	@NotifyChange("closeUploader")
//	public void closeUploader() {
//		U.say("close");
//	}
}