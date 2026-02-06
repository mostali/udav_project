package mpc.arr;

import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.core.ARR;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class STREAM {

	public static <T> Predicate<T> AND(Predicate<T>... predicates) {
		return toPredicate(true, predicates);
	}

	public static <T> Predicate<T> OR(Predicate<T>... predicates) {
		return toPredicate(false, predicates);
	}

	public static <T> Predicate<T> toPredicate(boolean andOr, Predicate<T>... predicates) {
		IT.notEmpty(predicates, "set predicate's");
		Predicate<T> predicate = predicates[0];
		for (int i = 1; i < predicates.length; i++) {
			predicate = andOr ? predicate.and(predicates[i]) : predicate.or(predicates[i]);
		}
		return predicate;
	}

	/**
	 * *************************************************************
	 * ----------------------------  FILTER TO ----------------------------
	 * *************************************************************
	 */

	public static <T> List<T> filterToList(Collection<T> l, Predicate<T>... predicates) {
		return l.stream().filter(toPredicate(true, predicates)).collect(Collectors.toList());
	}

	public static <T> TreeSet<T> filterToTreeSet(Collection<T> l, Predicate<T>... predicates) {
		return l.stream().filter(toPredicate(true, predicates)).collect(Collectors.toCollection(TreeSet::new));
	}

	public static <T, C extends Collection<T>, O> List<O> filterMapToList(C items, Predicate<T> filter, Function<T, O> function) {
		return items.stream().filter(filter).map(function).collect(Collectors.toList());
	}


	public static <T, C extends Collection<T>> C filterToAll(C items, Predicate<T> predicate) {
		Stream<T> stream = items.stream().filter(predicate);
		if (items instanceof List) {
			return (C) stream.collect(Collectors.toList());
		} else if (items instanceof Set) {
			return (C) stream.collect(Collectors.toSet());
		}
		throw new WhatIsTypeException("Unsupported class '%s' for stream.filter operation", items.getClass());
	}


	/**
	 * *************************************************************
	 * ----------------------------  MAP TO ----------------------------
	 * *************************************************************
	 */


	public static <T, C extends Collection<T>, O> List<O> mapFilterToList(C items, Function<T, O> function, Predicate<O>... filter) {
		return items.stream().map(function).filter(toPredicate(true, filter)).collect(Collectors.toList());
	}

	public static <T, C extends Collection<T>, O> C mapToAll(Iterator<T> items, Function<T, O> function) {
		return (C) mapToAll(ARR.toList(items), function);
	}

	public static <T, C extends Collection<T>, O> List<O> mapToList(C items, Function<T, O> function) {
		return items.stream().map(function).collect(Collectors.toList());
	}

	public static <T, C extends Collection<T>, O> Set<O> mapToSet(C items, Function<T, O> function) {
		return items.stream().map(function).collect(Collectors.toSet());
	}

	public static <T, C extends Collection<T>, O> C mapToAll(C items, Function<T, O> function) {
		Stream<O> stream = items.stream().map(function);
		if (items instanceof List) {
			return (C) stream.collect(Collectors.toList());
		} else if (items instanceof Set) {
			return (C) stream.collect(Collectors.toSet());
		}
		throw new WhatIsTypeException("Unsupported class '%s' for stream.map() operation", items.getClass());
	}

	/**
	 * *************************************************************
	 * ----------------------------  FLAT ----------------------------
	 * *************************************************************
	 */

	public static <T, C extends Collection<T>> List<T> flatMapToList(Collection<C> values) {
		return values.stream().flatMap(v -> v.stream()).collect(Collectors.toList());
	}

	public static <T, C extends Collection<T>> Set<T> flatMapToSet(Collection<C> values) {
		return values.stream().flatMap(v -> v.stream()).collect(Collectors.toSet());
	}


	/**
	 * *************************************************************
	 * ----------------------------  RMM ----------------------------
	 * *************************************************************
	 */


	public static <T> boolean noneMath(Collection<T> items, Predicate<T> predicate) {
		return items.stream().noneMatch(predicate);
	}

	public static <T> boolean anyMatch(Collection<T> items, Predicate<T> predicate) {
		return items.stream().anyMatch(predicate);
	}

//	public static <T> T findFirst(Collection<T> items, Predicate<T> predicate) {
//		return items.stream().filter(predicate).findFirst().get();
//	}

	public static <T> T findFirst(Collection<T> items, Predicate<T> predicate, T... defRq) {
		Optional<T> first = items.stream().filter(predicate).findFirst();
		return ARG.isDef(defRq) ? first.orElseGet(() -> ARG.toDef(defRq)) : first.orElseThrow(() -> new RequiredRuntimeException("Not found item"));
	}

	public static <T> Optional<T> findFirstOpt(Collection<T> items, Predicate<T> predicate) {
		return items.stream().filter(predicate).findFirst();
	}

	public static String findFirstNotEmpty(String... args) {
		return Stream.of(args).filter(X::notEmpty).findFirst().orElse(null);
	}


	/**
	 * *************************************************************
	 * ----------------------------  MAP----------------------------
	 * *************************************************************
	 */

	public static <K, V> Map<K, V> filterToMap(Map<K, V> map, Predicate<Map.Entry<K, V>> filter) {
		return map.entrySet().stream().filter(filter).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
	}

	public static <K, V> Map<K, V> filterToLinkedMap(Map<K, V> map, Predicate<Map.Entry<K, V>> filter) {
		return map.entrySet().stream().filter(filter).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public static <K, V> Map<K, V> mapToMap(Map<K, V> map, Function<Map.Entry<K, V>, Map.Entry<K, V>> mapper) {
		return map.entrySet().stream().map(mapper).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
	}

	public static <I, K, V> Map<K, V> mapToMap(List<I> items, Function<I, Map.Entry<K, V>> mapper) {
		return items.stream().map(mapper).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
	}

	public static <K, V> Map<K, V> mapToLinkedMap(Map<K, V> map, Function<Map.Entry<K, V>, Map.Entry<K, V>> mapper) {
		return map.entrySet().stream().map(mapper).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public static <K, V> Map<K, V> sortToLinkedMap(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
		return map.entrySet().stream().sorted(comparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public static <T> Stream<T> of(T... objs) {
		return Arrays.stream(objs);
	}
}
