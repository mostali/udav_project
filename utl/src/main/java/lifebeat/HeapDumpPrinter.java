package lifebeat;

import com.sun.management.HotSpotDiagnosticMXBean;
import mpu.core.ARG;
import mpe.core.P;
import mpc.fs.UF;
import mpu.core.QDate;

import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class HeapDumpPrinter extends LifePrinter {

	public static String FILENAME(String... parent) {
		String filename = "AppHeapDump." + QDate.now().f(QDate.F.MONO15_SEC) + "." + (dumpCtr++) + ".hprof";
		return ARG.isDef(parent) ? UF.normFile(ARG.toDef(parent), filename) : filename;
	}


	public static int dumpCtr = 0;

	public static String writeDump() throws IOException {
		return writeDump(FILENAME());
	}


	public static String writeDump(String outputFile) throws IOException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
				server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
		mxBean.dumpHeap(outputFile, true);
		P.pl("AppDump was created & write to file '{}'", outputFile);
		return outputFile;
	}
}
