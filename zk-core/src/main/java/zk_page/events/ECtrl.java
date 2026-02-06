package zk_page.events;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARG;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.KeyEvent;


public enum ECtrl implements IPressed {
	DEFAULT, NO_MOUSE,
	CTRL_ALT_SHIFT_META,
	CTRL_ALT_SHIFT, CTRL_ALT_META, CTRL_SHIFT_META, ALT_SHIFT_META,//
	CTRL_ALT, CTRL_SHIFT, CTRL_META, SHIFT_ALT, SHIFT_META, ALT_META, //
	CTRL, ALT, SHIFT, META;

	public static final int ZKE_1_INSERT_CODE = 256;
	public static final int ZKE_ALT_CODE = 257;
	public static final int ZKE_CTRL_CODE = 258; //MouseEvent.CTRL_KEY
	public static final int ZKE_SHIFT_CODE = 260;
	public static final int ZKE_2_CTRL_SHIFT_CODE = 262;
	public static final int ZKE_2_CTRL_ALT_CODE = 259;
	public static final int ZKE_2_SHIFT_ALT_CODE = 261;
	public static final int ZKE_3_CTRL_SHIFT_ALT_CODE = 263;

	@Override
	public ECtrl ectrl() {
		return this;
	}

	public static ECtrl ofKeyEvent(KeyEvent e, ECtrl... defRq) {
		if (e.isCtrlKey() && e.isAltKey() && e.isShiftKey() && e.isMetaKey()) {
			return ECtrl.CTRL_ALT_SHIFT_META;
		} else if (e.isCtrlKey() && e.isShiftKey() && e.isMetaKey()) {
			return ECtrl.CTRL_SHIFT_META;
		} else if (e.isCtrlKey() && e.isAltKey() && e.isMetaKey()) {
			return ECtrl.CTRL_ALT_META;
		} else if (e.isCtrlKey() && e.isAltKey() && e.isShiftKey()) {
			return ECtrl.CTRL_ALT_SHIFT;
		} else if (e.isAltKey() && e.isShiftKey() && e.isMetaKey()) {
			return ECtrl.ALT_SHIFT_META;
		}
		// 2 CTRL
		else if (e.isCtrlKey() && e.isAltKey()) {
			return ECtrl.CTRL_ALT;
		} else if (e.isCtrlKey() && e.isShiftKey()) {
			return ECtrl.CTRL_SHIFT;
		} else if (e.isCtrlKey() && e.isMetaKey()) {
			return ECtrl.CTRL_META;
		}
		// 2 OTHER
		else if (e.isAltKey() && e.isShiftKey()) {
			return ECtrl.SHIFT_ALT;
		} else if (e.isAltKey() && e.isMetaKey()) {
			return ECtrl.ALT_META;
		} else if (e.isShiftKey() && e.isMetaKey()) {
			return ECtrl.SHIFT_META;
		}
		//1
		else if (e.isCtrlKey()) {
			return ECtrl.CTRL;
		} else if (e.isAltKey()) {
			return ECtrl.ALT;
		} else if (e.isShiftKey()) {
			return ECtrl.SHIFT;
		} else if (e.isMetaKey()) {
			return ECtrl.META;
		}
		return ARG.toDefThrow(() -> new WhatIsTypeException(e + ""), defRq);
	}

	@Deprecated
	public static ECtrl ofAsCtrl(Event e) {
		return IPressed.of(e).ectrl();
	}


	public static int of(ECtrl ctrl) {
		switch (ctrl) {
			case CTRL_ALT_SHIFT:
				return ZKE_3_CTRL_SHIFT_ALT_CODE;
			case CTRL_ALT:
				return ZKE_2_CTRL_ALT_CODE;
			case CTRL_SHIFT:
				return ZKE_2_CTRL_SHIFT_CODE;
			case SHIFT_ALT:
				return ZKE_2_SHIFT_ALT_CODE;
			case CTRL:
				return ZKE_CTRL_CODE;
			case ALT:
				return ZKE_ALT_CODE;
			case SHIFT:
				return ZKE_SHIFT_CODE;
			default:
				throw new WhatIsTypeException(ctrl);
		}
	}

}

