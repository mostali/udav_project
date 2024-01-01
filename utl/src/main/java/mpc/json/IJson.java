package mpc.json;

import mpc.args.ARG;

import java.util.Map;

public interface IJson<T> {
	default String toStringFromObject(boolean... pretty) {
		String json = UGson.toStringJsonFromObject(this);
		return ARG.isDefEqTrue(pretty) ? UGson.toStringPretty(json) : json;
	}

	default T toTypeFromJson(String json) {
		return (T) UGson.as(json, ((T) this).getClass());
	}

	default Map<String, ?> toMapFromJson(String json) {
		return UGson.toMapFromString(json);
	}
}
