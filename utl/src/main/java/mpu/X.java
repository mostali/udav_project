package mpu;

import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.path.IPath;
import mpc.json.UGson;
import mpc.map.MAP;
import mpc.str.ObjTo;
import mpe.core.ERR;
import mpe.core.U;
import mpu.core.*;
import mpu.pare.Key;
import mpu.str.*;
import mpv.byteunit.ByteUnit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//
//[X](./src/main/java/mpu/X.java) - Size & Empty & Format & etc
//
//- Базовый утилитарный класс X (т.е. что это?)
//- выполняет 5 ключевых функций:
//		- - size* получение размера объектов
//- - empty* - проверка объектов на пустоту
//- - f/fl/fm - форматирование строк
//- - p - pretty print Object's/Collection's/Map
//- - to* приводит типы (not null & cast)
//- - throw* кидает исключения
//
//```javascript
//X.f("Hello %s", "World")   // String.format
//X.f_("Hello %s", "World")   // безопасный String.format
//X.fm("Hello {0}", "World") //MessageFormat.format
//X.fl("Hello {}", "World")  // Аналог log
//
/// /Возвращаем длину объекта
//X.sizeOf(null) // -1
//X.sizeOf0(null) // 0
//X.sizeOf(object) // size of Collection's, Map , etc
//
/// /Проверяем на пустоту
//X.empty(object) // true/false
//X.emptyAll(o1, o2, ..) // true/false
//X.emptyAny(o1, o2, ..) // true/false
//X.notNullOnlyOne(o1, o2, ..) // true/false
//

/// /печать pretty объектов
//p(ARR.as(1, 2, 3));
//ArrayList*3
//		1
//		2
//		3
//p(ARR.of(1, 2, 3));
//Array*3
//		1
//		2
//		3
//
//p(UMap.of(1, 11, 2, 22, 3, 33));
//HashMap*3
//		1=11
//		2=22
//		3=33
//
//pAsJson(UMap.of(1, 11, 2, 22, 3, 33));
//		{
//		"1": 11,
//		"2": 22,
//		"3": 33
//		}
//
//		```
public class X {

	public static void main(String[] args) {
		IT.state(nullAll(null, null));
		IT.state(nullAll(null, null, 1), "ok");
		Object arr = new int[]{1, 2};


//		List<Integer> integers = ARR.as(new Integer[]{1, 2, 3});
		p(ARR.as(1, 22, 3));
		p(ARR.of(1, 2, 3));
		p(MAP.of(1, 11, 2, 22, 3, 33));
		pAsJson(MAP.of(1, 11, 2, 22, 3, 33));
	}

	public static <T> boolean empty(T[] args) {
		return args == null || args.length == 0;
	}

	public static boolean empty(Map map) {
		return map == null || map.isEmpty();
	}

	public static boolean emptyFile_NotExist(Path path) {
		return Files.notExists(path);
	}

	@SneakyThrows
	public static boolean emptyFile_NoContent(Path path) {
		return emptyFile_NotExist(path) ? true : Files.size(path) == 0;
	}

	public static boolean emptyDir_NotExist(Path path) {
		return Files.notExists(path);
	}

	@SneakyThrows
	public static boolean emptyDir_NoContent(Path path) {
		return emptyDir_NotExist(path) ? true : !Files.list(path).findAny().isPresent();
	}

	/**
	 * *************************************************************
	 * ----------------------------- EMPTY --------------------------
	 * *************************************************************
	 */

	public static boolean notEmptyObj_Str(Object obj) {
		return !emptyObj_Str(obj);
	}

	public static boolean emptyObj_Str(Object obj) {
		return obj == null ? true : (obj instanceof CharSequence ? ((CharSequence) obj).length() == 0 : false);
	}


	public static boolean notEmptyBlankObj_Str(Object cell) {
		return !emptyBlankObj_Str(cell);
	}

