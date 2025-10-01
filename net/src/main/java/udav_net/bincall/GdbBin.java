package udav_net.bincall;

import lombok.RequiredArgsConstructor;
import mpc.env.APP;
import mpc.env.Env;
import mpc.map.MAP;
import mpc.rfl.RFL;
import mpu.IT;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;

@RequiredArgsConstructor
public class GdbBin {

	public static final String CLASS_GDBMOD = "mp.utl_gdb.GdbMod";

	public static final String JARNAME_GDBMOD = "gdb-mod.jar";
	public static final String CALL_METHODNAME = "invokeContext0";

	public static void main(String[] args) {

//		Object rows = GdbBin.invokeContext(UMap.of("sheet", "1R8jTpQuXMEqXlUqt1fC2nLGzVOP8wisJXftl6TZ-1ew", "range", "tasks!A1:Z", "return", ReturnAsType.rows));
//		Path pathKey = Paths.get("/home/dav/.data/tsm/__GD/gd.key.json");
//		Object rows = GdbBin.invokeContext(UMap.of("sheet", "1R8jTpQuXMEqXlUqt1fC2nLGzVOP8wisJXftl6TZ-1ew", "range", "tasks!A1:Z", "return", ReturnAsType.rows));
//		ArrayList<ArrayList<Object>> rows = (ArrayList<ArrayList<Object>>) GdbBin.invokeContext(0, UMap.of("sheet", "1R8jTpQuXMEqXlUqt1fC2nLGzVOP8wisJXftl6TZ-1ew", "range", "tasks!A1:Z", "return", "rows"));
//		GdbBin.invokeContext(0, UMap.of("sheet", "1R8jTpQuXMEqXlUqt1fC2nLGzVOP8wisJXftl6TZ-1ew", "range", "test!A1:Z", "rows", Arr.as2(1, 2, 3)));
//		P.exit(rows);
	}

	public static Object invokeContext(Map context) {
		return invokeJar0(new Class[]{Object.class, Map.class}, new Object[]{APP.getPathGdKey(), context});
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

	public static Object readData(Object fileKey_Or_UserAlias, String sheetId, String range, String toFile) {
		Map context = MAP.of("sheet", sheetId, "range", range);
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


	@Deprecated
	public static void readDataFromGdImpl(String dbFile, Pare<String, String> sheetMapping, Object fileKey_Or_UserAlias) {
		readDataFromGdImpl(fileKey_Or_UserAlias, dbFile, sheetMapping);
	}

	public static void readDataFromGdImpl(Object fileKey_Or_UserAlias, String dbFile, Pare<String, String> sheetMapping) {
		String gdSheetId = sheetMapping.key();
		String tableMapping = sheetMapping.val();
//		Path jar = APP.isPromMode() ? Env.RUN_LOCATION.resolve("utl-gdb").resolve(GdbBin.JARNAME_GDBMOD) : Env.RL_PJM.resolve("utl-gdb/target").resolve(GdbBin.JARNAME_GDBMOD);
		Path jar = Env.RL_PJM.resolve("utl-gdb/target").resolve(GdbBin.JARNAME_GDBMOD);
		IT.isFileExist(jar, "Run [mvn -f ~/pjm/mp/pom.xml package -Putl-gdb-mod]");
		IT.notNull(fileKey_Or_UserAlias, "fileKey_Or_UserAlias");
		IT.isTypeAny(fileKey_Or_UserAlias, new Class[]{String.class, Path.class});
		Class fileKey_Or_UserAlias_Class = fileKey_Or_UserAlias instanceof Path ? Path.class : String.class;
		Class[] run_args_class = new Class[]{fileKey_Or_UserAlias_Class, String.class, String.class, String.class};
		Object[] run_args = new Object[]{fileKey_Or_UserAlias, gdSheetId, tableMapping, dbFile};
		RFL.invokeJarSt(jar, GdbBin.CLASS_GDBMOD, "readDataFromGd", run_args_class, run_args);

		GdbBin.invokeWriteDb(fileKey_Or_UserAlias, gdSheetId, tableMapping, dbFile);

	}

	public enum ReturnAsType {
		json, rows
	}

}
