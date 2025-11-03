package mpu.core;

import lombok.RequiredArgsConstructor;
import mpc.exception.RequiredRuntimeException;
import mpc.str.condition.StringConditionPattern;
import mpu.IT;
import mpu.str.STR;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//Конвертим строки в типы, etc
//ENUM
public class ENUM {

	public static Integer indexOf(String enu, Class<? extends Enum> e) {
		return indexOf(Enum.valueOf(e, enu));
	}

	public static int statusIndexNegative(Enum enu) {
		return -1 * statusIndex(enu);
	}

	public static int statusIndex(Enum enu) {
		return indexOf(enu);
	}

	public static Integer indexOf(Enum enu, Enum def) {
		try {
			return indexOf(enu);
		} catch (Exception e) {
			return indexOf(def);
		}
	}

	public static Integer indexOf(Enum enu) {
		int ind = -1;
		for (Enum n : enu.getClass().getEnumConstants()) {
			ind++;
			if (n.name().equals(enu.name())) {
				return ind;
			}
		}
		throw new IllegalArgumentException();
	}

	public static <M> M getEnumSafe(int index, Class<M> type) {
		return getEnum(index, type, null);
	}

	public static <M> M getEnum(int index, Class<M> type, M def) {
		try {
			return getEnum(index, type);
		} catch (Exception ex) {
			return def;
		}
	}

