package lifebeat;

import mpu.Sys;
import mpc.exception.RequiredRuntimeException;
import mpu.core.ARR;
import mpu.str.STR;
import mpu.core.QDate;
import mpu.core.UTime;
import mpu.core.ARG;
import mpu.str.Hu;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.TimerTask;

import static mpu.str.Hu.MB1;
import static mpu.str.Hu.MS;
import static mpe.core.U.L;
import static mpu.core.UDbl.pct2dbl1;
import static mpu.core.UDbl.scale2;

public class GcExtPrinter {

//	public static final Queue cache = UL.cache_queue_FILO(3);

	public static FactorGc factorYGc = null;
	public static FactorGc factorOGc = null;

	public static long[] startFactor(long typeGc) {
//		typ_ctr_ms_time_mem
		return new long[]{typeGc, 0L, 0L, System.currentTimeMillis(), Runtime.getRuntime().freeMemory()};

	}

	public static void main(String[] args) {
		Sys.RUN_TIMER(0, 1000, new TimerTask() {
			@Override
			public void run() {
				Sys.p(print(0));
			}
		});

//		P.p(print(0));

	}

	private static void test() {
		QDate now = QDate.now();
		long[] gc1 = {0, 1, 1_000, now.ms(), 100_000_000};
		long[] gc2 = {0, 2, 3_000, now.addSeconds(60).ms(), 150_000_000};
		long[] gc3 = {0, 3, 4_500, now.addSeconds(100).ms(), 300_000_000};
		FactorGc fgc1 = new FactorGc(gc1);
		FactorGc fgc2 = fgc1.next(gc2);
		FactorGc fgc3 = fgc2.next(gc3);
		Sys.exit(fgc3 + "");
	}

	//	@RequiredArgsConstructor
	public static class FactorGc {

		public final long[] typ_ctr_ms_time_mem;//type,ctr,ms,time

		//
		private long singleGc_ms;
		private double countGcPerMin;
//		private long singleGc_mb;

		private int trend1gc;
		private int trend1sec;
		private int trendMem;

		public FactorGc(long[] typ_ctr_ms_time_mem) {
			this.typ_ctr_ms_time_mem = typ_ctr_ms_time_mem;
		}

		public FactorGc next(GarbageCollectorMXBean next) {
			return next(this, next);
		}

		public static FactorGc next(FactorGc prevFact, GarbageCollectorMXBean next) {
			return next(prevFact, new long[]{YoGc.index(next), next.getCollectionCount(), next.getCollectionTime(), System.currentTimeMillis(), Runtime.getRuntime().freeMemory()});
		}

		public FactorGc next(long[] typ_ctr_ms_time) {
			return next(this, typ_ctr_ms_time);
		}

		public static FactorGc next(FactorGc prevFact, long[] next__typ_ctr_ms_time) {
			long ctr = next__typ_ctr_ms_time[1];
			if (ctr == 0) {
				return null;
			}
			if (prevFact == null) {
				return FactorGc.next(new FactorGc(startFactor(next__typ_ctr_ms_time[0])), next__typ_ctr_ms_time);
			} else if (prevFact.typ_ctr_ms_time_mem[1] == ctr) {
				return prevFact;
			}
			if (prevFact.typ_ctr_ms_time_mem[2] == next__typ_ctr_ms_time[2]) {
				next__typ_ctr_ms_time[2]++;
			}
			if (prevFact.typ_ctr_ms_time_mem[3] == next__typ_ctr_ms_time[3]) {
				next__typ_ctr_ms_time[3]++;
			}

			FactorGc nextFact = new FactorGc(next__typ_ctr_ms_time);

			/**
			 * *************************************************************
			 * ----------------------------- 1 GC = ms --------------------------
			 * *************************************************************
			 */
			long dfCtr = next__typ_ctr_ms_time[1] - prevFact.typ_ctr_ms_time_mem[1];//20-10
			long dfMs = next__typ_ctr_ms_time[2] - prevFact.typ_ctr_ms_time_mem[2];//500 - 200
			long singleMs = dfMs / dfCtr;
			nextFact.singleGc_ms = singleMs;
			if (singleMs == 0) {
				Sys.p("wtf");
			}
			/**
			 * *************************************************************
			 * ----------------------------- 1 MINUTE = N GC --------------------------
			 * *************************************************************
			 */
			long prevDate = prevFact.typ_ctr_ms_time_mem[3];
			long nextDate = nextFact.typ_ctr_ms_time_mem[3];

			long absGcTime = nextDate - prevDate;
			double countGcPerMin = dfCtr / (absGcTime / (double) 1000 / 60);
			nextFact.countGcPerMin = countGcPerMin;

			if (Double.isInfinite(countGcPerMin)) {
				Sys.p("wtf");
			}

			/**
			 * *************************************************************
			 * ----------------------------- 1 GC = N Mb --------------------------
			 * *************************************************************
			 */
//			long dfMem = prevFact.typ_ctr_ms_time_mem[4] - next__typ_ctr_ms_time[4];
//			long singleBytes = dfMem / dfCtr;
//			nextFact.singleGc_mb = (long) ByteUnit.BYTE.toMB(singleBytes);

			/**
			 * *************************************************************
			 * ----------------------------- TREND's --------------------------
			 * *************************************************************
			 */
			nextFact.trend1gc = Hu.PCT(nextFact.singleGc_ms, prevFact.singleGc_ms);
//			if (nextFact.trend1gc > 500) {
//				P.p("wtf");
//			}
			nextFact.trend1sec = Hu.PCT(nextFact.countGcPerMin, prevFact.countGcPerMin);
			nextFact.trendMem = Hu.PCT(next__typ_ctr_ms_time[4], prevFact.typ_ctr_ms_time_mem[4]);

			return nextFact;
		}

