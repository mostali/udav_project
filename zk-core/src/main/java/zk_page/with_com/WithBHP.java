package zk_page.with_com;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import zk_page.ZKC;
import zk_page.panels.BottomHistoryPanel;

public interface WithBHP {

	default BottomHistoryPanel getWithBottomHistoryPanelOrAdd(Component... parent) {
		BottomHistoryPanel first = BottomHistoryPanel.findFirst(null);
		if (first != null) {
			return first;
		}
		BottomHistoryPanel bhpPanel = new BottomHistoryPanel();
		(ARG.isDef(parent) ? ARG.toDef(parent) : ZKC.getFirstWindow()).appendChild(bhpPanel);
		return bhpPanel;
	}
}
