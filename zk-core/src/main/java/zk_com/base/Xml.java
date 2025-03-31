package zk_com.base;

import lombok.SneakyThrows;
import mp.utilspoi.UMd2Html;
import mpc.html.EHtml5;
import mpe.str.URx;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;
import mpc.fs.fd.RES;
import mpc.fs.path.IPath;
import mpu.str.STR;
//import org.zkoss.zhtml.Embed;
//import org.zkoss.zhtml.Video;
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
		return Xml.ofXml("<hr/>");
	}

	public static Component BR() {
		return Xml.ofXml("<br/>");
	}

	public static Xml DETAILS_INLINE(String summary, String content) {
		String summaryHtml = EHtml5.summary.with(summary);
		String html = EHtml5.details.withTag(summaryHtml + STR.NL + content, "style='display:inline-block'");
		return Xml.ofXml(html);
	}

	public static Xml H(int i, Object headerValue, String... tagAttrs) {
		return ofXml(STR.wrapTag(headerValue, "H" + i, tagAttrs));
	}

	public static Xml P(CharSequence data) {
		return ofTag(EHtml5.p, data);
	}

	public static Xml PRE(CharSequence data) {
		return ofTag(EHtml5.pre, data);
	}

	public static Xml NBSP(Integer... count) {
		return new Xml(STR.repeat(EHtml5.NBSP, ARG.toDefOr(1, count)));
	}

	public static Xml B(CharSequence data) {
		return ofTag(EHtml5.b, data);
	}

	public static Xml ofMd(Path pathForm) {
		return ofMd(RW.readContent(pathForm));
	}

	public static Xml ofMd(String dataMd) {
		return ofXml(UMd2Html.buildHtml(dataMd));
	}

	public static Xml ofRsrc(String rsrcPath, Function<String, Object> plResolver) {
		String cat = RES.ofRoot(Xml.class, rsrcPath).cat();
		cat = URx.PlaceholderRegex.DOG.findAndReplaceAll(cat, plResolver);
		return ofXml(cat);
	}

	public static Xml ofStyle(String tagContent, Object... args) {
		return ofTag(EHtml5.style, X.f(tagContent, args));
	}

	public static Xml ofJs(String tagContent, Object... args) {
		return ofTag(EHtml5.script, X.f(tagContent, args));
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
		super(replaceSapceOnNbsp(STR.wrapTag(html, tag.name()), replaceSapceOnNbsp));
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
	public static Xml ofFile(String file, Enum... wrapTag) {
		return ofFile(Paths.get(file), wrapTag);
	}

	@SneakyThrows
	public static Xml ofFile(Path file, Enum... wrapTag) {
		return buildComponentFromFile(file, wrapTag);
	}

	public static Xml ofXml(String xmlData, Object... args) {
		return new Xml(X.f(xmlData.toString(), args));
	}

	public static Xml ofTag(Enum tag, CharSequence data, Object... args) {
		return ofXml(STR.wrapTag(X.f(data, args), tag.name()));
	}

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
		String data = RW.readContent(file);
		return e == null ? data : STR.wrapTag(data, e.name());
	}

	public static String loadDataFromRsrc(String file, Enum... wrapTag) throws IOException {
		Enum e = ARG.toDefOr(null, wrapTag);
		String data = RES.readString(file);
		return e == null ? data : STR.wrapTag(data, e.name());
	}


}