	public static boolean emptyBlankObj_Str(Object cell) {
		return cell == null ? true : (cell instanceof CharSequence ? ((CharSequence) cell).length() == 0 ? true : StringUtils.isBlank((CharSequence) cell) : false);
	}


	public static boolean empty(long[] args) {
		return args == null || args.length == 0;
	}

	public static boolean empty(int[] args) {
		return args == null || args.length == 0;
	}

	public static boolean empty(boolean[] args) {
		return args == null || args.length == 0;
	}

	public static boolean nullAll(Object... args) {
		return Stream.of(IT.NE(args)).noneMatch(o -> o != null);
	}

	public static boolean nullAnyObj(Object... args) {
		return Stream.of(IT.NE(args)).anyMatch(o -> o == null);
	}

	public static boolean notNullAll(Object... args) {
		return !nullAnyObj(args);
	}

	public static boolean isNull(Object obj) {
		return obj == null;
	}

	public static boolean notNull(Object obj) {
		return obj != null;
	}

	public static <T> boolean empty(boolean checkTypeString, Object obj) {
		return checkTypeString ? emptyObj_Str(obj) : obj == null;
	}

	public static <T> boolean emptyAllExceptItem(int index, T... args) {
		if (ARR.isNotIndex(index, args) || X.isNull(args[index])) {
			return false;
		}
		for (int i = 0; i < args.length; i++) {
			if (i != index) {
				if (X.notNull(args[i])) {
					return false;
				}
			}
		}
		return true;
	}

	public static <T> boolean notEmpty(T[] args) {
		return args != null && args.length > 0;
	}

	public static <T> boolean notEmpty(Key pare) {
		return !empty(pare);
	}

	public static <T> boolean empty(Key pare) {
		return pare == null ? true : pare.empty();
	}


	public static boolean empty(CharSequence str) {
		return str == null || str.length() == 0;
	}

	public static boolean blank(CharSequence str) {
		return StringUtils.isBlank(str);
	}

	public static boolean notBlank(CharSequence str) {
		return !blank(str);
	}

	public static boolean notEmpty(CharSequence str) {
		return str != null && str.length() > 0;
	}

	public static boolean empty(Number num) {
		return num == null;
	}

	public static boolean empty(Optional optional) {
		return optional == null || !optional.isPresent();
	}

	public static boolean notEmpty(Optional optional) {
		return !empty(optional);
	}

	public static boolean emptyOrZero(Number str) {
		return str == null || str.doubleValue() == 0;
	}

