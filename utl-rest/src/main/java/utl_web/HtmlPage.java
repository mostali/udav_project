package utl_web;

import mpu.core.ARG;
import mpu.str.Sb;
import org.jetbrains.annotations.NotNull;

public class HtmlPage extends Sb {
	public static final String XML_UTF8 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final String DOCTYPE_HTML = "<!doctype html>";
	String[] HTML = {"<html>", "</html>"};
	String[] HEAD = {"<head>", "</head>"};
	String[] BODY = {"<body>", "</body>"};

	protected final Sb head = new Sb();
	protected final Sb body = new Sb();

	public StringBuilder toHtml(boolean... fresh) {
		if (sb().length() == 0 || ARG.isDefEqTrue(fresh)) {
			StringBuilder page = sb();
			page.append(XML_UTF8).append(NL);
			page.append(DOCTYPE_HTML).append(NL);
			page.append(HTML[0]);

			buildHead(head);
			page.append(head);

			buildBody(body);
			page.append(body);

			page.append(HTML[1]);
		}
		return sb();
	}

	protected HtmlPage buildHead(Sb head) {
		head.append(HEAD[0]).append(HEAD[1]);
		return this;
	}

	protected void buildBody(Sb body) {
		body.append(BODY[0]).append(BODY[1]);
	}

	@Override
	public int length() {
		return toHtml().length();
	}

	@Override
	public char charAt(int index) {
		return toHtml().charAt(index);
	}

	@NotNull
	@Override
	public CharSequence subSequence(int start, int end) {
		return toHtml().subSequence(start, end);
	}

	@Override
	public String toString() {
		return toHtml().toString();
	}
}
