package mpc.env;

import mpu.Sys;
import mpu.core.RW;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;

public class PidUtils {
	public static String getPid_v0() {
		return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	}
	
	public static int getPid_v1() {
		try {
			java.lang.management.RuntimeMXBean runtime =
					ManagementFactory.getRuntimeMXBean();
			java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
			jvm.setAccessible(true);

//		sun.management.VMManagement mgmt =
//				(sun.management.VMManagement) jvm.get(runtime);
			Object mgmt = jvm.get(runtime);
			java.lang.reflect.Method pid_method =
					mgmt.getClass().getDeclaredMethod("getProcessId");
			pid_method.setAccessible(true);
			int pid = (Integer) pid_method.invoke(mgmt);
			return pid;
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	public static void writeAppPid() {
		try {
			String pid = String.valueOf(PidUtils.getPid_v0());
			Sys.p("Running on PID:" + pid);
			RW.write_(Paths.get("APP.PID"), pid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
