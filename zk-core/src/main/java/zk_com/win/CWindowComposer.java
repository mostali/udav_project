
package zk_com.win;

import mpu.IT;
import mpc.exception.NotifyMessageRtException;
import mpc.map.UMap;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;
import zk_com.core.IZCom;
import zk_form.notify.NtfDiv;
import zk_form.notify.NtfLevel;
import zk_page.ZKCFinder;
import zk_page.ZulLoader;

import java.util.Map;

//https://zkfiddle.org/sample/iv1qfn/2-ESC-on-modal-windows#source-4
public class CWindowComposer extends GenericForwardComposer {

	public static final String RSRC_ZUL = "/_com/modal-window/modal-window.zul";


	public static Window show(String title, NotifyMessageRtException message, Component... parent) {
		return showNotify(title, message.getMessage(), NtfLevel.of(message.type()), parent);
	}

	public static Window showNotify(String title, String message, NtfLevel ntfLevel, Component... parent) {
		return show(title, NtfDiv.ofMsg(message, ntfLevel), parent);
	}

	public static Window show(String title, Component child, Component... parent) {
		Map context = UMap.of("title", title, "child", child);
		Component component = ZulLoader.loadComponentFromRsrc(RSRC_ZUL, context, parent);
		Window com = ZKCFinder.findSibling(component, Window.class, true).get(0);
		com.doModal();
		return com;
	}

	public static Window _show(IZCom child, Component... parent) {
		return (Window) ZulLoader.loadComponentFromRsrc(RSRC_ZUL, UMap.of("child", child), parent);
	}

//	private Window secondModalWindow;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

//		Window pn = secondModalWindow;
		Window comWin = (Window) comp;

		IZCom child = (IZCom) UMap.getAs(super.arg, "child", Component.class, null);

		comWin.appendChild(child.com());

		String title = child.attr_str("_title", null);

		if (title != null) {
			Win0.getCap0OrCreate(comWin, true).appendChild(new Label(title));
		}
		Boolean isCloasable = child.attr_is("_closable", false);
//		isCloasable=true;
		comWin.setClosable(isCloasable);

		comWin.setSizable(child.attr_is("_sizable", false));

		if (child.attr_is("_modal", false)) {
			comWin.doModal();
		} else if (child.attr_is("_hl", false)) {
			comWin.doHighlighted();
		} else if (child.attr_is("_ovl", false)) {
			comWin.doOverlapped();
		} else if (child.attr_is("_popup", false)) {
			comWin.doPopup();
		} else if (child.attr_is("_embed", false)) {
			comWin.doEmbedded();
		}


		Component _captionCom = (Component) child.attr_as("_caption", Component.class, null);
		if (_captionCom != null) {
			IT.isNotType(_captionCom, Caption.class, "caption is illegal component type");
			Win0.getCap0OrCreate(comWin, true).appendChild(_captionCom);
//			Caption caption0 = comWin.getCaption();
//			if (caption0 == null) {
//				comWin.appendChild(caption0 = new Caption());
//			}
//			caption0.appendChild(_caption);
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