package zk_os;

import mpc.arr.QUEUE;
import mpc.log.L;
import mpe.core.ERR;
import mpe.core.P;
import mpe.rt.SLEEP;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.impl.SimpleDesktopCache;
import org.zkoss.zk.ui.impl.UiEngineImpl;
import org.zkoss.zk.ui.sys.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;
import zk_com.base.Lb;
import zk_page.ZKC;
import zk_page.ZKRPush;
import zk_page.ZKSession;

import java.util.Map;
import java.util.Queue;

//https://forum.zkoss.org/question/64048/zk-desktop/
//<session-config>
//	<device-type>ajax</device-type>
//    <automatic-timeout/> <!-- the same as <automatic-timeout>true</automatic-timeout> -->
//    <timeout-uri>/login.zul</timeout-uri>
//    <session-timeout>600</session-timeout>
//    <timer-keep-alive>false</timer-keep-alive>
//    <max-desktops-per-session>3</max-desktops-per-session>
//    <max-requests-per-session>6</max-requests-per-session>
//    <max-pushes-per-session>5</max-pushes-per-session>
//</session-config>

public class ZkApp {

	//	public static Queue<String> set = (Queue) QUEUE.cache_queue_sync_FILO(1000);
	public static Map<Desktop, SessionCtrl> map = QUEUE.cache_map_FILO(100);

	public static void add(Desktop desktop) {
		P.warnBig("off me");
		desktop.enableServerPush(true);
		SessionCtrl currentCtrl = SessionsCtrl.getCurrentCtrl();
		map.put(desktop, currentCtrl);
		L.info("newDesktop:" + desktop);
		L.info("newSession:" + currentCtrl);
	}

	public static Desktop getDesktop(String desktop) {
		return SessionsCtrl.getCurrentCtrl().getDesktopCache().getDesktop(desktop);
	}

//	static {
//		new Thread() {
//			@Override
//			public void run() {
//				try {
//					pushMsg("asd", null);
//					SLEEP.sec(10);
//				} catch (Exception ex) {
//					L.info("tttttt", ex);
//				}
//			}
//		}.start();
//	}

	public static void pushMsg(String evalNode, Exception e) {
		map.forEach((d, s) -> {
			if (d.isAlive()) {

//				d.getFirstPage().get/
//				ZKRPush.activePush(d);
				ZKRPush.activePushCom(d);

//				(Executions.getCurrent() != null) {
//				String desktopId = Executions.getCurrent().getDesktop().getId();
//				String desktopId = d.getId();
//				DesktopCache desktopCache = ((WebAppCtrl) WebApps.getCurrent()).getDesktopCache(Sessions.getCurrent());
//				s.getDesktopCache()
//				WebAppsCtrl.getCurrent().
				//					enableServerPushForThisTask();
//					And the enableServerPushForThisTask() is:
//				((DesktopCtrl) d).enableServerPush(true, "1");

				try {
					Clients.log(evalNode + "\n" + ERR.getStackTrace(e));
				} finally {

					ZKRPush.deactivePushCom(d);
				}

			}
		});
	}
}
