package zk_old_core.sd.core;

import lombok.RequiredArgsConstructor;
import mpu.core.ARG;
import mpu.str.JOIN;
import mpc.exception.FIllegalArgumentException;
import mpu.str.USToken;

@RequiredArgsConstructor
public class Sd3ID {

	public static final String[] EXAMPLE_STRICT = {"sd@", "sd@page", "@page"};

	public final String src;
//	@NotNull
	public final String[] two;

	public Type type() {
		return Type.get(src);
	}

	public enum Type {
		SD, PAGE, SD_PAGE, WORD;

		public static Type get(String sdid) {
			int ind = sdid.indexOf('@');
			if (ind == -1) {
				return Type.WORD;
			} else if (ind == 0) {
				return Type.PAGE;
			} else if (ind == sdid.length() - 1) {
				return Type.SD;
			}
			return SD_PAGE;
		}
	}

	@Override
	public String toString() {
		return two == null ? src : JOIN.argsBy("@", two);
	}

	public static boolean isStrictSyntax(String id) {
		return id.indexOf('@') > 0;
	}

	public static Sd3ID of(String id, boolean... checkStrictSyntax) {
		String[] two = USToken.two(id, "@", null);
		Sd3ID sd3Id = new Sd3ID(id, two);
		if (ARG.isDefEqTrue(checkStrictSyntax)) {
			checkStrictSDID(sd3Id);
		}
		return sd3Id;
	}

	public boolean isStrictSyntax() {
		return two != null;
	}

	public boolean isSingleSd3() {
		return src.indexOf('@') == src.length() - 1;
	}

	public boolean isSignlePage() {
		return src.lastIndexOf('@') == 0;
	}

	public String sd3() {
		return two == null ? src : i(0);
	}

	public String page() {
		return i(1);
	}

	public String i(int i) {
		return two[i];
	}

	public boolean isClean() {
		return src.indexOf('@') < 0;
	}

	/**
	 * *************************************************************
	 * ---------------------------- CHECK --------------------------
	 * *************************************************************
	 */

	public static boolean checkStrictSDID(Sd3ID sd3ID, boolean... RETURN) {
		if (sd3ID.isStrictSyntax()) {
			return true;
		}
		if (ARG.isDefEqTrue(RETURN)) {
			return false;
		}
		throw new FIllegalArgumentException("Illegal SDID [%s]. Valid SDID [ sd@page | sd@ | @page ]", sd3ID.src);
	}

	public static boolean checkSDID_NEEDNOTCLEAN(Sd3ID sd3ID, boolean... RETURN) {
		if (sd3ID.isClean()) {
			if (ARG.isDefEqTrue(RETURN)) {
				return false;
			}
			throw new FIllegalArgumentException("Illegal SDID [%s]. Not-Clean SDID [ sd@ | @page | sd@page ]", sd3ID.src);
		}
		return true;
	}

	public static boolean checkSDID_NEEDCLEAN(Sd3ID sd3ID, boolean... RETURN) {
		if (!sd3ID.isClean()) {
			if (ARG.isDefEqTrue(RETURN)) {
				return false;
			}
			throw new FIllegalArgumentException("Illegal SDID [%s]. Clean SDID [ sd ]", sd3ID.src);
		}
		return true;
	}

	public static boolean checkSDID_SINGLESD3(Sd3ID sd3ID, boolean... RETURN) {
		if (!sd3ID.isSingleSd3()) {
			if (ARG.isDefEqTrue(RETURN)) {
				return false;
			}
			throw new FIllegalArgumentException("Illegal SDID [%s]. Single SDID [ sd@ ]", sd3ID.src);
		}
		return true;
	}

}
