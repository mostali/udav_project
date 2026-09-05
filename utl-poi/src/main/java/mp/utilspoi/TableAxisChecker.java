package mp.utilspoi;
import java.util.List;

public class TableAxisChecker {

	public static void main(String[] args) {

	}
	/**
	 * Проверяет, что все ячейки в указанном диапазоне строк (ось Y) пусты.
	 * Диапазон включителен: [offset, maxOffset].
	 */
	public static boolean isEmptyY(List<List> table, Integer offset, Integer maxOffset) {
		if (table == null || offset == null || maxOffset == null) return true;
		if (offset > maxOffset) return true;

		int height = table.size();
		for (int y = offset; y <= maxOffset && y < height; y++) {
			List row = table.get(y);
			if (row == null) continue;
			for (int i = 0; i < row.size(); i++) {
				if (!isEmpty(row.get(i))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Проверяет, что все ячейки в указанном диапазоне столбцов (ось X) пусты.
	 * Диапазон включителен: [offset, maxOffset].
	 */
	public static boolean isEmptyX(List<List> table, Integer offset, Integer maxOffset) {
		if (table == null || offset == null || maxOffset == null) return true;
		if (offset > maxOffset) return true;

		int height = table.size();
		for (int y = 0; y < height; y++) {
			List row = table.get(y);
			if (row == null) continue;
			for (int x = offset; x <= maxOffset; x++) {
				// Защита от выхода за пределы строки (jagged arrays)
				if (x < row.size() && !isEmpty(row.get(x))) {
					return false;
				}
			}
		}
		return true;
	}

	// Внутренний хелпер для проверки пустоты ячейки
	private static boolean isEmpty(Object val) {
		if (val == null) return true;
		if (val instanceof String) {
			return ((String) val).trim().isEmpty();
		}
		return false;
	}
}