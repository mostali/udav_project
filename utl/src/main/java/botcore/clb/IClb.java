package botcore.clb;

import mpc.json.GsonMap;

import java.util.Map;

public interface IClb<B extends IBotButton> {

	String path();

	String label();

	String data();

	default boolean isCallback(String data, boolean eqStartsContains) {
		return eqStartsContains ? data.equals(path()) : data.startsWith(path());
	}

	default String pathWithData2(Object data2) {
		if (data2 == null) {
			data2 = "";
		} else if (data2 instanceof GsonMap) {
			data2 = ((GsonMap) data2).toStringJson();
		}
		String callbackData = path() + data2;
		return callbackData;
	}

	default B toKey() {
		return toKey(null, null);
	}

	default B toKey(Map data) {
		return toKeyAny(data);
	}

	default B toKey(CharSequence data) {
		return toKeyAny(data);
	}

	default B toKeyAny(Object data2) {
		return toKey(data2, null);
	}

	B toKey(Object data, String name, String... color);

}
