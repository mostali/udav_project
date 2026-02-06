package mpc.url;

import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.pare.Pare;
import mpu.str.TKN;

import java.util.stream.Collectors;
import java.util.stream.Stream;


public class QueryArg extends Pare<String, String> {

	public static final QueryArg EXE = QueryArg.of(CN.EXE, "eval");

	public QueryArg(String key, Object val) {
		super(IT.NE(key), X.toStringNN(val, null));
	}

	@Override
	public String toString() {
		return toStringArg();
	}

	public String toStringArg() {
		return key() + "=" + valOr("");
	}

	public static QueryArg of(String key, Object val) {
		return new QueryArg(key, val);
	}

	public static QueryArg of(Pare pare) {
		return new QueryArg(pare.keyStr(), pare.val());
	}

	public static String joinToUrl(String url, Pare... args) {
		for (Pare arg : args) {
			url = UUrl.addQueryParam(url, arg.keyStr(), arg.valStr());
		}
		return url;
	}

	public static String joinToUrlKV(String url, String... args) {
		if (X.empty(args)) {
			return url;
		}
		if (args.length == 1) {
			args = new String[]{args[0], ""};
		}
		IT.isEven2(args.length, "except args as pares, even 2");
		for (int i = 0; i < args.length - 1; i += 2) {
			url = joinToUrl(url, Pare.of(args[i], args[i + 1]));
		}
		return url;
	}

	public static String joinAsString(Pare... args) {
		return Stream.of(args).map(p -> QueryArg.of(p).toString()).collect(Collectors.joining("&"));
	}

	public static QueryArg ofQueryArg(String queryArg) {
		int i = queryArg.indexOf("=");
		if (i < 0) {
			return QueryArg.of(queryArg, "");
		}
		String[] two = TKN.twoExc(queryArg, i);
		return QueryArg.of(two[0], two[1]);
	}
}
