package zk_old_core.coms;

import mpc.fs.ext.EXT;
import mpu.IT;
import mpu.X;
import mpc.exception.NI;
import mpc.fs.*;
import mpc.fs.ext.GEXT;
import mpc.fs.path.UPath;
import mpc.map.UMap;
import org.zkoss.zul.Div;
import zk_com.base.Img;
import zk_com.base_ctr.Div0;
import zk_form.head.IHeadCom;
import zk_form.head.IHeadRsrc;
import zk_form.head.StdHeadLib;
import zk_com.elements.Pos;
import zk_page.ZKJS;
import zk_page.ZKS;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class GalleryVF extends AbsVF implements IGallery, IHeadCom {

	public static final String DEF_GALLERY_CLASS = "glrvf";

	//	public static final String DEF_ON_BIND = "$('." + DEF_GALLERY_CLASS + "').bxSlider({mode: 'fade', captions: true, pagerCustom: '#bx-pager', responsive: true, slideWidth: 500 });";
	public static final String DEF_ON_BIND = "$('." + DEF_GALLERY_CLASS + "').bxSlider({mode: 'fade', responsive: true });";

	//
	public GalleryVF(Path dir) {
		this(UPath.path2abs(GEXT.IMG.ls_filter(UDIR.lsAll(dir))), false);
	}

	public GalleryVF(List<String> srcs_or_files, boolean isSrcOrFiles) {
		super(UMap.of(isSrcOrFiles ? "srcs" : "files", srcs_or_files, "onBind", DEF_ON_BIND));
		if (L.isDebugEnabled()) {
			L.debug("Create component GallerVF with '{}', size '{}'\n{}", isSrcOrFiles ? "src" : "files", X.sizeOf(srcs_or_files), srcs_or_files);
		}
	}

	public static final IHeadRsrc[] HEAD_RSCS = {StdHeadLib.JQUERY_3_1_1, StdHeadLib.BXSLIDER_JS, StdHeadLib.BXSLIDER_CSS};

	@Override
	public IHeadRsrc[] getHeadRsrc() {
		return HEAD_RSCS;
	}

	@Override
	protected void initImpl() throws Exception {

//		Component image = ZulLoader.loadComponentFromRsrc(SimpleGalleryComposer.ZUL_RSRC, this);

		ZKS.BLOCK(this);

		Map<String, Object> props = getFormProps();

		String cls = IT.NE((String) props.getOrDefault("class", DEF_GALLERY_CLASS));
//		String onBindJs = UC.NE((String) props.getOrDefault("onBind", "$('.glrvf').bxSlider()"));
		String onBindJs = UMap.getAs(props, "onBind", String.class, DEF_ON_BIND);
		String text = (String) props.getOrDefault("text", null);
		Pos text_pos = UMap.getAs(props, "text.position", Pos.class, Pos.TOP);
		String width = UMap.getAs(props, "width", String.class, "80%");

		switch (text_pos) {
			case TOP:
			case BOTTOM:
				//ok
				break;
			default:
				throw NI.stop(text_pos);
		}


//		ZKJS.bindJS(this, "$('.glr').bxSlider()");
//		ZKJS.bindJS(this, "$('.glr').bxSlider({\n" +
//				"   mode: 'fade',\n" +
//				"   captions: true,\n" +
//				"   pagerCustom: '#bx-pager',\n" +
//				"   adaptiveHeight: true,\n" +
//				"   slideWidth: 150\n" +
//				"});");

		//
		//
		if (text != null && text_pos == Pos.TOP) {
			appendHtml(text);
		}
		Div0 divGallery = new Div0();

//		if (super.fdModel != null) {
//			for (Path image : getImages()) {
//				Div div = Img.wrapDiv(new Img(image.toFile()));
//				div.setWidth(width);
//				divGallery.appendChild(div);
//			}
//		}
		{
			List<String> srcs = UMap.getAs(props, "srcs", List.class, null);
			boolean isSrcOrFiles = X.notEmpty(srcs);
			List<String> files = isSrcOrFiles ? null : UMap.getAs(props, "files", List.class, null);
			if (X.notEmptyAny_Cll(srcs, files)) {
				for (String src : isSrcOrFiles ? srcs : files) {
					Div div = isSrcOrFiles ? Img.wrapDiv(src) : Img.wrapDiv(Paths.get(src));
					Img img = (Img) div.getChildren().get(0);
					//img.setSTYLE("margin:0 auto");
					img.setWidth("100%");
					div.setWidth(width);
					divGallery.appendChild(div);
				}
			}
		}

		divGallery.setClass(cls);
		ZKJS.bindJS(divGallery, onBindJs);
		appendChild(divGallery);

		if (text != null && text_pos == Pos.BOTTOM) {
			appendHtml(text);
		}

	}

	@Override
	public List<Path> getImages() {
		return UMap.getAs(getFormProps(), "files", List.class);
	}

//	@Override
//	public List<Path> getImages() {
//		return getRootChilds();
//	}


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