	public static <M> M getEnum(int index, Class<M> type) {
		try {
			return type.getEnumConstants()[Math.abs(index)];
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static List<String> enum2names(Enum... enums) {
		return Arrays.asList(enums).stream().filter(e -> e != null).map(e -> e.name()).collect(Collectors.toList());
	}

	public static <E extends Enum> List<E> getAll(Class<E> type) {
		return getValues(type);
	}

	public static <E extends Enum> List<String> getValuesAsString(Class<E> type) {
		return getValues(type).stream().map(Enum::toString).collect(Collectors.toList());
	}

	public static <E extends Enum> List<E> getValues(Class<E>... types) {
//		return Arrays.asList(type.getEnumConstants());
		return Arrays.stream(types).flatMap(et -> Arrays.stream(et.getEnumConstants())).collect(Collectors.toList());
	}

	public static Map<String, Enum> getValuesAsMap(Class<Enum>... types) {
		return getValues(types).stream().collect(Collectors.toMap(t -> t.name(), t -> t));
	}

	public static <T extends Enum<T>> T valueOf(String name, Class<T> type) {
		return Enum.valueOf(type, name);
	}

	public static <T extends Enum<T>> T valueOf(String name, T defRq) {
		return valueOf(name, (Class<T>) defRq.getClass(), false, defRq);
	}

	public static <T extends Enum<T>> T valueOf(String name, Class<T> clazz, T... defRq) {
		return valueOf(name, clazz, false, defRq);
	}

	public static <T extends Enum<T>> T valueOfStartWith(String name, Class<T> clazz, boolean ignoreCase, T... defRq) {
		if (name != null && clazz != null) {
			for (Enum<T> e : clazz.getEnumConstants()) {
				String ename = e.name();
				if (ignoreCase) {
					if (name.length() <= ename.length() && ename.substring(0, name.length()).equalsIgnoreCase(name)) {
						return (T) e;
					}
				} else if (ename.startsWith(name)) {
					return (T) e;
				}
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Type [%s] (startWith) not found from enum [%s]", name, clazz);
	}

	public static <T extends Enum<T>> T valueOf(String name, Class<T> clazz, boolean ignoreCase, T... defRq) {
		if (name != null && clazz != null) {
			for (Enum<T> e : clazz.getEnumConstants()) {
				if (ignoreCase) {
					if (e.name().equalsIgnoreCase(name)) {
						return (T) e;
					}
				} else if (e.name().equals(name)) {
					return (T) e;
				}
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Type [%s] not found from enum [%s]", name, clazz);
	}


	public static Enum valueOf(String name, Class<? extends Enum>[] classes, boolean ignoreCase, Enum... defRq) {
		if (name != null && classes != null) {
			for (Class type : classes) {
				Enum e = valueOf(name, type, ignoreCase, null);
				if (e != null) {
					return e;
				}
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Type [%s] not found from enum-types's [%s]", name, Arrays.asList(classes));
	}

	public static <T> T valueOf(Class<? extends Enum> type, StringConditionPattern conditionType, T... defRq) {
		if (type != null) {
			for (Enum e : getValues(type)) {
				if (conditionType.matches(e.name())) {
					return (T) e;
				}
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Enum not found. By condition type [%s]. From enum-types's [%s]", conditionType, Arrays.asList(getValues(type)));
	}

	public static Enum valueOf(String name, Enum type, boolean ignoreCase, Enum... defRq) {
		if (name != null && type != null) {
			if (ignoreCase) {
				if (type.name().equalsIgnoreCase(name)) {
					return type;
				}
			} else if (type.name().equals(name)) {
				return type;
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Type [%s] not found from enum's [%s]", name, type);
	}

	public static Enum valueOf(String name, Enum[] enums, boolean ignoreCase, Enum... defRq) {
		if (name != null && enums != null) {
			for (Enum e : enums) {
				if (ignoreCase) {
					if (e.name().equalsIgnoreCase(name)) {
						return e;
					}
				} else if (e.name().equals(name)) {
					return e;
				}
			}
		}
		if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw new RequiredRuntimeException("Type [%s] not found from enum's [%s]", name, Arrays.asList(enums));
	}

	public static <T extends Enum<T>> String name(String name, Class<T> type, T def) {
		return valueOf(name, type, def).name();
	}

	public static int[] getIndexes(Enum... enums) {
		return Stream.of(enums).map(ENUM::indexOf).mapToInt(x -> x).toArray();
	}

	public static boolean eqAny(Enum enum_, Object value, boolean... ignoreCase) {
		return value == null ? false : eq(enum_, value.toString(), ignoreCase);
	}

	public static boolean eq(Enum type, String value, boolean... ignoreCase) {
		return type == null || value == null ? false : type == valueOf(value, new Enum[]{type}, ARG.isDefEqTrue(ignoreCase));
	}

	public static boolean eq(Enum v1, Enum v2, boolean... ignoreCase_or_typeEq) {
		return v1 == null || v2 == null ? false : ARGn.isDef(ignoreCase_or_typeEq) ? EQ.equalsString(v1.name(), v2.name(), ignoreCase_or_typeEq) : v1 == v2;
	}

	public static void sort(List<? extends Enum> male, boolean... nullFirst_orLast) {
		Collections.sort(male, ARG.isDefEqTrue(nullFirst_orLast) ? Comparator.nullsFirst(Enum::compareTo) : Comparator.nullsLast(Enum::compareTo));
	}

	@NotNull
	public static <E extends Enum> E next(E view) {
		Enum[] enumConstants = view.getClass().getEnumConstants();
		if (enumConstants.length == 1) {
			return view;
		}
		Integer i = indexOf(view);
		return (E) enumConstants[enumConstants.length - 1 == i ? 0 : i + 1];
	}


	public static <T extends Enum> T choice(Class<T> enumType, Predicate<T> is, T... defRq) {
		List<T> values = getValues(enumType);
		Optional<T> first = values.stream().filter(is).findFirst();
		return ARG.toDefThrowOpt(() -> new RequiredRuntimeException("Enum Type not found from allowed - %s", values), first, defRq);
	}

	public static <T extends Enum, R> R apply(Class<T> enumType, Predicate<T> is, Function<T, R> apply) {
		return apply.apply(getValues(enumType).stream().filter(is).findFirst().get());
	}

	public static Object capitalize(Enum mdm) {
		return STR.capitalize(mdm.name().toLowerCase());
	}

	public static <T extends Enum> List<T> getValuesWoExclude(T... excludeValues) {
		T item = IT.notEmpty(excludeValues)[0];
		List<T> enums = ARR.as(excludeValues);
		return (List) Stream.of(item.getClass().getEnumConstants()).filter(i -> !enums.contains(i)).collect(Collectors.toList());
	}

	//
	//

	public interface EType<N extends Enum> {

		String etype(Enum name, String... defRq);

		N src();
	}
	//
 	//
	@RequiredArgsConstructor
	public class EnumType implements Enumeration<String> {
		private final String[] values;

		private int index = 0;

		@Override
		public boolean hasMoreElements() {
			return index < values.length;
		}

		@Override
		public String nextElement() {
			return values[index++];
		}
	}
}
