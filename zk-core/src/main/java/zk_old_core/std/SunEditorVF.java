package zk_old_core.std;

import mpu.IT;
import mpu.X;
import mpc.exception.NI;
import mpc.fs.ext.GEXT;
import mpc.map.UMap;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import zk_com.base.Img;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Span0;
import zk_com.elements.Pos;
import zk_com.gallery.SimpleSliderComposer;
import zk_old_core.std_core.IGallery;
import zk_form.head.IHeadCom;
import zk_form.head.IHeadRsrc;
import zk_form.head.StdHeadLib;
import zk_page.ZKJS;
import zk_page.ZKS;
import zk_page.ZulLoader;
import zk_old_core.mdl.FdModel;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SunEditorVF extends AbsVF implements IGallery, IHeadCom {

	public static final String DEF_EDITOR_EDITOR = "seditor";

	public SunEditorVF(FdModel fdm) {
		super(fdm);
	}

	//	public static final String DEF_ON_BIND = "$('." + DEF_GALLERY_CLASS + "').bxSlider({mode: 'fade', captions: true, pagerCustom: '#bx-pager', responsive: true, slideWidth: 500 });";
	public static final String DEF_ON_BIND = "SUNEDITOR.create((document.getElementById('" + DEF_EDITOR_EDITOR + "') || '" + DEF_EDITOR_EDITOR + "'),{\n" +
			"    // All of the plugins are loaded in the \"window.SUNEDITOR\" object in dist/suneditor.min.js file\n" +
			"    // Insert options\n" +
			"    // Language global object (default: en)\n" +
			"    //lang: SUNEDITOR_LANG['ko']\n" +
			"});";

	public static final String ON_BIND_PATTERN = "SUNEDITOR.create((document.getElementById('%s') || '%s'), %s );";

	//
	public SunEditorVF(List<String> srcs, Integer width) {
//		super(Map.of("srcs", srcs, "onBind", DEF_ON_BIND.replace("500", width + "")));
		super(UMap.of("srcs", srcs, "onBind", DEF_ON_BIND));
	}


	public static final IHeadRsrc[] HEAD_RSCS = {StdHeadLib.SUNEDITOR_CSS, StdHeadLib.SUNEDITOR_JS};//, RsrcName.SUNEDITOR_EN_JS

	@Override
	public IHeadRsrc[] getHeadRsrc() {
		return HEAD_RSCS;
	}

	@Override
	public String[] getAllowedExt() {
		return GEXT.EXT_IMGS.toArray(new String[GEXT.EXT_IMGS.size()]);
	}

	@Override
	protected void initImpl() throws Exception {

		ZKS.BLOCK(this);

		Map<String, Object> props = getFormProps();

		String cls = IT.NE((String) props.getOrDefault("class", DEF_EDITOR_EDITOR));
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

		if (super.fdModel != null) {
			for (Path image : getImages()) {
				Div div = Img.wrapDiv(new Img(image.toFile()));
				div.setWidth(width);
				divGallery.appendChild(div);
			}
		}
		List<String> srcs = UMap.getAs(getFormProps(), "srcs", List.class, Collections.EMPTY_LIST);
		if (X.notEmpty(srcs)) {
			for (String src : srcs) {
				Div div = Img.wrapDiv(src);
				Img img = (Img) div.getChildren().get(0);
//				img.setSTYLE("margin:0 auto");
				img.setWidth("100%");
				div.setWidth(width);
				divGallery.appendChild(div);
			}
		}

		divGallery.setClass(cls);
		ZKJS.bindJS(divGallery, onBindJs);
		appendChild(divGallery);

		if (text != null && text_pos == Pos.BOTTOM) {
			appendHtml(text);
		}

//		setWidth("500px");

		if (true) {
			return;
		}
		if (true) {

//			ZKS.of(this).BL
//			ZKS.BLOCK(this);
//			setWidth("500px");

//			setCLASS("wtf");
			Component slider = ZulLoader.loadComponentFromRsrc(SimpleSliderComposer.ZUL_RSRC0, this);

			return;
		}
//		DivWith div = new DivWith();
		Span0 div = this;

//		String rndClass = RandomString.ALPHA("glrch_", 3);
		String rndClassParent = "glr";
		String rndClass = "glr-item";
		ZKS.BLOCK(this);
		div.setCLASS(rndClassParent);

//		ZKS.classRnd();
		for (Path image : getImages()) {
			Img child = new Img(image.toFile());
			child.setCLASS(rndClass);
			div.appendChild(child);
		}

//		String init_func = "$('.glr').isotope({\n" +
//				"      itemSelector: '.glr-item'\n" +
//				"    });";
		//jq(this)

//		ZKJS.bindJS(div, "$('.glr').isotope({itemSelector: '.glr-item'});", 1000);

	}

	@Override
	public List<Path> getImages() {
		return getRootChilds();
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
