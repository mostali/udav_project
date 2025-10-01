package zk_form.control.breadcrumbs;

import mpu.pare.Pare;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_notes.events.ANMP;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.ZKR;
import zk_page.core.SpVM;

public class BreadLn extends Ln {

//	public BreadLn(String name) {
//		super(name);
//		onCLICK((e) -> ZKR.redirectToHome());
//	}

	public BreadLn(String name) {
		this(name, null);

	}

	public BreadLn(String name, SerializableEventListener action) {
		super(name);
		if (action != null) {
			onCLICK(action);
		}
		decoration_none();
		padding(5);
	}

//	public BreadLnNew(Pare sdn) {
//		this(toName(sdn), BreadPos.ROOT0);
//	}

//	public BreadLnNew(String name, BreadPos level) {
//		this(null, name, level);
//	}

//	public BreadLn(Pare sdn, String name) {
//		super(name);
//
////		Object[] topLeftBc = BreadPos.getAdptiveTopLeftBreadCrumbs(level);
//
//		font_bold_nice(20);
//
//		if (Sec.isNotAnonim()) {
//			if (SecMan.isAllowedEdit()) {
//				Menupopup0 menu = getOrCreateMenupopup(ZKC.getFirstWindow());
//				ANMP.applyPageLink(menu, sdn != null ? sdn : SpVM.get().ppi().sdnAny());
//			}
//		}
//	}

//	public static String toName(Pare<String, String> sdn) {
//		return (RSPath.isSd3Index(sdn.key()) ? "" : sdn.key()) + SYMJ.ARROW_RIGHT_SPEC + sdn.val();
//	}


}
