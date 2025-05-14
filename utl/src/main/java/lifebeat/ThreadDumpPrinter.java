package lifebeat;

import mpu.core.ARG;
import mpe.core.P;
import mpc.exception.RequiredRuntimeException;
import mpu.core.RW;
import mpc.fs.UF;
import mpu.core.QDate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Path;

public class ThreadDumpPrinter {
	public static int dumpCtr = 0;

	public static String FILENAME(String... parent) {
		String filename = "AppThreadDump." + QDate.now().f(QDate.F.MONO15_SEC) + "." + (dumpCtr++) + ".td";
		return ARG.isDef(parent) ? UF.normFile(ARG.toDef(parent), filename) : filename;
	}

	public static StringBuilder buildReport(Path... file) {
		StringBuilder report = buildReport();
		if (ARG.isNotDef(file)) {
			return report;
		}
		writeReport(ARG.toDef(file), report);
		return report;

	}

	@NotNull
	public static StringBuilder writeReport(Path file, StringBuilder... report_or_build) {
		StringBuilder report0 = ARG.isDef(report_or_build) ? ARG.toDef(report_or_build) : buildReport();
		try {
			Path path = RW.write_(file, report0);
			P.pl("Thread Dump created & was write to file '{}'", path);
			return report0;
		} catch (IOException e) {
			String msg = "Error build thread-dump report";
			throw new RequiredRuntimeException(e, msg);
		}
	}

	public static StringBuilder print(int tabLevel) {
		return buildReport();
	}

	//https://crunchify.com/how-to-generate-java-thread-dump-programmatically/
	public static StringBuilder buildReport() {
		final StringBuilder dump = new StringBuilder();
		final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
		for (ThreadInfo threadInfo : threadInfos) {
			dump.append('"');
			dump.append(threadInfo.getThreadName());
			dump.append("\" ");
			final Thread.State state = threadInfo.getThreadState();
			dump.append("\n   java.lang.Thread.State: ");
			dump.append(state);
			final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
			for (final StackTraceElement stackTraceElement : stackTraceElements) {
				dump.append("\n        at ");
				dump.append(stackTraceElement);
			}
			dump.append("\n\n");
		}
		return dump;
	}
}
