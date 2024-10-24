package zk_form.events;

import mpc.types.ruprops.RuProps;
import mpc.num.UNum;
import mpe.state_rw.IStateRw;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import zk_old_core.old.per_win.IPerWinStateRw;
import zk_old_core.old.per_win.PerDims;
import zk_old_core.old.per_win.PerWinStateRw;
import zk_page.ZKS;

import java.nio.file.Path;
import java.util.Map;

public interface IPerDndEvent extends IDndSerializableEventListener {

	static void recoveryState(IDndSerializableEventListener eventableComponent, Path path) {
		HtmlBasedComponent htmlCom = (HtmlBasedComponent) eventableComponent;

		IPerWinStateRw stateRw = new PerWinStateRw(path);

		{
//			RuProps props = stateRw.read();
			String left = stateRw.read(IPerWinStateRw.LTWH.LEFT, "20px");
//			String left = ZKS.PerDims.read_left(props);
			String top = stateRw.read(IPerWinStateRw.LTWH.TOP, "20px");
//			String top = ZKS.PerDims.read_top(props);
			String width = stateRw.read(IPerWinStateRw.LTWH.WIDTH, "200px");
//			String width = ZKS.PerDims.read_width(props);
			String height = stateRw.read(IPerWinStateRw.LTWH.HEIGHT, "200px");
//			String height = ZKS.PerDims.read_height(props);
			ZKS.of(htmlCom).abs().left(left).top(top).width(width).height(height).zindex(1000);
		}

		IDndSerializableEventListener.onBindJs(htmlCom);

		htmlCom.addEventListener(IDndSerializableEventListener.EVENT, eventableComponent);

	}

	default IStateRw<RuProps> getStateRw() {
		return null;
	}

	@Override
	default void onDndEvent(Event event) throws Exception {
		onDndEventImpl(this, event);
	}

	static void onDndEventImpl(IDndSerializableEventListener dndEvent, Event event) throws Exception {

		Map<String, Object> data = (Map) event.getData();

		Integer x = (Integer) data.get("x");
		x = UNum.round20(x);

		Integer y = (Integer) data.get("y");
		y = UNum.round20(y);

		Boolean isCtrl = Boolean.parseBoolean(data.get("isCtrl") + "");
		Boolean isShift = Boolean.parseBoolean(data.get("isShift") + "");

		//JSoon.fromString(event.getData()+"").first("x").getAsJsonPrimitive().getAsInt();
//		ZKNW.info("drop1:" + event + ":X(%s) Y(%s) isCtrl(%s) isShift (%s)", x, y, isCtrl, isShift);
//
		if (dndEvent instanceof IPerDndEvent) {
			IStateRw<RuProps> stateRw = ((IPerDndEvent) dndEvent).getStateRw();

			RuProps props = stateRw.read();

			if (isCtrl) {
				PerDims.write_left(props, x + "px");
				PerDims.write_top(props, y + "px");
			} else if (isShift) {

				String left = PerDims.read_left(props);
				String top = PerDims.read_top(props);

				{
					Integer left0 = ZKS.px(left);
					int wN = x - left0;
					if (wN > 0) {
						wN = UNum.round20(wN);
						PerDims.write_width(props, wN + "px");
					}
//				ZKNW.info("wN:" + wN);

				}
				{
					Integer top0 = ZKS.px(top);
					int hN = y - top0;
					if (hN > 0) {
						hN = UNum.round20(hN);
						PerDims.write_height(props, hN + "px");
					}
//				ZKNW.info("hN:" + hN);
				}
			}

			stateRw.write(props);
		}


	}
}
