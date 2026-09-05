//package zk_form.control.breadcrumbs.qview;
//
//import mpc.str.sym.SYMJ;
//import mpu.core.ARG;
//import zk_com.base.Lb;
//import zk_com.base_ctr.Menupopup0;
//import zk_notes.events.ANMD;
//import zk_notes.events.ANMP;
//import zk_os.sec.ROLE;
//import 
//import zk_page.ZKC;
//import zk_page.core.SpVM;
//
//@Deprecated
//public class QBreadLb extends Lb {
//
//	public QBreadLb(String plane, QBreadPos level, boolean... withPlane) {
//		this(plane, null, level, withPlane);
//	}
//
//	QBreadLb(String plane, String page, QBreadPos level, boolean... withPlane) {
//		super(page == null ? plane : (ARG.isDefEqTrue(withPlane) ? plane + SYMJ.ARROW_RIGHT_SPEC : "") + page);
//
//		Object[] topLeftBc = QBreadPos.getAdptiveTopLeftBreadCrumbs(level);
//
//		fixed().top_left(topLeftBc[0], topLeftBc[1]).font_bold_nice(20);
//
//		if (!ROLE.toIcon().equals(plane)) {
////			if (Sec.isNotAnonim()) {
//			if (SecMan.isAllowedEdit()) {
//				Menupopup0 menu = getOrCreateMenupopup(ZKC.getFirstWindow());
//				if (page == null) {
//					ANMD.applyPlaneLink(menu, plane);
//				} else {
//					ANMP.applyPageLink(menu, SpVM.get().sdn());
//				}
//			}
////			}
//		}
//	}
//
//
//
//}
