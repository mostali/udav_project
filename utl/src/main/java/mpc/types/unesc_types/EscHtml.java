package mpc.types.unesc_types;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

public class EscHtml implements CharSequence {

	public final String data;

	public String data() {
		return data;
	}

	public String convert() {
		return escape(data);
	}

	public EscHtml(String data) {
		this.data = data;
	}

	public static EscHtml of(String args) {
		return new EscHtml(args);
	}

	@Override
	public String toString() {
		return convert();
	}

	public static String escape(String str) {
		return StringEscapeUtils.escapeHtml4(str.toString());
	}

	public static String unescape(String str) {
		return StringEscapeUtils.unescapeHtml4(str.toString());
	}

	@Override
	public int length() {
		return data.length();
	}

	@Override
	public char charAt(int index) {
		return data.charAt(index);
	}

	@NotNull
	@Override
	public CharSequence subSequence(int start, int end) {
		return data.subSequence(start, end);
	}
}
