package botcore.msgcore;

import mpc.map.IGetterAs;
import mpc.map.MAP;
import mpc.types.ruprops.URuProps;
import mpu.IT;
import mpu.core.ARG;
import mpu.str.UST;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class ClbMap implements IGetterAs {

	private final Map<String, String> ctx;

	public Map<String, String> ctx() {
		return ctx;
	}

	public ClbMap(Map<String, String> ctx) {
		this.ctx = ctx;
	}

	public static ClbMap ofKV(Object... kv) {
		return new ClbMap(MAP.of(kv));
	}

//	public static ClbMap toClbMap(String map) {
//		return new ClbMap(toMap(map));
//	}
//
//	public static Map<String, String> toMap(String map) {
//		return str2ctx(map);
//	}

	public static ClbMap ofMapAsStr(String data) {
		return new ClbMap(str2map(data));
	}

	public static Map<String, String> str2map(String mapAsStr) {
		return URuProps.getRuProperties(Arrays.asList(StringUtils.split(mapAsStr, "|")), "=", "#", false, false, false);
	}

	public static String map2str(Map<String, String> ctx) {
		String str = URuProps.toRuProperties(ctx, "|", "=");
		return str;
	}

	public static Map<String, String> cloneMap(Map<String, String> ctx) {
		return str2map(map2str(ctx));
	}

//	public Boolean BOOL(String key, Boolean... val) {
//		if (ARG.isDef(val)) {
//			Boolean bool = ARG.toDef(val);
//			ctx.put(key, bool == null ? null : bool.toString());
//			return bool;
//		}
//		return UST.BOOL(VAL(key));
//	}

	@Override
	public <T> T getAs(String key, Class<T> asType, T... defRq) {
		return MAP.getAs(ctx, key, asType, defRq);
	}

	public void put(String key, Object value) {
		ctx.put(key, value.toString());
	}
//	public Integer INT(String key, Integer def, Integer... val) {
//		if (ARG.isDef(val)) {
//			Integer integer = ARG.toDef(val);
//			ctx.put(key, integer == null ? null : integer.toString());
//			return integer;
//		}
//		return UST.INT(VAL(key), def);
//	}

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
		String str = map2str(ctx);
		//		return ED.CacheClb.encode(str);
		return str;
	}

	@Override
	public String toString() {
		return toStringData();
	}

	public String toStringData() {
		return map2str(ctx);
	}

	public String toStringDataValid() {
		String clbData = toStringData();
		checValidCallbackData(clbData);
		return clbData;
	}

	public static int getByteLength(String data) {
		return data.getBytes(StandardCharsets.UTF_8).length;
	}

	public static boolean isValidCallbackData(String data) {
		return getByteLength(data) <= 64;
	}

	public static boolean checValidCallbackData(String clbData) {
		IT.isLengthBytes(clbData, 64, IT.EQ.LE, "ClbData max length is 64");
		return getByteLength(clbData) <= 64;
	}

	public Map getMap() {
		return ctx;
	}
}
