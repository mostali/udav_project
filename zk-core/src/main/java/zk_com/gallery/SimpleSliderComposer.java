package zk_com.gallery;

import mpu.X;
import org.zkoss.bind.annotation.HeaderParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;
import zk_com.base.Img;
import zk_com.base_ctr.Div0;
import zk_old_core.std_core.IGallery;
import zk_page.ZKC;
import zk_page.ZKS;
import zk_page.ZulLoader;

import java.nio.file.Path;

public class SimpleSliderComposer extends GenericForwardComposer {

	public static final String ZUL_RSRC = "/_com/simple-slider-composer/simple-slider-composer.zul";
	public static final String ZUL_RSRC0 = "/_com/simple-slider-composer/simple-slider-composer0.zul";

	public static void loadComponent(Component... parent) {
		ZulLoader.loadComponentFromRsrc(ZUL_RSRC, parent);
	}

	private Div slider;

	@Init
	public void init(@HeaderParam("user-agent") String browser) {
		X.nothing();
	}

	public static class GalleryDiv extends Div0 {

	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

//		slider.setClass("glr");
		IGallery gallery = (IGallery) comp.getParent();
		for (Path image : gallery.getImages()) {
//			Div div = new Div();
//			Img child = new Img(image.toFile());
//			div.appendChild(child);
//			div.setWidth("500px");
//			ZKS.BLOCK(child);
//			child.setHeight("500px");
			Div wrap = ZKS.WIDTH(ZKC.newDiv(new Img(image.toFile())), "500px");
			slider.appendChild(wrap);
		}

//		Div div = new Div();
//		div.appendChild(new Label("123123"));
//		slider.appendChild(div);

//		slider.appendLabel("gpoooo");
//		slider.appendLabel("gpoooo");
//		slider.appendLabel("gpoooo");
//		slider.appendLabel("gpoooo");
//		slider.appendLabel("gpoooo");

	}
}