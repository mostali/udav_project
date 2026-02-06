package mpc.num;

import mpu.str.RANDOM;
import mpu.IT;

import java.util.*;

public class UInt {

	public static boolean startsWith(int position, int prefix) {
		return String.valueOf(position).startsWith(String.valueOf(prefix));
	}

	public static boolean isPositive(Integer i) {
		return i != null && i > 0;
	}

	public static int rand(int min, int max) {
		return new Random().nextInt(max + 1 - min) + min;
	}

	public static Integer randInteger(int length) {
		return RANDOM.integer(length);
	}

	public static int negative(int i) {
		return i > 0 ? i * -1 : i;
	}

	public static List<Integer> range(int from, int to) {
		List<Integer> l = new ArrayList<>();
		for (int i = from; i <= to; i++) {
			l.add(i);
		}
		return l;
	}

	/**
	 * *************************************************************
	 * ---------------------------  AVG ----------------------------
	 * *************************************************************
	 */
	public static double average(List<Integer> vs) {
		return vs.stream().parallel().filter(e -> e != null)
				.reduce(new ImmutableAverager(), ImmutableAverager::accept, ImmutableAverager::combine).average();
	}

	public static boolean isSeqWithStep(List<Integer> ints, int step) {
		Integer prev = null;
		for (Integer i : ints) {
			if (prev != null) {
				if (prev + step != i) {
					return false;
				}
			}
			prev = i;
		}
		return true;
	}

	public static <N extends Number> N getMinMax(boolean maxOrMin, Collection<N> indexes) {
		IT.notEmpty(indexes);
		N val = null;
		for (N index : indexes) {
			if (val == null) {
				val = index;
				continue;
			}
			int compare = IT.NUMBER_COMPARATOR.compare(val, index);
			if (maxOrMin && compare < 0) {
				val = index;
			} else if (!maxOrMin && compare > 0) {
				val = index;
			}
		}
		return val;
	}

	public static class ImmutableAverager {
		private final int total;
		private final int count;

		public ImmutableAverager() {
			this.total = 0;
			this.count = 0;
		}

		public ImmutableAverager(int total, int count) {
			this.total = total;
			this.count = count;
		}

		public double average() {
			return count > 0 ? ((double) total) / count : 0;
		}

		public ImmutableAverager accept(int i) {
			return new ImmutableAverager(total + i, count + 1);
		}

		public ImmutableAverager combine(ImmutableAverager other) {
			return new ImmutableAverager(total + other.total, count + other.count);
		}
	}
}
