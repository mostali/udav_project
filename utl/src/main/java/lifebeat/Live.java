package lifebeat;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class Live {
	public static void main(String[] args) throws IOException {

//		Process proc = Runtime.getRuntime().exec("free -m");
//		P.p(proc.getInputStream());

//		Process proc2 = Runtime.getRuntime().exec("top -b");
//		P.p(proc2.getInputStream());

		top();
	}

	private static void top() {
//		ProcessBuilder pb = new ProcessBuilder("top", "-l", "1");
		ProcessBuilder pb = new ProcessBuilder("top", "-b");
		pb.redirectError();
		try {
			Process p = pb.start();
			InputStream is = p.getInputStream();
			int value = -1;
			while ((value = is.read()) != -1) {
				System.out.print(((char) value));
			}
			int exitCode = p.waitFor();
			System.out.println("Top exited with " + exitCode);
		} catch (IOException exp) {
			exp.printStackTrace();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}


	private static void loadSysAverage() {
		OperatingSystemMXBean mxBean = ManagementFactory.getOperatingSystemMXBean();
		double loadAverage = mxBean.getSystemLoadAverage();
	}
}
