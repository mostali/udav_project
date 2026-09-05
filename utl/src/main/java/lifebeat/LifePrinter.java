package lifebeat;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import mpu.Sys;
import mpu.X;
import mpu.core.ARG;
import mpu.str.Hu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//// При превышении порогов — сразу в ERROR, а не в INFO
//if (usedPercent > 95 || loadAverage > procs * 0.8) {
//    logger.error(rpt.toString());  // вместо logger.info
//}
public class LifePrinter {

	public static Logger L = LoggerFactory.getLogger(LifePrinter.class);

	public static void main(String[] args) throws InterruptedException {
		RUN(60_000);
		if (true) {
			Thread.sleep(200000);
			return;
		}
		Stopwatch started = Stopwatch.createStarted();
		for (int i = 1; i < 1_000_000; i++) {
			printTo(L);
		}
		X.exit("Test end at " + Hu.MS(started));

		LifePrinter.RUN(ON_BY_DEAFULT_DELAY_MS);
	}

	public static final Integer ON_BY_DEAFULT_DELAY_MS = 60_000;

	public static PowerState CURRENT_POWER_STATE = PowerState.NORM;

	@RequiredArgsConstructor
	enum PowerState {
		LIGHT(20, (int) (ON_BY_DEAFULT_DELAY_MS * 2.2)), NORM(40, (int) (ON_BY_DEAFULT_DELAY_MS * 1.8)), MIDDLE(65, ON_BY_DEAFULT_DELAY_MS), BAD(90, 10_000), CRYTICAL(96, 10_000);
		final int maxUsedPct;
		final int sleepMs;

		public static PowerState of(int lastUsedPct) {
			for (PowerState powerState : values()) {
				if (lastUsedPct < powerState.maxUsedPct) {
					return powerState;
				}
			}
			return PowerState.CRYTICAL;
		}
	}

	public static final String APK_PULSE_MIN_DELAY_MS = "app.pulse.min.delay";

	public static TimerTask LIFE_PRINTER = null;

	public static void RUN_BY_AUTO() {
		switch (PowerState.of(MemPrinter.LAST_USED_PCT)) {

		}
	}

	public static void RUN_BY_APK(Integer delay) {
		if (delay == null && LifePrinter.ON_BY_DEAFULT_DELAY_MS != null && LifePrinter.ON_BY_DEAFULT_DELAY_MS > 0) {
			LifePrinter.RUN(LifePrinter.ON_BY_DEAFULT_DELAY_MS);
		} else if (delay != null && delay > 0) {
			LifePrinter.RUN(delay);
		} else {
			LifePrinter.L.info("LifePrinter is NOT inited. Property '{}' not found or has negative value'{}'", LifePrinter.APK_PULSE_MIN_DELAY_MS, delay);
		}
	}

	public static void RUN(int periodMs, final Logger... logger) {
		if (timer == null) {
			L.info("LifePrinter is inited by delay, every ms '{}'", periodMs);
			Logger loggerUse = logger != null && logger.length > 0 ? logger[0] : L;
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
			L.error("LifePrinter ALREADY RUNNED by delay '{}'", periodMs);
		}
	}

	private static Timer timer;//create link

	private static TimerTask RUN_TIMER(int beforeDelayMs, int periodMs, TimerTask runnable) {
		timer = new Timer("LifePrinter-Daemon", true);
		timer.schedule(runnable, beforeDelayMs, periodMs);
		return runnable;
	}

	public static void printTo(Logger logger) {
		if (logger.isInfoEnabled()) {
			StringBuilder rpt = buildReport();
			logger.info(rpt.toString());
		}
	}

	private static final String GEAR = "⚙";

	public static StringBuilder buildReport() {
		StringBuilder reportLine = new StringBuilder(GEAR + GEAR + GEAR + " ");
		StringBuilder reportMem = MemPrinter.buildReport(0);
		StringBuilder reportGc = GcPrinter.buildReport(0, null);
		StringBuilder reportThreads = SysAvgPrinter.buildReport();
		reportLine.append(reportMem).append(" / ").append(reportGc).append(" / ").append(reportThreads);
		return reportLine;
	}

}
