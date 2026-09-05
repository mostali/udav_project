package zk_page.with_com;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import zk_notes.control.maintbx.MainTbx;
import zk_page.ZKC;

public interface WithMainTbx {

	default MainTbx getMainTbxOrAdd(Component... parent) {
		MainTbx first = MainTbx.findFirst(null);
		if (first != null) {
			return first;
		}
		MainTbx mainTbx = new MainTbx();
		(ARG.isDef(parent) ? ARG.toDef(parent) : ZKC.getFirstWindow()).appendChild(mainTbx);
		return mainTbx;
	}
}
