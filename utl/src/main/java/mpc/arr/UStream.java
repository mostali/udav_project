package mpc.arr;

import mpu.X;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.core.ARR;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UStream {
	public static <T> List<T> NN(List<T> arr) {
		return arr.stream().filter(i -> i != null).collect(Collectors.toList());
	}

	public static List<String> toStringLines(Collection twins) {
		return (List<String>) twins.stream().map(String::valueOf).collect(Collectors.toList());
	}

	public static <T, C extends Collection<T>> C filter(C items, Predicate<T> predicate) {
		Stream<T> stream = items.stream().filter(predicate);
		if (items instanceof List) {
			return (C) stream.collect(Collectors.toList());
		} else if (items instanceof Set) {
			return (C) stream.collect(Collectors.toSet());
		}
		throw new WhatIsTypeException("Unsupported class '%s' for stream.filter operation", items.getClass());
	}

	public static <T> List<T> toList(Collection<T> l, Predicate<T>... predicate) {
		return predicate.length == 0 ? l.stream().collect(Collectors.toList()) : l.stream().filter(predicate[0]).collect(Collectors.toList());
	}

	public static <T, C extends Collection<T>, O> C map(Iterator<T> items, Function<T, O> function) {
		return (C) map(ARR.toList(items), function);
	}

	public static <T, C extends Collection<T>, O> C map(C items, Function<T, O> function) {
		Stream<O> stream = items.stream().map(function);
		if (items instanceof List) {
			return (C) stream.collect(Collectors.toList());
		} else if (items instanceof Set) {
			return (C) stream.collect(Collectors.toSet());
		}
		throw new WhatIsTypeException("Unsupported class '%s' for stream.map() operation", items.getClass());
	}

	public static <T> T find(Collection<T> comments, Predicate<T> predicate, T... defRq) {
		Optional<T> first = comments.stream().filter(predicate).findFirst();
		return ARG.isDef(defRq) ? first.orElseGet(() -> ARG.toDef(defRq)) : first.orElseThrow(() -> new RequiredRuntimeException("Not found item"));
	}

	public static <T, C extends Collection<T>> List<T> flatMapToList(Collection<C> values) {
		return values.stream().flatMap(v -> v.stream()).collect(Collectors.toList());
	}

	public static <T, C extends Collection<T>> Set<T> flatMapToSet(Collection<C> values) {
		return values.stream().flatMap(v -> v.stream()).collect(Collectors.toSet());
	}

	public static String findFirstNotEmpty(String... args) {
		return Stream.of(args).filter(X::notEmpty).findFirst().orElse(null);
	}
}
