//package mp.utilspoi;
//
//import java.util.List;
//
//public class TableUtils {
//
//	/**
//	 * Проверяет, что все строки в указанном диапазоне (по вертикали) пустые
//	 *
//	 * @param table таблица List<List>
//	 * @param offset начальная строка (включительно)
//	 * @param maxOffset конечная строка (включительно)
//	 * @return true если все строки в диапазоне пустые, иначе false
//	 */
//	public static boolean isEmptyY(List<List<String>> table, Integer offset, Integer maxOffset) {
//		if (table == null || offset == null || maxOffset == null) {
//			return true;
//		}
//
//		if (offset < 0 || maxOffset >= table.size() || offset > maxOffset) {
//			return true;
//		}
//
//		for (int row = offset; row <= maxOffset; row++) {
//			List<Object> currentRow = TableAxisChecker.get(row);
//			if (!isEmptyRow(currentRow)) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//	/**
//	 * Проверяет, что все столбцы в указанном диапазоне (по горизонтали) пустые
//	 *
//	 * @param table таблица List<List>
//	 * @param offset начальный столбец (включительно)
//	 * @param maxOffset конечный столбец (включительно)
//	 * @return true если все ячейки в указанных столбцах всех строк пустые, иначе false
//	 */
//	public static boolean isEmptyX(List<List<Object>> table, Integer offset, Integer maxOffset) {
//		if (table == null || offset == null || maxOffset == null) {
//			return true;
//		}
//
//		if (offset < 0 || offset > maxOffset) {
//			return true;
//		}
//
//		for (List<Object> row : table) {
//			for (int col = offset; col <= maxOffset; col++) {
//				if (col < row.size()) {
//					Object cell = row.get(col);
//					if (!isEmptyCell(cell)) {
//						return false;
//					}
//				}
//			}
//		}
//
//		return true;
//	}
//
//	/**
//	 * Проверяет, пустая ли строка
//	 */
//	private static boolean isEmptyRow(List<Object> row) {
//		if (row == null) {
//			return true;
//		}
//
//		for (Object cell : row) {
//			if (!isEmptyCell(cell)) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//	/**
//	 * Проверяет, пустая ли ячейка
//	 */
//	private static boolean isEmptyCell(Object cell) {
//		if (cell == null) {
//			return true;
//		}
//
//		if (cell instanceof String) {
//			return ((String) cell).trim().isEmpty();
//		}
//
//		return false;
//	}
//
//	/**
//	 * Перегруженный метод для работы с List<List<String>>
//	 */
//	public static boolean isEmptyY(List<List<String>> table, Integer offset, Integer maxOffset) {
//		if (table == null || offset == null || maxOffset == null) {
//			return true;
//		}
//
//		if (offset < 0 || maxOffset >= table.size() || offset > maxOffset) {
//			return true;
//		}
//
//		for (int row = offset; row <= maxOffset; row++) {
//			List<String> currentRow = table.get(row);
//			if (!isEmptyRow(currentRow)) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//	/**
//	 * Перегруженный метод для работы с List<List<String>>
//	 */
//	public static boolean isEmptyX(List<List<String>> table, Integer offset, Integer maxOffset) {
//		if (table == null || offset == null || maxOffset == null) {
//			return true;
//		}
//
//		if (offset < 0 || offset > maxOffset) {
//			return true;
//		}
//
//		for (List<String> row : table) {
//			for (int col = offset; col <= maxOffset; col++) {
//				if (col < row.size()) {
//					String cell = row.get(col);
//					if (cell != null && !cell.trim().isEmpty()) {
//						return false;
//					}
//				}
//			}
//		}
//
//		return true;
//	}
//
//	/**
//	 * Проверяет, пустая ли строка для String
//	 */
//	private static boolean isEmptyRow(List<String> row) {
//		if (row == null) {
//			return true;
//		}
//
//		for (String cell : row) {
//			if (cell != null && !cell.trim().isEmpty()) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//	/**
//	 * Пример использования
//	 */
//	public static void main(String[] args) {
//		// Создаем тестовую таблицу
//		List<List<Object>> table = new java.util.ArrayList<>();
//
//		// Строка 0: пустая
//		table.add(createRow("", "", "", ""));
//
//		// Строка 1: с данными
//		table.add(createRow("", "data", "", ""));
//
//		// Строка 2: пустая
//		table.add(createRow("", "", "", ""));
//
//		// Строка 3: пустая
//		table.add(createRow("", "", "", ""));
//
//		System.out.println("=== ТЕСТИРОВАНИЕ isEmptyY ===");
//
//		// Проверка строк 0-0 (только строка 0)
//		boolean result1 = isEmptyY(table, 0, 0);
//		System.out.println("Строки 0-0 пустые? " + result1); // true
//
//		// Проверка строк 1-1 (только строка 1)
//		boolean result2 = isEmptyY(table, 1, 1);
//		System.out.println("Строки 1-1 пустые? " + result2); // false
//
//		// Проверка строк 2-3 (строки 2 и 3)
//		boolean result3 = isEmptyY(table, 2, 3);
//		System.out.println("Строки 2-3 пустые? " + result3); // true
//
//		// Проверка строк 0-2
//		boolean result4 = isEmptyY(table, 0, 2);
//		System.out.println("Строки 0-2 пустые? " + result4); // false
//
//		System.out.println("\n=== ТЕСТИРОВАНИЕ isEmptyX ===");
//
//		// Проверка столбцов 0-0 (только первый столбец)
//		boolean result5 = isEmptyX(table, 0, 0);
//		System.out.println("Столбцы 0-0 пустые? " + result5); // true
//
//		// Проверка столбцов 1-1 (второй столбец)
//		boolean result6 = isEmptyX(table, 1, 1);
//		System.out.println("Столбцы 1-1 пустые? " + result6); // false (есть "data")
//
//		// Проверка столбцов 2-3 (третий и четвертый)
//		boolean result7 = isEmptyX(table, 2, 3);
//		System.out.println("Столбцы 2-3 пустые? " + result7); // true
//
//		System.out.println("\n=== ТЕСТИРОВАНИЕ С NULL И ГРАНИЦАМИ ===");
//
//		// Проверка с null параметрами
//		boolean result8 = isEmptyY(table, null, 0);
//		System.out.println("Offset null: " + result8); // true
//
//		// Проверка с выходом за границы
//		boolean result9 = isEmptyY(table, 0, 100);
//		System.out.println("maxOffset за границей: " + result9); // true
//	}
//
//	/**
//	 * Вспомогательный метод для создания строки
//	 */
//	private static List<Object> createRow(Object... cells) {
//		List<Object> row = new java.util.ArrayList<>();
//		for (Object cell : cells) {
//			row.add(cell);
//		}
//		return row;
//	}
//}