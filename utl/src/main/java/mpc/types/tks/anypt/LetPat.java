package mpc.types.tks.anypt;

import lombok.Builder;
import mpc.exception.RequiredRuntimeException;
import mpu.core.ARR;
import mpu.core.ARG;
import mpu.X;
import mpu.str.STR;
import mpu.str.UST;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@Builder
public class LetPat<T> {

	final String startWithIC;

	final AnyPat anyPat;
	final T num;

	@Nullable
	public static Integer getIntFromPattern(String str, boolean checkInt, boolean checkLetNum, String let) {
		Integer num = null;
		if (checkInt) {
			num = UST.INT(str, null);
		}
		if (checkLetNum) {
			LetPat mNum = ofLong(str, let);
			num = mNum.getInt(null);
		}
		return num;
	}

	public static Integer INT_any(String str, String let) {
		return getIntFromPattern(str, true, true, let);
	}


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
		return STR.startsWith(msg, let + arg1, ignoreCase);
	}

	public static LetPat<Long> ofLong(String pattern, String startWithIC) {
		return of(pattern, startWithIC, (s) -> UST.LONG(s, null));
	}

	public static <T> LetPat<T> of(String pattern, String startWithIC, Function<String, T> extractor) {
		List<String> errors = null;
		if (!STR.startsWith(pattern, startWithIC, true)) {
			errors = ARR.as(X.f("Pattern '%s' must start with '%s'", pattern, startWithIC));
		}
		T target = null;
		if (X.empty(errors)) {
			String pat = pattern.substring(startWithIC.length());
			target = extractor.apply(pat);
			if (target == null) {
				errors = ARR.as(X.f("Except child value from LetPat(%s), pattern '%s'", startWithIC, pat));
			}
		}
		AnyPat anyPat = AnyPat.builder().original(pattern).errors(errors).build();
		return (LetPat<T>) LetPat.builder().anyPat(anyPat).num(target).build();
	}

	public boolean isValid() {
		return anyPat.isValid();
	}


	public static Long getLong(String pattern, String letPfx, Long... defRq) {
		if (pattern.startsWith(letPfx)) {
			String patNum = pattern.substring(letPfx.length());
			Long num = UST.LONG(patNum, null);
			if (num != null) {
				return num;
			}
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except LetPat(%s) with number '%s'", letPfx), defRq);
	}
}
