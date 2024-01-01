package mpc;

import lombok.SneakyThrows;
import mpc.arr.Arr;
import mpc.core.EQ;
import mpc.str.*;
import mpe.functions.FunctionV;
import mpv.byteunit.ByteUnit;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class X {

	public static void main(String[] args) {
		X.p(sizeOf(Paths.get("/home/dav/pjbf_stands/SAT/webapps/sufd-server-8.22.165.war"), ByteUnit.GB));
//		Sys.exit(sizeOf(Paths.get("/home/dav/pjbf_stands/SAT/hs_err_pid1570531.log").toFile()));
	}

	public static <T> boolean empty(T[] args) {
		return args == null || args.length == 0;
	}

	public static boolean empty(Map map) {
		return map == null || map.isEmpty();
	}

	/**
	 * *************************************************************
	 * ----------------------------- EMPTY --------------------------
	 * *************************************************************
	 */

	public static boolean emptyObjOrStr(Object obj) {
		return obj == null ? true : (obj instanceof CharSequence ? ((CharSequence) obj).length() == 0 : false);
	}

	public static boolean emptyObjOrBlank(Object cell) {
		return cell == null ? true : (cell instanceof CharSequence ? ((CharSequence) cell).length() == 0 ? true : StringUtils.isBlank((CharSequence) cell) : false);
	}

	public static <T> boolean empty(long[] args) {
		return args == null || args.length == 0;
	}

	public static <T> boolean empty(int[] args) {
		return args == null || args.length == 0;
	}

	public static <T> boolean empty(boolean[] args) {
		return args == null || args.length == 0;
	}

	public static <T> boolean isNullAll(T... args) {
		ERR.notEmpty(args);
		for (T t : args) {
			if (t != null) {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean isNullAny(T... args) {
		ERR.notEmpty(args);
		for (T t : args) {
			if (t == null) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean notNullAll(T... args) {
		ERR.notEmpty(args);
		for (T t : args) {
			if (t == null) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNull(Object obj) {
		return obj == null;
	}

	public static boolean notNull(Object obj) {
		return obj != null;
	}

	public static <T> boolean empty(boolean checkTypeString, Object obj) {
		return checkTypeString ? emptyObjOrStr(obj) : obj == null;
	}

	public static <T> boolean emptyAllExceptItem(int index, T... args) {
		if (Arr.isNotIndex(index, args) || X.isNull(args[index])) {
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

	public static boolean hasLength(String str) {
		return notEmpty(str);
	}

	public static boolean notEmptyAll(String... args) {
		ERR.NE(args);
		for (String arg : args) {
			if (empty(arg)) {
				return false;
			}
		}
		return true;
	}

	public static boolean emptyAll(String... args) {
		ERR.NE(args);
		for (String arg : args) {
			if (notEmpty(arg)) {
				return false;
			}
		}
		return true;
	}

	public static boolean emptyAll(Collection... args) {
		ERR.NE(args);
		for (Collection arg : args) {
			if (notEmpty(arg)) {
				return false;
			}
		}
		return true;
	}

	public static boolean emptyAny(String... args) {
		ERR.NN(args);
		for (String arg : args) {
			if (empty(arg)) {
				return true;
			}
		}
		return false;
	}

	public static boolean nullAny(String... args) {
		ERR.NN(args);
		for (String arg : args) {
			if (arg == null) {
				return true;
			}
		}
		return false;
	}

	public static boolean nullAny(Collection collection) {
		ERR.NN(collection);
		for (Object arg : collection) {
			if (arg == null) {
				return true;
			}
		}
		return false;
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
		ERR.notEmpty(collections);
		return Stream.of(collections).filter(X::notEmpty).findAny().isPresent();
	}

	public static boolean notEmptyAnyObjOrStr(Object... args) {
		ERR.notEmpty(args);
		for (Object arg : args) {
			if (X.notEmptyObjOrStr(arg)) {
				return true;
			}
		}
		return false;
	}

	public static boolean notEmptyObjOrStr(Object arg) {
		return !emptyObjOrStr(arg);
	}

	public static boolean notEmptyAnyArrObject(Object v) {
		return !emptyObject(v);
	}

	public static boolean emptyObject(Object v) {
		if (v == null) {
			return true;
		} else if (v instanceof CharSequence) {
			return ((CharSequence) v).length() == 0;
		} else if (v instanceof Collection) {
			return ((Collection) v).isEmpty();
		} else if (v instanceof Map) {
			return ((Map) v).isEmpty();
		}
		return false;
	}

	public static boolean isTrueAll(Boolean... conditions) {
		for (Boolean condition : conditions) {
			if (condition == null || !condition) {
				return false;
			}
		}
		return true;
	}

	public static boolean isFalseAll(Boolean... conditions) {
		for (Boolean condition : conditions) {
			if (condition == null || condition) {
				return false;
			}
		}
		return true;
	}

	public static boolean NE(CharSequence o) {
		return notEmpty(o);
	}

	public static boolean NN(Object o) {
		return o != null;
	}

	public static boolean notEmptyAnyString(String... args) {
		if (X.empty(args)) {
			return false;
		}
		for (String arg : args) {
			if (arg != null && !arg.isEmpty()) {
				return true;
			}
		}
		return false;
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

	public static int sizeOf(String list) {
		return list == null ? -1 : list.length();
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
		return map == null ? -1 : map.size();
	}

	@SneakyThrows
	public static long sizeOf(Path file) {
		return file == null ? -1 : Files.size(file);
	}

	@SneakyThrows
	public static long sizeOf(Path file, ByteUnit byteUnit) {
		if (file == null) {
			return -1;
		}
		long l = sizeOf(file);
		if (l <= 0) {
			return l;
		}
		Double convert = byteUnit.convert(l, ByteUnit.BYTE);
		return convert.longValue();
	}

	@SneakyThrows
	public static long sizeOfHu(Path file, ByteUnit byteUnit) {
		if (file == null) {
			return -1;
		}
		long l = sizeOf(file);
		if (l <= 0) {
			return l;
		}
		Double convert = byteUnit.convert(l, ByteUnit.BYTE);
		return convert < 1.0 ? 0 : convert.longValue();
	}

	@SneakyThrows
	public static String sizeOfHuStr(Path file) {
		if (file == null) {
			return "-1bb";
		}
		long l = sizeOf(file);
		if (l <= 0) {
			return "0kb";
		}
		Double convert = ByteUnit.MB.convert(l, ByteUnit.BYTE);
		return convert < 1.0 ? Hu.K(l, 0) + "b" : convert.longValue() + "Mb";
	}

	@Deprecated //?
	public static long sizeOf(File file) {
		return file == null ? -1 : file.length();
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
		return Arrays.stream(obj).map(Rt::buildReport).collect(Collectors.joining(STR.NL));
	}

	public static String p(Map... obj) {
		return Arrays.stream(obj).map(Rt::buildReport).collect(Collectors.joining(STR.NL));
	}

	public static String p(Object obj, Object... args) {
		String x = f_(obj == null ? "" : f_(obj.toString(), args), args);
		System.out.println(x);
		return x;
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

	public static String fm(String message, Object... args) {
		return args != null && args.length == 0 ? message : new MessageFormat(message).format(args);
	}

	//auto
	public static String fa(CharSequence msg, Object... args) {
		String string = msg.toString();
		return string.contains("{}") ? X.fl(string, args) : X.f_(string, args);
	}

	public static String f_(CharSequence template, @Nullable Object... args) {
		return f_(template == null ? "" : template.toString(), args);
	}

	//save method for formatting ( copy from Guava#Preconditions )
	public static String f_(String template, @Nullable Object... args) {
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
	//copy Objects.toString
	public static String toString(Object obj, String nullDefault) {
		return (obj != null) ? obj.toString() : nullDefault;
	}

	public static String toString(Object obj) {
		return String.valueOf(obj);
	}

	public static <T> T toObjectFromString(CharSequence obj, Class<T> asType, T... defRq) {
		return UST.strTo(obj, asType, defRq);
	}

	public static Boolean toBoolean(Boolean obj, Boolean def) {
		return obj != null ? obj : def;
	}

	public static Integer toInteger(Integer obj, Integer def) {
		return obj != null ? obj : def;
	}

	public static Long toLong(Long obj, Long def) {
		return obj != null ? obj : def;
	}

	public static Object toObject(Object obj, Object def) {
		return obj != null ? obj : def;
	}

	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	public static <T> T toObject(Object obj, Class<T> asType, T... defRq) {
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

	public static <T> T throwErrorNN_OrReturn(Throwable exception, T returnObject) {
		return exception == null ? returnObject : throwException(exception);
	}

	public static <T> T throwIfReturnNull(Supplier<Throwable> exception, T returnObject) {
		return returnObject == null ? throwException(exception.get()) : returnObject;
	}

	public static <T> T throwIfReturnNull(Throwable exception, T returnObject) {
		return returnObject == null ? throwException(exception) : returnObject;
	}

	public static <T> T throwException(Throwable exception) {
		_throwException(ERR.notNull(exception));
		return null;
	}

	/**
	 * *************************************************************
	 * ---------------------------- Specific --------------------------
	 * *************************************************************
	 */

	public static void nothing() {
	}

	public static boolean happensError(FunctionV funcVoid) {
		try {
			funcVoid.apply();
			return false;
		} catch (Exception ex) {
			return true;
		}

	}

	public static void exit(Object... objs) {
		Sys.exit(objs);
	}
}
