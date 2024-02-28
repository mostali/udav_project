package lifebeat;

import mpu.str.STR;

import static mpu.str.Hu.KB_TB;
import static mpu.str.Hu.PCT;

public class MemPrinter extends LifePrinter{

	public static void main(String[] args) {
//		U.RUN(0, 1000, new TimerTask() {
//			@Override
//			public void run() {
//				U.p(printTo(0));
//			}
//		});
//		RUN(3000);
	}


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
		sb.append("Used(").append(KB_TB(used)).append(", " + PCT(used, totalMemory)).append("%)");

		return sb;
	}

}
