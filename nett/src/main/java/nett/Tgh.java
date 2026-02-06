package nett;

import mpu.str.STR;

/**
 * Html
 */
//https://core.telegram.org/api/entities
public class Tgh {
	public static final int NAME_LENGTH = 33;

	public static String code(Object str) {
		return "<code>" + str + "</code>";
	}

	public static String code(Object pfx, Object str, Object sfx) {
		return (pfx == null ? "" : pfx) + code(str) + (sfx == null ? "" : sfx);
	}

	public static String b(Object str) {
		return "<b>" + str + "</b>";
	}

	public static String i(Object str) {
		return "<i>" + str + "</i>";
	}

	public static String s(Object str) {
		return "<strike>" + str + "</strike>";
	}

	public static String u(Object str) {
		return "<u>" + str + "</u>";
	}

	public static String pre(Object str, String lang) {
		return "<pre language=\"" + lang + "\">" + str + "</pre>";
	}

	public static String a(String name, String url) {
		return "<a href=\"" + url + "\">" + name + "</a>";
	}

	public static String link(String name, String url) {
		return a(STR.substr(name, 0, NAME_LENGTH, name), url);
	}

//	messageEntityBold => <b>bold</b>, <strong>bold</strong>, **bold**
//	messageEntityItalic => <i>italic</i>, <em>italic</em> *italic*
//	messageEntityCode => <code>code</code>, `code`
//	messageEntityStrike => <s>strike</s>, <strike>strike</strike>, <del>strike</del>, ~~strike~~
//	messageEntityUnderline => <u>underline</u>
//	messageEntityPre => <pre language="c++">code</pre>,
	//<a href="">link</a>
}
