package mpc.types.tks;

import mpu.str.JOIN;
import mpu.str.SPLIT;
import mpu.str.TKN;
import mpu.IT;

//@RequiredArgsConstructor
public class FIDD {

	public static final String DEL = "@";

	final String[] els;

	public FIDD(String... els) {
		this.els = els;
	}

	//	@Override
	public String del() {
		return DEL;
	}

	public static String to(Object o1, Object o2) {
		return JOIN.args(o1, DEL, o2);
	}

	public static String to(Object o1, Object o2, Object o3) {
		return JOIN.args(o1, DEL, o2, DEL, o3);
	}

	public static <T> T first(String fid, Class<T> asClass) {
		return TKN.first(fid, DEL, asClass);
	}

	public static String first(String fid) {
		return TKN.first(fid, DEL);
	}

	public static FIDD of(String fid) {
		IT.state(fid.contains(DEL));
		return new FIDD(SPLIT.argsBy(fid, DEL));
	}

	public static FIDD of(Object first, Object second) {
		return new FIDD(IT.NN(first) + "", IT.NN(second) + "");
	}

}
