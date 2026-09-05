package udav_net;

import mpu.core.ARRi;
import mpu.str.TKN;
import org.jsoup.Connection;
import org.jsoup.parser.Parser;
import udav_net.wrappercall.WrapperCallTooManyRedirectTc;
import org.jsoup.Jsoup;
import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpu.core.ARG;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpc.str.sym.SYM;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

//https://jsoup.org/cookbook/extracting-data/selector-syntax
//https://www.baeldung.com/java-with-jsoup
public class UJsoup {

	public static final Logger L = LoggerFactory.getLogger(UJsoup.class);
	private static final int DEF_TIMEOUT_MS = 20_000;

	private static String USERAGENT = "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/63.0.3239.84 Chrome/63.0.3239.84 Safari/537.36";

	public static String text(String html) {
		return Jsoup.parse(html).text();
	}

	public static String outerHTML(String html) {
		return Jsoup.parse(html).outerHtml();
	}

	public static Elements selectTC(String url, int timeoutMs, String selector, int tc) throws IOException {
		return new WrapperCallTooManyRedirectTc<Elements, IOException>("UJsoup::selectTc", tc) {
			@Override
			public Elements callImpl() throws IOException {
				return select(url, timeoutMs, selector);
			}
		}.call_();
	}

	public static Elements select(String url, int timeoutMs, String selector) throws IOException {
		timeoutMs = timeoutMs >= 0 ? timeoutMs : DEF_TIMEOUT_MS;
		return Jsoup.parse(new URL(url), timeoutMs).select(selector);
	}

	public static Elements select(String html, String selector) {
		return Jsoup.parse(html).select(selector);
	}

	public static Element select(String html, String selector, int index) {
		Elements els = select(html, selector);
		return ARRi.item(els, index, null);
	}

	// comment becuase net need utils-db dependency
	//	public static Document url2doc_GET_CACHE(String url2call)
	//			throws IOException {
	//
	//		String h = UTree.getH(url2call);
	//		if (h == null) {
	//			Document d = url2doc_GET(url2call);
	//			UTree.setH(url2call, d.html());
	//			return d;
	//		}
	//		L.info("From Cache :" + url2call);
	//		return Jsoup.parse(h);
	//
	//	}

	public static boolean isXml(String html) {
		try {
			Jsoup.parse(html);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static Document xml2doc(String html) {
		return IT.notNull(Jsoup.parse(html, "", Parser.xmlParser()));
	}

	public static Document html2doc(String html) {
		return IT.notNull(Jsoup.parse(html));
	}

	public static Document html2doc(File file, String... charset) throws IOException {
		return Jsoup.parse(IT.isFileExist(file), ARG.toDefOr(Charset.defaultCharset().toString(), charset));
	}

	public static void setSystemPropertyManyResirect(int count) {
		if (count < 1) {
			throw new IllegalArgumentException("http.maxRedirects is = " + count);
		}
		System.setProperty("http.maxRedirects", "" + count);
	}

	public static Document url2doc_GET(String url2call, boolean... allowManyRedirect) throws IOException {
		org.jsoup.Connection.Response response = Jsoup.connect(url2call).timeout(20 * 1000).userAgent(USERAGENT).execute();
		Document doc = Jsoup.connect(url2call).timeout(20 * 1000).userAgent(USERAGENT).followRedirects(ARG.isDefEqTrue(allowManyRedirect)).response(response).get();
		return doc;

	}

	public static Document url2doc_GET_TC(String url2call, int tc) throws IOException {
		return new WrapperCallTooManyRedirectTc<Document, IOException>("UJsoup::selectTc", tc) {
			@Override
			public Document callImpl() throws IOException {
				return url2doc_GET(url2call);
			}
		}.call_();

	}

	public static Document url2doc_GET(String url2call) throws IOException {
		Connection.Response response = Jsoup.connect(url2call).timeout(20 * 1000).userAgent(USERAGENT).execute();
		Document doc = Jsoup.connect(url2call).timeout(20 * 1000).userAgent(USERAGENT).response(response).get();
		return doc;
	}

	public static Document url2doc_POST(String url2call, String text) throws IOException {
		Connection.Response response = Jsoup.connect(url2call).timeout(20 * 1000).userAgent(USERAGENT).execute();
		Document doc = Jsoup.connect(url2call).timeout(20 * 1000).userAgent(USERAGENT).response(response).postDataCharset("Windows-1251").data("text", text).post();
		return doc;

	}

	public static Node createElement(String tagName) {
		switch (tagName) {
			case "cdata":
				return new CDataNode("");
			default:
				Element child = new Element(Tag.valueOf(tagName), "");
				return child;
		}
	}

	public static void addAttrContent(Element element, String attrContent) {
		StringBuilder html = new StringBuilder();
		html.append("<div ").append(IT.notEmpty(attrContent)).append("></div>");
		Element first = Jsoup.parse(html.toString());
		IT.notNull(element).attributes().addAll(first.attributes());
	}

	public static boolean isAttrWithContent(String attrContent) {
		String[] two = TKN.twoByChars(attrContent, SYM.WORD_EN_NUM_DASH + SYM.COLON, TKN.SplitByChars.ALLOWED);
		return !two[1].isEmpty();
	}

	public static void fillAttributes(Node parent, Map<String, String> attrs) {
		for (Map.Entry<String, String> attr : attrs.entrySet()) {
			if (attr.getValue() == null) {
				if (parent instanceof Element) {
					((Element) parent).attr(attr.getKey(), true);
				} else {
					parent.attr(attr.getKey());
				}
			} else {
				parent.attr(attr.getKey(), attr.getValue());
			}
		}
	}

	public static String selectFirstText(Document doc, String selector, String... defRq) {
		Element el = selectFirstElement(doc, selector, null);
		if (el != null) {
			return el.text();
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Element(text) is NULL, by selector '%s'", selector);
	}

	public static Element selectFirstElement(Document doc, String selector, Element... defRq) {
		Element el = doc.selectFirst(selector);
		if (el != null) {
			return el;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Element is NULL, by selector '%s'", selector);
	}
}
