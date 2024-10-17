package zk_old_core.sd;

import mpu.X;
import mpt.TrmRsp;
import utl_web.UWeb;
import zk_os.AppZosWeb;
import zk_old_core.sd.core.Sd3ID;
import zk_old_core.sd.core.SdMan;
import zk_page.ZKR;
import zk_page.core.SpVM;

public class GoToSd {
	public static TrmRsp goTo(Sd3ID sd3ID) throws Sd3EE {
		return goTo(sd3ID.sd3(), sd3ID.page());
	}

	public static TrmRsp goTo(String sd3, String page) throws Sd3EE {
		String address = "http://" + getHostWith_Sd3_Port_Page(SdMan.ROOT_AILAS_SD3.equals(sd3) ? SdMan.ROOT_SD3_DIR :sd3 , page);
		ZKR.redirectToPage(address);
		return TrmRsp.OK("redirected:" + address);
	}

	public static String getHostWith_Sd3_Port_Page(String sd3, String page) throws Sd3EE {
		String hostSd3 = getHostWith_Sd3(sd3);
		hostSd3 = UWeb.appendPortToHostWithPath(hostSd3, true);
		return X.empty(page) ? hostSd3 : hostSd3 + "/" + page;
	}

	public static String getHostWith_Sd3(String sd3) {
		String host = UWeb.getHost();
		String hostWoSd3 = UWeb.getHostWoSd();
		SpVM spVM = SpVM.get();
		if (SdMan.ROOT_SD3_DIR.equals(sd3)) {
			String sd3_ = AppZosWeb.getSd3();
			return X.empty(sd3_) ? host : hostWoSd3;
		} else {
			//Sd3EE.checkExistSd3(sd3);
			String gotoSd3 = sd3 + ".";
			if (host.startsWith(gotoSd3)) {
				return host;
			}
			if (spVM.ppi().isRootDomain()) {
				return gotoSd3 + host;
			} else {
				return gotoSd3 + hostWoSd3;
			}
		}
	}
}