	public static boolean empty(Collection collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean notEmpty(Map map) {
		return !empty(map);
	}

	public static boolean notEmpty(Collection collection) {
		return collection != null && !collection.isEmpty();
	}

//	public static boolean notEmptyCheckNulls(Collection collection) {
//		return collection != null && !collection.isEmpty() && collection.stream().anyMatch(X::NN) ;
//	}

	public static boolean hasLength(String str) {
		return notEmpty(str);
	}

	public static boolean notEmptyAll(String... args) {
		return Stream.of(IT.NE(args)).noneMatch(X::empty);
	}

	public static boolean emptyAll(String... args) {
		return Stream.of(IT.NE(args)).noneMatch(X::notEmpty);
	}

	public static boolean emptyAll(Collection... args) {
		return Stream.of(IT.NE(args)).noneMatch(X::notEmpty);
	}

	public static boolean emptyAllObj_Str_Cll_Num(Object... args) {
		return Stream.of(IT.NE(args)).noneMatch(o -> !emptyObj_Str_Cll_Num(o));
	}

	public static boolean emptyAnyObj_Str_Cll(Object... args) {
		return Stream.of(IT.NE(args)).anyMatch(X::emptyObj_Str_Cll);
	}

	public static boolean emptyAnyStr(String... args) {
		return Stream.of(IT.NE(args)).anyMatch(X::empty);
	}

	public static boolean nullAnyIn(Collection collection) {
		return collection.stream().anyMatch(o -> o == null);
	}

	public static boolean emptyOnlyOne(String... args) {
		boolean found = false;
		for (String arg : args) {
			if (empty(arg)) {
				if (found) {
					return false;
				}
				found = true;
			}
		}
		return found;
	}

	public static boolean nullOnlyOne(Object... args) {
		boolean found = false;
		for (Object arg : args) {
			if (arg == null) {
				if (found) {
					return false;
				}
				found = true;
			}
		}
		return found;
	}

	public static boolean notNullOnlyOne(Object... args) {
		boolean found = false;
		for (Object arg : args) {
			if (arg != null) {
				if (found) {
					return false;
				}
				found = true;
			}
		}
		return found;
	}

	public static boolean notEmptyAnyCollection(Collection... collections) {
		return Stream.of(IT.notEmpty(collections)).anyMatch(X::notEmpty);
	}

	public static boolean notEmptyAllObj_Str_Cll_Num(Object... args) {
		return Stream.of(IT.NE(args)).noneMatch(X::emptyObj_Str_Cll_Num);
	}

	public static boolean notEmptyObj_Str_Cll_Num(Object arg) {
		return !emptyObj_Str_Cll_Num(arg);
	}

	public static boolean notEmptyObj_Str_Cll(Object arg) {
		return !emptyObj_Str_Cll(arg);
	}

	public static boolean emptyObj_Str_Cll_Num(Object vl) {
		if (emptyObj_Str_Cll(vl)) {
			return true;
		} else if (vl instanceof Number) {
			return ((Number) vl).doubleValue() == 0;
		}
		return false;
	}

	public static boolean emptyObj_Str_Cll(Object vl) {
		if (vl == null) {
			return true;
		} else if (vl instanceof CharSequence) {
			return ((CharSequence) vl).length() == 0;
		} else if (vl instanceof Collection) {
			return ((Collection) vl).isEmpty();
		} else if (vl instanceof Map) {
			return ((Map) vl).isEmpty();
		}
		return false;
	}

	public static boolean NE(CharSequence o) {
		return notEmpty(o);
	}

	public static boolean NN(Object o) {
		return o != null;
	}

	public static boolean notEmptyAnyStr(String... args) {
		return Stream.of(IT.NE(args)).anyMatch(X::notEmpty);
	}

	public static <T> boolean isType(T v, Class clazz) {
		return v != null && clazz.isAssignableFrom(v.getClass());
	}

	public static <T> boolean isNotType(T v, Class clazz) {
		return !isType(v, clazz);
	}

	public static boolean eqObjAny(Object obj, Object... with) {
		return EQ.equalsAny(obj, true, with);
	}

	public static boolean isNotEqObjAny(Object obj, Object... with) {
		return EQ.notEqualsAny(obj, true, with);
	}

	/**
	 * *************************************************************
	 * -------------------------- Size Of --------------------------
	 * *************************************************************
	 */

	public static int sizeOf(CharSequence list) {
		return list == null ? -1 : list.length();
	}

	public static <T> int sizeOf(T[] list) {
		return list == null ? -1 : list.length;
	}

	public static int sizeOf(String str) {
		return str == null ? -1 : str.length();
	}

	public static int sizeOfLines(String str) {
		return str == null ? -1 : SPLIT.argsByNL(str).length;
	}

	public static int sizeOfSerializable(Serializable data) {
		return data == null ? -1 : data.toString().length();
	}

	public static long sizeOf(Number num) {
		return num == null ? -1 : Math.abs(num.longValue());
	}

	public static int sizeOf0(Object[] list) {
		return sizeOf(list);
	}

	public static int sizeOf0(Collection list) {
		return list == null ? 0 : list.size();
	}

	public static int sizeOf(Collection list) {
		return list == null ? -1 : list.size();
	}

	public static int sizeOf(Map map) {
		return map == null ? -1 : map.size();
	}

	public static int sizeOf0(Map map) {
		return map == null ? 0 : map.size();
	}

	public static int sizeOf0(CharSequence str) {
		return str == null ? 0 : str.length();
	}

	public static long sizeOfLines(Path path, Long... defRq) {
		return Files.isRegularFile(path) ? UFS.getSizeLinesOfFile(path) : ARG.toDefThrowMsg(() -> X.f("Error read file size '%s' size", path), defRq);
	}

	@SneakyThrows
	public static Long sizeOf(Path path, Long... defRq) {
		return path != null && Files.isRegularFile(path) ? Files.size(path) : ARG.toDefThrowMsg(() -> X.f("Error read file length '%s' size", path), defRq);
	}

	@SneakyThrows
	public static long sizeOf(Path file, ByteUnit byteUnit) {
		long l = sizeOf(file, -1L);
		if (l <= 0) {
			return l;
		}
		Double convert = byteUnit.convert(l, ByteUnit.BYTE);
		return convert.longValue();
	}

	@SneakyThrows
	public static long sizeOf(Path file) {
		return file == null ? -1 : Files.size(file);
	}

	@SneakyThrows
	public static String sizeOfHu(Path file) {
		if (file == null) {
			return "-1bb";
		}
		long l = sizeOf(file);
		if (l <= 0) {
			return "0kb";
		} else if (l < 1000) {
			return l + "b";
		}
		Double convert = ByteUnit.MB.convert(l, ByteUnit.BYTE);
		return convert < 1.0 ? Hu.K(l, 0) + "b" : convert.longValue() + "Mb";
	}

	public static int sizeOf(Iterator iterator) {
		return sizeOf(ARR.toListArr(iterator));
	}

	public static int sizeOf(Iterable iterable) {
		if (iterable == null) {
			return -1;
		}
		if (iterable instanceof Collection) {
			return ((Collection) iterable).size();
		}
		int count = 0;
		Iterator it = iterable.iterator();
		while (it.hasNext()) {
			count++;
			it.next();
		}
		return count;
	}

	/**
	 * *************************************************************
	 * ---------------------------- Print --------------------------
	 * *************************************************************
	 */

	public static String p(Collection... obj) {
		String str = Stream.of(obj).map(Rt::buildReport).collect(Collectors.joining(STR.NL));
		System.out.println(str);
		return str;
	}

	public static void pf(String str, Object... args) {
		p(f(str, args));
	}

	public static String p(Map... obj) {
		String str = Stream.of(obj).map(Rt::buildReport).collect(Collectors.joining(STR.NL));
		System.out.println(str);
		return str;

	}

	public static Sb p(Collection collection, Logger... logger) {
		return Rt.buildReport(collection, logger);
	}

	public static Sb p(Map map, Logger... logger) {
		return Rt.buildReport(map, logger);
	}

	public static String pArr(Object... obj) {
		String str = Stream.of(obj).map(Rt::buildReportArray).collect(Collectors.joining(STR.NL));
		System.out.println(str);
		return str;

	}

	public static Object p(int tabLevel, Object obj) {
		System.out.print(STR.TAB(tabLevel));
		return p(obj);
	}

	public static Object p(Throwable t) {
		String stackTrace = ERR.getStackTrace(t);
		System.out.println(stackTrace);
//		System.out.println(t);
		return stackTrace;
	}

	public static Object e(Throwable t) {
		//System.out.println(t);
		return e(ERR.getStackTrace(t));
	}

	public static Object e(CharSequence errMsg) {
		System.err.println(errMsg);
		return errMsg;
	}

	public static Object p(Object obj) {
		if (obj != null) {
			if (obj instanceof Collection) {
				return p(new Collection[]{(Collection) obj});
			} else if (obj.getClass().isArray()) {
				return pArr(obj);
			} else if (obj instanceof Map) {
				return p(new Map[]{(Map) obj});
			} else if (obj instanceof Path || obj instanceof File || obj instanceof IPath) {
				return p(UF.ln(obj));
			}
		}
		System.out.println(obj = STR.formatAll(obj));
		return obj;
	}

	public static void pAsJson(Object obj) {
		String stringPrettyFromObject = UGson.toStringPrettyFromObject(obj);
		p("Json" + "\n" + stringPrettyFromObject);
	}

	/**
	 * *************************************************************
	 * --------------------------- FORMAT -----------------------
	 * *************************************************************
	 */

	public static String f(CharSequence message, Object... args) {
		return args != null && args.length == 0 ? message.toString() : String.format(message.toString(), args);
	}

	public static String f(String message, Object... args) {
		return args != null && args.length == 0 ? message : String.format(message, args);
	}

	public static String fl(CharSequence message, Object... args) {
		return fl(message == null ? null : message.toString(), args);
	}

	public static String fl(String message, Object... args) {
		return args != null && args.length == 0 ? message : f(message.replace("{}", "%s"), args);
	}

	public static String fl_(String message, Object... args) {
		return args != null && args.length == 0 ? message : f_(message.replace("{}", "%s"), args);
	}

	public static String fm(String message, Object... args) {
		return args != null && args.length == 0 ? message : new MessageFormat(message).format(args);
	}

	//auto
	public static String fa(CharSequence msg, Object... args) {
		String string = msg.toString();
		return string.contains("{}") ? X.fl(string, args) : X.f_(string, args);
	}

	public static String f_(CharSequence template, Object... args) {
		return f_(template == null ? "" : template.toString(), args);
	}

	//save method for formatting ( copy from Guava#Preconditions )
	public static String f_(String template, Object... args) {
		if (true) {//new
			if (template == null) {
				throw new NullPointerException("set string");
			}
			if (args.length == 0) {
				return template;
			}
		}
		template = String.valueOf(template);
		StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
		int templateStart = 0;

		int i;
		int placeholderStart;
		for (i = 0; i < args.length; templateStart = placeholderStart + 2) {
			placeholderStart = template.indexOf("%s", templateStart);
			if (placeholderStart == -1) {
				break;
			}

			builder.append(template.substring(templateStart, placeholderStart));
			builder.append(args[i++]);
		}

		builder.append(template.substring(templateStart));
		if (i < args.length) {
			builder.append(" [");
			builder.append(args[i++]);

			while (i < args.length) {
				builder.append(", ");
				builder.append(args[i++]);
			}

			builder.append(']');
		}

		return builder.toString();
	}


	/**
	 * *************************************************************
	 * ----------- toObject NotNull or default ---------------------
	 * *************************************************************
	 */

	public static String toStringSE(String label, int len) {
		return STR.toStringSE(label, len, label);
	}

	//copy Objects.toString
	public static String toStringNN(Object obj, String defaultIfNull) {
		return (obj != null) ? obj.toString() : defaultIfNull;
	}

	public static String toStringNE(Object obj, String defaultIfEmpty) {
		if (obj == null) {
			return defaultIfEmpty;
		}
		String str = obj.toString();
		return X.empty(str) ? defaultIfEmpty : str;
	}

	public static String toString(Object obj) {
		return String.valueOf(obj);
	}

	public static <T> T toObjectFromString(CharSequence obj, Class<T> asType, T... defRq) {
		return UST.strTo(obj, asType, defRq);
	}

	public static String toString0(InputStream inputStream, String... defRq) {
		try {
			return IOUtils.toString(inputStream);
		} catch (Exception e) {
			return ARG.toDefThrow(new RequiredRuntimeException(e, "Error read InputStream. Cause:%s"), defRq);
		}
	}

//	public static String toString0(OutputStream inputStream, String... defRq) {
//		try {
//			return IOUtils.toString(inputStream);
//		} catch (Exception e) {
//			return ARG.toDefThrow(new RequiredRuntimeException(e, "Error read InputStream. Cause:%s"), defRq);
//		}
//	}

	public static Boolean toBool(Boolean obj, Boolean def) {
		return obj != null ? obj : def;
	}

	public static Boolean toBoolObj(Object obj, Boolean def) {
		return obj == null ? def : ((obj instanceof Boolean) ? (Boolean) obj : def);
	}

	public static Integer toInt(Integer obj, Integer def) {
		return obj != null ? obj : def;
	}

	public static Long toLong(Long obj, Long def) {
		return obj != null ? obj : def;
	}

	public static <T> T toObjOr(T obj, T def) {
		return obj == null ? def : obj;
	}

	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	public static <T> T toObjAs(Object obj, Class<T> asType, T... defRq) {
		return ObjTo.objTo(obj, asType, defRq);
	}

	/**
	 * *************************************************************
	 * ------------- Throw checked error as unchecked  --------------
	 * *************************************************************
	 */

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void _throwException(Throwable exception) throws T {
		throw (T) exception;
	}

	public static <T> T throwException(Throwable exception) {
		_throwException(IT.notNull(exception));
		return null;
	}

	public static <T> T throwException(String msg, Object... args) {
		_throwException(new FIllegalStateException(msg, args));
		return null;
	}

	/**
	 * *************************************************************
	 * ---------------------------- Specific --------------------------
	 * *************************************************************
	 */

	public static void nothing() {
	}

	public static void exit(Object... objs) {
		Sys.exit(objs);
	}

	public static void say(String msg, Object... objs) {
		Sys.say(msg, objs);
	}

	public static boolean notEquals(Object obj1, Object obj2) {
		return !Objects.equals(obj1, obj2);
	}

	public static boolean equals(Object obj1, Object obj2) {
		return Objects.equals(obj1, obj2);
	}

	public static boolean equalsSafe(Object obj1, Object obj2) {
		return obj1 == null || obj2 == null ? false : obj1.equals(obj2);
	}

	public static boolean equals(CharSequence obj1, CharSequence obj2) {
		return obj1 == null ? false : StringUtils.equals(obj1, obj2);
	}

	public static boolean equalsIgnoreCase(CharSequence obj1, CharSequence obj2) {
		return obj1 == null ? false : StringUtils.equalsIgnoreCase(obj1, obj2);
	}

	public static List<Integer> sizeOfColColAsList(Collection<? extends Collection> colCol) {
		return Arrays.asList(sizeOfColCol(colCol));
	}

	public static Integer[] sizeOfColCol(Collection<? extends Collection> colCol) {
		Integer x = X.sizeOf(colCol);
		Integer y = empty(colCol) ? -1 : sizeOf(ARRi.first(colCol));
		return new Integer[]{x, y};
	}

	public static String toStringRq(Object obj) {
		return IT.NN(obj).toString();
	}

	public static String toStringRfl(Object o) {
		return o == null ? "null" : (o instanceof CharSequence ? o.toString() : ToStringBuilder.reflectionToString(o));
	}

	public static String toStringLine(Object any) {
		return any == null ? "" : STR.toStrLine(any.toString());
	}

	public static String toStringLog(Object any) {
		if (any == null) {
			return U.__NULL__;
		} else if (any instanceof Collection) {
			Collection coll = (Collection) any;
			Object first = ARRi.first(coll, null);
			if (first != null && first instanceof Collection) {
				return any.getClass().getSimpleName() + "**" + X.sizeOf((Collection) first) + "x" + X.sizeOf(coll);
			}
			return any.getClass().getSimpleName() + "*" + X.sizeOf(coll);
		} else if (any instanceof Map) {
			return any.getClass().getSimpleName() + "*" + X.sizeOf((Map) any);
		} else if (any instanceof Number) {
			return any.getClass().getSimpleName() + "*" + any;
		} else if (any instanceof Path || any instanceof File || any instanceof IPath) {
			return any.getClass().getSimpleName() + ":" + UF.ln(any);
		}
		String str = any.toString();
		return any.getClass().getSimpleName() + "*" + STR.toStringSE(str, 50, str);
	}

}
