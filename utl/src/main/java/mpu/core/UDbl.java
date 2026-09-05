package mpu.core;

import mpu.core.ARG;

import java.math.BigDecimal;
import java.math.RoundingMode;

//Утилиты для Double
public class UDbl {

	public static Integer double2procInt(Double value) {
		value = value * 100;
		return value.intValue();
	}

	public static String double2procStr(Double value) {
		return double2procInt(value) + "%";
	}

	public static Double scale2(Double value, RoundingMode... roundMode) {
		return scale(value, 2, roundMode);
	}

	public static Double pct2dbl1(long pct, RoundingMode... roundMode) {
		return pct2dbl(pct, 1, roundMode);
	}

	public static Double pct2dbl(long pct, int scale, RoundingMode... roundMode) {
		return scale(pct / ((double) 100), scale, roundMode);
	}

	public static Double scale(Double value, int scale, RoundingMode... roundMode) {
		if (scale < 0) {
			return value;
		}
		BigDecimal d = new BigDecimal(value);
		d = d.setScale(scale, ARG.toDefOr(RoundingMode.HALF_UP, roundMode));
		return d.doubleValue();
	}

	public static Long ifNegative(Long l, Long def) {
		return l < 0 ? def : l;
	}

	public static Integer ifNegative(Integer l, Integer def) {
		return l < 0 ? def : l;
	}

}
