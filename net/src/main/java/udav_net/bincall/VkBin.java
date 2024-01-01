package udav_net.bincall;

import lombok.RequiredArgsConstructor;
import mpc.env.Env;
import mpc.map.UMap;
import mpc.rfl.RFL;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;

@RequiredArgsConstructor
public class VkBin {

	public static final String CLASS_GDBMOD = "netv5.VkMod";

	public static final String JARNAME_GDBMOD = "gdb-mod.jar";
	public static final String CALL_METHODNAME = "invokeContext0";
	//	final Path jar;

	public static void main(String[] args) {
		VkBin.invokeContext(0, UMap.of("ut", "TTTTTTOOOOKKKKEEEEEEEEEENNNNNNNN", "oid", "-123", "top", "20231221", "return", "rows", "count", 3));
		VkBin.invokeContext(0, UMap.of("ut", "TTTTTTOOOOKKKKEEEEEEEEEENNNNNNNN", "pid", "-123_3"));
		VkBin.invokeContext(0, UMap.of("ut", "TTTTTTOOOOKKKKEEEEEEEEEENNNNNNNN", "oid", "-123", "from_pid", "-123_3", "count", 3));
//		P.exit(rows);
	}

	public static Object invokeContext(Object auth, Map context) {
		return invokeJar0(new Class[]{Object.class, Map.class}, new Object[]{auth, context});
	}

	public static Object invokeWriteDb(Object fileKey_Or_UserAlias, String sheetId, String tableMapping, String db) {
		Map context = UMap.of("mapping", tableMapping, "db", db, "sheet", sheetId);
		return invokeContext(fileKey_Or_UserAlias, context);
	}

	public static Object readDataWriteFile(Object fileKey_Or_UserAlias, String sheetId, String range, String toFile) {
		Map context = UMap.of("file", toFile, "sheet", sheetId, "range", range);
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
		return Env.getBinPath(JARNAME_GDBMOD, true);
	}

}
