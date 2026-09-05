//package mp.utilspoi;
//
//
//import mpu.ARG;
//import mpu.U;
//import mp.utils.exception.WhatIsTypeException;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellType;
//import mp.utils.abstype.AbsType;
//import mp.utils.ftypes.types.NF202BigDecimal;
//import mp.utils.ftypes.types.NInt;
//import mp.utils.ftypes.types.ZF202BigDecimal;
//import mp.utils.ftypes.types.ZInt;
//import mp.utils.ftypes.types.base.FBigDecimal;
//import mp.utils.ftypes.types.base.FDate;
//import mp.utils.ftypes.types.base.FString;
//import mp.utils.refl.UNRefl;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.Date;
//import java.util.Map;
//
///**
// * @author dav 03.02.2021
// */
//public class TypedCellValue<T> extends AbsType<T> {
//
//	public TypedCellValue(String name, T value, Class<T> type) {
//		super(name, value, type);
//	}
//
//	//
//	//
//	//
//	public Object getCellValue() {
//		Object val = super.getValue();
//		if (val == null) {
//			return null;
//		}
//		if (val instanceof FString) {
//			return ((FString) val).toString();
//		} else if (val instanceof FBigDecimal) {
//			return ((FBigDecimal) val).toBigDecimal();
//		}
//		return val;
//	}
//
//	//
//	//
//	//
//	public static <T> TypedCellValue<T> getTypedCellValue(Cell cell, Class<T> type, boolean returnNullIfHappensError) {
//		return new TypedCellValue(cell.getAddress().toString(), getCellValueAs(cell, type, returnNullIfHappensError), type);
//	}
//
//	public static <T> TypedCellValue<T> getTypedCellValueRequired(Cell cell, Class<T> type) {
//		return new TypedCellValue(cell.getAddress().toString(), getCellValueAs(cell, type, false, true), type);
//	}
//
//	//
//	//
//	//
//	private static <T> T getCellValueAs(Cell cell, Class<T> type, boolean returnNullIfHappensError) {
//		return getCellValueAs(cell, type, returnNullIfHappensError, false);
//	}
//
//
//	/**
//	 * Метод можно разделить по возвращаемым типам. Но оставил целым для наглядности и типовых правок в одном месте.
//	 */
//	private static <T> T getCellValueAs(Cell cell, Class<T> type, boolean returnNullIfHappensError, boolean requiredSomeOne) {
//		try {
//
//			String error_typename = "undefined";
//
//			/**
//			 *  BLANK to
//			 */
//			if (cell.getCellTypeEnum() == CellType.BLANK) {
//
//				if (String.class.isAssignableFrom(type)) {
//					return (T) "";
//				} else if (FString.class.isAssignableFrom(type)) {
//					return (T) UNRefl.inst(type, String.class, null).toString();
//				} else if (ZF202BigDecimal.class.isAssignableFrom(type)) {
//					return (T) new ZF202BigDecimal(0);
//				} else if (NF202BigDecimal.class.isAssignableFrom(type)) {
//					return null;
//				} else if (ZInt.class.isAssignableFrom(type)) {
//					return (T) (Integer) 0;
//				} else if (NInt.class.isAssignableFrom(type)) {
//					return null;
//				}
//
//				error_typename = "blank";
//
//			}
//
//			/**
//			 * Need STRING
//			 */
//			else if (String.class.isAssignableFrom(type)) {
//
//				try {
//					return (T) getCellValueAsStringRequired(cell);
//				} catch (NullCellValueException ex) {
//					if (returnNullIfHappensError && !requiredSomeOne) {
//						return null;
//					}
//					throw ex;
//				}
//
//			}
//
//			/**
//			 * Need Formatted STRING
//			 */
//			else if (FString.class.isAssignableFrom(type)) {
//
//				switch (cell.getCellTypeEnum()) {
//					case NUMERIC:
//						return (T) UNRefl.inst(type, Number.class, cell.getNumericCellValue());
//					case STRING:
//						return (T) UNRefl.inst(type, String.class, cell.getStringCellValue());
//				}
//
//				error_typename = "fstring";
//
//			}
//
//			/**
//			 * Need NUMBER
//			 */
//			else if (Number.class.isAssignableFrom(type)) {
//
//				switch (cell.getCellTypeEnum()) {
//					case NUMERIC:
//						double number = cell.getNumericCellValue();
//						if (Integer.class.isAssignableFrom(type)) {
//							return (T) (Integer) (int) number;
//						} else if (Long.class.isAssignableFrom(type)) {
//							return (T) (Long) (long) number;
//						} else if (FBigDecimal.class.isAssignableFrom(type)) {
//							return (T) UNRefl.inst(type, double.class, number);
//						} else if (BigDecimal.class.isAssignableFrom(type)) {
//							return (T) new BigDecimal(number);
//						} else {
//							break;
//						}
//					case STRING:
//						return (T) UNRefl.inst(type, String.class, cell.getStringCellValue());
//				}
//
//				error_typename = "number";
//
//			}
//
//			/**
//			 * Need DATE
//			 */
//			else if (Date.class.isAssignableFrom(type)) {
//
//				switch (cell.getCellTypeEnum()) {
//					case STRING: {
//						if (FDate.class.isAssignableFrom(type)) {
//							return (T) UNRefl.inst(type, String.class, cell.getStringCellValue());
//						}
//						break;
//					}
//					default: {
//						Date date = cell.getDateCellValue();
//						if (date != null) {
//							if (FDate.class.isAssignableFrom(type)) {
//								return (T) UNRefl.inst(type, Date.class, date);
//							}
//							return (T) date;
//						}
//					}
//				}
//
//				error_typename = "date";
//
//			}
//
//			/**
//			 * Need BOOLEAN
//			 */
//			else if (Boolean.class.isAssignableFrom(type)) {
//
//				switch (cell.getCellTypeEnum()) {
//					case BOOLEAN: {
//						return (T) (Boolean) cell.getBooleanCellValue();
//					}
//					case STRING: {
//						Boolean bool = "true".equalsIgnoreCase(cell.getStringCellValue()) ? true : "false".equalsIgnoreCase(cell.getStringCellValue()) ? false : null;
//						if (bool != null) {
//							return (T) bool;
//						}
//						break;
//					}
//				}
//
//				error_typename = "boolean";
//
//			}
//
//			if (returnNullIfHappensError && !requiredSomeOne) {
//				return null;
//			}
//			throw new IllegalArgumentException(U.f("Illegal type '%s' is required some one: %s , cell:%s", error_typename, type.getName(), cell.getCellTypeEnum()));
//
//		} catch (Exception e) {
//			if (returnNullIfHappensError && !requiredSomeOne) {
//				return null;
//			}
//			throw e;
//		}
//
//	}
//
//	public static class NullCellValueException extends NullPointerException {
//	}
//
//	public static String getCellValueAsStringRequired(Cell cell, Map<Enum, Class>... mappingTypes) {
//		String val = getCellValueAsString(cell, mappingTypes);
//		if (val == null) {
//			throw new NullCellValueException();
//		}
//		return val;
//	}
//
//	public static String getCellValueAsString(Cell cell, Map<Enum, Class>... mappingTypes) {
//		Object val = getCellValue(cell, mappingTypes);
//		return val == null ? null : val instanceof String ? (String) val : val.toString();
//	}
//
//	public static Object getCellValueRequired(Cell cell, Map<Enum, Class>... mappingTypes) {
//		Object val = getCellValue(cell, mappingTypes);
//		if (val == null) {
//			throw new NullCellValueException();
//		}
//		return val;
//	}
//
//	public static Object getCellValue(Cell cell, Map<Enum, Class>... mappingTypes) {
//		if (cell == null) {
//			return null;
//		}
//		switch (cell.getCellTypeEnum()) {
//			case STRING:
//				return cell.getStringCellValue();
//			case NUMERIC:
//				double num = cell.getNumericCellValue();
//				Map<Enum, Class> types = ARG.toPredicatDefQk(mappingTypes);
//				Class type = types.get(cell.getCellTypeEnum());
//				if (type == null) {
//					return num;
//				} else if (type == BigDecimal.class) {
//					BigDecimal numBD = new BigDecimal(num);
//					numBD.setScale(2, RoundingMode.HALF_UP);
//					return numBD;
//				} else {
//					throw new WhatIsTypeException(type);
//				}
//			case BLANK:
//				return "";
//			case BOOLEAN:
//				return cell.getBooleanCellValue();
//			case FORMULA:
//				return cell.getCellFormula();
//			case ERROR:
//				return cell.getErrorCellValue();
//			default:
//				if (cell.getDateCellValue() != null) {
//					return cell.getDateCellValue();
//				}
//				break;
//		}
//		return null;
//	}
//}
