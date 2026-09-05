package utl_jack;


import lombok.SneakyThrows;
import mpu.IT;

import java.util.*;
import java.util.function.Function;

public class MapJacksonModel implements JacksonModel {

	private final Map<String, Object> map;

	public Map<String, Object> map() {
		return map;
	}

	public MapJacksonModel(Map map) {
		this.map = map;
	}

	public static MapJacksonModel of(Object... keyValues) {
		IT.isEven2(keyValues.length);
		Map map = new LinkedHashMap(keyValues.length / 2) {
			{
				for (int i = 0; i < keyValues.length; i += 2) {
					Object keyValue = keyValues[i + 1];
					put(keyValues[i], keyValue instanceof MapJacksonModel ? ((MapJacksonModel) keyValue).map() : keyValue);
				}
			}
		};
		return new MapJacksonModel(map);
	}

	public static MapJacksonModel of(Object key, Object value) {
		Map map = new LinkedHashMap(1) {
			{
				put(key, value);
			}
		};
		return new MapJacksonModel(map);
	}

	public <U> Optional<U> map(Function<Map, ? extends U> mapper) {
		return Optional.ofNullable(IT.NN(mapper).apply(map));
	}

	@SneakyThrows
	@Override
	public String toJson() {
		String json = UJack.serialize(this.map);
		return json;
	}

	@Override
	public Map<String, Object> getJson(boolean... createIfNull) {
		return map();
	}

}
