//package zk_form.events;
//
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.SerializableEventListener;
//import zk_form.head.IHeadCom;
//import zk_form.head.IHeadRsrc;
//import zk_form.head.StdHeadLib;
//
//public interface IDndSerializableEventListener extends SerializableEventListener, IHeadCom {
//
//	public static final String EVENT = "onChangeXY";
//
//	IHeadRsrc[] HEAD_RSCS = {StdHeadLib.DND_SIMPLE};
//
//	@Override
//	default IHeadRsrc[] getHeadRsrc() {
//		return HEAD_RSCS;
//	}
//
//	public static void onBindJs(Component com) {
//		com.setWidgetListener("onBind", "dragElementById(this.uuid);");
//	}
//
//	@Override
//	default void onEvent(Event event) throws Exception {
//		onDndEvent(event);
//	}
//
//	void onDndEvent(Event event) throws Exception;
//
//
//}
