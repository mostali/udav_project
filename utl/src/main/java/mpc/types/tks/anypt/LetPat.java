package mpc.types.tks.anypt;

import lombok.Builder;
import mpc.arr.Arr;
import mpc.args.ARG;
import mpc.X;
import mpc.str.STR;
import mpc.str.UST;

import java.util.List;
import java.util.function.Function;

@Builder
public class LetPat<T> {

	final String startWithIC;

	final AnyPat anyPat;
	final T num;


	public Long getLong(Long... defRq) {
		try {
			anyPat.throwIsNoOk();
			return (Long) num;
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public Integer getInt(Integer... defRq) {
		try {
			anyPat.throwIsNoOk();
			return ((Long) num).intValue();
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	public static boolean isStartWith(String msg, String let, String arg1, boolean ignoreCase) {
		return STR.startsWith(msg, let + arg1, ignoreCase, true);
	}

	public static LetPat<Long> ofLong(String pattern, String startWithIC) {
		return of(pattern, startWithIC, (s) -> UST.LONG(s, null));
	}

	public static <T> LetPat<T> of(String pattern, String startWithIC, Function<String, T> extractor) {
		List<String> errors = null;
		if (!STR.startsWithIC(pattern, startWithIC)) {
			errors = Arr.as(X.f("Pattern '%s' must start with '%s'", pattern, startWithIC));
		}
		T target = null;
		if (X.empty(errors)) {
			String pat = pattern.substring(startWithIC.length());
			target = extractor.apply(pat);
			if (target == null) {
				errors = Arr.as(X.f("Pattern '%s' is not extract to type with extractor (%s)", pat, extractor));
			}
		}
		AnyPat anyPat = AnyPat.builder().original(pattern).errors(errors).build();
		return (LetPat<T>) LetPat.builder().anyPat(anyPat).num(target).build();
	}

	public boolean isValid() {
		return anyPat.isValid();
	}
}
