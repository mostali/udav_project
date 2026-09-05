package zk_form.events;

import mpe.core.P;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import utl_spring.USpring;
import zk_os.sec.SecMan;
import zk_page.UPageSP;
import zk_page.ZKPage;

import java.util.Map;
import java.util.function.Supplier;

public interface IBoolEvent<T extends IBoolEvent> extends SerializableEventListener {

	//	final String JS_ON_BOOL_FUNC = "function onBool(data){zAu.send(new zk.Event(zk.Widget.$('.boolCom'), 'onBool', {'data':data?data:null}, {toServer:true}));1}";
	final String JS_ON_BOOL_FUNC = "function onBool(data){zAu.send(new zk.Event(zk.Widget.$('.boolCom'), 'onBool', data, {toServer:true}));}";

	//	static String JS_SEND_FROM_WINDOW = "function initWindowClickListener() {window.addEventListener('click', function(event) {const coordinates = {x: event.clientX,y: event.clientY};const json = JSON.stringify(coordinates);onBool(json);});}";
//	static String JS_SEND_FROM_WINDOW = "function initWindowClickListener() {window.addEventListener('click', function(event) {const coordinates = {x: event.clientX,y: event.clientY};const json = coordinates;onBool(json);});}";


	Supplier<String> JS_SEND_FROM_WINDOW = () -> USpring.readRsrcContent(IBoolEvent.class, "web/_js/sendCoor.js");

	static void initNewAndAppend(Window window) {
		if (SecMan.isAnonimUnsafe()) {
			return;
		}
		UPageSP.BoolEvent html = new UPageSP.BoolEvent();
		html.setClass("boolCom");
		window.appendChild(html);
		ZKPage.addJsTag(window.getPage(), JS_ON_BOOL_FUNC);
		ZKPage.addJsTag(window.getPage(), JS_SEND_FROM_WINDOW.get());
	}

	@Override
	default void onEvent(Event event) throws Exception {
		onBoolEventImpl(this, event);
	}

	static <T extends IBoolEvent> void onBoolEventImpl(T srcEvent, Event event) throws Exception {

//		Sys.say("bool eve");

		Map<String, Object> data = (Map) event.getData();
		P.p(data);

//
//		Integer x = (Integer) data.get("x");
//		x = UN.round20(x);
//
//		Integer y = (Integer) data.get("y");
//		y = UN.round20(y);
//
//		Boolean isCtrl = Boolean.parseBoolean(data.get("isCtrl") + "");
//		Boolean isShift = Boolean.parseBoolean(data.get("isShift") + "");


	}
}
