package zk_com.base;

import mpu.X;
import mpu.core.ARG;

public class XmlHtml extends Xml {

	public XmlHtml(String html, Object... args) {
		super(html, args);
	}

	public static zk_com.base.XmlHtml of(String htmlData, Object... args) {
		return new zk_com.base.XmlHtml(X.f(htmlData.toString(), args));
	}

	public static XmlHtml ofWithFormName(String html, String name) {
		return new XmlHtml(html) {
			@Override
			public String getFormName() {
				return ARG.isDef(name) ? ARG.toDef(name) : super.getFormName();
			}
		};
	}

}
