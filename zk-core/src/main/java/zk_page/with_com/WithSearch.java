package zk_page.with_com;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import zk_notes.search.MainSearchPanel;
import zk_page.ZKC;

@Deprecated
public interface WithSearch {

	default MainSearchPanel getSearchBandboxOrAdd(Component... parent) {
		MainSearchPanel first = MainSearchPanel.findFirst(null);
		if (first != null) {
			return first;
		}
		MainSearchPanel mainTbx = new MainSearchPanel();
		(ARG.isDef(parent) ? ARG.toDef(parent) : ZKC.getFirstWindow()).appendChild(mainTbx);
		return mainTbx;
	}
}
