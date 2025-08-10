package mpe.cron;

import mpu.X;
import mpu.core.ARRi;
import mpu.str.*;
import mpu.IT;
import mpc.num.UInt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//https://www.freeformatter.com/cron-expression-generator-quartz.html
public class UCron {

	public static final String CRON_PATTERN = "%s %s %s %s %s ?";//ss mm hh dd MM dayWeek
	public static final String ALWAYS = "0";
	public static final String EVERY = "*";
	public static final String EVERY_N = "*/";

	public static boolean isEveryPattern(String pattern) {
		return EVERY.equals(pattern);
	}

	public interface ISimpleDaylyJob {
		String getJobType();

		int getDaylyPeriod();

		default String getExpHours() {
			return "0";
		}

		default String getExpMinutes() {
			return "0";
		}
	}

	public static String build_every_n(int num, int max) {
		IT.isBetweenEQ(num, 0, max);
		switch (num) {
			case 0:
				return EVERY;
			default:
				return EVERY_N + num;
		}
	}

	public static List<Integer> getPeriodIntsWithCheck(String exp, String pfx, int[] minMax) {
		List<Integer> ints = getPeriodIntsWithCheck(exp, pfx);
		IT.isGE(ints.get(0), minMax[0]);
		if (ints.size() > 1) {
			IT.isLE(ARRi.last(ints), minMax[1]);
		}
		return ints;
	}

	public static List<Integer> getPeriodIntsWithCheck(String exp, String pfx) {
		return getPeriodInts(STR.removeStartsWith(exp, pfx));
	}

	public static List<Integer> getPeriodInts(String exp) {
		List<Integer> ints = new ArrayList<>();
		if (exp.contains(",")) {
			String[] all = exp.split(",");
			for (String p : all) {
				ints.addAll(getPeriodInts(p));
			}
		} else if (exp.contains("-")) {
			Integer[] integers = SPLIT.argsByRxAsRq(exp, "-", Integer.class);
			IT.isLength(integers, 2, "Period must contains only one delimter '-'", exp);
			IT.isLE(integers[0], integers[1]);
			ints.addAll(UInt.range(integers[0], integers[1]));
		} else {
			Integer p = UST.INT(exp, null);
			IT.NN(p, "Expression without delimiter must contain only one string with 'int'", exp);
			ints.add(p);
		}
		return ints;
	}

	public static String buildSimpleCronExpression(ISimpleDaylyJob iSimpleDaylyJob) {
		return buildSimpleCronExpression(iSimpleDaylyJob.getDaylyPeriod(), iSimpleDaylyJob.getExpHours(), iSimpleDaylyJob.getExpMinutes());
	}

	public static String buildSimpleCronExpression(int every_n_day, String allowed_hours, String allowed_minutes) {
		IT.isBetweenEQ(every_n_day, 0, 365);
		UCron.getPeriodInts(allowed_hours);
		UCron.getPeriodInts(allowed_minutes);
		String cronExp = String.format(UCron.CRON_PATTERN, UCron.ALWAYS, allowed_minutes, allowed_hours, UCron.build_every_n(every_n_day, 366), "*");
		return cronExp;
	}

	public static String toShortPeriodPartExp(List<Integer> ints) {
		Integer st = ints.get(0);
		if (ints.size() == 1) {
			return st.toString();
		}
		if (UInt.isSeqWithStep(ints, 1)) {
			return JOIN.args(st, "-", ARRi.last(ints));
		}
		return ints.stream().map(X::toStringRq).collect(Collectors.joining(","));
	}
}
