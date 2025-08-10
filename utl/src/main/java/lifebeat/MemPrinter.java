package lifebeat;

import mpu.Sys;
import mpu.str.STR;

import java.util.TimerTask;

import static mpu.str.Hu.KB_TB;
import static mpu.str.Hu.PCT;

public class MemPrinter extends LifePrinter {

	public static void main(String[] args) {
		Sys.RUN_TIMER(0, 5000, new TimerTask() {
			@Override
			public void run() {
				printTo(mpc.log.L.L);
			}
		});
//		RUN(3000);
	}


	public static int LAST_USED_PCT = 100;

	public static StringBuilder buildReport(int tabLevel) {

		String TAB = STR.TAB(tabLevel);
		String TAB_ = STR.TAB(tabLevel + 1);
		String TAB2 = STR.TAB(tabLevel + 2);

		Runtime rt = Runtime.getRuntime();

		long totalMemory = rt.totalMemory();
		long freeMemory = rt.freeMemory();
		long used = totalMemory - freeMemory;
		long maxMemory = rt.maxMemory();

		StringBuilder sb = new StringBuilder();

		sb.append(TAB);
		sb.append("Mem(").append(KB_TB(maxMemory)).append("), ");
		sb.append("Total(").append(KB_TB(totalMemory)).append("), ");
		sb.append("Free(").append(KB_TB(freeMemory)).append(", " + PCT(freeMemory, totalMemory)).append("%), ");
		sb.append("Used(").append(KB_TB(used)).append(", " + (LAST_USED_PCT = PCT(used, totalMemory))).append("%)");

		return sb;
	}

}
