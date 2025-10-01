package zk_form.events;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import mpu.IT;
import mpc.json.UGson;
import org.zkoss.json.JSONArray;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.elements.Pos4TRBL;
import zk_form.head.IHeadCom;
import zk_form.head.IHeadRsrc;
import zk_form.head.StdHeadLib;
import zk_page.ZKC;
import zk_page.ZKPage;

import java.util.Map;

//	@Override
//	public void onTouchEvent(Event event) throws Exception {
//		Info info = ITouchEvent.getDataInfo(event);
//		Rect rect = ITouchEvent.getDataRect(event);
//	}
public interface ITouchEvent extends SerializableEventListener, IHeadCom {

	public static final String EVENT = "onClickInfo";

	IHeadRsrc[] HEAD_RSCS = {StdHeadLib.CLICK_INFO};

	static void initEvent(IHeadCom com, boolean overrided_onClickInfo_OR_onTouchEvent) {
		ITouchEvent.onBindJs((Component) com);
		ZKPage.renderHeadRsrc(com);
		if (!overrided_onClickInfo_OR_onTouchEvent) {
			((Component) com).addEventListener(EVENT, (ITouchEvent) com);
		}
	}

	@Override
	default IHeadRsrc[] getHeadRsrc() {
		return HEAD_RSCS;
	}

	static void onBindJs(Component com) {
		com.setWidgetListener("onBind", "initWatchSwipeById(this.uuid);");
	}

//	public static JSONArray getDataInfo(Event event) {
//		Map map = (Map) event.getData();
//		return (JSONArray) map.get("info");
//	}

	public static Rect getDataRect(Event event) {
		return Rect.of((Map) ((Map) event.getData()).get("rect"));
	}

	public static Pos4TRBL[] getPos4(Event event) {
		Info info = getDataInfo(event);
		Pos4TRBL tb = info.tb == 0 ? null : IT.notZero(info.tb) > 0 ? Pos4TRBL.TC : (info.tb < 0 ? Pos4TRBL.BC : null);
		Pos4TRBL lr = info.lr == 0 ? null : IT.notZero(info.lr) > 0 ? Pos4TRBL.LC : (info.tb < 0 ? Pos4TRBL.RC : null);
		return new Pos4TRBL[]{tb, lr};
	}

	public static Info getDataInfo(Event event) {
		JSONArray array = (JSONArray) ((Map) event.getData()).get("info");
		return Info.of(array);
	}

	@ToString
	@RequiredArgsConstructor
	public class Rect {
		public final int x, y, width, height, top, right, bottom, left;

		public static Rect of(Map eventJson) {
			return UGson.of(eventJson, Rect.class);
		}
	}

	@ToString
	@RequiredArgsConstructor
	public class Info {
		public final int tb, lr, ms;

		public static Info of(JSONArray eventJson) {
			return new Info((Integer) eventJson.get(0), (Integer) eventJson.get(1), (Integer) eventJson.get(2));
		}
	}


	@Override
	default void onEvent(Event event) throws Exception {
		onTouchEvent(event);
	}

	default void onTouchEvent(Event event) throws Exception {

	}

}
