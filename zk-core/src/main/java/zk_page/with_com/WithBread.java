package zk_page.with_com;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import zk_form.control.breadcrumbs.BreadDiv;
import zk_os.core.Sdn;
import zk_page.ZKC;

public interface WithBread {

	default BreadDiv getBreadOrAdd(Sdn sdn, Component... parent) {
		BreadDiv first = BreadDiv.findFirst(null);
		if (first != null) {
			return first;
		}
		BreadDiv newBread = new BreadDiv(sdn);
		(ARG.isDef(parent) ? ARG.toDef(parent) : ZKC.getFirstWindow()).appendChild(newBread);
		return newBread;
	}
}