		public static String toString(FactorGc factorGc) {
			return factorGc.nameGc() + ":" +
					"mem(" + MB1(factorGc.typ_ctr_ms_time_mem[4]) + ")" +
					", 1gc(" + MS(factorGc.singleGc_ms) + ")" +
//				   ", 1gc(" + factorGc.singleGc_mb + "Mb)" +
					", 1min(" + scale2(factorGc.countGcPerMin) + ")" +
//				   ", trend_1gc (" + factorGc.trend1gc + "%)" +
//				   ", trend_1min (" + factorGc.trend1sec + "%)" +
					", trend_1gc (" + pct2dbl1(factorGc.trend1gc) + ")" +
					", trend_1min (" + pct2dbl1(factorGc.trend1sec) + ")" +
					", trend_mem_free (" + pct2dbl1(factorGc.trendMem) + ")" +
					"";
		}

		private String nameGc() {
			return typ_ctr_ms_time_mem[0] == 0L ? "Y" : "O";
//			return "G" + typ_ctr_ms_time_mem[0];
		}

		@Override
		public String toString() {
			try {
				return toString(this);
			} catch (Exception ex) {
				if (L.isErrorEnabled()) {
					L.error("FactorGc to String. It normal if happens only", ex);
				}
				return "FactorGC:E";
			}
		}
	}

	public static void RUN(int period) {
		Sys.RUN_TIMER(0, period, new TimerTask() {
			@Override
			public void run() {
				Sys.p(print(0));
			}
		});
	}

	public static StringBuilder print(int tabLevel) {
		StringBuilder sb = buildReport(0);
		return sb;
	}

	public static StringBuilder buildReport(int tabLevel) {

		String TAB = STR.TAB(tabLevel);
		String TAB_ = STR.TAB(tabLevel + 1);
		String TAB2 = STR.TAB(tabLevel + 2);

		StringBuilder sb = null;

		for (GarbageCollectorMXBean factor : ManagementFactory.getGarbageCollectorMXBeans()) {
			YoGc yo = YoGc.of(factor);

			StringBuilder single = printBean(factor, 0);
			if (sb != null) {
				single.insert(0, ", ");
			} else {
				sb = new StringBuilder();
			}
			sb.append(single);

			switch (yo) {
				case YGC:
					factorYGc = FactorGc.next(factorYGc, factor);
					break;
				case OGC:
					factorOGc = FactorGc.next(factorOGc, factor);
					break;
			}

		}

		sb.append(", // ").append(factorYGc == null ? "Y:null" : factorYGc.toString()).append(" //");
		sb.append(", // ").append(factorOGc == null ? "O:null" : factorOGc.toString()).append(" //");

		return sb;
	}

	private static StringBuilder printBean(GarbageCollectorMXBean factor, int tabLevel) {
		String TAB = STR.TAB(tabLevel);
		String name = YoGc.getShortName(factor);
		StringBuilder sb = new StringBuilder();
		sb.append(TAB);
		sb.append(name).append(":" + factor.getCollectionCount()).append("(" + UTime.toStringTimeHumanlyMs(factor.getCollectionTime()) + ")");
		return sb;
	}


	public enum YoGc {
		YGC, OGC;

		public static YoGc of(GarbageCollectorMXBean bean, YoGc... defRq) {
			if (ARR.contains(bean.getName(), "Young")) {
				return YGC;
			} else if (ARR.contains(bean.getName(), "Old")) {
				return OGC;
			} else if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("What is GC? " + bean);
		}

		public static String getShortName(GarbageCollectorMXBean factor) {
			YoGc gc = YoGc.of(factor, null);
			if (gc == null) {
				return factor.getName();
			}
			return gc.name();
		}

		public static long index(GarbageCollectorMXBean next) {
			YoGc of = of(next);
			switch (of) {
				case YGC:
					return 0L;
				case OGC:
					return 1L;
			}
			throw new RequiredRuntimeException("What is GC? " + next.getName());
		}
	}

}
