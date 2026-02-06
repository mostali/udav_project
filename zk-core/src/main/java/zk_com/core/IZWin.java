package zk_com.core;

import mpu.core.ARG;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.XulElement;
import zk_com.win.CWindowComposer;
import zk_com.win.HideBy;
import zk_com.win.WinPos;
import zk_form.events.DefAction;

public interface IZWin<T extends XulElement> extends IZCom<T> {

	default IZWin _closable(boolean... _closable) {
		attr_put("_closable", _closable.length == 0 ? true : _closable[0]);
		return this;
	}

	default IZWin _closableAction(DefAction... _closable) {
		attr_put("_closableAction", _closable.length == 0 ? true : _closable[0]);
		return this;
	}

	default IZWin _modal(Window.Mode... mode) {
		attr_put("_mode", ARG.toDefOr(Window.Mode.MODAL, mode));
		return this;
	}

	default IZWin _sizable(boolean... _sizable) {
		attr_put("_sizable", _sizable.length == 0 ? true : _sizable[0]);
		return this;
	}

	default IZWin _title(String title) {
		attr_put("_title", title);
		return this;
	}

	default IZWin _hide(HideBy hideBy) {
		attr_put("_hide", hideBy.name());
		return this;
	}

	default IZWin _pos(WinPos pos) {
		attr_put("_pos", pos.name());
		return this;
	}

	default IZWin _tooltip(String tooltip) {
		attr_put("_tooltip", tooltip);
		return this;
	}

	default IZWin _caption(Component caption) {
		attr_put("_caption", caption);
		return this;
	}

	default Window _showInWindowEmbed(Component... parent) {
		_modal(Window.Mode.EMBEDDED);
		return _showInWindow(parent);
	}

}
