package zk_com.base;

import mp.utilspoi.UMd2Html;
import mpu.core.ARG;

public class XmlMd extends Xml {

	public XmlMd(String html, Object... args) {
		super(html, args);
	}

	public static zk_com.base.XmlMd of(String dataMd) {
		return new zk_com.base.XmlMd(UMd2Html.buildHtml(dataMd));
	}

	public static XmlMd ofWithFormName(String dataMd, String name) {
		return new XmlMd(dataMd) {
			@Override
			public String getFormName() {
				return ARG.isDef(name) ? ARG.toDef(name) : super.getFormName();
			}
		};
	}
}
