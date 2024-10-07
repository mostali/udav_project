package zk_os.core;

import mpu.pare.Pare;

public class Sdn extends Pare<String, String> {
	public Sdn(String sd3, String pagename) {
		super(sd3, pagename);
	}

	public static Sdn of(String sd3, String pagename) {
		return new Sdn(sd3, pagename);
	}

	public static Sdn of(Pare pare) {
		return of(pare.keyStr(), pare.valStr());
	}
}
