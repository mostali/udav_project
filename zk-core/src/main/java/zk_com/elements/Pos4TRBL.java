package zk_com.elements;

import zk_form.events.ITouchEvent;

public enum Pos4TRBL {
	TC, RC, BC, LC;

	public static Pos4TRBL getAbsPos(ITouchEvent.Info info) {
		int y = info.tb;
		int ay = Math.abs(y);
		int x = info.lr;
		int ax = Math.abs(x);
		if (x == y && y == 0) {
			return null;
		} else if (ay > ax) {
			return y > 0 ? TC : BC;
		} else {
			return x > 0 ? LC : RC;
		}
	}

	public static Pos4TRBL getPosLR(ITouchEvent.Info info) {
		return info.lr == 0 ? null : (info.lr > 0 ? LC : RC);
	}

	public static Pos4TRBL getPosTB(ITouchEvent.Info info) {
		return info.tb == 0 ? null : (info.tb > 0 ? TC : BC);
	}

	public static boolean isV(Pos4TRBL absPos) {
		return absPos == TC || absPos == BC;
	}

	public static boolean isH(Pos4TRBL pos) {
		return pos == LC || pos == RC;
	}

	public static Boolean isPosTB_or_LR_or_NULL(ITouchEvent.Info info) {
		int ay = Math.abs(info.tb);
		int ax = Math.abs(info.lr);
		if (ay == ax && ax == 0) {
			return null;
		}
		return ay >= ax;
	}
}
