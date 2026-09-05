package zk_form;

import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpc.exception.FIllegalStateException;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Menuitem;
import zk_com.base.Img;
import zk_com.base.Lb;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_com.core.IZCom;
import zk_notes.AxnTheme;
import zk_os.db.net.WebUsr;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;
import zk_page.with_com.WithUsrLogo;
import zk_page.events.ZKEvents;

public interface WithLogo {

	default LogoCom getLogoDefault() {
		LogoCom first = LogoCom.findFirst(null);
		if (first != null) {
			return first;
		}
//		first = new LogoCom(null, "/_img/logo.png");
		first = new LogoCom(null, "/_img/logo.png");
		return first;
	}

	default LogoCom getLogoOrAdd(Component... parent) {

		WithLogo.LogoCom logoCom = ((WithLogo) this).getLogoDefault();
		(ARG.isDef(parent) ? ARG.toDef(parent) : ZKC.getFirstWindow()).appendChild(logoCom);

		Menupopup0 menu = logoCom.getContextMenu();

		if (this instanceof WithUsrLogo) {

			menu.add_______();

			WebUsr user = Sec.getUser();
			boolean isAnonim = SecMan.isAnonimUnsafe();
			if (isAnonim) {
				menu.addMI(SYMJ.USER + " Login", ZKEvents.getEventLogin());
			} else {
				String name = user.getUserName(5) + " ( " + user.getFID().toString() + " )";
				menu.addMI(SYMJ.USER + " " + name, ZKEvents.getEventOpenUserSettings());
				menu.addMI(SYMJ.LOGOUT + " Logout", ZKEvents.getEventLogout());
			}

		}
		return logoCom;
	}


	public static class LogoCom extends Div0 {

		final String lb_OR_title, src;
		final boolean asIcon;

		public static LogoCom findFirst(LogoCom... defRq) {
			return ZKCFinderExt.findFirst_inPage0(LogoCom.class, true, defRq);
		}

		public static Menupopup0 getMainMenu() {
			return findFirst().getContextMenu();
		}

		public LogoCom(String lb_OR_title, String src) {
			super();
			this.lb_OR_title = lb_OR_title;
			this.src = src;
			asIcon = src == null;
		}

		protected Menupopup0 menu;

		public Menupopup0 getContextMenu() {
			return menu;
		}

		public void addLogoMenuItem(SerializableEventListener listener, String caption, String... title_) {
			Pare<Menuitem, Menupopup0> menuitemSimpleMenupopupPare = getContextMenu().addMI(caption, listener);
			String title = ARRi.first(title_, null);
			if (X.notEmpty(title)) {
				menuitemSimpleMenupopupPare.key().setTooltip(title);
			}
		}

		protected IZCom targetView;

		@Override
		protected void init() {
			super.init();
			if (asIcon) {
				ZKS.STYLE(this, "position:fixed;top:0.5rem;right:2rem;opacity:0.93;");
			} else {
				ZKS.STYLE(this, "position:fixed;top:0rem;right:2rem;opacity:0.39");
			}
			setZindex(AxnTheme.ZI_LOGO);

			if (!asIcon) {
				Img img = new Img(src);
				img.setWidth("30px");
				img.setSTYLE("padding-top:0.5rem");
				targetView = img;
				if (X.notEmpty(lb_OR_title)) {
					targetView.title(lb_OR_title);
				}
			} else if (X.notEmpty(lb_OR_title)) {
				targetView = new Lb(lb_OR_title);
				targetView.addSTYLE("font-size:20px");
				targetView.title(Sec.getUser().toInfo());
			} else {
				throw new FIllegalStateException("Error init logo ( set label or src)");
			}

			menu = appendMenupopup(targetView.comX());

		}
	}

}
