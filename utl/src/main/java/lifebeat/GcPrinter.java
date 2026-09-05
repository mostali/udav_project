package lifebeat;

import lifebe.PulsePrinter;
import mpc.exception.WhatIsTypeException;
import mpc.log.L;
import mpu.Sys;
import mpu.core.ARR;
import mpu.str.STR;
import mpu.core.ARG;
import mpu.str.Hu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryManagerMXBean;
import java.util.TimerTask;

public class GcPrinter extends LifePrinter {

	public static void main(String[] args) throws InterruptedException {
		RUN(60_000, log);
		Thread.sleep(200_000);
	}

	private static final Logger log = LoggerFactory.getLogger(PulsePrinter.class);

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
				long[] count_i_time = getCountAndTime(factor, null);
				if (count_i_time == null) {
					L.warn("Error define YoGc type");
				} else {
					sb.append(yo.name()).append(":" + count_i_time[0]).append("(" + Hu.MS(count_i_time[1]) + ");");
				}
			}
			return sb;
		}

		public static long[] getCountAndTime(GarbageCollectorMXBean bean, long[]... defRq) {
			YoGc yo = YoGc.of(bean, null);
			if (yo == null) {
				if (ARG.isDef(defRq)) {
					return ARG.toDef(defRq);
				}
				throw new WhatIsTypeException("What is BeanGC? " + bean);
			}
			long[] ctr = new long[]{bean.getCollectionCount(), bean.getCollectionTime()};
			return ctr;
		}

		public static YoGc of(GarbageCollectorMXBean bean, YoGc... defRq) {
			String name = bean.getName().toLowerCase();
			if (name.contains("young")) {
				return YGC;
			}
			if (name.contains("old")) {
				return OGC;
			}
			for (String poolName : bean.getMemoryPoolNames()) {
				if (poolName.contains("Eden") || poolName.contains("Survivor") || poolName.contains("Young")) {
					return YGC;
				}
				if (poolName.contains("Old") || poolName.contains("Tenured")) {
					return OGC;
				}
			}
			// 2. Fallback: проверяем имя самого сборщика


//			String[] memoryPoolNames = bean.getMemoryPoolNames();
//			for (String poolName : memoryPoolNames) {
//				if (poolName.contains("Eden") || poolName.contains("Survivor")) {
//					return YGC;
//				} else if (poolName.contains("Old") || poolName.contains("Tenured")) {
//					return OGC;
//				}
//			}
//			if (ARG.isDef(defRq)) {
//				return ARG.toDef(defRq);
//			}
//			throw new WhatIsTypeException("What is BeanGC? " + bean);
//
//			if (bean.getName().toLowerCase().contains("young")) {
//				return YGC;
//			} else if (bean.getName().toLowerCase().contains("old")) {
//				return OGC;
//			}
//			String lowerName = bean.getName().toLowerCase();
//			// Young GC collectors
//			if (lowerName.contains("young") || lowerName.contains("ps scavenge")
//					|| lowerName.contains("g1 young") || lowerName.contains("copy")) {
//				return YGC;
//			}
//			// Old GC collectors
//			if (lowerName.contains("old") || lowerName.contains("ms mark sweep")
//					|| lowerName.contains("g1 old") || lowerName.contains("mark sweep")) {
//				return OGC;
//			}
			if (ARG.isDef(defRq)) {
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
