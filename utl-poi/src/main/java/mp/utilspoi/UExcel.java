package mp.utilspoi;

import mpu.X;
import mpu.core.ARRi;
import mpu.core.ENUM;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author dav 27.01.2021
 */
public class UExcel {

	public static void main(String[] args) {
//		List<List> ll = Wb0.ll();
//		Integer[] emptyPoint = UT.findEmptyPoint(ll);
//		X.exit(emptyPoint);
//		TableBlockSplitter coorBlocks = new TableBlockSplitter(ll);
//		Iterator<TableBlockSplitter.CoorBlock> iterator = coorBlocks.iterator();
//		while (iterator.hasNext()) {
//			TableBlockSplitter.CoorBlock next = iterator.next();
//			X.p(next);
//		}

		X.exit();
//		Row row = sheetAt.getRow(0);
//		Iterator<Row> iterator = sheetAt.iterator();
//		while (iterator.hasNext()) {
//			Row next = iterator.next();
//			Iterator<Cell> obj = next.cellIterator();
//			while (obj.hasNext()) {
//				Cell c = obj.next();
//				X.p(c.getStringCellValue());
//			}
//		}
//		X.exit(row);
	}


//	public static void findEmptyX(List<List> ll, int x) {
//		for (int i = 0; i < ll.size(); i++) {
//			List row = ll.get(i);
//			Object first = ARRi.first(row, x, null);
//			if(first==null)
//				continue;
//		}
//
//	}

//	public static class Digger {
//		final List<List> ll;
//		final int x;
//		final int y;
//
//		public Digger(List<List> ll) {
//			this(0, 0, ll);
//		}
//
//		public Digger(int x, int y, List<List> ll) {
//			this.ll = ll;
//			this.x = x;
//			this.y = y;
//		}
//
//		void findX() {
//
//		}

	/// /		public Digger hasSpaces() {
	/// /			List rowY = ARRi.item(ll, y + 1);
	/// /
	/// /			Object val = ARRi.item(rowY, x + 1, null);
	/// /			boolean bordX = val == null;
	/// /
	/// /
	/// /			return x < list;
	/// /		}
	/// /
	/// /		public Digger next() {
	/// /			for (int xN = x; xN < ll.size(); xN++) {
	/// /				for (int yN = y; yN < ll.size(); yN++) {
	/// /
	/// /				}
	/// /			}
	/// /			return x;
	/// /		}
//	}

	static class UT {

		public static Integer[] findEmptyPoint(List<List> ll, int checkX) {
			int maxY = ll.size();
			List first = ARRi.first(ll, null);
			if (first == null) {
				return null;
			}
			Integer maxX = first.size() - 1;

			for (int y = 0; y < maxY; y++) {
				for (int x = 0; x < maxX; x++) {
					if (UT.isEmptyX(ll, y, x) && UT.isEmptyY(ll, x, y)) {
						return new Integer[]{y, x};
					}
				}
			}

			return new Integer[]{-1, -1};
		}

		public static boolean isEmptyX(List<List> ll, int fromY, int maxX) {
			maxX = maxX < 0 ? Integer.MAX_VALUE : maxX;

			List rowY = ARRi.first(ll, fromY, null);
			if (rowY == null) {
				return false;
			}
			for (int x = 0; x <= maxX; x++) {
				if (x == rowY.size() - 1) {
					return true;
				}
				X.p("CheckX:" + fromY + "x" + x);
				Object cell = ARRi.first(rowY, x, null);
				if (X.notEmptyObj_Str(cell)) {
					return false;
				}
			}
			return true;
		}

		public static boolean isEmptyY(List<List> ll, int fromX, int maxY) {
			maxY = maxY < 0 ? Integer.MAX_VALUE : maxY;
			for (int y = 0; y <= maxY; y++) {
				if (y == ll.size() - 1) {
					return true;
				}
				List rowY = ll.get(y);
				Object cell = ARRi.first(rowY, fromX, null);
				X.p("CheckY:" + y + "x" + fromX);
				if (X.notEmptyObj_Str(cell)) {
					return false;
				}
			}
			return true;
		}
	}


