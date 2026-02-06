package mpu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mpu.core.ARG;
import mpu.core.ARR;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.path.UPath;
import mpc.fs.fd.EFT;
import mpc.fs.fd.UFD;
import mpu.func.FunctionV;
import mpu.str.Regexs;
import mpu.str.STR;
import mpu.str.UST;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//Аналог guava Precondition's & Apache Validate
//Check Utility
public class IT {

	//	public static void main(String[] args) {
	//		TG t = UC.isTypeGson("{\"error\":\"set file (or url)\"}", TG.class, true);
	//		P.exit(t);
	//		U.exit(UC.isDirExist(UF.replaceHomeCharacter("~")));
	//		U.exit(UC.isRegex("=asd-asd-", Regexs.WORDDASH));
	// 		test()
	//	}

	//	public static void test() {
	//		isBetween(0.99, 0.98, 1L);
	//		isBetweenEQ(1.01, 1.01, 5L);
	//	}


	public static final String MAP_IS_EMPTY = "Map is empty";
	public static final String KEY_IS_EMPTY = "Key is empty";

	/**
	 * *************************************************************
	 * --------------------------- EQUALS --------------------------
	 * *************************************************************
	 */

	public static <T> T isNotEq(T o1, T o2, Object... message) {
		if (Objects.equals(o1, o2)) {
			throw new CheckException(ARG.isDef(message) ? "Checked types EQUALS o1=[" + o1 + "] & o2=[" + o2 + "]" : STR.formatAll(message));
		}
		return o1;
	}

	public static <T> T isEq(T o1, T o2, Object... message) {
		if (!Objects.equals(o1, o2)) {
			throw new CheckException(ARG.isDef(message) ? "Checked types NOT EQUALS o1=[" + o1 + "] & o2=[" + o2 + "]" : STR.formatAll(message));
		}
		return o1;
	}

	public static <T> T isEqSafe(T checkable, Object condition, Object... message) {
		return isEq(checkable, condition, true, message);
	}

	public static <T> T isEqUnSafe(T checkable, Object condition, Object... message) {
		return isEq(checkable, condition, false, message);
	}

