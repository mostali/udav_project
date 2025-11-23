package zk_notes.control.maintbx;

import mpc.rfl.RFL;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Tbx;
import zk_com.base_ctr.Div0;
import zk_notes.control.maintbx.shconsole.ShConsolePanel;
import zk_os.db.net.WebUsr;
import zk_page.ZKCFinderExt;
import zk_page.ZKS;
import zk_page.events.ECtrl;

public class MainTbx extends Div0 {

	public static final String MAIN_TBX_MODE = "mode";
	public static final String MAIN_TBX_MODE_SH = "~";
	public static final int WIDTH_SH_MODE = 196;
	public static final int WIDTH_DEF_MODE = 96;
	public static final String MANUAL_RCRS = "/etc/manual/help-null-msg.md";
	final Tbx tbxIn = new Tbx();
//	final Tbxm tbxmOut = new Tbxm();

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

		}

		applyMode(false);

		tbxIn.onOK((Event e) -> AppCmdView.runMainCmd(sdn(), tbxIn.getValue().trim(), isShMode()));

		tbxIn.onDBLCLICK(e -> {
			if (isShMode() && ECtrl.CTRL == ECtrl.of(e)) {
				ShConsolePanel.openSimple();
			}
		});

		tbxIn.onChangingAutoWidth(WIDTH_DEF_MODE);

		appendChilds(tbxIn);
	}

	private boolean isShMode() {
		return MainTbx.this.attr_get(MAIN_TBX_MODE, "").equals(MAIN_TBX_MODE_SH);
	}

	void applyMode(boolean isShMode) {
		tbxIn.setValue("");
		if (isShMode) {
			ZKS.WIDTH(tbxIn, WIDTH_SH_MODE);
			tbxIn.placeholder(MAIN_TBX_MODE_SH);
			attr_put(MAIN_TBX_MODE, MAIN_TBX_MODE_SH);
		} else {
			ZKS.WIDTH(tbxIn, WIDTH_DEF_MODE);
			WebUsr webUsr = WebUsr.get(null);
			if (webUsr != null) {
//				tbxIn.placeholder(webUsr.getLogin());
				tbxIn.placeholder("?");
			}
			attr_rm(MAIN_TBX_MODE);
		}

	}


}
