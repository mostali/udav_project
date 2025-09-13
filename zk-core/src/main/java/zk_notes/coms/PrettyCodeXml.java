package zk_notes.coms;

import mpc.exception.RequiredRuntimeException;
import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import zk_com.base.Xml;
import zk_com.base.XmlHtml;
import zk_com.base.XmlMd;
import zk_com.core.IZWin;
import zk_form.head.IHeadCom;
import zk_form.head.IHeadRsrc;
import zk_form.head.StdHeadLib;
import zk_page.ZKJS;

import java.util.Optional;

//https://github.com/JiHong88/suneditor/blob/master/README.md
//http://suneditor.com/sample/html/examples.html
//http://suneditor.com/sample/html/out/document-user.html#noticeOpen
//https://github.com/JiHong88/SunEditor/issues/1056
public class PrettyCodeXml extends Xml implements IZWin, IHeadCom {

	public static final IHeadRsrc[] HEAD_RSCS = {StdHeadLib.PRETTYFY_JS};

	public static PrettyCodeXml of(String data) {
		return ofWithFormName(data, true);
	}

	public static PrettyCodeXml ofWithFormName(String data, boolean wrap, String... name) {
		String html;
		if (!wrap) {
			html = data;
		} else {
			html = "<pre class=\"prettyprint\">\n" +
					data +
					"\n</pre>";
		}
		return new PrettyCodeXml(html) {
			@Override
			public String getFormName() {
				return ARG.isDef(name) ? ARG.toDef(name) : super.getFormName();
			}
		};
	}

	public static PrettyCodeXml findInChildren_PrettyCodeXml(Component c, PrettyCodeXml... defRq) {
		Optional<Component> first = c.getChildren().stream().filter(ch -> PrettyCodeXml.class.isAssignableFrom(ch.getClass())).findFirst();
		return (PrettyCodeXml) ARG.toDefThrowOpt(() -> new RequiredRuntimeException("except PrettyCodeXml form"), first, defRq);
	}

	public static XmlHtml findInChildren_XmlHtml(Component c, XmlHtml... defRq) {
		Optional<Component> first = c.getChildren().stream().filter(ch -> XmlHtml.class.isAssignableFrom(ch.getClass())).findFirst();
		return (XmlHtml) ARG.toDefThrowOpt(() -> new RequiredRuntimeException("except XmlHtml form"), first, defRq);
	}

	public static XmlMd findInChildren_XmlMd(Component c, XmlMd... defRq) {
		Optional<Component> first = c.getChildren().stream().filter(ch -> XmlMd.class.isAssignableFrom(ch.getClass())).findFirst();
		return (XmlMd) ARG.toDefThrowOpt(() -> new RequiredRuntimeException("except XmlMd form"), first, defRq);
	}

//	public static SingleNodeAudio findInChildren_SimpleAudio(Component c, SingleNodeAudio... defRq) {
//		Optional<Component> first = c.getChildren().stream().filter(ch -> SingleNodeAudio.class.isAssignableFrom(ch.getClass())).findFirst();
//		return (SingleNodeAudio) ARG.toDefThrowOpt(() -> new RequiredRuntimeException("except SingleNodeAudio form"), first, defRq);
//	}
//
//	public static SingleNodeVideo findInChildren_SimpleVideo(Component c, SingleNodeVideo... defRq) {
//		Optional<Component> first = c.getChildren().stream().filter(ch -> SingleNodeVideo.class.isAssignableFrom(ch.getClass())).findFirst();
//		return (SingleNodeVideo) ARG.toDefThrowOpt(() -> new RequiredRuntimeException("except SingleNodeVideo form"), first, defRq);
//	}
//
//	public static SingleNodeImg findInChildren_SimpleImage(Component c, SingleNodeImg... defRq) {
//		Optional<Component> first = c.getChildren().stream().filter(ch -> SingleNodeImg.class.isAssignableFrom(ch.getClass())).findFirst();
//		return (SingleNodeImg) ARG.toDefThrowOpt(() -> new RequiredRuntimeException("except SingleNodeImg form"), first, defRq);
//	}

	@Override
	public IHeadRsrc[] getHeadRsrc() {
		return HEAD_RSCS;
	}

	public PrettyCodeXml(String value) {
		super(value);
	}

	@Override
	protected void init() {
		super.init();

		PrettyCodeXml tbxm = this;

		String uuid = tbxm.getUuid();
//		ZKL.log("SE:"+uuid);
//		if (super.saveble) {
//			tbxm.addEventListener(EVENT_ON_SAVE, (SerializableEventListener<Event>) event -> {
////				onSaveWtf(event, "wtf");
//
//			});
//		}
		ZKJS.bindJS(tbxm, "PR.prettyPrint()");


////								String v2="<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js\"></script>";
////								String v2="<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?skin=sunburst&autorun=true;lang=java\"></script>";
////								pageCtrl.addBeforeHeadTags("<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js\"></script>");
////								String v2 = "<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?autorun=true&amp;lang=java&amp;skin=sunburst\"></script>";
//		String v2 = "<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?autorun=true&amp;lang=kotlin&amp;skin=sunburst\"></script>";
////								String v2 = "<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js?autorun=true&amp;skin=sunburst\"></script>";
////								https://github.com/googlearchive/code-prettify/tree/master/src
//
//		ZKC.getFirstPageCtrl().addBeforeHeadTags(v2);

//		ZKJS.bindJS(tbxm, "");uuid

	}

}
