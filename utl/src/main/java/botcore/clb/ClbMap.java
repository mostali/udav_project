package botcore.clb;

import mpc.args.ARG;
import mpc.types.ruprops.URuProps;
import mpc.str.UST;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

public class ClbMap {
	private final Map<String, String> ctx;

	public Map<String, String> ctx() {
		return ctx;
	}

	public ClbMap(Map<String, String> ctx) {
		this.ctx = ctx;
	}

	public static Map<String, String> str2ctx(String str) {
		return URuProps.getRuProperties(Arrays.asList(StringUtils.split(str, "|")), "=", "#", false, false, false);
	}

	public static String ctx2str(Map<String, String> ctx) {
		String str = URuProps.toRuProperties(ctx, "|", "=");
		return str;
	}

	public static Map<String, String> cloneMap(Map<String, String> ctx) {
		return str2ctx(ctx2str(ctx));
	}

	public Boolean BOOL(String key, Boolean... val) {
		if (ARG.isDef(val)) {
			Boolean bool = ARG.toDef(val);
			ctx.put(key, bool == null ? null : bool.toString());
			return bool;
		}
		return UST.BOOL(VAL(key));
	}

	public Integer INT(String key, Integer def, Integer... val) {
		if (ARG.isDef(val)) {
			Integer integer = ARG.toDef(val);
			ctx.put(key, integer == null ? null : integer.toString());
			return integer;
		}
		return UST.INT(VAL(key), def);
	}

	public Integer INT(String key, Integer... val) {
		if (ARG.isDef(val)) {
			Integer integer = ARG.toDef(val);
			ctx.put(key, integer == null ? null : integer.toString());
			return integer;
		}
		return UST.INT(VAL(key));
	}

	public String VAL(String key, String... val) {
		if (ARG.isDef(val)) {
			String val_ = ARG.toDef(val);
			ctx.put(key, val_);
			return val_;
		}
		return ctx.get(key);
	}

	public static ClbMap toClbMap(String map) {
		return new ClbMap(toMap(map));
	}

	public static Map<String, String> toMap(String map) {
		return str2ctx(map);
	}

	public String toKeyClbEncode(String... saltKeyValue) {
		return encodeMap(saltKeyValue);
	}

	public String encodeMap(String... saltKeyValue) {
		Map ctx = ctx();
		if (saltKeyValue != null && saltKeyValue.length > 0) {
			String key = saltKeyValue[0];
			String val = saltKeyValue.length > 0 ? saltKeyValue[1] : "";
			ctx = cloneMap(ctx());
			ctx.put(key, val);
		}
		String str = ctx2str(ctx);
		//		return ED.CacheClb.encode(str);
		return str;
	}

	@Override
	public String toString() {
		return ctx2str(ctx);
	}
}
