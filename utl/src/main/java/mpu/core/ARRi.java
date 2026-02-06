package mpu.core;

import mpu.IT;
import mpu.X;
import mpc.exception.RequiredRuntimeException;
import mpu.pare.Pare;
import mpc.str.ObjTo;
import mpu.str.SPLIT;
import mpu.str.ToString;
import mpu.str.STR;

import java.util.*;

/**
 * Кусает объекты спереди, сзади, по индексу, рандомно
 */
//Utility ArrayItem
public class ARRi {

	/**
	 * *************************************************************
	 * ---------------------- First / Last As-------------------------
	 * *************************************************************
	 */
	public static <T> T firstAs(Collection collection, Class<T> as, T... defRq) {
		Object item = first(collection, null);
		if (item != null) {
			return ObjTo.objTo(item, as, defRq);
		}
		return ARG.toDefThrowMsg(() -> "Item is null", defRq);
	}

	/**
	 * *************************************************************
	 * ---------------------- FIRST -------------------------
	 * *************************************************************
	 */

	public static <K, V> K firstKey(Map<K, V> collection, K... defRq) {
		Map.Entry<K, V> first = first(collection, null);
		if (first != null) {
			return first.getKey();
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("First val not found from Map (Entry is null)"), defRq);
	}

	public static <K, V> V firstVal(Map<K, V> collection, V... defRq) {
		Map.Entry<K, V> first = first(collection, null);
		if (first != null) {
			return first.getValue();
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("First val not found from Map (Entry is null)"), defRq);
	}

	public static <K, V> Map.Entry<K, V> first(Map<K, V> collection, Map.Entry<K, V>... defRq) {
		return collection == null ? ARG.toDefThrowMsg(() -> X.f("Collection is null"), defRq) : first(collection.entrySet(), defRq);
	}

	public static <T> T first(Collection<T> collection, T... defRq) {
		return collection instanceof List ? first((List<T>) collection, defRq) : first(collection.iterator(), defRq);
	}

	public static <T> T first(Iterable<T> iterator, int i, T... defRq) {
		return first(iterator.iterator(), i, defRq);
	}

	public static <T> T first(Iterator<T> iterator, int i, T... defRq) {
		IT.isPosOrZero(i);
		try {
			while (iterator.hasNext()) {
				T next = iterator.next();
				if (i-- == 0) {
					return next;
				}
			}
			throw new NoSuchElementException("iterator not found:" + i);
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw ex;
		}
	}

	public static <T> T first(Iterable<T> iterator, T... defRq) {
		return first(iterator.iterator(), defRq);
	}