	public static String getCellValueString(Workbook book, int sheetIndex, int rowIndex, int colIndex) {
		return getCell(book, sheetIndex, rowIndex, colIndex).getStringCellValue();
	}

	public static Cell getCell(Workbook book, int sheetIndex, int rowIndex, int colIndex) {
		return book.getSheetAt(sheetIndex).getRow(rowIndex).getCell(colIndex);
	}

	public static Workbook createWorkbook(InputStream stream) throws IOException, InvalidFormatException {
		return WorkbookFactory.create(stream);
	}

	public static class WorkbookExtractor {
		private final Workbook workbook;

		public WorkbookExtractor(Workbook book) {
			this.workbook = book;
		}

		public String extract_string(int sheetIndex, int rowIndex, int colIndex) {
			return getCell(workbook, sheetIndex, rowIndex, colIndex).getStringCellValue();
		}

		public Date extract_date(int sheetIndex, int rowIndex, int colIndex) {
			return getCell(workbook, sheetIndex, rowIndex, colIndex).getDateCellValue();
		}

		public double extract_numeric(int sheetIndex, int rowIndex, int colIndex) {
			return getCell(workbook, sheetIndex, rowIndex, colIndex).getNumericCellValue();
		}

		public boolean extract_boolean(int sheetIndex, int rowIndex, int colIndex) {
			return getCell(workbook, sheetIndex, rowIndex, colIndex).getBooleanCellValue();
		}

		@Deprecated
		public List<Map<String, Object>> extract_rows(int sheetIndex, int startRow, boolean asStrings, boolean stopWhenEmptyFirstCol, String[][] mappings) {

			List<Map<String, Object>> rows = new ArrayList<>();

			Sheet sheet = workbook.getSheetAt(sheetIndex);

			for (int r = startRow; r <= sheet.getLastRowNum(); r++) {

				Row row = sheet.getRow(r);

				Map<String, Object> rowMap = createSingleRowMap(row, asStrings, stopWhenEmptyFirstCol, mappings);
				if (rowMap == null && stopWhenEmptyFirstCol) {
					return rows;
				} else {
					rows.add(rowMap);
				}

			}
			return rows;
		}

		@Deprecated
		public Map<String, Object> createSingleRowMap(Row row, boolean asString, boolean stopWhenEmptyFirstCol, String[][] mappings) {//String[] strings, String[] numeric, String[] dates

			Map<String, Object> rowMap = new LinkedHashMap<>();

			int lev = 0;

			for (CellType emf : CellType.values()) {

				String[] mapping = ARRi.item(mappings, ENUM.indexOf(emf));

				if (mapping == null) {
					continue;
				}
				for (int iPosField = lev; iPosField < mapping.length; iPosField++) {
					String fieldPath = ARRi.item(mapping, iPosField);
					if (fieldPath == null) {
						continue;
					}
					Object val = null;
					switch (emf) {
						case STRING:
							String vals = row.getCell(iPosField).getStringCellValue();
							if (vals != null && vals.isEmpty()) {
								val = null;
							}
							break;
						case DATE:
							val = asString ? row.getCell(iPosField).getStringCellValue() : row.getCell(iPosField).getDateCellValue();
							break;
						case DOUBLE:
							val = asString ? row.getCell(iPosField).getStringCellValue() : row.getCell(iPosField).getNumericCellValue();
							break;
						case BOOLEAN:
							val = asString ? row.getCell(iPosField).getStringCellValue() : row.getCell(iPosField).getBooleanCellValue();
							break;
						default:
							throw new IllegalStateException("ni");
					}
					if (val == null && stopWhenEmptyFirstCol) {
						return null;
					}
					rowMap.put(fieldPath, val);
					++lev;
				}
			}
			return rowMap;
		}
	}

	public enum CellType {
		STRING, DOUBLE, BOOLEAN, DATE;
	}
}
