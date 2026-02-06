package udav_net.bincall;

import lombok.RequiredArgsConstructor;
import mpc.env.APP;
import mpc.env.Env;
import mpc.json.GsonMap;
import mpc.map.MAP;
import mpc.rfl.RFL;
import mpf.zcall.ZJar;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GdbBin {

	public static final String CLASS_PACKAGE = "mp.utl_gdb";
	public static final String CLASS_GDBMOD = CLASS_PACKAGE + ".GdbMod";

	public static final String JARNAME_GDBMOD_V3 = "gdb3-mod.jar";
	public static final String JARNAME_GDBMOD_V1 = "gdb2-mod.mk17.stable.jar";
	public static final String JARNAME_GDBMOD = "gdb-mod.jar";
	public static final String CALL_METHODNAME = "invokeContext0";
	public static final String METHOD_LOAD_SHEET_ROWS_WITH_META = "load_sheet_rows_with_meta";
	public static final String METHOD_LOAD_SHEET_ROWS = "load_sheet_rows";
	public static final String METHOD_WRITE_SHEET_ROWS = "write_sheet_rows";
	public static final String METHOD_CLEAR_SHEET = "clear_sheet";

	public static void main(String[] args) {
//		Object rows = GdbBin.invokeContext(UMap.of("sheet", "1R8jTpQuXMEqXlUqt1fC2nLGzVOP8wisJXftl6TZ-1ew", "range", "tasks!A1:Z", "return", ReturnAsType.rows));
//		Path pathKey = Paths.get("/home/dav/.data/tsm/__GD/gd.key.json");
//		Object rows = GdbBin.invokeContext(UMap.of("sheet", "1R8jTpQuXMEqXlUqt1fC2nLGzVOP8wisJXftl6TZ-1ew", "range", "tasks!A1:Z", "return", ReturnAsType.rows));
//		ArrayList<ArrayList<Object>> rows = (ArrayList<ArrayList<Object>>) GdbBin.invokeContext(0, UMap.of("sheet", "1R8jTpQuXMEqXlUqt1fC2nLGzVOP8wisJXftl6TZ-1ew", "range", "tasks!A1:Z", "return", "rows"));
//		GdbBin.invokeContext(0, UMap.of("sheet", "1R8jTpQuXMEqXlUqt1fC2nLGzVOP8wisJXftl6TZ-1ew", "range", "test!A1:Z", "rows", Arr.as2(1, 2, 3)));
//		P.exit(rows);
	}

	@Deprecated
	public static Object invokeContext(Map context) {
		return invokeJar0(new Class[]{Object.class, Map.class}, new Object[]{APP.getPathGdKey(), context});
	}

	@Deprecated
	public static Object invokeContext(Object auth, Map context) {
		return invokeJar0(new Class[]{Object.class, Map.class}, new Object[]{auth, context});
	}

	@Deprecated
	public static Object invokeWriteDb(Object fileKey_Or_UserAlias, String sheetId, String tableMapping, String db) {
		Map context = MAP.of("mapping", tableMapping, "db", db, "sheet", sheetId);
		return invokeContext(fileKey_Or_UserAlias, context);
	}

	@Deprecated
	public static Object readDataWriteFile(Object fileKey_Or_UserAlias, String sheetId, String range, String toFile) {
		Map context = MAP.of("file", toFile, "sheet", sheetId, "range", range);
		return invokeContext(fileKey_Or_UserAlias, context);
	}

	@Deprecated
	public static Object readData(Object fileKey_Or_UserAlias, String sheetId, String range, String toFile) {
		Map context = MAP.of("sheet", sheetId, "range", range);
		return invokeContext(fileKey_Or_UserAlias, context);
	}

	@Deprecated
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
		return Env.getNativeBinLibsPath(JARNAME_GDBMOD_V3, true);
	}


	@Deprecated
	public static void readDataFromGdImpl(String dbFile, Pare<String, String> sheetMapping, Object fileKey_Or_UserAlias) {
		readDataFromGdImpl(fileKey_Or_UserAlias, dbFile, sheetMapping);
	}

	public static void readDataFromGdImpl(Object fileKey_Or_UserAlias, String dbFile, Pare<String, String> sheetMapping) {
		String gdSheetId = sheetMapping.key();
		String tableMapping = sheetMapping.val();
//		Path jar = APP.isPromMode() ? Env.RUN_LOCATION.resolve("utl-gdb").resolve(GdbBin.JARNAME_GDBMOD) : Env.RL_PJM.resolve("utl-gdb/target").resolve(GdbBin.JARNAME_GDBMOD);
//		Path jar = Env.RL_PJM.resolve("utl-gdb/target").resolve(GdbBin.JARNAME_GDBMOD_V1);
//		Path jar = Env.APPVOL_BIN_DIR.resolve(GdbBin.JARNAME_GDBMOD_V3);
		Path jar = getAndCheckJarLocation();
		IT.isFileExist(jar, "Run [mvn -f ~/pjm/mp/pom.xml package -Putl-gdb-mod]");
		IT.notNull(fileKey_Or_UserAlias, "fileKey_Or_UserAlias");
		IT.isTypeAny(fileKey_Or_UserAlias, new Class[]{String.class, Path.class});
		Class fileKey_Or_UserAlias_Class = fileKey_Or_UserAlias instanceof Path ? Path.class : String.class;
		Class[] run_args_class = new Class[]{fileKey_Or_UserAlias_Class, String.class, String.class, Path.class};
		Object[] run_args = new Object[]{fileKey_Or_UserAlias, gdSheetId, tableMapping, Paths.get(dbFile)};
		RFL.invokeJarSt(jar, GdbBin.CLASS_GDBMOD, "readDataFromGd", run_args_class, run_args);

		GdbBin.invokeWriteDb(fileKey_Or_UserAlias, gdSheetId, tableMapping, dbFile);

	}

	public static <T> T loadSheetRows(Path modJar, Path gdAuth, String sidBacklog, String range, boolean... withMeta) {
		ZJar zJar = ZJar.of(modJar, ARR.of(CLASS_PACKAGE));
		boolean isWithMeta = ARG.isDefEqTrue(withMeta);
		Object rsp = zJar.invokeWithArgs(isWithMeta ? METHOD_LOAD_SHEET_ROWS_WITH_META : METHOD_LOAD_SHEET_ROWS, gdAuth.toString(), sidBacklog, range);
		if (isWithMeta) {
			if (true) {
				return (T) GsonMap.ofObj(rsp);
			}
			List[] rowsWithMeta = (List[]) rsp;
			List rows = (List) rowsWithMeta[0].stream().map(GsonMap::ofObj).collect(Collectors.toList());
			List meta = (List) rowsWithMeta[0].stream().map(GsonMap::ofObj).collect(Collectors.toList());
			return (T) new List[]{rows, meta};
		} else {
			GsonMap gsonMap = GsonMap.of(rsp + "");
			if (true) {
				return (T) gsonMap;
			}
			List<List> values = gsonMap.getAsArray("values");
			return (T) values;
		}
	}

	public static String clearSheet(Path modJar, Path gdAuth, String sidBacklog, String range) {
		return (String) ZJar.of(modJar, ARR.of(CLASS_PACKAGE)).invokeWithArgs(METHOD_CLEAR_SHEET, gdAuth.toString(), sidBacklog, range);
	}

	public static String writeSheetRows(Path modJar, Path gdAuth, String sidBacklog, String range, List<List> rows) {
		return (String) ZJar.of(modJar, ARR.of(CLASS_PACKAGE)).invokeWithArgs(METHOD_WRITE_SHEET_ROWS, gdAuth.toString(), sidBacklog, range, rows);
	}

	public static GdRowsLoader getGdRowsLoader(Path modJar, Path gdAuth, String sidBacklog, String range) {
		return new GdRowsLoader(modJar, gdAuth, sidBacklog, range);
	}

	@Deprecated
	public enum ReturnAsType {
		json, rows
	}

	@RequiredArgsConstructor
	public static class GdRowsLoader {
		final Path pathMod;
		final Path pathGdKey;
		final String sheetId;
		final String range;

		GsonMap rspData;

		public List<List> getRowsValuesSimple() {
			if (rspData == null) {
				rspData = loadSheetRows(pathMod, pathGdKey, sheetId, range);
			}
			return rspData.getAsArray("values");
		}

		GsonMap rspMetaData;

		public List<RowWithMeta> getRowsWithMeta() {
			if (rspMetaData == null) {
				rspMetaData = loadSheetRows(pathMod, pathGdKey, sheetId, range, true);
			}
			return convert(rspMetaData);
		}

		private static List<RowWithMeta> convert(GsonMap rspMetaData) {
//			List columnMetadata = rspMetaData.getAsArray("columnMetadata");
			List rowData = rspMetaData.getAsArray("rowData");
			List rowMetadata = rspMetaData.getAsArray("rowMetadata");
			IT.state(rowData.size() == rowMetadata.size(), "except equals rows data with row metadata, but %s!=%s", rowData.size(), rowMetadata.size());
			List rows = new ArrayList<>();
			for (int i = 0; i < rowData.size(); i++) {
				List<Map> row = (List) ((Map) rowData.get(i)).get("values");
				Boolean hiddenByUser = (Boolean) ((Map) rowMetadata.get(i)).get("hiddenByUser");

				List<RowWithMeta.ValueWithMeta> cols = new ArrayList<>();
				for (int x = 0; x < row.size(); x++) {
					RowWithMeta.ValueWithMeta rowWithMeta = new RowWithMeta.ValueWithMeta(x, row.get(x));
					cols.add(rowWithMeta);
				}

				RowWithMeta rowWithMeta = new RowWithMeta(i, cols, hiddenByUser != null && hiddenByUser);
				rows.add(rowWithMeta);
			}
			return rows;
		}


//		public RowWithMeta getRowWithMeta(int rowIndex) {
//			List<List> rowValuesByMeta = getRowValuesByMeta();
//			GsonMap o = loadSheetRows(Paths.get(pathMod), Paths.get(pathGdKey), sheetId, range);
//			return o.getAsArray("values");
//		}

		@RequiredArgsConstructor
		public static class RowWithMeta {
			final int index;
			final List<ValueWithMeta> row;
			final boolean isHiddenByUser;

			@RequiredArgsConstructor
			public static class ValueWithMeta {
				final int colIndex;
				final Map map;

				public Object getValue() {
					Map vlMap = (Map) map.get("userEnteredValue");
					return vlMap.get("stringValue");
				}

				public String getValueString() {
					return X.toStringNN(getValue(), null);
				}
			}
		}
	}

}
