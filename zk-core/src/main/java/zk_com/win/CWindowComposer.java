
package zk_com.win;

import mpc.map.MAP;
import mpu.IT;
import mpu.core.ARR;
import mpu.core.ENUM;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;
import zk_com.core.IZCom;
import zk_page.ZKCFinder;
import zk_page.ZulLoader;

import java.util.Collection;
import java.util.Map;

//https://zkfiddle.org/sample/iv1qfn/2-ESC-on-modal-windows#source-4
public class CWindowComposer extends GenericForwardComposer {

	public static final String RSRC_ZUL = "/_com/modal-window/modal-window.zul";

	public static Window show(String title, Component child, Component... parent) {
		Map context = MAP.of("title", title, "child", child);
		Component component = ZulLoader.loadComponentFromRsrc(RSRC_ZUL, context, parent);
		Window com = ZKCFinder.findSibling(component, Window.class, true).get(0);
		com.doModal();
		return com;
	}

	public static Window _show(IZCom child, Component... parent) {
		return (Window) ZulLoader.loadComponentFromRsrc(RSRC_ZUL, MAP.of("child", child), parent);
	}


	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Window comWin = (Window) comp;

		IZCom child = (IZCom) MAP.getAs(super.arg, "child", Component.class, null);

		comWin.appendChild(child.com());

		String title = child.attr_str("_title", null);

		if (title != null) {
			Win0.getCap0OrCreate(comWin, true).appendChild(new Label(title));
		}
		Boolean isCloasable = child.attr_is("_closable", false);

		comWin.setClosable(isCloasable);

		comWin.setSizable(child.attr_is("_sizable", false));

		Window.Mode mode = (Window.Mode) child.attr_as("_mode", Window.Mode.class, null);
		if (mode != null) {
			comWin.setMode(mode);
		}

		Component _captionCom = (Component) child.attr_as("_caption", Component.class, null);
		if (_captionCom != null) {
			IT.isNotType(_captionCom, Caption.class, "caption is illegal component type");
			Win0.getCap0OrCreate(comWin, true).appendChild(_captionCom);
		}

		WinPos pos = (WinPos) child.attr_as("_pos", WinPos.class, null);

		if (pos != null) {
			pos.apply(comWin);
		}

		HideBy hideBy = (HideBy) child.attr_as("_hide", HideBy.class, null);
		if (hideBy != null) {
			hideBy.apply(comWin);
		}

		if (isCloasable) {
			comWin.addEventListener(Events.ON_CANCEL, (SerializableEventListener) event -> comWin.detach());
		}
	}


}