package zk_form.control.breadcrumbs.qview;

import mpc.str.sym.SYMJ;
import mpu.pare.Pare;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_notes.events.ANMP;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.core.SpVM;
import zk_page.index.RSPath;

public class QBreadLn extends Ln {
	public QBreadLn(Pare sdn) {
		this(toName(sdn), QBreadPos.ROOT0);
	}

	public QBreadLn(String name, QBreadPos level) {
		this(null, name, level);
	}

	public QBreadLn(Pare sdn, String name, QBreadPos level) {
		super(name);

		Object[] topLeftBc = QBreadPos.getAdptiveTopLeftBreadCrumbs(level);

		absolute().top_left(topLeftBc[0], topLeftBc[1]).font_bold_nice(20);

		if (Sec.isNotAnonim()) {
			if (SecMan.isAllowedEdit()) {
				Menupopup0 menu = getOrCreateMenupopup(ZKC.getFirstWindow());
				ANMP.applyPageLink(menu, sdn != null ? sdn : SpVM.get().ppi().sdnAny());
			}
		}
	}

	public static String toName(Pare<String, String> sdn) {
		return (RSPath.isSd3Index(sdn.key()) ? "" : sdn.key()) + SYMJ.ARROW_RIGHT_SPEC + sdn.val();
	}


}
