package mp.tslice;

import mp.gd.ApiGdExt;
import mp.tslice.markup.UNode;
import mpu.Sys;
import mpc.str.sym.SYM;
import mpu.str.STR;
import mpu.core.ARR;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class USlice {


	public static String toStringObj(Object obj, IFEMPTY ifEmpty) {
		if (!isEmptyObj(obj)) {
			return obj.toString();
		} else if (ifEmpty == null) {
			if (obj == null) {
				return null;
			} else {
				return obj.toString();
			}
		} else {
			switch (ifEmpty) {
				case NULL:
					return null;
				case BLANK:
					return SYM.EMPTY;
				case THROW:
					throw new IllegalArgumentException("Col object is empty");
				default:
					throw new IllegalArgumentException("Unknown type IE");
			}
		}
	}

	static {
		Sys.p("Reg ApiCL tag-checker RMM");
		UNode.TAG_CHECKERS.add(new UNode.ITagChecker() {

			@Override
			public String checkTagName(String type) {
				if (type.startsWith("ac:")) {
					return type;
				}
				return null;
			}
		});
	}

	public static List<Object> cutHeadRow(List<List<Object>> slice) {
		return ARR.cutHeadRow(slice);

	}

	@Deprecated
	public static List<List<Object>> loadTableValues(String user, String sheetId, String listWithRange) {
		ApiGdExt.GoogleSheetData data = ApiGdExt.GoogleSheetData.of(user, sheetId, listWithRange);
		data.setUserCredentialsAliasName(user);
		return data.get_VALUES();
	}

	public static List<List<Object>> loadTableValues(Path user, String sheetId, String listWithRange) {
		ApiGdExt.GoogleSheetData data = ApiGdExt.GoogleSheetData.of(user, sheetId, listWithRange);
		return data.get_VALUES();
	}

	public static List<List<Object>> getSliceEntity(List<List<Object>> rows, Integer[] border) {
		if (border[0] == border[1]) {
			List<Object> arrayList = rows.get(border[0]);
			List<List<Object>> returnList = new ArrayList();
			returnList.add(arrayList);
			return returnList;
		}
		return new ArrayList<>(rows.subList(border[0], border[1] + 1));
	}

	public static Integer[] findBord(List<List<Object>> rows, Integer offset) {
		return findBord(rows, null, null, null, offset, null);
	}

	private static Integer[] findBord(List<List<Object>> rows) {
		return findBord(rows, null, null, null, null, null);
	}

	private static Integer[] findBord(List<List<Object>> rows, Object name, Object value, Object ext, Integer offset, Integer level) {
		//U.pf("Rows:::%s; Offset:::%s", rows.size(), offset);
		if (rows.isEmpty()) {
			return null;
		}
		if (offset == null) {
			offset = 0;
		}
		if (level == null) {
			List<Object> row0 = rows.get(offset);
			level = getLevelRow(row0);
			if (level == null) {
				return new Integer[]{offset, offset};
			}
		}
		if (level == null) {
			return null;
		}

		Integer f = null;
		for (int ri = offset; ri < rows.size(); ri++) {
			List<Object> row = rows.get(ri);
			Integer levelCurrentRow = getLevelRow(row);
			if (levelCurrentRow == null) {
				continue;
			}
			if (levelCurrentRow <= level) {
				if (f != null) {
					return new Integer[]{f, ri - 1};
				}
			}
//			if (isRowEqLevel(row, level)) {
			if (!isRowEquals(row, level, name, value, ext)) {
				continue;
			} else if (f != null) {
				return new Integer[]{f, ri - 1};
			} else {
				f = ri;
			}
//			}
		}
		if (f == null) {
			return null;
		} else {
			return new Integer[]{f, rows.size() - 1};
		}
	}

//	private static boolean isRowEqLevel(List<Object> row, int level) {
//		if (level >= row.size()) {
//			return false;
//		}
//		Integer lev = getLevelRow(row);
//		return lev != null && lev.equals(level);
//	}

	public static Integer getLevelRow(List<Object> row) {
		if (row.isEmpty()) {
			return null;
		}
		for (int i = 0; i < row.size(); i++) {
			Object obj = row.get(i);
			if (isEmptyObj(obj)) {
				continue;
			} else {
				return i;
			}
		}
		return null;
	}

	public static Integer getLevelAfterSpace(int startFromLevel, List<Object> singleRow, int spaceMin) {
		Integer levAttr = null;
		int space = 0;
		for (int i = startFromLevel; i < singleRow.size(); i++) {
			Object obj = singleRow.get(i);
			if (isEmptyObj(obj)) {
				space++;
				continue;
			}
			if (space >= spaceMin) {
				return i;
			}
			space = 0;
		}
		return null;
	}

	@Deprecated
	public static Integer getLevelSlice__NU(List<List<Object>> rows) {
		if (rows.isEmpty()) {
			return null;
		}
		for (List<Object> row : rows) {
			Integer level = getLevelRow(row);
			if (level != null) {
				return level;
			}
		}
		return null;
	}

	public static boolean isEmptyStr(String str) {
		return str == null || str.isEmpty();
	}

	public static boolean isEmptyObj(Object obj) {
		return obj == null || (obj instanceof String && isEmptyStr((String) obj));
	}

	public static boolean isSliceEquals(List<List<Object>> slice, Integer level, Object... name_value_ext) {
		return isRowEquals(slice.get(0), level, name_value_ext);
	}

	public static boolean isRowEquals(List<Object> row, Integer level, Object... name_value_ext) {
		if (name_value_ext.length > 2) {
			return isRowEquals(row, level, name_value_ext[0], name_value_ext[1], name_value_ext[2]);
		} else if (name_value_ext.length > 1) {
			return isRowEquals(row, level, name_value_ext[0], name_value_ext[1], (Integer) null);
		} else if (name_value_ext.length > 0) {
			return isRowEquals(row, level, name_value_ext[0], (Integer) null, (Integer) null);
		}
		return isRowEquals(row, level, (Integer) null, (Integer) null, (Integer) null);
	}

	public static boolean isRowEquals(List<Object> row, Integer level, Object name, Object value, Object ext) {
		Integer lev = getLevelRow(row);
		if (lev != level) {
			return false;
		} else if (name != null && !Objects.equals(name, row.get(level))) {
			return false;
		} else if (value != null && (++level >= row.size() || !Objects.equals(value, row.get(level)))) {
			return false;
		} else if (ext != null && (++level >= row.size() || !Objects.equals(ext, row.get(level)))) {
			return false;
		}
		return true;

	}

	public static void trim(List<List<Object>> values) {
		if (values.isEmpty()) {
			return;
		}
		trimStart(values);
		trimEnd(values);
	}

	public static void trimStart(List<List<Object>> values) {
		do {
			if (values.isEmpty()) {
				return;
			}
			int ind0 = 0;
			List<Object> row = values.get(ind0);
			if (TRow.isEmpty(row)) {
				values.remove(ind0);
				continue;
			} else {
				return;
			}
		} while (true);
	}

	public static void trimEnd(List<List<Object>> values) {

		do {
			if (values.isEmpty()) {
				return;
			}
			int indN = values.size() - 1;
			List<Object> row = values.get(indN);
			if (TRow.isEmpty(row)) {
				values.remove(indN);
				if (values.isEmpty()) {
					return;
				}
				continue;
			} else {
				return;
			}
		} while (true);
	}

	public static String getErrorMessageCheckChildLevel(int parentLevel, List<Object> row) {
		Integer level = USlice.getLevelRow(row);
		if (Objects.equals(parentLevel, level + 1)) {
			return null;
		} else {
			return STR.format("Level must be +1, but parent-level is [{0}] and row-level is [{1}] in row [{2}]", parentLevel, level, row);
		}
	}

}
