package mpc.json;

import mpu.core.ARG;

import java.util.Map;

public interface IJsonType<T> {

	default String toStringFromObject(boolean... pretty) {
		String json = UGson.toStringJson_FromObject(this);
		return ARG.isDefEqTrue(pretty) ? UGson.toStringPretty(json) : json;
	}

	default T toTypeFromJson(String json) {
		return (T) UGson.as(json, ((T) this).getClass());
	}

	default Map<String, ?> toMapFromJson(String json) {
		return UGson.toMapFromString(json);
	}
}
