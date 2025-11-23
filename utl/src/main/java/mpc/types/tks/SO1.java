package mpc.types.tks;

import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;

// SeqOptionsID '-arg val'  - param in command
public class SO1 extends Pare<String, String> {

	public static final String SFX = "-";

	public SO1(String key, String val) {
		super(key, val);
	}

	public static String wrap(String name) {
		return SFX + name;
	}


	public static String unwrap(String val, String... defRq) {
		return val.startsWith(SFX) ? val.substring(SFX.length()) : ARG.toDefThrowMsg(() -> X.f("Except single '-arg' vs '%s'", val), defRq);
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
