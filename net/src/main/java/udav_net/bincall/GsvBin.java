package udav_net.bincall;

import lombok.RequiredArgsConstructor;
import mpc.env.Env;
import mpc.rfl.RFL;
import mpu.X;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@RequiredArgsConstructor
public class GsvBin {

	public static final String JARNAME_GDBMOD = "gsv.jar";
	public static final String CLASS_GDBMOD = "app_gsv.NEV";
	public static final String CALL_METHODNAME = "invokeLines";

	public static void main(String[] args) {
		X.exit(invokeLinesProxy(new String[]{"-piud", "all"}));
	}

	@NotNull
	private static Path getAndCheckJarLocation() {
		return Env.getNativeBinLibsPath(JARNAME_GDBMOD, true);
	}

	public static Object invokeLinesProxy(String[] args) {
		Class[] types = {args.getClass()};
		Object[] vls = {args};
		Object o = RFL.invokeJarSt(getAndCheckJarLocation(), CLASS_GDBMOD, CALL_METHODNAME, types, vls);
		return o;
	}
}
