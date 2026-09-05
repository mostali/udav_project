package zk_form.ext;

import zk_com.base_ctr.Div0;

public class ClbBt extends Div0 {
	public static String toDivBt(String html) {
		return "<div>" + html + "</div><button onClick='copyToClb(this.previousSibling.innerHTML)'>copy</button>";
	}
//	public static String toBtHtml(String aName,) {
//		return "<div>" + html + "</div><button onClick='copyToClb(this.previousSibling.innerHTML)'>copy</button>";
//	}
}
