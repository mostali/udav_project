package zk_com.base_ext;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;
import zk_page.ZKC;

public class EscTbx extends Textbox {

	private final Component closeIt;

	public EscTbx(Component... closeIt) {
		this.closeIt = ARG.toDefOr(null, closeIt);
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {

		Textbox tbx = this;
		tbx.setStyle("opacity:0;margin:-10000px;position:absolute");
		tbx.addEventListener(Events.ON_CANCEL, e -> ZKC.removeMeCheckWindowParentReturnParent(getClosableCom()));
		//		appendChild(tbx);
		tbx.focus();
		super.onPageAttached(newpage, oldpage);

	}

	protected Component getClosableCom() {
		return closeIt == null ? getParent() : closeIt;
	}
}
