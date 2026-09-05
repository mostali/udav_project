//package mp.utilspoi;
//
//
//import mpu.U;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellType;
//import org.apache.poi.ss.usermodel.Row;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import mp.utils.UArr;
//
//import java.util.List;
//import java.util.Objects;
//
//
///**
// * @author dav 03.02.2021
// */
//public class RowMapper {
//
//	private static final Logger L = LoggerFactory.getLogger(RowMapper.class);
//
//	private final Row row;
//	private final int index;
//
//	public static RowMapper of(Row row) {
//		return new RowMapper(row, -1);
//	}
//
//	public Row getRow() {
//		return row;
//	}
//
//	@Override
//	public String toString() {
//		List<Cell> first10 = UArr.firstCount(row.cellIterator(), 10);
//		StringBuilder sb = new StringBuilder();
//		if (index >= 0) {
//			sb.append(index).append(") ");
//		} else {
//			sb.append(row.getRowNum()).append(")' ");
//		}
//		for (int i = 0; i < first10.size(); i++) {
//			Object cellVal = TypedCellValue.getCellValue(first10.get(i));
//			sb.append(i).append(":").append(cellVal).append("~");
//		}
//		return sb.toString();
//	}
//
//	public RowMapper(Row row, int index) {
//		this.row = row;
//		this.index = index;
//	}
//
//	public RowMapper(Row row) {
//		this(row, -1);
//	}
//
//	public int index() {
//		return index;
//	}
//
//	public boolean isCellStartWith(int cell, String prefix) {
//		try {
//			return row.getCell(cell).getStringCellValue().startsWith(prefix);
//		} catch (Exception ex) {
//			return false;
//		}
//	}
//
//	public boolean isBlankOrNull(int cell) {
//		Cell c = row.getCell(cell);
//		return c == null ? true : c.getCellTypeEnum() == CellType.BLANK;
//	}
//
//	public boolean isCellStartWith(int cell, String prefix, int cell2, String prefix2) {
//		return isCellStartWith(cell, prefix) && isCellStartWith(cell2, prefix2);
//	}
//
//	public boolean isCellEqExtWithTrim(int cell, String value, boolean... ignoreCase) {
//		try {
//			String v = TypedCellValue.getCellValueAsString(row.getCell(cell));
//			return U.equalsUnsafe(v, value, ignoreCase);
//		} catch (Exception ex) {
//			return false;
//		}
//	}
//
//	public boolean isCellEqWithTrim(int cell, String value) {
//		try {
//			return row.getCell(cell).getStringCellValue().trim().equals(value);
//		} catch (Exception ex) {
//			return false;
//		}
//	}
//
//	public boolean isCellEqAnyString(int cellIndex, CharSequence any) {
//		Cell cell = row.getCell(cellIndex);
//		if (cell == null) {
//			return false;
//		}
//		switch (cell.getCellTypeEnum()) {
//			case STRING:
//				return any.equals(cell.getStringCellValue());
//			case NUMERIC:
//				return any.equals(String.valueOf(cell.getNumericCellValue()));
//			case BLANK:
//				return any.length() == 0;
//			case BOOLEAN:
//				return any.equals(String.valueOf(cell.getBooleanCellValue()));
//			case FORMULA:
//				return any.equals(cell.getCellFormula());
//			case ERROR:
//				return any.equals(cell.getErrorCellValue());
//			default:
//				if (cell.getDateCellValue() != null) {
//					return any.equals(cell.getDateCellValue());
//				}
//				return false;
//		}
//	}
//
//	public boolean isCellEqAnyNumber(int cellIndex, Number any) {
//		Cell cell = row.getCell(cellIndex);
//		if (cell == null) {
//			return false;
//		}
//		switch (cell.getCellTypeEnum()) {
//			case STRING:
//				try {
//					return String.valueOf(any).compareTo(cell.getStringCellValue().replace(",", ".")) == 0;
//				} catch (Exception ex) {
//					return false;
//				}
//			case NUMERIC:
//				return ((Double) any.doubleValue()).compareTo(cell.getNumericCellValue()) == 0;
//			default:
//				return false;
//		}
//	}
//
//	public boolean isCellEqNumberOr(int cellIndex, Integer... values) {
//		if (values.length == 0) {
//			return false;
//		}
//		for (Integer num : values) {
//			String numStr = String.valueOf(num);
//			if (isCellEqOr(cellIndex, num, numStr, numStr + ".0")) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public boolean isCellEqOr(int cellIndex, Object... values) {
//		Cell cell = row.getCell(cellIndex);
//		if (cell == null) {
//			return false;
//		}
//		for (Object eqObj : values) {
//			switch (cell.getCellTypeEnum()) {
//				case STRING: {
//					boolean r = eqObj instanceof CharSequence && ((CharSequence) eqObj).equals(cell.getStringCellValue());
//					if (r) {
//						return true;
//					}
//					continue;
//				}
//				case NUMERIC: {
//					boolean r = eqObj instanceof Number && ((Double) cell.getNumericCellValue()).compareTo(((Number) eqObj).doubleValue()) == 0;
//					if (r) {
//						return true;
//					}
//					continue;
//				}
//				case BLANK: {
//					boolean r = eqObj instanceof CharSequence && ((CharSequence) eqObj).length() == 0;
//					if (r) {
//						return true;
//					}
//					continue;
//				}
//				case BOOLEAN: {
//					boolean r = eqObj instanceof Boolean && cell.getBooleanCellValue() == (boolean) eqObj;
//					if (r) {
//						return true;
//					}
//					continue;
//				}
//
//			}
//		}
//		return false;
//
//	}
//
//	public boolean isCellEq(int cell, String value) {
//		return isCellEq(cell, value, String.class);
//	}
//
//	public <T> boolean isCellEq(int cell, T value, Class<T> type) {
//		try {
//			TypedCellValue typeCellValue = TypedCellValue.getTypedCellValue(row.getCell(cell), type, false);
//			return Objects.equals(typeCellValue.getCellValue(), value);
//		} catch (Exception ex) {
//			return false;
//		}
//	}
//
////	public <T> RowMapper fillField(ContentAccessor docOrRow, String fieldPath, Class<T> clazz, int cellIndex, boolean required) {
////		fillField(docOrRow, fieldPath, clazz, row, cellIndex, required);
////		return this;
////	}
////
////	public static <T> void fillField(ContentAccessor docOrRow, String fieldPath, Class<T> clazz, Row row, int cellIndex, boolean required) {
////
////		Cell cell = row.getCell(cellIndex);
////		if (cell == null) {
////			if (!required) {
////				return;
////			}
////			throw new NullPointerException(U.f("Field '%s' from cell=null '%s' is null, row\n%s", fieldPath, cellIndex, row));
////		}
////
////		try {
////			TypedCellValue<T> typedCellValue = TypedCellValue.getTypedCellValue(cell, clazz, false);
////			Object val = typedCellValue.getCellValue();
////			if (val == null && required) {
////				throw new NullPointerException(U.f("Field '%s' from cell '%s' is null, row\n%s", fieldPath, cellIndex, row));
////			}
////			docOrRow.setFieldValue(fieldPath, val);
////		} catch (Exception ex) {
////			if (required) {
////				throw new IllegalStateException(U.f("Field '%s' from cell=ex '%s' is null, row\n%s", fieldPath, cellIndex, row), ex);
////			} else {
////				L.warn(U.fm("E:getCellValue {0} / {1} happens error", fieldPath, cellIndex), ex.getMessage());
////			}
////		}
////
////	}
//
//	public String getCellValueAsString(int cellIndex) {
//		return TypedCellValue.getCellValueAsString(getRow().getCell(cellIndex));
//	}
//
//	public String getCellValueAsStringRequired(int cellIndex) {
//		return TypedCellValue.getCellValueAsStringRequired(getRow().getCell(cellIndex));
//	}
//}
