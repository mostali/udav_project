package mpu.core;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import mpc.exception.WrongLogicRuntimeException;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpc.str.sym.SYM;
import mpu.str.ToString;
import mpu.X;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//Содержит короткие алиасы для ARR.as(Arrays.asList) +  ARR.list(new ArrayList()) + ARR.of
//Общие утилиты для работы с массивами
//Array
public class ARR {

	public static void main(String[] args) {

		Object o = toListFromArrayWithPrimitive(new int[]{1, 2});
		X.nothing();
		//Object[] arr = {1, 2, 3};
		//U.p(UArr.sublist((Object[]) null, 0, 0));

		//List wheres = Arrays.asList(1, 2, 3);
//			P.p(AR.sublist((List) null, 0, 0));

	}

	public static final List EMPTY_LIST = Collections.EMPTY_LIST;
	public static final Set EMPTY_SET = Collections.EMPTY_SET;
	public static final Map EMPTY_MAP = Collections.EMPTY_MAP;
	public static final String[] EMPTY_STR = new String[0];

	//
	public static final Set TSET = new HashSet();//use for debug
	public static final List TLIST = new ArrayList();//use for debug
	public static final Map TMAP = new HashMap();//use for debug
	public static final Map TTMAP = new TreeMap();//use for debug
	//	public static final Map TMMAP = new MultiValueMap();//use for debug

