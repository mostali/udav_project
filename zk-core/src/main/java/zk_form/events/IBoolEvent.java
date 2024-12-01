package zk_form.events;

import mpc.fs.fd.RES;
import mpe.core.P;
import org.springframework.util.ResourceUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zul.Window;
import utl_spring.USpring;
import zk_os.sec.Sec;
import zk_page.ZkPage;
import zklogapp.XsdValidatorPageSP;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.Map;

public interface IBoolEvent<T extends IBoolEvent> extends SerializableEventListener {

	//	final String JS_ON_BOOL_FUNC = "function onBool(data){zAu.send(new zk.Event(zk.Widget.$('.boolCom'), 'onBool', {'data':data?data:null}, {toServer:true}));1}";
	final String JS_ON_BOOL_FUNC = "function onBool(data){zAu.send(new zk.Event(zk.Widget.$('.boolCom'), 'onBool', data, {toServer:true}));}";

	//	static String JS_SEND_FROM_WINDOW = "function initWindowClickListener() {window.addEventListener('click', function(event) {const coordinates = {x: event.clientX,y: event.clientY};const json = JSON.stringify(coordinates);onBool(json);});}";
//	static String JS_SEND_FROM_WINDOW = "function initWindowClickListener() {window.addEventListener('click', function(event) {const coordinates = {x: event.clientX,y: event.clientY};const json = coordinates;onBool(json);});}";


	static String JS_SEND_FROM_WINDOW = USpring.readRsrc(IBoolEvent.class,"/web/appjs/sendCoor.js");
//	static String JS_SEND_FROM_WINDOW = RES.of(IBoolEvent.class, "/web/appjs/sendCoor.js").cat();

	public static void initNewAndAppend(Window window) {
		if (Sec.isAnonim()) {
			return;
		}
		XsdValidatorPageSP.BoolCom html = new XsdValidatorPageSP.BoolCom();
		html.setClass("boolCom");
		window.appendChild(html);

		ZkPage.addJsTag(window.getPage(), JS_ON_BOOL_FUNC);

		ZkPage.addJsTag(window.getPage(), JS_SEND_FROM_WINDOW);
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
