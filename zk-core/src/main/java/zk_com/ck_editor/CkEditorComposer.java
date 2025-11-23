package zk_com.ck_editor;

import mpu.core.ARG;
import mpu.core.RW;
import mpc.fs.UF;
import org.zkforge.ckez.CKeditor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;
import zk_com.base_ctr.Div0;
import zk_page.ZKR;
import zk_page.ZulLoader;

import java.nio.file.Path;

public class CkEditorComposer extends GenericForwardComposer {

	public static final String ZUL_RSRC = "/_com/_ck-editor/simple-ck-editor.zul";

	public static Component loadComponent(Path file, Component... parent) {
		Component p = ARG.isDef(parent) ? ARG.toDef(parent) : initParent(file);
		Component com = ZulLoader.loadComponentFromRsrc(ZUL_RSRC, p);
		CKeditor cKeditor = (CKeditor) com.getChildren().get(0);
//		cKeditor.setAutoHeight(true);
//		cKeditor.setHeight("100%");
//		cKeditor.setVflex("max");
		cKeditor.addEventListener("onSave", (SerializableEventListener<Event>) event -> {
			RW.write(file, cKeditor.getValue());
//			ZKR.rebuildPage();
//			NI.stop("ni");
			ZKR.restartPage();
		});
		cKeditor.setValue(RW.readString(file, "set data"));
		return cKeditor;
	}

	private static Component initParent(Path file) {
//		DivWith of = DivWith.of();
//		of.setHeight("1000px");
//		of.setSTYLE("border:1px solid red");
//		ZKC.appendChild(of);
//		return of;
//		of.setHeight("20px");

		Window window = Div0.of()._modal()._title(UF.fn(file))._closable()._showInWindow();
		window.setSizable(true);
		window.setWidth("80%");
		window.setHeight("80%");
		return window;
	}

//	private Listbox shoppingCartListbox;
//
//	@RequiredArgsConstructor
//	public static class ImageModel implements Serializable {
//		final String img_file;
//
//		public static List<ImageModel> toList(List<Path> images) {
//			return images.stream().map(Path::toString).map(ImageModel::new).collect(Collectors.toList());
//		}
//	}

//	@Init
//	public void init(@HeaderParam("user-agent") String browser) {
//		U.nothing();
//	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

//		IGallery gallery = (IGallery) comp.getParent();
//
//		//create model
//		ListModel<ImageModel> model = new ListModelArray(ImageModel.toList(gallery.getImages()));
//
//		shoppingCartListbox.setModel(model); //assign model to Grid
//
//		shoppingCartListbox.setItemRenderer((ListitemRenderer<ImageModel>) (listItem, data, i) -> {
//
////				final ImageModel cartItem = (ImageModel) data;
//
////				listItem.setValue("asd");
////				new Image(data.img_file).getContent();
////				AImage aImage = new AImage(new File(data.img_file));
////				listItem.setImage(aImage.getStringData());
////				listItem.setCoImage(new Img());
//
//			Listcell listcell = new Listcell();
//			listcell.appendChild(new Img(new File(data.img_file)));
//			listItem.appendChild(listcell);
//		});


	}
}