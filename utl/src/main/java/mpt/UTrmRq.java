package mpt;

import mpc.arr.Arr;
import mpc.args.ARG;
import mpc.X;
import mpc.types.opts.SeqOptions;

import java.util.Map;

public class UTrmRq {

	public static <T> T getSingleAs(SeqOptions map, String key, Class<T> asType, T... defRq) {
		T rslt = map.getSingleAs(key, asType, null);
		if (rslt != null) {
			return rslt;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw TrmRsp.ERR("Value is null. Type '%s'. -Key '%s'", asType, key);
	}

	public static String getSingle(SeqOptions map, String key, String... defRq) {
		String rslt = map.getSingle(key, null);
		if (rslt != null) {
			return rslt;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw TrmRsp.ERR("Value is null. -Key '%s'", key);
	}

	public static boolean hasDouble(SeqOptions map, String key, Boolean... defRq) {
		Boolean rslt = map.hasDouble(key, null);
		if (rslt != null) {
			return rslt;
		} else if (ARG.isDef(defRq)) {
			return ARG.toDef(defRq);
		}
		throw TrmRsp.ERR("Value is null. --Key '%s'", key);
	}

	public static Map getSingleAsMap(SeqOptions opts, String[] keys, Map... defRq) {
		Map map = SeqOptions.getSingleAsMap(opts, keys, null);
		if (X.notEmpty(map)) {
			return map;
		}
		throw TrmRsp.ERR("Values is null. -Key's %s", Arr.as(keys));
	}
}
