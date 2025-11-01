package zk_page.events;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.MouseEvent;

public enum ECtrl {
	DEFAULT,
	CTRL_ALT_SHIFT,//
	CTRL_ALT, CTRL_SHIFT, SHIFT_ALT, //
	CTRL, ALT, SHIFT;

	public static final int ZKE_1_INSERT_CODE = 256;
	public static final int ZKE_ALT_CODE = 257;
	public static final int ZKE_CTRL_CODE = 258; //MouseEvent.CTRL_KEY
	public static final int ZKE_SHIFT_CODE = 260;
	public static final int ZKE_2_CTRL_SHIFT_CODE = 262;
	public static final int ZKE_2_CTRL_ALT_CODE = 259;
	public static final int ZKE_2_SHIFT_ALT_CODE = 261;
	public static final int ZKE_3_CTRL_SHIFT_ALT_CODE = 263;

	public static ECtrl of(Event e) {
		if (!(e instanceof MouseEvent)) {
			return DEFAULT;
		}
		int keys = ((MouseEvent) e).getKeys();
		switch (keys) {
			case ZKE_3_CTRL_SHIFT_ALT_CODE:
				return CTRL_ALT_SHIFT;

			case ZKE_2_CTRL_ALT_CODE:
				return CTRL_ALT;
			case ZKE_2_CTRL_SHIFT_CODE:
				return CTRL_SHIFT;
			case ZKE_2_SHIFT_ALT_CODE:
				return SHIFT_ALT;

			case ZKE_CTRL_CODE:
				return CTRL;
			case ZKE_ALT_CODE:
				return ALT;
			case ZKE_SHIFT_CODE:
				return SHIFT;

			default:
				return DEFAULT;
		}

	}

}
