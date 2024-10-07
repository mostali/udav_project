package zk_notes.control;

import mpu.IT;
import mpu.str.USToken;
import org.zkoss.zk.ui.event.Event;
import zk_com.base.Tbx;
import zk_os.db.net.WebUsr;
import zk_os.sec.SecMan;
import zk_page.ZKS;
import zk_page.node.fsman.NodeFileTransferMan;

public class NewFormTbx extends Tbx {

//	public static Function<String, Pare3<Boolean, String, String>> authFunc = (tk) -> Pare3.of(false, "ni", "{}");

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

		onOK((Event e) -> {

			if (SecMan.isAdminOrOwner() || SecMan.isAllowedEdit()) {
				boolean hasBody = getValue().contains(":");
				String[] newName = hasBody ? USToken.two(getValue(), ":") : new String[]{getValue(), ""};
				NodeFileTransferMan.addNewFormAndOpenUX(IT.isFilename(newName[0]), newName[1]);
			}
//			Pare3<Boolean, String, String> authRslt = authFunc.apply(getValue());
//			if (authRslt.key()) {
//			WebUsr webUsr = Sec.setAuthByToken(authRslt);
//			if (webUsr != null) {
//				placeholder(webUsr.login);
//				Clients.showNotification("ok", "info", null, "bottom_right", 5000);
//				ZKR.restartPage();
//			} else {
//				Clients.showNotification("auth error", "error", null, "bottom_right", 5000);
//			}
//			} else {
//				Clients.showNotification(authRslt.val(), "EXPIRED".equals(authRslt.val()) ? "warning" : "error", null, "bottom_right", 5000);
//			}
		});

		WebUsr webUsr = WebUsr.get(null);

		if (webUsr != null) {
			placeholder(webUsr.login);
		}

	}

}
