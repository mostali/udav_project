package zk_page;

import mpc.types.AtomicQDate;
import mpu.X;
import mpu.core.QDate;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

import java.util.concurrent.atomic.AtomicReference;

public class ZKCB_OpenHide {

	public static void addBeahaviourOn_byMaxHeight(HtmlBasedComponent triggerCom, HtmlBasedComponent hideOpenBodyCom, boolean openOrHide) {

		ZKS.addSTYLE(hideOpenBodyCom, X.f("%s overflow:hidden; transition: max-height 0.5s ease-in-out;", openOrHide ? "" : "max-height: 0px;"));

		AtomicReference<Boolean> lastState = new AtomicReference<>();

		triggerCom.addEventListener(Events.ON_CLICK, e -> {
			boolean needOpen = lastState.get() == null || !lastState.get();
			String script;
			if (needOpen) {
				script =
//							"setTimeout(function() {" +
						"var el = document.getElementById('" + hideOpenBodyCom.getUuid() + "');"
								+ "if (el) { el.style.maxHeight = '3000px' };";
//							+ "}, 100);"
			} else {
				script = "" +
//						"setTimeout(function() {" +
						"var el = document.getElementById('" + hideOpenBodyCom.getUuid() + "');"
						+ "if (el) el.style.maxHeight = '0px';"
//						+ "}, " + hideAfterDelayMS + ")" +
//						";";
				;
			}
			lastState.set(needOpen);
			Clients.evalJavaScript(script);
		});

//		triggerCom.addEventListener(Events.ON_MOUSE_OUT, e -> {
//		triggerCom.addEventListener(Events.ON_MOUSE_OUT, e -> {
//		triggerCom.addEventListener(Events.ON_DOUBLE_CLICK, e -> {
////			QDate now = QDate.now();
////			if (last.get() != null && now.diff(last.get()) < hideAfterDelayMS) {
////				return;
////            } else {
////				last.setNow();
////            }
//			String script = "setTimeout(function() {"
//					+ "var el = document.getElementById('" + hideOpenBodyCom.getUuid() + "');"
//					+ "if (el) el.style.maxHeight = '1px';"
//					+ "}, " + hideAfterDelayMS + ");";
//			Clients.evalJavaScript(script);
//
//			lastState.set(false);
//
//		});
	}

	public static void addBeahaviourOnOff2(HtmlBasedComponent triggerCom, HtmlBasedComponent hideOpenBodyCom, int hideAfterDelayMS) {

		ZKS.addSTYLE(hideOpenBodyCom, "max-height: 0px; overflow:hidden; transition: max-height 2.0s ease-in-out;");

//				AtomicLong atomicInteger = new AtomicLong(System.currentTimeMillis());
		AtomicQDate last = new AtomicQDate(null);
//		AtomicReference<Boolean> last = new AtomicReference<>();

		triggerCom.addEventListener(Events.ON_CLICK, e -> {
//		triggerCom.addEventListener(Events.ON_MOUSE_OVER, e -> {
//		triggerCom.addEventListener("onMouseEnter", e -> {
			String script =
//							"setTimeout(function() {" +
					"var el = document.getElementById('" + hideOpenBodyCom.getUuid() + "');"
							+ "if (el) { el.style.maxHeight = '1000px' };"
//							+ "}, 100);"
					;
			Clients.evalJavaScript(script);
		});

//		triggerCom.addEventListener(Events.ON_MOUSE_OUT, e -> {
//		triggerCom.addEventListener(Events.ON_MOUSE_OUT, e -> {
		triggerCom.addEventListener(Events.ON_DOUBLE_CLICK, e -> {
			QDate now = QDate.now();
			if (last.get() != null && now.diff(last.get()) < hideAfterDelayMS) {
				return;
			} else {
				last.setNow();
			}
			String script = "setTimeout(function() {"
					+ "var el = document.getElementById('" + hideOpenBodyCom.getUuid() + "');"
					+ "if (el) el.style.maxHeight = '1px';"
					+ "}, " + hideAfterDelayMS + ");";
			Clients.evalJavaScript(script);
		});
	}

	public static void addBeahaviourOnOff_byVisible(HtmlBasedComponent triggerCom, HtmlBasedComponent hideOpenCom, int hideAfterWithDelay) {
		triggerCom.addEventListener(Events.ON_MOUSE_OVER, e -> {
			String script = "setTimeout(function() {"
					+ "zk.Widget.$('" + hideOpenCom.getUuid() + "').setVisible(true);"
					+ "}, 100);";
			Clients.evalJavaScript(script);

		});
		triggerCom.addEventListener(Events.ON_MOUSE_OUT, e -> {
			String script = "setTimeout(function() {"
					+ "zk.Widget.$('" + hideOpenCom.getUuid() + "').setVisible(false);"
					+ "}, " + hideAfterWithDelay + ");";
			Clients.evalJavaScript(script);
		});
	}
}
