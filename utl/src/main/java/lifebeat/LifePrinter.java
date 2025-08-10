package lifebeat;

import lombok.RequiredArgsConstructor;
import mpu.Sys;
import mpu.core.ARG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class LifePrinter {

	public static Logger L = LoggerFactory.getLogger(LifePrinter.class);

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

	public static void RUN(int period, final Logger... logger) {
		if (LIFE_PRINTER == null) {
			L.info("LifePrinter is inited by delay, every '{}'", period);
			Sys.RUN_TIMER(0, period, LIFE_PRINTER = new TimerTask() {
				@Override
				public void run() {
					printTo(ARG.toDefOr(L, logger));
				}
			});
		} else {
			L.error("LifePrinter ALREADY RUNNED by delay '{}'" + period);
		}
	}

	public static void printTo(Logger logger) {
		if (logger.isInfoEnabled()) {
			StringBuilder rpt = buildReport();
			logger.info(rpt.toString());
		}
	}

	public static StringBuilder buildReport() {
		StringBuilder reportMem = MemPrinter.buildReport(0);
		StringBuilder reportGc = GcPrinter.buildReport(0, null);
		StringBuilder reportThreads = SysAvgPrinter.buildReport();
		reportMem.append(" / ").append(reportGc).append(" / ").append(reportThreads);
		return reportMem;
	}

	public static void main(String[] args) {
		LifePrinter.RUN(60_000);
	}
}