	public static <T> T isEq(T checkable, Object condition, boolean safeEquals, Object... message) {
		if (mpu.core.EQ.equals(checkable, condition, safeEquals)) {
			return checkable;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked types must be equals [isPareIsNullIsEquals=%s][%s == %s]", safeEquals, checkable, condition) : STR.formatAll(message));
	}

	/**
	 * *************************************************************
	 * -------------------- IS LENGTH  --------------------
	 * *************************************************************
	 */
	public static <T> Collection<T> hasLength(Collection<T> checkable, long length, Object... message) {
		if (checkable != null && checkable.size() != 0 && length >= 0 && length <= checkable.size()) {
			return checkable;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked collection length except minimal Length(%s) vs Collection(%s)", length, checkable.size()) : STR.formatAll(message));
	}

	public static <T> T[] hasLength(T[] checkable, long length, Object... message) {
		if (checkable != null && checkable.length != 0 && length >= 0 && length <= checkable.length) {
			return checkable;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked array length except minimal Length(%s) vs Array(%s)", length, checkable.length) : STR.formatAll(message));
	}

	public static <T> Collection<T> isLength(Collection<T> checkable, long length, Object... message) {
		if (checkable != null && checkable.size() == length) {
			return checkable;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked collection length must be equals Length(%s) vs Collection(%s)", length, checkable.size()) : STR.formatAll(message));
	}

	public static <T> T[] isLength(T[] checkable, long length, Object... message) {
		if (checkable != null && checkable.length == length) {
			return checkable;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked array length must be equals Length(%s) vs Array(%s)", length, checkable.length) : STR.formatAll(message));
	}

	public static <S extends CharSequence> S isLength(S checkable, int length, Object... message) {
		return isLength(checkable, length, EQ.EQ, message);
	}

	public static <S extends CharSequence> S isLength(S checkable, int length, EQ eq, Object... message) {
		if (eq.isCondition(checkable.length(), length)) {
			return checkable;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked CharSequence.Length must be [%s %s %s]", checkable.length(), eq, length) : STR.formatAll(message));
	}

	public static <S extends CharSequence> S isLengthBytes(S checkable, int length, EQ eq, Object... message) {
		if (checkable != null && eq.isCondition(checkable.toString().getBytes(StandardCharsets.UTF_8).length, length)) {
			return checkable;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked CharSequence.Bytes.Length must be [%s %s %s]", checkable == null ? checkable : checkable.length(), eq, length) : STR.formatAll(message));
	}

	/**
	 * *************************************************************
	 * -------------------- IS SPECIFIC EQUALS  --------------------
	 * *************************************************************
	 */

	public static <N extends Number> void isNumber(N checked, N condition, EQ eq, Object... message) {
		if (NUMBER_COMPARATOR.of(eq).isCondition(checked, condition)) {
			return;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked number's must be [%s %s %s]", checked, eq, condition) : STR.formatAll(message));
	}

	public static <N extends Number> N isEqNumberStr(N checkable, String condition, boolean safeEquals, Object... message) {
		if (checkable == null || condition == null) {
			if (!safeEquals && checkable == null && condition == null) {
				return checkable;
			}
		} else if (String.valueOf(checkable).equals(condition)) {
			return checkable;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked number types must be equals [%s == %s]", checkable, condition) : STR.formatAll(message));
	}

	public static <N extends Number> N isEqNumber(N checkable, Number condition, Object... message) {
		if (NumberComparator.EQ.isCondition(checkable, condition)) {
			return checkable;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked number types must be equals [%s == %s]", checkable, condition) : STR.formatAll(message));
	}

	public static boolean state(Optional state, Object... message) {
		return state(state.isPresent(), message);
	}

	public static boolean state(boolean state, Object... message) {
		return isTrue(state, message);
	}

	public static boolean stateNot(boolean state, Object... message) {
		return isFalse(state, message);
	}

	public static void exceptError(FunctionV func, Object... message) {
		try {
			func.apply();
			throw new CheckException(STR.formatAllOr("Except error, but function is ok", message));
		} catch (Exception ex) {
		}
	}

	public static boolean isTrue(Boolean condition, Object... message) {
		if (condition == null || !condition) {
			throw new CheckException(STR.formatAllOr("Checked boolean type must be true", message));
		}
		return condition;
	}

	public static boolean isFalse(Boolean condition, Object... message) {
		if (condition == null || condition) {
			throw new CheckException(STR.formatAllOr("Checked boolean type must be false", message));
		}
		return condition;
	}

	/**
	 * *************************************************************
	 * --------------------------- NUMBERS -------------------------
	 * *************************************************************
	 */
	public static <N extends Number> N isLT(N itLess, N that, Object... message) {
		if (itLess.doubleValue() < that.doubleValue()) {
			return itLess;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked numbers must be [%s < %s]", itLess, that) : STR.formatAll(message));
	}

	public static <N extends Number> N isLE(N itLess, N that, Object... message) {
		if (itLess.doubleValue() <= that.doubleValue()) {
			return itLess;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked numbers must be [%s <= %s]", itLess, that) : STR.formatAll(message));
	}

	public static <N extends Number> N isGT(N itMore, N that, Object... message) {
		if (itMore.doubleValue() > that.doubleValue()) {
			return itMore;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked numbers must be [%s > %s]", itMore, that) : STR.formatAll(message));
	}

	public static <N extends Number> N isGE(N itMore, N that, Object... message) {
		if (itMore.doubleValue() >= that.doubleValue()) {
			return itMore;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked numbers must be [%s >= %s]", itMore, that) : STR.formatAll(message));
	}


	public static <N extends Number> N notZero(N number, Object... message) {
		if (number == null || number.doubleValue() == 0) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Checked number [%s] type must be NOT [0]", number) : STR.formatAll(message));
		}
		return number;
	}

	public static <N extends Number> N isNotZero(N number, Object... message) {
		if (number == null || number.doubleValue() == 0) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Checked number [%s] type must be [!0]", number) : STR.formatAll(message));
		}
		return number;
	}

	public static <N extends Number> N isZero(N number, Object... message) {
		if (number == null || number.doubleValue() != 0) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Checked number [%s] type must be [0]", number) : STR.formatAll(message));
		}
		return number;
	}

	public static <N extends Number> N isNegOrZero(N number, Object... message) {
		if (number == null || number.doubleValue() > 0) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Number [%s] must be negative", number) : STR.formatAll(message));
		}
		return number;
	}

	public static <N extends Number> N isNegNotZero(N number, Object... message) {
		if (number == null || number.doubleValue() >= 0) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Number [%s] must be negative or zero", number) : STR.formatAll(message));
		}
		return number;
	}

	public static <N extends Number> N isPosNotZero(N number, Object... message) {
		if (number == null || number.doubleValue() <= 0) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Number [%s] must be positive", number) : STR.formatAll(message));
		}
		return number;
	}

	public static <N extends Number> N isPosOrZero(N number, Object... message) {
		if (number == null || number.doubleValue() < 0) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Number [%s] must be positive or zero", number) : STR.formatAll(message));
		}
		return number;
	}

	/**
	 * *************************************************************
	 * --------------------------- isIndex -------------------------
	 * *************************************************************
	 */

	public static <C extends CharSequence> C isIndex(int index, C charSequence, Object... message) {
		if (ARR.isIndex(index, charSequence)) {
			return charSequence;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked (CharSequence) index must be 0 <= Index(%s) < CharSequence.Length(%s)", index, charSequence == null ? "NULL" : charSequence.length()) : STR.formatAll(message));
	}

	public static Integer isIndex(Integer index, Integer size, Object... message) {
		if (ARR.isIndex(index, size)) {
			return index;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked (size) index must be 0 <= Index(%s) < size(%s)", index, size == null ? "NULL" : size) : STR.formatAll(message));
	}

	public static <N extends Number & Comparable> N isIndex(N index, Map collection, Object... message) {
		if (ARR.isIndex(index.intValue(), collection)) {
			return index;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked (Map) index must be 0 <= Index(%s) < Collection.Size(%s)", index, collection == null ? "NULL" : collection.size()) : STR.formatAll(message));
	}

	public static <N extends Number & Comparable> N isIndex(N index, Collection collection, Object... message) {
		if (ARR.isIndex(index.intValue(), collection)) {
			return index;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked (Collection) index must be 0 <= Index(%s) < Collection.Size(%s)", index, collection == null ? "NULL" : collection.size()) : STR.formatAll(message));
	}

	public static <T> int isIndex(int index, T[] array, Object... message) {
		if (ARR.isIndex(index, array)) {
			return index;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Checked (Array) index must be 0 <= Index(%s) < Array.Length(%s)", index, array == null ? "NULL" : array.length) : STR.formatAll(message));
	}

	/**
	 * *************************************************************
	 * -------------------------- BETWEEN --------------------------
	 * *************************************************************
	 */

	public static <N extends Number & Comparable> N isMinMax(N num, N[] minMax) {
		if (minMax.length > 1) {
			return IT.isBetweenEQ(num, minMax[0], minMax[1]);
		} else {
			return IT.isGE(num, minMax[0]);
		}
	}

	public static <N extends Number & Comparable> N isBetween(N checkable, Number left, Number right, Object... message) {
		notNullAll(checkable, left, right);
		boolean leftTrue = NUMBER_COMPARATOR.compare(checkable, left) > 0;
		boolean rightTrue = NUMBER_COMPARATOR.compare(checkable, right) < 0;
		if (leftTrue && rightTrue) {
			return checkable;
		}
		throw new CheckException(message != null && message.length > 0 ? STR.formatAllOr(null, message) : STR.f("Checkable type must be between [%s] < [%s]* < [%s]", left, checkable, right));
	}

	public static <N extends Number & Comparable> N isBetweenEQ(N checkable, Number left, Number right, Object... message) {
		notNullAll(checkable, left, right);
		boolean leftTrue = NUMBER_COMPARATOR.compare(checkable, left) > 0 || NUMBER_COMPARATOR.compare(checkable, left) == 0;
		boolean rightTrue = NUMBER_COMPARATOR.compare(checkable, right) < 0 || NUMBER_COMPARATOR.compare(checkable, right) == 0;
		if (leftTrue && rightTrue) {
			return checkable;
		}
		throw new CheckException(message != null && message.length > 0 ? STR.formatAllOr(null, message) : STR.f("Checkable type must be between [%s] <= [%s]* <= [%s]", left, checkable, right));
	}

	public static <N extends Number & Comparable> N isBetweenLEQ(N checkable, Number left, Number right, Object... message) {
		notNullAll(checkable, left, right);
		boolean leftTrue = NUMBER_COMPARATOR.compare(checkable, left) > 0 || NUMBER_COMPARATOR.compare(checkable, left) == 0;
		boolean rightTrue = NUMBER_COMPARATOR.compare(checkable, right) < 0;
		if (leftTrue && rightTrue) {
			return checkable;
		}
		throw new CheckException(message != null && message.length > 0 ? STR.formatAllOr(null, message) : STR.f("Checkable type must be between [%s] <= [%s]* < [%s]", left, checkable, right));
	}

	public static <N extends Number & Comparable> N isBetweenREQ(N checkable, Number left, Number right, Object... message) {
		notNullAll(checkable, left, right);
		boolean leftTrue = NUMBER_COMPARATOR.compare(checkable, left) > 0;
		boolean rightTrue = NUMBER_COMPARATOR.compare(checkable, right) < 0 || NUMBER_COMPARATOR.compare(checkable, right) == 0;
		if (leftTrue && rightTrue) {
			return checkable;
		}
		throw new CheckException(message != null && message.length > 0 ? STR.formatAllOr(null, message) : STR.f("Checkable type must be between [%s] < [%s]* <= [%s]", left, checkable, right));
	}

	/**
	 * *************************************************************
	 * ---------------------------- NULL & EMPTY --------------------------
	 * *************************************************************
	 */

	public static <T> T NN(T type, Object... message) {
		return notNull(type, message);
	}

	public static <T> T isNull(T type, Object... message) {
		if (type != null) {
			throw new CheckException(STR.formatAllOr("Checkable type is NOT null", message));
		}
		return type;
	}

	public static <T> T notNull(T type, Object... message) {
		if (type == null) {
			throw new CheckException(STR.formatAllOr("Checkable type is null", message));
		}
		return type;
	}

	public static <T extends CharSequence> T NE(T type, Object... message) {
		return notEmpty(type, message);
	}

	public static <T> T empty(Optional<T> type, Object... message) {
		if (type == null || !type.isPresent()) {
			return null;
		}
		throw new CheckException(STR.formatAllOr("Checked type(Optional) is NOT empty", message));
	}

	public static <T> T notEmpty(Optional<T> type, Object... message) {
		if (type != null && type.isPresent()) {
			return type.get();
		}
		throw new CheckException(STR.formatAllOr("Checked type(Optional) is empty", message));
	}

	public static <T extends CharSequence> T isEmpty(T type, Object... message) {
		if (type == null || type.length() == 0) {
			return type;
		}
		throw new CheckException(STR.formatAllOr("Checked type(String) is NOT empty", message));
	}

	public static <T extends CharSequence> T notEmpty(T type, Object... message) {
		if (type == null || type.length() == 0) {
			throw new CheckException(STR.formatAllOr("Checked type(String) is empty", message));
		}
		return type;
	}

	public static <T> T[] NE(T[] type, Object... message) {
		return notEmpty(type, message);
	}

	public static <T> T[] isEmpty(T[] type, Object... message) {
		if (type == null || type.length == 0) {
			return type;
		}
		throw new CheckException(STR.formatAllOr("Checked type(Array) must be empty", message));
	}

	public static <T> T[] notEmpty(T[] type, Object... message) {
		if (type == null || type.length == 0) {
			throw new CheckException(STR.formatAllOr("Checked type(Array) is empty", message));
		}
		return type;
	}

	public static <T extends Collection> T isEmpty(T collection, Object... message) {
		if (collection == null || collection.size() == 0) {
			return collection;
		}
		throw new CheckException(STR.formatAllOr("Checked type(Collection) must be empty", message));
	}

	public static <I, T extends Collection<I>> T NE(T type, Object... message) {
		return notEmpty(type, message);
	}

	public static <K, V> Map<K, V> NE(Map<K, V> type, Object... message) {
		return notEmpty(type, message);
	}


//	public static <T> List<T> notEmpty(List<T> type, Object... message) {
//		return (List<T>) notEmpty((Collection) type, message);
//	}

	public static <I, T extends Collection<I>> T notEmpty(T type, Object... message) {
		if (type == null || type.size() == 0) {
			throw new CheckException(STR.formatAllOr("Checked type(Collection) is empty", message));
		}
		return type;
	}

	public static <K, V> Map<K, V> notEmpty(Map<K, V> map, Object... message) {
		if (map == null || map.size() == 0) {
			throw new CheckException(STR.formatAllOr("Checked type(Map) is empty", message));
		}
		return map;
	}

	public static <T extends CharSequence> T NB(T type, Object... message) {
		return notBlank(type, message);
	}

	public static <T extends CharSequence> T notBlank(T type, Object... message) {
		if (StringUtils.isBlank(type)) {
			throw new CheckException(STR.formatAllOr("Checked type(CharSequence) is blank", message));
		}
		return type;
	}

	public static Object notNull(Map map, String key, Object... message) {
		return IT.NN(IT.NN(map, MAP_IS_EMPTY).get(IT.NN(key, KEY_IS_EMPTY)), message);
	}

	/**
	 * *************************************************************
	 * --------------------------- NOT ALL -------------------------
	 * *************************************************************
	 */

	public static <T> T[] notNullOnlyOne(T... typeN) {
		List<Integer> n = new LinkedList<>();
		for (int i = 0; i < typeN.length; i++) {
			if (typeN[i] != null) {
				n.add(i);
			}
		}
		if (n.size() == 1) {
			return typeN;
		}
		throw new CheckException("Checkable type is null more that 1:" + n);
	}

	public static <T> T[] notNullAll(T... typeN) {
		List<Integer> n = new LinkedList<>();
		for (int i = 0; i < typeN.length; i++) {
			if (typeN[i] == null) {
				n.add(i);
			}
		}
		if (n.isEmpty()) {
			return typeN;
		}
		throw new CheckException("Checkable type is ALL has null :" + n);
	}

	public static <T> T notNullAny(T... typeN) {
		for (int i = 0; i < typeN.length; i++) {
			if (typeN[i] != null) {
				return typeN[i];
			}
		}
		throw new CheckException("Checkable type's is ANY has all null " + typeN.length);
	}

	public static Collection[] notEmptyAll(Collection... typeN) {
		for (int i = 0; i < typeN.length; i++) {
			notEmpty(typeN[i], "Checked type(Collection) is empty", i);
		}
		return typeN;
	}

	public static <T> T[][] notEmptyAll(T[]... typeArray) {
		for (int i = 0; i < typeArray.length; i++) {
			notEmpty(typeArray[i], "Checked type(Array) is empty", i);
		}
		return typeArray;
	}

	public static <T extends CharSequence> T notEmptyAny(T... many) {
		IT.notEmpty(many);
		for (int i = 0; i < many.length; i++) {
			if (X.notEmpty(many[i])) {
				return many[i];
			}
		}
		throw new CheckException("Checked Many CharSequence . Is all is empty");
	}

	public static <T extends CharSequence> T[] notEmptyAll(T... type) {
		for (int i = 0; i < type.length; i++) {
			notEmpty(type[i], "Checked type is empty", i);
		}
		return type;
	}

	/**
	 * *************************************************************
	 * --------------------------- PATH ---------------------------
	 * *************************************************************
	 */
	public static Path isPathNotExist0(String file, EFT ft_or_any, Object... message) {
		Path path = UPath.existed(file, ft_or_any, null);
		if (path == null) {
			return Paths.get(file);
		}
		throw new CheckException(ARG.isNotDef(message) ? ft_or_any + " exist '" + file + "'" : STR.formatAll(message));
	}

	public static Path isPathExist0(String file, EFT ft_or_any, Object... message) {
		Path path = UPath.existed(file, ft_or_any, null);
		if (path != null) {
			return path;
		}
		throw new CheckException(ARG.isNotDef(message) ? ft_or_any + " NOT exist '" + file + "'" : STR.formatAll(message));
	}

	/**
	 * *************************************************************
	 * --------------------------- FILES ---------------------------
	 * *************************************************************
	 */

	public static String isFilename(String filename, Object... message) {
		boolean isFilename = UF.isValidFilename(filename);
		if (filename == null || isFilename) {
			throw new CheckException(ARG.isNotDef(message) ? "Invalid simple filename '" + filename + "'" : STR.formatAll(message));
		}
		return filename;
	}

	public static String isPathname(String path, Object... message) {
		if (path == null || !StringUtils.containsNone(path, UF.ILLEGAL_CHARACTERS_DIRNAME)) {
			throw new CheckException(ARG.isNotDef(message) ? "Invalid filename '" + path + "'" : STR.formatAll(message));
		}
		return path;
	}

	public static String isFileExist(String file, Object... message) {
		isFileExist(file == null ? null : Paths.get(file), message);
		return file;
	}

	public static File isFileExist(File file, Object... message) {
		isFileExist(file == null ? null : file.toPath(), message);
		return file;
	}

	public static Path isFileExist(Path file, Object... message) {
		if (file == null || !Files.isRegularFile(file)) {
			throw new CheckException(ARG.isNotDef(message) ? "File NOT exist '" + file + "'" : STR.formatAll(message));
		}
		return file;
	}

	public static Path isFileWithContent(Path file, Object... message) {
		if (!UFS.isFileWithContent(file)) {
			throw new CheckException(ARG.isNotDef(message) ? "File '" + file + "' size is 0" : STR.formatAll(message));
		}
		return file;
	}

	public static String isFileNotExist(String file, Object... message) {
		isFileNotExist(Paths.get(file), message);
		return file;
	}

	public static Path isFileOrNotExist(Path file, Object... message) {
		return file == null ? file : isFileExist(file, message);
	}

	public static Path isFileNotExist(Path file, Object... message) {
		if (Files.isRegularFile(file)) {
			throw new CheckException(ARG.isNotDef(message) ? "File exist '" + file + "'" : STR.formatAll(message));
		}
		return file;
	}

	public static Path isChildOfParentStrict(Path parent, Path child, Object... message) {
		if (!UFD.CHILDS.isChildOfParent(parent, child)) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Parent '%s' NOT contain child '%s'", parent, child) : STR.formatAll(message));
		}
		return child;
	}

	public static Path isChildOfParent(Path parent, Path child, Object... message) {
		if (!UFD.CHILDS.isChildOfParent(parent, child)) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Parent '%s' NOT contain child '%s'", parent, child) : STR.formatAll(message));
		}
		return child;
	}

	public static Path isNotChildOfParent(Path parent, Path child, Object... message) {
		if (UFD.CHILDS.isChildOfParent(parent, child)) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Parent '%s' contain child '%s'", parent, child) : STR.formatAll(message));
		}
		return child;
	}

	/**
	 * *************************************************************
	 * --------------------------- FOLDER --------------------------
	 * *************************************************************
	 */

	public static String isDirname(String dirname, Object... message) {
		if (StringUtils.containsNone(dirname, UF.ILLEGAL_CHARACTERS_DIRNAME)) {
			return dirname;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Dirname invalid '%s'", dirname) : STR.formatAll(message));
	}

	public static String isDirExist(String file, Object... message) {
		isDirExist(Paths.get(file), message);
		return file;
	}

	public static Path isDirOrNotExist(Path dir, Object... message) {
		return dir == null ? dir : isDirExist(dir, message);
	}

	public static Path isDirWithContent(Path dir, Object... message) {
		isDirExist(dir, message);
		if (UFS.isDirWithContent(dir, true)) {
			return dir;
		}
		throw new CheckException(ARG.isNotDef(message) ? "Folder is EMPTY '" + dir + "'" : STR.formatAll(message));
	}

	public static Path isDirExist(Path file, Object... message) {
		if (!Files.isDirectory(file)) {
			throw new CheckException(ARG.isNotDef(message) ? "Folder NOT exist '" + file + "'" : STR.formatAll(message));
		}
		return file;
	}

	public static String isDirNotExist(String file, Object... message) {
		isDirNotExist(Paths.get(file), message);
		return file;
	}

	public static Path isDirNotExist(Path file, Object... message) {
		if (Files.isDirectory(file)) {
			throw new CheckException(ARG.isNotDef(message) ? "Folder exist '" + file + "'" : STR.formatAll(message));
		}
		return file;
	}

	public static Path isDirOrFileExist(Path file, Object... message) {
		if (Files.isRegularFile(file) || Files.isDirectory(file)) {
			return file;
		}
		throw new CheckException(ARG.isNotDef(message) ? "Fd NOT exist '" + file + "'" : STR.formatAll(message));
	}

	public static Path isDirOrFileNotExist(Path file, Object... message) {
		isDirNotExist(file, message);
		isFileNotExist(file, message);
		return file;
	}

	/**
	 * *************************************************************
	 * --------------------------- TYPES --------------------------
	 * *************************************************************
	 */

	public static int isInt0(Long val, Object... message) {
		if (val <= Integer.MAX_VALUE) {
			return val.intValue();
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Long value '%s' too big for Integer", val) : STR.formatAll(message));
	}

	public static int isInt0(String type, Object... message) {
		try {
			return Integer.parseInt(type);
		} catch (NumberFormatException ex) {
			throw new CheckException(ARG.isNotDef(message) ? ex.getMessage() : STR.formatAll(message));
		}
	}

	public static long isLong0(String type, Object... message) {
		try {
			return Long.parseLong(type);
		} catch (NumberFormatException ex) {
			throw new CheckException(ARG.isDef(message) ? ex.getMessage() : STR.formatAll(message));
		}
	}

	public static String isUrl(String url, Object... message) {
		try {
			URL url1 = new URL(url);
			return url;
		} catch (MalformedURLException ex) {
			throw new CheckException(ARG.isNotDef(message) ? ex.getMessage() : STR.formatAll(message));
		}
	}

	public static URL isUrl0(String url, Object... message) {
		try {
			return new URL(url);
		} catch (MalformedURLException ex) {
			throw new CheckException(ARG.isNotDef(message) ? ex.getMessage() : STR.formatAll(message));
		}
	}

	public static String isDate(String str, String format, String... messages) {
		isDate0(str, format, messages);
		return str;
	}

	public static Date isDate0(String str, String format, String... message) {
		try {
			return new SimpleDateFormat(format).parse(str);
		} catch (ParseException e) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Parse date '%s' error with format '%s'. Cause:%s", str, format, e.getMessage()) : STR.formatAll(message));
		}
	}

	public static void isDateAfter(Date first, Date last) {
		IT.state(first.compareTo(last) < 0, "date '%s' must be after date '%s'", first, last);
	}

	public static void isDateBefore(Date first, Date last) {
		IT.state(first.compareTo(last) > 0, "date '%s' must be before date '%s'", first, last);
	}


	public static String isWord(String string, Object... message) {
		return isRegex(string, Regexs.WORD, message);
	}

	public static String isRegex(String string, String regex, Object... message) {
		if (X.notEmpty(string) && string.matches(regex)) {
			return string;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("String [%s] is not matches regex [%s]", string, regex) : STR.formatAll(message));
	}

	public static Class isClassOf(Class var, Class type, Object... message) {
		if (var != null && (type == var || type.isAssignableFrom(var))) {
			return var;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Clas [%s] is not type [%s]", var == null ? null : var.getClass(), type) : STR.formatAll(message));
	}

	public static Number isTypeNumber0(Object var, Object... message) {
		if (var != null && Number.class.isAssignableFrom(var.getClass())) {
			return Number.class.cast(var);
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Object [%s] is not Number type [%s]", var == null ? null : var.getClass(), Number.class) : STR.formatAll(message));
	}

	public static <T> T isType0(Object var, Class<T> type, Object... message) {
		if (var != null && type.isAssignableFrom(var.getClass())) {
			return type.cast(var);
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Object [%s] is not type [%s]", var == null ? null : var.getClass(), type) : STR.formatAll(message));
	}

	public static void isTypeAny(Object var, Class[] typesOr, Object... message) {
		if (var != null) {
			for (Class type : typesOr) {
				if (type.isAssignableFrom(var.getClass())) {
					return;
				}
			}
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Object [%s] is not type [%s]", var == null ? null : var.getClass(), Arrays.asList(typesOr)) : STR.formatAll(message));
	}

	public static <T> T isJsonType(InputStream inputStream, Class<T> type, Object... message) {
		if (inputStream != null) {
			T t = new Gson().fromJson(new InputStreamReader(inputStream), type);
			if (t != null) {
				return t;
			}
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("InputStream [%s] is not gson-type [%s]", inputStream, type) : STR.formatAll(message));
	}

	public static <T> T isJsonType(Reader reader, Class<T> type, Object... message) {
		if (reader != null) {
			T t = new Gson().fromJson(reader, type);
			if (t != null) {
				return t;
			}
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Reader [%s] is not gson-type [%s]", reader, type) : STR.formatAll(message));
	}

	public static <T> T isJsonType(CharSequence json, Class<T> type, Object... message) {
		if (json != null) {
			T t = new Gson().fromJson(json.toString(), type);
			if (t != null) {
				return t;
			}
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Object is not [%s] [%s]", type, json) : STR.formatAll(message));
	}

	public static JsonElement isJsonOrArray(CharSequence json, Object... message) {
		Exception exJson = null;
		try {
			JsonObject json0 = new Gson().fromJson(json.toString(), JsonObject.class);
			if (json0 != null) {
				return json0;
			}
		} catch (Exception ex) {
			exJson = ex;
		}
		Exception exArray = null;
		try {
			JsonArray array0 = new Gson().fromJson(json.toString(), JsonArray.class);
			if (array0 != null) {
				return array0;
			}
		} catch (Exception ex) {
			exArray = ex;
		}

		throw new CheckException(ARG.isNotDef(message) ? X.f("Object is not JsonObject or JsonArray [%s]", json) : STR.formatAll(message));
	}

	public static JsonArray isArray(CharSequence json, Object... message) {
		Exception exArray = null;
		if (json != null) {
			JsonArray t = new Gson().fromJson(json.toString(), JsonArray.class);
			try {
				if (t != null) {
					return t;
				}
			} catch (Exception ex) {
				exArray = ex;
			}
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Object is not JsonArray [%s]", json) : STR.formatAll(message));
	}

	public static JsonObject isJson(CharSequence json, Object... message) {
		if (json != null) {
			JsonObject t = new Gson().fromJson(json.toString(), JsonObject.class);
			if (t != null) {
				return t;
			}
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Object is not JsonObject [%s]", json) : STR.formatAll(message));
	}

	public static String isXml(String xml, Object... message) {
		if (xml != null && UST.XML_STRICT(xml, null) != null) {
			return xml;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Object is not XmlObject [%s]", xml) : STR.formatAll(message));
	}
//	@Nullable
//	public static CharSequence toStringVar(CharSequence var, boolean showContentOrType) {
//		return showContentOrType ? var : (var == null ? null : var.getClass().toString());
//	}

	public static Object isType(Object var, Class type, Object... message) {
		if (var != null && type.isAssignableFrom(var.getClass())) {
			return var;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Object [%s] is NOT type [%s]", var == null ? null : var.getClass(), type) : STR.formatAll(message));
	}

	public static void isNotType(Object var, Class type, Object... message) {
		if (var != null && !type.isAssignableFrom(var.getClass())) {
			return;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Object [%s] is type [%s]", var == null ? null : var.getClass(), type) : STR.formatAll(message));
	}

	public static <T> void contains(Collection<T> all, T item, Object... message) {
		if (all != null && !all.contains(item)) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Collection(%s) NOT contain [%s]", all, item) : STR.formatAll(message));
		}
	}

	public static <T> void notContains(Collection<T> all, T item, Object... message) {
		if (all != null && all.contains(item)) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Collection(%s) contain [%s]", all, item) : STR.formatAll(message));
		}
	}

	public static void isEven2(long number, Object... message) {
		isEven(number, 2, message);
	}

	public static void isEven(long number, int mod, Object... message) {
		if (number == 0 || number % mod == 0) {
			return;
		}
		throw new CheckException(ARG.isNotDef(message) ? X.f("Number [%s] must be even [%s]", number, mod) : STR.formatAll(message));
	}

	public static UUID isUUID0(String uuid, Object... message) {
		try {
			return UUID.fromString(uuid);
		} catch (Exception x) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Uuid invalid:" + uuid) : STR.formatAll(message));
		}
	}

	public static void isEqFile(String file1, String file2, boolean byContent, String... message) {
		if (!UFS.isEqFile(file1, file2, byContent)) {
			throw new CheckException(ARG.isNotDef(message) ? X.f("Files [%s] & [%s] must be equals (byContent:%s)", file1, file2, byContent) : STR.formatAll(message));
		}
	}


	/**
	 * *************************************************************
	 * ----------------------- CheckException ----------------------
	 * *************************************************************
	 */
	public static class FCheckException extends CheckException {
		public FCheckException(String message) {
			super(message);
		}

		public FCheckException(String message, Object... args) {
			this(String.format(message, args));
		}

		public FCheckException(Throwable throwable, String message) {
			super(message, throwable);
		}

		public FCheckException(Throwable throwable, String message, Object... args) {
			this(throwable, String.format(message, args));
		}
	}

	public static class CheckException extends IllegalArgumentException {
		public CheckException(String message) {
			super(message);
		}

		public CheckException(String message, Object... args) {
			super(String.format(message, args));
		}

		public CheckException(Exception ex, String message, Object... args) {
			super(String.format(message, args), ex);
		}
	}

	/**
	 * *************************************************************
	 * ---------------------- NumberComparator ---------------------
	 * *************************************************************
	 */

	public static final NumberComparator NUMBER_COMPARATOR = new NumberComparator();

	public interface IEqualable<L, R> {
		<L, R> boolean isCondition(L left, R right);

		Comparator<L> toComparator();
	}

	public enum EQ {
		EQ, NE, LT, LE, GE, GT,// LESS GREATER
		BT, BE, BEL, BER,// BETWEEN
		NN, INULL, NEMP, IEMP;// NULL & EMPTY

		public boolean isCondition(long checked, long condition) {
			switch (this) {
				case EQ:
					return checked == condition;
				case NE:
					return checked != condition;
				case LT:
					return checked < condition;
				case LE:
					return checked <= condition;
				case GT:
					return checked > condition;
				case GE:
					return checked >= condition;
				default:
					throw new UnsupportedOperationException(String.format("Operation EQ [%s] not supported for number's", this));
			}
		}
	}

	public static class NumberComparator<L extends Number & Comparable, R extends Number & Comparable> implements Comparator<L>, IEqualable<L, R> {
		public static final IEqualable<Number, Number> EQ = new NumberComparator(IT.EQ.EQ) {
		};
		public static final IEqualable<Number, Number> NE = new NumberComparator(IT.EQ.NE) {
		};
		public static final IEqualable<Number, Number> LT = new NumberComparator(IT.EQ.LT) {
		};
		public static final IEqualable<Number, Number> LE = new NumberComparator(IT.EQ.LE) {
		};
		public static final IEqualable<Number, Number> GE = new NumberComparator(IT.EQ.GE) {
		};
		public static final IEqualable<Number, Number> GT = new NumberComparator(IT.EQ.GT) {
		};

		final IT.EQ ceq;

		public NumberComparator() {
			this(null);
		}

		public NumberComparator(IT.EQ ceq) {
			this.ceq = ceq;
		}

		@Override
		public <L, R> boolean isCondition(L left, R right) {
			if (ceq == null) {
				return false;
			} else {
				switch (ceq) {
					case EQ:
						return NUMBER_COMPARATOR.compare((Number) left, (Number) right) == 0;
					case NE:
						return NUMBER_COMPARATOR.compare((Number) left, (Number) right) != 0;
					case LT:
						return NUMBER_COMPARATOR.compare((Number) left, (Number) right) < 0;
					case LE:
						return NUMBER_COMPARATOR.compare((Number) left, (Number) right) <= 0;
					case GT:
						return NUMBER_COMPARATOR.compare((Number) left, (Number) right) > 0;
					case GE:
						return NUMBER_COMPARATOR.compare((Number) left, (Number) right) >= 0;
					default:
						throw new CheckException("What is checkable predicat? " + ceq);
				}
			}
		}

		public NumberComparator of(IT.EQ eq) {
			switch (eq) {
				case EQ:
					return (NumberComparator) NumberComparator.EQ;
				case NE:
					return (NumberComparator) NumberComparator.NE;
				case LT:
					return (NumberComparator) NumberComparator.LT;
				case LE:
					return (NumberComparator) NumberComparator.LE;
				case GT:
					return (NumberComparator) NumberComparator.GT;
				case GE:
					return (NumberComparator) NumberComparator.GE;
				default:
					throw new UnsupportedOperationException(String.format("Operation EQ [%s] not supported for", eq));
			}
		}

		@Override
		public Comparator<L> toComparator() {
			return this;
		}

		@Override
		public int compare(L a, L b) throws ClassCastException {
			try {
				return a.compareTo(b);
			} catch (ClassCastException ex) {
				if (a instanceof BigDecimal) {
					if (b instanceof BigDecimal) {
						return NUMBER_COMPARATOR.compare(a, b);
					} else {
						return NUMBER_COMPARATOR.compare(a, new BigDecimal(b.doubleValue()));
					}
				} else if (b instanceof BigDecimal) {
					if (a instanceof BigDecimal) {
						return NUMBER_COMPARATOR.compare(a, b);
					} else {
						return NUMBER_COMPARATOR.compare(new BigDecimal(a.doubleValue()), b);
					}
				} else {
					return NUMBER_COMPARATOR.compare(a.doubleValue(), b.doubleValue());
				}
			}
		}

	}
}
