package zk_form.control.breadcrumbs.qview;

import mpc.str.sym.SYMJ;
import mpe.call_msg.core.NodeID;
import mpu.pare.Pare;
import zk_com.base.Ln;
import zk_com.base_ctr.Menupopup0;
import zk_notes.events.ANMP;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.core.SpVM;

@Deprecated
public class QBreadLn extends Ln {
	public QBreadLn(Pare sdn) {
		this(toName(sdn), QBreadPos.ROOT0);
	}

	public QBreadLn(String name, QBreadPos level) {
		this(null, name, level);
	}

	public QBreadLn(Pare<String, String> sdn, String name, QBreadPos level) {
		super(name);

		Object[] topLeftBc = QBreadPos.getAdptiveTopLeftBreadCrumbs(level);

		absolute().top_left(topLeftBc[0], topLeftBc[1]).font_bold_nice(20);

		if (SecMan.isAllowedEditPlane(sdn.key())) {
			Menupopup0 menu = getOrCreateMenupopup(ZKC.getFirstWindow());
			ANMP.applyPageLink(menu, sdn != null ? sdn : SpVM.get().ppi().sdnAny());
		}
	}

	public static String toName(Pare<String, String> sdn) {
		return (NodeID.isPlaneAliasIndexOrEmpty(sdn.key()) ? "" : sdn.key()) + SYMJ.ARROW_RIGHT_SPEC + sdn.val();
	}


}
