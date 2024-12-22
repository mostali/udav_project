package zk_form.string_analyze;

import lombok.RequiredArgsConstructor;

//		switch (big) {
//			case LITTLE:
//				width = 100;
//				break;
//			case DEFAULT:
//				width = 100;
//				break;
//			case STANDART:
//				width = 100;
//				break;
//			case MIDDLE:
//				width = 100;
//				break;
//			case MAX:
//				width = 100;
//				break;
//			default:
//				throw new WhatIsTypeException(big);
//		}
//BigType
@RequiredArgsConstructor
public enum BtWidth {
	ONE(1), TWO(2), THREE(3), STANDART(5), MIDDLE(8), MAX(12);
	public final int cols;
	//	public static final int STEP_HOR_PX = 30;
	public static final int STEP_HOR_COL_PX = 60;
	public static final int MIN_WIDTH = 100;
	public static final int MAX_WIDTH = 600;

	public static int getWidth(String cat) {
		BtWidth bigType = StringInfo.getBigType(cat);
		int width = bigType.cols * STEP_HOR_COL_PX;
		if (width <= MIN_WIDTH) {
			width = MIN_WIDTH;
		} else if (width >= MAX_WIDTH) {
			width = MAX_WIDTH;
		}
		return width;
	}

	public double getThink() {
		return cols * BtWidth.STEP_HOR_COL_PX / 12;
	}
}
