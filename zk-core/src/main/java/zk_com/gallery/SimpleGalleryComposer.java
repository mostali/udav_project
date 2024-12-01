package zk_com.gallery;

import lombok.RequiredArgsConstructor;
import mpu.X;
import org.zkoss.bind.annotation.HeaderParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;
import zk_com.base.Img;
import zk_old_core.coms.IGallery;
import zk_page.ZulLoader;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleGalleryComposer extends GenericForwardComposer {

	public static final String ZUL_RSRC = "/_com/simple-gallery-composer/simple-gallery-composer.zul";

	public static void loadComponent(Component... parent) {
		ZulLoader.loadComponentFromRsrc(ZUL_RSRC, parent);
	}

	private Listbox shoppingCartListbox;

	@RequiredArgsConstructor
	public static class ImageModel implements Serializable {
		final String img_file;

		public static List<ImageModel> toList(List<Path> images) {
			return images.stream().map(Path::toString).map(ImageModel::new).collect(Collectors.toList());
		}
	}

	@Init
	public void init(@HeaderParam("user-agent") String browser) {
		X.nothing();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		IGallery gallery = (IGallery) comp.getParent();

		//create model
		ListModel<ImageModel> model = new ListModelArray(ImageModel.toList(gallery.getImages()));

		shoppingCartListbox.setModel(model); //assign model to Grid

		shoppingCartListbox.setItemRenderer((ListitemRenderer<ImageModel>) (listItem, data, i) -> {

//				final ImageModel cartItem = (ImageModel) data;

//				listItem.setValue("asd");
//				new Image(data.img_file).getContent();
//				AImage aImage = new AImage(new File(data.img_file));
//				listItem.setImage(aImage.getStringData());
//				listItem.setCoImage(new Img());

			Listcell listcell = new Listcell();
			listcell.appendChild(new Img(new File(data.img_file)));
			listItem.appendChild(listcell);
		});


	}
}