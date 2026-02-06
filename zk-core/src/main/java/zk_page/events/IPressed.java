package zk_page.events;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.MouseEvent;

public interface IPressed {
	public static String toString(IPressed iPressed) {
		return iPressed.ectrl() + "_" + iPressed.ecode();
	}

	static IPressed of(Event e) {
		boolean isMouseEvent = e instanceof MouseEvent;
		if (!isMouseEvent) {
			if (e instanceof KeyEvent) {
				return ofKey((KeyEvent) e);
			}
			return ECtrl.NO_MOUSE;
		}
		int keys = ((MouseEvent) e).getKeys();
		switch (keys) {
			case ECtrl.ZKE_3_CTRL_SHIFT_ALT_CODE:
				return ECtrl.CTRL_ALT_SHIFT;

			case ECtrl.ZKE_2_CTRL_ALT_CODE:
				return ECtrl.CTRL_ALT;
			case ECtrl.ZKE_2_CTRL_SHIFT_CODE:
				return ECtrl.CTRL_SHIFT;
			case ECtrl.ZKE_2_SHIFT_ALT_CODE:
				return ECtrl.SHIFT_ALT;

			case ECtrl.ZKE_CTRL_CODE:
				return ECtrl.CTRL;
			case ECtrl.ZKE_ALT_CODE:
				return ECtrl.ALT;
			case ECtrl.ZKE_SHIFT_CODE:
				return ECtrl.SHIFT;

			default:
				return ECtrl.DEFAULT;
		}
	}

	static IPressed ofKey(KeyEvent e) {
		return new IPressed() {
			@Override
			public int ecode() {
				return e.getKeyCode();
			}

			@Override
			public ECtrl ectrl() {
				return ECtrl.ofKeyEvent(e,null);
			}
		};
	}

	default int ecode() {
		return -1;
	}

	default ECtrl ectrl() {
		return ECtrl.DEFAULT;
	}

	default String toStringLog() {
		return ectrl() + "_" + ecode();
	}

	;
}
