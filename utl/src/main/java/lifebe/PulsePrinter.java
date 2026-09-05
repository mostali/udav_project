package lifebe;

import lifebeat.mod.OomMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PulsePrinter {

	public static void main(String[] args) throws InterruptedException {
		RUN(5_000, log);
//		OomMod.RUN(10, 1000, 1000, 1000);
		Thread.sleep(200_000);
	}

	private static final Logger log = LoggerFactory.getLogger(PulsePrinter.class);

	private static final Integer ON_BY_DEAFULT_DELAY_MS = 60_000;

	private static TimerTask LIFE_PRINTER = null;

	public static void RUN_BY_DELAY(Integer delay) {
		if (delay == null && ON_BY_DEAFULT_DELAY_MS != null && ON_BY_DEAFULT_DELAY_MS > 0) {
			RUN(ON_BY_DEAFULT_DELAY_MS);
		} else if (delay != null && delay > 0) {
			RUN(delay);
		} else {
			log.info("LifePrinter is NOT inited, delay = {}", delay);
		}
	}

	private static void RUN(int periodMs, final Logger... logger) {
		if (timer == null) {
			log.info("LifePrinter is inited by delay, every ms '{}'", periodMs);
			Logger loggerUse = logger != null && logger.length > 0 ? logger[0] : log;
			RUN_TIMER(0, periodMs,  new TimerTask() {
				@Override
				public void run() {
					try {
						printTo(loggerUse);
					} catch (Exception e) {
						loggerUse.error(String.format("LifePrinter task failed: %s", e.getMessage()), e);
					}
				}
			});
		} else {
			log.error("LifePrinter ALREADY RUNNED by delay '{}'", periodMs);
		}
	}

	private static Timer timer;//create link

	private static TimerTask RUN_TIMER(int beforeDelayMs, int periodMs, TimerTask runnable) {
		timer = new Timer("LifePrinter-Daemon", true);
		timer.schedule(runnable, beforeDelayMs, periodMs);
		return runnable;
	}

	private static void printTo(Logger logger) {
		if (logger.isInfoEnabled()) {
			StringBuilder rpt = buildReport();
			logger.info(rpt.toString());
		}
	}

	private static final String GEAR = "⚙";

	private static StringBuilder buildReport() {
		StringBuilder reportLine = new StringBuilder(GEAR + GEAR + GEAR + " ");
		StringBuilder reportMem = MemPrinter.buildReport(0);
		StringBuilder reportGc = GcPrinter.buildReport(0, null);
		StringBuilder reportThreads = SysAvgPrinter.buildReport();
		reportLine.append(reportMem).append(" / ").append(reportGc).append(" / ").append(reportThreads);
		return reportLine;
	}

	private static class GcPrinter {

		private static StringBuilder buildReport(int tabLevel, StringBuilder... defRq) {
			return YoGc.buildReport(tabLevel, defRq);
		}

		public enum YoGc {
			YGC, OGC;

			public int index() {
				return index(this).intValue();
			}

			private static StringBuilder buildReport(int tabLevel, StringBuilder... defRq) {
				String TAB = Utils.TAB(tabLevel);
				StringBuilder sb = new StringBuilder();
				sb.append(TAB);
				for (GarbageCollectorMXBean factor : ManagementFactory.getGarbageCollectorMXBeans()) {
					YoGc yo = YoGc.of(factor, null);
					if (yo == null) {
						if (Utils.isDef(defRq)) {
							return Utils.toDef(defRq);
						}
						throw new IllegalStateException("What is BeanGC? " + factor);
					}
					long[] count_i_time = getCountAndTime(factor, null);
					if (count_i_time == null) {
						log.error("Error define YoGc type");
					} else {
						String ms = Utils.MS(count_i_time[1]);
						sb.append(yo.name()).append(":" + count_i_time[0]).append("(" + ms + ");");
					}
				}
				return sb;
			}

			private static long[] getCountAndTime(GarbageCollectorMXBean bean, long[]... defRq) {
				YoGc yo = YoGc.of(bean, null);
				if (yo == null) {
					if (Utils.isDef(defRq)) {
						return Utils.toDef(defRq);
					}
					throw new IllegalStateException("What is BeanGC? " + bean);
				}
				long[] ctr = new long[]{bean.getCollectionCount(), bean.getCollectionTime()};
				return ctr;
			}

			private static YoGc of(GarbageCollectorMXBean bean, YoGc... defRq) {
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
				if (Utils.isDef(defRq)) {
					return Utils.toDef(defRq);
				}
				throw new IllegalStateException("What is BeanGC? " + bean);
			}

			private static Long index(YoGc of, Long... defRq) {
				switch (of) {
					case YGC:
						return 0L;
					case OGC:
						return 1L;
					default:
						if (Utils.isDef(defRq)) {
							return Utils.toDef(defRq);
						}
						throw new IllegalStateException("What is GC? " + of.name());
				}
			}
		}

	}

	private static class MemPrinter {

		private static StringBuilder buildReport(int tabLevel) {

			String TAB = PulsePrinter.Utils.TAB(tabLevel);
			String TAB_ = PulsePrinter.Utils.TAB(tabLevel + 1);
			String TAB2 = PulsePrinter.Utils.TAB(tabLevel + 2);

			Runtime rt = Runtime.getRuntime();

			long totalMemory = rt.totalMemory();
			long freeMemory = rt.freeMemory();
			long used = totalMemory - freeMemory;
			long maxMemory = rt.maxMemory();

			StringBuilder sb = new StringBuilder();

			sb.append(TAB);
			sb.append("Mem(").append(PulsePrinter.Utils.KB_TB(maxMemory)).append("), ");
			sb.append("Total(").append(PulsePrinter.Utils.KB_TB(totalMemory)).append("), ");
			sb.append("Free(").append(PulsePrinter.Utils.KB_TB(freeMemory)).append(", " + PulsePrinter.Utils.PCT(freeMemory, totalMemory)).append("%), ");
			sb.append("Used(").append(PulsePrinter.Utils.KB_TB(used)).append(", " + PulsePrinter.Utils.PCT(used, totalMemory)).append("%)");

			return sb;
		}

	}

	private static class SysAvgPrinter {

		private static StringBuilder buildReport() {
			final StringBuilder dump = new StringBuilder();
			OperatingSystemMXBean mxBean = ManagementFactory.getOperatingSystemMXBean();
			double loadAverage = mxBean.getSystemLoadAverage();
			dump.append("LoadAvg(").append(String.format("%.2f", loadAverage)).append("), ");
			int procs = mxBean.getAvailableProcessors();
			dump.append("Proc's(").append(procs).append("), ");
			final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
			int tc = threadMXBean.getThreadCount();
			dump.append("Thread's(").append(tc).append(")");
			return dump;
		}
	}

	private static class Utils {

		private static final String TAB = "    ";

		private static String KB_TB(long size) {
			if (size <= 0) {
				return "0";
			}
			final String[] units = new String[]{"", "kb", "mb", "gb", "tb"};
			int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
			return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + "" + units[digitGroups];
		}

		private static Integer double2procInt(Double value) {
			value = value * 100;
			return value.intValue();
		}

		private static Integer PCT(Number num1, Number num2) {
			return double2procInt(num1.doubleValue() / num2.doubleValue());
		}

		private static String repeat(String str, int count) {
			StringBuilder sb = new StringBuilder();
			do {
				sb.append(str);
			} while (--count > 0);
			return sb.toString();
		}

		private static String TAB(int tabLevel) {
			return tabLevel <= 0 ? "" : Utils.repeat(TAB, tabLevel);
		}

		private static void p(StringBuilder print) {
			System.out.println(print);
		}

		private static boolean isDef(Object... predicat) {
			return predicat == null || predicat.length > 0;
		}

		private static <T> T toDef(T... predicat) {
			return predicat == null ? null : predicat[0];
		}

		private static String MS(long ms) {
			return toStringTimeHumanlyMs(ms);
		}

		private static final long SEC_MS = 1000;
		private static final long MIN_SEC = 60;
		private static final long HOUR_SEC = 3600;
		private static final long DAY_SEC = HOUR_SEC * 24;

		private static String toStringTimeHumanlyMs(long ms) {
			if (ms < SEC_MS) {
				return ms + "ms";
			} else if (ms < MIN_SEC * 1000) {
				return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 1) + "s";
			} else if (ms < DAY_SEC * 1000) {
				return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.MINUTES, 1) + "m";
			} else {
				return toTime(ms, TimeUnit.MILLISECONDS, TimeUnit.HOURS, 1) + "h";
			}
		}

		private static BigDecimal toTime(long duration, TimeUnit timeUnitData, TimeUnit timeUnitTarget, Integer... scale) {
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
					throw new IllegalStateException("Unsupported type - " + timeUnitData);

			}
			BigDecimal bd = new BigDecimal(val);
			return isDefNNF(scale) ? bd.setScale(toDef(scale), RoundingMode.HALF_DOWN) : bd;
		}

		private static boolean isDefNotEmpty(Object... predicat) {
			return predicat != null && predicat.length > 0;
		}

		private static boolean isDefNNF(Object... predicat) {
			return isDefNotEmpty(predicat) && predicat[0] != null;
		}

	}
}
