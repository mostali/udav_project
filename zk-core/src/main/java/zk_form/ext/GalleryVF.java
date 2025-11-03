package zk_form.ext;

import mpc.exception.NI;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.map.MAP;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zul.Div;
import zk_com.base.Img;
import zk_com.base_ctr.Div0;
import zk_com.core.IZState;
import zk_com.elements.Pos;
import zk_form.head.IHeadCom;
import zk_form.head.IHeadRsrc;
import zk_form.head.StdHeadLib;
import zk_page.ZKJS;
import zk_page.ZKS;
import zk_notes.node.NodeDir;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class GalleryVF extends Div0 implements IHeadCom, IZState {

//	public static final Logger L = LoggerFactory.getLogger(AppSmm.class);

	public static final String DEF_GALLERY_CLASS = "glrvf";

	//	public static final String DEF_ON_BIND = "$('." + DEF_GALLERY_CLASS + "').bxSlider({mode: 'fade', captions: true, pagerCustom: '#bx-pager', responsive: true, slideWidth: 500 });";
	public static final String DEF_ON_BIND = "$('." + DEF_GALLERY_CLASS + "').bxSlider({mode: 'fade', responsive: true });";


	public final Map<String, Object> formProps;

	public static GalleryVF ofFileImages(@NotNull List<String> files) {
		return new GalleryVF(files, true);
	}


	public Map<String, Object> getFormProps(Map<String, Object>... defRq) {
		if (X.notEmpty(formProps)) {
			return formProps;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("FormRootProps from '%s' is null", className()), defRq);
	}

	public GalleryVF(List<String> srcs_or_files, boolean isSrcOrFiles) {
		this(null, srcs_or_files, isSrcOrFiles);
	}

	public GalleryVF(NodeDir nodeDir) {
		this(nodeDir, null, false);
	}

	private final String nodeName;

	@Override
	public String getFormName() {
		return nodeName;
	}

	private GalleryVF(NodeDir nodeDir, List<String> srcs_or_files, boolean isSrcOrFiles) {

		nodeName = nodeDir == null ? getClass().getSimpleName() : nodeDir.nodeName();

		if (srcs_or_files == null) {
			srcs_or_files = (List<String>) UFS.convert(nodeDir.fLsGEXT(GEXT.IMG), String.class);
		}
		this.formProps = MAP.of(isSrcOrFiles ? "srcs" : "files", srcs_or_files, "onBind", DEF_ON_BIND);
//		if (L.isDebugEnabled()) {
//			L.debug("Create component GallerVF with '{}', size '{}'\n{}", isSrcOrFiles ? "src" : "files", X.sizeOf(srcs_or_files), srcs_or_files);
//		}
	}

	public static final IHeadRsrc[] HEAD_RSCS = {StdHeadLib.JQUERY_3_1_1, StdHeadLib.BXSLIDER_JS, StdHeadLib.BXSLIDER_CSS};

	@Override
	public IHeadRsrc[] getHeadRsrc() {
		return HEAD_RSCS;
	}

	@Override
	protected void init() {

//		appendLb("asd");
//		if (true) {
//			return;
//		}
//		FormState comStateJson = getComState_JSON();
//		comStateJson.apply_WIDTH_HEIGHT(this);

//		Component image = ZulLoader.loadComponentFromRsrc(SimpleGalleryComposer.ZUL_RSRC, this);

		ZKS.BLOCK(this);

		Map<String, Object> props = getFormProps();

		String cls = IT.NE((String) props.getOrDefault("class", DEF_GALLERY_CLASS));
//		String onBindJs = UC.NE((String) props.getOrDefault("onBind", "$('.glrvf').bxSlider()"));
		String onBindJs = MAP.getAs(props, "onBind", String.class, DEF_ON_BIND);
		String text = (String) props.getOrDefault("text", null);
		Pos text_pos = MAP.getAs(props, "text.position", Pos.class, Pos.TOP);
		String width = MAP.getAs(props, "width", String.class, "80%");

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

		{
			List<String> srcs = MAP.getAs(props, "srcs", List.class, null);
			boolean isSrcOrFiles = X.notEmpty(srcs);
			List<String> files = isSrcOrFiles ? null : MAP.getAs(props, "files", List.class, null);
			if (X.notEmptyAnyCollection(srcs, files)) {
				for (String src : isSrcOrFiles ? srcs : files) {
					Div div = isSrcOrFiles ? Img.wrapDiv(src) : Img.wrapDiv(Paths.get(src));
					Img img = (Img) div.getChildren().get(0);
					//img.setSTYLE("margin:0 auto");
					img.setWidth("100%");
					div.setWidth(width);
					div.setTooltiptext(src);
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

}
