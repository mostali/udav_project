package mpu.str;

import lombok.RequiredArgsConstructor;
import mpc.arr.STREAM;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.WhatIsTypeException;
import mpc.map.BootContext;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.pare.Pare4;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SPL {

	public static final String KEY_START = "${";
	public static final String KEY_END = "}";

//	public static final Logger L = LoggerFactory.getLogger(SPL.class);

	public static boolean isPlaceholder(String s) {
		return s != null && STR.startsEndWith(s, KEY_START, KEY_END);
	}

	public static String wrapKey(String key) {
		return KEY_START + key + KEY_END;
	}

	public static String unwrapKey(String key, String... defRq) {
		return STR.substrBetweenStartEnd(key, KEY_START, KEY_END, defRq);
	}

	public static class SplPareBoot extends SplPare {

		public final Pare<BootContext.ApType, String> apKey;

		private List<SplPareBoot> resolve;

		public List<SplPareBoot> resolve0() {
			return resolve == null ? (resolve = new LinkedList<>()) : resolve;
		}

		public void add(SplPareBoot splPareBoot) {
			resolve0().add(splPareBoot);
		}

		@Override
		public String toString() {
			return "SplPareBoot{" + super.toString() + ", APK=" + (apKey == null ? "*" : apKey.key() + (apKey.val() == null ? "" : ":" + apKey.val())) + ", resolved=" + X.sizeOf(resolve) + '}';
		}

		public SplPareBoot(String org) {
			super(org, null);
			this.apKey = null;
		}


		public SplPareBoot(String org, Pare<String, Optional<String>> nameWithDef, Pare apKey) {
			super(org, nameWithDef);
			this.apKey = apKey;
		}

		public static SplPareBoot of(String value, Pare apKey) {
			return new SplPareBoot(value, unwrap(value), apKey);
		}

		public List<SplPareBoot> getUnresolved() {
			return resolve == null ? null : STREAM.filterToList(resolve, SplPareBoot::isSplValue);
		}

		public Pare4<SplPareBoot, String, Optional<String>, List<Pare4>> toTree() {
			List lists = STREAM.mapToList(resolve0(), SplPareBoot::toTree);
			String simpleValue = toSimpleValue(null);
			Optional<String> defValue = toDefValueOpt(null);
			return Pare4.of(this, simpleValue, defValue, lists);
		}

		public List<String>[] findTargetValues() {
			List<String> simpleValues = new LinkedList<>();
			List<String> defValues = new LinkedList<>();
			List<Pare4<SplPareBoot, String, Optional<String>, List<Pare4>>> innerSpls = (List) toTree().o1();
			for (Pare4<SplPareBoot, String, Optional<String>, List<Pare4>> inerrSpl : innerSpls) {
				String cleanValue = inerrSpl.val();
				if (cleanValue != null) {
					//L.debug("Add clean value '{}' from:{}", cleanValue, inerrSpl.key());
					simpleValues.add(cleanValue);
				} else {
					List<String>[] values = inerrSpl.key().findTargetValues();
					if (X.notEmpty(values[0])) {
						simpleValues.addAll(values[0]);
					} else {
						Optional<String> defValue = inerrSpl.ext();
						if (defValue != null) {
							defValues.add(defValue.orElse(null));
						}
					}
				}
			}
			return new List[]{simpleValues, defValues};
		}

		public Pare3<BootContext.ApType, String, String> propApKey() {
			return Pare3.of(apKey.key(), apKey.val(), name());
		}
	}

	@RequiredArgsConstructor
	public static class SplPare {
		public final String org;
		public final Pare<String, Optional<String>> nameWithDef;

		@Override
		public String toString() {
			return "SplPare{" + "org='" + org + '\'' + ", nameWithDef=" + nameWithDef + '}';
		}

		public String org() {
			return org;
		}

		public static SplPare of(String value) {
			return unwrap0(value);
		}

		public boolean isSplValue() {
			return nameWithDef != null;
		}

		public boolean isSimpleValue() {
			return !isSplValue();
		}

		public String name() {
			return nameWithDef.key();
		}

		public boolean hasDef() {
			return nameWithDef != null && nameWithDef.val() != null;
		}

		public Optional<String> toDefValueOpt(Optional<String>... defRq) {
			if (nameWithDef == null) {
				return ARG.toDefThrowMsg(() -> X.f("it no spl '%s'", org), defRq);
			} else if (nameWithDef.val() == null) {
				return ARG.toDefThrowMsg(() -> X.f("Spl not set def value '%s'", org), defRq);
			}
			return nameWithDef.val();
		}

		public String toDefValue(String... defRq) {
			if (nameWithDef == null) {
				return ARG.toDefThrowMsg(() -> X.f("it no spl '%s'", org), defRq);
			} else if (nameWithDef.val() == null) {
				return ARG.toDefThrowMsg(() -> X.f("Spl not set def value '%s'", org), defRq);
			}
			return nameWithDef.val().orElse(null);
		}

		public String toSimpleValue(String... defRq) {
			if (isSimpleValue()) {
				return org();
			}
			return ARG.toDefThrowMsg(() -> X.f("Not found SIMPLE value from '%s'", this), defRq);
		}

		public String toAnyValue(String... defRq) {
			if (isSimpleValue()) {
				return org();
			} else if (hasDef()) {
				return toDefValue();
			}
			return ARG.toDefThrowMsg(() -> X.f("Not found ANY value from '%s'", this), defRq);
		}
	}

	public static SplPare unwrap0(String vl) {
		return new SplPare(vl, unwrap(vl));
	}

	public static Pare<String, Optional<String>> unwrap(String vl) {
		if (!isPlaceholder(vl)) {
			return null;
		}
		String nameIdef = unwrapUnsafe(vl);
		boolean hasDefVal = nameIdef.indexOf(':') != -1;
		if (!hasDefVal) {
			return Pare.of(nameIdef, null);
		}
		String[] two = TKN.two(nameIdef, ":");
		switch (two[1]) {
			case "":
				return Pare.of(two[0], Optional.of(""));
			case "null":
				return Pare.of(two[0], Optional.ofNullable(null));
			default:
				return Pare.of(two[0], Optional.ofNullable(two[1]));
		}
	}

	public static String unwrapUnsafe(String s) {
		return STR.substrCount(s, 2, 1);
	}

	public static <T extends Collection<O>, O extends String> T replaceSpringPlacholderWithDefaultOr(T all, boolean returnPlaceholderNameOrOriginal) {
		Stream<Pare<String, PlOper>> objectStream = all.stream().map(s -> replaceSpringPlacholderWithDefaultOr(s, returnPlaceholderNameOrOriginal));
		if (all instanceof List) {
			return (T) objectStream.map(Pare::key).collect(Collectors.toList());
		} else if (all instanceof Set) {
			return (T) objectStream.map(Pare::key).collect(Collectors.toSet());
		}
		throw new WhatIsTypeException(all.getClass());
	}


	public enum PlOper {
		NOPL, NAME, DEF, ORG
	}

	public static Pare<String, PlOper> replaceSpringPlacholderWithDefaultOr(String s, boolean returnPlaceholderNameOrOriginal) {
		boolean isPlaceholder = isPlaceholder(s);
		if (!isPlaceholder) {
			return Pare.of(s, PlOper.NOPL);
		} else if (s.indexOf(':') != -1) {
			String last = TKN.last(unwrapUnsafe(s), ":");
			return Pare.of(last, PlOper.DEF);
		} else if (returnPlaceholderNameOrOriginal) {
			return Pare.of(unwrapUnsafe(s), PlOper.NAME);
		}
		return Pare.of(s, PlOper.ORG);
	}


	public static <T> T replaceSpringPlacholderWithDefaultOr(String s, Function<String, T>... finderPlaceholderValue) {
		boolean isPlaceholder = isPlaceholder(s);
		if (!isPlaceholder) {
			return (T) s;
		}
		String plNameWithDef = unwrapUnsafe(s);
		if (ARG.isDef(finderPlaceholderValue)) {
			String plName = plNameWithDef;
			boolean hasDefaultValue = plNameWithDef.indexOf(':') != -1;
			if (hasDefaultValue) {
				plName = TKN.firstGreedy(plNameWithDef, ":");
			}
			T newValue = ARG.toDef(finderPlaceholderValue).apply(plName);
			return newValue == null ? (T) TKN.last(plNameWithDef, ":") : newValue;
		}
		return (T) s;
	}
}