	public static <T> void addIfNE(List<T> list, T t) {
		if (X.notEmptyObj_Str_Cll_Num(t)) {
			list.add(t);
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- MERGE --------------------------
	 * *************************************************************
	 */

	public static <K, V> Map<K, V> merge(boolean linked, Map<K, V> map, Map<K, V>... with) {
		Map merged = linked ? new LinkedHashMap() : new HashMap();
		merged.putAll(map);
		for (Map<K, V> withMap : with) {
			merged.putAll(withMap);
		}
		return merged;
	}

	public static <T> T[] merge(T[] arr, T... add) {
		return mergeAll(arr, add);
	}

	public static <T> T[] merge(T a, T[] b) {
		int aLen = 1;
		int bLen = b.length;
		Class type = a != null ? a.getClass() : b.getClass().getComponentType();
		@SuppressWarnings("unchecked") T[] c = (T[]) Array.newInstance(type, aLen + bLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		c[0] = a;
		return c;
	}

	public static <T> Collection<T> mergeUnsafeTypeCollection(Collection src, Collection newItems) {
		try {
			src.addAll(newItems);
		} catch (UnsupportedOperationException ex) {
			List l = new ArrayList();
			src.forEach(l::add);
			Stream.of(newItems).forEach(l::add);
			return l;
		}
		return src;
	}

	public static List mergeAll(List src, List... add) {
		for (List list : add) {
			if (X.notEmpty(list)) {
				src.addAll(list);
			}
		}
		return src;
	}

	//copy of ArrayUtils.addAll
	public static <T> T[] mergeAll(T[] array1, T... array2) {
		if (array1 == null) {
			return clone(array2);
		} else if (array2 == null) {
			return clone(array1);
		} else {
			Class<?> type1 = array1.getClass().getComponentType();
			T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
			System.arraycopy(array1, 0, joinedArray, 0, array1.length);
			try {
				System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
				return joinedArray;
			} catch (ArrayStoreException var6) {
				Class<?> type2 = array2.getClass().getComponentType();
				if (!type1.isAssignableFrom(type2)) {
					throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), var6);
				} else {
					throw var6;
				}
			}
		}
	}

	/**
	 * *************************************************************
	 * ---------------------------- OF --------------------------
	 * *************************************************************
	 */

	public static <T> T[] of(T... objs) {
		return objs;
	}

	public static <T> List<T> as(T... objs) {
		return Arrays.asList(objs);
	}

//	public static <T> List<T> asAL(T t, Collection<T> all) {
//		List l = new ArrayList(all.size() + 1);
//		l.add(t);
//		l.addAll(all);
//		return l;
//	}

	public static <T> List<T> asAL(T... objs) {
		return new ArrayList(objs.length) {
			{
				for (T t : objs) {
					add(t);
				}
			}
		};
	}

	public static LinkedList asLL() {
		return new LinkedList();
	}

	public static <T> List<T> asLL(T... objs) {
		return new LinkedList() {
			{
				for (T t : objs) {
					add(t);
				}
			}
		};
	}

	public static <T> Set<T> asHS(T... collection) {
		return new HashSet() {
			{
				for (T t : collection) {
					add(t);
				}
			}
		};
	}

	public static <T> Set<T> asLS(T... collection) {
		return new LinkedHashSet() {
			{
				for (T t : collection) {
					add(t);
				}
			}
		};
	}

	public static LinkedList asLL(Collection lines) {
		return lines instanceof LinkedList ? (LinkedList) lines : new LinkedList(lines);
	}

	public static ArrayList asAL(Collection lines) {
		return lines instanceof ArrayList ? (ArrayList) lines : new ArrayList(lines);
	}

	public static ArrayList asAL() {
		return new ArrayList<>();
	}

	public static <T> List<T> as(T[] objs, List<T>... defRq) {
		if (objs != null) {
			return Arrays.asList(objs);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Args is null");
	}

	public static <T> List<List<T>> as2(T... objs) {
		return Arrays.asList(Arrays.asList(objs));
	}

	public static <T> Set<T> toSet(Enumeration<T> headers) {
		Set set = new LinkedHashSet();
		while (headers.hasMoreElements()) {
			set.add(headers.nextElement());
		}
		return set;
	}


	/**
	 * *************************************************************
	 * ---------------------------- INCREMENT --------------------------
	 * *************************************************************
	 */

	public static <T> T[] addArrayElements(T[] src, T... element) {
		for (T t : element) {
			src = incrementArrayElement(src, t);
		}
		return src;
	}

	public static <T> T[] addArrayElement(T[] src, T element) {
		return incrementArrayElement(src, element);
	}

	public static <T> T[] incrementArrayElement(T[] src, T element) {
		int last = src.length;
		src = incrementArray(src);
		src[last] = element;
		return src;
	}

	public static <T> T[] incrementArray(T[] src, int... lengthIncrement) {
		int delta = 0;
		if (lengthIncrement.length == 0) {
			delta = 1;
		} else if (lengthIncrement[0] <= 0) {
			throw new IllegalArgumentException("Increment length '" + lengthIncrement[0] + "'");
		} else {
			delta = lengthIncrement[0];
		}
		@SuppressWarnings("unchecked") final T[] newArr = (T[]) Array.newInstance(src.getClass().getComponentType(), src.length + delta);
		System.arraycopy(src, 0, newArr, 0, src.length);
		return newArr;
	}

	/**
	 * *************************************************************
	 * ----------------------------- Cut ---------------------------
	 * *************************************************************
	 */

	public static <T> T cutHeadRow(List<T> slice) {
		T parent = ARRi.first(slice);
		slice.remove(0);
		return parent;
	}

	public static <T> T cutFirstElementOrNull(List<T> slice, int... indexCutableElement) {
		try {
			return cutFirstElement(slice, indexCutableElement);
		} catch (ArrayIndexOutOfBoundsException ex) {
			return null;
		}
	}

	public static <T> T cutFirstElement(List<T> slice, int... indexCutableElement) {
		IT.NE(slice);
		int index = ARGn.isDef(indexCutableElement) ? indexCutableElement[0] : 0;
		IT.isBetweenLEQ(index, 0, slice.size());
		T parent = slice.get(index);
		slice.remove(index);
		return parent;
	}

	public static List cutLastRecursively(List l, Object last) {
		while (true) {
			Object lastInList = l.get(l.size() - 1);
			if (EQ.equalsUnsafe(last, lastInList)) {
				l.remove(l.size() - 1);
				continue;
			}
			break;
		}
		return l;
	}

	/**
	 * *************************************************************
	 * -------------------------- Is Index -------------------------
	 * *************************************************************
	 */

	public static <T> boolean isIndex(int index, T[] types) {
		return !isNotIndex(index, types);
	}

	public static <T> boolean isNotIndex(int index, T[] types) {
		return types == null || types.length == 0 || index < 0 || index >= types.length;
	}

	public static boolean isIndex(int index, Collection types) {
		return !isNotIndex(index, types);
	}

	public static boolean isNotIndex(int index, Collection types) {
		return types == null || types.size() == 0 || index < 0 || index >= types.size();
	}

	public static boolean isIndex(int index, Map types) {
		return !isNotIndex(index, types);
	}

	public static boolean isNotIndex(int index, Map types) {
		return types == null || types.size() == 0 || index < 0 || index >= types.size();
	}

	public static <C extends CharSequence> boolean isIndex(int index, C charSequence) {
		return !isNotIndex(index, charSequence);
	}

	public static <C extends CharSequence> boolean isNotIndex(long index, C charSequence) {
		return charSequence == null || charSequence.length() == 0 || index < 0 || index >= charSequence.length();
	}

	public static <C extends CharSequence> boolean isIndex(Integer index, Integer size) {
		return index != null && size != null && size > 0 && index < size && index >= 0;
	}


	/**
	 * *************************************************************
	 * ---------------------- toList -------------------------
	 * *************************************************************
	 */

	public static <T> List<T> toListFromArray(Object obj) {
		Class<?> componentType = obj.getClass().getComponentType();
		IT.state(obj.getClass().isArray(), "except array type '%s'", componentType);
		return componentType.isPrimitive() ? toListFromArrayWithPrimitive(obj) : as((T[]) obj);
	}

	public static <T> List<T> toListFromArrayWithPrimitive(Object obj) {
		Class<?> componentType = obj.getClass().getComponentType();
		IT.state(componentType.isPrimitive(), "except primitive type '%s'", componentType);
		Object[] o = null;
		if (componentType == boolean.class) {
			o = ArrayUtils.toObject((boolean[]) obj);
		} else if (componentType == byte.class) {
			o = ArrayUtils.toObject((byte[]) obj);
		} else if (componentType == short.class) {
			o = ArrayUtils.toObject((short[]) obj);
		} else if (componentType == int.class) {
			o = ArrayUtils.toObject((int[]) obj);
		} else if (componentType == long.class) {
			o = ArrayUtils.toObject((long[]) obj);
		} else if (componentType == float.class) {
			o = ArrayUtils.toObject((float[]) obj);
		} else if (componentType == double.class) {
			o = ArrayUtils.toObject((double[]) obj);
		} else if (componentType == char.class) {
			o = ArrayUtils.toObject((char[]) obj);
		} else {
			throw new WrongLogicRuntimeException("it is not array type '%s' with primitve", componentType);
		}
		List<T> l = (List<T>) as(o);
		return l;
	}


	public static <T> List<List<T>> toListList(T... iterable) {
		List list = new ArrayList();
		for (T t : iterable) {
			list.add(Arrays.asList(t));
		}
		return list;
	}

	public static <T> List<List<T>> toListList(T[]... iterable) {
		List list = new ArrayList();
		for (T[] t : iterable) {
			list.add(Arrays.asList(t));
		}
		return list;
	}

	public static <T> List<T> toList(Enumeration<T> iterable, boolean... reverse) {
		List l = new ArrayList<>();
		while (iterable.hasMoreElements()) {
			l.add(iterable.nextElement());
		}
		if (ARG.isDefEqTrue(reverse)) {
			Collections.reverse(l);
			return l;
		}
		return l;
	}

	public static <T> List<T> toList(Iterable<T> iterable, boolean... reverse) {
		if (iterable instanceof List) {
			return (List) iterable;
		}
		return toList(iterable.iterator(), reverse);
	}

	public static <T> List<T> toList(Iterator<T> iterable, boolean... reverse) {
		List l = new ArrayList<>();
		iterable.forEachRemaining(l::add);
		if (ARG.isDefEqTrue(reverse)) {
			Collections.reverse(l);
			return l;
		}
		return l;
	}

	/**
	 * *************************************************************
	 * ----------------------------- SubList ---------------------------
	 * *************************************************************
	 */

	public static <T> T[] sublist(T[] list, int fromIndex, T[]... defRq) {
		if (list != null) {
			return sublist(list, fromIndex, list.length == 0 ? 0 : list.length - 1, defRq);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("array is null");
	}

	public static <T> T[] sublist(T[] list, int fromIndex, int lastIndex, T[]... defRq) {
		IT.isPosOrZero(fromIndex);
		if (list != null && lastIndex >= fromIndex && fromIndex < list.length && lastIndex < list.length) {
			return Arrays.copyOfRange(list, fromIndex, lastIndex + 1);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("SubArray ( %s - %s ) Array Size (%s)", fromIndex, lastIndex, list == null ? null : list.length);
	}

	public static <T> List<T> sublist(List<T> list, int fromIndex, List<T>... defRq) {
		if (list != null) {
			return sublist(list, fromIndex, list.size() == 0 ? 0 : list.size() - 1, defRq);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("list is null");
	}

	public static <T> List<T> sublist(List<T> list, int fromIndex, int lastIndex, List<T>... defRq) {
		IT.isPosOrZero(fromIndex);
		IT.isGE(lastIndex, fromIndex);
		if (list != null && lastIndex >= fromIndex && fromIndex < list.size() && lastIndex < list.size()) {
			return list.subList(fromIndex, lastIndex + 1);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("SubList ( %s - %s ) List Size (%s)", fromIndex, lastIndex, list == null ? null : list.size());
	}

	/**
	 * *************************************************************
	 * ---------------------- CONTAINS -------------------------
	 * *************************************************************
	 */

	public static boolean contains(Collection<String> strings, String needle, boolean... ignoreCase) {
		if (needle == null) {
			return false;
		}
		return strings.stream().filter(s -> s != null).anyMatch(s -> ignoreCase.length > 0 && ignoreCase[0] ? s.equalsIgnoreCase(needle) : s.equals(needle));
	}

	public static <T> boolean contains(Collection<T> arr, T item) {
		if (X.notEmpty(arr)) {
			for (T t : arr) {
				if (t == item || EQ.equalsUnsafe(t, item)) {
					return true;
				}
			}
		}
		return false;
	}

	public static <T> boolean contains(T[] arr, T item) {
		if (X.notEmpty(arr)) {
			for (T t : arr) {
				if (t == item || EQ.equalsUnsafe(t, item)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * *************************************************************
	 * ----------------------------- PARTITION ---------------------------
	 * *************************************************************
	 */

	public static <T> List<List<T>> partition(List<T> objs, int part, Integer... requiredEven) {
		if (ARG.isDefNNF(requiredEven)) {
			IT.isEven(objs.size(), ARG.toDef(requiredEven));
		}
		return Lists.partition(objs, part);
	}

	public static <T> List<List<T>> partition(T[] objs, int part, Integer... requiredEven) {
		if (ARG.isDefNNF(requiredEven)) {
			IT.isEven(objs.length, ARG.toDef(requiredEven));
		}
		return Lists.partition(Arrays.asList(objs), part);
	}

	/**
	 * *************************************************************
	 * ----------------------------- ************* ---------------------------
	 * *************************************************************
	 */

	public static String[] copy(String[] src, Integer... indexes) {
		if (indexes.length == 0) {
			String[] dst = new String[src.length];
			System.arraycopy(src, 0, dst, 0, dst.length);
			return dst;
		} else {
			String[] dst = new String[indexes.length];
			for (int i = 0; i < indexes.length; i++) {
				dst[i] = src[i];
			}
			return dst;
		}
	}

	public static <T extends Comparable> List<T> sort(List<T> comparable, boolean isAsc) {
		if (isAsc) {
			return comparable.stream().sorted((p1, p2) -> (p1.compareTo(p2))).collect(Collectors.toList());
		} else {
			return comparable.stream().sorted((p1, p2) -> (p2.compareTo(p1))).collect(Collectors.toList());
		}

	}

	public static String toNiceStringCompact(Object... collection) {
		return ToString.toNiceStringCompact(as(collection));
	}

	public static void removeEmptyRows(List<List<Object>> rows) {
		Iterator<List<Object>> it = rows.iterator();
		while (it.hasNext()) {
			List<Object> row = it.next();
			if (X.empty(row)) {
				it.remove();
			}
		}
	}

	public static void trimListSizeByHead(List<List<Object>> rows) {
		int headRowSize = -1;
		List head = IT.notEmpty(rows).get(0);
		if (head.isEmpty()) {
			return;
		} else {
			headRowSize = head.size();
		}
		for (int i = 1; i < rows.size(); i++) {
			List valsRow = rows.get(i);
			if (valsRow.size() > headRowSize) {
				rows.set(i, rows.get(i).subList(0, headRowSize));
			}
			while (valsRow.size() < headRowSize) {
				valsRow.add(SYM.EMPTY);
			}
		}
	}

	public static List<Integer> getIndexes(int length) {
		List<Integer> indexes = new ArrayList<>();
		IT.isPosOrZero(length);
		for (int index = 0; index < length; index++) {
			indexes.add(index);
		}
		return indexes;
	}

	public static String removeFirst(String str, String... defRq) {
		if (str != null && !str.isEmpty()) {
			return str.substring(1);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Except not empty string");
	}

	public static String removeLast(String str, String... defRq) {
		if (str != null && !str.isEmpty()) {
			return str.substring(0, str.length() - 1);
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Except not empty string");
	}

	public static <T> Collection<T> removeFirst(Collection<T> collection, Collection<T>... defRq) {
		if (X.notEmpty(collection)) {
			collection = collection instanceof ArrayList ? collection : new ArrayList(collection);
			((ArrayList) collection).remove(0);
			return collection;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Except not empty collection");
	}

	public static <T> Collection<T> removeLast(Collection<T> collection, Collection<T>... defRq) {
		if (X.notEmpty(collection)) {
			collection = collection instanceof ArrayList ? collection : new ArrayList(collection);
			((ArrayList) collection).remove(collection.size() - 1);
			return collection;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Except not empty collection");
	}

	public static <T> Collection<T> cloneListWithList(Collection<T> array) {
		Collection newClone = new ArrayList<>(array.size());
		for (T child : array) {
			if (child instanceof Collection) {
				Collection child0 = (Collection) child;
				Collection clone = (Collection) child0.stream().collect(Collectors.toList());
				newClone.add(clone);
			} else {
				newClone.add(child);
			}
		}
		//way#1,2,3 - copy only first level
		//List<List<Object>> rows_clone = new ArrayList<>(rows.size());
		//Collections.copy(rows_clone, rows);
		//way#2
		//List<List<Object>> rows_clone = rows.stream().collect(Collectors.toList());
		//way#3 java10
		//List<List<Object>> rows_clone = List.copyOf(rows);
		return newClone;
	}

	public static <T> T[] clone(T[] array) {
		return array == null ? null : (T[]) array.clone();
	}

	@Deprecated
	public static <T> T defIfNull(T def, T... type) {
		return type != null && type.length > 0 ? type[0] : def;
	}

	public static List<Integer> getIndexesOfDublicates(List original, List incomming, boolean safeEquals) {
		List<Integer> dublicate = new ArrayList<>();
		int minSize = original.size() <= incomming.size() ? original.size() : incomming.size();
		for (int i = 0; i < minSize; i++) {
			Object org = original.get(i);
			Object inc = incomming.get(i);
			if (EQ.equals(org, inc, safeEquals)) {
				dublicate.add(i);
			}
		}
		return dublicate;
	}

	public static <R> R[] reverse(R[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			R temp = array[i];
			array[i] = array[array.length - 1 - i];
			array[array.length - 1 - i] = temp;
		}
		//return ArrayUtils.reverse(array);;
		//return Lists.reverse(array);;
		return array;
	}

	public static boolean isAllUniq(Collection list) {
		return list.size() == new HashSet(list).size();
	}

	@SneakyThrows
	public static <T> T[] removeFirst(T[] array, int count) {
		return Arrays.copyOfRange(array, count, array.length);
	}

	public static <C extends Collection<I>, I> List<I> mergeToList(C... lists) {
		ArrayList<I> result = new ArrayList();
		for (C l : lists) {
			result.addAll(l);
		}
		return result;
	}

	public static <T> ArrayList<T> wrap(Collection<T> items) {
		return new ArrayList<>(items);
	}

	public static <T> T[] newArray(Class<T> asType, int capacity) {
		return (T[]) Array.newInstance(asType, capacity);
	}

	public static <T> IntFunction<T[]> newArrayIntFunction(Class<T> asType, int capacity) {
		return (i) -> ARR.newArray(asType, capacity);
	}

	public static List<Integer> asInt(int[] mode) {
		return (List) IntStream.of(mode).boxed().collect(Collectors.toList());
	}

	public static List<Integer> LL() {
		return new LinkedList<>();
	}

	public static boolean isLast(int i, List<String> codes) {
		return i == codes.size();
	}

//	public static <T> T[] removeByIndex(T[] args, int index, boolean... required) {
//		if (X.notEmpty(args) && (index >= 0 && index < args.length)) {
//// Create another array of size one less
//			T[] anotherArray = new int[arr.length - 1];
//
//			// Copy the elements except the index
//			// from original array to the other array
//			for (
//					int i = 0, k = 0;
//					i < arr.length; i++) {
//
//				// if the index is
//				// the removal element index
//				if (i == index) {
//					continue;
//				}
//
//				// if the index is not
//				// the removal element index
//				anotherArray[k++] = arr[i];
//			}
//			return anotherArray;
//		}
//
//
//		// return the resultant array
//		return anotherArray;
//	}
}