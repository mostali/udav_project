package mpc.env;


import mpc.fs.UFS;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.RW;

import java.nio.file.Path;

public class EnvTmp {

	public static void writeApp(String filename, String val) {
		Path tmpLocal = IT.isDirExist(Env.TMP_LOCAL, "create tmp local dir %s", Env.TMP_LOCAL);
		RW.write(tmpLocal.resolve(filename), val);
	}

	public static String readApp(String filename, String... defRq) {
		Path tmpLocal = IT.isDirExist(Env.TMP_LOCAL, "create tmp local dir %s", Env.TMP_LOCAL);
		return RW.readString(tmpLocal.resolve(filename), defRq);
	}

	public static String rmApp(String filename, String... defRq) {
		Path tmpLocal = Env.TMP_LOCAL.resolve(filename);
		if (UFS.exist(tmpLocal)) {
			Path tmpLocal1 = UFS.delete(tmpLocal);
			return null;
		}
		return ARG.toDefThrowMsg(() -> X.f("not found tmp local item '%s'", filename), defRq);
	}
}
