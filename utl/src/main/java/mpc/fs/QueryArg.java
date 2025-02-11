package mpc.fs;

import mpu.IT;
import mpu.X;
import mpu.pare.Pare;

import java.util.stream.Collectors;
import java.util.stream.Stream;


public class QueryArg extends Pare<String, String> {

	public QueryArg(String key, Object val) {
		super(IT.NE(key), X.toString(val, null));
	}

	@Override
	public String toString() {
		return key() + "=" + valOr("");
	}

	public static QueryArg of(String key, Object val) {
		return new QueryArg(key, val);
	}

	public static QueryArg of(Pare pare) {
		return new QueryArg(pare.keyStr(), pare.val());
	}

	public static String join(Pare... args) {
		return Stream.of(args).map(p -> QueryArg.of(p).toString()).collect(Collectors.joining("&"));
	}
}
