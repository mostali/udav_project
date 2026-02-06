package mpc.html;

import mpu.X;

public class HTML {

	public static String H0(int i, String title, Object... args) {
		return "<h" + i + ">" + X.f(title, args) + "</h" + i + ">";
	}

	public static String P(String text, Object... args) {
		return "<p>" + X.f(text, args) + "</p>";
	}

	public static String HR() {
		return "<hr/>";
	}

	public static String BR() {
		return "<br/>";
	}

	public static String B(CharSequence line, Object... args) {
		return "<b>" + X.f(line, args) + "</b>";
	}

	public static String A(String name, String href, Object... args) {
		return "<a href=\"" + X.f(href, args) + "\">" + name + "</a>";
	}
}
