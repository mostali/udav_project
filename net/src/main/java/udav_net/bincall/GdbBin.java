package udav_net.bincall;

import lombok.RequiredArgsConstructor;
import mpc.arr.Arr;
import mpc.core.P;
import mpc.env.Env;
import mpc.map.UMap;
import mpc.rfl.RFL;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class GdbBin {

	public static final String CLASS_GDBMOD = "mp.utl_gdb.GdbMod";

	public static final String JARNAME_GDBMOD = "gdb-mod.jar";
	public static final String CALL_METHODNAME = "invokeContext0";
	//	final Path jar;

	public static void main(String[] args) {
//		ArrayList<ArrayList<Object>> rows = (ArrayList<ArrayList<Object>>) GdbBin.invokeContext(0, UMap.of("sheet", "1R8jTpQuXMEqXlUqt1fC2nLGzVOP8wisJXftl6TZ-1ew", "range", "tasks!A1:Z", "return", "rows"));
//		GdbBin.invokeContext(0, UMap.of("sheet", "1R8jTpQuXMEqXlUqt1fC2nLGzVOP8wisJXftl6TZ-1ew", "range", "test!A1:Z", "rows", Arr.as2(1, 2, 3)));
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
