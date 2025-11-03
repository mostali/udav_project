package zk_page.events;

import mpu.Sys;
import mpu.core.ARG;
import mpu.core.ARR;
import mpc.rfl.RFL;
import mpc.str.condition.StringConditionType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.impl.XulElement;
import utl_web.UWeb;
import zk_form.notify.ZKI;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Patterns/Keystroke_Handling
public class ZKE {
	public static final String SHORTCUT_STORE_ALT_V = "@v";
	public static final String SHORTCUT_STORE_CTRL_S = "^s";
	public static final String SHORTCUT_STORE_ALT_Q = "@q";
	public static final SerializableEventListener EVENT_LISTENER_INFO_CONSOLE = event -> Sys.p("Event:" + event);

	public static final SerializableEventListener EVENT_LISTENER_INFO_ZLOG = event ->
	{
		if (event instanceof DropEvent) {
			//https://webref.ru/html/attr/draggable
			DropEvent de = (DropEvent) event;
			ZKI.log("DropEvent:" + de.getPageX() + "x" + de.getPageY() + ":" + de.getDragged() + ">>>" + de.getTarget() + " <<< " + event);
		} else {
			ZKI.log("Event:" + event);
		}
	};


	public static void addEventListenerAll(Component com, String... exclude) {
		createEvents(null, exclude).stream().forEach(e -> com.addEventListener(e, EVENT_LISTENER_INFO_ZLOG));
	}

	public static void addEventListenerAll(Component com, SerializableEventListener serializableEventListener, String... exclude) {
		createEvents(null, exclude).stream().forEach(e -> com.addEventListener(e, serializableEventListener));
	}


	private static List<String> createEvents(String[] events, String[] exclude) {
		List<String> eventsValues = null;
		if (ARG.isDefNF(events) || events.length == 0) {
			eventsValues = getAllDefault();
		} else {
			eventsValues = Arrays.asList(events);
		}
		return eventsValues.stream().filter(e -> !ARR.contains(exclude, e)).collect(Collectors.toList());
	}

	private static List allDefault;

	public static List getAllDefault() {
		return allDefault == null ? allDefault = RFL.fieldValuesSt(Events.class, StringConditionType.STARTS.buildCondition("ON_"), false) : allDefault;
	}

	public static void addEventListener(Component w, EventListener eventListener, String... events) {
		List<String> eventsValues = createEvents(events, ARR.EMPTY_ARGS);
		for (String event : eventsValues) {
			w.addEventListener(event, eventListener != null ? eventListener : new EventListener() {
				@Override
				public void onEvent(Event event) throws Exception {
					ZKI.showMsgBottomRightFast_INFO("EVENT:" + event);
				}
			});
		}
	}


	public static void addEventListenerCtrl(XulElement xulElement, String event, String shortcut_keys, EventListener eventListener) {
		xulElement.setCtrlKeys(shortcut_keys);
		xulElement.addEventListener(event, eventListener);
	}

	public static void addEventListenerCtrl(XulElement xulElement, EventListener eventListener, ZKEventKey ctrlKeys, String... events) {
		addEventListenerCtrl(xulElement, eventListener, ctrlKeys.toString(), events);
	}

	public static void addEventListenerCtrl(XulElement xulElement, EventListener eventListener, String ctrlKeys, String... events) {
		List<String> eventsValues = createEvents(events, ARR.EMPTY_ARGS);
		xulElement.setCtrlKeys(ctrlKeys);
		for (Object event : eventsValues) {
			xulElement.addEventListener((String) event, eventListener != null ? eventListener : new SerializableEventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					ZKI.showMsgBottomRightFast_INFO("EV-CTRL:" + event);
				}
			});
		}
	}
//
//	public static boolean isZkEventName(String key) {
//		return key != null && key.startsWith("on") && key.length() > 2 && Character.isUpperCase(key.charAt(2)) && ZKE.getAllDefault().contains(key);
//	}

	public static String getSensEventDesctopOrMobile() {
		return UWeb.isMobile() ? Events.ON_CLICK : Events.ON_MOUSE_OVER;
	}

	public static boolean isWithCtrl(Event e, int keys) {
		return e instanceof MouseEvent && ((MouseEvent) e).getKeys() == keys;
	}

	public static void sendPostEventClick(HtmlBasedComponent com) {
		sendPostEvent(com, Events.ON_CLICK);
	}

	public static void sendPostEvent(HtmlBasedComponent com, String event) {
		Events.postEvent(event, com, null);
	}
}
