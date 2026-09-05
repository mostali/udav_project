package mpe.checks;

import mpc.exception.WrongLogicRuntimeException;

import java.util.List;

//Check Utility Extended
public class UCExt {

	public static boolean isMinMax(String[] checkablePattern, int[] minMax) {
		return checkMinMax(checkablePattern, minMax) == null;
	}

	public static boolean isMinMax(List checkablePattern, int[] minMax) {
		return checkMinMax(checkablePattern == null ? null : checkablePattern.toArray(), minMax) == null;
	}

	public static String checkMinMax(Object[] checkablePattern, int[] minMax) {
		if (checkablePattern == null) {
			return "CheckablePattern is NULL";
		} else if (checkablePattern.length == 0) {
			return "CheckablePattern is EMPTY";
		}
		if (minMax == null) {
			return "MinMax is NULL";
		} else if (minMax.length == 0 || minMax.length > 2) {
			return "MinMax length is " + minMax.length;
		}
		int min = minMax[0];
		if (min < 0) {
			return "MinMax[0] is negative";
		}
		switch (minMax.length) {
			case 1: {
				if (min > 0 && checkablePattern.length < min) {
					return "CheckablePattern length must be MORE vs " + checkablePattern.length + "<" + min;
				}
			}
			break;
			case 2: {
				int max = minMax[1];
				if (max != -1 && min > max) {
					return "MinMax[0] > MinMax[1] vs " + min + ">" + max;
				}
				if (min == max) {
					if (checkablePattern.length != min) {
						return "CheckablePattern length must be " + min;
					}
				} else if (checkablePattern.length < min) {
					if (checkablePattern.length < min) {
						return "CheckablePattern length must be MORE vs " + checkablePattern.length + "<" + min;
					}
				} else if (checkablePattern.length > max) {
					if (max != -1 && checkablePattern.length > max) {
						return "CheckablePattern length must be LESS vs " + checkablePattern.length + ">" + max;
					}
				}
			}
			break;
			default:
				throw new WrongLogicRuntimeException("need check eq 1|2");
		}
		return null;
	}
}
