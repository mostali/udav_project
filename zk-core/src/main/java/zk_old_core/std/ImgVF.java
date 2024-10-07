package zk_old_core.std;

import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Image;
import zk_old_core.std_core.CType;
import zk_old_core.mdl.FdModel;
import zk_page.ZulLoader;

import java.io.File;
import java.nio.file.Path;

public class ImgVF extends AbsVF {

	public ImgVF(FdModel fdm) {
		super(fdm);
	}

	public static final String[] EXTS = {"png", "jpg", "jpeg", "bmp"};

	@Override
	public String[] getAllowedExt() {
		return EXTS;
	}

	@Override
	protected void initImpl() throws Exception {

		CType ctype = ctype();
		Path path = path();
		ViewMode viewMode = getViewMode();

		for (Path child : getRootChilds()) {
			switch (viewMode) {
				case view:
//					appendChild(Xml.buildComponentFromFile(child));
//					break;
				case edit:
					Component image = ZulLoader.loadComponentFromRsrc("/_com/image/image.zul", this);
					Image img = (Image) image;
					File file = child.toFile();
					img.setContent(new AImage(file));
					img.setWidth("100px");

					break;
				case error:
				default:
					appendLb("Empty:" + name());
					break;

			}
		}


	}

//	private void addContextItem_REMOVE(SpanCtx parent) {
//		Menuitem rmm = new Menuitem("Remove");
//
//
//		rmm.addEventListener(Events.ON_CLICK, new EventRmmForm(path()));
//		parent.addContextMenuItem(rmm);
//	}
//
//	private void addContextItem_HIGHLIGHT(SpanCtx parent) {
//		Menuitem higlight = new Menuitem("Highlight");
//		higlight.addEventListener(Events.ON_CLICK, new EventHighlightForm(parent));
//		parent.addContextMenuItem(higlight);
//
//	}

}
