package mpe.logs.filter;

import mpc.fs.UF;
import mpc.fs.ext.EXT;
import mpc.log.Lev;
import mpu.str.TKN;
import mpu.str.STR;
import mpc.str.condition.StringCondition;
import mpe.ftypes.core.FDate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ILogFilter {
	String PAGE_DELIMITER = ">>>>>>>>>>>>>>>>>>>> PAGE <<<<<<<<<<<<<<<<<<<<<";

	static String toStringHashcode(Object... values) {
		return Arrays.hashCode(values) + "";
	}

	StringCondition toFilter();

	default String toStringFnPart() {
		StringCondition filter = toFilter();
		return filter == null ? null : filter.toStringFnPart();
	}

	static String toStringFnPart(Date start, Date end) {
		return "S" + FDate.toString(start, FDate.YYYYMMDD_mmhhss_S).substring(4) + "-E" + FDate.toString(end, FDate.YYYYMMDD_mmhhss_S).substring(4);
	}

	static String toStringFnPart(Map<Lev, Boolean> state, boolean isNullThat) {
		return "" + Lev.toShortFilenameString(state, isNullThat);
	}

	static String toStringFnPart(List<String> mergeFiles) {
		List<String> fnames = mergeFiles.stream().map(fn -> EXT.twoRq(UF.fn(fn))[0]).collect(Collectors.toList());
		fnames = fnames.stream().map(fn -> TKN.trim(fn, ((Predicate<Character>) ch -> Character.isAlphabetic(ch)).negate())).collect(Collectors.toList());
		fnames = fnames.stream().map(fn -> fn.length() <= 6 ? fn : STR.substrKeepStartEndAndInsertBetween(fn, 3, 3, "-", fn)).collect(Collectors.toList());
//		return fnames+mergeFiles.hashCode() + "";
		String collect = fnames.stream().collect(Collectors.joining("_"));
		return collect;
	}
}
