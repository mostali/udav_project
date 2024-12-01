package zk_form.control;

import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import mpu.pare.Pare;
import zk_com.base.Lb;
import zk_com.base_ctr.Menupopup0;
import zk_notes.ANM;
import zk_os.AFCC;
import zk_os.sec.ROLE;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.core.SpVM;

@Deprecated
public class BreadLb extends Lb {

	public BreadLb(String plane, int level, boolean... withPlane) {
		this(plane, null, level, withPlane);
	}

	BreadLb(String plane, String page, int level, boolean... withPlane) {
		super(page == null ? plane : (ARG.isDefEqTrue(withPlane) ? plane + SYMJ.ARROW_RIGHT_SPEC : "") + page);
		Object[] topLeftBc = BreadDiv.getAdptiveTopLeftBreadCrumbs(level);
		absolute().top_left(topLeftBc[0], topLeftBc[1]).font_bold_nice(20);

		if (!ROLE.toIcon().equals(plane)) {
			if (Sec.isNotAnonim()) {
				if (SecMan.isAllowedEdit()) {
					Menupopup0 menu = getOrCreateMenupopup(ZKC.getFirstWindow());
					if (page == null) {
						ANM.applyPlaneLink(menu, plane);
					} else {
						ANM.applyPageLink(menu, SpVM.get().sdn());
					}
				}
			}
		}
	}


	public static String toName(Pare sdn) {
		return (AFCC.SD3_INDEX_ALIAS.equals(sdn.key()) ? "" : sdn.key()) + SYMJ.ARROW_RIGHT_SPEC + sdn.val();
	}
}
