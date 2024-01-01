package lifebeat;

import mpc.args.ARG;
import mpc.core.U;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class LifePrinter {
	public static Logger L = LoggerFactory.getLogger(LifePrinter.class);

	public static TimerTask LIFE_PRINTER = null;

	public static void RUN(int period, final Logger... logger) {
		if (LIFE_PRINTER != null) {
			U.RUN_TIMER(0, period, LIFE_PRINTER = new TimerTask() {
				@Override
				public void run() {
					printTo(ARG.toDefOr(L, logger));
				}
			});
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
