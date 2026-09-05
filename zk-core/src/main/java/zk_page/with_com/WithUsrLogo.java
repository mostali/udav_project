package zk_page.with_com;

public interface WithUsrLogo {

//	default UsrLogoCom getUsrLogoOrCreate() {
//		UsrLogoCom first = UsrLogoCom.findFirst(null);
//		if (first != null) {
//			return first;
//		}
//		WebUsr user = Sec.getUser();
//		String name = Sec.isAnonim() ? "?" : user.getFID().toString();
//		first = new UsrLogoCom(name, null);
//		return first;
//	}
//
//
//	public static class UsrLogoCom extends DivWith {
//
//		final String lb_OR_title, src;
//
//		public static UsrLogoCom findFirst(UsrLogoCom... defRq) {
//			return ZKComFinder.findFirst(UsrLogoCom.class, false, defRq);
//		}
//
//		public UsrLogoCom(String lb_OR_title, String src) {
//			super();
//			this.lb_OR_title = lb_OR_title;
//			this.src = src;
//		}
//
//		SimpleMenupopup simpleMenupopup;
//
//		public SimpleMenupopup getContextMenu() {
//			return simpleMenupopup;
//		}
//
//		@Override
//		protected void init() {
//			super.init();
//			ZKS.STYLE(this, "position:absolute;top:2rem;right:6rem");
//			IZkCom el;
//			if (src != null) {
//				Img img = new Img(src);
//				img.setWidth("50px");
//				el = img;
//				if (X.notEmpty(lb_OR_title)) {
//					el.setTITLE(lb_OR_title);
//				}
//			} else if (X.notEmpty(lb_OR_title)) {
//				el = new Lb(lb_OR_title);
//			} else {
//				throw new FIllegalStateException("Error init logo ( set label or src)");
//			}
//			simpleMenupopup = appendMenupopup(el.comX());
//			simpleMenupopup.addMenuitem(SYMJ.LOGOUT, ZKR.logout());
//		}
//	}
}