	public static <T> T first(Iterator<T> iterator, T... defRq) {
		if (!iterator.hasNext()) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new NoSuchElementException("first not found");
		}
		return iterator.next();
	}

	public static <T> T first(List<T> types, T... defRq) {
		return first(types, 0, defRq);
	}

	public static <T> T first(List<T> types, int ind, T... defRq) {
		return item(types, ind, defRq);
	}

	public static <T> T first(T[] types, T... defRq) {
		return first(types, 0, defRq);
	}

	public static <T> T first(T[] types, int ind, T... defRq) {
		return item(types, ind, defRq);
	}

	public static <T> T firstOrNull(T... type) {
		return ARR.defIfNull(null, type);
	}

	public static <T> T firstFirst(List<List<T>> q, T... defRq) {
		List<T> list = first(q, null);
		if (X.notEmpty(list)) {
			return first(list, defRq);
		}
		return ARG.toDefRq(defRq);
	}

	public static String firstLine(String str, String... defRq) {
		return first(SPLIT.argsByNL(str), defRq);
	}

	public static String firstLine(String str, int index, String... defRq) {
		return first(SPLIT.argsByNL(str), index, defRq);
	}

	public static Character first(String str, Character... defRq) {
		return str.length() > 0 ? str.charAt(str.length() - 1) : ARG.toDefThrow(() -> new RequiredRuntimeException("Length 0", defRq));
	}

	/**
	 * *************************************************************
	 * ----------------------- LAST -------------------------
	 * *************************************************************
	 */

	public static <T> T last(List<T> list, T... defRq) {
		return last(list, 0, defRq);
	}

	public static <K, V> Map.Entry<K, V> last(Map<K, V> map, Map.Entry<K, V>... defRq) {
		if (X.notEmpty(map)) {
			return last(map.entrySet());
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new NoSuchElementException("Map is " + ToString.toStrNullOrEmp(map));
	}

	public static <T> T lastValue(Map<?, T> map, T... defRq) {
		if (X.notEmpty(map)) {
			return last(map.values());
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new NoSuchElementException("Map is " + ToString.toStrNullOrEmp(map));
	}

	public static <T> T last(Collection<T> collection, T... defRq) {
		return collection instanceof List ? last((List<T>) collection, defRq) : last(collection.iterator(), defRq);
	}

	public static <T> T last(Iterable<T> iterable, T... defRq) {
		return last(iterable.iterator(), defRq);
	}

	public static <T> T last(Iterator<T> iterator, T... defRq) {
		if (!iterator.hasNext()) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new NoSuchElementException("iterator is empty");
		}
		T current;
		do {
			current = iterator.next();
		} while (iterator.hasNext());
		return current;
	}

	public static <T> T last(List<T> list, int ind, T... defRq) {
		T last = null;
		if (list != null && list.size() > 0 && ind >= 0 && ind < list.size()) {
			return list.get(list.size() - 1 - ind);
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Index '%s' incorrect from list size '%s'", ind, list == null ? "null" : list.size());
	}

	public static <T> T last(T[] list, T... defRq) {
		return last(list, 0, defRq);
	}

	public static <T> T last(T[] list, int ind, T... defRq) {
		T last = null;
		if (list != null && list.length > 0 && ind >= 0 && ind < list.length) {
			return list[list.length - 1 - ind];
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Index '%s' incorrect from array size '%s'", ind, list == null ? "null" : list.length);
	}

	public static Character last(String str, Character... defRq) {
		return str.length() > 0 ? str.charAt(str.length() - 1) : ARG.toDefThrow(() -> new RequiredRuntimeException("Length 0", defRq));
	}

	/**
	 * *************************************************************
	 * ----------------------- MANY -------------------------
	 * *************************************************************
	 */

	public static <T> List<T> many(Iterator<T> iterator, int count, boolean firstOrLast, List<T>... defRq) {
		return firstOrLast ? firstMany(iterator, count, defRq) : lastMany(iterator, count, defRq);
	}

	public static <T> List<T> firstMany(Iterable<T> collection, int count, List<T>... defRq) {
		return firstMany(collection.iterator(), count, defRq);
	}

	public static <T> List<T> firstMany(Iterator<T> iterator, int count, List<T>... defRq) {
		ArrayList items = new ArrayList<>();
		int _count = count;
		while (iterator.hasNext()) {
			if (--_count < 0) {
				return items;
			}
			items.add(iterator.next());
		}
		if (items.size() == count) {
			return items;
		}
		return ARG.toDefThrowMsg(() -> X.f("Item's size '%s' not equals required count '%s'", items.size(), count), defRq);
	}

	public static <T> List<T> lastMany(Iterator<T> iterator, int count, List<T>... defRq) {
		return lastMany(ARR.toList(iterator), count, defRq);
	}

	public static <T> List<T> lastMany(Iterable<T> iterable, int count, List<T>... defRq) {
		IT.isPosOrZero(count);
		List<T> list = iterable == null ? null : ARR.toList(iterable, false);
		if (X.sizeOf(list) >= count) {
			if (list.size() == count) {
				return list;
			}
			return count == 0 ? list : ARR.sublist(list, list.size() - count);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except string with size more that '%s', but need '%s' elements", X.sizeOf(list), count), defRq);
	}

	public static String lastMany(String str, int count, String... defRq) {
		IT.isPosOrZero(count);
		if (X.sizeOf(str) >= count) {
			if (str.length() == count) {
				return str;
			}
			return count == 0 ? str : str.substring(str.length() - count);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except collection with size more that '%s', but need '%s' elements", X.sizeOf(str), count), defRq);
	}

	/**
	 * *************************************************************
	 * ----------------------- ITEM's -------------------------
	 * *************************************************************
	 */

	public static <T> List<T> itemsMany(Iterable<T> list, int[] index, List<T>... defRq) {
		List<T> items = new ArrayList<>();
		int i = -1;
		nextItem:
		for (T item : list) {
			i++;
			for (int ind : index) {
				if (i == ind) {
					items.add(item);
					continue nextItem;
				}
			}
		}
		if (items.size() == index.length) {
			return items;
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Item's size '%s' not equals required size of index's '%s'", items.size(), index.length);
	}

	public static <T> T itemAs(Collection collection, int index, Class<T> as, T... defRq) {
		Object item = item(collection, index, null);
		if (item != null) {
			return ObjTo.objTo(item, as, defRq);
		}
		return ARG.toDefThrowMsg(() -> "Item is null", defRq);
	}

	public static <T> T itemAs(Object[] collection, int index, Class<T> as, T... defRq) {
		Object item = item(collection, index, null);
		if (item != null) {
			return ObjTo.objTo(item, as, defRq);
		}
		return ARG.toDefThrowMsg(() -> "Item is null", defRq);
	}

	public static <T> T item(Iterable<T> list, int index, T... defRq) {
		if (list != null) {
			int i = 0;
			if (list instanceof List) {
				List<T> l = (List) list;
				if (ARR.isIndex(index, l.size())) {
					return l.get(index);
				}
			} else {
				for (T e : list) {
					if (i++ == index) {
						return e;
					}
				}
			}
		}
		return ARG.toDefThrowMsg(() -> X.f("Item by index [%s] not found", index), defRq);
	}

	public static <T> T item(T[] args, int index, T... defRq) {
		return index >= 0 && args != null && index < args.length ? args[index] : ARG.toDefThrowMsg(() -> X.f("Try get item by index '%s' from array with size '%s'", index, args.length), defRq);
	}

	/**
	 * *************************************************************
	 * ----------------------- RANDOM -------------------------
	 * *************************************************************
	 */

	public static Optional<Map.Entry> rand(Map map) {
		return map.entrySet().stream().skip((int) (map.size() * Math.random())).findFirst();
	}

	public static <T> T rand(T[] answer) {
		return answer[STR.rand(0, answer.length - 1)];
	}

	public static <T> T rand(Collection<T> answer) {
		return item(answer, STR.rand(0, answer.size() - 1));
	}

	public static <T> T rand(T[] answer, T[] answer2) {
		int rand = STR.rand(0, answer.length + answer2.length - 1);
		return rand < answer.length ? answer[rand] : answer2[rand - answer.length];
	}

	public static <T> Pare<Integer, T> next(Collection<T> items, Integer current, T... defRq) {
		Exception err = null;
		try {
			Integer next = nextIndex(current, items.size());
			return Pare.of(next, item(items, next));
		} catch (Exception ex) {
			err = ex;
		}
		return Pare.of(current, ARG.toDefThrow(err, defRq));
	}

	public static Integer nextIndex(Integer current, Integer size, Integer... defRq) {
		if (size > 0) {
			if (size == 1) {
				return 1;
			} else if (current == size - 1 || ++current < 0) {
				return 0;
			}
			return current;
		}
		Integer finalCurrent = current;
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Next index unrecognized, current(%s), size (%s)", finalCurrent, size), defRq);
	}

	public static <T> void addIfNot(List<T> items, T item) {
		if (!items.contains(item)) {
			items.add(item);
		}
	}
}
