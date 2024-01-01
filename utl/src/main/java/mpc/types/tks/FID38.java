package mpc.types.tks;

import lombok.RequiredArgsConstructor;
import mpc.ERR;
import mpc.str.SPLIT;
import mpe.str.ToString;
import mpc.str.USToken;

@RequiredArgsConstructor
public class FID38 extends FID {

	public static final String DEL = ":";

	public FID38(Object... els) {
		super(els);
	}

	@Override
	public String del() {
		return DEL;
	}

	public static String to(Object o1, Object o2) {
		return ToString.join(o1, DEL, o2);
	}

	public static String to(Object o1, Object o2, Object o3) {
		return ToString.join(o1, DEL, o2, DEL, o3);
	}

	public static <T> T first(String fid, Class<T> asClass) {
		return USToken.first(fid, DEL, asClass);
	}

	public static String first(String fid) {
		return USToken.first(fid, DEL);
	}

	public static FID38 of(String fid) {
		ERR.state(fid.contains(DEL));
		return new FID38(SPLIT.by_(fid, DEL));
	}

	public static FID38 of(Object first, Object second) {
		return new FID38(first, second);
	}

}
