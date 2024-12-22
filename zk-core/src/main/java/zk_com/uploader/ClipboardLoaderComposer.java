package zk_com.uploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mpc.env.APP;
import mpc.fs.UF;
import mpu.X;
import mpu.core.RW;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.fs.path.UPath;
import mpc.log.L;
import mpc.map.UMap;
import mpu.str.STR;
import mpc.html.EHtml5Head;
import mpu.func.FunctionV1;
import org.apache.commons.io.IOUtils;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zul.*;
import zk_com.base.Tbx;
import zk_com.base.Xml;
import zk_com.win.Win0;
import zk_com.win.WinPos;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Log;
import zk_page.ZulLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Function;

//https://zkfiddle.org/sample/3g4588e/13-upload-canvas-paste#source-1
@Slf4j
public class ClipboardLoaderComposer extends GenericForwardComposer implements IUploaderCom {

	public static final String ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_ZUL = "/_com/clipboard-loader/clipboard-loader.zul";
	public static final String ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_JS = "/_com/clipboard-loader/clipboard-loader.js";
	public static final String DEFAULT_PANEL_TITLE = "Upload image";


//	public static void loadComponent(Component... parent) {
//		SdRsrc.LocRsrc globalUploads = SdRsrc.LocRsrc.SITE_UPLOADS;
//		loadComponent("Upload image to '" + globalUploads.nameru() + "'", globalUploads.getParentOfStdLocation(null), parent);
//	}

	public static void loadComponent(String title, Path uploadTo, Component... parent) {
		loadComponent(title, uploadTo, null, parent);
	}

	public static void loadComponent(String title, Path uploadTo, Function<String, Boolean> successCallback, Component... parent) {
		Map context = UMap.of(AK_UPLOAD_TO_DIR, uploadTo.toString(), AK_UPLOAD_TO_TITLE, title, AK_SUCCESS_CALLBACK, successCallback);
		Component com = ZulLoader.loadComponentFromRsrc(ZUL_WEB_COM_CLIPBOARD_LOADER_CLIPBOARD_LOADER_ZUL, context, parent);
	}

	@RequiredArgsConstructor
	private class Wrapper implements Serializable {
		final Panel panel;
		Panelchildren panelchildren;

		final boolean inWin = true;

		private Win0 win;

		void appendChild(Component com) {
			if (panelchildren == null) {
				panel.appendChild(panelchildren = new Panelchildren());
			}
			if (inWin) {
				if (win == null) {
					win = new Win0() {
						@Override
						public void onClose() {
							super.onClose();
							panel.onClose();
						}
					};
					win.popup();
					win.position(WinPos.TC);
					panelchildren.appendChild(win);
				}
				win.appendChild(com);
			} else {
				panelchildren.appendChild(com);
			}
		}
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

		String title = UMap.getAs(getZulLoaderContext(), AK_UPLOAD_TO_TITLE, String.class);
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
		tbx_wgtPasteArea.setPlaceholder("focus && copy & paste");
//		tbx_wgtPasteArea.onChange((SerializableEventListener<Event>) event -> {
//			String org = ((InputEvent) event).getValue();
//			tbx_wgtPasteArea.setValue(org);
//		});
		tbx_wgtPasteArea.onChangingAlwaysUpdate();

		EventListener<Event> imageUploadListener = (SerializableEventListener<Event>) event -> {

			Map data = (Map) event.getData();

//			Clients.log(data.toString());

			List<org.zkoss.image.Image> uploads = (List<org.zkoss.image.Image>) desktop.removeAttribute(event.getTarget().getUuid() + "." + data.get("sid"));

			Media fileMediaData = uploads.get(0);

			boolean isImg = fileMediaData instanceof org.zkoss.image.Image;

			try {
				do {//
//					String fn = tbx_wgtPasteArea.getValue();
					String fn = fileMediaData.getName();
					if (X.empty(fn)) {
						fn = STR.randAlphaNum(10) + ".wth";
					} else if (isImg) {
						if ("image.png".equals(fn)) {
							fn = "image-" + STR.randAlphaNum(5);
						}
						fn += ".png";
					}

					Path uploadTo = UMap.getAs(getZulLoaderContext(), AK_UPLOAD_TO_DIR, Path.class);

					Path writedClpImg = uploadTo.resolve(fn);
					if (UFS.existFile(writedClpImg)) {
						ZKI_Log.alert("File '%s' already exist, before remove", UPath.getTwo_ParentWithChild(writedClpImg));
						break;
					}
					UFS_BASE.MKDIR.mkdirsIfNotExistForParent(writedClpImg);

					InputStream inputStream;
					if (isImg || !fileMediaData.getContentType().contains("text")) {
						inputStream = fileMediaData.getStreamData();
					} else {
						inputStream = IOUtils.toInputStream(fileMediaData.getStringData());
					}

					RW.write_(writedClpImg, inputStream, StandardCopyOption.REPLACE_EXISTING);

					ZKI.infoSingleLine("Uploaded '%s'", APP.isDebugEnable() ? writedClpImg : UF.fnWithSize(writedClpImg));

					Function callbackSuccess = UMap.getAs(getZulLoaderContext(), AK_SUCCESS_CALLBACK, Function.class, null);

					if (callbackSuccess != null) {
						callbackSuccess.apply(writedClpImg.toString());
					}

					log.info("File is uploaded to {}", writedClpImg);
//					pn.onClose();

					break;
				} while (true);

			} catch (IOException e) {
				L.info("Upload image happens errors", e);
				throw new RuntimeException(e);
			}

			if (isImg) {
				testOutput.setContent((org.zkoss.image.Image) fileMediaData);
			}

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