package lifebeat;

import mpu.Sys;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

public class SysAvgPrinter {

	public static void main(String[] args) {
		Sys.p(print(0));
	}

	public static StringBuilder print(int tabLevel) {
		return buildReport();
	}

	//https://crunchify.com/how-to-generate-java-thread-dump-programmatically/
	public static StringBuilder buildReport() {
		final StringBuilder dump = new StringBuilder();
		OperatingSystemMXBean mxBean = ManagementFactory.getOperatingSystemMXBean();
		double loadAverage = mxBean.getSystemLoadAverage();
		dump.append("SysLoadAvg(").append(Math.scalb(loadAverage, 2)).append("), ");
		int procs = mxBean.getAvailableProcessors();
		dump.append("Proc's(").append(procs).append("), ");
		final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		int tc = threadMXBean.getThreadCount();
		dump.append("Thread's(").append(tc).append(")");
		return dump;
	}
}
