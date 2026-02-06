package mpc.types.tks;

import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.str.STR;

// SeqOptionsID '--arg'  - param in command
public class SO2 extends Pare<String, Boolean> {

	public static final String SFX = "--";

	public SO2(String key, Boolean val) {
		super(key, val);
	}

	public static String wrap(Enum val) {
		return wrap(val.name());
	}

//	public static String wrapCap(Enum val) {
//		return wrap(STR.capitalize(val.name().toLowerCase()));
//	}

	public static String wrapCapName(Enum name, String pfx) {
		return pfx + STR.capitalize(wrap(name));
	}

//	public static String wrap(Enum val, String sfx, String cap) {
//		return wrap(val.name());
//	}

//	public static String wrap(String name, String pfx) {
//		return pfx + wrap(name);
//	}
//
//	public static String wrap(String name, String pfx, String sfx) {
//		return pfx + wrap(name) + sfx;
//	}

	public static String wrap(String name) {
		return SFX + name;
	}

	public static String unwrap(String val, String... defRq) {
		return val.startsWith(SFX) ? val.substring(SFX.length()) : ARG.toDefThrowMsg(() -> X.f("Except double '--arg' vs '%s'", val), defRq);
	}

//
//	@Override
//	public int hashCode() {
//		return Objects.hashCode(els);
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		return obj == null || obj.getClass() != SOID.class ? false : Arrays.equals(els, ((SOID) obj).els);
//	}

}
