package zk_form.notify;

import org.zkoss.zk.ui.Component;
import zk_com.base.Lb;
import zk_com.base.Xml;
import zk_com.base_ctr.Div0;
import zk_form.ZkTheme;

public class NotifyLevelDiv extends Div0 {

	public final ZKI.Level ntfLevel;

	public NotifyLevelDiv(ZKI.Level ntfLevel) {
		super();
		this.ntfLevel = ntfLevel;
	}

	public static NotifyLevelDiv ofMsg(String msg, ZKI.Level level) {
		return of(new Lb(msg), level);
	}

	public static NotifyLevelDiv ofHtml(String html, ZKI.Level level) {
		return of(new Xml(html), level);
	}

	public static NotifyLevelDiv of(Component html, ZKI.Level level) {
		NotifyLevelDiv divParent = new NotifyLevelDiv(level);
		divParent.setCLASS(ZkTheme.getClassStyle(level));
		Div0 divWithChild = new Div0(html);
		divParent.appendChild(divWithChild);
		divWithChild.setCLASS(ZkTheme.DIV_NOTIFY_CHILD);
		return divParent;
	}
}
