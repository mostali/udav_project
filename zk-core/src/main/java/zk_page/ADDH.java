package zk_page;

import mpu.X;
import mpu.IT;
import mpc.html.HTML;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Html;
import zk_com.base.Xml;

public class ADDH {
	public static Html B(Component c, String html, Object... args) {
		Html cHtml = new Html(HTML.B(html, args));
		c.appendChild(cHtml);
		return cHtml;
	}

	public static Html H0(Component c, int hN, String html, Object... args) {
		IT.isBetweenEQ(hN, 1, 6);
		Html cHtml = new Html(HTML.H0(hN, html, args));
		c.appendChild(cHtml);
		return cHtml;
	}

	public static Html P(Component c, String html, Object... args) {
		Html cHtml = new Html(HTML.P(html, args));
		c.appendChild(cHtml);
		return cHtml;
	}

	public static Html H2(Component c, String html, Object... args) {
		Html cHtml = new Html("<h2>" + X.f(html, args) + "</h2>");
		c.appendChild(cHtml);
		return cHtml;
	}

	public static Html BR(Component c) {
		Html cHtml = new Html("<br/>");
		c.appendChild(cHtml);
		return cHtml;
	}

	public static Xml A(Component c, String name, String href, Object... args) {
		Xml cHtml = new Xml(HTML.A(name, href, args));
		c.appendChild(cHtml);
		return cHtml;
	}

	public static Html HR(Component c) {
		Html cHtml = new Html("<hr/>");
		c.appendChild(cHtml);
		return cHtml;
	}
}
