package mpc.types.tks.cmt;

import mpu.core.ARR;
import mpu.core.ARG;
import mpu.IT;
import mpu.str.SPLIT;
import mpu.str.STR;
import mpc.types.opts.SeqOptions;
import mpu.str.UST;

public class Cmd {
	public static final String METHOD_STR_TO = "strTo";
	public static final String PTRX_DEF_SPACE_SEP = "\\s++";
	public final String original;
	public final String sep;

	public Cmd(String original) {
		this(original, PTRX_DEF_SPACE_SEP);
	}

	public Cmd(String original, String separator) {
		this.original = original;
		this.sep = separator;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + original + "]";
	}

	public static void main(String[] args) {

	}

	public static <V> OBJ<V> toEqOrAny(String str, OBJ<V> val) {
		if (val == null) {
			return (OBJ<V>) OBJ.ANY.newObj(str);
		}
		V objV = UST.strToExt(str, METHOD_STR_TO, val.clazz);
		if (val.val == null) {
			val = val.newObj(objV);
		} else {
			IT.isEqSafe(objV, val.val);
		}
		return val;
	}

	public static <V> OBJ<V> toEqOrRq(String cmd, OBJ<V> val) {
		V objV = UST.strTo(cmd, val.clazz);
		if (val.val == null) {
			val = val.newObj(objV);
		} else {
			IT.isEqSafe(objV, val.val);
		}
		return val;
	}

	public static <C extends Cmd> C ofAs(CharSequence pattern, Class<C> cmd) {
		return UST.strToExt(pattern, METHOD_STR_TO, cmd);
	}

	public boolean isOnlyOne() {
		return true;
	}

	public static String[] toArgs(String cmd) {
		return toArgs(cmd, " ", true);
	}

	public static String[] toArgs(String cmd, String del, boolean... normSpace) {
		if (ARG.isDefEqTrue(normSpace)) {
			cmd = STR.normalizeSpace(cmd);
		}
		return SPLIT.argsByRq(cmd, del);
	}

	public SeqOptions getSeqArgs(int from) {
		return SeqOptions.of(getArgs(from));
	}

	public String[] getArgs(int from, String[]... defRq) {
		return ARR.sublist(toArgs(original), from, defRq);
	}

	public String[] getArgs(int from, int to, String[]... defRq) {
		return ARR.sublist(toArgs(original), from, to, defRq);
	}

	public Cmd throwIsNotWhole() {
		IT.NE(original, "except whole");
		return this;
	}
}
