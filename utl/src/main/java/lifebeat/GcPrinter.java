package lifebeat;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARR;
import mpu.str.STR;
import mpu.core.ARG;
import mpu.str.Hu;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

public class GcPrinter extends LifePrinter {


//	public static TimerTask MEM_PRINTER = null;
//
//	public static void RUN(int period, final Logger... logger) {
//		U.RUN(0, period, MEM_PRINTER = new TimerTask() {
//			@Override
//			public void run() {
//				printTo(ARG.toPredicatOr(new LoggerToSystemOut(), logger));
//			}
//		});
//	}

//	public static void printTo(Logger logger) {
//		if (logger.isInfoEnabled()) {
//			StringBuilder report = YoGc.buildReport(0);
//			logger.info(report.toString());
//		}
//	}

	public static void main(String[] args) {
//		U.RUN(0, 1000, new TimerTask() {
//			@Override
//			public void run() {
//				P.p(print(0));
//			}
//		});

//		P.p(print(0));

//		U.p(YoGc.buildReport(0));

	}

	public static StringBuilder buildReport(int tabLevel, StringBuilder... defRq) {
		return YoGc.buildReport(tabLevel, defRq);
	}


	public enum YoGc {
		YGC, OGC;

		public int index() {
			return index(this).intValue();
		}

		private static StringBuilder buildReport(int tabLevel, StringBuilder... defRq) {
			String TAB = STR.TAB(tabLevel);
			StringBuilder sb = new StringBuilder();
			sb.append(TAB);
			for (GarbageCollectorMXBean factor : ManagementFactory.getGarbageCollectorMXBeans()) {
				YoGc yo = YoGc.of(factor, null);
				if (yo == null) {
					if (ARG.isDef(defRq)) {
						return ARG.toDef(defRq);
					}
					throw new WhatIsTypeException("What is BeanGC? " + factor);
				}
				long[] ctr = get(factor);
				sb.append(yo.name()).append(":" + ctr[0]).append("(" + Hu.MS(ctr[1]) + ");");
			}
			return sb;
		}

		public static long[][] get(long[][]... defRq) {
			long[][] gc = new long[2][2];
			for (GarbageCollectorMXBean factor : ManagementFactory.getGarbageCollectorMXBeans()) {
				YoGc yo = YoGc.of(factor, null);
				if (yo == null) {
					if (ARG.isDef(defRq)) {
						return ARG.toDef(defRq);
					}
					throw new WhatIsTypeException("What is BeanGC? " + factor);
				}
				gc[yo.index()] = get(factor);
			}
			return gc;
		}

		public static long[] get(GarbageCollectorMXBean bean, long[]... defRq) {
			YoGc yo = YoGc.of(bean, null);
			if (yo == null) {
				if (ARG.isDef(defRq)) {
					return ARG.toDef(defRq);
				}
				throw new WhatIsTypeException("What is BeanGC? " + bean);
			}
			long[] ctr = new long[]{bean.getCollectionTime(), bean.getCollectionCount()};
			return ctr;
		}

		public static YoGc of(GarbageCollectorMXBean bean, YoGc... defRq) {
			if (ARR.contains(bean.getName(), "Young")) {
				return YGC;
			} else if (ARR.contains(bean.getName(), "Old")) {
				return OGC;
			} else if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new WhatIsTypeException("What is BeanGC? " + bean);
		}

		public static String getShortName(GarbageCollectorMXBean bean, String... defRq) {
			YoGc gc = YoGc.of(bean, null);
			if (gc != null) {
				return gc.name();
			}
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new WhatIsTypeException("What is BeanGC? " + bean);
		}

		public static Long index(GarbageCollectorMXBean bean, Long... defRq) {
			YoGc of = of(bean, null);
			if (of != null) {
				return index(of, defRq);
			}
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new WhatIsTypeException("What is BeanGC? " + bean);
		}

		public static Long index(YoGc of, Long... defRq) {
			switch (of) {
				case YGC:
					return 0L;
				case OGC:
					return 1L;
				default:
					if (ARG.isDef(defRq)) {
						return ARG.toDef(defRq);
					}
					throw new WhatIsTypeException("What is GC? " + of.name());
			}
		}
	}

}
