package zk_page.with_com;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import zk_notes.search.NoteBandboxLogo;
import zk_page.ZKC;

@Deprecated
public interface WithSearch {

	default NoteBandboxLogo getSearchBandboxOrAdd(Component... parent) {
		NoteBandboxLogo first = NoteBandboxLogo.findFirst(null);
		if (first != null) {
			return first;
		}
		NoteBandboxLogo mainTbx = new NoteBandboxLogo();
		(ARG.isDef(parent) ? ARG.toDef(parent) : ZKC.getFirstWindow()).appendChild(mainTbx);
		return mainTbx;
	}
}
