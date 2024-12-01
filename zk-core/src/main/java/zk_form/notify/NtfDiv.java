package zk_form.notify;

import org.zkoss.zk.ui.Component;
import zk_com.base.Lb;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_form.ZkTheme;

public class NtfDiv extends Div0 {

	public final NtfLevel ntfLevel;

	public NtfDiv(NtfLevel ntfLevel) {
		super();
		this.ntfLevel = ntfLevel;
	}

	public static NtfDiv ofMsg(String msg, NtfLevel level) {
		return of(new Lb(msg), level);
	}

	public static NtfDiv ofHtml(String html, NtfLevel level) {
		return of(new Xml(html), level);
	}

	public static NtfDiv of(Component html, NtfLevel level) {
		NtfDiv divParent = new NtfDiv(level);
		divParent.setCLASS(ZkTheme.getClassStyle(level));
		Div0 divWithChild = new Div0(html);
		divParent.appendChild(divWithChild);
		divWithChild.setCLASS(ZkTheme.DIV_NOTIFY_CHILD);
		return divParent;
	}
}
