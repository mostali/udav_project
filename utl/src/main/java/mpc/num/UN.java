package mpc.num;

import com.google.common.base.Stopwatch;
import mpc.exception.WhatIsTypeException;
import mpc.args.ARG;
import mpc.ERR;
import mpc.X;
import mpc.str.ObjTo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class UN {

	public static String KB(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[]{"", "kb", "mb", "gb", "tb"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + "" + units[digitGroups];
	}

	public static Integer PCT(Number num1, Number num2) {
		return double2procInt(num1.doubleValue() / num2.doubleValue());
	}

	public static Integer PCT(Double value) {
		return double2procInt(value);
	}

	public static Integer double2procInt(Double value) {
		value = value * 100;
		return value.intValue();
	}

	public static String TIME(Stopwatch timer) {
		return MS(timer.elapsed(TimeUnit.MILLISECONDS));
	}

	public static String MS(Stopwatch timer) {
		return TIME(timer);
	}

	public static String MS(long ms) {
		if (ms < 1000) {
			return ms + "ms";
		} else if (ms < 60 * 1000) {
			return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 1) + "s";
		} else if (ms < 3600 * 1000) {
			return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.MINUTES, 1) + "m";
		} else {
			return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.HOURS, 1) + " h";
		}
	}

	public static BigDecimal toTime(long duration, TimeUnit timeUnitData, TimeUnit timeUnitTarget, Integer... scale) {
		Double val = null;
		switch (timeUnitTarget) {
			case DAYS:
				val = (double) timeUnitData.toHours(duration) / 24;
				break;
			case HOURS:
				val = (double) timeUnitData.toMinutes(duration) / 60;
				break;
			case MINUTES:
				val = (double) timeUnitData.toSeconds(duration) / 60;
				break;
			case SECONDS:
				val = (double) timeUnitData.toMillis(duration) / (1000);
				break;
			default:
				throw new WhatIsTypeException(timeUnitData);

		}
		BigDecimal bd = new BigDecimal(val);
		return ARG.isDefNNF(scale) ? bd.setScale(ARG.toDef(scale), RoundingMode.HALF_DOWN) : bd;
	}

	public static void add(int[] part, int[]... parts) {
		if (parts.length == 0) {
			return;
		}
		for (int[] part0 : parts) {
			ERR.state(part.length == part0.length);
			for (int i = 0; i < part.length; i++) {
				part[i] += part0[i];
			}
		}
	}

	public static boolean hasMinMax(double x, Double[] mnx, boolean... requiredInMinMaxPreiod) {
		if (X.empty(mnx)) {
			return ARG.isDefEqTrue(requiredInMinMaxPreiod) ? false : true;
		}
		boolean rslt = x >= mnx[0] && (mnx.length == 1 || x <= mnx[1]);
		return rslt;
	}

	public static <N extends Number> N round10(N num) {
		return round(num, 20);
	}

	public static <N extends Number> N round20(N num) {
		return round(num, 10);
	}

	public static <N extends Number> N round(N num, int step) {
		if (num == null || num.intValue() == 0) {
			return num;
		}
		long l = Math.round(num.doubleValue() / step) * step;
		return (N) ObjTo.objTo(l, num.getClass());
	}

	public static int neg(int num) {
		return num < 0 ? num : (num == 0 ? num : -num);
	}

	public static long neg(long num) {
		return num < 0 ? num : (num == 0 ? num : -num);
	}

	public static double neg(double num) {
		return num < 0 ? num : (num == 0 ? num : -num);
	}

	public static int pos(int num) {
		return Math.abs(num);
	}

	public static long pos(long num) {
		return Math.abs(num);
	}

	public static long N2Z(Long val) {
		return val == null ? 0 : val;
	}

	public static int N2Z(Integer val) {
		return val == null ? 0 : val;
	}

	public static Integer defIfNull(Integer n, Integer def) {
		return n == null ? def : n;
	}

	public static Integer defIfNullNegZero(Integer n, Integer def) {
		return n == null || n <= 0 ? def : n;
	}

	public static boolean isPos(Integer integer) {
		return integer != null && integer > 0;
	}

	public static boolean isPosOrZero(Integer integer) {
		return integer != null && integer >= 0;
	}

	public static boolean isPos(Long integer) {
		return integer != null && integer > 0;
	}

	public static boolean isPosOrZero(Long integer) {
		return integer != null && integer >= 0;
	}

	public static int maxGE(int i, int max) {
		return i >= max ? max : i;
	}

	public static int maxGT(int i, int max) {
		return i > max ? max : i;
	}

	public static int minLE(int i, int min) {
		return i <= min ? min : i;
	}

	public static int minLT(int i, int min) {
		return i < min ? min : i;
	}

	public static boolean isSeries(int status, int... i) {
		int num = Integer.parseInt((status + "").charAt(0)+"");
		for (int i0 : i) {
			if (i0 == num) {
				return true;
			}
		}
		return false;
	}
}
