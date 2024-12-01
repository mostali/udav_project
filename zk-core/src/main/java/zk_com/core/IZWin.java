package zk_com.core;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.impl.XulElement;
import zk_com.win.HideBy;
import zk_com.win.WinPos;

public interface IZWin<T extends XulElement> extends IZCom<T> {

	default IZWin _closable(boolean... _closable) {
		attr_put("_closable", _closable.length == 0 ? true : _closable[0]);
		return this;
	}

	default IZWin _modal(boolean... doModal) {
		attr_put("_modal", doModal.length == 0 ? true : doModal[0]);
		return this;
	}

	default IZWin _ovl(boolean... ovl) {
		attr_put("_ovl", ovl.length == 0 ? true : ovl[0]);
		return this;
	}

	default IZWin _sizable(boolean... _sizable) {
		attr_put("_sizable", _sizable.length == 0 ? true : _sizable[0]);
		return this;
	}

	default IZWin _popup(boolean... _popup) {
		attr_put("_popup", _popup.length == 0 ? true : _popup[0]);
		return this;
	}

	default IZWin _embed(boolean... _popup) {
		attr_put("_embed", _popup.length == 0 ? true : _popup[0]);
		return this;
	}

	default IZWin _hl(boolean... _hl) {
		attr_put("_hl", _hl.length == 0 ? true : _hl[0]);
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
}
