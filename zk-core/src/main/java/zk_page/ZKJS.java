package zk_page;

import mpu.X;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;

import java.util.List;
import java.util.stream.Collectors;

public class ZKJS {

	public static final String FN_PRETTIFY_JS = "run_prettify.js";
	public static final String TAG_ATTR = "type=\"text/javascript\"";


	public static void bindJS(HtmlBasedComponent com, String javascript) {
		com.setWidgetListener("onBind", javascript);
	}

	public static void bindJS(HtmlBasedComponent com, int ms, String javascript) {
		com.setWidgetListener("onBind", js_setTimeout(func(javascript), ms));
	}

	public static void setAction_ShowEffect(XulElement pnl, int showEffect_ms) {
		if (showEffect_ms >= 0) {
//			Sys.say("show");
			pnl.setAction("show: slideDown({duration:" + showEffect_ms + "})");
		}
	}

	public static void setAction_HideEffect(XulElement pnl, int showEffect_ms) {
		if (showEffect_ms >= 0) {
//			Sys.say("hide");
			pnl.setAction("show: slideUp({duration:" + showEffect_ms + "})");
		}
	}

	public static void eval_fadeOut(Component c) {
		ZKJS.eval_fadeOutById(c.getUuid());
	}

	public static void eval_fadeOutById(String idComponent) {
		eval_fadeOutBySelector("#" + idComponent, 2000);
	}

	public static void eval_fadeOutByClass(String classComponent) {
		eval_fadeOutBySelector("." + classComponent, 2000);
	}

	public static void eval_fadeOutBySelector(String selector, int duration) {
		String javascript = "document.querySelector(\"" + selector + "\").classList.add('ehio');";
		String javascript2 = "document.querySelector(\"" + selector + "\").classList.add('nout');";
		String javascript3 = "setTimeout(function(){ document.querySelector(\"" + selector + "\").remove(); } , " + duration + ");";
		Clients.evalJavaScript(javascript + javascript2 + javascript3);
	}

	public static void eval(String js, Object... args) {
		Clients.evalJavaScript(X.f(js, args));
	}


	public static void restartPageEvery(@ContextParam(ContextType.VIEW) Window window, int ms) {
		PageCtrl pageCtrl = (PageCtrl) window.getPage();
		String scriptReload = "setTimeout(function(){location.reload();}," + ms + ")";
		pageCtrl.addBeforeHeadTags("<script type=\"text/javascript\">" + scriptReload + "</script>");
	}

	public static void eval_sendEvent(String comId_Or_Class, String comEventListener, String dataJsonStr) {
		String func = func_zauSend(comId_Or_Class, comEventListener, dataJsonStr);
		String funcWithTimeout = js_setTimeout(func, 1000);
		Clients.evalJavaScript(funcWithTimeout);
	}

	public static String func(String jsBody, Object... args) {
		return "function(){" + X.f(jsBody, args) + "}";
	}


	public static String func_zauSend(String comId, String comEventListener, String dataJsonStr) {
		return func(js_zauSend(comId, comEventListener, dataJsonStr));
	}

	public static String js_zauSend(String comId, String comEventListener, String dataJsonStr) {
		return X.f("zAu.send(new zk.Event(zk.Widget.$('" + comId + "'), '" + comEventListener + "', " + dataJsonStr + ", {toServer:true}));", comId, comEventListener, dataJsonStr);
	}

	public static String js_addClass(String selectorCom, String className) {
		return "document.querySelector('" + selectorCom + "').classList.add('" + className + "');";
	}

	public static String js_removeClass(String selectorCom, String className) {
		return "document.querySelector('" + selectorCom + "').classList.remove('" + className + "');";
	}

//	public static String func_setTimeout(String js, int ms) {
//		return js_setTimeout(func(js), ms);
//	}

	public static String js_setTimeout(String func, int ms) {
		String jsTimeout = "setTimeout(" + func + "," + ms + ");";
		return jsTimeout;
	}

	public static void evalPrettyPrint() {
		Clients.evalJavaScript("PR.prettyPrint()");
	}

	public static void evalScrollIntoView(Component com) {
		Clients.scrollIntoView(com);
	}

	public static void evalConsoleLog(CharSequence msg, Object... args) {
		eval("console.log('%s')", X.f(msg, args));
	}

	public static String getAsJsArray(List<String> allEmojs) {
		return allEmojs.stream().filter(s -> !s.contains("'")).map(s -> "'" + s + "'").collect(Collectors.joining(",", "[", "]"));
	}
}
