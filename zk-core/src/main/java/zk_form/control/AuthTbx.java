package zk_form.control;

import mpu.core.ARRi;
import mpu.pare.Pare3;
import mpu.str.SPLIT;
import mpu.str.USToken;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import zk_com.base.Tbx;
import zk_os.db.net.WebUsr;
import zk_os.sec.Sec;
import zk_os.sec.SecMan;
import zk_page.ZKR;
import zk_page.ZKS;
import zk_page.node.fsman.NodeFileTransferMan;

import java.util.function.Function;

public class AuthTbx extends Tbx {

	public static Function<String, Pare3<Boolean, String, String>> authFunc = (tk) -> Pare3.of(false, "ni", "{}");

	@Override
	protected void init() {
		super.init();

		ZKS.ABSOLUTE(this);
		ZKS.RIGHT(this, 80);
		ZKS.TOP(this, 5);
		ZKS.OPACITY(this, 0.8);
		ZKS.TEXT_ALIGN(this, 1);
		ZKS.FONT_SIZE(this, "14pt");
		ZKS.HEIGHT(this, 24);
		ZKS.WIDTH(this, 96);

//		onOK((Event e) -> {
//			String value = getValue().trim();
//			if (value.startsWith("!")) {
//				value = value.substring(1).trim();
//				String[] two = USToken.twoQk(value, " ");
//				String name = ARRi.first(two, 0, null);
//				String cnt = ARRi.first(two, 1, null);
//				NodeFileTransferMan.addNewFormAndOpenUX(name, cnt);
//				return;
//			}
//			Pare3<Boolean, String, String> authRslt = authFunc.apply(value);
//			if (authRslt.key()) {
//				WebUsr webUsr = Sec.setAuthByToken(authRslt);
//				if (webUsr != null) {
//					placeholder(webUsr.login);
//					Clients.showNotification("ok", "info", null, "bottom_right", 5000);
//					ZKR.restartPage();
//				} else {
//					Clients.showNotification("auth error", "error", null, "bottom_right", 5000);
//				}
//			} else {
//				Clients.showNotification(authRslt.val(), "EXPIRED".equals(authRslt.val()) ? "warning" : "error", null, "bottom_right", 5000);
//			}
//		});

		placeholder(SecMan.login(SecMan.ANONIM));


	}

}
