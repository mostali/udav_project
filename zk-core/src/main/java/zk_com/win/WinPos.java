package zk_com.win;

import org.zkoss.zul.Window;

public enum WinPos {
	left, top, right, bottom, center, TR, TC, TL, BR, BC, BL;

	public String getPattern() {
		WinPos[] poss;
		switch (this) {
			case TL:
				poss = new WinPos[]{WinPos.top, WinPos.left};
				break;
			case TC:
				poss = new WinPos[]{WinPos.top, WinPos.center};
				break;
			case TR:
				poss = new WinPos[]{WinPos.top, WinPos.right};
				break;
			case BL:
				poss = new WinPos[]{WinPos.bottom, WinPos.left};
				break;
			case BC:
				poss = new WinPos[]{WinPos.bottom, WinPos.center};
				break;
			case BR:
				poss = new WinPos[]{WinPos.bottom, WinPos.right};
				break;
			default:
				poss = new WinPos[]{this};
				break;

		}
		return poss.length == 1 ? poss[0].name() : poss[0] + "," + poss[1];
	}

	public void apply(Window com) {
		com.setPosition(getPattern());
	}
}
