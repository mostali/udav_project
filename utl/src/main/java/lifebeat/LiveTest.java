package lifebeat;

import lifebeat.mod.OomMod;

import java.util.ArrayList;
import java.util.List;

public class LiveTest {
	public static List l = new ArrayList();


	public static void main(String[] args) {
//		UDev.runInfinity(3);
//		installGCMonitoring();

//		GcPrinter.RUN(1000);
//		MemPrinter.RUN(1000);

		OomMod.RUN(1000, null, null, 10_000_000);

	}


}
