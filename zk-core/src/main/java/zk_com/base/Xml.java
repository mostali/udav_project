package zk_com.base;

import lombok.SneakyThrows;
import mp.utilspoi.UMd2Html;
import mpc.html.EHtml5;
import mpe.core.ERR;
import mpe.str.URx;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpc.fs.fd.RES;
import mpc.fs.path.IPath;
import mpu.str.STR;
//import org.zkoss.zhtml.Embed;
//import org.zkoss.zhtml.Video;
import mpu.str.WRAP;
import org.jetbrains.annotations.NotNull;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Html;
import zk_com.core.IZWin;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

public class Xml extends Html implements IZWin, IPath {

	public static Component HR() {
		return Xml.asXml("<hr/>");
	}

	public static Component HR(int size, String color) {
		return Xml.asXml("<hr style=\"" +
				"border: none;\n" +
				"height: " + size + "px;\n" +
				"color: " + color + ";\n" +
				"background-color: " + color + ";\n" +
				"\" />");
	}

	public static Component BR() {
		return Xml.asXml("<br/>");
	}

	public static Xml asDetailsInline(String summary, String content) {
		String summaryHtml = EHtml5.summary.with(summary);
		String html = EHtml5.details.withTag(summaryHtml + STR.NL + content, "style='display:inline-block'");
		return Xml.asXml(html);
	}

	public static Xml H(int i, Object headerValue, String... tagAttrs) {
		return asXml(WRAP.byTag(headerValue, "H" + i, tagAttrs));
	}

	public static Xml P(CharSequence data) {
		return asTag(EHtml5.p, data);
	}

	public static Xml PRE(CharSequence data) {
		return asTag(EHtml5.pre, data);
	}

	public static Xml NBSP(Integer... count) {
		return new Xml(STR.repeat(EHtml5.NBSP, ARG.toDefOr(1, count)));
	}

	public static Xml B(CharSequence data) {
		return asTag(EHtml5.b, data);
	}

	public static Xml asRsrc(String rsrcPath, Function<String, Object> plResolver) {
		String cat = RES.ofRoot(Xml.class, rsrcPath).cat();
		cat = URx.PlaceholderRegex.DOG.findAndReplaceAll_Get(cat, plResolver);
		return asXml(cat);
	}

	public static Xml asStyle(String tagContent, Object... args) {
		return asTag(EHtml5.style, X.f(tagContent, args));
	}

	public static Xml asJs(String tagContent, Object... args) {
		return asTag(EHtml5.script, X.f(tagContent, args));
	}

	public static Component asDetailsSummary(String summary, String details) {
		return asXml(EHtml5.wrapDetailsSummary(summary, details));
	}

	public static Component asDetailsSummary(String header, Exception ex) {
		return asDetailsSummary(header, ERR.getMessagesAsStringWithHead(ex, header));
	}

	public static Xml asFrame(String url, String... tagAttributes) {
		return asXml(EHtml5.iframe.withTag("", ARR.merge("src=\"" + url + "\"", tagAttributes)));
	}

	public static @NotNull XmlMd asRsrcXml(String rcrsPath) {
		return asMd(RES.ofRoot(Xml.class, rcrsPath).cat());
	}

//	public static @NotNull Xml ofPdf(Path pathForm) {
//		Embed pdf = new Embed();
//		pdf.setDynamicProperty();
//		AdvVideo sd;

	/// /		Video sd;
	/// /		AbstractTag sd;
//		String tmpl = "<embed src=\"file_name.pdf\" width=\"800px\" height=\"2100px\" />";
//		return ofXml(UMd2Html.buildHtml(dataMd));
//	}
	
	@Override
	public Path toPath() {
		return null;
	}

	public Xml addEventListener(EventListener<? extends Event> listener) {
		super.addEventListener(Events.ON_CLICK, listener);
		return this;
	}

	public Xml(String html, Object... args) {
		super(X.f(html, args));
	}

	public Xml(Enum tag, String html, boolean... replaceSapceOnNbsp) {
		super(replaceSapceOnNbsp(WRAP.byTag(html, tag.name()), replaceSapceOnNbsp));
	}


	public static String replaceSapceOnNbsp(String string, boolean... replaceSapceOnNbsp) {
		return ARG.isDefEqTrue(replaceSapceOnNbsp) ? string.replace(" ", EHtml5.NBSP) : string;
	}

//	public Xml(String html, LazyBuilder lb) {
//		super();
//		this.lazyBuilder = lb;
//		addEventListener(Events.ON_AFTER_SIZE, new SerializableEventListener<Event>() {
//			@Override
//			public void onEvent(Event event) throws Exception {
//				if (lb == null) {
//					setContent(html);
//				} else {
//					getLazyBuilder().buildAndAppend(Xml.this);
//				}
//			}
//		});
//	}

	@SneakyThrows
	public static Xml asXmlOf(String file, Enum... wrapTag) {
		return asXmlOf(Paths.get(file), wrapTag);
	}

	@SneakyThrows
	public static Xml asXmlOf(Path file, Enum... wrapTag) {
		return buildComponentFromFile(file, wrapTag);
	}


	public static Xml asTag(Enum tag, CharSequence data, Object... args) {
		return asXml(WRAP.byTag(X.f(data, args), tag.name()));
	}

	//
	//

	public static XmlHtml asHtml(String htmlData, Object... args) {
		return XmlHtml.ofWithFormName(X.f(htmlData, args), null);
	}

	public static Xml asXml(String xmlData, Object... args) {
		return new Xml(X.f(xmlData, args));
	}

	//
	//

	public static XmlMd asMd(Path pathForm) {
		return asMd(RW.readString(pathForm));
	}

	public static XmlMd asMd(String dataMd) {
		return XmlMd.of(UMd2Html.buildHtml(dataMd));
	}

	//
	//

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		init();
		super.onPageAttached(newpage, oldpage);
	}

	protected void init() {
	}

	public static Xml buildComponentFromFileRsrc(String path, Enum... wrapTag) throws IOException {
		return new Xml(loadDataFromRsrc(path, wrapTag));
	}

	public static Xml buildComponentFromFile(Path path, Enum... wrapTag) throws IOException {
		return new Xml(loadDataFrom(path, wrapTag)) {
			@Override
			public Path toPath() {
				return path;
			}
		};
	}

	public static String loadDataFrom(Path file, Enum... wrapTag) throws IOException {
		Enum e = ARG.toDefOr(null, wrapTag);
		String data = RW.readString(file);
		return e == null ? data : WRAP.byTag(data, e.name());
	}

	public static String loadDataFromRsrc(String file, Enum... wrapTag) throws IOException {
		Enum e = ARG.toDefOr(null, wrapTag);
		String data = RES.readString(file);
		return e == null ? data : WRAP.byTag(data, e.name());
	}


}
