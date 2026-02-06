package mpu.str;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mpc.html.UXPath;
import mpc.types.ruprops.RuProps;
import mpe.core.P;
import mpe.str.URx;
import mpu.IT;
import mpu.core.*;
import mpc.exception.RequiredRuntimeException;
import mpc.json.GsonMap;
import mpc.json.UGson;
import mpc.types.unesc_types.EscHtml;
import mpc.types.unesc_types.UnescHtml;
import mpc.fs.fd.FILE;
import mpc.rfl.RFL;
import mpu.X;
import org.w3c.dom.Document;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

//Конвертит строки в типы
//String Utils - TypeConverter
public class UST {

	public static void main(String[] args) {
		UST.INT(" 7");
		UST.BD("asd");
		String str = RW.readString(Paths.get("/home/dav/pjm/pt.sql"));
		List<String> allGroup = URx.findAllGroup(str, URx.PT_PLH_DOUBLE_ROUND);
		P.exit(allGroup);
	}

	public static UUID UUID(String s, UUID... defRq) {
		try {
			return UUID.fromString(s);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("Wrong UUID from string [%s]", s);
		}
	}

	public static <N extends Number & Comparable> N NUM(String str, String pfx, N[] minMax, N... defRq) {
		try {
			String arg = STR.removeStartsWith(str, pfx);
			N val = (N) UST.NUM(arg, minMax.getClass().getComponentType());
			return X.empty(minMax) ? val : IT.isMinMax(val, minMax);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static Integer INT(String s, Integer... defRq) {
		try {
			return Integer.parseInt(s);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Wrong INTEGER from string '%s'", s), defRq);
		}
	}

	public static Double DBL(String s, Double... defRq) {
		try {
			return Double.parseDouble(s);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("Wrong DOUBLE from string '%s'", s);
		}
	}

	public static Float FLOAT(String s, Float... defRq) {
		try {
			return Float.parseFloat(s);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("Wrong FLOAT from string '%s'", s);
		}
	}

	public static Short SHORT(String s, Short... defRq) {
		try {
			return Short.parseShort(s);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("Wrong SHORT from string '%s'", s);
		}
	}

	public static Byte BYTE(String s, Byte... defRq) {
		try {
			return Byte.parseByte(s);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("Wrong BYTE from string '%s'", s);
		}
	}

	public static Long LONG(String s, Long... defRq) {
		try {
			return Long.parseLong(s);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("Wrong LONG from string '%s'", s);
		}
	}

	public static Boolean BOOL(String str, Boolean... defRq) {
		if (str != null) {
			if ("true".equals(str)) {
				return true;
			} else if ("false".equals(str)) {
				return false;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Wrong BOOLEAN from string '%s'", str);
	}

	public static BigDecimal BD(String s, BigDecimal... defRq) {
		try {
			return new BigDecimal(s);
		} catch (NumberFormatException ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("Wrong BigDecimal from string '%s'", s);
		}
	}

	public static BigInteger BI(String s, BigInteger... defRq) {
		try {
			return new BigInteger(s);
		} catch (NumberFormatException ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException("Wrong BigInteger from string '%s'", s);
		}
	}

	public static Integer INTany(String s, Integer... def) {
		try {
			return Integer.valueOf(s);
		} catch (Exception ex) {
			try {
				Double i = Double.valueOf(s);
				return i.intValue();
			} catch (Exception ex2) {
				if (ARG.isDef(def)) {
					return ARG.toDef(def);
				}
				throw new RequiredRuntimeException("Wrong INT(any) from string '%s'", s);
			}
		}
	}

	//
	//
	//

	public static <T> T strToExt(CharSequence seq, Class<T> clazz, T... defRq) {
		return strToExt(seq, null, clazz, defRq);
	}

	public static <T> T strToExt(CharSequence seq, String checkMethod_StrTo, Class<T> clazz, T... defRq) {
		T t = strTo(seq, clazz, null);
		if (t != null) {
			return t;
		}
		String str = seq instanceof String ? (String) seq : seq.toString();
		Throwable err_;
		try {

			if (clazz == EscHtml.class) {
				return (T) EscHtml.of(str);
			} else if (clazz == UnescHtml.class) {
				return (T) UnescHtml.of(str);
			}

			outCheckMethod:
			if (checkMethod_StrTo != null) {
				Method strTo = RFL.method(clazz, checkMethod_StrTo, new Class[]{String.class}, true, true, true, null);
				if (strTo == null || !clazz.isAssignableFrom(strTo.getReturnType())) {
					break outCheckMethod;
				}
				return (T) strTo.invoke(null, seq);
			}

			return t;

		} catch (Throwable err) {
			err_ = err;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		String msg = "Wrong string '" + seq + "' for converting to type(ext) [" + clazz + "]";
		RequiredRuntimeException e = err_ == null ? new RequiredRuntimeException(msg) : new RequiredRuntimeException(err_, msg);
		throw e;
	}

	public static <T> T strTo(CharSequence seq, Class<T> clazz, T... defRq) {
//		return strTo(seq, null, clazz, defRq);
//	}
//
//	public static <T> T strTo(CharSequence seq, String checkMethod_StrTo, Class<T> clazz, T... defRq) {
		Throwable err_ = null;
		try {
			String str = seq instanceof String ? (String) seq : seq.toString();
			if (clazz == String.class) {
				return (T) str;
			} else if (clazz == Boolean.class) {
				return clazz.cast(BOOL(str));
			} else if (clazz == Path.class) {
				return (T) Paths.get(seq.toString());
			} else if (clazz.isEnum()) {
				return (T) ENUM.valueOf(seq.toString(), (Class<? extends Enum>) clazz);
			} else if (clazz == URL.class) {
				return (T) UST.URL(str);
			} else if (clazz == File.class) {
				return (T) new File(seq.toString());
			} else if (clazz == FILE.class) {
				return (T) FILE.of(seq.toString());
			} else if (clazz == UUID.class) {
				return clazz.cast(UUID(str));
			} else if (clazz == JsonObject.class) {
				return (T) UGson.JO(str);
			} else if (clazz == GsonMap.class) {
				return (T) GsonMap.of(UGson.toMapFromString(str));
			} else if (clazz == RuProps.class) {
				return (T) RuProps.of(seq.toString());
			} else if (clazz == List.class) {
				return str.isEmpty() ? (T) Collections.EMPTY_LIST : (T) SPLIT.allByNL(str);
			}

			T type = NUM(str, clazz, null);
			if (type != null) {
				return type;
			}

		} catch (Throwable err) {
			err_ = err;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		String msg = "Wrong string '" + seq + "' for converting to type [" + clazz + "]";
		RequiredRuntimeException e = err_ == null ? new RequiredRuntimeException(msg) : new RequiredRuntimeException(err_, msg);
		throw e;
	}

	public static <T> T NUM(String str, Class<T> clazz, T... defRq) {
		Throwable err_ = null;
		try {
			if (clazz == Integer.class) {
				return clazz.cast(INT(str));
			} else if (clazz == Long.class) {
				return clazz.cast(LONG(str));
			} else if (clazz == Double.class) {
				return clazz.cast(DBL(str));
			} else if (clazz == BigDecimal.class) {
				return clazz.cast(BD(str));
			} else if (clazz == Short.class) {
				return clazz.cast(SHORT(str));
			} else if (clazz == Byte.class) {
				return clazz.cast(BYTE(str));
			}
		} catch (Throwable err) {
			err_ = err;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		String msg = "Wrong Number Value [" + str + "] for type [" + clazz + "]";
		RequiredRuntimeException e = err_ == null ? new RequiredRuntimeException(msg) : new RequiredRuntimeException(err_, msg);
		throw e;
	}

	public static Class load_class_by_name(String className, boolean lang, boolean math, Class... defRq) {
		Class classType = RFL.clazz(className, null);
		if (classType == null && lang) {
			classType = RFL.clazz("java.lang." + className, null);
		}
		if (classType == null && math) {
			classType = RFL.clazz("java.math." + className, null);
		}
		if (classType != null) {
			return classType;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(classType);
		}
		throw new RequiredRuntimeException("load_class:" + className);
	}

	public static boolean isLong(Long[] minMax, String... n) {
		IT.notEmpty(n);
		IT.isLength(minMax, 2);
		for (String s : n) {
			try {
				long num = Long.parseLong(s);
				if (num >= minMax[0] && num <= minMax[1]) {
					return true;
				}
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return true;
	}

	public static boolean isByte(String... n) {
		IT.notEmpty(n);
		for (String s : n) {
			try {
				Byte.parseByte(s);
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return true;
	}

	public static boolean isShort(String... n) {
		IT.notEmpty(n);
		for (String s : n) {
			try {
				Short.parseShort(s);
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return true;
	}

	public static boolean isInt(String... n) {
		IT.notEmpty(n);
		for (String s : n) {
			try {
				Integer.parseInt(s);
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumber(String number) {
		return UST.INTany(number, null) != null;
	}

	//
	//
	//

	public static URL URL(String url, URL... defRq) {
		try {
			return new URL(url);
		} catch (MalformedURLException ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(ex, "Parse url '%s'", url);
		}
	}

	public static Date DATE(String str, String[] formats, Date... defRq) {
		for (String format : formats) {
			Date date = DATE(str, format, null);
			if (date != null) {
				return date;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Wrong date '%s'. Not found from checked format's '%s'.", str, ARR.of(formats));
	}

	public static Date DATE(String str, String format, Date... defRq) {
		try {
			return new SimpleDateFormat(format).parse(str);
		} catch (ParseException e) {
			return ARG.toDefThrow(new RequiredRuntimeException(e, "Parse date '%s' error with format '%s'", str, format), defRq);
		}
	}

	public static QDate QDATE(String str, String format, QDate... defRq) {
		try {
			return QDate.of(new SimpleDateFormat(format).parse(str));
		} catch (ParseException e) {
			return ARG.toDefThrow(new RequiredRuntimeException(e, "Parse date '%s' error with format '%s'", str, format), defRq);
		}
	}

	public static Path PATH(String str, Path... defRq) {
		try {
			return Paths.get(str);
		} catch (Exception e) {
			return ARG.toDefThrow(new RequiredRuntimeException(e, "Parse path '%s' error.", str), defRq);
		}
	}

	public static GsonMap JSON(String str, GsonMap... defRq) {
		return GsonMap.of(str, defRq);
	}

	public static JsonArray JSONARRAY(String jsonArray, JsonArray... defRq) {
		try {
			return IT.isJsonType(jsonArray, JsonArray.class);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> ex instanceof RequiredRuntimeException ? (RequiredRuntimeException) ex : new RequiredRuntimeException(ex, "Error parse JsonArray with json: %s", jsonArray), defRq);
		}
	}

	public static Document XML_STRICT(CharSequence str, Document... defRq) {
		return UXPath.stringToDocument(str, defRq);
	}

	public static Path FILE(String file) {
		return IT.isFileExist(Paths.get(file));
	}

	public static Path DIR(String file) {
		return IT.isDirExist(Paths.get(file));
	}

	public static boolean isXml(CharSequence str) {
		return UST.XML_STRICT(str, null) != null;
	}

	public static boolean isJson(String str) {
		return UST.JSON(str, null) != null;
	}

	public static boolean isJsonArray(String str) {
		return UST.JSONARRAY(str, null) != null;
	}

	public static boolean isJsonAny(String str) {
		return isJson(str) || isJsonArray(str);
	}
}
