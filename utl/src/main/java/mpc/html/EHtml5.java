package mpc.html;

import mpu.X;
import mpu.core.ARG;
import mpu.str.STR;
import org.apache.commons.lang3.StringEscapeUtils;

//"https://developer.mozilla.org/en-US/docs/Web/HTML/Element"
public enum EHtml5 {
	address, article, aside, footer, header, h1, h2, h3, h4, h5, h6, hgroup, main, body, nav, section, blockquote, cite, dd, dt, dl, div, figcaption, figure, hr, li, ol, p, pre, ul, a, re, abbr, b, bdi, bdo, br, code, data, time, dfn, em, i, kbd, mark, q, rb, ruby, rp, rt, rtc, s, del, ins, samp, small, span, strong, sub, sup, u, var, wbr, area, map, audio, source, ediaStrea, img, track, video, embed, iframe, object, param, picture, canvas, noscript, script, style, caption, col, colgroup, table, tbody, tr, td, tfoot, th, thead, button, datalist, option, fieldset, label, form, input, legend, meter, optgroup, select, output, progress, textarea, details, dialog, menu, summary, slot, template, acronym, applet, basefont, bgsound, big, blink, center, command, content, dir, element, font, frame, frameset, image, isindex, keygen, listing, marquee, menuitem, multicol, nextid, nobr, noembed, noframes, plaintext, shadow, spacer, strike, tt, xmp;

	public static final String NBSP = "&nbsp;";

	public static String unescapeHtml(String data) {
		return StringEscapeUtils.unescapeHtml4(data);
	}

	public static String wrapMdCode(String classContent) {
		return "```java\n" + classContent + "\n```";
	}

	public static String wrapPrettyCode(String code) {
		return "<pre class=\"prettyprint\">" + code + "</pre>";
	}

	public static String wrapDetailsSummary(String summary, String details, boolean... open) {
		return "<details" + (ARG.isDefEqTrue(open) ? " open" : "") + "><summary>" + summary + "</summary>" + details + "</details>";
	}

	public static String H(int size, String val) {
		return STR.wrapTag(val, "H" + size);
	}

	public static String H(int size, String val, String... tagAttrs) {
		return STR.wrapTag(val, "H" + size, tagAttrs);
	}

	public static String NLH2NL(String value) {
		return value.replace(STR.NL_HTML, STR.NL);
	}

	public static String NL2NLH(String value) {
		return value.replace(STR.NL, STR.NL_HTML);
	}

	public boolean isTagName(String tagName) {
		return name().equalsIgnoreCase(tagName);
	}

	public String with() {
		switch (this) {
			case br:
			case hr:
				return "<" + name() + "/>";
			default:
				return STR.wrapTag("", name());
		}
	}

	public String with(CharSequence data, Object... args) {
		return STR.wrapTag(X.f(data, args), name());
	}

	public String withStyle(Object data, String name, String value) {
		return withTag(X.toString(data), "style=\"" + name + ":" + value + "\"");
	}

	public String withClass(Object data, String clazz) {
		return withTag(X.toString(data), "class=" + clazz);
	}

	public String withTag(String data, Object... tagAttributes) {
		return STR.wrapTag(data, name(), tagAttributes);
	}

}
