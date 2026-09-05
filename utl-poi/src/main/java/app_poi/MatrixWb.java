package app_poi;

import mpc.exception.WhatIsTypeException;
import mpc.types.abstype.AbsType;
import mpe.str.table.Matrix;
import mpu.IT;
import org.apache.poi.ss.usermodel.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MatrixWb {

	public static List<List> toListList_Complete_String(Path path, int sheetIndex) {
		Workbook sheets = Wb0.of(path);
		List<List> ll = toListList_Complete(sheets, sheetIndex, FillMode.EMPTY_BN);
		return ll;
	}

	public static List<List> toListList_Complete_AT(Path path, int sheetIndex) {
		Workbook sheets = Wb0.of(path);
		List<List> ll = toListList_Complete(sheets, sheetIndex, FillMode.ABS_TYPE);
		return ll;
	}

//	public static List<List> toLL_complete(Path workbook, int sheetIndex) {
//		Workbook wb = Wb0.of(workbook);
//		return readSheetComplete(wb, sheetIndex, "");
//	}

//	public static List<List> toLL_complete(Workbook workbook, int sheetIndex) {
//		List<List> ll0 = readSheetComplete(workbook, sheetIndex, "");

	/// /		return noramalizeCompleteSizeWith(ll0, "");
//		return ll0;
//	}
	public static List<List> toListList_NotCompleteSize(Workbook workbook, int sheetIndex) {
		List<List> matrix = new ArrayList<>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		DataFormatter formatter = new DataFormatter();

		for (Row row : sheet) {
			List<String> rowList = new ArrayList<>();
			for (int cn = 0; cn < row.getLastCellNum(); cn++) {
				Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				String cellVal = formatter.formatCellValue(cell);
				rowList.add(cellVal);
			}
			matrix.add(rowList);
		}

		return matrix;
	}

	private enum FillMode {
		EMPTY_BN, ABS_TYPE
	}

	public static List<List> toListList_Complete(Workbook workbook, int sheetIndex, FillMode fillMode) {
		List<List> matrix = new ArrayList<>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		DataFormatter formatter = new DataFormatter();

		int lastRowNum = sheet.getLastRowNum();

		int maxColumns = 0;
		for (int rn = 0; rn <= lastRowNum; rn++) {
			Row row = sheet.getRow(rn);
			if (row != null) {
				int lastCell = row.getLastCellNum();
				if (lastCell > maxColumns) {
					maxColumns = lastCell;
				}
			}
		}

		for (int rn = 0; rn <= lastRowNum; rn++) {
			Row row = sheet.getRow(rn);
			List rowList = new ArrayList<>();

			if (row == null) {
				for (int cn = 0; cn < maxColumns; cn++) {
					fillRowWithBlank(rowList, fillMode, rn, cn);
				}
				matrix.add(rowList);
				continue;
			}

			for (int cn = 0; cn < maxColumns; cn++) {
				Cell cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
				String val = formatter.formatCellValue(cell);
				IT.NN(val, "Except value for cell %s_%s", rn, cn);
				fillRowWithVal(rowList, fillMode, val, rn, cn);
			}
			matrix.add(rowList);
		}

		return matrix;
	}

	private static void fillRowWithBlank(List rowList, FillMode fillMode, int rn, int cn) {
		switch (fillMode) {
			case EMPTY_BN:
				rowList.add("");
				return;
			case ABS_TYPE:
				rowList.add(AbsType.of(rn + "_" + cn, ""));
				return;
			default:
				throw new WhatIsTypeException(fillMode);
		}
	}

	private static void fillRowWithVal(List rowList, FillMode fillMode, String val, int rn, int cn) {
		switch (fillMode) {
			case EMPTY_BN:
				rowList.add(val);
				return;
			case ABS_TYPE:
				rowList.add(AbsType.of(rn + "_" + cn, val));
				return;
			default:
				throw new WhatIsTypeException(fillMode);
		}
	}

	public static List<List> noramalizeCompleteSizeWith(List<List> matrix, String withEmptyCell) {
		if (matrix == null || matrix.isEmpty()) {
			return matrix;
		}

		int maxLength = Matrix.maxWidth(matrix, 0);

		for (List<String> row : matrix) {
			if (row != null) {
				while (row.size() < maxLength) {
					row.add(withEmptyCell);
				}
			}
		}

		return matrix;
	}

}
