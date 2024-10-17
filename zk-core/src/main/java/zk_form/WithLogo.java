package zk_form;

import mpu.X;
import mpu.core.ARRi;
import mpc.exception.FIllegalStateException;
import mpu.pare.Pare;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Menuitem;
import zk_com.base.Img;
import zk_com.base.Lb;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_page.ZKCF;
import zk_page.ZKS;

public interface WithLogo {

	default LogoCom getLogoOrCreate() {
		LogoCom first = LogoCom.findFirst(null);
		if (first != null) {
			return first;
		}
		first = new LogoCom(null, "/_img/logo.png");
		return first;
	}


	public static class LogoCom extends Div0 {

		final String lb_OR_title, src;

		public static LogoCom findFirst(LogoCom... defRq) {
			return ZKCF.findFirstIn_Page(LogoCom.class, true, defRq);
		}

		public LogoCom(String lb_OR_title, String src) {
			super();
			this.lb_OR_title = lb_OR_title;
			this.src = src;
		}

		protected Menupopup0 menu;

		public Menupopup0 getContextMenu() {
			return menu;
		}

		public void addLogoMenuItem(SerializableEventListener listener, String caption, String... title_) {
			Pare<Menuitem, Menupopup0> menuitemSimpleMenupopupPare = getContextMenu().addMenuitem(caption, listener);
			String title = ARRi.first(title_, null);
			if (X.notEmpty(title)) {
				menuitemSimpleMenupopupPare.key().setTooltip(title);
			}
		}

		protected IZCom targetView;

		@Override
		protected void init() {
			super.init();
			ZKS.STYLE(this, "position:absolute;top:0rem;right:2rem;opacity:0.39");
			setZindex(9999);

			if (src != null) {
				Img img = new Img(src);
				img.setWidth("30px");
				img.setSTYLE("padding-top:0.5rem");
				targetView = img;
				if (X.notEmpty(lb_OR_title)) {
					targetView.title(lb_OR_title);
				}
			} else if (X.notEmpty(lb_OR_title)) {
				targetView = new Lb(lb_OR_title);
			} else {
				throw new FIllegalStateException("Error init logo ( set label or src)");
			}

			menu = appendMenupopup(targetView.comX());

		}
	}

}
