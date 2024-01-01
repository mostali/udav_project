package mpe.cron;

import mpc.arr.ArrItem;
import mpc.str.SPLIT;
import mpc.str.STR;
import mpc.str.UST;
import mpc.ERR;
import mpc.num.UInt;
import mpe.str.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UCron {

	public static final String CRON_PATTERN = "%s %s %s %s %s ?";//ss mm hh dd MM dayWeek
	public static final String ALWAYS = "0";
	public static final String EVERY = "*";
	public static final String EVERY_N = "*/";

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
		ERR.isBetweenEQ(num, 0, max);
		switch (num) {
			case 0:
				return EVERY;
			default:
				return EVERY_N + num;
		}
	}

	public static List<Integer> getPeriodIntsWithCheck(String exp, String pfx, boolean isPfxRq, int[] minMax) {
		List<Integer> ints = getPeriodIntsWithCheck(exp, pfx, isPfxRq);
		ERR.isGE(ints.get(0), minMax[0]);
		if (ints.size() > 1) {
			ERR.isLE(ArrItem.last(ints), minMax[1]);
		}
		return ints;
	}

	public static List<Integer> getPeriodIntsWithCheck(String exp, String pfx, boolean isPfxRq) {
		exp = STR.removeStartPfxWithCheckRq(exp, pfx, isPfxRq);
		return getPeriodInts(exp);
	}

	public static List<Integer> getPeriodInts(String exp) {
		List<Integer> ints = new ArrayList<>();
		if (exp.contains(",")) {
			String[] all = exp.split(",");
			for (String p : all) {
				ints.addAll(getPeriodInts(p));
			}
		} else if (exp.contains("-")) {
			Integer[] integers = SPLIT.byRxAs(exp, "-",Integer.class);
			ERR.isLength(integers, 2, "Period must contains only one delimter '-'", exp);
			ERR.isLE(integers[0], integers[1]);
			ints.addAll(UInt.range(integers[0], integers[1]));
		} else {
			Integer p = UST.INT(exp, null);
			ERR.NN(p, "Expression without delimiter must contain only one string with 'int'", exp);
			ints.add(p);
		}
		return ints;
	}

	public static String buildSimpleCronExpression(ISimpleDaylyJob iSimpleDaylyJob) {
		return buildSimpleCronExpression(iSimpleDaylyJob.getDaylyPeriod(), iSimpleDaylyJob.getExpHours(), iSimpleDaylyJob.getExpMinutes());
	}

	public static String buildSimpleCronExpression(int every_n_day, String allowed_hours, String allowed_minutes) {
		ERR.isBetweenEQ(every_n_day, 0, 365);
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
			return ToString.join(st, "-", ArrItem.last(ints));
		}
		return ints.stream().map(ToString::strOrNpe).collect(Collectors.joining(","));
	}
}
