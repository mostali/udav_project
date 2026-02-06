package zk_notes.control.maintbx;

import mpc.rfl.RFL;
import mpu.X;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import zk_com.base.Tbx;
import zk_com.base.Tbxm;
import zk_com.base_ctr.Div0;
import zk_form.notify.ZKI;
import zk_notes.control.maintbx.shconsole.ShConsolePanel;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecMan;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;
import zk_page.events.ECtrl;
import zk_page.events.IPressed;
import zk_page.events.ZKE;

public class MainTbx extends Div0 {

	public static final String MAIN_TBX_MODE = "mode";
	public static final String MAIN_TBX_MODE_SH = "~";
	public static final int WIDTH_SH_MODE = 196;
	public static final int WIDTH_DEF_MODE = 96;
	public static final String MANUAL_RCRS = "/etc/manual/help-null-msg.md";
	public static final String MANUAL_RCRS_ADMIN = "/etc/manual/help-admin-msg.md";
	final Tbx tbxIn = new Tbx();
	final Tbxm tbxmIn = new Tbxm();
//	final Textbox tm2 = new Textbox();

	public static MainTbx findFirst(MainTbx... defRq) {
		return ZKCFinderExt.findFirst_inPage0(MainTbx.class, true, defRq);
	}

	@Override
	protected void init() {
		super.init();

		setCLASS(RFL.scn(MainTbx.class));

		{
			ZKS.FIXED(tbxIn);
			ZKS.RIGHT(tbxIn, "50pt");
			ZKS.TOP(tbxIn, "5pt");
			ZKS.OPACITY(tbxIn, 0.8);
			ZKS.TEXT_ALIGN(tbxIn, 1);
			ZKS.FONT_SIZE(tbxIn, "14pt");
			ZKS.HEIGHT(tbxIn, 24);
			tbxIn.setCLASS(RFL.scn(MainTbx.class) + "In");


			ZKS.FIXED(tbxmIn);
			ZKS.RIGHT(tbxmIn, "50pt");
			ZKS.TOP(tbxmIn, "5pt");
			ZKS.OPACITY(tbxmIn, 0.8);
			ZKS.TEXT_ALIGN(tbxmIn, 1);
			ZKS.FONT_SIZE(tbxmIn, "14pt");
			ZKS.HEIGHT(tbxmIn, 50);
			tbxmIn.setCLASS(RFL.scn(MainTbx.class) + "mIn");

//			ZKE.addEventListenerCtrl(this, Events.ON_CTRL_KEY, ZKE.SHORTCUT_STORE_ALT_Q, e -> {
//				AppCmdView.runMainCmd(e, sdn(), tbxmIn.getValue().trim(), true);
//			});

		}

		applyMode(false);

		tbxIn.onOK((Event e) -> {
			IPressed iPressed = IPressed.of(e);

			boolean isCtrlEnter = iPressed.ectrl() == ECtrl.CTRL && iPressed.ecode() == 13;

			if (tbxIn != null && isCtrlEnter) {
				tbxmIn.setValue(tbxIn.getValue());
				tbxIn.replaceWith(tbxmIn);
			} else {
				AppCmdView.runMainCmd(e, sdn(), tbxIn.getValue().trim(), isShMode());
			}
		});

		tbxIn.onDBLCLICK(e -> {
			if (isShMode() && ECtrl.CTRL == ECtrl.ofAsCtrl(e)) {
				ShConsolePanel.openSimple();
			}
		});

		tbxIn.onChangingAutoWidth(WIDTH_DEF_MODE);

		appendChilds(tbxIn);
//		appendChilds(tbxmIn);
//		appendChilds(tm2);
	}

//	SubmitEvent submitEvent;
//
//	public class SubmitEvent implements SerializableEventListener {
//
//		@Override
//		public void onEvent(Event event) throws Exception {
//			X.p("EEEE:"+ECtrl.of(event));
//		}
//	}

	private boolean isShMode() {
		return MainTbx.this.attr_get(MAIN_TBX_MODE, "").equals(MAIN_TBX_MODE_SH);
	}

	void applyMode(boolean isShMode) {
		tbxIn.setValue("");
		if (isShMode) {
			ZKS.WIDTH(tbxIn, WIDTH_SH_MODE);
//			tbxIn.placeholder(MAIN_TBX_MODE_SH);
			attr_put(MAIN_TBX_MODE, MAIN_TBX_MODE_SH);
		} else {
			ZKS.WIDTH(tbxIn, WIDTH_DEF_MODE);
			WebUsr webUsr = WebUsr.get(null);
			if (webUsr != null) {
//				tbxIn.placeholder(webUsr.getLogin());
				tbxIn.placeholder(SecMan.isOwnerOrAdmin() ? "??" : "?");
			}
			attr_rm(MAIN_TBX_MODE);
		}

	}


}
