//package zk_os;
//
//import mpc.env.APP;
//import utl_rest.StatusException;
//import zk_os.sec.Sec;
//
////Check Utility ( for App )
//
//public class UCApp {
//
//	public static void notAnonim404() {
//		boolean anonim = Sec.isAnonim();
//		if (anonim) {
//			if (APP.isDebugEnable()) {
//				throw StatusException.C404("anonim forbidden");
//			}
//			throw StatusException.C404();
//		}
//	}
//}
