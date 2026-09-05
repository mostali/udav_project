package mp.tslice;

import mpu.core.ARR;
import mpc.str.sym.SYM;
import mpu.str.STR;

import java.util.List;

public class TRow {

	private final List<Object> row;
	private Integer index;

	public String name() {
		return getNvxObjectAsString(row, NVX.NAME);
	}

	public String val() {
		return getNvxObjectAsString(row, NVX.VALUE);
	}

	public String ext() {
		return getNvxObjectAsString(row, NVX.EXT);
	}

	public TRow(List<Object> row, Integer index) {
		this.row = row;
		this.index = index;

	}

	//
	public static String nameAsString(List<Object> row) {
		return getNvxObjectAsString(row, NVX.NAME);
	}

	public static Object nameRequired(List<Object> row) {
		return getNvxObject(row, NVX.NAME, IFEMPTY.THROW);
	}

	public static Object nameOrNull(List<Object> row) {
		return getNvxObject(row, NVX.NAME, IFEMPTY.NULL);
	}

	//
	public static String valueAsString(List<Object> row) {
		return getNvxObjectAsString(row, NVX.VALUE);
	}

	public static Object valueRequired(List<Object> row) {
		return getNvxObject(row, NVX.VALUE, IFEMPTY.THROW);
	}

	public static Object valueOrNull(List<Object> row) {
		return getNvxObject(row, NVX.VALUE, IFEMPTY.NULL);
	}

	//
	public static String extAsString(List<Object> row) {
		return getNvxObjectAsString(row, NVX.EXT);
	}

	public static Object extRequired(List<Object> row) {
		return getNvxObject(row, NVX.EXT, IFEMPTY.THROW);
	}

	public static Object extOrNull(List<Object> row) {
		return getNvxObject(row, NVX.EXT, IFEMPTY.NULL);
	}

	//
	public static String getNvxObjectAsString(List<Object> row, NVX nvx) {
		return USlice.toStringObj(getNvxObject(row, nvx), null);
	}

	public static String[] getNvxStringArray(List<Object> row) {
		Integer lev = USlice.getLevelRow(row);
		String[] nvxArray = new String[]{null, null, null};
		if (lev == null) {
			return nvxArray;
		}
		if (lev < row.size()) {
			nvxArray[0] = USlice.toStringObj(row.get(lev), null);
		}
		if (++lev < row.size()) {
			nvxArray[1] = USlice.toStringObj(row.get(lev), null);
		}
		if (++lev < row.size()) {
			nvxArray[2] = USlice.toStringObj(row.get(lev), null);
		}
		return nvxArray;
	}

	public static Object getNvxObject(List<Object> row, NVX nvx, IFEMPTY... ife) {
		IFEMPTY _ife = ARR.defIfNull(IFEMPTY.NULL, ife);
		Integer lev = USlice.getLevelRow(row);
		try {
			switch (nvx) {
				case NAME:
					return row.get(lev);
				case VALUE:
					return row.get(lev + 1);
				case EXT:
					return row.get(lev + 2);
			}
		} catch (Exception ex) {
			switch (_ife) {
				case NULL:
					return null;
				case BLANK:
					return SYM.EMPTY;
				case THROW:
				default:
					throw ex;
			}
		}
		throw new IllegalArgumentException("What is nvx-objext ? " + nvx);
	}

	public static TRow of(List<Object> row) {
		return of(row, null);
	}

	public static TRow of(List<Object> row, Integer index) {
		return new TRow(row, index);
	}

	public static boolean isEmpty(List<Object> row) {
		if (row.isEmpty()) {
			return true;
		}
		return USlice.getLevelRow(row) == null;
	}

	public static String concatValue(List<List<Object>> rows, int[] index, String delimetrCol, String delimetrRow) {
		StringBuilder sb = new StringBuilder();
		boolean needRemoveLastRowDelimetr = false;
		boolean needRemoveLastColDelimetr = false;
		for (List<Object> row : rows) {
			for (int ind : index) {
				if (ind < row.size()) {
					Object v = row.get(ind);
					if (v != null) {
						sb.append(v).append(delimetrCol);
						needRemoveLastColDelimetr = true;
					}
				}
			}
			if (needRemoveLastColDelimetr) {
				STR.removeLast(sb, delimetrCol);
				needRemoveLastColDelimetr = false;
			}
			sb.append(delimetrRow);
			needRemoveLastRowDelimetr = true;
		}
		if (needRemoveLastRowDelimetr) {
			STR.removeLast(sb, delimetrRow);
			needRemoveLastRowDelimetr = false;
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return "TRow{" +
				"row=" + row +
				", index=" + index +
				'}';
	}

	public Integer level() {
		return USlice.getLevelRow(this.row);
	}
}
