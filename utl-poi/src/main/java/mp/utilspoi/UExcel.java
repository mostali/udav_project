package mp.utilspoi;

import lombok.SneakyThrows;
import mpu.core.ARRi;
import mpu.IT;
import mpu.core.ENUM;
import mpc.exception.FIllegalArgumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

/**
 * @author dav 27.01.2021
 */
public class UExcel {

	public static String getCellValueString(Workbook book, int sheetIndex, int rowIndex, int colIndex) {
		return getCell(book, sheetIndex, rowIndex, colIndex).getStringCellValue();
	}

	public static Cell getCell(Workbook book, int sheetIndex, int rowIndex, int colIndex) {
		return book.getSheetAt(sheetIndex).getRow(rowIndex).getCell(colIndex);
	}

	public static Workbook createWorkbook(InputStream stream) throws IOException, InvalidFormatException {
		return WorkbookFactory.create(stream);
	}

	@SneakyThrows
	public static Workbook readWorkBook(Path file) {
		FileInputStream fis = new FileInputStream(IT.isFileExist(file).toFile());
		Workbook workbook = null;
		String filename = file.getFileName().toString().toLowerCase();
		if (filename.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(fis);
		} else if (filename.endsWith("xls")) {
			workbook = new HSSFWorkbook(fis);
		} else {
			throw new FIllegalArgumentException("What is Excel file '%s'", file);
		}
		return workbook;
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
