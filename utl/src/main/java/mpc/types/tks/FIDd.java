package mpc.types.tks;

import lombok.RequiredArgsConstructor;
import mpu.str.JOIN;
import mpu.str.SPLIT;
import mpu.str.TKN;
import mpu.IT;

@RequiredArgsConstructor
public class FIDd extends FID {

	public static final String DEL = "@";

	public FIDd(Object... els) {
		super(els);
	}

	@Override
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

	public static FIDd of(String fid) {
		IT.state(fid.contains(DEL));
		return new FIDd(SPLIT.argsBy(fid, DEL));
	}

	public static FIDd of(Object first, Object second) {
		return new FIDd(first, second);
	}

}
