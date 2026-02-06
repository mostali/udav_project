package mpc.map;

import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpc.str.condition.StringConditionPattern;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface WhatIs<T> extends Predicate {

	WhatIs NN = new WhatIs() {
		public boolean test(Object v) {
			return v != null;
		}

		@Override
		public String toString() {
			return "NOT_NULL";
		}
	};

	WhatIs NE = new WhatIs() {
		public boolean test(Object v) {
			return X.notEmptyObj_Str_Cll_Num(v);
		}

		@Override
		public String toString() {
			return "NOT_EMPTY";
		}
	};

	WhatIs NB = new WhatIs() {
		public boolean test(Object v) {
			return NE.test(v) || !X.emptyBlankObj_Str(v);
		}

		@Override
		public String toString() {
			return "NOT_BLANK";
		}
	};

	WhatIs RX = new WhatIs() {
		public boolean test(Object v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public WhatIs of(Object tester) {
			if (tester instanceof CharSequence) {
				Pattern pat = Pattern.compile(tester.toString());
				return new WhatIs() {
					@Override
					public boolean test(Object v) {
						return X.isType(v, CharSequence.class) && pat.matcher(v.toString()).matches();
					}
				};
			}
			throw new WhatIsTypeException("What is Tester for RX?" + tester);
		}

		@Override
		public String toString() {
			return "REGEX:" + tester();
		}
	};

	WhatIs<StringConditionPattern> SC = new WhatIs() {

		@Override
		public WhatIs of(Object tester) {
			if (tester instanceof CharSequence) {
				return new WhatIs() {
					@Override
					public boolean test(Object v) {
						return v != null && v.toString().equals(v);
					}
				};
			} else if (tester instanceof StringConditionPattern) {
				StringConditionPattern stringConditionPattern = (StringConditionPattern) tester;
				return new WhatIs() {
					@Override
					public boolean test(Object v) {
						return X.isType(v, CharSequence.class) && stringConditionPattern.matches(v.toString());
					}
				};
			}
			throw new WhatIsTypeException("What is Tester for StringCondition?" + tester);
		}

		@Override
		public String toString() {
			return "SC:" + tester();
		}
	};

	default boolean test(Object v) {
		throw new UnsupportedOperationException();
	}

	default Object tester() {
		throw new UnsupportedOperationException();
	}

	default WhatIs<T> of(T tester) {
		throw new UnsupportedOperationException();
	}

}
