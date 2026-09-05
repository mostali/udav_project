package mpe.str.table;

import mpc.json.GsonMap;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Matrix {

	public static void normalizeWidth(List<List> ll) {
		if (ll == null || ll.isEmpty()) {
			return;
		}
		int maxWidth = 0;
		for (List row : ll) {
			if (row != null && row.size() > maxWidth) {
				maxWidth = row.size();
			}
		}
		for (List row : ll) {
			if (row == null) {
				continue;
			}
			while (row.size() < maxWidth) {
				row.add(null);
			}
		}
	}

	public static List<List> copyAndClean(List<List> ll, Integer[] start, Integer[] end, boolean modifyOriginal) {
		List<List> result = new ArrayList<>();
		for (int y = 0; y < ll.size(); y++) {
			List originalRow = (List) ll.get(y);
			List newRow = new ArrayList<>();
			for (int x = 0; x < originalRow.size(); x++) {
				if (y >= start[1] && y <= end[1] && x >= start[0] && x <= end[0]) {
					Object value = originalRow.get(x);
					newRow.add(value);
					if (modifyOriginal) {
						originalRow.set(x, null);
					}
				} else {
					newRow.add(null);
				}
			}
			result.add(newRow);
		}
		return result;
	}

	public static List<List> trimAll(List<List> ll, boolean mutateThisList) {
		if (mutateThisList) {
			TrimAllMutate.trimAll(ll);
			return ll;
		} else {
			return TrimAllClone.trimAll(ll);
		}

	}


	public static List<List> rotateLeft(List<List> matrix) {
		if (matrix == null || matrix.isEmpty()) {
			return new ArrayList<>();
		}

		int rows = matrix.size();
		int cols = matrix.get(0).size();

		List<List> result = new ArrayList<>();

		// Поворот против часовой стрелки: новая матрица будет размером cols x rows
		for (int j = cols - 1; j >= 0; j--) {
			List newRow = new ArrayList<>();
			for (int i = 0; i < rows; i++) {
				Object value = matrix.get(i).get(j);
				newRow.add(value);
			}
			result.add(newRow);
		}

		return result;
	}

	public static int maxWidth(List<List> matrix, int orDefault) {
		Integer maxSize = null;
		for (List<String> row : matrix) {
			if (row != null && row.size() > (maxSize == null ? 0 : maxSize)) {
				maxSize = row.size();
			}
		}
		return maxSize == null ? orDefault : maxSize;
	}

	public static List<List> rspToValues(Object rsp) {
		return rspToValues(rsp, false, true);
	}

	public static <T> T rspToValues(Object rsp, boolean withMeta, boolean needListOrGsonMap) {
		if (withMeta) {
			if (true) {
				return (T) GsonMap.ofObj(rsp);
			}
			List[] rowsWithMeta = (List[]) rsp;
			List rows = (List) rowsWithMeta[0].stream().map(GsonMap::ofObj).collect(Collectors.toList());
			List meta = (List) rowsWithMeta[0].stream().map(GsonMap::ofObj).collect(Collectors.toList());
			return (T) new List[]{rows, meta};
		} else {
			GsonMap gsonMap = GsonMap.of(rsp + "");
			if (ARG.isDefNotEqTrue(needListOrGsonMap)) {
				return (T) gsonMap;
			}
			List<List> values = gsonMap.getAsArray("values", ARR.EMPTY_LIST);
			return (T) values;
		}
	}

	private static class TrimAllClone {
		private static List<List> trimAll(List<List> ll) {
			if (ll == null || ll.isEmpty()) {
				return new ArrayList<>();
			}

			int rows = ll.size();
			int cols = ll.get(0).size();

			// Находим строки с данными
			boolean[] nonEmptyRows = new boolean[rows];
			boolean[] nonEmptyCols = new boolean[cols];

			for (int y = 0; y < rows; y++) {
				List row = ll.get(y);
				for (int x = 0; x < cols; x++) {
					if (X.notEmptyObj_Str(row.get(x))) {
						nonEmptyRows[y] = true;
						nonEmptyCols[x] = true;
					}
				}
			}

			// Находим границы
			int firstRow = -1, lastRow = -1;
			int firstCol = -1, lastCol = -1;

			for (int y = 0; y < rows; y++) {
				if (nonEmptyRows[y]) {
					if (firstRow == -1) {
						firstRow = y;
					}
					lastRow = y;
				}
			}

			for (int x = 0; x < cols; x++) {
				if (nonEmptyCols[x]) {
					if (firstCol == -1) {
						firstCol = x;
					}
					lastCol = x;
				}
			}

			if (firstRow == -1) {
				return new ArrayList<>();
			}

			// Создаем обрезанную матрицу
			List<List> result = new ArrayList<>();
			for (int y = firstRow; y <= lastRow; y++) {
				List row = ll.get(y);
				List newRow = new ArrayList<>();
				for (int x = firstCol; x <= lastCol; x++) {
					newRow.add(row.get(x));
				}
				result.add(newRow);
			}

			return result;
		}
	}

	private static class TrimAllMutate {
		private static void trimAll(List<List> ll) {
			if (ll == null || ll.isEmpty()) {
				return;
			}

			// Удаляем пустые строки в начале
			while (!ll.isEmpty() && isEmptyRow(ll.get(0))) {
				ll.remove(0);
			}

			// Удаляем пустые строки в конце
			while (!ll.isEmpty() && isEmptyRow(ll.get(ll.size() - 1))) {
				ll.remove(ll.size() - 1);
			}

			if (ll.isEmpty()) {
				return;
			}

			// Удаляем пустые колонки в начале
			while (!isEmptyColumn(ll, 0)) {
				for (List row : ll) {
					row.remove(0);
				}
			}

			// Удаляем пустые колонки в конце
			int lastCol = ll.get(0).size() - 1;
			while (lastCol >= 0 && isEmptyColumn(ll, lastCol)) {
				for (List row : ll) {
					row.remove(lastCol);
				}
				lastCol--;
			}
		}

		private static boolean isEmptyRow(List row) {
			for (Object obj : row) {
				if (X.notEmptyObj_Str(obj)) {
					return false;
				}
			}
			return true;
		}

		private static boolean isEmptyColumn(List<List> ll, int colIndex) {
			for (List row : ll) {
				if (colIndex < row.size() && X.notEmptyObj_Str(row.get(colIndex))) {
					return false;
				}
			}
			return true;
		}
	}

	public static List<List> trimAll(List<List> ll, Integer[] start, Integer[] end) {
		int startRow = start[1];
		int endRow = end[1];
		int startCol = start[0];
		int endCol = end[0];
		List<List> result = new ArrayList<>();
		for (int y = startRow; y <= endRow; y++) {
			List originalRow = ll.get(y);
			List newRow = new ArrayList<>();
			for (int x = startCol; x <= endCol; x++) {
				Object value = originalRow.get(x);
				newRow.add(value);
			}
			if (!X.emptyAllObjStr(newRow)) {
				result.add(newRow);
			}
		}

		Boolean hasSpace = Space.Cause.hasSpaceX(result, result.get(0).size() - 1, result.size() - 1);
		if (hasSpace) {
			result = trimLastColumn(result);
		}
		return result;
	}

	public static List<List> trimLastColumn(List<List> ll) {
		if (ll == null || ll.isEmpty()) {
			return new ArrayList<>();
		}
		List<List> result = new ArrayList<>();
		for (int y = 0; y < ll.size(); y++) {
			List originalRow = (List) ll.get(y);
			List newRow = new ArrayList<>();
			for (int x = 0; x < originalRow.size() - 1; x++) {
				newRow.add(originalRow.get(x));
			}
			result.add(newRow);
		}
		return result;
	}

	//
	//

	public static class Line {
		public static boolean isEmptyRow(List row) {
			for (Object obj : row) {
				if (X.notEmptyObj_Str(obj)) {
					return false;
				}
			}
			return true;
		}

		public static boolean isEmptyColumn(List<List> ll, int colIndex) {
			for (List row : ll) {
				if (colIndex < row.size() && X.notEmptyObj_Str(row.get(colIndex))) {
					return false;
				}
			}
			return true;
		}
	}
}
