package udav_net.bincall;

import lombok.RequiredArgsConstructor;
import mpe.core.P;
import mpc.env.Env;
import mpc.map.MAP;
import mpc.rfl.RFL;
import mpu.SysExec;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;

@RequiredArgsConstructor
public class GsvBinOld {

	public static final String CLASS_GDBMOD = "app_gsv.mod.GsvMod";

	public static final String JARNAME_GDBMOD = "gsv-mod.jar";
	public static final String CALL_METHODNAME = "invokeContext0";

	//	final Path jar;
	private static Object invokeExec0(String puid) {
		SysExec.exec_SafeSpace("java", "-jar", getAndCheckJarLocation().toString(), "-puid", puid, "--gsv");
//		Object o = RFL.invokeJarSt(getAndCheckJarLocation(), CLASS_GDBMOD, CALL_METHODNAME, types, vls);
//		return o;
		return null;
	}

	public static void main(String[] args) {
		GsvBinOld.invokeExec0("39");
		GsvBinOld.invokeContext(0, MAP.of("puid", "39", "act", "gsv"));
		P.exit();
		GsvBinOld.invokeContext(0, MAP.of("ut", "TTTTTTOOOOKKKKEEEEEEEEEENNNNNNNN", "oid", "-123", "top", "20231221", "return", "rows", "count", 3));
		GsvBinOld.invokeContext(0, MAP.of("ut", "TTTTTTOOOOKKKKEEEEEEEEEENNNNNNNN", "pid", "-123_3"));
		GsvBinOld.invokeContext(0, MAP.of("ut", "TTTTTTOOOOKKKKEEEEEEEEEENNNNNNNN", "oid", "-123", "from_pid", "-123_3", "count", 3));
//		P.exit(rows);
	}

	public static Object invokeContext(Object auth, Map context) {
		return invokeJar0(new Class[]{Object.class, Map.class}, new Object[]{auth, context});
	}

	public static Object invokeWriteDb(Object fileKey_Or_UserAlias, String sheetId, String tableMapping, String db) {
		Map context = MAP.of("mapping", tableMapping, "db", db, "sheet", sheetId);
		return invokeContext(fileKey_Or_UserAlias, context);
	}

	public static Object readDataWriteFile(Object fileKey_Or_UserAlias, String sheetId, String range, String toFile) {
		Map context = MAP.of("file", toFile, "sheet", sheetId, "range", range);
		return invokeContext(fileKey_Or_UserAlias, context);
	}

	private static Object invokeJar0(Class[] types, Object[] vls) {
		Object o = RFL.invokeJarSt(getAndCheckJarLocation(), CLASS_GDBMOD, CALL_METHODNAME, types, vls);
		return o;
	}

	private static Object invokeJarWith0(Object... kv) {
		Object o = RFL.invokeJarStWith(getAndCheckJarLocation(), CLASS_GDBMOD, CALL_METHODNAME, kv);
		return o;
	}

	@NotNull
	private static Path getAndCheckJarLocation() {
		return Env.getNativeBinLibsPath(JARNAME_GDBMOD, true);
	}

}
