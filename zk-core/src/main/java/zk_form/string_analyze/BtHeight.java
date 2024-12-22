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
public enum BtHeight {
	ONE(1), TWO(2), THREE(3), FOUR(4);
	public final int rows;
	public static final int STEP_VERT_PX = 100;

	public static int getHeight(int lines) {
		return BtHeight.getType(lines).rows * STEP_VERT_PX;
	}

	public static BtHeight getType(int lineCount) {
		if (lineCount < 7) {
			return ONE;
		} else if (lineCount < 16) {
			return TWO;
		} else if (lineCount < 36) {
			return THREE;
		}
		return FOUR;
	}

}
